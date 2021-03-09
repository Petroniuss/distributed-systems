import socket

serverIP = "127.0.0.1"
serverPort = 9011
ID = 'Task-4-Python-UDP-Client'

header_byte = (1).to_bytes(1, byteorder='big')
message = 'Python-Ping!'
meassage_bytes = message.encode('utf-8')

bytes_stream = header_byte + meassage_bytes

print(ID)
client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
client.sendto(bytes_stream, (serverIP, serverPort))

buff, _ = client.recvfrom(1024)
received = buff.decode('utf-8')
print(ID + ' received: ' + received)


