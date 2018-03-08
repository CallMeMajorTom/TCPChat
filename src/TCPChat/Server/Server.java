package TCPChat.Server;

//
// Source file for the server side. 
//
// Created by Sanny Syberfeldt
// Maintained by Marcus Brohede
//

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
//import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONObject;


public class Server {
	
    private ArrayList<ClientConnection> m_connectedClients = new ArrayList<ClientConnection>();
    private static ServerSocket m_socket;
    
    public static void main(String[] args) throws IOException{
	if(args.length < 1) {
	    System.err.println("Usage: java Server portnumber");
	    System.exit(-1);
	}
	try {
	    Server instance = new Server(Integer.parseInt(args[0]));
	   }
    catch(NumberFormatException e) {
	    System.err.println("Error: port number must be an integer.");
	    System.exit(-1);
	}
	}

	private Server(int portNumber) throws IOException {
    	// TODO: create a ServerSocket, attach it to port based on portNumber, and assign it to m_socket
    	m_socket = new ServerSocket(portNumber);
    	System.out.println("Waiting for client messages... ");
    	while(true){
    		Socket clientSocket = m_socket.accept();
	    	DataInputStream in;
	    	DataOutputStream out;
	    	out = new DataOutputStream(clientSocket.getOutputStream());
			in = new DataInputStream(clientSocket.getInputStream());
			String received = in.readUTF();
			ChatMessage msg = new ChatMessage(received);
			if(addClient(msg.getParameters(), clientSocket)){
	    		String message = "success";
	    		out.writeUTF(message);
	    		ChatMessage broadcast = new ChatMessage("New participants",msg.getParameters(),"(broadcast");
		    	broadcast(broadcast);//inform everyone
	    	}
	    	else{//Name has been token
	    		String message = "NameExist";
	    		out.writeUTF(message);
	    	} 
    	}
    }

    public void operate(ChatMessage msg) throws IOException {
	    // TODO: Listen for client messages.
	    // On reception of message, do the following:
	    // * Unmarshal message
	    // * Depending on message type, either
	    //    - Try to create a new ClientConnection using addClient(), send 
	    //      response message to client detailing whether it was successful
	    //    - Broadcast the message to all connected users using broadcast()
	    //    - Send a private message to a user using sendPrivateMessage()
		if(msg.getCommand().equalsIgnoreCase("/broadcast")){//Broadcast(Display in everyone's GUI)
			ChatMessage broadcast = new ChatMessage(msg.getSender(),msg.getParameters(),"(broadcast");
	    	broadcast(broadcast);
	    }
	    else if(msg.getCommand().contains("/tell")){//PrivateMsg(Display in Sender and Receiver's GUI)
	    	ChatMessage message = new ChatMessage(msg.getSender(),msg.getParameters(),"(private");
	    	//If the be-told one do not exist, the sender will be informed with an Error
	    	if(!sendPrivateMessage(message, msg.getCommand().substring(6, msg.getCommand().length())))
	    		message = new ChatMessage("Error","There is no "+msg.getCommand().substring(6, msg.getCommand().length()),"(private");
	    	//Inform the sender
	    	sendPrivateMessage(message,msg.getSender());
	    }
	    else if(msg.getCommand().equalsIgnoreCase("/kick")){//Administrator kick someone out from the chat(Display in everyone's GUI)
	    	if(m_connectedClients.get(0).getTheName().equalsIgnoreCase(msg.getSender())){//The first participant of the chat is administrator
	    		if(remove(msg.getParameters())){//remove the kicked member from the m_connectedClients list
	    		ChatMessage message = new ChatMessage("Kicked",msg.getParameters(),"(broadcast");
		    	broadcast(message);
	    		}
	    		else{
    			ChatMessage message = new ChatMessage("Error","There is no "+msg.getParameters(),"(private");
    			sendPrivateMessage(message,msg.getSender());
	    		}
	    	}
	    	else {//If the sender of the request is not administrator, inform him with an Error
	    		ChatMessage message = new ChatMessage("Error","Rejected(Don't have right)","(private");
	    		sendPrivateMessage(message,msg.getSender());
	    	}
	    }
	    else if(msg.getCommand().equalsIgnoreCase("/list")) {//List the user's name and the administrator(Display in Sender's GUI)
	    	ClientConnection c;
	    	String message = "";
	    	for(Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
	    	    c = itr.next();
	    	    message = message + c.getTheName() + "\n";  
	    	    }
	    	message = message + "============\n";
    		ChatMessage Message = new ChatMessage("====List====\nadministrator",message,"(private");
	    	sendPrivateMessage(Message,msg.getSender());
	    }
	    else if(msg.getCommand().equalsIgnoreCase("/leave")){//Leave chatting-room(Display in everyone's GUI)
	    	ChatMessage message = new ChatMessage("leave",msg.getSender(),"(broadcast");
	    	if(m_connectedClients.get(0).getTheName().equalsIgnoreCase(msg.getSender())&& m_connectedClients.size() > 1){
	    		//If the original administrator leave, the new administrator will be the next one on the list, and the replacement will be broadcast
	    		ChatMessage Message = new ChatMessage("New administrator",m_connectedClients.get(1).getTheName(),"(broadcast");
	    		broadcast(Message);
	    	}
	    	broadcast(message);
	    	remove(msg.getSender());
	    }
	    else if(msg.getCommand().equalsIgnoreCase("/help")){//help list
	    	String message = null;
	    	if(msg.getParameters().equalsIgnoreCase("list")){
	    		message = "List participants in the chat";
	    	}
	    	else if(msg.getParameters().equalsIgnoreCase("leave")){
	    		message = "Leave the chat, and everyone will be informed";
	    	}
	    	else if(msg.getParameters().equalsIgnoreCase("join")){
	    		message = "When you join this chat, it will be automattically send to server, and everyone will be informed";
	    	}
	    	else if(msg.getParameters().equalsIgnoreCase("tell")){
	    		message = "Tell someone sth privatly, and everyone else will not be informed";
	    	}
	    	else if(msg.getParameters().equalsIgnoreCase("broadcast")){
	    		message = "Tell everyone sth, and everyone will be informed";
	    	}
	    	else if(msg.getParameters().equalsIgnoreCase("kick")){
	    		message = "If you are the administrator, you can kick someone";
	    	}
	    	else if(msg.getParameters().equalsIgnoreCase("")){
	    		message = "You should add some parameters(join,list,tell,broadcast,kick,leave) to see how to use that command";
	    	}
	    	else {
		    	message = "There is no such command";
		   }
	    	ChatMessage Message = new ChatMessage("help-"+msg.getParameters(),message,"(private");
	    	sendPrivateMessage(Message,msg.getSender());
	    }
	    else{//The Wrong Command
	    	ChatMessage message = new ChatMessage("Error","This is a wrong command","(private");
	    	sendPrivateMessage(message,msg.getSender());
	    }
    } 

    @SuppressWarnings("deprecation")
	public boolean remove(String name){
    	ClientConnection c;
    	//Remove it from the ArrayList
    	Iterator<ClientConnection> itr;
    	for(itr = m_connectedClients.iterator(); itr.hasNext();) {
    	    c = itr.next(); 
    	    if(c.getTheName().equals(name)){
    	    	m_connectedClients.remove(c);
    	    	c.stop();//Shutdown the thread
    	    	break; 
    	    }
    	}
    	if(itr.hasNext()) return true;
    	else return false;    	
    }
    
    public boolean addClient(String name,Socket clientSocket) throws IOException {
	ClientConnection c;
	for(Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
	    c = itr.next();
	    if(c.hasName(name)) {
		return false; // Already exists a client with this name
	    }
	}
	m_connectedClients.add(new ClientConnection(name,clientSocket,this));
	return true;
    }

    public boolean sendPrivateMessage(ChatMessage message, String name) throws IOException {
	ClientConnection c; 
	for(Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
	    c = itr.next();
	    if(c.hasName(name)) {
		c.sendMessage(message);
		return true;
	    }
	}
	return false;
    }

    public void broadcast(ChatMessage message) throws IOException {
	for(Iterator<ClientConnection> itr = m_connectedClients.iterator(); itr.hasNext();) {
	    itr.next().sendMessage(message);
	}
    }
}
