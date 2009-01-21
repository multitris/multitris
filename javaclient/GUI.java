package javaclient;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import common.*;
 
public class GUI extends JFrame implements ActionListener
{
	private JButton bLeft;
	private JButton bRight;
	private JButton bTurn;
	private JPanel	mainPanel;
	private Client client;
	private boolean loggedIn;
	private void delay(int ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch (Exception e)
		{}
	}
	private void enableGUI(boolean enable)
	{
		bLeft.setEnabled(enable);
		bRight.setEnabled(enable);
		bTurn.setEnabled(enable);
	}
	private Color convertColor(String hexcolor)
	{
		try
		{
			int r = Integer.parseInt(hexcolor.substring(0, 2), 16);
			int g = Integer.parseInt(hexcolor.substring(2, 4), 16);
			int b = Integer.parseInt(hexcolor.substring(4, 6), 16);
			return new Color(r,g,b);
		}
		catch (Exception e)
		{
			System.err.println("Der idiot kann keine farben... wir machen schwarz.,");
			return new Color(0,0,0);
		}
	}
	public GUI(String Name, String URL, int Port)
	{
		if (Name.equals("") || URL.equals("") || Port==0)
		{
			MultiRequester m = new MultiRequester(new String[]{"Name", "URL", "Port"}, 
					new String[]{"Player 1", "localhost", "12346"}, 
					"Bitte Namen, Port und URL eingeben.");
			String[] r = m.getResult();
			if (r==null)
				System.exit(0);
			Name = r[0];
			URL = r[1];
			Port = Integer.parseInt(r[2]);
		}
		while(client==null)
		{
			try
			{
				client=new Client(URL, Port, this);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(null, "Konnte nicht verbinden: "+e+
										"\n"+"URL: "+URL+"\n"
										+"Port: "+Port);
				MultiRequester m = new MultiRequester(new String[]{"URL", "Port"}, new String[]{URL, ""+Port}, "Bitte Port und URL eingeben.");
				String[] r = m.getResult();
				if (r==null)
					System.exit(0);
				URL = r[0];
				Port = Integer.parseInt(r[1]);
				client=null;
			}
		}
		client.sendString("IWANTFUN 1.0 "+Name);
		while (!loggedIn)
			delay(200);
		
		mainPanel = new JPanel();
		bLeft = new JButton("<-");
		bRight = new JButton("->");
		bTurn = new JButton("o");
		enableGUI(false);
		bRight.addActionListener(this);
		bTurn.addActionListener(this);
		bLeft.addActionListener(this);
		this.setSize(200, 100);
		mainPanel.add(bLeft);
		mainPanel.add(bTurn);
		mainPanel.add(bRight);
		mainPanel.doLayout();
		this.setContentPane(mainPanel);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	public void networkDataArrived(String command, String parameter)
	{
		if (command.equals("GOFORREST"))
		{
			enableGUI(true);
		}
		if (command.equals("PLONK"))
		{
			enableGUI(false);
		}
		if (command.equals("ATTENTION"))
		{
			//String color = parameter.split(" ",2)[0];
			loggedIn=true;
		}
         if(command.equals("CHUCK"))
         {
      	   client.sendString("NORRIS "+parameter);
         }
         if(command.equals("FUCKYOU"))
         {
      	   JOptionPane.showMessageDialog(this, parameter);
      	   System.exit(0);
         }
	}
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource()==bLeft)
			client.sendString("LAFONTAINE");
		if (e.getSource()==bRight)
			client.sendString("STOIBER");
		if (e.getSource()==bTurn)
			client.sendString("TURN");
	}
	public static void main(String[] args)
	{
		if (args.length==3)
			new GUI(args[0], args[1], Integer.parseInt(args[2]));
		else
			new GUI("", "", 0);
	}
}
