/**
 * 
 */
package com.woodeck.fate.fatecard;

import com.woodeck.fate.card.RoleCard.RoleCardEnum;
import com.woodeck.fate.game.Game;
import com.woodeck.fate.player.Player;


/**
 * @author Killua
 * 你左侧和右侧的角色都死亡
 */
public class SpreadingPlague extends FateCard {

	public SpreadingPlague(Integer cardId) {
		super(cardId);
	}
	
	@Override
	public boolean checkGameOver(Game game, Player deadPlayer) {
		if (game.hasAlivePlayerWithRole(RoleCardEnum.RoleCardNeutral)) {
			Player neutralPlayer = null;
			Player prePlayer = game.getPreviousPlayer(deadPlayer);
			Player nextPlayer = game.getNextPlayer(deadPlayer);
			
//			Find out one correct neutral player and check if victory
			if (prePlayer.getRoleCard().isNeutral() && nextPlayer.getRoleCard().isNeutral()) {
				neutralPlayer = game.getAliveNeutralPlayer(game.getTurnOwner());
			} else if (prePlayer.getRoleCard().isNeutral() && !prePlayer.isDead()) {
				neutralPlayer = prePlayer;
			} else if (nextPlayer.getRoleCard().isNeutral() && !nextPlayer.isDead()) {
				neutralPlayer = nextPlayer;
			}
			
//			Check if left and right player of the neutral player are both dead
			if (null != neutralPlayer) {
				Player leftPlayer = game.getPreviousPlayer(neutralPlayer);
				Player rightPlayer = game.getNextPlayer(neutralPlayer);
				if (leftPlayer.isDead() && rightPlayer.isDead()) {
					neutralPlayer.setIsVictory(true); return true;
				}
			}
		}
		
		return super.checkGameOver(game, deadPlayer);
	}
	
}
