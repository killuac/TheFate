/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.lang.reflect.InvocationTargetException;
import java.util.Deque;

import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public abstract class BaseSkill extends HeroSkill implements Callback {

	protected Player player;
	protected Callback callback;
	
	protected boolean isTriggered;
	protected boolean isDyingTriggered;		// 技能是由于濒死触发的
	protected boolean isPassiveLaunchable;
	protected boolean isAlreadyUsed;		// 限定技(每局1次)是否已经使用过了
	protected int usedTimes;
	
	protected boolean isFirstResolveContinue = true;
	protected boolean isFirstResolveCancel = true;
	
	public static BaseSkill newHeroSkillBySkillId(Integer skillId, Player player) {
		try {
			StringBuilder sbPath = new StringBuilder();
			sbPath.append(BaseSkill.class.getPackage().getName());
			sbPath.append(".");
			sbPath.append(new HeroSkill(skillId).getSkillName());
			return (BaseSkill) BaseSkill.class.getClassLoader().loadClass(sbPath.toString()).
					getConstructor(Integer.class, Player.class).newInstance(skillId, player);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	Create instance dynamically, so parameter skillId type must be "Integer", not "int".
	public BaseSkill(Integer skillId, Player player) {
		super(skillId);
		this.player = player;
	}
	
	/**************************************************************************
	 * Getter and setter methods
	 */
	public void setCallback(Callback callback) {
		this.callback = callback;
	}
	public void resetCallback() {
		this.callback = null;
	}
	
	public boolean isActiveLaunchable() {
		boolean isUsable = (this.isActive() && player.getSkillPoint() >= this.getRequiredSp() &&
							player.getHandCardCount() >= getMinHandCardCount());
		if (this.isTurnLimitSkill()) {
			isUsable = (isUsable && usedTimes < 1);
		} else if (this.isRoundLimitSkill()) {
			isUsable = (isUsable && !isAlreadyUsed);
		}
		return isUsable;
	}
	
	public boolean isPassiveLaunchable() {
		return isPassiveLaunchable;
	}
	public void setIsPassiveLaunchable(boolean isPassiveLaunchable) {
		this.isPassiveLaunchable = isPassiveLaunchable;
	}
	
	public boolean isTriggered() {
		return isTriggered;
	}
	public void setIsTriggered(boolean isTriggered) {
		this.isTriggered = isTriggered;
		if (!isTriggered) isDyingTriggered = false;
	}
	
	public boolean isDyingTriggered() {
		return isDyingTriggered;
	}
	
	public int getUsedTimes() {
		return usedTimes;
	}
	public void setUsedTimes(int usedtimes) {
		this.usedTimes = usedtimes;
	}
	
	@Override
	public Player getPlayer() {
		return player;
	}
	
	public int getDamageValue() {
		return super.getDamageValue();
	}
	
	public boolean isCard() {
		return false;
	}
	
	public boolean isSkill() {
		return true;
	}
	
	public boolean isJudgedYes() {
		return false;
	}
	
	/**************************************************************************
	 * Resolve methods
	 */
	@Override
	public void resolveSelect() {}
	public void resolveResult(Deque<Integer> cardIds) {}
	public void resolveOkay() {}
	public void resolveJudge() {}
	public void makeHandCardAvailable() {}
	
	@Override
	public void resolveSelectByNPC() {
		this.resolveSelect();
		if (this.isTargetable()) this.selectTargetByNPC();
	}
	
	@Override
	public void selectTargetByNPC() {
		player.selectMinHpTargetPlayer(false);
	}
	
	@Override
	public void resolveUse() {
		if (this.isTurnLimitSkill()) this.usedTimes++;
		if (this.isDispellable()) player.getGame().askForDispel(this);
	}
	
	@Override
	public void resolveUse(Deque<Integer> cardIds) {
		this.resolveUse();
	}
	
	@Override
//	1. Maybe interrupted by asking dispel
//	2. Maybe interrupted by BladeMail, need back to continue resolve
	public void resolveContinue() {
		if (this.isDispellable()) {
			player.getGame().askForDispel(this);
		} else {
			this.resolveResult();
		}
	}
	
	@Override
	public void resolveCancel() {
		if (this.isFirstResolveCancel && player.getGame().somebodyIsDying()) {
			this.isFirstResolveCancel = false;
			player.getGame().askForHelpFromPlayer(player.getGame().getTurnOwner(), this);
		} else {
			player.getGame().backToTurnOwner();
		}
	}
	
	@Override
	public void resolveCancelByTarget(Player tarPlayer) {
		int damage = (getDamageValue() > 0) ? this.getDamageValue() : player.getAttackDamage();
		tarPlayer.updateHeroHpSp(-damage, damage, this);
	}
	
	@Override
	public void resolveResult() {
		player.getGame().backToTurnOwner();
	}
	
	@Override
	public void resolveResult(Player tarPlayer, Deque<Integer> cardIdxes, Deque<Integer> cardIds) {
		player.getGame().backToTurnOwner();
	}
	
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.callback = callback;
		this.isTriggered = true;
		player.setHeroSkill(this);
		player.getGamePlugin().sendChooseOkayOrCancelMessage(player);
		return true;
	}
	
}
