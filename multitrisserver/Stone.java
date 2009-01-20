package multitrisserver;

public class Stone 
{
	private int x;
	private int y;
	private boolean [] [] matrix;
	private int numberOfPixels = 0; // is computed the first time numberOfPixels() is called
	private int color = 1;
	
	public static Stone randomStone()
	{
		int randType = (int)(7.0 * Math.random());
		
		boolean[][] matrix;
		
		switch(randType)
		{
			case 0:	matrix = new boolean[][] {{false,true,true},{true,true,false}}; break;
			case 1:	matrix = new boolean[][] {{false,true},{false,true},{true,true}}; break;
			case 2:	matrix = new boolean[][] {{true,true},{true,true}}; break;
			case 3:	matrix = new boolean[][] {{true,true,false},{false,true,true}}; break;
			case 4:	matrix = new boolean[][] {{true,true},{false,true},{false,true}}; break;
			case 5:	matrix = new boolean[][] {{false,true,false},{true,true,true}}; break;
			default:	matrix = new boolean[][] {{true,true,true,true}}; break;
		}
		
		return new Stone(matrix);
	}
	
	public Stone(boolean[][] matrix)
	{
		this.matrix = matrix;
	}
	
	public boolean[][] getPixelMatrix()
	{
		return this.matrix;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public void move(int deltaY, int deltaX)
	{
		this.x += deltaX;
		this.y += deltaY;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public int getWidth()
	{
		return this.matrix[0].length;
	}
	
	public int getHeight()
	{
		return this.matrix.length;
	}
	
	public void setColor(int color)
	{
		this.color = color;
	}
	
	public int getColor()
	{
		return this.color;
	}
	
	public Stone clone()
	{
		Stone copy = new Stone(this.matrix);
		copy.setX(this.x);
		copy.setY(this.y);
		
		return copy;
	}
	
	private int numberOfPixels()
	{
		if(this.numberOfPixels == 0)
		{
			for(int row=0;row<this.getHeight();row++)
			{
				for(int col=0;col<this.getWidth();col++)
				{
					if(this.matrix[row][col] == true)
						this.numberOfPixels++;
				}
			}
		}
		
		return this.numberOfPixels;
	}
	
	public void rotate()
	{
		// rotate matrix first, anticlockwise
		
		boolean[][] newMatrix = new boolean[this.getWidth()][];
		
		for(int newRow=0;newRow<this.getWidth();newRow++)
		{
			newMatrix[newRow] = new boolean[this.getHeight()];
			
			for(int newCol=0;newCol<this.getHeight();newCol++)
				newMatrix[newRow][newCol] = this.matrix[newCol][this.getWidth()-newRow-1];
		}
		
		this.matrix = newMatrix;
		
		// rotation of the matrix is not enough; move the rotated stone so that it is rotated around its center
		
		int diffY = this.getHeight() - this.getWidth();
		int diffX = this.getWidth() - this.getHeight();
		
		this.move(((diffY)/2), (diffX)/2);
	}
	
	public boolean collidesWith(Stone b)
	{
		// check position and dimensions first
		
		if(((b.getY() + b.getHeight()) <= this.getY()) || ((this.getY() + this.getHeight()) <= b.getY()) || ((b.getX() + b.getWidth()) <= this.getX()) || ((this.getX() + this.getWidth()) <= b.getX()))
			return false;
		
		int[][] myPixels = this.getAbsolutePixels();
		int[][] hisPixels = b.getAbsolutePixels();
		
		for(int myi=0;myi<myPixels.length;myi++)
		{
			for(int hisi=0;hisi<hisPixels.length;hisi++)
			{
				if(myPixels[myi][0] == hisPixels[hisi][0] && myPixels[myi][1] == hisPixels[hisi][1])
					return true;
			}
		}
		
		return false;
	}
	
	public boolean collidesWith(int [][] fixedPixels)
	{
		int[][] absolutePixels = this.getAbsolutePixels();
		
		for(int i=0;i<absolutePixels.length;i++)
		{
			int[] coords = absolutePixels[i];
			
			if(coords[0] > -1 && coords[0] < fixedPixels.length && coords[1] > -1 && coords[1] < fixedPixels[0].length)
			{
				if(fixedPixels[coords[0]][coords[1]] != 0)
					return true;
			}
		}
		
		return false;
	}
	
	public int[][] getAbsolutePixels()
	{
		int[][] absolutePixels = new int[this.numberOfPixels()][];
		int i = 0;
		
		for(int row=0;row<this.getHeight();row++)
		{
			for(int col=0;col<this.getWidth();col++)
			{
				if(this.matrix[row][col] == true)
					absolutePixels[i++] = new int[] {row+this.y, col+this.x};
			}
		}
		
		return absolutePixels;
	}
}
