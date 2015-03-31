/**
 * 
 */
package com.woodeck.fate.fatecard;

import com.woodeck.fate.game.Game;
import com.woodeck.fate.player.Player;


/**
 * @author Killua
 * 当你的下家胜利时，代替其获得胜利
 */
public class PuppetOfBackfire extends FateCard {

	public PuppetOfBackfire(Integer cardId) {
		super(cardId);
	}
	
	@Override
	public boolean checkGameOver(Game game, Player deadPlayer) {
		if (super.checkGameOver(game, deadPlayer)) {
			Player neutralPlayer = this.determineNeutralWinner(game);
			if (null != neutralPlayer) {
				for (Player player : game.getAllPlayers()) {
					player.setIsVictory(false);
				}
				neutralPlayer.setIsVictory(true);
			}
			return true;
		}
		return false;
	}
	
	private Player determineNeutralWinner(Game game) {
		Player neutralPlayer = game.getAliveNeutralPlayer(game.getTurnOwner());	// 最近的中立玩家(当前的)
		
		if (null != neutralPlayer) {
			Player prePlayer = game.getPreviousPlayer(neutralPlayer);
			Player nextPlayer = game.getNextPlayer(neutralPlayer);
			Player nnextPlayer = game.getNextPlayer(nextPlayer);
			
			if (nextPlayer.isVictory()) {
				if (prePlayer.getRoleCard().isNeutral() && !prePlayer.isDead()) {
					return prePlayer;		// 传递: 当前中立玩家的上家也是中立玩家
				} else {
					return neutralPlayer;
				}
			}
			else if (nextPlayer.getRoleCard().isNeutral() && !nextPlayer.isDead() && nnextPlayer.isVictory()) {
				return neutralPlayer;		// 传递: 当前中立玩家的下家也是中立玩家
			}
			else {
				Player nextNeutralPlayer = game.getAliveNeutralPlayer(nextPlayer);
				nextPlayer = game.getNextPlayer(nextNeutralPlayer);
				if (nextPlayer.isVictory()) return nextNeutralPlayer;	// 下一个中立玩家获胜
			}
		}
		return null;
	}

}
