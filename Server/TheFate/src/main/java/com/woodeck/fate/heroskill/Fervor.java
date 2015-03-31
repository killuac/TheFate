/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class Fervor extends BaseSkill {

	private HandCard normalAttack;
	
	public Fervor(Integer skillId, Player player) {
		super(skillId, player);
		normalAttack = HandCard.newCardWithCardId(getTransformedCardId(), player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		boolean isAvailable = (super.isActiveLaunchable() && normalAttack.isActiveUsable());
		if (player.isNPC()) {
			return (isAvailable && player.hasEvasion());
		} else {
			return isAvailable;
		}
	}
	
	@Override
	public void resolveSelect() {
		if (player.isTurnOwner()) {
			player.getGamePlugin().sendPlayCardMessage(player, player.isNPC());
		} else {
			player.setRequiredTargetCount(0);
			player.getGamePlugin().sendChooseCardToUseMessage(player, player.isNPC());
		}
	}
	
	@Override
	public void selectTargetByNPC() {
		normalAttack.selectTargetByNPC();
	}
	
	@Override
	public void resolveUse() {
		normalAttack.resolveUse();
	}
	
	@Override
	public void resolveContinue() {
		normalAttack.resolveContinue();
	}
	
	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		normalAttack.resolveCancelByTarget(tarPlayer);
	}
	
	@Override
	public void resolveCancel() {
		if (null != callback) {		// BattleHunger
			callback.resolveCancelByTarget(player);
		} else {
			super.resolveCancel();
		}
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeEvasionCardAvailable();
	}
	
}
