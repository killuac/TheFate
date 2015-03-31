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
public class WildAxe extends HandCard {
		
	public WildAxe(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public void selectTargetByNPC() {
		for (Player tarPlayer : player.getGame().getOpponentPlayers()) {
			player.getTargetPlayerNames().add(tarPlayer.getPlayerName());
		}
	}
	
	@Override
	public void resolveUse() {		
		Player tarPlayer = player.getNotResolvedTargetPlayer();
		if (null != tarPlayer) {
			if (!tarPlayer.checkHeroSkillAndEquipment(TriggerReason.TriggerReasonBeAttackedByMagic, this)) {
				if (tarPlayer.getHandCardCount() == 0) {
					this.resolveCancelByTarget(tarPlayer);
				} else {
					tarPlayer.makeEvasionCardAvailable();
					player.getGamePlugin().sendChooseCardToUseMessage(tarPlayer, true);
				}
			}
		} else {
			player.getGame().backToTurnOwner(this);
		}
	}
	
//	private boolean isNeedTargetAgainByPlayer() {
//		targetIndex = player.getGame().getSortedAlivePlayers().indexOf(player.getTargetPlayer());
//		int playerCount = player.getGame().getAlivePlayerCount();
//		int halfCount = playerCount / 2;
//		return (playerCount > 2 && playerCount%2 == 0 && targetIndex == halfCount);
//	}
//	
//	Update all target players according to specified one target by client
//	private void updateTargetPlayerNamesFromIndex(int startIdx) {
//		player.setIsWildAxing(false);
//		
//		int count = Math.min(targetIndex, player.getGame().getAlivePlayerCount()-targetIndex);
//		player.getTargetPlayerNames().clear();
//		for (int i = startIdx; i < startIdx+count; i++) {
//			Player tp = player.getGame().getSortedAlivePlayers().get(i);
//			player.getTargetPlayerNames().add(tp.getPlayerName());
//		}
//	}
	
	@Override
	public void resolveContinue() {
		this.resolveUse();
	}
	
	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		int damageValue = this.getDamageValue();
		tarPlayer.updateHeroHpSp(-damageValue, damageValue, this);
	}

}
