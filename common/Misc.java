package common;

import java.util.Random;

/** This class provides some misc functions as delay() and random()
 * 
 * @author thommy
 *
 */
public class Misc 
{
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
