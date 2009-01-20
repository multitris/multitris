package javagui;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client 
{
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
        	   System.out.println("Got:"+first);
        	   
        	   String command = first.split(" ", 3)[0];
	           if(command.equals("POINTS"))
	           {
	        	   int n=Integer.parseInt(first.split(" ", 3)[1]);
	        	   int p=Integer.parseInt(first.split(" ", 3)[2]);
	        	   gp.POINTS(n,p);
	           }
        	   if(command.equals("PLAYER"))
	           {
	        	   int n=Integer.parseInt(first.split(" ", 3)[1]);
	        	   String p = first.split(" ", 3)[2];
	        	   gp.PLAYER(n, p);
	           }
        	   if(command.equals("AUTH"))
	        	   gp.AUTH(first.split(" ", 2)[1]);
	           if(command.equals("SET"))
	           {
	        	   String[] s = first.split(" ", 4);
	        	   gp.SET(Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]));
	           }
	           if(command.equals("FLUSH"))
	        	   gp.FLUSH();
	           if(command.equals("COLOR"))
	           {
	        	   int player = Integer.parseInt(first.split(" ", 3)[1]);
	        	   if (player==0)
	        		   continue;
	        	   String color = first.split(" ", 3)[2];
	        	   gp.COLOR(player, color);
	           }
        	   if(command.equals("MESSAGE"))
	           {
	        	   gp.MESSAGE(first.split(" ", 2)[1]);
	           }
        	   if(command.equals("SIZE"))
	           {
	        	   int w = Integer.parseInt(first.split(" ", 3)[1]);
	        	   int h = Integer.parseInt(first.split(" ", 3)[2]);
	        	   gp.SIZE(w, h);
	        	   continue;
	           }
	           if(command.equals("RESET"))
	           {
	        	   gp.RESET(
	        			   first.contains("FIELD"),
	        			   first.contains("PLAYERS"),
	        			   first.contains("COLORS"),
	        			   first.contains("POINTS"),
	        			   first.contains("MESSAGES")
	        			   );
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
                System.out.println("Server closed connection.");
                System.exit(0);
        }
        catch( Exception e ){
             System.err.println( e.toString() );
        }
    }
}