/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCPChat.Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import org.json.simple.JSONObject;

/**
 *
 * @author brom
 */
public class ServerConnection {
	
	
    private Socket m_socket = null;
    private int m_serverPort = -1;

    public ServerConnection(String hostName, int port) throws IOException {
    	m_serverPort = port;

	// TODO: 
	// * set up socket and assign it to m_socke
		m_socket = new Socket(hostName, m_serverPort);
	    m_socket.setSoTimeout(100000);//To avoid being stuck
    }

    public boolean handshake(String name) throws IOException {
	// TODO:
	// * marshal connection message containing user name
	// * send message via socket
	// * receive response message from server
	// * unmarshal response message to determine whether connection was successful
	// * return false if connection failed (e.g., if user name was taken)
    	ChatMessage msg = new ChatMessage("/join",name,name);
    	DataInputStream in = new DataInputStream( m_socket.getInputStream());
    	DataOutputStream out = new DataOutputStream( m_socket.getOutputStream());
    	sendChatMessage(msg);//send the /join message to establish a connection
    	String received = in.readUTF();
    	if(received.equalsIgnoreCase("NameExist")) {
    		System.out.println("Client received:" + received);
    		return false;
    	}
    	else
    		System.out.println("Client received:" + received);
    		return true;
    }

    public String receiveChatMessage() throws IOException {
	// TODO: 
	// * receive message from server
	// * unmarshal message if necessary
	
	// Note that the main thread can block on receive here without
	// problems, since the GUI runs in a separate thread
	 
	// Update to return message contents
    String received = "";
    DataInputStream in = new DataInputStream( m_socket.getInputStream());
    try{
    	received = in.readUTF();
    }catch(SocketException e){
    	e.printStackTrace();
    	m_socket.close();
    	return "The server has been stopped!";
    }
   
    ChatMessage message = new ChatMessage(received);
    String display = message.getCommand()+":"+message.getParameters()+message.getSender();
    return display;
    }

    public void sendChatMessage(ChatMessage msg) throws IOException {
    	Random generator = new Random();
    	int i = 0;
    	double failure = generator.nextDouble();
    	DataOutputStream out = new DataOutputStream(m_socket.getOutputStream());
    	String jsonmsg = msg.toString();
		// TODO: 
		// * marshal message if necessary
		// * send a chat message to the server
		out.writeUTF(jsonmsg);
    }
}
