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
            String Current = B.readLine();
            System.out.println(B);
            if (!Current.equals(Last)){
                for (Object K:Method.values()){
                    if (!(K == null)){
                        System.out.println(Server.IsServer);
                        if (Server.IsServer){
                            String _IP = Connection.getInetAddress().toString();
                            if (!Server.CheckIP(_IP)){
                                Server.AddIPConnection(_IP);
                            }
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
        
        if (Server.IsServer == true){
            Server.CheckIP(Connection.getInetAddress().toString());
        }
    }
    
    public TimerTsk(HashMap<String,FuncStore> Methods,BufferedReader B,Socket Listener){
        this.Method = Methods;
        this.B = B;
        this.Connection = Listener;
    }
}
