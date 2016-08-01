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
    static HashMap<String,Server> Servers = new HashMap<>();
    
    static HashMap<String,Integer> IpToConnection = new HashMap<>();
    
    
    private static HashMap<String,HashMap<String,Integer>> Connections = new HashMap<>();
    
    public static HashMap<String,HashMap<String,Integer>> getConnections(){
        return Connections;
    }
    
    public static void addConnection(String MainIP,String IP){
        Connections.get(MainIP).put(IP,1);
    }
    
    public static void removeConnection(String IP){
        Connections.remove(IP);
    }
    
    public static void CreateDefaultConnectionset(String MainIP){
        Connections.put(MainIP,new HashMap<>());
        Connections.forEach((String I,Object B) -> {
            if (!I.equals(MainIP)){
                addConnection(MainIP,I);
            }
        });
    }
    
    public static void SendAt(String IP,String Text){
        Servers.get(IP).EnSend(IP, Text, IP);
    }
    
    public static void SendAll(String CurrentIP,String Text){
        Connections.forEach((String IP,Object B) -> {
            if (!IP.equals(CurrentIP)){
                Servers.get(IP).EnSend(IP,Text,IP);
            }
        });
    }
    
    public Connector(){
        Server.IsServer = true;
        
    }
    
    public static void main(String[] args) {
        System.out.println("Initiating...");
        Connector C = new Connector();
        
        System.out.println("Initiated server...");
        
        Thread ConnectionThread = new Thread(new Runnable(){
            @Override
            public void run(){
                while (true){
                    System.out.println("Waiting for connection...");
                    Socket New = API.GetServerSocket(6789);
                    String IP = New.getInetAddress().toString().substring(1);
                    Servers.put(IP,new Server(New));
                }
            }
        },"Connector");
        
        ConnectionThread.start();
    }
}