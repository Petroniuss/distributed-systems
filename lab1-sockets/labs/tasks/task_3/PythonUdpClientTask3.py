import socket

serverIP = "127.0.0.1"
serverPort = 9010
ID = 'Task-3-Python-UDP-Client'
msg_bytes = (300).to_bytes(4, byteorder='little')

print(ID)
client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
client.sendto(msg_bytes, (serverIP, serverPort))

buff, _ = client.recvfrom(1024)
received_number = int.from_bytes(buff, byteorder='little')
print(ID + ' received: ' + str(received_number))


