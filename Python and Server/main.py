import base64
import socket
import cv2 as cv
import base64,time, threading
import numpy as np
import pyttsx3
import face_detection

BUFF_SIZE = 65000
WIDTH = 380
HEIGHT = 640

hostname = socket.gethostname()
IPAddr = socket.gethostbyname(hostname)

def robot_talk(text):
    engine = pyttsx3.init()
    engine.say(text)
    engine.runAndWait()


def GetMsg():
    print("Getting messages")
    socket_address = (IPAddr, 4381)
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(socket_address)
    while True:
        s.listen(1)
        connection, client_address = s.accept()

        data = connection.recv(4096).decode()
        if data != "":
            print(data)
            robot_talk(data)

def SendNotify():
    socket_address = (IPAddr, 4383)
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(socket_address)
    while True:
        s.listen(1)
        connection, client_address = s.accept()
        print("sending intrusion")
        face_detection.faceRecognizer()
        msg = face_detection.faceRecognizer() + "is approaching the door!"
        connection.send(msg.encode())


def SendVideo():
    socket_address = (IPAddr, 4382)
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_SNDBUF, BUFF_SIZE)
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_RCVBUF, BUFF_SIZE)
    server_socket.settimeout(0.001)
    try:
        server_socket.recvfrom(BUFF_SIZE)
    except:
        pass
    server_socket.bind(socket_address)
    print('Listening at:', socket_address)
    vid = cv.VideoCapture(0)
    FPS = vid.get(cv.CAP_PROP_FRAME_COUNT)
    _, frame = vid.read()
    vidheight = len(frame[0])
    vidwidth = len(frame)
    # fps
    clients = []
    test = 0
    while vid.isOpened():
        try:
            msg, client_address = server_socket.recvfrom(BUFF_SIZE)
            if msg.decode() == "close":
                for client in clients:
                    if client[0] == client_address[0] and client[1] == client_address[1]:
                        clients.remove(client)
                        print('popped ' + client_address)
                        break
                else:
                    print(f'received close from unconnected client({client_address})')
            else:
                print('Got connection:' + str(client_address))
                print("Message from client:" + msg.decode())
                clients.append(client_address)
                print(clients)
        except:
            key = cv.waitKey(1) & 0xFF
            if key == ord('q'):
                cv.destroyAllWindows
                server_socket.close()
                break
            if len(clients) == 0:
                continue

        # while vid.isOpened():
        _, frame = vid.read()
        frame = frame[round(vidheight/2 - HEIGHT/2):round(vidheight/2 + HEIGHT/2),
                round(vidwidth/2 - WIDTH/2):round(vidwidth/2 + WIDTH/2)]
        qu = 90
        encode, buffer = cv.imencode('.jpg', frame, [cv.IMWRITE_JPEG_QUALITY, qu])
        while len(buffer) > 0.74 * BUFF_SIZE and qu > 0:
            qu -= 5
            encode, buffer = cv.imencode('.jpg', frame, [cv.IMWRITE_JPEG_QUALITY, qu])
        if qu == 0:
            continue
        message = base64.b64encode(buffer)
        # print(f"buffer: {len(buffer)}, message: {len(message)}, ratio:{len(buffer)/len(message)}")
        # if len(message) > test:
        #     test = max(test, len(message))
        #     print(test)
        cv.imshow('TRANSMITTING', frame)
        for client in clients:
            server_socket.sendto(message, client)




print("Your Computer Name is:"+hostname)
print("Your Computer IP Address is:"+IPAddr)
# threading.Thread(target=SendVideo).start()
threading.Thread(target=GetMsg).start()
threading.Thread(target=SendNotify).start()

