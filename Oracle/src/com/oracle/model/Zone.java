package com.oracle.model;

import java.util.ArrayList;

import com.oracle.utils.MyTools;

public class Zone {
	
	private ArrayList<int[]> lands = new ArrayList<>();
	private ArrayList<int[]> boundaries = new ArrayList<>();
	public ArrayList<Zone> neighbours = new ArrayList<>();
	
	private int zoneID;
	
	public Zone(int id)
	{
		zoneID = id;
	}
	
	public Zone(Zone zone) {
		this.lands = MyTools.arrayCopy(zone.lands);		
		this.boundaries = MyTools.arrayCopy(zone.boundaries);
		this.neighbours = new ArrayList<>(zone.neighbours);
		this.zoneID = zone.zoneID;
	}

	public int getID()
	{
		return zoneID;
	}

	public ArrayList<int[]> getLands() {
		return lands;
	}

	public int size() {
		return lands.size();
	}

	public ArrayList<int[]> getBoundaries() {
		return boundaries;
	}

	public ArrayList<Zone> getNeighbours() {
		return neighbours;
	}

	public void setLands(ArrayList<int[]> lands) {
		this.lands = lands;
	}
	
	/**
	 * @return zoneID
	 */
	public int addToZone(int j, int i)
	{
		int[] tmp = {j,i};
		lands.add(tmp);
		return zoneID;
	}
	
	public int addToBoundaries(int j, int i)//Returns the state ID 
	{
		int[] tmp = {j,i};
		boundaries.add(tmp);
		return zoneID;
	}
	
	public int addToBoundaries(int[] c)//Returns the state ID 
	{
		boundaries.add(c);
		return zoneID;
	}
	
	public void resetBoundaries() 
	{
		boundaries = new ArrayList<>();
	}
	
	public void addNeighbour(Zone s)
	{
		neighbours.add(s);
	}

	public void resetNeighbours()
	{
		neighbours = new ArrayList<>();
	}

}
