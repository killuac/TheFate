/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class Overload extends BaseSkill {

	private HandCard card;
	
	public Overload(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.card = (HandCard) callback;
//		boolean isTriggered = ((card.isMagicCard() || card.isSuperSkill()) && super.trigger(reason, callback));
//		this.isTriggered = false;	// Can be triggered multiple times
//		return isTriggered;
		if (card.isMagicCard() || card.isSuperSkill()) {
			player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, skillId, 0);
			player.drawCard(1);
		}
		return false;
	}
	
//	如果自己使用驱散的时候触发"超负荷"，可能会被其他玩家使用的驱散打断，改成锁定技。
//	@Override
//	public void resolveOkay() {
//		player.drawCard(1);
//		this.resolveCancel();
//	}
//	
//	@Override
//	public void resolveCancel() {
//		player.setHeroSkill(null);
//		if (player.getGame().isWaitingDispel()) {
//			player.resolveUseDispel(card);
//		} else {
//			player.handleUseHandCard(card);
//		}
//	}

}
