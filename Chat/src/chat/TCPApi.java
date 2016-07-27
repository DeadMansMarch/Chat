/** An API giving a full range of TCP interaction,
specifically for text and images.
*
*/
package chat;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * @author Liam Pierce
 */
public class TCPApi {
    boolean Log = true;

    final private HashMap<String,Socket> Connections = new HashMap<>();
    final private HashMap<String,BufferedReader> Readers = new HashMap<>();
    final private HashMap<String,Timer> Timers = new HashMap<>();
    final private HashMap<String,HashMap<String,FuncStore>> ListenerActions = new HashMap<>();
    
    
    public void Connection(IP Host,String Name){
        try{
            Connections.put(Name,new Socket(Host.Converter(),Host.GetPort()));
            if (Log == true){
                System.out.println("Connection Init Successful");
            }
            
        }catch(Exception E){
            if (Log == true){
                System.out.println("Connection to server: " + Host.GetIP() +
                        " failed.");
            }
        }
    }
    
    public void Send(String Connector,String Send){
        Socket To = Connections.get(Connector);
        DataOutputStream Output;
        try{
            Output = new DataOutputStream(To.getOutputStream());
            Output.writeBytes("::" + Send + "\n");
        }catch(IOException E){
            if (Log == true){
                System.out.println("Message sending failed.");
            }
        }
    }
    
    public void CreateListener(int Port, String Name, FuncStore Action){
        Socket Listener;
        InputStreamReader R = null;
        try{
            Listener = new ServerSocket(Port).accept();
            if (Log == true){
                System.out.println("ServerSocket created successfully.");
            }
            R = new InputStreamReader(Listener.getInputStream());
        }catch(IOException E){
            if (Log == true){
                System.out.println("Socket creation failed: " + E);
            }
        }
        
        BufferedReader B = new BufferedReader(R);
        Readers.put(Name,B);
        
        if (!Action.equals(null)){
            CreateListenerAction(Name,"Main_Listener",Action);
        }
        Timer Reader = new Timer();
        Timers.put(Name,Reader);
        try{
            Reader.scheduleAtFixedRate(new TimerTsk(ListenerActions.get(Name),B.readLine()),10,300);
        }catch(IOException E){
            if (Log == true){
                System.out.println("Error reading line.");
            }
        }
    }
    
    public void CreateListenerAction(String ListenerKey,String Name, FuncStore Action){
        ListenerActions.get(ListenerKey).put(Name,Action);
    }
    
    public void RemoveListener(String Key){
        Timers.remove(Key);
        Readers.remove(Key);
    }
    
    public void RemoveListenerAction(String Listener,String Action){
        ListenerActions.get(Listener).remove(Action);
    }
    
    public TCPApi(){
    }
}