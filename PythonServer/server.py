import asyncio
from Game import GameSession
import json

GAME_SPEED = 2


class Client:
    def __init__(self, reader, writer):
        self.reader = reader
        self.writer = writer


class Server:
    def __init__(self):
        self.loop = asyncio.get_event_loop()
        self.clients = []
        self.game_sessions = {0: GameSession(self.loop, 3, 'asdf', 3)}

    async def handleCommand(self, client):
        command = await client.reader.read(100)
        command = json.loads(command.decode())
        if command['command'] == 'join':
            if self.game_sessions[int(command['game_id'])].isGameRunning():
                print('connection refused')
                return
            await self.connectClientToGameSession(int(command['game_id']), client)
        elif command['command'] == 'create_game':
            game_id = self.createNewGameSession(command['name'])
            await self.connectClientToGameSession(game_id, client)

    async def connectClientToGameSession(self, game_id, client):
        await self.game_sessions[game_id].addClient(client)

    def createNewGameSession(self, name):
        game_id = len(self.game_sessions.keys())
        self.game_sessions[game_id] = GameSession(
            self.loop, game_id, name, GAME_SPEED)
        return game_id

    async def sendAvaliableGames(self, client):
        avaliable_games = {}
        for game_id, game_session in self.game_sessions.items():
            avaliable_games[game_id] = game_session.name
        client.writer.write(json.dumps(avaliable_games).encode())
        addr = client.writer.get_extra_info('peername')
        try:
            await client.writer.drain()
        except ConnectionResetError:
            print('lost connection from', addr)

    async def handleConnection(self, reader, writer):
        client = Client(reader, writer)
        await self.sendAvaliableGames(client)
        await self.handleCommand(client)

    def run_server(self):
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


server = Server()
server.run_server()
