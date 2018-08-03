package com.oracle.model;

import java.util.ArrayList;

public class Nation
{
	private ArrayList<Actor> actors;
	
	private ArrayList<Place> places;
	
	private ArrayList<Nation> neighbours;
	
	public String name;
	
	private int id;
	
	public Nation(ArrayList<Actor> actors, String name, int id)
	{
		this.actors = new ArrayList<>(actors);
		
		this.id = id;
		
		this.name = name;
	}
	
	
	/*** GETTERS SETTERS ***/
	public ArrayList<Actor> getActors() {return actors;}
	public int getID() {return id;}
	public ArrayList<Place> getPlaces(){return places;}
	public ArrayList<Nation> getNeighbours(){return neighbours;}
	
	public void setPlaces(ArrayList<Place> places)
	{
		this.places = new ArrayList<>(places);
	}
	
	public void setNeighbours(ArrayList<Nation> neighbours)
	{
		this.neighbours = new ArrayList<>(neighbours);
	}
	

	
	public ArrayList<Place> findPlacesNear(int targetId)
	{
		ArrayList<Place> places = new ArrayList<>();
		
		for(Place p : this.places)
		{
			for(Place v : p.neighbours)
			{
				if(v.owner == targetId)
				{
					places.add(v);
				}
			}
		}
		
		return places;
	}
}
