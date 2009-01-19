package javagui;

import javax.swing.JFrame;

public class MainWindow extends JFrame 
{
	public MainWindow(GamePanel gp)
	{
		this.setContentPane(gp);
		this.setSize(1024,768);
		this.setVisible(true);
	}

}
