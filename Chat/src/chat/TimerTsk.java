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
    private HashMap<String,FuncStore> Method;
    private BufferedReader B;
    private String Last = "";
    private Socket Connection;
    
    
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
                }
                System.out.println(Method.values().size());
                for (Object K:Method.values()){
                    System.out.println(K);
                    if (!(K == null)){
                        System.out.println("Running");
                        ((FuncStore) K).Run(Current);
                    }
                }
            }else{
                Last = Current;
            }
        }catch(Exception E){
            System.out.println(E);
            Connector.API.CloseConnection(Connection.toString().substring(1));
        }
    }
    
    public TimerTsk(HashMap<String,FuncStore> Methods,BufferedReader B,Socket Listener){
        this.Method = Methods;
        this.B = B;
        this.Connection = Listener;
    }
}