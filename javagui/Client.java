package javagui;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client 
{
	private boolean keepRunning=true;
	private GamePanel gp;
	private String url;
	private int port;
	
	public Client(GamePanel gp, String url, int Port)
	{
		this.gp=gp;
		this.url=url;
		this.port=Port;
	}
	public void processRequest(BufferedReader in) 
	{  
       try 
       {
           String first;
           while((first=in.readLine())!=null)
           {
        	   String command = first.split(" ", 3)[0];
	           if(command.equals("SIZE"))
	           {
	        	   int w = Integer.parseInt(first.split(" ", 3)[1]);
	        	   int h = Integer.parseInt(first.split(" ", 3)[2]);
	        	   gp.SIZE(w, h);
	        	   continue;
	           }
	           if(command.equals("RESET"))
	           {
	        	   //to be continued.... 
	           }
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
        	Socket cs = new Socket(url, port);
            BufferedReader in
                   = new BufferedReader(
                       new InputStreamReader(
                          cs.getInputStream() ) );
                processRequest(in);
                cs.close();
        }
        catch( Exception e ){
             System.err.println( e.toString() );
        }
    }
}