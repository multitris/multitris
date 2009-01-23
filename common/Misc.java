package common;

import java.awt.Color;
import java.util.Random;

/** This class provides some misc functions as delay() and random()
 * 
 * @author thommy
 *
 */
public class Misc 
{
	/**
	 * Converts a given Hexcolor String like ff0012 into an Java.awt.Color Object
	 * @param hexcolor
	 * @return
	 */
	public Color convertColor(String hexcolor)
	{
		try
		{
			int r = Integer.parseInt(hexcolor.substring(0, 2), 16);
			int g = Integer.parseInt(hexcolor.substring(2, 4), 16);
			int b = Integer.parseInt(hexcolor.substring(4, 6), 16);
			return new Color(r,g,b);
		}
		catch (Exception e)
		{
			System.err.println("Der idiot kann keine farben... wir machen schwarz.,");
			return new Color(0,0,0);
		}
	}
	/**
	 * Generates a random Color in Hex-Format like ff0023
	 * (In fact uses a predefined list of colors until end is reached
	 * and then is generates random colors. You're welcome to adapt your
	 * favorite colors here ;)
	 */
	private static int lastColorIndex=0;
	public static String generateColor()
	{
		String color="";
		switch (lastColorIndex)
		{
			//here you can define the first colors
			case 0:{color="ff0000";break;}
			case 1:{color="00ff00";break;}
			case 2:{color="0000ff";break;}
			case 3:{color="ff00ff";break;}
			case 4:{color="f0f0f0";break;}
			//every other color will be random.(=bad)
			default:
			{
				color=Integer.toHexString(1048576 + (int)(Math.random() * 9437184.0));
				break;
			}
		}
		lastColorIndex++;
		return color;
	}
	/**
	 * Blocks the Program for a given Time (and handles the Exception)
	 * @param Milisecs Time in Milisecs
	 */
	public static void delay(int Milisecs)
	{
		try
		{
			Thread.sleep(Milisecs);
		}
		catch (Exception e)
		{
			System.err.println("During Misc.delay() Exception occured: "+e);
		}
	}
	/**
	 * Generates a random Integer number between the given values
	 * @param min the minimum value the number may be
	 * @param max the maximum value the number may be
	 * @return
	 */
	public static int random(int min, int max)
	{
        Random rn = new Random();
        int n = max - min + 1;
        int i = rn.nextInt() % n;
        if (i < 0)
                i = -i;
        return min + i;
	}
	/**
	 * generates a random int number between 0 and the given Max value
	 * @param max
	 * @return
	 */
	public static int random(int max)
	{
		return random(0, max);
	}
}
