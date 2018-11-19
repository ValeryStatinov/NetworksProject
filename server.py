import asyncio


class Client:
    def __init__(self, reader, writer):
        self.reader = reader
        self.writer = writer


class Server:
    def __init__(self):
        self.loop = asyncio.get_event_loop()
        self.clients = []

    def run_server(self):
        self.coro = asyncio.start_server(
            self.handle_connection, '127.0.0.1', 8888, loop=self.loop)
        self.server = self.loop.run_until_complete(self.coro)
        print('Server is running on {}'.format(
            self.server.sockets[0].getsockname()))
        try:
            self.loop.run_forever()
        except KeyboardInterrupt:
            self.server.close()
            self.loop.run_until_complete(self.server.wait_closed())
            self.loop.close()

    async def handle_connection(self, reader, writer):
        self.clients.append(Client(reader, writer))
        await self.spam_client(writer)

    async def spam_client(self, writer):
        addr = writer.get_extra_info('peername')
        while True:
            if writer.is_closing():
                return
            print('spam to', addr)
            writer.write('spam'.encode())
            await asyncio.sleep(2)


server = Server()
server.run_server()
