package com.oracle.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.oracle.hmi.MapDisplayer;
import com.oracle.hmi.Window;
import com.oracle.map.Terrain;
import com.oracle.utils.Callback;
import com.oracle.utils.generators.NameGenerator;

public class Manager {
	
	public ArrayList<Place> places = new ArrayList<>();
	public ArrayList<Nation> nations = new ArrayList<>();
	
	private MapDisplayer display;
	private Window window;
	
	private Terrain terrainManager;
	
	public Manager()
	{
		this.terrainManager = new Terrain(1000,1000, Math.random()*10);
		terrainManager.generateMap();
		
		createNations(terrainManager.getAllZones(), terrainManager.getNationsOfPlaces());
		
		computePlacesNeighbours();
		
		int[][] map = terrainManager.getMap();
		
		display = new MapDisplayer(map, this.places);
		window = new Window(display);
		
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
		window.registerListener(new Callback(){			
			@Override
			public void callback(){
				drawWarEvent();
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
		
		int randIndex = generator.nextInt(protagonist.getNeighbours().size());
		Nation otherNation = protagonist.getNeighbours().get(randIndex);
		
		//Find places of otherNation neighbours of nation
		ArrayList<Place> possibilities = protagonist.findPlacesNear(otherNation.getID());
		
		Collections.shuffle(possibilities);
		
		if(possibilities.size() == 0)
		{
			System.err.println("No eligible places");
		}
		else
		{
			Place p = possibilities.get(0);
			switchProperty(p, otherNation, protagonist);
		}
	}
	
	
	private void switchProperty(Place place, Nation src, Nation dest)
	{
		place.owner = dest.getID();
		src.getPlaces().remove(place);
		dest.getPlaces().add(place);
		
		terrainManager.repaintZone(place.lands, dest.getID());

		if(src.getPlaces().size() == 0)
		{
			this.nations.remove(src);
		}
		else
		{
			registerNewNeighborhood(src);
		}
		
		registerNewNeighborhood(dest);
	}
	
	public void registerNewNeighborhood(Nation n)
	{
		ArrayList<Nation> neighbours = new ArrayList<>();		
		for(Integer id : n.getNewNeighborhood())
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
		
		/*** Managing Nation neighbourhood ***/
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
		}
	}

	public ArrayList<Place> createPlaces(ArrayList<Integer> arrayList, int ownerId)
	{
		ArrayList<Place> places = new ArrayList<>();
		
		for(Integer zoneID : arrayList)
		{
			Zone z = getAtomicZoneByZoneID(zoneID);
			Place daPlace = new Place(z.getLands(), z.getBoundaries(), z.getID(), 0.5, NameGenerator.getRandomName());
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
