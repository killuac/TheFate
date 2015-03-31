/**
 * 
 */
package com.woodeck.fate.handcard;

import java.lang.reflect.InvocationTargetException;
import java.util.Deque;

import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;

/**
 * @author Killua
 *
 */
public abstract class HandCard extends PlayingCard implements Callback {

	protected Player player;
	protected Player preTargetPlayer;
	protected Callback callback;
	
	protected boolean isAvailable;
	protected boolean isStrengthened;
	
	public static HandCard newCardWithCardId(Integer cardId, Player player) {
		try {
			StringBuilder sbPath = new StringBuilder();
			sbPath.append(HandCard.class.getPackage().getName());
			sbPath.append(".");
			sbPath.append(new PlayingCard(cardId).getCardName());
//			return (HandCard) ClassLoader.getSystemClassLoader().loadClass(path).	// NullPointerException
//					getConstructor(Integer.class, Player.class).newInstance(cardId, player);
			return (HandCard) HandCard.class.getClassLoader().loadClass(sbPath.toString()).
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
	
	public HandCard(Integer cardId, Player player) {
		super(cardId);
		this.player = player;
	}
	
	/**************************************************************************
	 * Getter and setter methods
	 */
	public void setIsAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public boolean isAvailable() {
		return isAvailable;
	}
	
	public boolean isActiveUsable() {
		if (this.isSuperSkill()) {
			return (player.getSkillPoint() >= this.getRequiredSp());
		} else {
			return true;
		}
	}
	public boolean isPassiveUsable() {
		return false;
	}
	
	public boolean isStrengthened() {
		return isStrengthened;
	}
	public void setIsStrengthened(boolean isStrengthened) {
		this.isStrengthened = isStrengthened;
	}
	
	@Override
	public Player getPlayer() {
		return player;
	}
	public Player getPreTargetPlayer() {
		return preTargetPlayer;
	}
	
	public int getDamageValue() {
		return super.getDamageValue();
	}
	
	public boolean isCard() {
		return true;
	};
	
	public boolean isSkill() {
		return false;
	}
	
	public boolean isJudgedYes() {
		return false;
	}
	
	/**************************************************************************
	 * Resolve methods
	 */
	@Override
	public abstract void resolveUse();
	public void resolveUse(Deque<Integer> cardIds) {}
	public void resolveSelect() {}
	public void resolveSelectByNPC() {}
	public void resolveResult() {}
	public void resolveResult(Deque<Integer> cardIds) {}
	public void resolveResult(Player tarPlayer, Deque<Integer> cardIdxes, Deque<Integer> cardIds) {}
	public void resolveCancelByTarget(Player tarPlayer) {}
	public void resolveJudge() {}
	public void resolveContinue(Deque<Integer> cardIds) {}
	public void makeHandCardAvailable() {}
	public void makeTargetHandCardAvailable(Player tarPlayer) {}
	
	@Override
	public void selectTargetByNPC() {
		player.selectMinHpTargetPlayer(false);
	}
	
	@Override
	public void resolveContinue() {
		player.getGame().backToTurnOwner(this);
	}
	
	public void resolveStrengthen(Callback callback) {
		if (player.isNPC() && null == callback) {
			player.resolveUseHandCard(this); return;
		}
		
		this.callback = callback;
		int waitingTime = player.getGame().getPlayTime();
		player.getGame().setPlayTime(waitingTime/2);
		player.setIsStrengthening(true);
		player.getGamePlugin().sendChooseOkayOrCancelMessage(player);
		player.getGame().setPlayTime(waitingTime);
	}
	
	@Override
	public void resolveOkay() {
//		Maybe trigger skill "FierySoul", don't need consume SP while strengthening
		if (null == callback) player.getCharacter().updateHeroHpSp(0, -this.getRequiredSp());
		if (player.isStrengthening()) this.isStrengthened = true;
		this.resolveCancel();
	}
	
	@Override
	public void resolveCancel() {
		player.setIsStrengthening(false);
		
		if (null == this.callback) {
			player.resolveUseHandCard(this);
		} else {
			callback.resolveContinue();
		}
	}
	
	/**
	 * Check if the specified target player(from client) can be as a qualified target
	 */
	public boolean checkTargetPlayerAvailable() {
		return true;
	}
	
}
