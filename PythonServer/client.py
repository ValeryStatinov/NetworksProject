import asyncio
from contextlib import suppress
import sys
import json


async def get_spam(reader, writer):
    while True:
        data = await reader.read(100)
        print(data.decode())


async def tcp_echo_client(loop):
    reader, writer = await asyncio.open_connection('127.0.0.1', 8888, loop=loop)
    games = await reader.read(100)
    games = json.loads(games.decode())
    print(games)
    print('What game you want to join?')
    msg = input()
    if msg == 'new':
        name = input('Enter game name: ')
        command = (json.dumps(
            {'command': 'create_game', 'name': name})).encode()
        writer.write(command)
    else:
        command = (json.dumps({'command': 'join', 'game_id': msg})).encode()
        writer.write(command)

    await writer.drain()
    ready = input('Enter go whenever you ready: ')
    if ready == 'go':
        command = (json.dumps({'command': 'ready_to_start'}).encode())
    writer.write(command)
    await writer.drain()

    await get_spam(reader, writer)

loop = asyncio.get_event_loop()
task = loop.create_task(tcp_echo_client(loop))
try:
    loop.run_forever()
except KeyboardInterrupt:
    print('caught interrupt')
    task.cancel()
    with suppress(asyncio.CancelledError):
        loop.run_until_complete(task)
        loop.close()
