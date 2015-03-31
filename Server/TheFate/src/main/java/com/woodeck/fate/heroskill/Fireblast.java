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
public class Fireblast extends BaseSkill {

	private HandCard card;
	
	public Fireblast(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.card = (HandCard) callback;
		if (card.isMagicCard()) {
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, skillId, 0);
			if (card.isStrengthenable() && player.getSkillPoint() > 0) {
				this.card.resolveStrengthen(this);
			} else {
				this.resolveContinue();
			}
		}
		return card.isMagicCard();
	}
	
	@Override
	public void resolveContinue() {
		if (card.isStrengthened()) {
			player.getCharacter().updateHeroHpSp(0, -card.getRequiredSp());
		}
		
		if (card.isDispel()) {
			player.getGame().continueResolveAfterDispel();
		} else {
			if (!player.checkTargetEquipment(TriggerReason.TriggerReasonBeCastMagic, this)) {
				this.card.resolveUse();
			}
		}
	}
	
}
