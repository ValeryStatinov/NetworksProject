import asyncio
from Game import GameSession
from Client import Client, ClientStatus
import json
from contextlib import suppress

IP_ADDRESS = '127.0.0.1'
TICK_DELAY = 1


class Server:
    def __init__(self, loop):
        self.loop = loop
        self.clients = []
        self.clients_mutex = asyncio.Lock()
        self.game_sessions = {}
        self.game_sessions_mutex = asyncio.Lock()
        self.commands = {'join': self.handleJoin, 'create_game': self.handleCreate,
                         'ready_to_start': self.handleReady, 'leave_game': self.handleLeave}
        self.tick = asyncio.Condition()
        self.server = None
        self.tasks = []

    def runServer(self):
        server_task = asyncio.start_server(
            self.handleConnection, IP_ADDRESS, 8888, loop=self.loop)
        self.server = self.loop.run_until_complete(server_task)
        print('Server is running on {}'.format(
            self.server.sockets[0].getsockname()))

        tick_task = self.loop.create_task(self.tickLoop())
        send_available_games_task = self.loop.create_task(
            self.infiniteSendAvailableGames())
        self.tasks.append(tick_task)
        self.tasks.append(send_available_games_task)

        try:
            self.loop.run_forever()
        except KeyboardInterrupt:
            for task in self.tasks:
                task.cancel()
                with suppress(asyncio.CancelledError):
                    loop.run_until_complete(task)

            self.server.close()
            self.loop.run_until_complete(self.server.wait_closed())
            self.loop.close()

    async def handleConnection(self, reader, writer):
        client_id = await self.getIdForClient()
        client = Client(reader, writer, client_id)
        print('New connection from {}'.format(client.addr))
        await self.addClient(client)
        await self.sendAvailableGames(client)
        await self.listenForCommand(client)

    async def tickLoop(self):
        print('Tick loop started')
        while True:
            await self.tick.acquire()
            self.tick.notify_all()
            self.tick.release()
            await asyncio.sleep(TICK_DELAY)

    async def addClient(self, client):
        async with self.clients_mutex:
            self.clients.append(client)

    async def getIdForClient(self):
        async with self.clients_mutex:
            return len(self.clients)

    async def listenForCommand(self, client):
        while not client.reader.at_eof():
            receivedJSON = (await client.read()).decode()
            try:
                command_data = json.loads(receivedJSON)
                self.tasks.append(self.loop.create_task(
                    self.commands[command_data['command']](client, command_data)))
            except Exception:
                if receivedJSON != '':
                    print('Error: wrong data received from {}. Received {}'.format(
                        client.addr, receivedJSON))
        await self.removeClient(client)

    async def sendAvailableGames(self, client):
        available_games = (json.dumps(self.getAvailableGames())).encode()
        if not await client.write(available_games):
            await self.removeClient(client)

    def getAvailableGames(self):
        available_games = []
        for game_id, game in self.game_sessions.items():
            available_games.append(
                {'game_id': game_id, 'name': game.name, 'empty_slots': game.getEmptySlots(
                ), 'max_slots': game.getMaxSlots()}
            )
        return available_games

    async def infiniteSendAvailableGames(self):
        print('Started sending available games coroutine')
        while True:
            await self.tick.acquire()
            task = loop.create_task(self.tick.wait())
            self.tasks.append(task)
            await task

            available_games = (json.dumps(self.getAvailableGames())).encode()
            for client in list(filter(lambda x: x.status == ClientStatus.CHOOSING_GAME, self.clients)):
                if not await client.write(available_games):
                    await self.removeClient(client)
            self.tick.release()
            self.tasks.remove(task)

    async def removeClient(self, client):
        async with self.clients_mutex:
            self.clients.remove(client)
        print('Client {} disconnected'.format(client.addr))

    async def handleJoin(self, client, command_data):
        if client.status == ClientStatus.CHOOSING_GAME:
            await self.addClientToGameSession(client, command_data['game_id'])
            client.setGameId(command_data['game_id'])
            client.setStatus(ClientStatus.WAITING_NOT_READY)

    async def addClientToGameSession(self, client, game_id):
        if game_id in self.game_sessions:
            await self.game_sessions[game_id].addClient(client)
            print('Client {} connected to game {}'.format(client.addr, self.game_sessions[game_id].name))

    async def handleCreate(self, client, command_data):
        if client.status == ClientStatus.CHOOSING_GAME:
            game_id = await self.createGameSession(command_data['name'])
            await self.addClientToGameSession(client, game_id)
            client.setGameId(game_id)
            client.setStatus(ClientStatus.WAITING_NOT_READY)
            self.game_sessions[game_id].startGameSession()

    async def createGameSession(self, name):
        print('Creating new game {}'. format(name))
        game_id = await self.getIdForGame()
        event = asyncio.Event()
        async with self.game_sessions_mutex:
            self.game_sessions[game_id] = GameSession(
                self.loop, name, event)
        self.loop.create_task(self.waitForGameFinished(event, game_id))
        return game_id

    async def getIdForGame(self):
        async with self.game_sessions_mutex:
            return next(new_id for new_id, exist_id
                        in enumerate(sorted(self.game_sessions.keys()) + [None], 0)
                        if new_id != exist_id)

    async def waitForGameFinished(self, event, game_id):
        await event.wait()
        async with self.game_sessions_mutex:
            print('Game {} finished'.format(self.game_sessions[game_id].name))
            self.game_sessions.pop(game_id, None)

    async def handleReady(self, client, command_data):
        if client.status == ClientStatus.WAITING_NOT_READY:
            await self.game_sessions[client.game_id].increaseReadiness()
            client.setStatus(ClientStatus.WAITING_READY)

    async def handleLeave(self, client, command_data):
        if client.game_id is None:
            return
        await self.removeClientFromGameSession(client)

    async def removeClientFromGameSession(self, client):
        await self.game_sessions[client.game_id].removeClient(client)


if __name__ == '__main__':
    loop = asyncio.get_event_loop()
    server = Server(loop)
    server.runServer()
