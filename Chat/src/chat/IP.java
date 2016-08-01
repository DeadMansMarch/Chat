// A class describing an IP.
package chat;
import java.net.*;
/**
 *
 * @author Liam Pierce
 */
public class IP {
    private String IP = "";
    private int port = 0;
    
    public IP(String H,int Port){
        this.IP = H;
        this.port = Port;
    }
    
    public int GetPort(){
        return port;
    }
    
    public String GetIP(){
        return IP;
    }
    
    public InetAddress Converter(){
        try{
            return InetAddress.getByName(IP);
        }catch(UnknownHostException E){
            System.out.println(E);
        }
        System.exit(1);
        return null;
    }
    //This is some trash I swear.
    public String tostring(){
        return IP;
    }
}