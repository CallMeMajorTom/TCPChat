package TCPChat.Client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

public class ChatMessage{
		
	private JSONObject obj = new JSONObject();
		
	public ChatMessage(String command, String parameters, String sender){
		obj.put("command", command);
		obj.put("parameters", parameters);
		obj.put("sender", sender);
		obj.put("timestamp", System.currentTimeMillis());
	}
	
	public ChatMessage(String received){//To create a ChatMessage with a String, using Regular Expression
		String C = null,P = null,S = null;
		String command="command:([\\s\\S]*?),parameters:";
	    Matcher matcher_c=Pattern.compile(command).matcher(received);  
	    if(matcher_c.find())  {
	    	C=matcher_c.group(1);
	    	System.out.println(C);
	    }
		String parameters="parameters:([\\s\\S]*?),sender:"; 
	    Matcher matcher_p=Pattern.compile(parameters).matcher(received);  
	    if(matcher_p.find())  {
	    	P=matcher_p.group(1);
	    	System.out.println(P);
	    }
		String sender="sender:([\\s\\S]*?)\n"; 
	    Matcher matcher_s=Pattern.compile(sender).matcher(received);  
	    if(matcher_s.find())  {
	    	S=matcher_s.group(1);
	    	System.out.println(S);
	    }
		obj.put("command", C);
		obj.put("parameters", P);
		obj.put("sender", S);
		obj.put("timestamp", System.currentTimeMillis());
	}
	
	public String toString(){//overwrite the toString method
		return "command:" + getCommand() + ",parameters:" + getParameters() + ",sender:" + getSender() + "\n";
	}
	
	public String getCommand(){
		return (String)obj.get("command");	
	}
	
	public String getParameters(){
		return (String)obj.get("parameters");	
	}
	
	public String getSender(){
		return (String)obj.get("sender");	
	}
	
	public String getTimeStamp(){
		return obj.get("timestamp").toString();
	}
}
