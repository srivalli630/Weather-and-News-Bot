import org.jibble.pircbot.*;
import java.util.*;

public class MyBotMain {
    
    public static void main(String[] args) throws Exception {
    	
    	//Variables
    	String channelName = "#usa";
        
        // Now start our bot up.
        MyBot bot = new MyBot();
        
        // Enable debugging output.
        bot.setVerbose(true);
        
        // Connect to the IRC server.
        bot.connect("irc.freenode.net");

        // Join the #pircbot channel.
        bot.joinChannel(channelName);
        
        

        
    }
    
}