package com.oracle.model;

public class Penalty {

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
}
