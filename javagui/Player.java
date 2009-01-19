package javagui;
import java.awt.Color;
public class Player 
{
	public Color color;
	public int points;
	public String name;
	public int ID;
	public Player(int ID)
	{
		this.ID=ID;
		this.name="Player "+ID;
		this.points=0;
		this.color=new Color(0,0,0);
	}
	public Player(int ID, String Name)
	{
		this(ID);
		this.name=Name;
	}
}
