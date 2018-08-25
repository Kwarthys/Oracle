package com.oracle.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.oracle.hmi.MapDisplayer;
import com.oracle.hmi.Window;
import com.oracle.map.Terrain;
import com.oracle.model.Actor;
import com.oracle.model.Nation;
import com.oracle.model.Place;
import com.oracle.model.Zone;
import com.oracle.model.events.WarEvent;
import com.oracle.utils.Callback;
import com.oracle.utils.generators.NameGenerator;

public class Manager {
	
	public ArrayList<Place> places = new ArrayList<>();
	public ArrayList<Nation> nations = new ArrayList<>();
	
	private MapDisplayer display;
	private Window window;
	
	private Terrain terrainManager;
	
	public static int turnCount = 0;
	
	public Manager()
	{
		this.terrainManager = new Terrain(1000,1000, Math.random()*10);
		terrainManager.generateMap();
		
		createNations(terrainManager.getAllZones(), terrainManager.getNationsOfPlaces());
		
		computePlacesNeighbours();
		
		display = new MapDisplayer(terrainManager.getMap(), this.nations);
		display.updateGraphics();
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
		for(Nation n : nations)
		{
			n.registerNewNeighborhood();
			n.computeNewPlans();
		}
		
		window.registerListener(new Callback(){			
			@Override
			public void callback(){
				playATurn();
			}
		},
		new Callback() {
			@Override
			public void callback() {
				showStandings();
			}
		});
	}
	
	
	private void playATurn()
	{
		System.out.println(turnCount);
		drawWarEvent();
		turnCount++;
	}
	
	private void showStandings()
	{
		for(Nation n : nations)
		{
			n.printStandings();
		}
	}
	
	private void drawWarEvent()
	{
		Nation protagonist = null;
		
		protagonist = nations.get(turnCount % nations.size());
		
		Place wanted = protagonist.getPlans();
		
		if(wanted == null)
		{
			System.out.println(protagonist.getQuickDescriptor() + " is peacefull (or forced to be).");
			protagonist.changeScore(1);
			return;
		}	


		WarEvent w = new WarEvent(wanted, protagonist);

		if(w.getSuccess())
		{
			switchProperty(wanted, protagonist);
		}
			

		System.out.println(w.getStory());


	}
	
	
	private void switchProperty(Place place, Nation dest)
	{
		Nation src = place.owner;
		place.owner = dest;
		src.getPlaces().remove(place);
		dest.getPlaces().add(place);

		int debugSavedID = -1;
		String debugSavedName = "";
		
		terrainManager.repaintZone(place.lands, dest.getID());		
		display.repaintPlace(place, src);
		
		ArrayList<Nation> toRefresh = new ArrayList<>();

		if(src.getPlaces().size() == 0)
		{
			this.nations.remove(src);
			debugSavedID = src.getID();
			debugSavedName = src.name;
		}
		else
		{
			toRefresh.add(src);
		}

		toRefresh.add(dest);
		for(Place p : place.neighbours)
		{
			if(!toRefresh.contains(p.owner))
			{
				toRefresh.add(p.owner);
			}
		}
		
		for(Nation i : toRefresh)
		{
			if(i == null)
			{
				System.err.println("Failed to destroy " + debugSavedName + " id " + debugSavedID + " while trying to fetch " + i);
			}
			i.registerNewNeighborhood();
			i.computeNewPlans();
		}
		
	}
	
	public Nation findNationByID(int id)
	{
		for(Nation n : nations)
		{
			if(n.getID() == id)
				return n;
		}
		System.err.println("findNationByID returning NULL " + id);
		return null;
	}


	private void createNations(ArrayList<Zone> zones, HashMap<Integer, ArrayList<Integer>> hashMap)
	{
		nations = new ArrayList<>();
		
		for(Zone z : zones)
		{
			Nation n = new Nation(new ArrayList<Actor>(), NameGenerator.getRandomName(), z.getID());
			nations.add(n);
			n.setPlaces(createPlaces(hashMap.get(z.getID()), z.getID()));
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
			daPlace.owner = findNationByID(ownerId);
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
