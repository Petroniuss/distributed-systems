import socket

serverIP = "127.0.0.1"
serverPort = 9009
msg = "żółta gęś"
ID = 'Task-2-Python-UDP-Client'

print(ID)
client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
client.sendto(bytes(msg, 'UTF-8'), (serverIP, serverPort))




