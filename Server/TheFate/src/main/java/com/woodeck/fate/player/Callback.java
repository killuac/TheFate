/**
 * 
 */
package com.woodeck.fate.player;

import java.util.Deque;

/**
 * @author Killua
 *
 */
public interface Callback {
	
	public Player getPlayer();		// The owner of card or hero skill
	public int getDamageValue();	// Can deal damage
	public boolean isCard();
	public boolean isSkill();
	public boolean isJudgedYes();
	
	
	/**
	 * Select a hero skill or equipment to use
	 */
	public void resolveSelect();
	
	/**
	 * Select a hero skill or equipment by NPC
	 */
	public void resolveSelectByNPC();
	
	/**
	 * Select a target by NPC
	 */
	public void selectTargetByNPC();
	
	/**
	 * Use hand card, hero skill or equipment
	 */
	public void resolveUse();
	
	/**
	 * Use hero skill or equipment by dropping card
	 */
	public void resolveUse(Deque<Integer> cardIds);
	
	/**
	 * Resolve card or hero skill result if it isn't dispelled
	 */
	public void resolveResult();
	
	
	/**
	 * Resolve card or hero skill result if it isn't dispelled (e.g. Assign card)
	 */
	public void resolveResult(Deque<Integer> cardIds);
	
	
	/**
	 * Resolve card or hero skill result if it isn't dispelled (e.g. Get/Greed card)
	 */
	public void resolveResult(Player tarPlayer, Deque<Integer> cardIdxes, Deque<Integer> cardIds);
	
	
	/**
	 * 1. Maybe interrupt by other hero skill or equipment, need callback.
	 * 2. If have multiple target, continue resolve next player until all finished
	 */
	public void resolveContinue();
	
	
	/**
	 * Okay to trigger hero skill or equipment
	 */
	public void resolveOkay();
	
	
	/**
	 * Cancel trigger hero skill or equipment
	 */
	public void resolveCancel();
	
	
	/**
	 * Target player chose cancel
	 */
	public void resolveCancelByTarget(Player tarPlayer);
	
	
	/**
	 * Hero skill or equipment judge resolve
	 */
	public void resolveJudge();
	
	
	/**
	 * Used/Triggered hero skill or equipment determine self hand card available
	 */
	public void makeHandCardAvailable();
	
}
