/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.card.PlayingCard.CardSuits;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class Frostbite extends BaseSkill {
	
	private HandCard card;
	
	public Frostbite(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
//	Can be triggered multiple times
	public boolean trigger(TriggerReason reason, Callback callback) {
		boolean isTriggered = (!callback.getPlayer().equals(player) && player.getHandCardCount() >= getMinHandCardCount());
		if (player.isNPC() && isTriggered) {
			isTriggered = (!callback.getPlayer().getRoleCard().isSameWithRole(player.getRoleCard()) &&
							player.getHandCardCountBySuits(CardSuits.CardSuitsHearts) >= getMinHandCardCount());
		}
		
		if (isTriggered) {
			this.card = (HandCard) callback;
			player.setHeroSkill(this);
			
			// If damage source equipped EyeOfSkadi, self player need use 2 evasion. So need backup it.
			int preSelectableCardCount = player.getSelectableCardCount();
			
			player.setRequiredTargetCount(0);
			player.setRequiredSelCardCount(getMinHandCardCount());
			player.makeHandCardAavailableBySuits(CardSuits.CardSuitsHearts);
			player.getGamePlugin().sendChooseCardToDropMessage(player, true);
			player.setSelectableCardCount(preSelectableCardCount);
			return true;
		}
		return isTriggered;
	}
	
	@Override
	public void resolveResult() {
		player.setHeroSkill(null);
		
		if (card.isAttack()) {
			if (card.getPlayer().isTurnOwner()) {
				player.getGame().backToTurnOwner();
			} else {
				card.getPlayer().resolveCancelByTarget();	// Maybe attack due to skill BattleHunger
			}
		} else if (card.isEvasion()) {
			card.getPlayer().resolveCancelByTarget();
		} else {
			card.resolveCancel();	// Maybe launched equipment QuellingBlade
		}
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		// e.g. Damage source use attack, self player launch the skill, damage source launch DiffusalBlade to dispel
		player.getGame().getDamageSource().setEquipmentCard(null);
		this.card.resolveContinue();
	}
	
}
