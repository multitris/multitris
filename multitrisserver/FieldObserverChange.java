package multitrisserver;

class FieldObserverChange // represents the change of a pixel that will be sent to the gui
{
	private int x;
	private int y;
	private int col;
	private boolean exploding;

	public FieldObserverChange(int y, int x, int col, boolean exploding)
	{
		this.x = x;
		this.y = y;
		this.col = col;
		this.exploding = exploding;
	}

	public void setX(int x)
	{
		this.x = x;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public void setColor(int col)
	{
		this.col = col;
	}
	
	public int getColor()
	{
		return this.col;
	}
	
	public void setExploding(boolean exploding)
	{
		this.exploding = exploding;
	}
	
	public boolean getExploding()
	{
		return this.exploding;
	}
}
