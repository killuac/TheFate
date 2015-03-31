/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.game.Game.GameState;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 * 技能【查克拉】和卡牌【查克拉】效果相同
 */
public class ChakraMagic extends BaseSkill {

	private HandCard chakra;
	
	public ChakraMagic(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendPlayCardMessage(player, player.isNPC());
	}
	
	@Override
	public void selectTargetByNPC() {
		Player tarPlayer = player.selectMinDeltaCardCountTargetPlayer(true);
		if (tarPlayer.getHandCardCount() > 2) {
			player.getTargetPlayerNames().clear();
			player.getTargetPlayerNames().add(player.getPlayerName());
		}
	}
	
	@Override
	public void resolveUse() {
		Player tarPlayer = player.getTargetPlayer();
		if (null == tarPlayer) tarPlayer = this.player;
		tarPlayer.setHeroSkill(this);
		chakra = HandCard.newCardWithCardId(getTransformedCardId(), tarPlayer);
		
		this.usedTimes++;
		player.resolveUseHandCard(chakra);
	}
	
	@Override
	public void resolveResult() {
		if (GameState.GameStatePlaying == player.getGame().getState()) {
			chakra.resolveUse();
		} else {
			chakra.resolveResult();
		}
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeAllHandCardsAvailable();
	}
	
}
