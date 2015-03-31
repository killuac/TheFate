/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.Deque;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class BrillianceAura extends BaseSkill {
	
	public BrillianceAura(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		return (super.trigger(reason, callback) && !player.isNPC());
	}
	
	@Override
	public void resolveOkay() {
		this.usedTimes++;
		player.setDrawCardCount(player.getDrawCardCount()-1);
		player.showCardAndChooseToAssign();
	}
	
	@Override
	public void resolveCancel() {
//		Must set it with null, otherwise, getActiveUsableCardIdList will return empty.
		player.setHeroSkill(null);
		player.startDrawCardAndPlay();
	}
	
	@Override
	public void resolveResult(Deque<Integer> cardIds) {
		player.setHeroSkill(null);	// Ditto
		player.assignCardToEachPlayer(cardIds);
		player.startDrawCardAndPlay();
	}
	
}
