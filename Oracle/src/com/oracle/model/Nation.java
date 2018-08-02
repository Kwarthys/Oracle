package com.oracle.model;

import java.util.ArrayList;

public class Nation
{
	private Zone lands;
	private ArrayList<Actor> actors;
	
	public String name;
	
	private int id;
	
	public Nation(Zone land, ArrayList<Actor> actors, String name)
	{
		this.lands = new Zone(land);
		this.actors = new ArrayList<>(actors);
		
		id = this.lands.getID();
		
		this.name = name;
	}
	
	
	/*** GETTERS SETTERS ***/
	public Zone getLands() {return lands;}
	public ArrayList<Actor> getActors() {return actors;}
	public int getID() {return id;}
}
