/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class SirenSong extends HandCard {

	public SirenSong(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public void resolveUse() {
		player.getGame().getSirenPlayers().add(player);
		player.setSirenTarget(player.getTargetPlayer());
		player.getTargetPlayer().setIsSirenSong(true);
	}

	@Override
	public void resolveStrengthen(Callback callback) {
		if (player.equals(player.getTargetPlayer()))
			player.getCharacter().updateHeroHpSp(0, -this.getRequiredSp());
		player.resolveUseHandCard(this);
	}
	
}
