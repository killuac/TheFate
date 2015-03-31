/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class BattleHunger extends BaseSkill {
		
	public BattleHunger(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
		player.setRequiredTargetCount(0);	// For trigger CounterHelix
	}
	
	@Override
	public void resolveResult() {
		Player tarPlayer = player.getTargetPlayer();
		tarPlayer.getTargetPlayerNames().add(player.getPlayerName());	// Turn owner as target
		if (tarPlayer.getHandCardCount() > 0) {
			this.makeTargetPlayerHandCardAvailable(tarPlayer);
			player.getGamePlugin().sendChooseCardToUseMessage(tarPlayer, true);
		} else {
			this.resolveCancelByTarget(tarPlayer);
		}
	}
	
	@Override
	public void resolveContinue() {
		player.getGame().backToTurnOwner();
	}
	
//	@Override
//	public void resolveCancel() {
//		Refer to hero skill "CounterHelix"
//	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeAllHandCardsAvailable();
	}
	
	public void makeTargetPlayerHandCardAvailable(Player tarPlayer) {
		tarPlayer.triggerAttackHeroSkillBySkill(this);
		tarPlayer.triggerAttackEquipmentBySkill(this);
		tarPlayer.makeAttackCardAvailable();
	}
	
}
