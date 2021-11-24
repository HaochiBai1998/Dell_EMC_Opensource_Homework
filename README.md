# Dell EMC Pravega Homework

> ### **Orientation:**
>
> Implement **client group chat** .
>
> Implement **one to one** client file transmission.
>
> Implement **group** client file transmission.

## Coding Environment

1. Java 11
2. Pravega 0.10.1
3. IntelliJ

## Client group chat
### - Basic Architecture

In my group chat architecture, one stream serves as a message-exchange center for a chat room. Each client in a chat room will create their own Writer, ReaderGroup and Reader based on their client ID and connect to one stream based on chat room ID.

For writing task, client can push a string as an event to their connected stream.

For reading task, each client will create a reading thread, this reading thread will constantly pull data from connected stream until it received a interrupt from client.

Client will be terminated when user type "Bye." in System.in.
- Running instructions
1. run "GroupChat.java"
2. input client name in terminal
3. input chat room ID in terminal
4. start chatting!
5. Terminate your client with "Bye." message
### - Demo
<img alt="ChatGroup.png" height="350" src="https://i.loli.net/2021/11/23/SHLKlvFV4tI3rPd.png" width="800"/>

## File transmission
### - Basic Architecture
When a client makes a request for uploading a file, a new stream will be created based on the hashed result of uploaderID, receiverID and fileName. File data will be uploaded to that stream. When another client want to download that file, a mapping uploaderID, receiverID and file name need to be provided.
### One to one file transmission

#### - Upload file
- Running instructions

1. run "GroupChat.java" as previous.
2. Type "Upload" in chat room when you want to upload file to a user.
3. choose "One to one file transfer" mode.
4. input receiver client ID.
5. input the path or the name of a file that you want to upload.
6. If file is uploaded successfully, a message will be sent to chat room:
   (ClientID):Upload_(fileName)to(receiverID)
    #### - Demo

    <img alt="One2OneUpload.png" height="200" src="https://i.loli.net/2021/11/23/xOvu8RdGDQsTif7.png" width="350"/>

#### - Download file
- Running instructions

1. run "GroupChat.java" as previous.
2. Type "Download" in chat room when you want to download file to a user.
3. choose "One to one file transfer" mode.
4. input receiver client ID.
5. input the path or the name of a file that you want to download.
6. If file is downloaded successfully, a message will be sent to chat room:
   (ClientID):Download_(fileName)from(uploaderID)
   #### - Demo

    <img alt="One2OneDownload.png" height="350" src="https://i.loli.net/2021/11/23/xwjptTDZankf39B.png" width="450"/>

### Group file transmission

#### Upload file
- Running instructions
1. almost the same as One-to-one transmission, but choose mode "Group file transfer"
   #### - Demo

    <img alt="GroupFileUpload.png" height="270" src="https://i.loli.net/2021/11/23/xnwOfYAzeFPZEBs.png" width="380"/>

#### Download file

- Running instructions
1. always the same as One-to-one transmission, but choose mode "Group file transfer"

   ##### - Demo
    <img alt="GroupFileDownload.png" height="320" src="https://i.loli.net/2021/11/23/MBLOP8CEWJqUDma.png" width="420"/>

## Meta

Haochi Bai – hb174@duke.edu
