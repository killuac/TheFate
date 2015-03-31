/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class Enchant extends BaseSkill {

	public Enchant(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = (callback.getPlayer().getJudgedCard().isRedColor() && 
				(player.getHandCardCount()+player.getEquipmentCount() > 0) && !player.isNPC());
		if (this.isTriggered) {
			this.callback = callback;
			player.setHeroSkill(this);
			player.makeAllHandCardsAvailable();
			player.getEquipment().makeAllCardsDroppable();
			player.getGamePlugin().sendChooseCardToDropMessage(player, true);
		}
		return this.isTriggered;
	}
	
	@Override
	public void resolveResult() {
		player.getPreviousCardsFromTable(1);
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		this.callback.resolveJudge();
	}
	
}
