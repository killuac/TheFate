/**
 * 
 */
package com.woodeck.fate.equipment;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class SacredRelic extends EquipmentCard {	
	
	public SacredRelic(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
//		Maybe triggered multiple times by multiple target players
		return (player.getFirstUsedCard().isBlackColor() && super.trigger(reason, callback));
	}
	
	@Override
	public void resolveOkay() {
		player.setAttackDamageExtra(player.getAttackDamageExtra() + 1);	// Maybe triggered hero skill before
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setEquipmentCard(null);
		callback.resolveCancelByTarget(player.getNotResolvedTargetPlayer());
	}
	
}
