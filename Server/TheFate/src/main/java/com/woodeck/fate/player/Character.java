/**
 * 
 */
package com.woodeck.fate.player;

import java.util.ArrayDeque;
import java.util.Deque;

import com.woodeck.fate.card.HeroCard;
import com.woodeck.fate.card.HeroCard.HeroCardId;
import com.woodeck.fate.heroskill.BaseSkill;
import com.woodeck.fate.player.Player.TriggerReason;

/**
 * @author Killua
 *
 */
public class Character {
	
	public interface CharacterListener {
		public void onHeroHpSpChanged(Character character, int hpChanged, int spChanged, boolean isSunder);
	}
	private CharacterListener listener;
	
	
	private Player player;
	private int heroId;
	private HeroCard heroCard;
	private int healthPoint;
	private int skillPoint;
	private Deque<BaseSkill> skills = new ArrayDeque<BaseSkill>();
	

	public Character(int heroId, Player player) {
		super();
		this.heroId = heroId;
		this.player = player;
		heroCard = new HeroCard(heroId);
		this.healthPoint = this.getHpLimit();
		
		for (Integer skillId : heroCard.getSkillIds()) {
			skills.add(BaseSkill.newHeroSkillBySkillId(skillId, player));
		}
	}
	
	/**
	 * Getter and Setter method
	 */
	public void setListener(CharacterListener listener) {
		this.listener = listener;
	}
	
	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}
	
	public HeroCard getHeroCard() {
		return heroCard;
	}
	public int getHpLimit() {
		return heroCard.getHpLimit();
	}
	public int getSpLimit() {
		return heroCard.getSpLimit();
	}
	public int getHandSizeLimit() {
		return heroCard.getHandSizeLimit();
	}

	public int getHealthPoint() {
		return healthPoint;
	}
	public int getSkillPoint() {
		return skillPoint;
	}
	
	public boolean isStrength() {
		return heroCard.isStrength();
	}
	public boolean isAgility() {
		return heroCard.isAgility();
	}
	public boolean isIntelligence() {
		return heroCard.isIntelligence();
	}
	
	public boolean isJuggernaut() {
		return (HeroCardId.HeroCardJuggernaut == heroCard.getCardId());
	}
	
	public Deque<BaseSkill> getHeroSkills() {
		return skills;
	}
	
	public BaseSkill getHeroSkillBySkillId(int skillId) {
		for (BaseSkill skill : skills) {
			if (skill.getSkillId() == skillId)
				return skill;
		}
		return null;
	}
	
	/**
	 * Hero HP and SP updating
	 */
	public void updateHeroHpSp(int hpChanged, int spChanged) {
		healthPoint += hpChanged;
		if (healthPoint > getHpLimit()) healthPoint = getHpLimit();
		
		skillPoint += spChanged;
		if (skillPoint > getSpLimit()) {
			skillPoint = getSpLimit();
			spChanged = 0;
		}
		if (skillPoint < 0) skillPoint = 0;
		
		if (hpChanged != 0 || spChanged != 0)
			listener.onHeroHpSpChanged(this, hpChanged, spChanged, false);
	}
	
	public void setHeroHpSp(int hp, int sp) {
		healthPoint = hp;
		skillPoint = sp;
		listener.onHeroHpSpChanged(this, 0, 0, true);
	}
	
	public void clearHpSp() {
		this.setHeroHpSp(0, 0);
	}
	
	public void resetTriggerFlag() {
		for (BaseSkill skill : skills) {
			skill.resetCallback();
			skill.setIsTriggered(false);
			skill.setIsPassiveLaunchable(false);
		}
	}
	
	public void resetDamagedTriggerFlag() {
		for (BaseSkill skill : skills) {
			if (skill.getTriggerReasons().contains(TriggerReason.TriggerReasonBeDamaged) ||
				skill.getTriggerReasons().contains(TriggerReason.TriggerReasonBeAttackDamaged) ||
				skill.getTriggerReasons().contains(TriggerReason.TriggerReasonAnyBeDamaged) ) {
				skill.setIsTriggered(false);
			}
		}
	}
	
//	Reset the hero skill which can be trigger multiple times by any distinct target
	public void resetConditionalTriggerFlag() {
		for (BaseSkill skill : skills) {
			if (skill.getTriggerReasons().contains(TriggerReason.TriggerReasonJudging) ||
				skill.getTriggerReasons().contains(TriggerReason.TriggerReasonAnyBeDamaged) ||
				skill.getTriggerReasons().contains(TriggerReason.TriggerReasonAnyPlayerDying)) {
				skill.setIsTriggered(false);
			}
		}
	}
	
	public void resetSkillUsedTimes() {
		for (BaseSkill skill : skills) {
			skill.setUsedTimes(0);
		}
	}
	
	/**************************************************************************
	 * Check if there is hero skill can be triggered according to trigger reason
	 */
	public boolean checkTrigger(TriggerReason reason) {		
		return this.checkTrigger(reason, null);
	}
	
	public boolean checkTrigger(TriggerReason reason, Callback callback) {
		boolean isTriggered = false;
		for (BaseSkill skill : this.skills) {
			if (!skill.isTriggered() && skill.getTriggerReasons().contains(reason)) {
				isTriggered = skill.trigger(reason, callback);
				player.getGame().getLogger().debug("Check trigger hero skill: {}", skill);
			}
		}
		return isTriggered;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Character [heroCard=");
		builder.append(heroCard);
		builder.append("]");
		return builder.toString();
	}
	
}
