import asyncio
from contextlib import suppress


async def get_spam(reader, writer):
    while True:
        data = await reader.read(100)
        print(data.decode())


async def tcp_echo_client(loop):
    reader, writer = await asyncio.open_connection('127.0.0.1', 8888, loop=loop)
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
