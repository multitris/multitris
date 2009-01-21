package javaclient;
import java.io.*;
import java.net.*;
import java.util.*;
public class Client implements Runnable
{
	private String url;
	private int port;
	private BufferedReader in;
	private OutputStreamWriter out;
	
	public Client(String url, int Port) throws Exception
	{
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
        	   System.out.println(first);
        	   String command = first.split(" ", 2)[0];
	           if(command.equals("CHUCK"))
	           {
	        	   sendString("NORRIS "+first.split(" ", 2)[1]);
	           }
           }
        }
       catch( Exception e )
       {
             System.err.println(e);
        }
    }
}