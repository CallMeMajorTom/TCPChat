### Assignment2 - TCPChat ###
#### The format of testing input ####
>**<u>Example of the parameters:</u>** </br>
>`Server `- 25021 </br>
>`client` - 127.0.0.1 25021 XiaotongJiang </br>
>**<u>Examp le of each request:</u>**</br>
>`connection request` - Will be automatically send during the establishment of connection </br>
>`broadcast request`  - /broadcast,`message`  eg./broadcast,Hi,I'm Xiaotong Jiang </br>
>`private message`  - /tell `name`,`message`  eg./tell Alice,I'm Bob </br>
>`list request` - /list   </br>
>`leave request`  - /leave </br>
>`help request`- /help,`command`  eg./help,list</br>
>`kick request` - /kick,`name`   eg./kick,Alice</br>
>
>The `kick` is a command that is used when the administrator want to kick someone out from the chat, and the administrator is the first person of the chat among all member who participant this chat now.

#### Failure model ####

Failure | Solution  
- | -
The server shut down suddenly | Stop the socket and inform clients
The client leave the chat in an abnormal way (Power off, Shut down the chat GUI) | Shutdown the clientconnection thread and remove it from the connection list
The client will block in the read forever if no data arrives| Use setSotimeout() method and set reasonable timeout, when reach this timeout, SocketTimeoutException will be thrown 
The client send wrong command | Inform the client with "Error:This is a wrong command"
The client send wrong parameter in /help command | Inform the client with "Error:There is no such command"
The client send wrong parameter in /tell command | Inform the client with "Error:There is no *name*"
The client send wrong parameter in /kick command | Inform the client with "Error:There is no *name*"
The client who is not administrator wants to kick someone | Inform the client with "Error:Rejected(Don't have right)"
The packet loss over a connection passes some limit | Cannot handle
The network problem | Cannot handle

#### Comparison with UDP ###
- In TCP, a pair of communicating processes establish a connection before they can communicate over a stream. Once a connection is established, the processes simply read from and write to the stream without needing to use Internet addresses and ports. While in UDP, every time you send a instance of DataGramPacket which is an array of bytes comprising a message, the length of the message and the Internet address and local port number of the destination socket.
-  In TCP, messages are guaranteed to be delivered even when some of the underlying packets are lost, since TCP streams use timeouts and retransmissions to deal with lost packets. While in UDP, messages are not guaranteed to be delivered.
- In TCP, message identifiers are associated with each IP packet, which enables the recipient to detect and reject duplicates, or to reorder messages that do not arrive in sender order. While UDP message sometimes be delivered out of sender order.


For some applications who feel acceptable to use a service that is liable to occasional omission failures, and do not want to suffer from the overheads associated with guaranteed message delivery, UDP is a better choice. In contrast, if applications require more reliable delivery, TCP will be a better choice.

#### Reflection and Feedback
In this assignment, I have a better understanding on TCP communication. Because I continue using my UDPChat code, it is much clear to tell the differences between TCP and UDP during the process of achieving the function of TCP communication. Besides, by using JSON in communication, the level of standardization of message is improved, and I believe JSON will show more advantages in the situation where the message is more complex. Meanwhile, in order to handle some failures, I have better understanding on exception and error in Java, such as try catch and throw mechanism. By using thread to achieve clientconnection, I have better understanding on the features and methods of use, in this case, I use thread by overwrite the run() method of Thread, and call start() and stop() method in proper opportunity.
