package com.oracle.model;

import java.util.ArrayList;
import java.util.HashMap;

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
		
		int[][] map = terrainManager.getMap();
		
		MapDisplayer display = new MapDisplayer(map, this.places);
		//Window w = 
		new Window(display);
		
		startTheGame();
	}

	
	private void startTheGame()
	{
		drawWarEvent();		
	}
	
	
	private void drawWarEvent()
	{
		/*
		Random generator = new Random();
		int nationIndexOne = generator.nextInt(nations.size());
		Nation protagonist = nations.get(nationIndexOne);
		*/
		System.out.println(findNationByID(1).getNeighbours().size());
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
			Place daPlace = new Place(z.getLands(), z.getBoundaries(), 0.5, NameGenerator.getRandomName());
			daPlace.owner = ownerId;
			daPlace.seaAccess = this.terrainManager.hasSeaAccess(z);
			places.add(daPlace);
			this.places.add(daPlace);
		}
		
		return places;
	}

}
