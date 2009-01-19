package multitris;

import javax.swing.JFrame;

public class MainWindow extends JFrame 
{
	public MainWindow(GamePanel gp)
	{
		this.setContentPane(gp);
		this.setSize(800,600);
		this.setVisible(true);
	}

}
