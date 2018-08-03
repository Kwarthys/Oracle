package com.oracle.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.oracle.hmi.MapDisplayer;
import com.oracle.hmi.Window;
import com.oracle.map.Terrain;
import com.oracle.utils.generators.NameGenerator;

public class Manager {
	
	public ArrayList<Place> places = new ArrayList<>();
	public ArrayList<Nation> nations = new ArrayList<>();
	
	private Terrain terrainManager;
	
	public Manager()
	{
		this.terrainManager = new Terrain(1000,1000, Math.random()*10);
		terrainManager.generateMap();
		
		createNations(terrainManager.getAllZones(), terrainManager.getNationsOfPlaces());
		
		computePlacesNeighbours();
		
		int[][] map = terrainManager.getMap();
		
		MapDisplayer display = new MapDisplayer(map, this.places);
		//Window w = 
		new Window(display);
		
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
		drawWarEvent();		
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
		
		System.out.println("Nation:" + protagonist.getID() + " vs " + "other:" + otherNation.getID());
		
		//Find places of otherNation neighbours of nation
		ArrayList<Place> possibilities = protagonist.findPlacesNear(otherNation.getID());
		
		Collections.shuffle(possibilities);
		
		Place p = possibilities.get(0);
		
		
		System.out.print("Press Enter to continue");
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(" Yeah.");
		switchProperty(p, otherNation, protagonist);
	}
	
	
	private void switchProperty(Place place, Nation src, Nation dest)
	{
		place.owner = dest.getID();
		src.getPlaces().remove(place);
		dest.getPlaces().add(place);
		
		terrainManager.repaintZone(place.lands, dest.getID());
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


	private void createNations(ArrayList<Zone> zones, HashMap<Integer, ArrayList<Zone>> placesOfZones)
	{
		nations = new ArrayList<>();
		
		for(Zone z : zones)
		{
			Nation n = new Nation(new ArrayList<Actor>(), NameGenerator.getRandomName(), z.getID());
			n.setPlaces(createPlaces(placesOfZones.get(z.getID()), z.getID()));
			nations.add(n);
		}
		
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

	public ArrayList<Place> createPlaces(ArrayList<Zone> zones, int ownerId)
	{
		ArrayList<Place> places = new ArrayList<>();
		
		for(Zone z : zones)
		{
			Place daPlace = new Place(z.getLands(), z.getBoundaries(), z.getID(), 0.5, NameGenerator.getRandomName());
			daPlace.owner = ownerId;
			daPlace.seaAccess = this.terrainManager.hasSeaAccess(z);
			places.add(daPlace);
			this.places.add(daPlace);
		}
		
		return places;
	}

}
