import asyncio
import json


class GameSession:
    def __init__(self, loop, game_id, name, game_speed):
        self.clients = []
        self.loop = loop
        self.game_id = game_id
        self.name = name
        self.game_speed = game_speed
        self.tick = asyncio.Condition()
        self.is_game_running = False
        self.is_waiting = False
        self.ready_count = 0

    async def gameLoop(self):
        while self.is_game_running and len(self.clients):
            print('tick')
            await self.tick.acquire()
            self.tick.notify_all()
            self.tick.release()
            await asyncio.sleep(self.game_speed)
        self.is_game_running = False
        self.is_waiting = False
        self.ready_count = 0

    async def addClient(self, client):
        self.clients.append(client)
        if not self.is_waiting:
            self.is_waiting = True
            await self.waitForReadyFromEveryone()

    async def sapmClient(self, client):
        addr = client.writer.get_extra_info('peername')
        print('spam to', addr)
        client.writer.write('spam'.encode())
        try:
            await client.writer.drain()
        except ConnectionResetError:
            print('lost connection from', addr)
            self.clients.remove(client)

    async def spamEveryone(self):
        while self.is_game_running:
            await self.tick.acquire()
            tick_task = self.loop.create_task(self.tick.wait())

            done, pending = await asyncio.wait([tick_task], return_when=asyncio.FIRST_COMPLETED)

            if tick_task in done:
                for client in self.clients:
                    await self.sapmClient(client)
                self.tick.release()
                tick_task = None

    async def waitForReadyFromClient(self, client):
        is_ready = await client.reader.read(100)
        is_ready = json.loads(is_ready.decode())
        if is_ready['command'] == 'ready_to_start':
            self.ready_count += 1
            if self.ready_count == len(self.clients):
                self.startGame()

    async def waitForReadyFromEveryone(self):
        while not self.is_game_running:
            for client in self.clients:
                await self.waitForReadyFromClient(client)

    def isGameRunning(self):
        return self.is_game_running

    def startGame(self):
        if not self.is_game_running:
            self.is_game_running = True
            self.loop.create_task(self.gameLoop())
            self.loop.create_task(self.spamEveryone())
