package com.oracle.model;

import java.util.ArrayList;

import com.oracle.hmi.MapDisplayer;
import com.oracle.hmi.Window;
import com.oracle.map.Terrain;
import com.oracle.utils.generators.NameGenerator;

public class Manager {
	
	public ArrayList<Place> places;
	
	public Manager()
	{
		Terrain terrainGenerator = new Terrain(1000,1000, Math.random()*10);
		int[][] map = terrainGenerator.generateMap();
		
		createPlaces(terrainGenerator.getAtomicPlaces());
		
		MapDisplayer display = new MapDisplayer(map, terrainGenerator.getAtomicPlaces());
		//Window w = 
		new Window(display);
	}

	public void createPlaces(ArrayList<Zone> allZones)
	{
		places = new ArrayList<>();
		
		for(Zone z : allZones)
		{
			places.add(new Place(z.getLands(), z.getBoundaries(), 0.5, NameGenerator.getRandomName()));
		}
		
		for(Place p : places)
		{
			System.out.print(p.name + " ");
		}
		System.out.println("\n" + places.size());
	}

}
