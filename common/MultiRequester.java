package common;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;
/**
 * This class opens a window and asks for Strings given in the Constructor.
 * the Method getResult() blocks until the user has entered everything and pressed the OK Button
 * if the User closes the Window, it will return null.
 *
 */
public class MultiRequester extends JFrame implements ActionListener, WindowListener
{
	private JTextField[] txtField;
	private JButton OK;
	private String[] result;
	private boolean ready=false;
	
	private static void setConstraints(GridBagConstraints c, int x,int y,int w,int h,int wx,int wy)
	{
		c.gridx=x;
		c.gridy=y;
		c.gridwidth=w;
		c.gridheight=h;
		c.weightx=wx;
		c.weighty=wy;
	}
	public MultiRequester(String[] Questions, String[] Standard)
	{
		this(Questions, Standard, "MultiRequester");
	}
	public MultiRequester(String[] Questions, String[] Standard, String Title)
	{
		this(Questions, Title);
		for (int i=0;i<Math.min(txtField.length, Standard.length);i++)
			txtField[i].setText(Standard[i]);
	}
	public MultiRequester(String[] Questions, String Title)
	{
		super(Title);
		addWindowListener(this);
		GridBagLayout gbla = new GridBagLayout();
		this.setLayout(gbla);
		GridBagConstraints constraints = new GridBagConstraints();
		txtField=new JTextField[Questions.length];
		int i=0;
		for (i=0;i<Questions.length;i++)
		{
			setConstraints(constraints, 0, i, 1, 1, 50, 100);
			constraints.anchor=GridBagConstraints.WEST;
			constraints.fill=GridBagConstraints.NONE;
			JLabel tmp=new JLabel(Questions[i]);
			gbla.setConstraints(tmp, constraints);
			this.add(tmp);
			
			setConstraints(constraints, 1, i, 1, 1, 50, 100);
			constraints.anchor=GridBagConstraints.CENTER;
			constraints.fill=GridBagConstraints.HORIZONTAL;
			txtField[i]=new JTextField();
			gbla.setConstraints(txtField[i], constraints);
			this.add(txtField[i]);
		}
		i++;
		setConstraints(constraints, 1, i, 1, 1, 50, 100);
		constraints.anchor=GridBagConstraints.WEST;
		constraints.fill=GridBagConstraints.NONE;
		OK=new JButton("OK");
		gbla.setConstraints(OK, constraints);
		OK.addActionListener(this);
		this.add(OK);
		this.setMinimumSize(new Dimension(200, 30+30*Questions.length));
		this.doLayout();
		this.setVisible(true);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==OK)
		{
			result = new String[txtField.length];
			for(int i=0;i<txtField.length;i++)
				result[i]=txtField[i].getText();
			this.dispose();
			ready=true;
		}
	}
	public String[] getResult()
	{
		while(!ready)
		{
			try
			{
				Thread.sleep(500);
			}
			catch(Exception e)
			{}
		}
		return result;
	}
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public MultiRequester(String[] Questions)
	{
		this(Questions, "MultiRequester");
	}
	public void windowClosing(WindowEvent e)
	{
		ready=true;
	}
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
