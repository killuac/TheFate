/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class ManaVoid extends BaseSkill {
	
	public ManaVoid(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		if (player.isNPC()) {
			Player tarPlayer = player.selectMinDeltaCardCountTargetPlayer(false);
			player.getTargetPlayerNames().clear();
			int deltaCount = tarPlayer.getHandSizeLimit() - tarPlayer.getHandCardCount();
			return (super.isActiveLaunchable() && deltaCount >= 2);
		} else {
			return super.isActiveLaunchable();
		}
	}
	
	@Override
	public void resolveSelect() {
		player.getGamePlugin().sendChooseTargetPlayerMessage(player, false);
	}
	
	@Override
	public void resolveSelectByNPC() {
		this.selectTargetByNPC();
		this.resolveUse();
	}
	
	@Override
	public void selectTargetByNPC() {
		player.selectMinDeltaCardCountTargetPlayer(false);
	}
	
	@Override
	public void resolveUse() {
		player.getCharacter().updateHeroHpSp(0, -this.getRequiredSp());
		super.resolveUse();
	}
	
	@Override
	public void resolveResult() {
		Player tarPlayer = player.getTargetPlayer();
		int damage = tarPlayer.getHandSizeLimit() - tarPlayer.getHandCardCount();
		tarPlayer.updateHeroHpSp(-damage, damage, this);
	}
	
	@Override
	public void resolveContinue() {
		player.getGame().backToTurnOwner();
	}
	
}
