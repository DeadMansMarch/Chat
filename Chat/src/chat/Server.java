/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.util.*;
/**
 *
 * @author Liam Pierce
 */
public class Server {
    TCPApi API = new TCPApi();
    Crypt EnDe = new Crypt();
    Random R = new Random();
    
    HashMap<String,Integer> Keys = new HashMap<>();
    
    public static void main(String[] args) {
        Server Chat = new Server();
        Chat.EnDe = new Crypt();
        
    }
    
    public boolean ProtocolC(){
        API.CreateListener(6789,"", null); //Error Line 27
        
        API.CreateListenerAction("Main","ProtocolC",
                new FuncStore("MainConnectionProtocol"){
            @Override
            void Run(String Text){
                Client.Last = Text;
                ConnectionProtocolAssist(Text);
            }
        });
        API.Send("Main","Connect?");
        
        return false;
    }
    
    private void ConnectionProtocolAssist(String K){
        Client.Last = K;
        
        switch(K){
            
            case "::Connect?\n":
                API.Send("Main","OK");
                break;
            
            case "::Encrypt?\n":
                API.Send("Main",Integer.toString(R.nextInt(256)));
                break;
            case "::EncryptStart@Connect\n":
                EnSend("Main","Connection_Started","guest");
                break;
            default:
                break; 
        }
    }
    
    public void EnSend(String Connection,String Text,String Name){
        API.Send(Connection,EnDe.Encrypt(Text, Keys.get(Name)));
    }
}