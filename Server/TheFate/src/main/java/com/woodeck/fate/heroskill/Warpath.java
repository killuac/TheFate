/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.equipment.EquipmentCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class Warpath extends BaseSkill {

	BaseSkill targetHeroSkill;
	EquipmentCard targetEquipCard;
	private boolean isFirstResolveContinue;
	
	public Warpath(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.isFirstResolveContinue = true;
		return super.trigger(reason, callback);
	}
	
	@Override
	public void resolveOkay() {
		player.revealOneCardFromDeck(this);
	}
	
	@Override
	public boolean isJudgedYes() {
		return player.getJudgedCard().isRedColor();
	}
	
	@Override
	public void resolveJudge() {
		if (this.isJudgedYes()) {
			if (player.isNPC()) {
				this.selectTargetByNPC();
				this.resolveUse();
			} else {
				player.setIsRequiredTarget(true);
				player.setRequiredTargetCount(1);
				player.getGamePlugin().sendChooseTargetPlayerMessage(player, true);
			}
		} else {
			this.resolveCancel();
		}
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		this.resolveContinue();
	}
	
	@Override
	public void resolveUse() {
		Player tarPlayer = player.getTargetPlayer();
		if (tarPlayer.getHandCardCount() > 0) {
			player.getGame().setDamageSource(player);
			
			this.targetHeroSkill = tarPlayer.getHeroSkill();
			this.targetEquipCard = tarPlayer.getEquipmentCard();
			
			tarPlayer.setHeroSkill(null);
			tarPlayer.setEquipmentCard(null);
			tarPlayer.makeEvasionCardAvailable();
			tarPlayer.setRequiredSelCardCount(1);
			player.getGamePlugin().sendChooseCardToDropMessage(tarPlayer, true);
		} else {
			this.resolveCancelByTarget(tarPlayer);
		}
	}
	
	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		player.getTargetPlayer().setHeroSkill(this.targetHeroSkill);
		player.getTargetPlayer().setEquipmentCard(this.targetEquipCard);
		super.resolveCancelByTarget(tarPlayer);
	}
	
	@Override
	public void resolveContinue() {
		Player tarPlayer = player.getTargetPlayer();
		if (null != tarPlayer) {
			tarPlayer.setHeroSkill(this.targetHeroSkill);
			tarPlayer.setEquipmentCard(this.targetEquipCard);
		}
		
		if (this.isFirstResolveContinue) {
			this.isFirstResolveContinue = false;
			player.getGame().setDamageSource(callback.getPlayer());
//			Maybe equipped "BladeMail", so need continue resolve damage
			player.continueResolveDamage(callback);
		} else {
			super.resolveCancel();
		}
	}
	
}
