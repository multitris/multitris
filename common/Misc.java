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
	 */
	public static String generateColor()
	{
		return Integer.toHexString(1048576 + (int)(Math.random() * 9437184.0));
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
	public static int random(int max)
	{
		return random(0, max);
	}
}
