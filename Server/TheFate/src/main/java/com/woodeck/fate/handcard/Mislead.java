/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class Mislead extends HandCard {
	
	private boolean isFirstResolveContinue = true;
	
	public Mislead(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean isActiveUsable() {		
		if (player.isNPC()) {
			for (Player tarPlayer : player.getGame().getOpponentPlayers()) {
				if (tarPlayer.getSkillPoint() > 0) {
					return (super.isActiveUsable() && player.getSkillPoint() < player.getSpLimit());
				}
			}
			return false;
		}
		return super.isActiveUsable();
	}
	
	@Override
	public void selectTargetByNPC() {
		for (Player tarPlayer : player.getGame().getOpponentPlayers()) {
			if (tarPlayer.getSkillPoint() > 0) {
				player.getTargetPlayerNames().add(tarPlayer.getPlayerName());
				player.getTargetPlayerNames().add(player.getPlayerName());
				break;
			}
		}
	}
	
	@Override
	public void resolveUse() {
		this.isFirstResolveContinue = true;		// Call again by MultiCast
		player.setRequiredTargetCount(this.getTargetCount());
		
//		Need keep original target sequence, don't get sorted target player.
		Player playerA = player.getTargetPlayers().peekFirst();
		playerA.drawCard(1);	// Must draw card first for client UI
		playerA.updateHeroHpSp(0, -1, this);
	}
	
	@Override
	public void resolveContinue() {
		if (this.isFirstResolveContinue) {
			this.isFirstResolveContinue = false;
			Player playerB = player.getTargetPlayers().peekLast();
			playerB.updateHeroHpSp(0, 1, this);
		} else {
			player.getGame().backToTurnOwner(this);
		}
	}
	
	@Override
	public boolean checkTargetPlayerAvailable() {
		Player playerA = player.getTargetPlayers().peekFirst();
		Player playerB = player.getTargetPlayers().peekLast();
		return (playerA.getSkillPoint() > 0 &&
				playerB.getSkillPoint() < playerB.getSpLimit());
	}
	
}
