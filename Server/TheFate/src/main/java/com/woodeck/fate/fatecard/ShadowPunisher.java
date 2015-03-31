/**
 * 
 */
package com.woodeck.fate.fatecard;

import com.woodeck.fate.card.RoleCard;
import com.woodeck.fate.card.RoleCard.RoleCardEnum;
import com.woodeck.fate.game.Game;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 * 天灾军团全灭
 */
public class ShadowPunisher extends FateCard {

	public ShadowPunisher(Integer cardId) {
		super(cardId);
	}
	
	@Override
	public boolean checkGameOver(Game game, Player deadPlayer) {
		RoleCard deadRole = new RoleCard(deadPlayer.getRoleCardId());
		
		if (deadRole.isScourge() && game.hasAlivePlayerWithRole(RoleCardEnum.RoleCardNeutral)) {
			for (Player player : game.getAlivePlayers()) {
				if (player.getRoleCard().isSameWithRole(deadRole))
					return false;
			}
			game.getAliveNeutralPlayer(game.getTurnOwner()).setIsVictory(true);
			return true;
		} else {
			return super.checkGameOver(game, deadPlayer);
		}
	}

}
