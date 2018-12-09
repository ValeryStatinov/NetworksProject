import asyncio
from contextlib import suppress
import json
import sys

TASKS = []
DELAY = 5


async def getMsg(reader):
    print('read started')
    while True:
        data = await reader.read(100)
        data = json.loads(data.decode())
        print(data)


async def sendCreate(writer):
    await asyncio.sleep(DELAY)
    msg = (json.dumps(
        {'command': 'create_game', 'name': 'fuck'})).encode()
    writer.write(msg)

    await asyncio.sleep(5)
    #sendReady(writer)

    msg = (json.dumps({'command': 'leave_game'})).encode()
    writer.write(msg)


async def sendJoin(writer):
    await asyncio.sleep(DELAY)
    msg = (json.dumps({'command': 'join', 'game_id': 0})).encode()
    writer.write(msg)

    await asyncio.sleep(5)
    #sendReady(writer)


def sendReady(writer):
    print('Send ready command')
    msg = (json.dumps({'command': 'ready_to_start'})).encode()
    writer.write(msg)


async def tcp_echo_client(loop):
    reader, writer = await asyncio.open_connection('127.0.0.1', 8888, loop=loop)
    games = await reader.read(100)
    games = json.loads(games.decode())
    print(games)
    TASKS.append(loop.create_task(getMsg(reader)))
    if sys.argv[1] == 'create':
        TASKS.append(loop.create_task(sendCreate(writer)))
    elif sys.argv[1] == 'join':
        TASKS.append(loop.create_task(sendJoin(writer)))


loop = asyncio.get_event_loop()
TASKS.append(loop.create_task(tcp_echo_client(loop)))
try:
    loop.run_forever()
except KeyboardInterrupt:
    print('caught interrupt')
    for task in TASKS:
        task.cancel()
        with suppress(asyncio.CancelledError):
            loop.run_until_complete(task)
    loop.close()
