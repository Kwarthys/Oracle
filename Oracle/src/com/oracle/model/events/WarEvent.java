package com.oracle.model.events;

import com.oracle.manager.Manager;
import com.oracle.model.HistoricEvent;
import com.oracle.model.Nation;
import com.oracle.model.Penalty;
import com.oracle.model.Place;
import com.oracle.utils.NationFinder;

public class WarEvent extends HistoricEvent
{
	//private final String[] adverbs = {"gloriously", "awfully"}; //We must have two separate lists for attackers and defenders
	
	private static final int[] values = {2, 5, 10, 20};
	
	private NationFinder nationFinder;
	private Place attacked;
	
	private int attackerID;
	private int defenderID;
	
	private boolean success;
	
	private boolean attackHasPenalty = false;
	private boolean defenseHasPenalty = false;
	
	private String defenderName;
	
	//private String adverb;
	
	public WarEvent(NationFinder finder, Place attacked, int attackerID)
	{
		this.nationFinder = finder;
		this.attacked = attacked;
		this.attackerID = attackerID;
		
		this.defenderID = attacked.owner;

		this.defenderName = nationFinder.getNationById(defenderID).name;
		
		nationFinder.getNationById(attackerID).refreshPenalties();
		nationFinder.getNationById(defenderID).refreshPenalties();
		
		resolveCombat();
	}
	
	private void resolveCombat()
	{
		double attackerScore = nationFinder.getNationById(attackerID).getScore() - computeModifiers(attackerID);
		double defenderScore = nationFinder.getNationById(defenderID).getScore() - computeModifiers(defenderID);
		
		double r = Math.random() * (attackerScore + defenderScore);
		
		this.success = r < attackerScore;
		
		if(this.success) // Attacker won, defender lose points
		{
			double defenderLoss = 2 * (attackerScore - r) / attackerScore;
			nationFinder.getNationById(defenderID).changeScore(-defenderLoss);
			nationFinder.getNationById(defenderID).addNewPenalty(new Penalty(2, Manager.turnCount + 1));

			nationFinder.getNationById(attackerID).changeScore(attacked.landValue);
			
			//this.adverb = adverbs[(int)(defenderLoss / 2 * (adverbs.length-1))];
		}
		else //Attack failed
		{
			double attackerLoss = 2 * (r - attackerScore) / defenderScore;
			nationFinder.getNationById(attackerID).changeScore(-attackerLoss);

			nationFinder.getNationById(attackerID).addNewPenalty(new Penalty(2, Manager.turnCount + 1));
			nationFinder.getNationById(defenderID).addNewPenalty(new Penalty(4, Manager.turnCount + 1, attacked));
			
			//this.adverb = adverbs[(int)(attackerLoss / 2 * (adverbs.length-1))];
		}
	}

	private double computeModifiers(int nationID)
	{
		Nation n = nationFinder.getNationById(nationID);
		
		if(nationID == attackerID)
		{
			double penalty = penaltyAmount(n.getPenalties().size());
			attackHasPenalty = penalty != 0;
			return penalty;
		}
		else
		{
			int marks = n.getPenalties().size();
			if(n.hasPenaltiesAboutThisPlace(attacked) && marks != 0)
			{
				marks -= 1;
			}
			defenseHasPenalty = marks != 0;
			return penaltyAmount(marks);
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
		
		if(attackHasPenalty)
		{
			sb.append("*");
		}
		
		sb.append(" attacks ");
		sb.append(attacked.name);
		sb.append("(");
		sb.append(String.format("%.2f", attacked.landValue));
		sb.append(")");
		
		sb.append("of Nation ");
		sb.append(this.defenderName);
		sb.append("(");
		
		if(nationFinder.getNationById(defenderID) == null)
		{
			sb.append("dead");
		}
		else
		{
			sb.append(String.format("%.1f", nationFinder.getNationById(defenderID).getScore()));	
		}
		
		sb.append(")");
		
		if(defenseHasPenalty)
		{
			sb.append("*");
		}
		
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
	
	private static double penaltyAmount(int marks)
	{
		if(marks == 0)
			return 0;
		if(marks < 5)
		{
			return values[marks];
		}
		else
		{
			return 20 + 20*Math.log10(marks - 3);
		}
	}
}
