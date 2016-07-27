package chat;

import java.util.HashMap;
import java.util.TimerTask;
/**
 *
 * @author Liam Pierce
 */
public class TimerTsk extends TimerTask {
    HashMap<String,FuncStore> Method;
    String Text = "";
    @Override
    public void run(){
        for (Object K:Method.entrySet().toArray()){
            ((FuncStore) K).Run(Text);
        }
        
    }
    
    public TimerTsk(HashMap<String,FuncStore> Methods,String Line){
        this.Text = Line;
        this.Method = Methods;
    }
}