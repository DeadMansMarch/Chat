package chat;

import java.io.BufferedReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.TimerTask;
/**
 *
 * @author Liam Pierce
 */
public class TimerTsk extends TimerTask {
    HashMap<String,FuncStore> Method;
    BufferedReader B;
    String Last = "";
    Socket Connection;
    @Override
    public void run(){
        try{
            System.out.println("Try 1");
            String Current = B.readLine();
            System.out.println("Text: " + Current);
            if (!Current.equals(Last)){
                String _IP = Connection.getInetAddress().toString().substring(1);
                if (Server.IsServer){
                    System.out.println(_IP);
                    Server.CheckIP(_IP);
                }
                System.out.println(Method.values().size());
                for (Object K:Method.values()){
                    if (!(K == null)){
                        System.out.println(Connection);
                        if (Server.IsServer){
                            ((FuncStore) K).Run(Current,_IP);
                        }else{
                            ((FuncStore) K).Run(Current);
                        }
                    }
                }
            }else{
                Last = Current;
            }
        }catch(Exception E){
            System.out.println(E);
        }
    }
    
    public TimerTsk(HashMap<String,FuncStore> Methods,BufferedReader B,Socket Listener){
        this.Method = Methods;
        System.out.println(Methods.values().size());
        this.B = B;
        this.Connection = Listener;
    }
}