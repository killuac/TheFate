/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class FierySoul extends BaseSkill {

	private HandCard card;
	
	public FierySoul(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.card = (HandCard) callback;
		this.isTriggered = (card.isMagicCard() && card.isStrengthenable() && usedTimes < 1);
		if (this.isTriggered) {
			this.card.resolveStrengthen(this);
		}
		return this.isTriggered;
	}
	
	@Override
	public void resolveContinue() {
		if (card.isStrengthened()) {
			this.usedTimes++;
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, skillId, 0);
		}
		player.resolveUseHandCard(card);
	}
	
}
