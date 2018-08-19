package com.oracle.model;

import java.util.ArrayList;

import com.oracle.manager.Manager;

public class Nation
{
	private ArrayList<Actor> actors;	
	private ArrayList<Place> places;	
	private ArrayList<Nation> neighbours;	
	public String name;	
	private int id;
	
	private double score;
	
	private Place placeWanted = null;
	
	private ArrayList<Penalty> penalties = new ArrayList<>();
	
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
	public double getScore() {return score;}
	public ArrayList<Penalty> getPenalties(){return penalties;}
	
	public boolean hasPenaltiesAboutThisPlace(Place place)
	{
		for(Penalty p : penalties)
		{
			if(p.place != null)
			{
				if(p.place == place)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	public void changeScore(double amount)
	{
		//System.out.println("Nation " + name + " : " + String.format("%.1f", score) + " -> " + String.format("%.1f", (score + amount)));
		score += amount;
	}
	
	public void computeFirstScore()
	{
		double score = 0;
		
		for(Place p : this.places)
		{
			score += p.landValue;
		}
		
		score += (Math.random() * score);
		
		this.score = score;
	}
	
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


	public ArrayList<Integer> getNeighborhood()
	{
		ArrayList<Integer> voisins = new ArrayList<>();
		
		for(Place p : this.places)
		{
			for(Place v : p.neighbours)
			{
				if(!voisins.contains(v.owner) && v.owner != getID())
				{
					voisins.add(v.owner);
				}
			}
		}
		
		return voisins;
	}

	
	public Place getPlans() {return placeWanted;}
	

	public void computeNewPlans()
	{
		placeWanted = null;
		for(Place p : this.places)
		{
			for(Place v : p.neighbours)
			{
				if(v.owner != getID())
				{
					if(placeWanted == null)
						placeWanted = v;
					
					else if(v.landValue > placeWanted.landValue)
					{
						placeWanted = v;
					}
				}
			}
		}		
	}


	public void addNewPenalty(Penalty penalty)
	{
		if(penalty.place == null)
		{
			penalties.add(penalty);
			return;
		}
		
		for(Penalty p : penalties)
		{
			if(p.place != null)
			{
				if(penalty.place == p.place)
				{
					p.startTurn = penalty.startTurn;
					return;
				}
			}
		}
	}


	public void refreshPenalties()
	{
		ArrayList<Penalty> toRemove = new ArrayList<>();
		for(Penalty p : penalties)
		{
			if(p.startTurn + p.duration < Manager.turnCount)
			{
				toRemove.add(p);
			}
			else if(p.place != null)
			{
				if(!places.contains(p.place))
				{
					System.out.println("lostTerrain");
					toRemove.add(p);
				}
			}
		}
		
		if(toRemove.size() > 0)
		{
			System.out.println(name + " removes " + toRemove.size() + " penalty(ies).");
		}
		
		for(Penalty p : toRemove)
		{
			penalties.remove(p);
		}
	}
}
