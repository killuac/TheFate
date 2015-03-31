/**
 * 
 */
package com.woodeck.fate.equipment;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.heroskill.HeroSkill;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class DemonEdge extends EquipmentCard {

	public DemonEdge(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		HandCard usedCard = player.getFirstUsedCard();
		HandCard transformedCard = null;
		HeroSkill heroSkill = player.getHeroSkill();
		if (null != heroSkill && heroSkill.getTransformedCardId() > 0) {
			transformedCard = player.getTransoformedCard(heroSkill.getTransformedCardId(), heroSkill.getMinHandCardCount());
		}
		
		isTriggered = ((null != usedCard && usedCard.isAttack()) || (null != transformedCard && transformedCard.isAttack()));
		if (this.isTriggered) {
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, 0, cardId);
			player.getGame().resolvePlayerDeath(callback);
		}
		
		return isTriggered;
	}
	
}
