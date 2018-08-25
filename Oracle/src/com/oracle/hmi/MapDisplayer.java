package com.oracle.hmi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.oracle.map.Terrain;
import com.oracle.model.Nation;
import com.oracle.model.Place;

@SuppressWarnings("serial")
public class MapDisplayer extends JPanel{
	
	private static final int zoomAmount = 3;

	private BufferedImage image;

	private BufferedImage waterLayer;
	private BufferedImage zoomedWaterLayer;
	
	private ArrayList<Nation> nations;
	

	private int[][] map;

	/** Camera **/
	public boolean zoomed = false;
	public int xOffset = 0;
	public int yOffset = 0;

	public MapDisplayer(int[][] map, ArrayList<Nation> nations)
	{
		super();

		this.map = map;
		
		createWaterLayers();

		this.nations = nations;
		
		updateGraphics();
	}
	
	private void createWaterLayers()
	{
		int height = map.length;
		int width = map[0].length;

		this.waterLayer = new BufferedImage(width, height,  BufferedImage.TYPE_4BYTE_ABGR);
		this.zoomedWaterLayer = new BufferedImage(width * zoomAmount, height * zoomAmount,  BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics2D g = (Graphics2D) waterLayer.getGraphics();
		Graphics2D zg = (Graphics2D) zoomedWaterLayer.getGraphics();
		
		for(int j = 0; j < map.length; j++)
		{
			for(int i = 0; i < map[0].length; i++)
			{
				Color color;
				
				if(map[j][i] == Terrain.SEA_CODE || map[j][i] == Terrain.WATER_CODE)
				{
					color = Color.CYAN;
				}
				else
				{
					color = new Color(0,0,0,0); // transparent
				}

				g.setColor(color);
				zg.setColor(color);
				
				g.fillRect(i, j, 1, 1);
				zg.fillRect(i*zoomAmount, j*zoomAmount, zoomAmount, zoomAmount);
			}
		}
	}



	public void paintComponent(Graphics gd)
	{
		Graphics2D g = (Graphics2D)gd;

		this.xOffset = Math.min(0, Math.max(this.xOffset, -2000));
		this.yOffset = Math.min(0, Math.max(this.yOffset, -2000));

		int xOffset = this.zoomed ? this.xOffset : 0;
		int yOffset = this.zoomed ? this.yOffset : 0;

		g.drawImage(image, null, xOffset, yOffset);		
	}


	public void updateGraphics()
	{
		int height = map.length;
		int width = map[0].length;

		int zoomCoef = this.zoomed ? zoomAmount : 1;

		this.image = new BufferedImage(width * zoomCoef, height * zoomCoef, BufferedImage.TYPE_3BYTE_BGR);

		Graphics2D g = (Graphics2D) image.getGraphics();
		
		g.setColor(Color.cyan);
		g.fillRect(0, 0, width * zoomCoef, height * zoomCoef);
		
		g.setFont(new Font("Arial", Font.PLAIN, 18));

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		Stroke defaultStroke = g.getStroke();

		for(Nation n : nations)
		{
			paintNation(n, g);
			g.setStroke(defaultStroke);
		}

		paintTheNames();


		g.dispose();

		repaint();
	}

	private void paintNation(Nation n, Graphics2D g)
	{		
		int zoomCoef = this.zoomed ? zoomAmount : 1;
		
		int i = n.getID();

		Color countryColor = new Color((int)(Math.pow(i,8)%255), (int)(Math.pow(i,6)%255) ,(int)(Math.pow(i,7)%255));

		for(Place p : n.getPlaces())
		{
			g.setColor(countryColor);
			for(int[] land : p.lands)
			{
				g.fillRect(land[1] * zoomCoef, land[0] * zoomCoef, zoomCoef, zoomCoef);
			}

			g.setColor(new Color(0,0,0,50));
			if(p.seaAccess)
				g.setColor(new Color(250,250,250,50));

			for(int[] boundaryPoint : p.boundaries)
			{				
				g.fillRect(boundaryPoint[1] * zoomCoef, boundaryPoint[0] * zoomCoef, zoomCoef, zoomCoef);
			}
		}		
	}
	
	private void paintTheNames()
	{
		Stroke countryNameStroke = new BasicStroke(2f);
		int zoomCoef = this.zoomed ? zoomAmount : 1;
		
		Graphics2D g = (Graphics2D) image.getGraphics();
		FontRenderContext frc = g.getFontRenderContext();
		Stroke defaultStroke = g.getStroke();
		
		for(Nation n : nations)
		{
			int placeCount = 0;
			
			int[] countryCenter = {-1,-1};
			
			int i = n.getID();

			Color countryColor = new Color((int)(Math.pow(i,8)%255), (int)(Math.pow(i,6)%255) ,(int)(Math.pow(i,7)%255));
			boolean isTooDark = (countryColor.getRed() + countryColor.getBlue() + countryColor.getGreen()) / 3 < 30;

			Color nameFillColor;

			if(isTooDark)
			{
				nameFillColor = Color.white;
			}
			else
			{
				nameFillColor = Color.BLACK;
			}
			
			for(Place p : n.getPlaces())
			{			
				int[] center = {-1, -1};
				if(zoomed)
				{				
					int pointCount = 0;
					
					for(int[] land : p.lands)
					{
						if(center[0] == -1)//init
						{
							center[0] = land[0];
							center[1] = land[1];
						}
						else
						{
							center[0] += land[0];
							center[1] += land[1];
						}
						pointCount++;
					}

					center[0] = center[0] / pointCount;
					center[1] = center[1] / pointCount;
					
					g.setColor(nameFillColor);
					g.drawString(p.name, (center[1] - (int)(p.name.length() * 1.5)) * zoomCoef, (center[0] + 3) * zoomCoef);
				}
				else
				{
					center[0] = p.lands.get(0)[0];
					center[1] = p.lands.get(0)[1];
				}
				
				placeCount++;
				
				if(countryCenter[0] == -1)//init
				{
					countryCenter[0] = center[0];
					countryCenter[1] = center[1];
				}
				else
				{
					countryCenter[0] += center[0];
					countryCenter[1] += center[1];
				}
			}
			
			g.setStroke(countryNameStroke);

			countryCenter[0] = countryCenter[0] / placeCount;
			countryCenter[1] = countryCenter[1] / placeCount;

			countryCenter[1] -= n.name.length() * 10;
			TextLayout textTl = new TextLayout(n.name, new Font("Arial", Font.BOLD, 35 * zoomCoef), frc);
			Shape outline = textTl.getOutline(null);

			g.translate(countryCenter[1] * zoomCoef, countryCenter[0] * zoomCoef);
			g.setColor(nameFillColor);
			g.fill(outline);
			g.setColor(countryColor);
			g.draw(outline);
			g.translate(-(countryCenter[1] * zoomCoef), -(countryCenter[0] * zoomCoef));
			
			g.setStroke(defaultStroke);
		}
	}



	public void repaintPlace(Place place, Nation oldNation)
	{
		ArrayList<Nation> toRepaint = new ArrayList<>();
		if(nations.contains(oldNation))
		{
			toRepaint.add(oldNation);
			for(Nation n : oldNation.getNeighbours())
			{
				toRepaint.add(n);
			}
		}
		
		toRepaint.add(place.owner);
		
		for(Nation n : place.owner.getNeighbours())
		{
			if(!toRepaint.contains(n))
			{
				toRepaint.add(n);
			}
		}
		
		Graphics2D g = (Graphics2D) image.getGraphics();
		
		for(Nation n : toRepaint)
		{
			paintNation(n, g);
		}
		
		applyWaterMask();
		
		paintTheNames();
		
		repaint();
	}

	private void applyWaterMask()
	{
		int xOffset = this.zoomed ? this.xOffset : 0;
		int yOffset = this.zoomed ? this.yOffset : 0;
		
		Graphics2D g = (Graphics2D) image.getGraphics();
		if(this.zoomed)
		{
			g.drawImage(zoomedWaterLayer, null, xOffset, yOffset);	
		}
		else
		{
			g.drawImage(waterLayer, null, xOffset, yOffset);			
		}
	}
}
