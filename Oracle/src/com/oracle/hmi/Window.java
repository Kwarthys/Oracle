package com.oracle.hmi;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Window extends JFrame
{
	private JPanel container = new JPanel();
	private MapDisplayer sim;
	
	public Window()
	{	
		setTitle("Risk Project");		
		
		container.setLayout(new BorderLayout());
		
		setSize(1050,1050);
		sim = new MapDisplayer();
		container.add(sim, BorderLayout.CENTER);
			
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
			//System.out.println("�a tourne");
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
}