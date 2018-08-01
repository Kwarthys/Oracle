package com.oracle.hmi;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.oracle.map.Terrain;
import com.oracle.map.model.Zone;

@SuppressWarnings("serial")
public class MapDisplayer extends JPanel{

	private int map[][];
	
	private Terrain terrainGenerator;
	
	public MapDisplayer()
	{
		super();
		
		terrainGenerator = new Terrain(1000,1000, Math.random()*10);
		
		map = terrainGenerator.generateMap();
	}
	
	
	
	public void paintComponent(Graphics g)
	{		
		int stateMaxID = 0;
		for(int i = 0; i < terrainGenerator.getAllZones().size();i++)
			stateMaxID = Math.max(stateMaxID, terrainGenerator.getAllZones().get(i).getID());
		
		for(int j = 0; j < 1000;j++)
		{
			for(int i = 0; i < 1000;i++)
			{
				if(map[j][i] == -1)g.setColor(Color.BLACK);
				else if(map[j][i] == 0)g.setColor(new Color(0x6F,0x91,0xD1));//Seas
				else 
				{
					g.setColor(new Color(150+(int)(Math.pow(map[j][i],8)%105), (int)(Math.pow(map[j][i],6)%156) ,10+(int)(Math.pow(map[j][i],7)%150)));
				}
				
				g.fillRect(i, j, 1,1);
				
				//g.setColor(Color.WHITE);
				//g.drawRect(150, 150, 700, 700);
			}
		}
		System.out.println("Nb of Zones : " + terrainGenerator.getAllZones().size());
		g.setColor(Color.BLACK);
		for(Zone s : terrainGenerator.getAllZones())
		{
			for(int[] c : s.getBoundaries())
			{
				g.fillRect(c[1], c[0], 1,1);
			}
		}
		
		
	}
}
