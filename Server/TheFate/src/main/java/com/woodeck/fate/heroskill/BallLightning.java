/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;
import com.woodeck.fate.util.Constant;

/**
 * @author Killua
 * 2张手牌当【普通攻击】或【闪避】使用
 */
public class BallLightning extends BaseSkill {

	private HandCard transformedCard;	// NormalAttack or Evasion
	private boolean isNeedEvasion;
	
	public BallLightning(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public int getTransformedCardId() {
		if (this.isNeedEvasion) {			
			return this.transformedCardId2;		// Need transformed to evasion
		} else {
			return this.transformedCardId;		// Need transformed to attack including triggered by BattleHunger
		}
	}
	
	@Override
//	Only replace second "&"
	public String getHistoryText() {
		int index = historyText.lastIndexOf(Constant.REPLACE_SIGN);
		return historyText.substring(index).replaceFirst(Constant.REPLACE_SIGN, getTransformedCard().getCardText());
	}
	
	
	@Override
	public boolean isActiveLaunchable() {
//		Not transform color
		transformedCard = HandCard.newCardWithCardId(getTransformedCardId(), player);
		boolean isAvailable = (super.isActiveLaunchable() && transformedCard.isActiveUsable());
		if (player.isNPC() && isAvailable) {
			return (!player.hasAttack() && player.getHandCardCount() > player.getHandSizeLimit());
		}
		return isAvailable;
	}
	
	@Override
	public void setIsTriggered(boolean isTriggered) {
		super.setIsTriggered(isTriggered);
		if (!isTriggered) {
			this.isNeedEvasion = false;
		}
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isNeedEvasion = true;
		this.callback = callback;
		this.isPassiveLaunchable = (player.getHandCardCount() >= getMinHandCardCount());
		if (player.isNPC() && isPassiveLaunchable) {
			this.isPassiveLaunchable = (!player.hasEvasion() && player.getHandCardCount() > player.getHandSizeLimit());
		}
		return false;
	}
	
	@Override
	public void resolveSelect() {
//		Maybe damage source equipped EyeOfSkadi, self player need use 2 evasion. So need backup it.
		int preSelectableCardCount = player.getSelectableCardCount();
		player.setRequiredSelCardCount(getMinHandCardCount());
		
		if (player.isTurnOwner()) {
			player.getGamePlugin().sendPlayCardMessage(player, player.isNPC());
		} else {
			player.setRequiredTargetCount(0);
			player.getGamePlugin().sendChooseCardToUseMessage(player, player.isNPC());
			player.setSelectableCardCount(preSelectableCardCount);
		}
	}
	
	@Override
	public void resolveSelectByNPC() {
		this.resolveSelect();
	}
	
	@Override
	public void resolveUse() {
		if (this.isNeedEvasion) {
			player.setHeroSkill(null);
			this.resolveTransformedEvasion();
			return;
		}
		
//		Also transform color
		transformedCard = player.getTransoformedCard(getTransformedCardId(), getMinHandCardCount());
		transformedCard.resolveUse();
	}
	
//	If played one evasion is not enough, will ask play evasion again.
	private void resolveTransformedEvasion() {
		int usedEvasionCount = player.getUsedEvasionCount();
		if (usedEvasionCount < player.getSelectableCardCount()) {
			int selCount = player.getSelectableCardCount();
			player.setSelectableCardCount(selCount-usedEvasionCount);
			player.makeEvasionCardAvailable();
			player.getGamePlugin().sendChooseCardToUseMessage(player, true);
			player.setSelectableCardCount(selCount);
		} else {
			player.getTransoformedCard(getTransformedCardId(), getMinHandCardCount()).resolveUse();
			this.isNeedEvasion = false;
		}
	}
	
	@Override
	public void resolveContinue() {
		transformedCard.resolveContinue();
	}
	
	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		transformedCard.resolveCancelByTarget(tarPlayer);
	}
	
	@Override
	public void resolveCancel() {
		if (null != this.callback) {		// Need evasion or attack due to BattleHunger
			this.isNeedEvasion = false;
			player.setHeroSkill(null);
			callback.resolveCancelByTarget(player);
		} else {
			super.resolveCancel();
		}
	}
	
	@Override
	public void makeHandCardAvailable() {
		Player damageSource = player.getGame().getDamageSource();
		if (!player.isTurnOwner() && damageSource.getCharacter().isJuggernaut()) {
			player.makeAllHandCardsUnavailable();
			for (HandCard card : player.getHandCards()) {
				card.setIsAvailable(card.isDiamonds());		// Only use diamonds cards as evasion
			}
		} else {
			player.makeAllHandCardsAvailable();
		}
	}

}
