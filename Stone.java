package multitris;

public class Stone 
{
	private int x;
	private int y;
	private String color;
	private int rotation;
	private int type;
	public boolean down=false;
	public boolean [] [] matrix;
	public Stone(int type, String color, int rotation)
	{
		switch(type)
		{
			case 0:
			{
				matrix = new boolean[][]{{true,true,true,true}};
				break;
			}
			case 1:
			{
				matrix = new boolean[] [] {{true, true},{true,true}};
				break;
			}
			case 2:
			{
				matrix = new boolean[] [] {{true,true,true},{false,true,false}};
				break;
			}
		}
		this.color=color;
		this.rotation=rotation;
	}
	public boolean [] [] getPixelMatrix()
	{
		return matrix;
	}
	public int getX()
	{
		return x;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getRotation() {
		return rotation;
	}
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setX(int x) {
		this.x = x;
	}
	
}
