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
 * @author Camper
 */
public class Connector {
    static TCPApi API = new TCPApi();
    static Random RandCreator = new Random();
    static Crypt EnDe = new Crypt();
    
    private static HashMap<String,Server> Servers = new HashMap<>();
    private static HashMap<String,HashMap<String,Integer>> Connections = new HashMap<>();
    private static HashMap<String,String> NameMap = new HashMap<>();
    
    //Return map of all connections.
    public static HashMap<String,HashMap<String,Integer>> getConnections(){
        return Connections;
    }
    
    //Get assigned name for IP.
    public String getName(String IP){
        return NameMap.get(IP);
    }
    
    //Assign name for given IP.
    public void setName(String IP, String Name){
        NameMap.put(IP,Name);
    }
    
    //Log an IP for sending.
    public static void addConnection(String MainIP,String IP){
        Connections.get(MainIP).put(IP,1);
    }
    
    //Remove an IP for sending.
    public static void removeConnection(String IP){
        Connections.remove(IP);
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
        Servers.get(IP).enSend(IP, Text, IP);
    }
    
    //Send chat to all users logged in server.
    public static void sendAll(String CurrentIP,String Text){
        Connections.forEach((String IP,Object B) -> {
            if (!IP.equals(CurrentIP)){
                Servers.get(IP).enSend(IP,Text,IP);
            }
        });
    }
    
    public void sendConnectionset(String IP,String Text){
        Connections.get(IP).forEach((String IPc,Integer B) -> {
            Servers.get(IPc).enSend(IPc,Text,IPc);
        }
        
    }
    
    //Constructor.
    public Connector(){
        Server.IsServer = true;
        
    }
    
    public static void main(String[] args) {

        Connector C = new Connector();

        //Thread for accepting new clients constantly.
        Thread ConnectionThread = new Thread(new Runnable(){
            @Override
            public void run(){
                while (true){
                    Socket New = API.GetServerSocket(6789);
                    String IP = New.getInetAddress().toString().substring(1);
                    Servers.put(IP,new Server(New));
                }
            }
        },"Connector");
        
        //Starts connector thread.
        ConnectionThread.start();
    }
}
