package com.oracle.model;

import java.util.ArrayList;

public class Place {

	public ArrayList<int[]> lands = new ArrayList<>();
	public ArrayList<int[]> boundaries = new ArrayList<>();
	
	public double landValue;
	
	public String name;
	
	public Place()
	{
		landValue = 0.5;
	}
	
	public Place(ArrayList<int[]> lands, ArrayList<int[]> boundaries, double value, String name)
	{
		this.lands = new ArrayList<>(lands);
		this.boundaries = new ArrayList<>(boundaries);
		this.landValue = value;
		this.name = name;
	}
}
