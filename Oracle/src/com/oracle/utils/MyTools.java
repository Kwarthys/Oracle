package com.oracle.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.oracle.model.Zone;

public class MyTools{

	public static ArrayList<int[]> arrayCopy(ArrayList<int[]> array)
	{
		ArrayList<int[]> a = new ArrayList<>();
		for(int[] point : array)
		{		
			int[] tmp = new int[point.length];
			System.arraycopy(point, 0, tmp, 0, point.length);
			a.add(tmp);
		}		
		return a;
	}


	public static void debugZone(ArrayList<Zone> zones)
	{
		HashMap<Integer, Integer> debug = new HashMap<>();
		for(Zone z : zones)
		{
			int index = (int)(z.getLands().size()/100);
			debug.put(index, debug.containsKey(index) ? index + 1 : 1);
		}

		System.out.print("ZONE DEBUG : ");

		debug.forEach((k,v) ->
		{
			System.out.print(k*100 + ":" + v + " ");
		});
		System.out.println(" ");
	}
}
