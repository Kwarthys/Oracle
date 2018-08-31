package com.oracle.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	
	private HashMap<Nation, Standing> standings = new HashMap<>();
			
	/*** nation stats ***/
	private int aggressivity;
	//private int diplomaty;
	//private int trust;
	
	public Nation(ArrayList<Actor> actors, String name, int id)
	{
		this.actors = new ArrayList<>(actors);
		
		this.id = id;
		
		this.name = name;

		this.aggressivity = (int) (Math.random() * 100); // [ 0 : 100 [
		//this.diplomaty = (int) (Math.random() * 100);
		//this.trust = (int) (Math.random() * 100);
	}
	
	
	/*** GETTERS SETTERS ***/
	public ArrayList<Actor> getActors() {return actors;}
	public int getID() {return id;}
	public ArrayList<Place> getPlaces(){return places;}
	public ArrayList<Nation> getNeighbours(){return neighbours;}
	public double getScore() {return score;}
	public ArrayList<Penalty> getPenalties(){return penalties;}
	
	public void updateStandingAbout(Nation about, int amount)
	{
		if(standings.containsKey(about))
		{
			standings.get(about).addModifier(amount);
		}
	}
	
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
		//System.out.println("Nation " + name + " : " + String.format("%.1f", score) + " -> " + String.format("%.1f", (score + amount)) + " (+" + amount + ")");
		score += amount;
		score = Math.max(0.1, score);
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
				if(v.owner.getID() == targetId)
				{
					places.add(v);
				}
			}
		}
		
		return places;
	}


	public void registerNewNeighborhood()
	{
		ArrayList<Nation> voisins = new ArrayList<>();
		
		for(Place p : this.places)
		{
			for(Place v : p.neighbours)
			{
				if(!voisins.contains(v.owner) && v.owner.getID() != getID())
				{
					voisins.add(v.owner);
				}
			}
		}
		
		for(Nation n : voisins)
		{
			if(!standings.containsKey(n))
			{
				standings.put(n, new Standing());
			}
		}
		
		this.neighbours = voisins;
	}

	
	public Place getPlans() {return placeWanted;}
	

	public void computeNewPlans()
	{		
		this.placeWanted = decideWar();
	}
	
	private Place decideWar()
	{
		Place wanted = null;
		
		Nation toAttack = null;
		int worstScore = 1000;

		for(Nation n : neighbours)
		{
			int nationStandings = standings.get(n).getCurrentStanding();

			if(nationStandings < 2 * this.aggressivity - 130)//Wants to attack
			{
				double nationActualScore = n.score * (1 - Penalty.penaltyAmount(n.getPenalties().size()) / 100);
				double ourActualScore = this.score * (1 - Penalty.penaltyAmount(this.getPenalties().size()) / 100);			
				
				if(nationActualScore < ourActualScore * ( 1 + 0.01 * this.aggressivity)) //Can Attack
				{					
					int nationScore = nationStandings * (int)nationActualScore;

					if(toAttack == null || worstScore > nationScore)
					{
						toAttack = n;
						worstScore = nationScore;
					}
				}
			}
		}
		
		if(toAttack == null)
		{
			return null;
		}
		
		HashMap<Place, Double> places = new HashMap<>();

		for(Place p : this.places)
		{
			for(Place v : p.neighbours)
			{
				if(v.owner.getID() == toAttack.getID())
				{
					if(!places.containsKey(v))
					{
						places.put(v, 1.0);
					}
					else
					{
						places.put(v, places.get(v) + 1.0);
					}
				}
			}
		}
		
		double bestScore = 0;
		
		for (Map.Entry<Place, Double> entry : places.entrySet())
		{
			double landValue = entry.getKey().landValue * (1 + 0.1  * Math.random());
			
			double valueByOccurence = landValue * (1 +  (entry.getValue()-1)*(entry.getValue()-1)/4);
			
			if(bestScore == 0)
			{
				bestScore = valueByOccurence;
				wanted = entry.getKey();
			}
			else if(bestScore < valueByOccurence)
			{
				bestScore = valueByOccurence;
				wanted = entry.getKey();
			}
		}

		return wanted;
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
		
		penalties.add(penalty);
	}

	
	public void update()
	{
		updateStandings();
		updatePenalties();
	}
	
	private void updateStandings()
	{
		standings.forEach((k, v) -> v.refresh());
	}

	private void updatePenalties()
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
					toRemove.add(p);
				}
			}
		}
		
		for(Penalty p : toRemove)
		{
			penalties.remove(p);
		}
	}
	
	public String getQuickDescriptor()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("Nation ");
		sb.append(this.name);
		sb.append("(");
		sb.append(this.aggressivity);
		sb.append(", ");
		sb.append(String.format("%.1f", this.getScore()));
		sb.append(")");
		
		return sb.toString();
	}


	public void printStandings()
	{
		System.out.println("\nStandings of " + getQuickDescriptor());
		standings.forEach((k, v) -> {
			System.out.println("about " + k.getQuickDescriptor() + " : " + v.getCurrentStanding() + ".");
		});
	}


	public void setDeathScore()
	{
		this.score = -1;
	}
}
