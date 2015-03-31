/**
 * 
 */
package com.woodeck.fate.handcard;

import com.woodeck.fate.equipment.EquipmentCard;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class NormalAttack extends HandCard {
	
	private boolean isFirstResolveContinue = true;
	private boolean isResolveObtainedSp;
	
	public NormalAttack(Integer cardId, Player player) {
		super(cardId, player);
	}

	@Override
	public boolean isActiveUsable() {
		boolean isUsable = (player.isFreeAttack() || player.getAttackedTimes() < player.getAttackLimit());
		if (player.isNPC()) {
			for (Player tarPlayer : player.getGame().getOpponentPlayers()) {
				if (player.isAttackRangeEnough(tarPlayer)) {
					return isUsable;
				}
			}
			return false;
		}
		return isUsable;
	}
	
	@Override
	public void selectTargetByNPC() {
		Player tarPlayer = player.selectMinHpTargetPlayer(false);
		if (!player.isAttackRangeEnough(tarPlayer)) {
			player.getTargetPlayerNames().clear();
			for (Player p : player.getGame().getOpponentPlayers()) {
				if (player.isAttackRangeEnough(p)) {
					player.getTargetPlayerNames().add(p.getPlayerName()); break;
				}
			}
		}
	}
	
	@Override
	public void resolveUse() {
		if (player.isTurnOwner()) {		// 发动"战争饥渴"被动使用攻击(不是TurnOwner)，不计入攻击次数
			player.setAttackedTimes(player.getAttackedTimes()+1);
		}
		
		this.preTargetPlayer = player.getNotResolvedTargetPlayer();
		
		if (!player.getGame().checkEverybodyHeroSkill(TriggerReason.TriggerReasonAnyAttack, this)) {
			this.resolveResult();
		}
	}
	
	@Override
//	NOTE: May attack can specify multiple target, need resolve one by one
	public void resolveResult() {
		Player tarPlayer = player.getNotResolvedTargetPlayer();
		if (null == tarPlayer) {
			player.getGame().backToTurnOwner(); return;
		} else {
			tarPlayer.getEquipment().resetDamagedTriggerFlag();		// BladeMail can be triggered multiple times
			
//			Some hero skill/equipment maybe triggered multiple times by different target
			if (!tarPlayer.equals(this.preTargetPlayer)) {
				this.preTargetPlayer = tarPlayer;
				player.getGame().resetAllHeroSkills();
				player.getGame().resetAllEquipments();
			}
		}
		
		tarPlayer.makeEvasionCardAvailable();
		
		if (!player.checkHeroSkillAndEquipment(TriggerReason.TriggerReasonAttack, this) &&
			!tarPlayer.checkHeroSkillAndEquipment(TriggerReason.TriggerReasonBeAttacked, this)) {
			Player damageSource = player.getGame().getDamageSource();
			EquipmentCard equipCard = damageSource.getEquipmentCard();
			if (tarPlayer.getHandCardCount() == 0 ||					// Hand card is 0
				((null != equipCard) && !equipCard.isEvadable())) {		// Can't evade(SangeAndYasha)
				this.resolveCancelByTarget(tarPlayer);
			} else {
				player.setRequiredTargetCount(0);
				player.getGamePlugin().sendChooseCardToUseMessage(tarPlayer, true);
			}
		}
	}
	
	@Override
	public void resolveContinue() {
		if (!this.isFirstResolveContinue && preTargetPlayer.isContinueResolveDamage()){
			preTargetPlayer.continueResolveDamage(this);
		} else {
			this.isFirstResolveContinue = false;
			// Resolve damage source obtain SP
			if (this.isResolveObtainedSp) {
				this.isResolveObtainedSp = false;
				if (player.getDealDamagedSp() > 0) {
					this.player.updateHeroHpSp(0, player.getDealDamagedSp(), this);
				} else {
					preTargetPlayer.continueResolveDamage(this);
				}
			} else {
				this.resolveResult();
			}
		}
	}
	
	@Override
//	NOTE: May attack can specify multiple target, need resolve one by one
	public void resolveCancelByTarget(Player tarPlayer) {
		if (!player.checkHeroSkillAndEquipment(TriggerReason.TriggerReasonAttackDamage, this) &&
			!tarPlayer.checkHeroSkill(TriggerReason.TriggerReasonBeDamage, this)) {
			this.isResolveObtainedSp = true;
			
			int attackDamage = this.player.getAttackDamage();
			int obtainedSp = tarPlayer.getBeDamagedSp() + attackDamage;
			tarPlayer.updateHeroHpSp(-attackDamage, obtainedSp, this);
		}
	}
	
}
