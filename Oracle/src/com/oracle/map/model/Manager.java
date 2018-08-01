package com.oracle.map.model;

import java.util.ArrayList;

import com.oracle.utils.generators.NameGenerator;

public class Manager {
	
	public static ArrayList<Place> places;

	public static void createPlaces(ArrayList<Zone> allZones)
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
