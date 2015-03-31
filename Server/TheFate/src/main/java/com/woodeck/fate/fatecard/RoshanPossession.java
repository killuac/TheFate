/**
 * 
 */
package com.woodeck.fate.fatecard;

import com.woodeck.fate.game.Game;
import com.woodeck.fate.player.Player;


/**
 * @author Killua
 * 你的回合，公布你的身份，立即摸X张牌，此后你的摸牌阶段可额外摸X张牌，
 * 手牌上限+X(X为场上除你之外存活角色数)，你杀死其他所有角色即获胜(其他角色的获胜条件变为杀死你即获胜)
 */
public class RoshanPossession extends FateCard {

	public RoshanPossession(Integer cardId) {
		super(cardId);
	}
	
	@Override
	public boolean checkGameOver(Game game, Player deadPlayer) {
//		TODO: Implement until next update version
		return super.checkGameOver(game, deadPlayer);
	}

}
