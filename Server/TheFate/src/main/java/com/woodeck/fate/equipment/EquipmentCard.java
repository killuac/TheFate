/**
 * 
 */
package com.woodeck.fate.equipment;

import java.lang.reflect.InvocationTargetException;

import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public abstract class EquipmentCard extends HandCard {

	protected Callback callback;
	protected boolean isTriggered;
	protected boolean isPassiveLaunchable;
	protected boolean isDroppable;	// 有的英雄可以通过弃任意牌来发动(包括装备)
	protected int usedTimes;
	
	public static EquipmentCard newEquipmentWithCardId(Integer cardId, Player player) {
		try {
			StringBuilder sbPath = new StringBuilder();
			sbPath.append(EquipmentCard.class.getPackage().getName());
			sbPath.append(".");
			sbPath.append(new PlayingCard(cardId).getCardName());
			return (EquipmentCard) EquipmentCard.class.getClassLoader().loadClass(sbPath.toString()).
					getConstructor(Integer.class, Player.class).newInstance(cardId, player);
			
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
	
	public EquipmentCard(Integer cardId, Player player) {
		super(cardId, player);
	}

	/**************************************************************************
	 * Getter and setter methods
	 */
	public void setCallback(Callback callback) {
		if (null == callback || (callback.isCard() && callback.equals(this))) return;
		this.callback = callback;
	}
	public void resetCallback() {
		this.callback = null;
	}
	
	@Override
//	装备了"圣者遗物"后不能装备防具
	public boolean isActiveUsable() {
		return (this.isWeapon() || !player.getEquipment().hasEquippedOneWeapon());
	}
	
	@Override
	public boolean isActiveLaunchable() {
		return (super.isActiveLaunchable() && player.getHandCardCount() >= getMinHandCardCount());
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
	}
	
	public boolean isDroppable() {
		return isDroppable;
	}
	public void setIsDroppable(boolean isDroppable) {
		this.isDroppable = isDroppable;
	}
	
	public boolean isEvadable() {
		return true;
	}
	
	public int getUsedTimes() {
		return usedTimes;
	}
	public void setUsedTimes(int usedtimes) {
		this.usedTimes = usedtimes;
	}
	
	/**************************************************************************
	 * Resolve methods
	 */
	@Override
	public void resolveSelect() {}
	public void resolveUse() {}
	public void resolveJudge() {}
	public void resolveOkay() {}
	
	@Override
	public void resolveSelectByNPC() {
		this.resolveSelect();
		if (this.isTargetable()) this.selectTargetByNPC();
	}
	
	@Override
	public void selectTargetByNPC() {}
	
	@Override
	public void resolveCancel() {
		if (player.getGame().somebodyIsDying()) {
			player.getGame().askForHelpFromPlayer(player.getGame().getTurnOwner(), this);
		} else {
			player.getGame().backToTurnOwner();
		}
	}
	
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.setCallback(callback);
		
		this.isTriggered = true;
		player.setEquipmentCard(this);
		player.getGamePlugin().sendChooseOkayOrCancelMessage(player);
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append(" [isTriggered=");
		builder.append(isTriggered);
		builder.append("]");
		return builder.toString();
	}
	
}
