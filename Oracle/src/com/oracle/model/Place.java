package com.oracle.model;

import java.util.ArrayList;

public class Place {

	public ArrayList<int[]> lands = new ArrayList<>();
	public ArrayList<int[]> boundaries = new ArrayList<>();
	public ArrayList<Place> neighbours = new ArrayList<>();
	
	public double landValue;
	
	public boolean seaAccess;
	
	public String name;
	
	public int owner;
	
	private int zoneID;
	
	public Place()
	{
		landValue = 0.5;
	}

	public Place(ArrayList<int[]> lands, ArrayList<int[]> boundaries, int zoneID, double value, String name)
	{
		this.lands = new ArrayList<>(lands);
		this.boundaries = new ArrayList<>(boundaries);
		this.landValue = value;
		this.name = name;
		this.zoneID = zoneID;
	}
	
	public int getZoneID() {
		return zoneID;
	}
}
