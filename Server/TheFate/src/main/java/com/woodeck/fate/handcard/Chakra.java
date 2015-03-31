/**
 * 
 */
package com.woodeck.fate.handcard;

import com.electrotank.electroserver5.extensions.api.ScheduledCallback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.util.Constant;

/**
 * @author Killua
 *
 */
public class Chakra extends HandCard implements ScheduledCallback {

	public Chakra(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public void resolveUse() {
		Player tarPlayer = player.getTargetPlayer();
		if (null != tarPlayer) {
			this.player = tarPlayer;
			player.getUsedCards().addFirst(this);
		}
		
		player.drawCard(1);
		player.guessCardColor();
	}
	
	@Override
	public void resolveResult() {
		player.getGamePlugin().sendFaceUpTableCardPublicMessage(player);
		
//		If guess right, continue.
		if (player.getJudgedCard().getCardColor() == player.getSelectedColor()) {
			player.getGamePlugin().getApi().scheduleExecution(Constant.DEFAULT_DELAY_SHORT_TIME, 1, this);
		} else {
			player.getGame().backToTurnOwner(this);
		}
	}

	@Override
	public void scheduledCallback() {
		player.getLastCardFromTable();
		player.guessCardColor();
	}
	
}
