import base64
import socket
import cv2 as cv
import base64,time, threading
import numpy as np

img = open("image/image0.png", 'rb')
msg = img.read()
img.close()
BUFF_SIZE = 65536
WIDTH = 356
HEIGHT = 600


def GetMsg():
    print("Getting messages")
    socket_address = ("192.168.1.204", 4381)
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(socket_address)
    while True:
        s.listen(1)
        connection, client_address = s.accept()

        data = connection.recv(4096).decode()
        if data != "":
            print(data)

def SendVideo():
    socket_address = ("192.168.1.204", 4382)
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_RCVBUF, BUFF_SIZE)
    server_socket.settimeout(0.001)
    host_name = socket.gethostname()
    server_socket.bind(socket_address)
    print('Listening at:', socket_address)
    vid = cv.VideoCapture(0)
    FPS = vid.get(cv.CAP_PROP_FRAME_COUNT)
    _, frame = vid.read()
    vidheight = len(frame[0])
    vidwidth = len(frame)
    # fps
    test = 0
    while True:
        try:
            msg, client_address = server_socket.recvfrom(BUFF_SIZE)
        except:
            continue
        print('Got connection:' + str(client_address))
        print("Message from client:" + msg.decode())
        while vid.isOpened():
            _, frame = vid.read()
            cv.imshow("test",frame)
            frame = frame[round(vidheight/2 - HEIGHT/2):round(vidheight/2 + HEIGHT/2),
                    round(vidwidth/2 - WIDTH/2):round(vidwidth/2 + WIDTH/2)]
            encode, buffer = cv.imencode('.jpg', frame, [cv.IMWRITE_JPEG_QUALITY, 90])
            message = base64.b64encode(buffer)
            test = max(test, len(message))
            cv.imshow('TRANSMITTING', frame)
            server_socket.sendto(message, client_address)
            try:
                msg, client_address = server_socket.recvfrom(BUFF_SIZE)
                if len(msg.decode()) > 0:
                    cv.destroyAllWindows
                    break
            except:
                pass
            key = cv.waitKey(1) & 0xFF
            if key == ord('q'):
                cv.destroyAllWindows
                break


threading.Thread(target=SendVideo).start()
threading.Thread(target=GetMsg).start()



