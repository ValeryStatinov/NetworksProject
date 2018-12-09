import asyncio
from enum import Enum, auto
from contextlib import suppress

READ_NUM_BYTES = 100


class ClientStatus(Enum):
    CHOOSING_GAME = auto()
    WAITING_READY = auto()
    WAITING_NOT_READY = auto()
    PLAYING = auto()


class Client:
    def __init__(self, reader, writer, client_id):
        self.reader = reader
        self.writer = writer
        self.id = client_id
        self.addr = writer.get_extra_info('peername')
        self.status = ClientStatus.CHOOSING_GAME
        self.game_id = None
        self.reading_task = None

    async def read(self):
        loop = asyncio.get_event_loop()
        self.reading_task = loop.create_task(
            self.reader.read(READ_NUM_BYTES))
        return await self.reading_task

    async def write(self, data):
        self.writer.write(data)
        try:
            await self.writer.drain()
            return True
        except ConnectionResetError:
            await self.cancelReadingTask()
            return False

    async def cancelReadingTask(self):
        self.reading_task.cancel()
        with suppress(asyncio.CancelledError):
            await self.reading_task

    def setGameId(self, game_id):
        self.game_id = game_id

    def setStatus(self, status):
        self.status = status
