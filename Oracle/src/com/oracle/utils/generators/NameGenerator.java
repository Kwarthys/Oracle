package com.oracle.utils.generators;

import java.util.Random;

public class NameGenerator {
	
	final static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	
	final static char[] voyelles = "aeiouy".toCharArray();
	final static char[] consones = "bcdfghjklmnpqrstvwxz".toCharArray();
	
	public static String getRandomName()
	{
		StringBuilder sb = new StringBuilder();
		Random generator = new Random();
		
		int lenght = 4 + generator.nextInt(3);

		boolean keep = true;
		boolean voyelle = true;
		boolean memory = false;
		
		char lastConsone = 's';

		for(int i = 0; i <= lenght; i++)
		{
			keep = true;
			if(generator.nextDouble() > 0.3) keep = false;
			
			if(voyelle)
			{
				if(keep)
				{
					sb.append(voyelles[generator.nextInt(voyelles.length)]);
					//voyelle = true;
				}
				else
				{
					lastConsone = consones[generator.nextInt(consones.length)];
					sb.append(lastConsone);
					voyelle = false;
					memory = false;
				}
			}
			else
			{
				if(keep)
				{
					if(!memory)
					{
						if(generator.nextDouble() > 0.6 && i != 1)
						{
							sb.append(lastConsone);
							memory = true;
						}
							
					}
					//voyelle = false;
				}
				else
				{
					sb.append(voyelles[generator.nextInt(voyelles.length)]);
					voyelle = true;
				}
			}

		}
		
		String name = sb.toString();
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
}
