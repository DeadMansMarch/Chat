package chat;

import java.util.List;
import java.util.TimerTask;
/**
 *
 * @author Liam Pierce
 */
public class TimerTsk extends TimerTask {
    List<FuncStore> Method;
    String Text = "";
    @Override
    public void run(){
        for (FuncStore K:Method){
            K.Run(Text);
        }
        
    }
    
    public TimerTsk(List<FuncStore> Methods,String Line){
        this.Text = Line;
        this.Method = Methods;
    }
}
