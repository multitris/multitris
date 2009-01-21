package javaclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import common.*;
 
public class GUI extends JFrame implements ActionListener
{
	private JButton bLeft;
	private JButton bRight;
	private JButton bTurn;
	private Client client;
	
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
				client=new Client(URL, Port);
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
			new GUI("", "", 0);
	}
}
