package com.oracle.hmi;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.oracle.model.Zone;

@SuppressWarnings("serial")
public class MapDisplayer extends JPanel{

	public int map[][];
	private ArrayList<Zone> places;
	
	public MapDisplayer(int[][] map, ArrayList<Zone> places)
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
				else if(map[j][i] == 0)g.setColor(new Color(0x6F,0x91,0xD1));//Seas
				else 
				{
					g.setColor(new Color((int)(Math.pow(map[j][i],8)%255), (int)(Math.pow(map[j][i],6)%255) ,(int)(Math.pow(map[j][i],7)%255)));
				}
				g.fillRect(i, j, 1,1);
			}
		}
		
		
		g.setColor(new Color(255,255,255,50));
		for(Zone z : places)
		{
			for(int[] boundaryPoint : z.getBoundaries())
			{				
				g.fillRect(boundaryPoint[1], boundaryPoint[0], 1,1);
			}
		}
	}
}
