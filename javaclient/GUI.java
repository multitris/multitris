package javaclient;

//import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import common.*;
 
public class GUI extends JFrame implements ActionListener, KeyListener
{
	private JButton bLeft;
	private JButton bRight;
	private JButton bTurn;
	private JButton bUp;
	private JButton bDown;
	private JButton bKeyboard;
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
		bUp.setEnabled(enable);
		bDown.setEnabled(enable);
		bKeyboard.setEnabled(enable);
	}
	public GUI(String Name, String URL, int Port)
	{
		mainPanel = new JPanel();
		bLeft = new JButton("<-");
		bRight = new JButton("->");
		bTurn = new JButton("o");
		bUp = new JButton("up");
		bDown = new JButton ("down");
		bKeyboard = new JButton("KeyboardControl");
                bKeyboard.addKeyListener(this);
		enableGUI(false);
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
		if (Name.equals(""))
			client.sendString("IWANTFUN 0.1");
		else
			client.sendString("IWANTFUN 0.1 "+Name);
		while (!loggedIn)
			delay(200);
		bRight.addActionListener(this);
		bTurn.addActionListener(this);
		bLeft.addActionListener(this);
		bUp.addActionListener(this);
		bDown.addActionListener(this);
		mainPanel.setLayout(new GridLayout(3,3));
		this.setSize(200, 100);
		mainPanel.add(new JPanel());
		mainPanel.add(bUp);
		mainPanel.add(new JPanel());
		mainPanel.add(bLeft);
		mainPanel.add(bTurn);
		mainPanel.add(bRight);
		mainPanel.add(new JPanel());
		mainPanel.add(bDown);
		mainPanel.add(bKeyboard);
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
		else if (command.equals("PLONK"))
		{
			enableGUI(false);
		}
		else if (command.equals("ATTENTION"))
		{
			//String color = parameter.split(" ",2)[0];
			loggedIn=true;
		}
		else  if(command.equals("CHUCK"))
	    {
	  	   client.sendString("NORRIS "+parameter);
	    }
		else  if(command.equals("FUCKYOU"))
        {
      	   JOptionPane.showMessageDialog(this, parameter);
      	   System.exit(0);
        }
		else if(command.equals("NOTBAD"))
		{
	      	   JOptionPane.showMessageDialog(this, "Herzlichen Glückwunsch!\n Die Spieler haben gewonnen.");
	    }
		else if(command.equals("THATWASMISERABLE"))
		{
	      	   JOptionPane.showMessageDialog(this, "Tja,... so ein Pech\n Der Computer hat gewonnen");
	    }
	}
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource()==bUp)
			client.sendString("MARIHUANA");
		else if (e.getSource()==bDown)
			client.sendString("MOELLEMANN");
		else if (e.getSource()==bLeft)
			client.sendString("LAFONTAINE");
		else if (e.getSource()==bRight)
			client.sendString("STOIBER");
		else if (e.getSource()==bTurn)
			client.sendString("TURN");
	}
	public static void main(String[] args)
	{
		if (args.length==3)
			new GUI(args[0], args[1], Integer.parseInt(args[2]));
		else
			new GUI("", "", 0);
	}
	public void keyPressed(KeyEvent e) 
	{
		if (e.getKeyCode()==java.awt.event.KeyEvent.VK_LEFT)
			actionPerformed(new ActionEvent(bLeft, 0, "lala"));
		else if (e.getKeyCode()==java.awt.event.KeyEvent.VK_RIGHT)
			actionPerformed(new ActionEvent(bRight, 0, "lala"));
		else if (e.getKeyCode()==java.awt.event.KeyEvent.VK_SPACE)
			actionPerformed(new ActionEvent(bTurn, 0, "lala"));
		else if (e.getKeyCode()==java.awt.event.KeyEvent.VK_UP)
			actionPerformed(new ActionEvent(bUp, 0, "lala"));
		else if (e.getKeyCode()==java.awt.event.KeyEvent.VK_DOWN)
			actionPerformed(new ActionEvent(bDown, 0, "lala"));
	}
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
