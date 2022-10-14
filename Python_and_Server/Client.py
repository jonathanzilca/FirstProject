import base64
import socket
import cv2 as cv
import numpy as np
socket_address = ("192.168.1.204", 4382)
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
BUFF_SIZE = 65536

print("Starting")
s.sendto(b"Hello", socket_address)
while True:
    packet, _ = s.recvfrom(BUFF_SIZE)
    data = base64.b64decode(packet, ' /')
    npdata = np.fromstring(data, dtype=np.uint8)
    frame = cv.imdecode(npdata, 1)
    cv.imshow("RECEIVING", frame)
    key = cv.waitKey(1) & 0xFF
    if key == ord('q'):
        s.close()
        cv.destroyAllWindows
        break


# data = temp
# while len(temp) == 200000:
#     temp = s.recv(200000)
#     data += temp
# print(data)
# file = open("test.png", "wb")
# file.truncate()
# file.write(data)
# file.close()
# s.close()
# cv.imshow('test', cv.imread("test.png", 1))

print("Finished")



# s.connect(socket_address)


# temp = s.recv(200000)


