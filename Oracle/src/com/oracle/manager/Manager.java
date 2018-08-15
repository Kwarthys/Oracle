package com.oracle.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.oracle.hmi.MapDisplayer;
import com.oracle.hmi.Window;
import com.oracle.map.Terrain;
import com.oracle.model.Actor;
import com.oracle.model.Penalty;
import com.oracle.model.Nation;
import com.oracle.model.Place;
import com.oracle.model.Zone;
import com.oracle.model.events.WarEvent;
import com.oracle.utils.Callback;
import com.oracle.utils.NationFinder;
import com.oracle.utils.generators.NameGenerator;

public class Manager {
	
	public ArrayList<Place> places = new ArrayList<>();
	public ArrayList<Nation> nations = new ArrayList<>();
	
	private MapDisplayer display;
	private Window window;
	
	private Terrain terrainManager;
	
	private NationFinder nationFinder;
	
	private int turnCount = 0;
	
	public Manager()
	{
		this.terrainManager = new Terrain(1000,1000, Math.random()*10);
		terrainManager.generateMap();
		
		createNations(terrainManager.getAllZones(), terrainManager.getNationsOfPlaces());
		
		computePlacesNeighbours();
		
		int[][] map = terrainManager.getMap();
		
		display = new MapDisplayer(map, this.places);
		window = new Window(display);
		
		this.nationFinder = new NationFinder() {			
			@Override
			public Nation getNationById(int nationID) {
				return findNationByID(nationID);
			}
		};
		
		startTheGame();
	}


	private void computePlacesNeighbours()
	{		
		for (Map.Entry<Zone, ArrayList<Zone>> entry : terrainManager.getZonesNeighbours().entrySet())
		{
			Place p = getPlaceByZoneID(entry.getKey().getID());
			
			if(p!=null)
			{
				ArrayList<Place> neighbours = new ArrayList<>();
				for(Zone z : entry.getValue())
				{
					Place n = getPlaceByZoneID(z.getID());
					if(n != null)
					{
						neighbours.add(n);
					}
				}
				
				p.neighbours = neighbours;
			}
		}			
	}
	
	private Place getPlaceByZoneID(int zoneID)
	{
		for(int i = 0; i < places.size(); i++)
		{
			if(zoneID == places.get(i).getZoneID())
			{
				return places.get(i);
			}
		}
		
		System.err.println("Returning Null in getPlaceByZoneID.");
		return null;
	}


	private void startTheGame()
	{
		for(Nation n : nations)
		{
			n.computeNewPlans();
		}
		
		window.registerListener(new Callback(){			
			@Override
			public void callback(){
				drawWarEvent();
				turnCount++;
			}
		});
	}
	
	
	private void drawWarEvent()
	{
		Nation protagonist;
		Random generator = new Random();
		
		do
		{			
			int nationIndexOne = generator.nextInt(nations.size());
			protagonist = nations.get(nationIndexOne);			
		}while(protagonist.getNeighbours().size() == 0);
		
		Place wanted = protagonist.getPlans();
		if(wanted == null)
		{
			System.err.println("No eligible places");
			return;
		}	


		WarEvent w = new WarEvent(nationFinder, wanted, protagonist.getID());

		if(w.getSuccess())
		{
			switchProperty(wanted, protagonist);
		}
		else
		{
			findNationByID(wanted.owner).addNewPenalty(new Penalty(3, turnCount, wanted)); //defender
			protagonist.addNewPenalty(new Penalty(2, turnCount));
		}
			

		System.out.println(w.getStory());


	}
	
	
	private void switchProperty(Place place, Nation dest)
	{
		Nation src = findNationByID(place.owner);
		place.owner = dest.getID();
		src.getPlaces().remove(place);
		dest.getPlaces().add(place);
		
		terrainManager.repaintZone(place.lands, dest.getID());

		if(src.getPlaces().size() == 0)
		{
			this.nations.remove(src);
		}

		ArrayList<Integer> toRefresh = new ArrayList<>();
		toRefresh.add(src.getID());
		toRefresh.add(dest.getID());
		for(Place p : place.neighbours)
		{
			if(!toRefresh.contains(p.owner))
			{
				toRefresh.add(p.owner);
			}
		}
		
		for(Integer i : toRefresh)
		{
			Nation current = findNationByID(i);
			registerNewNeighborhood(current);
			current.computeNewPlans();
		}
		
	}
	
	private void registerNewNeighborhood(Nation n)
	{
		ArrayList<Nation> neighbours = new ArrayList<>();		
		for(Integer id : n.getNeighborhood())
		{
			neighbours.add(findNationByID(id));
		}		
		n.setNeighbours(neighbours);
	}
	
	public Nation findNationByID(int id)
	{
		for(Nation n : nations)
		{
			if(n.getID() == id)
				return n;
		}		
		return null;
	}


	private void createNations(ArrayList<Zone> zones, HashMap<Integer, ArrayList<Integer>> hashMap)
	{
		nations = new ArrayList<>();
		
		for(Zone z : zones)
		{
			Nation n = new Nation(new ArrayList<Actor>(), NameGenerator.getRandomName(), z.getID());
			n.setPlaces(createPlaces(hashMap.get(z.getID()), z.getID()));
			nations.add(n);
		}
		
		/*** Managing Nation neighbourhood then computing their starting score ***/
		for(Nation n : nations)
		{
			ArrayList<Nation> voisins = new ArrayList<>();
			ArrayList<Zone> voisinsZones = null;
			boolean found = false;
			for(int i = 0; i < zones.size() && !found; i++)
			{
				Zone current = zones.get(i);
				if(current.getID() == n.getID())
				{
					found = true;
					voisinsZones = new ArrayList<>(current.getNeighbours());
				}
			}
			
			for(Zone z : voisinsZones)
			{
				voisins.add(findNationByID(z.getID()));
			}
			
			n.setNeighbours(voisins);
			
			/**score**/
			n.computeFirstScore();
		}
	}

	public ArrayList<Place> createPlaces(ArrayList<Integer> arrayList, int ownerId)
	{
		ArrayList<Place> places = new ArrayList<>();
		
		for(Integer zoneID : arrayList)
		{
			Zone z = getAtomicZoneByZoneID(zoneID);
			Place daPlace = new Place(z.getLands(), z.getBoundaries(), z.getID(), Math.random(), NameGenerator.getRandomName());
			daPlace.owner = ownerId;
			daPlace.seaAccess = this.terrainManager.hasSeaAccess(z);
			places.add(daPlace);
			this.places.add(daPlace);
		}
		
		return places;
	}


	private Zone getAtomicZoneByZoneID(Integer zoneID)
	{
		for(Zone z : terrainManager.getAtomicPlaces())
		{
			if(z.getID() == zoneID)
				return z;
		}
		
		System.err.println("Returning null in getAtomicZoneByZoneID.");
		return null;
	}

}
