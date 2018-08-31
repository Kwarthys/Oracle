package com.oracle.model;

public class Standing {

	public static final int ATTACK = -20;
	public static final int TERRIROTY_WANTED = -2;	
	
	private int standing = 0;
	
	public void addModifier(int amount)
	{
		standing += amount;
	}
	
	public int getCurrentStanding()
	{
		return standing;
	}

	public void refresh()
	{
		standing *= 0.95;
	}

}
