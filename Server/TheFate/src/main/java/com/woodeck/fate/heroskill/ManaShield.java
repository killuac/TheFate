/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.Deque;

import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 * 受到1点伤害便可触发
 */
public class ManaShield extends BaseSkill {
	
	public ManaShield(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isTriggered = (!callback.getPlayer().equals(player) && player.getSkillPoint() > 0 &&
							player.getHandCardCount() >= getMinHandCardCount());
		if (this.isTriggered) {
			this.callback = callback;			
			player.setHeroSkill(this);
			player.setRequiredSelCardCount(getMinHandCardCount());
			int selCount = Math.min(Math.min(player.getHandCardCount(), player.getSkillPoint()), getCallbackDamage());
			player.setSelectableCardCount(selCount);
			player.makeAllHandCardsAvailable();
			player.getGamePlugin().sendChooseCardToDropMessage(player, true);
		}
		return this.isTriggered;
	}
	
	@Override
	public void resolveUse(Deque<Integer> cardIds) {
		player.getCharacter().updateHeroHpSp(0, -cardIds.size());
		
		int damage = getCallbackDamage() - cardIds.size();
		if (damage > 0) {
			this.resolveDamage(damage);
		} else {
			player.finishResolve(callback);
		}
	}
	
	private int getCallbackDamage() {
		int damage = callback.getDamageValue();
		if (callback.isCard() && ((HandCard)callback).isAttack()) {
			damage = callback.getPlayer().getAttackDamage();
		}
		return damage;
	}
	
	private void resolveDamage(int damage) {
		if (callback.isCard() && ((HandCard)callback).isAttack()) {
//			If be damaged more than 1, maybe offset part of damage.
			this.callback.getPlayer().setAttackDamage(damage);
			this.callback.getPlayer().setAttackDamageExtra(0);
			this.callback.resolveCancelByTarget(player);
		} else {
			player.updateHeroHpSp(-damage, damage, callback);
		}
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		this.resolveDamage(this.getCallbackDamage());
	}

}
