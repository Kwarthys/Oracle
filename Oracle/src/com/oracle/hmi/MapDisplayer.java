package com.oracle.hmi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.oracle.map.Terrain;
import com.oracle.model.Nation;
import com.oracle.model.Place;
import com.oracle.utils.NationFinder;

@SuppressWarnings("serial")
public class MapDisplayer extends JPanel{

	public int map[][];
	private ArrayList<Place> places;
	
	public NationFinder nf = null;
	
	public MapDisplayer(int[][] map, ArrayList<Place> places)
	{
		super();
		
		this.map = map;
		
		this.places = new ArrayList<>(places);
	}
	
	
	
	public void paintComponent(Graphics gd)
	{
		Graphics2D g = (Graphics2D)gd;
		ArrayList<Integer> nationIDs = new ArrayList<>();
		
		for(int j = 0; j < 1000;j++)
		{
			for(int i = 0; i < 1000;i++)
			{
				if(map[j][i] == -1)g.setColor(Color.BLACK);
				else if(map[j][i] == 0 || map[j][i] == Terrain.SEA_CODE)g.setColor(new Color(0x6F,0x91,0xD1));//Seas
				else 
				{
					g.setColor(new Color((int)(Math.pow(map[j][i],8)%255), (int)(Math.pow(map[j][i],6)%255) ,(int)(Math.pow(map[j][i],7)%255)));
					if(!nationIDs.contains(map[j][i]))
					{
						nationIDs.add(map[j][i]);
					}
				}
				g.fillRect(i, j, 1,1);
			}
		}
		
				
		for(Place z : places)
		{
			g.setColor(new Color(0,0,0,50));
			if(z.seaAccess)
				g.setColor(new Color(250,250,250,50));
			
			for(int[] boundaryPoint : z.boundaries)
			{				
				g.fillRect(boundaryPoint[1], boundaryPoint[0], 1,1);
			}
		}
		
		if(nf != null)
		{
			g.setFont(new Font("Arial", Font.PLAIN, 35));
			
	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	        
	        g.setStroke(new BasicStroke(1.3f));
			
			FontRenderContext frc = g.getFontRenderContext();
			
			for(Integer i : nationIDs)
			{
				int pointCount = 0;
				
				Color countryColor = new Color((int)(Math.pow(i,8)%255), (int)(Math.pow(i,6)%255) ,(int)(Math.pow(i,7)%255));
				boolean isTooDark = (countryColor.getRed() + countryColor.getBlue() + countryColor.getGreen()) / 3 < 30;
				
				if(isTooDark)
				{
					g.setColor(Color.white);
				}
				else
				{
					g.setColor(Color.BLACK);
				}
				
				Nation n = nf.getNationById(i);
				int[] center = {-1, -1};
				
				for(Place p : n.getPlaces())
				{
					int[] point = p.boundaries.get(0);
					
					if(center[0] == -1)//init
					{
						center[0] = point[0];
						center[1] = point[1];
					}
					else
					{
						center[0] += point[0];
						center[1] += point[1];
					}
					
					pointCount++;
				}

				center[0] = center[0] / pointCount;
				center[1] = center[1] / pointCount;
				
				center[1] -= n.name.length() * 10;
	            TextLayout textTl = new TextLayout(n.name, g.getFont(), frc);
	            Shape outline = textTl.getOutline(null);
	            
	            g.translate(center[1], center[0]);
	            g.fill(outline);
	            g.setColor(countryColor);
	            g.draw(outline);
	            g.translate(-center[1], -center[0]);
			}
			

            g.dispose();
		}
		
		
	}
}
