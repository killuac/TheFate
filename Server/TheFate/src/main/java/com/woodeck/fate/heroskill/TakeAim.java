/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.player.Player;
import com.woodeck.fate.util.PluginConstant.UpdateReason;

/**
 * @author Killua
 *
 */
public class TakeAim extends BaseSkill {

	public TakeAim(Integer skillId, Player player) {
		super(skillId, player);
		player.setPlusDistance(1);
		player.setMinusDistance(-1);
		player.getGamePlugin().sendUpdateEquipmentMessage(player, 0, UpdateReason.UpdateReasonDefault);
	}

}
