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
            String Current = B.readLine();
            if (!Current.equals(Last)){
                for (Object K:Method.values()){
                    System.out.println(K);
                    ((FuncStore) K).Run(Current);
                }
            }
        }catch(Exception E){
            System.out.println(E);
            Connector.API.CloseConnection(Connection.getInetAddress().toString().substring(1));
        }
    }
    
    public TimerTsk(HashMap<String,FuncStore> Methods,BufferedReader B,Socket Listener){
        this.Method = Methods;
        this.B = B;
        this.Connection = Listener;
    }
}