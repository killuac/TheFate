/**
 * 
 */
package com.woodeck.fate.equipment;

import java.util.Deque;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class MysticStaff extends EquipmentCard {

	public MysticStaff(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		return (super.isActiveLaunchable() && player.getCharacter().isIntelligence() && usedTimes < 1 && !player.isNPC());
	}
	
	@Override
	public void selectTargetByNPC() {
//		 Don't select any target player, only equip for self.
	}
	
	@Override
	public void resolveSelect() {
		player.setSelectableCardCount(player.getHandCardCount()+player.getEquipmentCount()-1);	// Exclude self
		player.getEquipment().makeArmorDroppable();
		player.getGamePlugin().sendChooseCardToDropMessage(player, false);
	}
	
	@Override
	public void resolveUse(Deque<Integer> cardIds) {
		this.usedTimes++;
		player.drawCard(cardIds.size()-1);
		player.getGame().backToTurnOwner();
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeAllHandCardsAvailable();
	}
	
}
