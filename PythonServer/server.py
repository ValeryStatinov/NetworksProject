import asyncio
from Game import GameSession
import json


class Client:
    def __init__(self, reader, writer):
        self.reader = reader
        self.writer = writer
        self.addr = writer.get_extra_info('peername')


class Server:
    def __init__(self):
        self.loop = asyncio.get_event_loop()
        self.clients = []
        self.game_sessions = {}
        self.game_sessions_mutex = asyncio.Lock()
        self.avaliableCommands = {
            'join': self.handleJoinCommand, 'create_game': self.handleCreateGameCommand}

    def runServer(self):
        coro = asyncio.start_server(
            self.handleConnection, '127.0.0.1', 8888, loop=self.loop
        )
        self.server = self.loop.run_until_complete(coro)
        print('Server is running on {}'.format(
            self.server.sockets[0].getsockname()))
        try:
            self.loop.run_forever()
        except KeyboardInterrupt:
            self.server.close()
            self.loop.run_until_complete(self.server.wait_closed())
            self.loop.close()

    async def handleConnection(self, reader, writer):
        client = Client(reader, writer)
        await self.sendAvaliableGames(client)
        await self.receiveCommand(client)

    async def sendAvaliableGames(self, client):
        avaliable_games = self.getJSONavaliableGames()
        client.writer.write(avaliable_games.encode())
        try:
            await client.writer.drain()
        except ConnectionResetError:
            print('lost connection from', client.addr)

    def getJSONavaliableGames(self):
        avaliable_games = []
        for game_id, game in self.game_sessions.items():
            avaliable_games.append(
                {'game_id': game_id, 'name': game.name, 'empty_slots': game.getEmptySlots(), 'max_slots': game.getMaxSlots()})
        return json.dumps(avaliable_games)

    async def receiveCommand(self, client):
        command_data = await client.reader.read(100)
        command_data = json.loads(command_data.decode())
        if command_data['command'] not in self.avaliableCommands:
            print('from', client.addr, 'receives wrong command:', command_data)
            return
        else:
            await self.avaliableCommands[command_data['command']](client, command_data)

    async def handleJoinCommand(self, client, command_data):
        async with self.game_sessions_mutex:
            if not int(command_data['game_id']) in self.game_sessions:
                print('wrong game id from', client.addr,
                      'received', command_data['game_id'])
                return
            elif self.game_sessions[int(command_data['game_id'])].isGameRunning():
                print(command_data['game_id'],
                      'is alredy started, cannot connect', client.addr, 'to the game')
                return
            else:
                self.connectClientToGameSession(
                    client, int(command_data['game_id']))

    def connectClientToGameSession(self, client, game_id):
        self.game_sessions[game_id].addClient(client)

    async def handleCreateGameCommand(self, client, command_data):
        async with self.game_sessions_mutex:
            game_id = self.createNewGameSession(command_data['name'])
            self.connectClientToGameSession(client, game_id)

    def getAvailableGameId(self):
        return len(self.game_sessions.keys())

    def createNewGameSession(self, name):
        event = asyncio.Event()
        game_id = self.getAvailableGameId()
        self.game_sessions[game_id] = GameSession(
            self.loop, game_id, name, event)
        self.loop.create_task(
            self.game_sessions[game_id].waitForReadyFromEveryone())
        self.loop.create_task(self.waitForGameFinised(event, game_id))
        return game_id

    async def waitForGameFinised(self, event, game_id):
        await event.wait()
        self.game_sessions.pop(game_id, None)
        print('game with id', game_id, 'was destroyed')


server = Server()
server.runServer()
