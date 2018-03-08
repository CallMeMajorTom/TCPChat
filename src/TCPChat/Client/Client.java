package TCPChat.Client;

import java.awt.event.*;
//import java.io.*;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;



public class Client implements ActionListener {

    private String m_name = null;
    private final ChatGUI m_GUI;
    private ServerConnection m_connection = null;

    public static void main(String[] args) throws IOException {
	if(args.length < 3) {
	    System.err.println("Usage: java Client serverhostname serverportnumber username");
	    System.exit(-1);
	}

	try {
	    Client instance = new Client(args[2]);
	    instance.connectToServer(args[0], Integer.parseInt(args[1]));
	} catch(NumberFormatException e) {
	    System.err.println("Error: port number must be an integer.");
	    System.exit(-1);
	}
    }

    private Client(String userName) {	
	m_name = userName;
	// Start up GUI (runs in its own thread)
	m_GUI = new ChatGUI(this, m_name);
    }

    private void connectToServer(String hostName, int port) throws IOException {
	//Create a new server connection
    m_connection = new ServerConnection(hostName, port);
	if(m_connection.handshake(m_name)) {
	    listenForServerMessages();
	}
	else {//if the name is already taken, shutdown the GUI
	    System.err.println("Unable to connect to server");
	    m_GUI.shutdown();
	}
    }

    private void listenForServerMessages() throws IOException {
	// Use the code below once m_connection.receiveChatMessage() has been implemented properly.
    // If it is a [leave] message, shut down the leave one's GUI.
    // otherwise, just display it.
	do {
		String tmp = m_connection.receiveChatMessage();
		if(tmp.contains("leave:"+m_name)||tmp.contains("Kicked:"+m_name)) m_GUI.shutdown();//if someone leave the chat, shutdown the GUI
		m_GUI.displayMessage(tmp);
	} while(true);
    }

    // Sole ActionListener method; acts as a callback from GUI when user hits enter in input field
    
    @Override
    public void actionPerformed(ActionEvent e) {
	// Since the only possible event is a carriage return in the text input field,
	// the text in the chat input field can now be sent to the server.
	try {
		//Modify the message's format and create a JSON message
		String Message = m_GUI.getInput();
		String[] split = Message.split(",");
		String message = "";
		if(split.length>=2){
				message = split[1];
			for (int i = 2;i < split.length; i++){
				message =message + "," + split[i];
			}
		}
		ChatMessage msg = new ChatMessage(split[0],message,m_name);
		m_connection.sendChatMessage(msg);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	m_GUI.clearInput();
    }
}
