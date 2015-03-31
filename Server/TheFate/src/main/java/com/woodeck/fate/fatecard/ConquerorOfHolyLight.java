/**
 * 
 */
package com.woodeck.fate.fatecard;

import com.woodeck.fate.card.RoleCard;
import com.woodeck.fate.game.Game;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 * 近卫军团全灭
 */
public class ConquerorOfHolyLight extends FateCard {

	public ConquerorOfHolyLight(Integer cardId) {
		super(cardId);
	}
	
	@Override
	public boolean checkGameOver(Game game, Player deadPlayer) {
		RoleCard deadRole = new RoleCard(deadPlayer.getRoleCardId());
		Player neutralPlayer = game.getAliveNeutralPlayer(game.getTurnOwner());
		
		if (deadRole.isSentinel() && null != neutralPlayer) {
			for (Player player : game.getAlivePlayers()) {
				if (player.getRoleCard().isSameWithRole(deadRole))
					return false;
			}
			neutralPlayer.setIsVictory(true);
			return true;
		} else {
			return super.checkGameOver(game, deadPlayer);
		}
	}
	
}
