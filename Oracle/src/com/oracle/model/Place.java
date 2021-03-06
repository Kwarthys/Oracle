package com.oracle.model;

import java.util.ArrayList;

import com.oracle.utils.MyTools;

public class Place {

	public ArrayList<int[]> lands = new ArrayList<>();
	public ArrayList<int[]> boundaries = new ArrayList<>();
	public ArrayList<Place> neighbours = new ArrayList<>();
	
	public double landValue;
	
	public boolean seaAccess;
	
	public String name;
	
	public Nation owner;
	
	private int zoneID;
	
	public Place()
	{
		landValue = 0.5;
	}

	public Place(ArrayList<int[]> lands, ArrayList<int[]> boundaries, int zoneID, double value, String name)
	{
		this.lands = MyTools.arrayCopy(lands);
		this.boundaries = MyTools.arrayCopy(boundaries);
		this.landValue = value;
		this.name = name;
		this.zoneID = zoneID;
	}
	
	public int getZoneID() {
		return zoneID;
	}
}
