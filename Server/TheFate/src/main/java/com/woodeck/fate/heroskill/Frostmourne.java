/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class Frostmourne extends BaseSkill {

	private Player preTargetPlayer;
	
	public Frostmourne(Integer skillId, Player player) {
		super(skillId, player);
	}

	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
//		Resolve skill "Frostmourne" after finish the resolve of attack dealt damage
		HandCard card = (HandCard) callback;
		this.isTriggered = (!card.getPreTargetPlayer().isDead() && player.getHandCardCount() >= getMinHandCardCount());
		if (this.isTriggered) {
			this.callback = callback;
			preTargetPlayer = card.getPreTargetPlayer();
			preTargetPlayer.resetValueAfterResolved();	// Reset for next attack
			
			this.makeHandCardAvailable();
			player.setHeroSkill(this);
			player.setAttackLimit(player.getAttackLimit()+1);
			player.setRequiredSelCardCount(1);
			player.getGamePlugin().sendChooseCardToUseMessage(player, true);
		}
		return this.isTriggered;
	}
	
	@Override
	public void resolveUse() {
		player.setHeroSkill(null);
		if (null != player.getEquipmentCard()) {		// e.g. Use BladesOfAttack
			player.getEquipmentCard().resolveUse();
		} else {
			player.getLastUsedCard().resolveUse();
		}
	}
	
	@Override
	public void resolveCancel() {
		preTargetPlayer.setIsResolved(true);			// Don't continue use attack
		player.setHeroSkill(null);
		player.setAttackLimit(player.getAttackLimit()-1);
		this.callback.resolveContinue();
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.triggerAttackEquipmentBySkill(null);
		player.makeAttackCardAvailable();
	}
	
}
