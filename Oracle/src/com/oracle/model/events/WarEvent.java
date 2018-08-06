package com.oracle.model.events;

import com.oracle.model.HistoricEvent;
import com.oracle.model.Place;
import com.oracle.utils.NationFinder;

public class WarEvent extends HistoricEvent
{
	private NationFinder nationFinder;
	private Place attacked;
	
	private int attackerID;
	private int defenderID;
	
	private boolean success;
	
	public WarEvent(NationFinder finder, Place attacked, int attackerID)
	{
		this.nationFinder = finder;
		this.attacked = attacked;
		this.attackerID = attackerID;
		
		this.defenderID = attacked.owner;
		
		this.success = Math.random() > 0.5;
	}
	
	public boolean getSuccess()
	{
		return success;
	}

	@Override
	public String getStory()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("Nation ");
		sb.append(nationFinder.getNationById(attackerID).name);
		sb.append(" attacks ");
		sb.append(attacked.name);
		sb.append(" of Nation ");
		sb.append(nationFinder.getNationById(defenderID).name);
		
		if(success)
		{
			sb.append(" and wins. Nation ");
			sb.append(nationFinder.getNationById(attackerID).name);
			sb.append(" is now the new master of ");
			sb.append(attacked.name);
			sb.append(".");
		}
		else
		{
			sb.append(" and fails.");
		}
		
		
		return sb.toString();
	}
}
