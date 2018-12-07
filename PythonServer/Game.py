import asyncio
import json
from contextlib import suppress


DEFAULT_GAME_SPEED = 2
MAX_SLOTS = 8


class GameSession:
    def __init__(self, loop, game_id, name, event, game_speed=DEFAULT_GAME_SPEED):
        self.clients = []
        self.loop = loop
        self.game_id = game_id
        self.name = name
        self.game_speed = game_speed
        self.tick = asyncio.Condition()
        self.is_game_running = False
        self.is_waiting = False
        self.ready_count = 0
        self.event = event

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
        self.spam_task.cancel()
        self.tick_task.cancel()
        with suppress(asyncio.CancelledError):
            await self.spam_task
            await self.tick_task
        self.event.set()

    def addClient(self, client):
        self.clients.append(client)

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
            self.tick_task = self.loop.create_task(self.tick.wait())

            done, pending = await asyncio.wait([self.tick_task], return_when=asyncio.FIRST_COMPLETED)

            if self.tick_task in done:
                for client in self.clients:
                    await self.sapmClient(client)
                self.tick.release()
                self.tick_task = None

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

    def getMaxSlots(self):
        return MAX_SLOTS

    def getEmptySlots(self):
        return MAX_SLOTS - len(self.clients)

    def startGame(self):
        if not self.is_game_running:
            self.is_game_running = True
            self.loop.create_task(self.gameLoop())
            self.spam_task = self.loop.create_task(self.spamEveryone())
