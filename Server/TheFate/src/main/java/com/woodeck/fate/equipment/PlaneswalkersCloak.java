/**
 * 
 */
package com.woodeck.fate.equipment;

import java.util.ArrayList;
import java.util.List;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class PlaneswalkersCloak extends EquipmentCard {

	public PlaneswalkersCloak(Integer cardId, Player player) {
		super(cardId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		return (!callback.getPlayer().equals(player) && super.trigger(reason, callback));
	}
	
	@Override
	public void resolveOkay() {
		player.revealOneCardFromDeck(this);
	}
	
	@Override
	public boolean isJudgedYes() {
		return player.getJudgedCard().isRedColor();
	}
	
	@Override
	public void resolveJudge() {
		if (this.isJudgedYes()) {
			Player turnOwner = player.getGame().getTurnOwner();
			List<String> tarPlayerNames = new ArrayList<String>(turnOwner.getTargetPlayerNames());
			
			if (tarPlayerNames.size() > 1) {
				turnOwner.getTargetPlayerNames().remove(player.getPlayerName());
				int index = tarPlayerNames.indexOf(player.getPlayerName());
				if (index == 1) {
					this.callback.resolveUse();			// Mislead: Need continue resolve Player A
				} else {
					this.callback.resolveContinue();	// Mislead: Need continue resolve Player B
				}
			} else {
				player.getGame().backToTurnOwner();
			}
			
			return;
		}
		
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setEquipmentCard(null);
		
		HandCard usedCard = player.getGame().getDamageSource().getFirstUsedCard();
		if (callback.isSkill()) {	// e.g.Fireblast
			usedCard.resolveUse();
		} else {
			player.getGame().askForDispel(usedCard);
		}
	}
	
}
