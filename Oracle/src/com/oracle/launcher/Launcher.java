package com.oracle.launcher;

import java.util.ArrayList;

import com.oracle.model.Manager;
import com.oracle.utils.MyTools;

public class Launcher {

	public static void main(String[] args)
	{
		new Manager();
		
		/*
		int[] a = {1,2,3,4};
		ArrayList<int[]> aa = new ArrayList<>();
		aa.add(a);
		
		ArrayList<int[]> bb = MyTools.arrayCopy(aa);

		int[] tmp = {8,8,8};
		
		aa.add(tmp);
		
		for(int[] c : bb)
		{
			for(int cc : c)
			{
				System.out.print(cc + " ");			
			}
			System.out.println();
		}
		*/
	}
}
