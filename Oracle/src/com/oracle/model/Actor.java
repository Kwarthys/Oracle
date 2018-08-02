package com.oracle.model;

import com.oracle.utils.generators.NameGenerator;

public class Actor {
	
	public String name;
	public String role;
	
	
	public Actor()
	{
		this.name = NameGenerator.getRandomName();
		this.role = "Actor";
	}
}
