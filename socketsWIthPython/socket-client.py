import socket
import threading

listen = True

def listen_to_response(clientSocket):
    while listen:
        data = clientSocket.recv(1024).decode()
        print('response=> '+data.strip())

def start_client():
    host = "127.0.0.1"
    port = 1234
    client_socket = socket.socket()
    client_socket.connect((host, port))
    thread = threading.Thread(target=listen_to_response, args=(client_socket,))
    thread.start()
    req = ""
    while req.lower().strip()!= 'exit':
        req = input('')
        client_socket.send(req.encode()+b'\n')
    listen = False
    client_socket.close()


if __name__ == '__main__':
    start_client()