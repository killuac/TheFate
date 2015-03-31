/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.Deque;

import com.woodeck.fate.player.Player;
import com.woodeck.fate.util.PluginConstant.UpdateReason;

/**
 * @author Killua
 *
 */
public class NetherSwap extends BaseSkill {

	public NetherSwap(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		return (super.isActiveLaunchable() && !player.isNPC());
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
	}
	
	@Override
	public void resolveResult() {
		Player tarPlayer = player.getTargetPlayer();
		player.getGamePlugin().sendShowPlayerHandCardMessage(player, tarPlayer);
		player.getGamePlugin().sendChooseCardToGetMessage(player, false);
	}
	
	@Override
	public void resolveResult(Player tarPlayer, Deque<Integer> cardIdxes, Deque<Integer> cardIds) {
		cardIds = tarPlayer.getCardIdsByCardIndexes(cardIdxes);
		tarPlayer.getHand().removeCardIds(cardIds, UpdateReason.UpdateReasonPlayer);
//		Need play animation on client UI, so use "UpdateReasonPlayer".
		player.getHand().addCardIds(cardIds, UpdateReason.UpdateReasonPlayer);
		tarPlayer.getLastCardFromTable();
		player.getGame().backToTurnOwner();
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeAllHandCardsAvailable();
	}
	
}
