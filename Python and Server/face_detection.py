# install cmake, dlib (19.18.0 version),face-recognition,numpy, opencv-python

import os
import cv2
import numpy as np
import face_recognition

path = "faceImages"
images = []
classNames = []
myList = os.listdir(path)
for cl in myList:
    curImg = cv2.imread(f'{path}/{cl}')
    images.append(curImg)
    classNames.append(os.path.splitext(cl)[0])

    # print(classNames)


def findEncoding(images):
    encodeList = []
    for img in images:
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        encode = face_recognition.face_encodings(img)[0]
        encodeList.append(encode)
    return encodeList


def faceRecognizer():
    encodeListKnown = findEncoding(images)

    cap = cv2.VideoCapture(0, cv2.CAP_DSHOW)
    curName = ""
    name = "  "
    while True:
        success, img = cap.read()
        imgS = cv2.resize(img, (0, 0), None, 0.25, 0.25)
        imgS = cv2.cvtColor(imgS, cv2.COLOR_BGR2RGB)
        faceCurFrame = face_recognition.face_locations(imgS)
        encodeCurFrame = face_recognition.face_encodings(imgS, faceCurFrame)
        for encodeFace, faceLocation in zip(encodeCurFrame, faceCurFrame):
            matches = face_recognition.compare_faces(encodeListKnown, encodeFace)
            face_accuracy = face_recognition.face_distance(encodeListKnown, encodeFace)
            matchIndex = np.argmin(face_accuracy)

            if matches[matchIndex]:
                name = classNames[matchIndex]
                # x1, y1, x2, y2 = faceLocation
                # x1, y1, x2, y2 = x1 * 4, y1 * 4, x2 * 4, y2 * 4
                # # cv2.rectangle(img, (x1, y1), (x2, y2), (255, 255, 255), 2) # to create a square around the face
                # cv2.rectangle(img, (x1, y2 - 35), (x2, y2), (255, 255, 255), cv2.FILLED)  # place to write the name
                # cv2.putText(img, name, (x1 + 6, y2 - 6), cv2.FONT_HERSHEY_COMPLEX, 1, (0, 0, 0), 1) # writing the name
                the_most_right_match = min(face_accuracy)
                if the_most_right_match > 0.55:
                    name = "Unknown"
                    # cv2.rectangle(img, (x1, y1), (x2, y2), (255, 255, 255), 2)
                    # cv2.rectangle(img, (x1, y2 - 35), (x2, y2), (255, 255, 255), cv2.FILLED)
                    # cv2.putText(img, name, (x1 + 6, y2 - 6), cv2.FONT_HERSHEY_COMPLEX, 1, (0, 0, 0), 1)

            if curName is not name:
                curName = name
            if curName == name:
                return name


recognized_name = faceRecognizer()
if "Jonathan" == recognized_name:
    print("hey master")
    print("your name is Jonathan Zilca and are 20 years old.")
    print("These days you work as a soldier.")

