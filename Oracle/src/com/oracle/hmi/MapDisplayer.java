package com.oracle.hmi;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.oracle.map.Terrain;
import com.oracle.model.Place;

@SuppressWarnings("serial")
public class MapDisplayer extends JPanel{

	public int map[][];
	private ArrayList<Place> places;
	
	public MapDisplayer(int[][] map, ArrayList<Place> places)
	{
		super();
		
		this.map = map;
		
		this.places = new ArrayList<>(places);
	}
	
	
	
	public void paintComponent(Graphics g)
	{
		for(int j = 0; j < 1000;j++)
		{
			for(int i = 0; i < 1000;i++)
			{
				if(map[j][i] == -1)g.setColor(Color.BLACK);
				else if(map[j][i] == 0 || map[j][i] == Terrain.SEA_CODE)g.setColor(new Color(0x6F,0x91,0xD1));//Seas
				else 
				{
					g.setColor(new Color((int)(Math.pow(map[j][i],8)%255), (int)(Math.pow(map[j][i],6)%255) ,(int)(Math.pow(map[j][i],7)%255)));
				}
				g.fillRect(i, j, 1,1);
			}
		}
				
		for(Place z : places)
		{
			g.setColor(new Color(0,0,0,250));
			if(z.seaAccess)
				g.setColor(Color.WHITE);
			
			for(int[] boundaryPoint : z.boundaries)
			{				
				g.fillRect(boundaryPoint[1], boundaryPoint[0], 1,1);
			}
		}
	}
}
