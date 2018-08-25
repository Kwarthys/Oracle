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

import com.oracle.model.Nation;
import com.oracle.model.Place;

@SuppressWarnings("serial")
public class MapDisplayer extends JPanel{

	private BufferedImage image;
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

		this.nations = new ArrayList<>(nations);
		
		updateGraphics();
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

		int zoomCoef = this.zoomed ? 3 : 1;

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
			g.setStroke(defaultStroke);
			paintNation(n, g);
		}


		g.dispose();

		repaint();
	}

	private void paintNation(Nation n, Graphics2D g)
	{
		Stroke countryNameStroke = new BasicStroke(2f);
		FontRenderContext frc = g.getFontRenderContext();
		
		int zoomCoef = this.zoomed ? 3 : 1;
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
	}



	public void repaintPlace(Place place, Nation oldNation)
	{
		paintNation(oldNation, (Graphics2D)image.getGraphics());
		paintNation(place.owner, (Graphics2D)image.getGraphics());
		repaint();
	}
}
