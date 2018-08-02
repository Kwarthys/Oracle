package com.oracle.map;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.oracle.map.utils.ImprovedNoise;
import com.oracle.model.Zone;

public class Terrain {
	
	int map2[][], map1[][], width, height;
	
	private double seed;
	
	private final int ZONE_SIZE_INIT = 500;

	private ArrayList<Zone> allZones = new ArrayList<>();
	public ArrayList<Zone> getAllZones(){return allZones;}
	
	private ArrayList<Zone> places;
	
	private ArrayList<Zone> noVoisins = new ArrayList<>();
	public ArrayList<Zone> getNoVoisins(){return noVoisins;}
	
	private HashMap<Integer, ArrayList<Zone>> zonesLands;
	
	public Terrain(int width, int height, double Seed)
	{
		this.width = width;
		this.height = height;
		
		this.seed = Seed;
		
		map1 = new int[height][width];
		map2 = new int[height][width];
		
		for(int j = 0; j < height;j++)
		{
			for(int i = 0; i < width;i++)
			{
				map1[j][i] = 0;
				map2[j][i] = -1;
			}
		}
	}
	
	public int[][] generateMap()
	{
		Date startTime = new Date();
		System.out.println("Generating...");
		generate();
		System.out.println("Generated in " + (new Date().getTime() - startTime.getTime()) + "ms\nCreating Zones...");
		
		Date startZones = new Date();
		drawZones();		
		System.out.println("Cleaning too small zones ....");		
		cleanSmallZones();
		
		places = new ArrayList<>(allZones);


		System.out.println("Zones Created in " + (new Date().getTime() - startZones.getTime()) + "ms\n Merging Zones...");

		Date startMerge = new Date();
		this.zonesLands = mergeForNations(10);

		System.out.println("Zones merged in " + (new Date().getTime() - startMerge.getTime()) + "ms.");
		
		
		for(Zone z : allZones)
			computeNeighbours(z);
		
		System.out.println("OverAll generation in " + (new Date().getTime() - startTime.getTime()) + "ms.");
		
		return map1;
	}

	public ArrayList<Zone> getAtomicPlaces(){return places;}
	public HashMap<Integer, ArrayList<Zone>> getNationsOfPlaces(){return zonesLands;}

	private void generate()
	{
		for(int j = 0; j < height;j++)
		{
			for(int i = 0; i < width;i++)
			{
				double noise = 0;
				
				if(i > 850 || i < 150)
				{
					if(i>850)noise-= (150 - (1000-i));
					else noise-=(150-i);
				}
				if( j > 850 || j<150)
				{
					if(j>850)noise-= (150 - (1000-j));
					else noise-=(150-j);
				}
				
				double x = (double)i;x/=200;
				double y = (double)j;y/=200;
				noise += (int)(127*ImprovedNoise.noise(x,y,seed));
				noise += (int)(100*ImprovedNoise.noise(x+10,y+10,seed));
				noise += (int)(80*ImprovedNoise.noise(x*2,y*2,seed));
				noise += (int)(5*ImprovedNoise.noise(x*50,y*50,seed));
				noise += (int)(5*ImprovedNoise.noise(x*100,y*100,seed));
				if(noise>0)
					map1[j][i] = 1;
				else
					map1[j][i] = 0;
				
			}
		}
	}
	
	private void applyMap()
	{
		for(int j = 0; j < height;j++)
		{
			for(int i = 0; i < width;i++)
			{
				map1[j][i] = map2[j][i];
				map2[j][i] = -1;
			}
		}
	}
	
	private ArrayList<int[]> getVoisins(int j, int i, int comparator)
	{
		ArrayList<int[]> leReturn = new ArrayList<int[]>();
		
		if(map1[j-1][i] == comparator)//DESSUS
		{
			int[] tmp = {j-1,i};
			leReturn.add(tmp);
		}
		
		if(map1[j+1][i] == comparator)//DESSOUS
		{
			int[] tmp = {j+1,i};
			leReturn.add(tmp);
		}
		
		if(map1[j][i+1] == comparator)//DROITE
		{
			int[] tmp = {j,i+1};
			leReturn.add(tmp);
		}
		
		if(map1[j][i-1] == comparator)//GAUCHE
		{
			int[] tmp = {j,i-1};
			leReturn.add(tmp);
		}
		
		return leReturn;
	}
	
	private void drawZones()
	{
		int zoneID = 1;
		for(int j = 0; j < height ; j++)
		{
			for (int i = 0; i < width; i++)
			{
				if(map1[j][i] == 1 && map2[j][i] == -1)
				{
					Zone newZone = new Zone(zoneID++);
					allZones.add(newZone);
					//System.out.println("new Zone " + stateID);
					zoneBuilding(newZone, j, i);
				}
				else if(map1[j][i] == 0)map2[j][i] = 0;
			}
		}
		
		applyMap();
		
		for(Zone z : allZones)
		{
			computeBoundaries(z);
		}
	}
	
	
	private void zoneBuilding(Zone toBuild, int firstj, int firsti)
	{		
		int j = firstj;
		int i = firsti;
		
		ArrayList<int[]> toVisit = new ArrayList<>();		
		
		while(toBuild.size() < ZONE_SIZE_INIT)
		{
			ArrayList<int[]> voisins = getVoisins(j,i,1);
			
			map2[j][i] = toBuild.addToZone(j, i);
			
			while(voisins.size()>0)
			{
				int[] v = voisins.remove((int)(Math.random()*voisins.size()));
				
				if(map1[v[0]][v[1]] == 1) // if terrain is land
				{
					if(map2[v[0]][v[1]] == -1) // if land has not yet been stated
					{
						if(toVisit.size() < ZONE_SIZE_INIT)
						{
							boolean gotIt = false;
							for(int vi = 0; vi < toVisit.size() && !gotIt; vi++)
							{
								if(v[0] == toVisit.get(vi)[0] && v[1] == toVisit.get(vi)[1])
									gotIt = true;
							}
							if(!gotIt)
								toVisit.add(v);
						}
					}
				}
			}
			
			if(toVisit.size() == 0) return;			
			
			int[] point = toVisit.remove(Math.random() < 0.5 ? 0 : toVisit.size()-1);
			j = point[0];
			i = point[1];
		}
	}
	
	
	private void cleanSmallZones()
	{
		boolean found;
		do
		{
			found = false;
			for(int i = 0; i < allZones.size() && !found; i++)
			{
				if(allZones.get(i).size() < ZONE_SIZE_INIT)
				{
					found = true;
				}
			}
			if(found)
			{
				zoneFusion(zonesToMerge());
			}
		}while(found);
		
		for(Zone z : allZones)
			computeNeighbours(z);
	}
	
	private Zone[] zonesToMerge()
	{
		int smallerIndex = -1;
		int smallerSize = (int)Float.POSITIVE_INFINITY;
		for(int i = 0; i < allZones.size(); i++)
		{
			if(smallerSize > allZones.get(i).size() && !noVoisins.contains(allZones.get(i)))
			{
				smallerSize = allZones.get(i).size();
				smallerIndex = i;
			}
		}
		
		Zone smallerZone = allZones.get(smallerIndex);
		computeNeighbours(smallerZone);
		
		if(smallerZone.getNeighbours().size() == 0)
		{
			Zone closest = findClosestLand(smallerZone);
			if(closest.getID() == smallerZone.getID())
				return null;
			Zone[] tmp = {smallerZone, closest}; 
			return tmp;
		}
		
		
		Zone otherZone;
		if(Math.random() < -2)
		{
			otherZone = mergeZonesSize(smallerZone);
		}
		else
		{
			otherZone = mergeZonesBoundaries(smallerZone);
		}
		
		Zone[] tmp = {smallerZone, otherZone};
		if(otherZone.getID() == smallerZone.getID())
			return null;
		return tmp;
		
	}
	
	private Zone mergeZonesSize(Zone z)
	{
		int smallerIndex = 0;
		int smallerSize = z.getNeighbours().get(0).size();
		for(int i = 0; i < z.getNeighbours().size(); i++)
		{
			if(smallerSize > z.getNeighbours().get(i).size());
			{
				smallerSize = z.getNeighbours().get(i).size();
				smallerIndex = i;
			}
		}
		return z.getNeighbours().get(smallerIndex);
	}
	
	private Zone mergeZonesBoundaries(Zone z)
	{
		HashMap<Integer, Integer> count = new HashMap<>(); 
		for(int i = 0; i < z.getNeighbours().size(); i++)
		{
			count.put(i,0);
			
			for(int[] c : z.getBoundaries())
			{
				if(getVoisins(c[0], c[1], z.getNeighbours().get(i).getID()).size() != 0)
				{
					count.put(i,count.get(i)+1);
				}
			}
		}
		int maxBoundariesIndex = 0;
		int biggerBoundaries = 0;
		
		for(HashMap.Entry<Integer, Integer> entry : count.entrySet())
		{			
			if(entry.getValue() > biggerBoundaries)
			{
				biggerBoundaries = entry.getValue();
				maxBoundariesIndex = entry.getKey();
			}		   
		}

		
		return z.getNeighbours().get(maxBoundariesIndex);
	}

	private HashMap<Integer, ArrayList<Zone>> mergeForNations(int numberOfNationsWanted)
	{
		HashMap<Integer, ArrayList<Zone>> countries = new HashMap<>();
		
		for(Zone z : allZones)
		{
			ArrayList<Zone> tmp = new ArrayList<>();
			tmp.add(z);
			countries.put(z.getID(), tmp);
		}
		
		while(countries.size() >= numberOfNationsWanted)
		{
			Zone[] toMerge = zonesToMerge();
			Zone lowerIDZone = Math.min(toMerge[0].getID(), toMerge[1].getID()) == toMerge[0].getID() ? toMerge[0] : toMerge[1];
			Zone higherIDZone = Math.max(toMerge[0].getID(), toMerge[1].getID()) == toMerge[0].getID() ? toMerge[0] : toMerge[1];
			
			/** add place to country **/
			ArrayList<Zone> shiftingLands = countries.get(higherIDZone.getID());
			ArrayList<Zone> keptlands = new ArrayList<>(countries.get(lowerIDZone.getID()));
			keptlands.addAll(shiftingLands);
			
			countries.put(lowerIDZone.getID(), keptlands);
			
			countries.remove(higherIDZone.getID());
			
			/** repainting the map **/
			zoneFusion(toMerge);
		}
		
		return countries;		
	}
		
	private Zone findClosestLand(Zone s)
	{
		double smallerDistance = Float.POSITIVE_INFINITY;
		Zone closestZone = null;
		for(Zone n : allZones)
		{
			if(n!=s)
			{
				for(int[] p1 : s.getBoundaries())
				{
					for(int[] p2 : n.getBoundaries())
					{
						if(Distance(p1,p2) < smallerDistance)
						{
							closestZone = n;
							smallerDistance = Distance(p1,p2);
						}
					}
				}
			}
		}
		return closestZone;
	}
	
	private void zoneFusion(Zone[] zones)
	{
		if(zones.length >= 1)
			zoneFusion(zones[0], zones[1]);
	}
	
	private void zoneFusion(Zone s1, Zone s2)
	{
		Zone biggerIDZone, lowerIDZone;
		if(s1.getID()>s2.getID())
		{
			biggerIDZone = s1;
			lowerIDZone = s2;
		}
		else
		{
			biggerIDZone = s2;
			lowerIDZone = s1;
		}
	
		allZones.remove(biggerIDZone);
		
		for(int i = 0; i < biggerIDZone.getLands().size(); i++)
		{
			int[] c = biggerIDZone.getLands().get(i);
			map1[c[0]][c[1]] = lowerIDZone.getID();
			lowerIDZone.addToZone(c[0],c[1]);
		}

		/*
		for(int[] c : biggerIDZone.getLands())
		{
			map1[c[0]][c[1]] = lowerIDZone.getID();
			lowerIDZone.addToZone(c[0],c[1]);
		}
		*/
		
		computeBoundaries(lowerIDZone);
		
	}
	
	private void computeBoundaries(Zone s)
	{		
		s.resetBoundaries();
		s.resetNeighbours();
		for(int[] c : s.getLands())
		{
			if(getVoisins(c[0], c[1], s.getID()).size() < 4 )
			{
				s.addToBoundaries(c);
			}
		}
	}
	
	private void computeNeighbours(Zone s)
	{
		s.resetNeighbours();
		for(Zone z : allZones)
		{
			if(z != s)
			{
				for(int[] p : s.getBoundaries())
				{
					if(getVoisins(p[0],p[1], z.getID()).size() != 0 && !s.getNeighbours().contains(z))
					{
						s.addNeighbour(z);
					}	
				}
			}
		}
	}
	
	static public double Distance(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	
	static public double Distance(int[] p1, int[] p2)
	{
		return Distance(p1[0], p1[1], p2[0], p2[1]);
	}
}
