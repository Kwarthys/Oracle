package com.oracle.map;

import java.util.ArrayList;
import java.util.Date;

import com.oracle.map.model.Zone;
import com.oracle.map.utils.ImprovedNoise;

public class Terrain {
	
	int map2[][], map1[][], width, height;
	
	private double seed;

	private ArrayList<Zone> allZones = new ArrayList<>();
	public ArrayList<Zone> getAllZones(){return allZones;}
	
	private ArrayList<Zone> noVoisins = new ArrayList<>();
	public ArrayList<Zone> getNoVoisins(){return noVoisins;}
	
	public Terrain(int pwidth, int pheight, double pSeed)
	{
		width = pwidth; height = pheight;
		
		seed = pSeed;
		
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

		System.out.println("Zones Created in " + (new Date().getTime() - startZones.getTime()) + "ms\n Merging Zones...");

		Date startMerge = new Date();
		while(allZones.size() > 45)
			mergeZones();

		System.out.println("Zones merged in " + (new Date().getTime() - startMerge.getTime()) + "ms.");
		System.out.println("OverAll generation in " + (new Date().getTime() - startTime.getTime()) + "ms.");
		
		return map1;
	}

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
	
	private ArrayList<int[]> Voisins(int j, int i, int comparator)
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
		int stateID = 1;
		for(int j = 0; j < height ; j++)
		{
			for (int i = 0; i < width; i++)
			{
				if(map1[j][i] == 1 && map2[j][i] == -1)
				{
					allZones.add(new Zone(stateID++));
					//System.out.println("new Zone " + stateID);
					stateBuilding(allZones.get(allZones.size()-1), j, i);
				}
				else if(map1[j][i] == 0)map2[j][i] = 0;
			}
		}
		applyMap();
	}
	
	private void stateBuilding(Zone s,int j, int i)
	{
		map2[j][i] = s.addToZone(j,i);
		//System.out.println("Zone ID : " + s.ID());
		if(s.getLands().size() > 1000)return;
		
		boolean isFrontier = false;
		
		ArrayList<int[]> voisins = Voisins(j,i,1);
		if(voisins.size() < 4)isFrontier = true;
		while(voisins.size()>0)
		{
			int[] c = voisins.remove((int)(Math.random()*voisins.size()));
			//System.out.println(c[0] + " " + c[1]);
			if(map1[c[0]][c[1]] == 1) // if terrain is land
			{
				if(map2[c[0]][c[1]] == -1) // if land has not yet been stated
				{
					//System.out.println("Coming from : " + j + "|" + i + " to " + c[0] + "|" + c[1]);
					stateBuilding(s, c[0],c[1]);
				}
				else if(map2[c[0]][c[1]] != s.getID()) // if land is another state
				{
					isFrontier = true;
				}
			}

		}
		
		if(isFrontier)
			s.addToBoundaries(j, i);
	}
	
	private void mergeZones()
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
		
		for(Zone s : allZones)
		{
			if(s != smallerZone) //state not checking boundaries with himslef
			{
				for(int[] bSmaller : smallerZone.getBoundaries())
				{
					if(Voisins(bSmaller[0],bSmaller[1], s.getID()).size() != 0)
					{
						smallerZone.addNeighbour(s);
					}
				}
			}
		}
		if(smallerZone.getNeighbours().size() == 0)
		{
			smallIslandMerge(smallerZone);
			return;
		}
		Zone otherZone;
		if(allZones.size()>2000)
		{
			smallerIndex = 0;
			smallerSize = smallerZone.getNeighbours().get(0).size();
			for(int i = 0; i < smallerZone.getNeighbours().size(); i++)
			{
				if(smallerSize > smallerZone.getNeighbours().get(i).size());
				{
					smallerSize = smallerZone.getNeighbours().get(i).size();
					smallerIndex = i;
				}
			}
			
			otherZone = smallerZone.getNeighbours().get(smallerIndex);
		}
		else
		{
			ArrayList<Integer> count = new ArrayList<>(); 
			for(int i = 0; i < smallerZone.getNeighbours().size(); i++)
			{
				count.add(i,0);
				for(int[] c : smallerZone.getBoundaries())
				{
					if(Voisins(c[0], c[1], smallerZone.getNeighbours().get(i).getID()).size() != 0)
					{
						count.set(i,count.get(i)+1);
					}
				}
			}
			smallerIndex = 0;
			int biggerBoundaries = count.get(0);
			for(int i = 0; i < count.size(); i++)
			{
				if(biggerBoundaries < count.get(i))
				{
					smallerIndex = i;
					biggerBoundaries = count.get(i);
				}
			}
			
			otherZone = smallerZone.getNeighbours().get(smallerIndex);
		}
		
		stateFusion(smallerZone,otherZone);
		
	}
	
	private void smallIslandMerge(Zone s)
	{
		double smallerDistance = Float.POSITIVE_INFINITY;
		Zone closestZone = s;
		for(Zone n : allZones)
		{
			if(s!=n)
			{
				for(int[] p1 : s.getBoundaries())
				{
					for(int[] p2 : n.getBoundaries())
					{
						if(Distance(p1,p2) < smallerDistance)
						{
							if(n!=closestZone)closestZone = n;
							smallerDistance = Distance(p1,p2);
						}
					}
				}
			}
		}
		
		stateFusion(s, closestZone);
	}
	
	private void stateFusion(Zone s1, Zone s2)
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

		for(int[] c : biggerIDZone.getLands())
		{
			map1[c[0]][c[1]] = lowerIDZone.getID();
			lowerIDZone.addToZone(c[0],c[1]);
		}
		
		computeBoundaries(lowerIDZone);
		
	}
	
	private void computeBoundaries(Zone s)
	{
		s.resetBoundaries();
		for(int[] c : s.getLands())
		{
			if(Voisins(c[0], c[1], s.getID()).size() < 4 )
			{
				s.addToBoundaries(c);
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
