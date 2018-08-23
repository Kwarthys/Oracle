package com.oracle.model;

public class Penalty {
	
	private static final int[] values = {0, 2, 5, 10, 20};

	public Place place = null;
	public int duration;
	public int startTurn;
	
	public Penalty(int duration, int turnStart)
	{
		this.duration = duration;
		this.startTurn = turnStart;
	}
	
	public Penalty(int duration, int turnStart, Place place)
	{
		this(duration, turnStart);
		this.place = place;
	}
	
	public static double penaltyAmount(int marks)
	{
		if(marks < 5)
		{
			return values[marks];
		}
		else
		{
			return 20 + 20*Math.log10(marks - 3);
		}
	}
}
