/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class Sunder extends HandCard {

	public Sunder(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean isActiveUsable() {
		if (player.isNPC()) {
			return (super.isActiveUsable() && player.getHealthPoint() < 2);
		}
		return super.isActiveUsable();
	}
	
	@Override
	public void selectTargetByNPC() {
		player.selectMaxHpTargetPlayer(false);
	}
	
	@Override
	public void resolveUse() {
		Player tarPlayer = player.getTargetPlayer();
		int tarPlayerHp = tarPlayer.getHealthPoint();
		int playerHp = player.getHealthPoint();
		
//		Exchange HP, don't trigger hero skill.
		player.getCharacter().setHeroHpSp(tarPlayerHp, player.getSkillPoint());
		tarPlayer.getCharacter().setHeroHpSp(playerHp, tarPlayer.getSkillPoint());
		
		if ((player.getHealthPoint() > 1 && tarPlayer.getHealthPoint() > 1) ||
			(player.getHealthPoint() == 1 && tarPlayer.getHealthPoint() == 1)) {
			player.getGame().backToTurnOwner(); return;
		}
		
//		Restore HP, maybe trigger hero skill. So call below method.
		if (1 == player.getHealthPoint()) {
			tarPlayer.setIsResolved(true);
			player.updateHeroHpSp(1, 0, this);
		} else {
			player.setIsResolved(true);
			tarPlayer.updateHeroHpSp(1, 0, this);
		}
	}
	
	@Override
	public void resolveContinue() {
		if (player.isResolved() && player.getTargetPlayer().isResolved())
			player.getGame().backToTurnOwner();
	}
	
}
