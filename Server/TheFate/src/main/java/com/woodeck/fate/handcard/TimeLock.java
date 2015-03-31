/**
 * 
 */
package com.woodeck.fate.handcard;

import java.util.List;

import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class TimeLock extends HandCard {

	private int continueIndex;
	
	public TimeLock(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public boolean isActiveUsable() {		
		if (player.isNPC()) {
			return (super.isActiveUsable() && player.getHandCardCount() <= player.getHandSizeLimit());
		}
		return super.isActiveUsable();
	}
	
	@Override
	public void resolveUse() {
		player.setIsTimeLocking(true);
		
		for (Player player : this.player.getGame().getSortedAlivePlayers()) {
			this.player.getGame().resetSirenTargetOfPlayer(player);
			if (this.checkHeroSkillOfPlayer(player)) return;
		}
		
		player.startDiscard();
	}
	
	private boolean checkHeroSkillOfPlayer(Player player) {
		if (!player.isSirenSonging() && player.checkHeroSkill(TriggerReason.TriggerReasonEndingTurn, this)) {
			this.continueIndex = player.getGame().getSortedAlivePlayers().indexOf(player);
			return true;
		}
		return false;
	}
	
	@Override
	public void resolveContinue() {
		List<Player> allSortedPlayers = player.getGame().getSortedAlivePlayers();
		for (int i = continueIndex; i < allSortedPlayers.size(); i++) {
			Player player = allSortedPlayers.get(continueIndex);
			if (this.checkHeroSkillOfPlayer(player)) return;
		}
		
		player.startDiscard();
	}
	
}
