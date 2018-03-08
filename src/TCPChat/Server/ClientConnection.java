/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCPChat.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;


/**
 * 
 * @author brom
 */
public class ClientConnection extends Thread{
	
	
	private String  m_name ;
	private Socket m_ClientSocket;
	private Server m_server;
	

	public ClientConnection(String name,Socket ClientSocket,Server server) throws IOException {
		m_name = name;
		m_ClientSocket = ClientSocket;
		m_server = server;
		this.start();//When establish the connection, start the thread and call the run() method
	}
	
	public void run(){
			String received = "";
			DataInputStream in;
			while(true){
				try {
					m_ClientSocket.setSoTimeout(100000);
					in = new DataInputStream(m_ClientSocket.getInputStream());		
					try {
						received = in.readUTF();
					} catch(SocketException e) {
						e.printStackTrace();
						m_server.remove(m_name);//remove the connection from the m_connectedClients list
					}
					ChatMessage msg = new ChatMessage(received);
					m_server.operate(msg);//Server will operate the received message
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
	}
	

	public void sendMessage(ChatMessage message) throws IOException {
		String Message = message.toString();
      	DataOutputStream out = new DataOutputStream(m_ClientSocket.getOutputStream());
		// TODO: 
		// * marshal message if necessary
		// * send a chat message to the server
		out.writeUTF(Message);
	}
 
	public boolean hasName(String testName) {
		return testName.equals(m_name);
	}
	

	public String getTheName() {
		return m_name;
	}
}
