package com.oracle.hmi;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.oracle.utils.Callback;

@SuppressWarnings("serial")
public class Window extends JFrame
{
	private JPanel container = new JPanel();
	private MapDisplayer sim;
	
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

	
	public void registerListener(Callback cb)
	{
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
					repaint();
				}
			}
		});
	}
}
