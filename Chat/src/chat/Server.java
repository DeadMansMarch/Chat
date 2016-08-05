/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.util.*;
import java.net.Socket;

/**
 *
 * @author Liam Pierce  
 */
public class Server {
    public static boolean IsServer = false;
    public static TCPApi API;
    public static Random RandCreator = new Random();
    public static Crypt EnDe = new Crypt();
    private static HashMap<String,Connector> Servers = new HashMap<>();
    private static HashMap<String,HashMap<String,Integer>> Connections = new HashMap<>();
    private static HashMap<String,String> NameMap = new HashMap<>();
    private static String Password = "";
    
    //Closes an active connection.
    public static void closeConnection(String Name){
        try{
            API.closeTimer(Name);
            API.removeListener(Name);
            removeConnection(Name);
            removeServer(Name);
        }finally{
            
        }
    }
    
    //Compares a string with the password of the server.
    public static boolean compareStrings(String A){
        return A.equals(Password);
    }
    
    //Sets the server's password. This is for the server UI.
    public static void setPassword(String Password){
        Server.Password = Password;
    }
    
    //Return map of all connections.
    public static HashMap<String,HashMap<String,Integer>> getConnections(){
        return Connections;
    }
    
    //Get assigned name for IP.
    public static String getName(String IP,boolean ReturnIP){
        
        if (NameMap.containsKey(IP)){
            return NameMap.get(IP);
        }
        if (ReturnIP){
            return IP;
        }
        return "";
    }
    
    //Returns mapped name for IP.
    //This is a basic reroute function for a missing argument.
    public static String getName(String IP){
        return getName(IP,false);
    }
    
    //Gets the IP for a connection given a name.
    public static String getIP(String Name){
        String IP = "";
        for (Map.Entry<String, String> NameSet : NameMap.entrySet()) {
             if (NameSet.getValue().equals(Name)){
                 IP = NameSet.getKey();
                 break;
             }
        }
        return IP;
    }
    
    //Assign name for given IP.
    public static void setName(String IP, String Name){
        NameMap.put(IP,Name);
    }
    
    //Changes connectionset to have only one connection.
    public static void oneConnection(String MainIP,String IP){
        Connections.get(MainIP).clear();
        Connections.get(MainIP).put(IP,1);
    }
    
    //Log an IP for sending.
    public static void addConnection(String MainIP,String IP){
        Connections.get(MainIP).put(IP,1);
    }
    
    //Remove an IP for sending.
    public static void removeConnection(String IP){
        Connections.remove(IP);
    }
    
    //Removes a client's connection object.
    public static void removeServer(String IP){
        Servers.get(IP).Close();
        Servers.remove(IP);
        removeConnection(IP);
    }
    
    //Assign the map for sender as all logged IPs.
    //TODO: Allow for new users to recieve chats by adding new users to default.
    public static void createDefaultConnectionset(String MainIP){
        Connections.put(MainIP,new HashMap<>());
        Connections.forEach((String I,Object B) -> {
            if (!I.equals(MainIP)){
                addConnection(MainIP,I);
            }
        });
    }
    
    //Send chat to specific user.
    public static void sendAt(String IP,String Text){
        Servers.get(IP).enSend(IP, Text);
    }
    
    //Send chat to all users logged in server.
    public static void sendAll(String CurrentIP,String Text){
        Connections.forEach((String IP,Object B) -> {
            if (!IP.equals(CurrentIP)){
                Servers.get(IP).enSend(IP,Text);
            }
        });
    }
    
    //Sends a message or command to all clients.
    public static void broadcast(String Text){
        Connections.forEach((String IP,Object B) -> {
            System.out.println(IP);
            Servers.get(IP).enSend(IP,Text);
        });
    }
    
    //Sends a message to every client in a mapped connection set.
    //Connection sets are the given IPS a client sends to, meaning the IP given will
    //relate to the connections of the client that called this command.
    public static void sendConnectionset(String IP,String Text){
        Connections.get(IP).forEach((String IPc,Integer B) -> {
            Servers.get(IPc).enSend(IPc,Text);
        });
    }
    
    //Appends a new connection to every non singular connection set.
    public static void updateConnectionsets(String IP){
        Connections.forEach((String IPc,Object B) ->{
            if (!IPc.equals(IP)){
                addConnection(IPc,IP);
            }
        });
    }
    
    //Updates every client's connection set.
    public static void updateAll(){
        Servers.forEach((String K,Connector Upd) -> {
            Upd.updateConnections(Connections);
        });
    }
    
    //Returns all IPs connected to the server.
    public String[] IPConnections(){
        return (String[]) Connections.keySet().toArray();
    }
    
    //Constructor.
    public Server(){
        
    }
    
    //Main method.
    
    public static void main(String[] args) {
        Server.IsServer = true;
        Server.API = new TCPApi(true);
        Server C = new Server();

        //Thread for accepting new clients constantly.
        Thread ConnectionThread = new Thread(new Runnable(){
            @Override
            public void run(){
                while (true){
                    API.log("Waiting for new connection :");
                    Socket New = API.getServerSocket(6789);
                    String IP = New.getInetAddress().toString().substring(1);
                    API.log("Connection inititated for IP: " + IP);
                    Servers.put(IP,new Connector(New));
                }
            }
        },"Connector");
        
        //Starts connector thread.
        ConnectionThread.start();
        
        Thread ServerSender = new Thread(new Runnable(){
            @Override
            public void run(){
                while (true){
                    Scanner S = new Scanner(System.in);

                    broadcast("|||RealServerPlsStandUP:" + S.nextLine());
                }
            }
        });
        
        ServerSender.start();
        
    }

}