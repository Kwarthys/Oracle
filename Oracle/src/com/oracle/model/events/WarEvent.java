package com.oracle.model.events;

import com.oracle.model.HistoricEvent;
import com.oracle.model.Place;
import com.oracle.utils.NationFinder;

public class WarEvent extends HistoricEvent
{
	//private final String[] adverbs = {"gloriously", "awfully"}; //We must have two separate lists for attackers and defenders
	
	private NationFinder nationFinder;
	private Place attacked;
	
	private int attackerID;
	private int defenderID;
	
	private boolean success;
	
	//private String adverb;
	
	public WarEvent(NationFinder finder, Place attacked, int attackerID)
	{
		this.nationFinder = finder;
		this.attacked = attacked;
		this.attackerID = attackerID;
		
		this.defenderID = attacked.owner;
		
		resolveCombat();
	}
	
	private void resolveCombat()
	{
		double attackerScore = Math.pow(nationFinder.getNationById(attackerID).getScore(), 1); //score raised to a certain power to tweak the softmax-ish rand
		double defenderScore = Math.pow(nationFinder.getNationById(defenderID).getScore(), 1);
		
		double r = Math.random() * (attackerScore + defenderScore);
		
		this.success = r < attackerScore;
		
		if(this.success) // Attacker won, defender lose points
		{
			double defenderLoss = 2 * (attackerScore - r) / attackerScore;
			nationFinder.getNationById(defenderID).changeScore(-defenderLoss);
			
			nationFinder.getNationById(attackerID).changeScore(attacked.landValue);
			
			//this.adverb = adverbs[(int)(defenderLoss / 2 * (adverbs.length-1))];
		}
		else
		{
			double attackerLoss = 2 * (r - attackerScore) / defenderScore;
			nationFinder.getNationById(attackerID).changeScore(-attackerLoss);
			
			//this.adverb = adverbs[(int)(attackerLoss / 2 * (adverbs.length-1))];
		}
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
		sb.append("(");
		sb.append(String.format("%.1f", nationFinder.getNationById(attackerID).getScore()));
		sb.append(")");
		sb.append(" attacks ");
		sb.append(attacked.name);
		sb.append("(");
		sb.append(String.format("%.2f", attacked.landValue));
		sb.append(") of Nation ");
		sb.append(nationFinder.getNationById(defenderID).name);
		sb.append("(");
		sb.append(String.format("%.1f", nationFinder.getNationById(defenderID).getScore()));
		sb.append(")");
		
		if(success)
		{
			sb.append(" and");
			//sb.append(this.adverb);
			sb.append(" wins. Nation ");
			sb.append(nationFinder.getNationById(attackerID).name);
			sb.append(" is now the new master of ");
			sb.append(attacked.name);
			sb.append(".");
		}
		else
		{
			sb.append(" and");
			//sb.append(this.adverb);
			sb.append(" fails.");
		}
		
		
		return sb.toString();
	}
}
