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
                    System.out.println(IP);
                    Servers.put(IP,new Server(New));
                }
            }
        },"Connector");
        
        ConnectionThread.start();
    }
}
