/**
 * 
 */
package com.woodeck.fate.fatecard;

import com.woodeck.fate.card.RoleCard.RoleCardEnum;
import com.woodeck.fate.game.Game;
import com.woodeck.fate.player.Player;


/**
 * @author Killua
 * 场上仅剩3名不同阵营的角色
 */
public class GameOfFate extends FateCard {

	public GameOfFate(Integer cardId) {
		super(cardId);
	}
	
	@Override
	public boolean checkGameOver(Game game, Player deadPlayer) {
		if (3 == game.getAlivePlayers().size() &&
			game.hasAlivePlayerWithRole(RoleCardEnum.RoleCardNeutral) &&
			game.hasAlivePlayerWithRole(RoleCardEnum.RoleCardSentinel) &&
			game.hasAlivePlayerWithRole(RoleCardEnum.RoleCardScourge)) {
			game.getAliveNeutralPlayer(game.getTurnOwner()).setIsVictory(true);
			return true;
		} else {
			return super.checkGameOver(game, deadPlayer);
		}
	}
	
}
