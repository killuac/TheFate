/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.card.PlayingCard.CardColor;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class LagunaBlade extends BaseSkill {

	private HandCard lagunaBlade;
	
	public LagunaBlade(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean isActiveLaunchable() {
		if (player.isNPC() && super.isActiveLaunchable()) {
			return (player.getHandCardCountByColor(CardColor.CardColorRed) >= getMinHandCardCount());
		} else {
			return super.isActiveLaunchable();
		}
	}
	
	@Override
	public void resolveSelect() {
		player.setRequiredSelCardCount(getMinHandCardCount());
		player.getGamePlugin().sendPlayCardMessage(player, player.isNPC());
	}
	
	@Override
	public void resolveUse() {
		lagunaBlade = player.getTransoformedCard(getTransformedCardId(), getMinHandCardCount());
		player.getCharacter().updateHeroHpSp(0, -lagunaBlade.getRequiredSp());
		lagunaBlade.resolveUse();
	}
	
	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		player.setHeroSkill(null);
		lagunaBlade.resolveCancelByTarget(tarPlayer);
	}
	
	@Override
	public void makeHandCardAvailable() {
		player.makeHandCardAavailableByColor(CardColor.CardColorRed);
	}
	
}
