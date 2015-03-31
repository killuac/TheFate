/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class LagunaBlade extends HandCard {

	public LagunaBlade(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public void resolveUse() {
		Player tarPlayer = player.getTargetPlayer();
		tarPlayer.setSelectableCardCount(3);
		if (!tarPlayer.checkHeroSkillAndEquipment(TriggerReason.TriggerReasonBeAttackedByMagic, this)) {
			if (tarPlayer.getHandCardCount() > 0) {
				tarPlayer.makeEvasionCardAvailable();
				player.getGamePlugin().sendChooseCardToUseMessage(tarPlayer, true);
			} else {
				this.resolveCancelByTarget(tarPlayer);
			}
		}
	}
	
	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
//		Target player chose cancel, this.player is turn owner
		int damage = this.getDamageValue() - tarPlayer.getUsedEvasionCount();
		tarPlayer.updateHeroHpSp(-damage, damage, this);
	}
	
}
