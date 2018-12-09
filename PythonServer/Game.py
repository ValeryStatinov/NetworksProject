import asyncio
import json
from enum import Enum, auto
from contextlib import suppress
from Client import Client, ClientStatus

DEFAULT_GAME_SPEED = 1
MAX_SLOTS = 8


class GameSessionStatus(Enum):
    WAITING_READY = auto()
    GAME_STARTED = auto()
    NOT_STARTED = auto()
    FINISHED = auto()


class GameSession:
    def __init__(self, loop, name, end_game_session_event, game_speed=DEFAULT_GAME_SPEED):
        self.clients = []
        self.clients_mutex = asyncio.Lock()
        self.loop = loop
        self.name = name
        self.game_speed = game_speed
        self.status = GameSessionStatus.NOT_STARTED
        self.tick = asyncio.Condition()
        self.ready_count = 0
        self.end_game_session_event = end_game_session_event
        self.everyone_ready_event = asyncio.Event()
        self.tasks = []

    async def gameLoop(self):
        print('Game session loop started in {} game'.format(self.name))
        while not self.status == GameSessionStatus.FINISHED and len(self.clients):
            await self.tick.acquire()
            self.tick.notify_all()
            self.tick.release()
            await asyncio.sleep(self.game_speed)

        print('Destroying coroutines in {} game'.format(self.name))
        self.status = GameSessionStatus.FINISHED
        self.ready_count = 0
        for task in self.tasks:
            task.cancel()
            with suppress(asyncio.CancelledError):
                await task
        self.end_game_session_event.set()

    async def addClient(self, client):
        async with self.clients_mutex:
            self.clients.append(client)
            await client.write(self.getJSONMetaInfo())

    def getJSONMetaInfo(self):
        meta_info = {'empty_slots': self.getEmptySlots(), 'max_slots': self.getMaxSlots(), 'ready': self.ready_count}
        return (json.dumps(meta_info)).encode()

    async def sendMetaInfo(self):
        print('Started sending meta info in {} game'.format(self.name))
        while self.status == GameSessionStatus.WAITING_READY:
            await self.tick.acquire()
            task = self.loop.create_task(self.tick.wait())
            self.tasks.append(task)
            await task

            meta_info = self.getJSONMetaInfo()
            for client in self.clients:
                if not await client.write(meta_info):
                    await self.removeClient(client)

            self.tick.release()
            self.tasks.remove(task)

    async def removeClient(self, client):
        async with self.clients_mutex:
            self.clients.remove(client)
            if client.status == ClientStatus.WAITING_READY:
                self.ready_count -= 1
            client.setStatus(ClientStatus.CHOOSING_GAME)

    @staticmethod
    def getMaxSlots():
        return MAX_SLOTS

    def getEmptySlots(self):
        return MAX_SLOTS - len(self.clients)

    def startGameSession(self):
        if self.status == GameSessionStatus.NOT_STARTED:
            self.status = GameSessionStatus.WAITING_READY
            self.tasks.append(self.loop.create_task(self.gameLoop()))
            self.tasks.append(self.loop.create_task(self.sendMetaInfo()))
            self.tasks.append(self.loop.create_task(self.sendGameData()))

    async def increaseReadiness(self):
        self.ready_count += 1
        async with self.clients_mutex:
            if self.ready_count == len(self.clients):
                print('Start playing {} game in 5 seconds'.format(self.name))
                await asyncio.sleep(5)
                self.status = GameSessionStatus.GAME_STARTED
                self.everyone_ready_event.set()

    async def sendGameData(self):
        await self.everyone_ready_event.wait()
        print('Start playing')

        while self.status == GameSessionStatus.GAME_STARTED:
            await self.tick.acquire()
            task = self.loop.create_task(self.tick.wait())
            self.tasks.append(task)
            await task

            spam = (json.dumps({'hello': 'world'})).encode()
            for client in self.clients:
                if not await client.write(spam):
                    await self.removeClient(client)

            self.tick.release()
            self.tasks.remove(task)
