package com.oracle.model;

import java.util.ArrayList;
import java.util.Random;

import com.oracle.hmi.MapDisplayer;
import com.oracle.hmi.Window;
import com.oracle.map.Terrain;
import com.oracle.utils.generators.NameGenerator;

public class Manager {
	
	public ArrayList<Place> places;
	public ArrayList<Nation> nations;
	
	public Manager()
	{
		Terrain terrainGenerator = new Terrain(1000,1000, Math.random()*10);
		int[][] map = terrainGenerator.generateMap();
		
		createPlaces(terrainGenerator.getAtomicPlaces());
		createNations(terrainGenerator.getAllZones());
		
		MapDisplayer display = new MapDisplayer(map, terrainGenerator.getAtomicPlaces());
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
		Random generator = new Random();
		int nationIndexOne = generator.nextInt(nations.size());
		Nation protagonist = nations.get(nationIndexOne);
		
		int voisinID = protagonist.getLands().getNeighbours().get(generator.nextInt(protagonist.getLands().getNeighbours().size())).getID();
		
		Nation otherNation = findNationByID(voisinID);
	}
	
	
	private Nation findNationByID(int id)
	{
		for(Nation n : nations)
		{
			if(n.getID() == id)
				return n;
		}
		
		return null;
	}


	private void createNations(ArrayList<Zone> zones)
	{
		nations = new ArrayList<>();
		
		for(Zone z : zones)
		{
			nations.add(new Nation(z, new ArrayList<Actor>(), NameGenerator.getRandomName()));
		}
	}

	public void createPlaces(ArrayList<Zone> zones)
	{
		places = new ArrayList<>();
		
		for(Zone z : zones)
		{
			places.add(new Place(z.getLands(), z.getBoundaries(), 0.5, NameGenerator.getRandomName()));
		}
	}

}
