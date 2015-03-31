/**
 * 
 */
package com.woodeck.fate.heroskill;

import com.woodeck.fate.card.PlayingCard.CardColor;
import com.woodeck.fate.equipment.EquipmentCard;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 * 技能【闪烁】的效果和武器【洛萨之风】效果相同
 */
public class Blink extends BaseSkill {
	
	private EquipmentCard lotharsEdge;
	
	public Blink(Integer skillId, Player player) {
		super(skillId, player);
	}
	
	@Override
	public boolean trigger(TriggerReason reason, Callback callback) {
		this.callback = callback;
		lotharsEdge = player.getEquipment().getTransformedEquipment(getTransformedCardId());
		Player damageSource = player.getGame().getDamageSource();
		this.isPassiveLaunchable = (!damageSource.getCharacter().isJuggernaut() &&
				player.getHandCardCountByColor(CardColor.CardColorBlack) >= getMinHandCardCount());
		lotharsEdge.trigger(reason, callback);
		return false;
	}
	
	@Override
	public void resolveSelect() {
		lotharsEdge.resolveSelect();
	}
	
	@Override
	public void resolveUse() {
		lotharsEdge.resolveUse();
	}
	
	@Override
	public void resolveCancel() {
		player.setHeroSkill(null);
		lotharsEdge.resolveCancel();
	}
	
	@Override
	public void makeHandCardAvailable() {
		lotharsEdge.makeHandCardAvailable();
	}
	
}
