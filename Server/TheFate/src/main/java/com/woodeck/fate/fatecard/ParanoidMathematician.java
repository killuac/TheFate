/**
 * 
 */
package com.woodeck.fate.fatecard;

import com.woodeck.fate.game.Game;
import com.woodeck.fate.player.Player;


/**
 * @author Killua
 * 任意时刻(非结算过程中)，场上所有角色的怒气值均为奇数
 */
public class ParanoidMathematician extends FateCard {

	public ParanoidMathematician(Integer cardId) {
		super(cardId);
	}
	
	@Override
	public boolean checkGameOver(Game game, Player deadPlayer) {
		Player neutralPlayer = game.getAliveNeutralPlayer(game.getTurnOwner());
		
		if (null == deadPlayer && null != neutralPlayer) {
			for (Player player : game.getAlivePlayers()) {
				if (0 == player.getSkillPoint()%2)
					return false;
			}
			neutralPlayer.setIsVictory(true);
			return true;
		} else {
			return (null != deadPlayer) ? super.checkGameOver(game, deadPlayer) : false;
		}
	}

}
