package javagui;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server 
{
	private int PORT=6010;
	private boolean keepRunning=true;
	private GamePanel gp;
	
	public Server(GamePanel gp)
	{
		this.gp=gp;
	}
	public void processRequest(BufferedReader in) 
	{  
       try 
       {
           String first = in.readLine();
           if(first.substring(0, 3).equals("SIZE"))
           {
        	   int w = Integer.parseInt(first.split(" ", 3)[1]);
        	   int h = Integer.parseInt(first.split(" ", 3)[2]);
        	   gp.SIZE(w, h);
           }
        }
       catch( Exception e )
       {
             System.err.println(e);
        }
    }
    public void loop(){
        try
        {
            ServerSocket ss = new ServerSocket(PORT);
            while(keepRunning)
            {
                Socket sock = ss.accept();
                BufferedReader in
                   = new BufferedReader(
                       new InputStreamReader(
                          sock.getInputStream() ) );
                processRequest(in);
                sock.close();
            }
        }
        catch( Exception e ){
             System.err.println( e.toString() );
        }
    }
}