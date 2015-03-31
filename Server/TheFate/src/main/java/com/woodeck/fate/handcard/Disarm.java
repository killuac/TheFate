/**
 * 
 */
package com.woodeck.fate.handcard;

import java.util.Deque;

import com.woodeck.fate.player.Player;
import com.woodeck.fate.util.PluginConstant.UpdateReason;

/**
 * @author Killua
 *
 */
public class Disarm extends HandCard {

	public Disarm(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean isActiveUsable() {
		if (player.isNPC()) {
			for (Player p : player.getGame().getOpponentPlayers()) {
				if (p.getEquipmentCount() > 0) {
					return super.isActiveUsable();
				}
			}
			return false;
		}
		return super.isActiveUsable();
	}
	
	@Override
	public void selectTargetByNPC() {
		player.selectEquipmentTargetPlayer();
	}
	
	@Override
	public void resolveUse() {
		player.setIsDisarming(true);
		
		player.getGamePlugin().sendShowPlayerEquipmentMessage(player, player.getTargetPlayer());
		if (this.isStrengthened) {
			player.getGamePlugin().sendChooseCardToGetMessage(player, false);
		} else {
			player.getGamePlugin().sendChooseCardToRemoveMessage(player);
		}
	}
	
	@Override
	public void resolveResult(Player tarPlayer, Deque<Integer> cardIdxes, Deque<Integer> cardIds) {
		if (this.player.equals(tarPlayer) && this.isStrengthened) {		// If disarm self and strengthened, update by default.
			tarPlayer.getEquipment().removeCardIds(cardIds, UpdateReason.UpdateReasonDefault);
		} else {
			tarPlayer.getEquipment().removeCardIds(cardIds, UpdateReason.UpdateReasonPlayer);
		}
		player.getHand().addCardIds(cardIds, UpdateReason.UpdateReasonPlayer);
		player.getGame().backToTurnOwner(this);
	}
	
	@Override
	public boolean checkTargetPlayerAvailable() {
		return (player.getTargetPlayer().getEquipmentCount() > 0);
	}
	
}
