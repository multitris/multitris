package javaclient;
import java.io.*;
import java.net.*;
public class Client implements Runnable
{
	private BufferedReader in;
	private OutputStreamWriter out;
	private GUI GUI;
	
	public Client(String url, int Port, GUI gui) throws Exception
	{
		this.GUI=gui;
    	Socket cs = new Socket(url, Port);
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
        	   if (x.length<=1)
        		   GUI.networkDataArrived(x[0], "");
        	   else
        		   GUI.networkDataArrived(x[0], x[1]);
           }
        }
       catch( IOException e )
       {
             System.err.println("Error in Client.run():"+e);
        }
    }
}