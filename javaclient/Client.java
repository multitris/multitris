package javaclient;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;
public class Client implements Runnable
{
	private String url;
	private int port;
	private BufferedReader in;
	private OutputStreamWriter out;
	private GUI GUI;
	
	public Client(String url, int Port, GUI gui) throws Exception
	{
		this.GUI=gui;
		this.url=url;
		this.port=Port;
    	Socket cs = new Socket(url, port);
        in = new BufferedReader(
                   new InputStreamReader(
                      cs.getInputStream() ) );
        out = new OutputStreamWriter(cs.getOutputStream());
        new Thread(this).start();
	}
	public void sendString(String s)
	{
		try
		{
			out.write(s+"\n");
			out.flush();
		}
		catch (Exception e)
		{
			System.out.println("Error in sendString:"+e);
		}
	}
	public void run() 
	{  
       try 
       {
           String first;
           while((first=in.readLine())!=null)
           {
        	   String[] x = first.split(" ", 2);
        	   if (x.length==1)
        		   GUI.networkDataArrived(x[0], "");
        	   else
        		   GUI.networkDataArrived(x[0], x[1]);
           }
        }
       catch( Exception e )
       {
             System.err.println(e);
        }
    }
}