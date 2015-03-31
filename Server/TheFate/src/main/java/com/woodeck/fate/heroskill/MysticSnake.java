/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import com.woodeck.fate.player.Player;
import com.woodeck.fate.util.PluginConstant.UpdateReason;

/**
 * @author Killua
 *
 */
public class MysticSnake extends BaseSkill {

	public MysticSnake(Integer skillId, Player player) {
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
		
		Deque<String> tarPlayerNames = new ArrayDeque<String>(player.getGame().getSortedAlivePlayerNames());
		player.setTargetPlayerNames(tarPlayerNames);
	}
	
	@Override
	public void resolveResult() {
		Player tarPlayer = player.getNotResolvedTargetPlayer();
		if (tarPlayer.equals(player)) {		// Skip self player
			tarPlayer.finishResolve(this); return;
		}
		
		if (0 == tarPlayer.getHandCardCount() && 0 == tarPlayer.getSkillPoint()) {
			player.getTargetPlayerNames().remove(tarPlayer.getPlayerName());
			tarPlayer.updateHeroHpSp(-1, 1, this);
		} else if (0 == tarPlayer.getHandCardCount()) {
			player.getTargetPlayerNames().remove(tarPlayer.getPlayerName());
			tarPlayer.updateHeroHpSp(0, -1, this);
		}
		else {	// Draw one card from target player by random
			List<Integer> cardIds = new ArrayList<Integer>(tarPlayer.getHandCardIds());
			Collections.shuffle(cardIds);
			tarPlayer.getHand().removeCardId(cardIds.get(0), UpdateReason.UpdateReasonPlayer);
			player.getAssignedCardIds().add(cardIds.get(0));
			
			tarPlayer.finishResolve(this);
		}
	}
	
	@Override
	public void resolveContinue() {
		if (null != player.getNotResolvedTargetPlayer()) {
			this.resolveResult();
		} else {
			player.setHeroSkill(this);		// Maybe cleared by triggered target player's skill
			Integer cardId = player.getGame().getTableCardIds().pollFirst();
			player.getAssignedCardIds().addFirst(cardId);
			player.getGamePlugin().sendShowAssignedCardMessage(player);
			player.chooseCardToAssign();
		}
	}
	
	@Override
	public void resolveResult(Deque<Integer> cardIds) {
		player.setHeroSkill(null);
		player.getGamePlugin().sendShowAssignedCardPublicMessage(player, cardIds);
		
		player.assignCardToPlayers(cardIds, player.getTargetPlayers());
		player.getGame().backToTurnOwner();
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeAllHandCardsAvailable();
	}

}
