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
    public void Log(String Text){
        if (Log){
            logHeader();
            System.out.println(Text);
        }
    }
    
    //Logs an object.
    public void Log(Object Text){
        if (Log){
            logHeader();
            System.out.println(Text);
        }
    }
    
    //Logs a boolean.
    public void Log(boolean Text){
        if (Log){
            logHeader();
            System.out.println(Text);
        }
    }
    
    //Logs a character.
    public void Log(char Text){
        if (Log){
            logHeader();
            System.out.println(Text);
        }
    }
    
    private boolean Connector(IP Host,String Name) throws IOException{

            Connections.put(Name,new Socket(Host.Converter(),Host.GetPort()));
            Log(" Connection Init Successful");
            
        
        return true;
    }
    
    public boolean Connection(IP Host,String Name){
        try{
            Connector(Host,Name);
        }catch(Exception E){
            if (APIType){
                if (!AutoConnect(Host,Name)){
                    Client.Disconnect();
                }
            }else{
                AutoConnect(Host,Name);
            }
        }
        return true;
    }
    
    public boolean AutoConnect(IP Host, String Name){
        int Tries = 0;
        boolean Connected = false;
        while (!Connected){
            try{
                Connector(Host,Name);
                break;
            }catch(Exception E){
                Tries += 1;
                Log("Reconnect failed, retrying...");
                if (Tries >= 30){
                    Client.Disconnect();
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public void Send(String Connector,String Send){
        Socket To = Connections.get(Connector);
        DataOutputStream Output;
        try{
            Output = new DataOutputStream(To.getOutputStream());
            Output.writeBytes("::" + Send + "\n");
            Output.flush();
        }catch(IOException E){
            Log("Sending failed.");
        }
    }
    
    public Socket GetServerSocket(int Port){
        Socket Try = null;
        if (Accept == null){
            try{
                Accept = new ServerSocket(Port);
            }catch(IOException E){
                Log("Error in serversocket: " + E);
            }
        }
        try{
            Try = Accept.accept();
        }catch(Exception E){
            Log("Server failed : " + E);
        }
        return Try;
    }
    
    public void CreateListener(int Port, String Name, FuncStore Action){
        Socket Listener = null;
        InputStreamReader R = null;
        try{
            Log("Working on listener.");
            Listener = GetServerSocket(Port);
            Log("ServerSocket created successfully.");
            R = new InputStreamReader(Listener.getInputStream());
        }catch(IOException E){
            Log("Socket creation failed with exception: " + E);
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
            Log("Error reading line.");
        }
    }
    
    public void CreateServerListener(Socket Listener, String Name, FuncStore Action){
        InputStreamReader R = null;
        try{
            R = new InputStreamReader(Listener.getInputStream());
        }catch(IOException E){
            Log("Stream reader failed with exception : " + E);
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
            Log("Stream reader failed with exception : " + E);
        }
    }
    
    public void CloseConnection(String Name){
        try{
            Timers.get(Name).cancel();
            Connections.get(Name).close();
            Connections.remove(Name);
            RemoveListener(Name);
            Server.sendAll(Name, "Rem:" + Server.getName(Name));
            Server.removeServer(Name);
        }catch(IOException E){
            
        }
    }
    
    public void CreateListenerAction(String ListenerKey,String Name, FuncStore Action){
        HashMap<String,FuncStore> Save = ListenerActions.get(ListenerKey);
        if (Save == null){
            ListenerActions.put(ListenerKey,new HashMap<>());
        }
        ListenerActions.get(ListenerKey).put(Name,Action);
    }
    
    public void CreateNilActionSet(String ListenerKey,String Name){
        ListenerActions.put(ListenerKey,new HashMap<>());
    }
    
    public void RemoveListener(String Key){
        Timers.remove(Key);
        ListenerActions.remove(Key);
    }
    
    public void RemoveListenerAction(String ListenerKey,String Action){
        ListenerActions.get(ListenerKey).remove(Action);
    }
    
    public TCPApi(){
        if (Server.IsServer){
            APIType = true;
            Log("Creating server API.");
        }else{
            Log("Creating client API.");
        }
    }
}