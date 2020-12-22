# A TCP-based socket communication system

- This is a communication system using socket programming techniques.
- It includes a server and clients, which can realize online chatting. 
- The server could supports multiple clients. It assigns multiple userThreads to process each connected client. 
- And I use Wireshark to capture and analyze the transmitting packets.  

#### About Socket Programming

Sockets provide the communication mechanism between two computers using TCP. A client program creates a socket on its end of the communication and attempts to connect that socket to a server. When the connection is made, the server creates a socket object on its end of the communication. The client and the server can now communicate by writing to and reading from the socket.
