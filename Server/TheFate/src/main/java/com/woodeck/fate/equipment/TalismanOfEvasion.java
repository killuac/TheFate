/**
 * 
 */
package com.woodeck.fate.equipment;

import java.util.Deque;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class TalismanOfEvasion extends EquipmentCard {

	private boolean isVerticalSet = true;	// 是否是竖直状态
	
	public TalismanOfEvasion(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public String getHistoryText() {
		return (player.isTurnOwner()) ? historyText : historyText2;
	}
	
	/**
	 * Getter and Setter method
	 */
	public boolean isVerticalSet() {
		return isVerticalSet;
	}
	public void setIsVerticalSet(boolean isVerticalSet) {
		this.isVerticalSet = isVerticalSet;
	}
	
	@Override
	public boolean isActiveLaunchable() {
		return (super.isActiveLaunchable() && !isVerticalSet);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = isVerticalSet;
		return (this.isTriggered && super.trigger(reason, callback));
	}
	
	@Override
	public void resolveSelect() {
		player.getGamePlugin().sendChooseCardToDropMessage(player, player.isNPC());
	}
	
	@Override
	public void resolveUse(Deque<Integer> cardIds) {
		isVerticalSet = true;
		player.getGame().backToTurnOwner();
	}
	
	@Override
	public void resolveOkay() {
		isVerticalSet = false;
		player.finishResolve(callback);
	}
	
	@Override
	public void resolveCancel() {
//		If cancel use "TalismanOfEvasion", need reset equipmentCard for next checking "LotharsEdge".
		player.setEquipmentCard(null);
		callback.resolveContinue();
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeAllHandCardsAvailable();
	}
	
}
