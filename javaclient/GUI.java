package javaclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GUI extends JFrame implements ActionListener
{
	private JButton bLeft;
	private JButton bRight;
	private JButton bTurn;
	private Client client;
	
	public GUI(String Name, String URL, int Port)
	{
		while(client==null)
		{
			try
			{
				client=new Client(URL, Port);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(null, "Konnte nicht verbinden: "+e+
										"\n"+"URL: "+URL+"\n"
										+"Port: "+Port);
				URL = JOptionPane.showInputDialog(null, "Bitte URL eingeben:", "URL??", 0);
				Port = Integer.parseInt(JOptionPane.showInputDialog(null, "Bitte Port eingeben", "Port:", 0));
				if (URL.equals("") || Port==0)
					System.exit(0);
				client=null;
			}
		}
		client.sendString("IWANTFUN 1.0 "+Name);
		JPanel mainPanel = new JPanel();
		bLeft = new JButton("<-");
		bRight = new JButton("->");
		bTurn = new JButton("o");	
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
			new GUI("PeterParker", "localhost", 12346);
	}
}
