import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import ChatServer.ClientThread;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.

    private int type = 0;
    private String message = "";
    private String recipient = "";
    
    ChatMessage (int type, String message, String recipient){
        this.type = type;
        // 0 = general message
        // 1 = logout message
        // The logout  message will let the server know who logged out.
        this.message = message;
    }
    /*
    public void directMessage (String message, String username){
        String pattern = "HH:mm:ss";
        SimpleDateFormat timeFormat = new SimpleDateFormat(pattern);
        String currTime = timeFormat.format(new Date());
        ((ArrayList<ClientThread>)clients).trimToSize();
        for (int i = 0; i < clients.size(); i++) {
        	if (clients.get(i) == username){
                clients.get(i).writeMessage(currTime + " Server Broadcast: " + message + "\n");
        	}
        }
        System.out.println(currTime + " Server Broadcast: " + message);
    }
	*/
    public int getType(){
        return type;
    }

    public String getMessage(){
        return message;
    }
    
    public String getRecipient(){
    	return recipient;
    }





}
