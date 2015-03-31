/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.util.Deque;

import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public class DeathCoil extends BaseSkill {
	
	public DeathCoil(Integer skillId, Player player) {
		super(skillId, player);
	}

	@Override
	public boolean isActiveLaunchable() {
		if (player.isNPC()) {
			return (super.isActiveLaunchable() && player.getHealthPoint() > 2);
		}
		return super.isActiveLaunchable();
	}
	
	@Override
	public void resolveSelect() {
		player.getGamePlugin().sendChooseTargetPlayerMessage(player, false);
	}
	
	@Override
	public void resolveSelectByNPC() {
		this.selectTargetByNPC();
		this.resolveUse();
	}
	
	@Override
	public void selectTargetByNPC() {
		Player tarPlayer = player.selectMinHpTargetPlayer(true);	// 自己阵营作为目标
		
		if (tarPlayer.isTurnOwner() || tarPlayer.getHealthPoint() > 1) {
			player.getTargetPlayerNames().clear();
			player.selectMinHpTargetPlayer(false);					// 地方阵营作为目标
		}
	}
	
	@Override
	public void resolveUse() {
		this.usedTimes++;
		int damage = this.getDamageValue();
		player.updateHeroHpSp(-damage, damage, this);
	}
	
	@Override
	public void resolveContinue() {
		if (this.isFirstResolveContinue) {
			this.isFirstResolveContinue = false;
			player.getGame().askForDispel(this);
		} else {
			this.isFirstResolveContinue = true;
			player.getGame().backToTurnOwner();
		}
	}
	
	@Override
	public void resolveResult() {
		Player tarPlayer = player.getTargetPlayer();
//		If selected target player is full HP, must trigger damage, not restore HP.
		if (tarPlayer.getHealthPoint() == tarPlayer.getHpLimit()) {
			this.resolveOkay();
		} else {
			player.setHeroSkill(this);
			if (player.isNPC()) {
				if (tarPlayer.getRoleCard().isSameWithRole(player.getRoleCard())) {
					this.resolveCancel();	// 加血
				} else {
					this.resolveOkay();		// 造成伤害
				}
			} else {
				player.getGamePlugin().sendChooseOkayOrCancelMessage(player);
			}
		}
	}
	
	@Override
	public void resolveResult(Player tarPlayer, Deque<Integer> cardIdxes, Deque<Integer> cardIds) {
		player.setHeroSkill(null);
		tarPlayer.updateHeroHpSp(0, -1, this);
	}
	
	@Override
	public void resolveOkay() {
		Player tarPlayer = player.getTargetPlayer();
		if (tarPlayer.getHandCardCount() > 0 && tarPlayer.getSkillPoint() > 0) {
			player.getGamePlugin().sendShowPlayerHandCardMessage(player, tarPlayer);
			player.getGamePlugin().sendChooseCardToRemoveMessage(player);
		} else {
			int damage = this.getDamageValue();
			tarPlayer.updateHeroHpSp(-damage, damage, this);	// Maybe trigger "ManaShield" or "BladeMail"
		}
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		
		if (player.getGame().isDispelled()) {
			super.resolveCancel();
		} else {
			Player tarPlayer = player.getTargetPlayer();
			tarPlayer.updateHeroHpSp(1, 0, this);
		}
	}
	
}
