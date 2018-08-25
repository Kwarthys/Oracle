package com.oracle.hmi;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.oracle.utils.Callback;

@SuppressWarnings("serial")
public class Window extends JFrame
{
	private JPanel container = new JPanel();
	private MapDisplayer sim;

	private int pressedX;
	private int pressedY;
	
	public Window(MapDisplayer sim)
	{	
		setTitle("Risk Project");
		
		this.sim = sim;
		
		container.setLayout(new BorderLayout());
		
		setSize(1050,1050);
		container.add(this.sim, BorderLayout.CENTER);
			
		this.setContentPane(container);
		setVisible(true);	
			
		//System.out.println("lancement !");
		
		setLocationRelativeTo(null);		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//go();
		//sim.start(this);
	}
		
	/*
	private void go()
	{
		boolean go = true;
		while(go)
		{
			//System.out.println("ça tourne");
			repaint();
			try{
				Thread.sleep(20);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			
			go = false;
		}
	}
	*/

	
	public void registerListener(Callback cb, Callback scb)
	{
		this.addMouseWheelListener(new MouseWheelListener() {			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
			       if (e.getWheelRotation() < 0)
			       {
			    	   if(!sim.zoomed)
			    	   {
				    	   sim.zoomed = true;
						sim.updateGraphics();
				    	   System.out.println("Zooming");
			    	   }
			       }
			       else
			       {
			    	   if(sim.zoomed)
			    	   {
				    	   sim.zoomed = false;
				    	   sim.updateGraphics();
				    	   System.out.println("Zooming out");
			    	   }
			       }				
			}			
		});
		
		
		this.addMouseListener(new MouseListener() {			
			@Override
			public void mouseReleased(MouseEvent e) {}			
			@Override
			public void mousePressed(MouseEvent e) {
				pressedX = e.getX(); pressedY = e.getY();				
			}			
			@Override
			public void mouseExited(MouseEvent e) {}			
			@Override
			public void mouseEntered(MouseEvent e) {}			
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {			
			@Override
			public void mouseMoved(MouseEvent e) {}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				sim.xOffset -= pressedX-e.getX();
				sim.yOffset -= pressedY-e.getY();
				pressedX = e.getX(); pressedY = e.getY();
				repaint();
			}
		});
		
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					cb.callback();
				}
				else if(e.getKeyCode() == KeyEvent.VK_S)
				{
					scb.callback();
				}
			}
		});
	}
}
