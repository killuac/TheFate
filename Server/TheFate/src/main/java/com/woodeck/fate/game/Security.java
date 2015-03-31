/**
 * 
 */
package com.woodeck.fate.game;

import java.util.Arrays;

import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.card.PlayingCard.PlayingCardEnum;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class Security {
	
	/**
	 * Check if there is player cheating in the game
	 */
	public static boolean checkCheating(Game game, Player player) {
		switch (game.getAction()) {
			case ActionUseHandCard:
				return (checkTurnOwner(game, player) || checkUseHandCard(game, player));
				
			case ActionChoseCardToUse:
				return checkChoseCardToUse(game, player);
				
			case ActionChoseCardToDrop:
				
				break;
				
			default:
				break;
		}
		
		return false;
	}
	
	private static void handleCheating(Game game, Player player) {
		game.getLogger().error("Player [{}] cheating in the game.", player.getPlayerName());
		game.getLogger().debug("Call stack trace: {}", Arrays.toString(Thread.currentThread().getStackTrace()));
		game.getPlugin().sendInvalidMessage(player);
		player.setIsDead(true);
		
		if (game.getPlayerCount() > 1) {
			game.turnToNextPlayer();
		} else {
//			TODO: 玩家胜利
		}
	}
	
	private static boolean checkTurnOwner(Game game, Player player) {
		if (!player.getPlayerName().equals(game.getTurnOwner().getPlayerName())) {
			handleCheating(game, player);
			return true;
		}
		return false;
	}
	
	private static boolean checkUseHandCard(Game game, Player player) {
		game.getLogger().error("Shit!!!");
		game.getLogger().debug("All hand card ids: {}", player.getHandCardIds());
		game.getLogger().debug("Used cards: {}", player.getUsedCards());
		
		if (null != player.getHeroSkill() || null != player.getEquipmentCard())	return false;
		
		if (!player.getHandCardIds().contains(player.getLastUsedCardId())) {
//			handleCheating(game, player);	// Don't need handle(Maybe connection delay leads to the issue)
			return true;
		}
		
		PlayingCard card = player.getLastUsedCard();
		if (card.isSuperSkill()) {
			if (player.getSkillPoint() < card.getRequiredSp()) {
				handleCheating(game, player);
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean checkChoseCardToUse(Game game, Player player) {
		if (checkUseHandCard(game, player)) return true;
		
		if (null != player.getHeroSkill() || null != player.getEquipmentCard())	return false;
		
		switch (game.getTurnOwner().getLastUsedCard().getCardEnum()) {
			case PlayingCardNormalAttack:
			case PlayingCardFlameAttack:
			case PlayingCardChaosAttack:
				if (PlayingCardEnum.PlayingCardEvasion != player.getLastUsedCard().getCardEnum()) {
					handleCheating(game, player);
					return true;
				}
				break;
				
			case PlayingCardLagunaBlade:
				for (HandCard card : player.getUsedCards()) {
					if (PlayingCardEnum.PlayingCardEvasion != card.getCardEnum()) {
						handleCheating(game, player);
						return true;
					}
				}
				break;
				
			default:
				break;
		}
		
		return false;
	}
	
}
