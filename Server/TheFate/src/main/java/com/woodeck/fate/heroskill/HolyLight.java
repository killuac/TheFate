/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class HolyLight extends BaseSkill {

	public HolyLight(Integer skillId, Player player) {
		super(skillId, player);
	}

	public boolean trigger(TriggerReason reason, Callback callback) {
//		this.isTriggered = !player.isDying();
//		if (this.isTriggered) super.trigger(reason, callback);
//		return this.isTriggered;
		
		player.getGamePlugin().sendSkillOrEquipmentTriggeredPublicMessage(player, skillId, 0);
		player.drawCard(1);
		return false;
	}
	
//	@Override
//	public void resolveOkay() {
//		player.drawCard(1);
//		this.resolveCancel();
//	}
//	
//	@Override
//	public void resolveCancel() {
//		if (null != callback && callback.isSkill()) {
//			this.callback.resolveCancel();
//		} else {
//			player.getGame().backToTurnOwner();
//		}
//	}
	
}
