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
    private static boolean APIType;
    private ServerSocket Accept;
    public static boolean Log = true;

    final private HashMap<String,Socket> Connections = new HashMap<>();
    final private HashMap<String,Timer> Timers = new HashMap<>();
    final private HashMap<String,HashMap<String,FuncStore>> ListenerActions = new HashMap<>();
    
    //Logs a header.
    private void logHeader(){
        System.out.print("Log: ");
    }

    //Logs a string.
    public void log(String Text){
        if (Log){
            logHeader();
            System.out.println(Text);
        }
    }
    
    //Logs an object.
    public void log(Object Text){
        if (Log){
            logHeader();
            System.out.println(Text);
        }
    }
    
    //Logs a boolean.
    public void log(boolean Text){
        if (Log){
            logHeader();
            System.out.println(Text);
        }
    }
    
    //Logs a character.
    public void log(char Text){
        if (Log){
            logHeader();
            System.out.println(Text);
        }
    }
    
    //Private method for creating a connection. Not to be used in public APIs.
    private boolean connector(IP Host,String Name) throws IOException{
        Connections.put(Name,new Socket(Host.Converter(),Host.GetPort()));
        log(" Connection Init Successful");
        return true;
    }
    
    //Creates a connection given an IP. Will autoreconnect.
    public boolean connection(IP Host,String Name){
        try{
            connector(Host,Name);
        }catch(Exception E){
            return autoConnect(Host,Name);
        }
        return true;
    }
    
    //A method for autoreconnection.
    public boolean autoConnect(IP Host, String Name){
        int Tries = 0;
        boolean Connected = false;
        while (!Connected){
            try{
                connector(Host,Name);
                break;
            }catch(Exception E){
                Tries += 1;
                log("Reconnect failed, retrying...");
                if (Tries >= 30){
                    Client.Disconnect();
                    return false;
                }
            }
        }
        
        return true;
    }
    
    //Sends data to a given mapped connection.
    public void send(String Connector,String Send){
        Socket To = Connections.get(Connector);
        DataOutputStream Output;
        try{
            Output = new DataOutputStream(To.getOutputStream());
            Output.writeBytes("::" + Send + "\n");
            Output.flush();
        }catch(IOException E){
            log("Sending failed.");
        }
    }
    
    //Returns a socket that will
    public Socket getServerSocket(int Port){
        Socket Try = null;
        if (Accept == null){
            try{
                Accept = new ServerSocket(Port);
            }catch(IOException E){
                log("Error in serversocket: " + E);
            }
        }
        try{
            Try = Accept.accept();
        }catch(Exception E){
            log("Server failed : " + E);
        }
        return Try;
    }
    
    //A method that creates a listener on a given port.
    public void createListener(int Port, String Name, FuncStore Action){
        Socket Listener = null;
        InputStreamReader R = null;
        try{
            log("Working on listener.");
            Listener = getServerSocket(Port);
            log("ServerSocket created successfully.");
            R = new InputStreamReader(Listener.getInputStream());
        }catch(IOException E){
            log("Socket creation failed with exception: " + E);
        }
        
        if (!ListenerActions.containsKey(Name)){
            ListenerActions.put(Name, new HashMap<>());
        }
        
        ListenerActions.get(Name).put("Main_Listener",Action);
        
        BufferedReader B = new BufferedReader(R);
        
        
        Timer Reader = new Timer();
        Timers.put(Name,Reader);
        try{
            Reader.scheduleAtFixedRate(new TimerTsk(ListenerActions.get(Name),B,Listener),10,300);
        }catch(Exception E){
            log("Error reading line.");
        }
    }
    
    //Creates a listener with a given socket.
    public void createServerListener(Socket Listener, String Name, FuncStore Action){
        InputStreamReader R = null;
        try{
            R = new InputStreamReader(Listener.getInputStream());
        }catch(IOException E){
            log("Stream reader failed with exception : " + E);
        }
        
        if (!ListenerActions.containsKey(Name)){
            ListenerActions.put(Name, new HashMap<>());
        }
        
        ListenerActions.get(Name).put("Main_Listener",Action);
        
        BufferedReader B = new BufferedReader(R);

        Timer Reader = new Timer();
        Timers.put(Name,Reader);
        
        try{
            Reader.scheduleAtFixedRate(new TimerTsk(ListenerActions.get(Name),B,Listener),10,300);
        }catch(Exception E){
            log("Stream reader failed with exception : " + E);
        }
    }
    
    //Creates an action for a given listener.
    public void createListenerAction(String ListenerKey,String Name, FuncStore Action){
        HashMap<String,FuncStore> Save = ListenerActions.get(ListenerKey);
        if (Save == null){
            ListenerActions.put(ListenerKey,new HashMap<>());
        }
        ListenerActions.get(ListenerKey).put(Name,Action);
    }
    
    //Creates a void action set for a given listener.
    public void createNilActionSet(String ListenerKey,String Name){
        ListenerActions.put(ListenerKey,new HashMap<>());
    }
    
    //Removes a listener socket.
    public void removeListener(String Key){
        Timers.remove(Key);
        ListenerActions.remove(Key);
    }
    
    //Removes a listener action for a given listener.
    public void removeListenerAction(String ListenerKey,String Action){
        ListenerActions.get(ListenerKey).remove(Action);
    }
    
    //Removes and closes a running timer.
    public void closeTimer(String Name){
        Timers.get(Name).cancel();
        Timers.remove(Name);
    }
    
    //Returns API type.
    public static boolean APIType(){
        return APIType;
    }
    
    public TCPApi(boolean APIType){
        this.APIType = APIType;
    }
}
