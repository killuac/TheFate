/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.card.PlayingCard.CardColor;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class Purification extends BaseSkill {
	
	private boolean isResultContinue;
	
	public Purification(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isResultContinue = false;
		
//		伤害来源不是全能骑士自己，也不是受伤害的玩家自己，或者玩家处于濒死状态
		this.isTriggered = ((!callback.getPlayer().equals(player) &&
				!callback.getPlayer().equals(player.getGame().getDamagedPlayer()) ||
				callback.getPlayer().isDying()) &&
				(player.getHandCardCount()+player.getEquipmentCount() > 1));
		
		if (player.isNPC() && isTriggered) {
			this.isTriggered = (!callback.getPlayer().getRoleCard().isSameWithRole(player.getRoleCard()) &&
								player.getHandCardCountByColor(CardColor.CardColorRed) >= getMinHandCardCount());
		}
		
		if (this.isTriggered) {
			this.callback = callback;
			player.setHeroSkill(this);
			player.setRequiredSelCardCount(2);
			player.makeHandCardAavailableByColor(CardColor.CardColorRed);
			player.getEquipment().makeCardDroppableWithColor(CardColor.CardColorRed);
			player.getGamePlugin().sendChooseCardToDropMessage(player, true);
			return true;
		}
		return false;
	}
	
	@Override
	public void resolveResult() {
		player.setHeroSkill(null);
		this.isResultContinue = true;
		
//		受伤害的角色回复1点血量
//		玩家处于濒死状态，发动洗礼令其回复1点血，但没救活；这时在结算完玩家阵亡后，回到洗礼继续结算。
		Player damagedPlayer = player.getGame().getDamagedPlayer();
		damagedPlayer.setCallback(this);	// Back to Purification
		player.getGame().getDamagedPlayers().addFirst(damagedPlayer);
		damagedPlayer.updateHeroHpSp(1, 0, this);
	}
	
	@Override
	public void resolveContinue() {
		if (this.isResultContinue) {
			this.isResultContinue = false;
//			对伤害来源造成1点伤害
			Player damageSource = callback.getPlayer();
			player.getGame().getDamagedPlayers().addFirst(damageSource);
			
			player.getGame().setDamageSource(player);
			int damage = this.getDamageValue();
			damageSource.updateHeroHpSp(-damage, damage, this);
		}
		else {
			player.getGame().setDamageSource(callback.getPlayer());
//			Player damagedPlayer = damagedPlayers.pop();	// Will raise NoSuchElementException
			Player damagedPlayer = player.getGame().getDamagedPlayers().pollFirst();
			if (null != damagedPlayer && !player.getGame().somebodyIsDying()) {
				player.setHeroSkill(null);
				damagedPlayer.continueResolveDamage(callback);
			}
		}
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		
//		1. If some player is dying, resolve death first.
//		2. Let damage source continue resolve after cancelled
		if (player.getGame().somebodyIsDying()) {
			player.getGame().askForHelpFromPlayer(player.getGame().getTurnOwner(), callback);
		} else {
			Player damagedPlayer = player.getGame().getDamagedPlayer();
			damagedPlayer.continueResolveDamage(callback);	// Maybe continue trigger BladeMail
		}
	}

}
