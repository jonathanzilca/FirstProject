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

