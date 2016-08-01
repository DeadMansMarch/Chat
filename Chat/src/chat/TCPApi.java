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
    private ServerSocket Accept;
    boolean Log = true;

    final private HashMap<String,Socket> Connections = new HashMap<>();
    final private HashMap<String,Timer> Timers = new HashMap<>();
    final private HashMap<String,HashMap<String,FuncStore>> ListenerActions = new HashMap<>();
    
    public boolean Connection(IP Host,String Name){
        
        try{
            System.out.print("Trying :");
            Connections.put(Name,new Socket(Host.Converter(),Host.GetPort()));
            if (Log == true){
                System.out.println(" Connection Init Successful");
            }
            
        }catch(Exception E){
            if (Log == true){
                System.out.print(" Connection to server: " + Host.GetIP() +
                        " failed.");
                return false;
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
            System.out.println("Sending Completed :" + Send);
        }catch(IOException E){
            if (Log == true){
                System.out.println("Message sending failed.");
            }
        }
    }
    
    public Socket GetServerSocket(int Port){
        Socket Try = null;
        if (Accept == null){
            try{
                Accept = new ServerSocket(Port);
            }catch(IOException E){
                
            }
        }
        try{
            Try = Accept.accept();
        }catch(Exception E){
            System.out.println("Server failed : " + E);
        }
        return Try;
    }
    
    public void CreateListener(int Port, String Name, FuncStore Action){
        Socket Listener = null;
        InputStreamReader R = null;
        try{
            System.out.println("Working...");
            Listener = GetServerSocket(Port);
            if (Log == true){
                System.out.println("ServerSocket created successfully.");
            }
            R = new InputStreamReader(Listener.getInputStream());
        }catch(IOException E){
            if (Log == true){
                System.out.println("Socket creation failed: " + E);
            }
        }
        
        if (!ListenerActions.containsKey(Name)){
            ListenerActions.put(Name, new HashMap<>());
        }
        
        BufferedReader B = new BufferedReader(R);
        
        
        Timer Reader = new Timer();
        Timers.put(Name,Reader);
        try{
            Reader.scheduleAtFixedRate(new TimerTsk(ListenerActions.get(Name),B,Listener),10,300);
        }catch(Exception E){
            if (Log == true){
                System.out.println("Error reading line : " + E);
            }
        }
    }
    
    public void CreateServerListener(Socket Listener, String Name, FuncStore Action){
        InputStreamReader R = null;
        try{
            R = new InputStreamReader(Listener.getInputStream());
        }catch(IOException E){
            if (Log == true){
                System.out.println("Stream reader failed : " + E);
            }
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
            if (Log == true){
                System.out.println("Error reading line : " + E);
            }
        }
    }
    
    public void CloseConnection(String Name){
        try{
            Connections.get(Name).close();
            RemoveListener(Name);
            Connector.removeConnection(Name);
        }catch(IOException E){
            System.out.println(E);
        }
    }
    
    public void CreateListenerAction(String ListenerKey,String Name, FuncStore Action){
        HashMap<String,FuncStore> Save = ListenerActions.get(ListenerKey);
        if (Save == null){
            ListenerActions.put(ListenerKey,new HashMap<>());
        }
        ListenerActions.get(ListenerKey).put(Name,Action);
        System.out.println("Done");
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
    }
}