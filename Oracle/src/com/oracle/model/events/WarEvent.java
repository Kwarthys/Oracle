package com.oracle.model.events;

import com.oracle.manager.Manager;
import com.oracle.model.HistoricEvent;
import com.oracle.model.Nation;
import com.oracle.model.Penalty;
import com.oracle.model.Place;
import com.oracle.model.Standing;

public class WarEvent extends HistoricEvent
{
	//private final String[] adverbs = {"gloriously", "awfully"}; //We must have two separate lists for attackers and defenders
	private Place attacked;
	
	private Nation attacker;
	private Nation defender;
	
	private boolean success;
	
	private boolean attackHasPenalty = false;
	private boolean defenseHasPenalty = false;
	
	private String defenderName;
	
	//private String adverb;
	
	public WarEvent(Place attacked, Nation attacker)
	{
		this.attacked = attacked;
		this.attacker = attacker;
		
		this.defender = attacked.owner;

		this.defenderName = defender.name;
		
		attacker.update();
		defender.update();
		
		resolveCombat();
	}
	
	private void resolveCombat()
	{
		double attackerScore = attacker.getScore() * (1 - computeModifiers(attacker) / 100);
		double defenderScore = defender.getScore() * (1 - computeModifiers(defender) / 100);
		
		double r = Math.random() * (attackerScore + defenderScore);
		
		this.success = r < attackerScore;
		
		if(this.success) // Attacker won, defender lose points
		{
			double defenderLoss = 2 * (attackerScore - r) / attackerScore;
			defender.changeScore(-defenderLoss);
			defender.addNewPenalty(new Penalty(2, Manager.turnCount + 1));
			
			attacker.changeScore(attacked.landValue);
			
			//this.adverb = adverbs[(int)(defenderLoss / 2 * (adverbs.length-1))];
		}
		else //Attack failed
		{
			double attackerLoss = 2 * (r - attackerScore) / defenderScore;

			attacker.changeScore(-attackerLoss);

			attacker.addNewPenalty(new Penalty(2, Manager.turnCount + 1));
			defender.addNewPenalty(new Penalty(4, Manager.turnCount + 1, attacked));
			
			//this.adverb = adverbs[(int)(attackerLoss / 2 * (adverbs.length-1))];
		}
		
		attacker.updateStandingAbout(defender, Standing.TERRIROTY_WANTED);
		defender.updateStandingAbout(attacker, Standing.ATTACK);
	}

	private double computeModifiers(Nation nation)
	{
		Nation n = nation;
		
		if(nation == attacker)
		{
			double penalty = Penalty.penaltyAmount(n.getPenalties().size());
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
			return Penalty.penaltyAmount(marks);
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

		sb.append(attacker.getQuickDescriptor());
		
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
		
		if(defender == null)
		{
			sb.append("dead");
		}
		else
		{
			sb.append(String.format("%.1f", defender.getScore()));	
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
			sb.append(attacker.name);
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
