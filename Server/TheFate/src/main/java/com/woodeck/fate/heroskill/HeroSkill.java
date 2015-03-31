/**
 * 
 */
package com.woodeck.fate.heroskill;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.plist.NSArray;
import net.sf.plist.NSDictionary;
import net.sf.plist.NSInteger;
import net.sf.plist.NSObject;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.player.Player.TriggerReason;
import com.woodeck.fate.util.FileConstant;

/**
 * = author Killua
 *
 */
public class HeroSkill {
	
	public static final String
		kSkillCategory      = "skillCategory",
		kSkillType          = "skillType",
		kSkillName			= "skillName",
		kSkillText          = "skillText",
		kIsDispellable		= "isDispellable",
		kTargetCount        = "targetCount",
		kDamageValue		= "damageValue",
		kMaxTargetCount		= "maxTargetCount",
		kTransformedCardId	= "transformedCardId",
		kMinHandCardCount	= "minHandCardCount",
		kRequiredSp			= "requiredSp",
		kTriggerReasons		= "triggerReasons",
		
		kHistoryText		= "historyText",
		kHistoryText2		= "historyText2";
	
	public interface HeroSkillId {
		public static final int
			HeroSkillNull = 0,
			HeroSkillDeathCoil = 1,             // 死亡缠绕
			HeroSkillFrostmourne = 2,           // 霜之哀伤
	
			HeroSkillReincarnation = 3,         // 重生
			HeroSkillVampiricAura = 4,          // 吸血
	
			HeroSkillWarpath = 5,               // 战意
			HeroSkillBristleback = 6,           // 刚毛后背
	
			HeroSkillLifeBreak = 7,             // 牺牲
			HeroSkillBurningSpear = 8,          // 沸血之矛
	
			HeroSkillPurification = 9,          // 洗礼
			HeroSkillHolyLight = 10,            // 圣光
	
			HeroSkillBattleHunger = 11,         // 战争饥渴
			HeroSkillCounterHelix = 12,         // 反转螺旋
	
			HeroSkillDoubleEdge = 13,           // 双刃剑
	
			HeroSkillBreatheFire = 14,          // 火焰气息
			HeroSkillDragonBlood = 15,          // 龙族血统
	
			HeroSkillGuardian = 16,             // 援护
			HeroSkillFaith = 17,                // 信仰
			HeroSkillFatherlyLove = 18,			// 父爱
	
			HeroSkillMysticSnake = 19,          // 秘术异蛇
			HeroSkillManaShield = 20,           // 魔法护盾
	
			HeroSkillPlasmaField = 21,          // 等离子场
			HeroSkillUnstableCurrent = 22,		// 不定电流
	
			HeroSkillOmnislash = 23,            // 无敌斩
			HeroSkillBladeDance = 24,           // 剑舞
	
			HeroSkillNetherSwap = 25,           // 移形换位
			HeroSkillWaveOfTerror = 26,         // 恐怖波动
	
			HeroSkillBloodrage = 27,            // 血之狂暴
			HeroSkillStrygwyrsThirst = 28,      // 嗜血
			HeroSkillBloodBath = 29,            // 屠戮
	
			HeroSkillBattleTrance = 30,         // 战斗专注
			HeroSkillFervor = 31,               // 热血战魂
	
			HeroSkillHeadshot = 32,             // 爆头
			HeroSkillTakeAim = 33,              // 瞄准
			HeroSkillShrapnel = 34,             // 散弹
	
			HeroSkillManaBurn = 35,             // 法力燃烧
			HeroSkillVendetta = 36,             // 复仇
			HeroSkillSpikedCarapace = 37,       // 穿刺护甲
	
			HeroSkillManaBreak = 38,            // 法力损毁
			HeroSkillBlink = 39,                // 闪烁
			HeroSkillManaVoid = 40,             // 法力虚空
	
			HeroSkillTheSwarm = 41,             // 蝗虫群
			HeroSkillTimeLapse = 42,            // 时光倒流
	
			HeroSkillFurySwipes = 43,           // 怒意狂击
			HeroSkillEnrage = 44,               // 激怒
	
			HeroSkillOrdeal = 45,               // 神判
			HeroSkillSpecialBody = 46,          // 特殊体质
	
			HeroSkillFierySoul = 47,            // 炽魂
			HeroSkillLagunaBlade = 48,          // 神灭斩
			HeroSkillFanaticismHeart = 49,      // 狂热之心
	
			HeroSkillHeartstopperAura = 50,     // 竭心光环
			HeroSkillSadist = 51,               // 施虐之心
	
			HeroSkillIcePath = 52,              // 冰封
			HeroSkillLiquidFire = 53,           // 液态火
	
			HeroSkillFrostbite = 54,            // 冰封禁制
			HeroSkillBrillianceAura = 55,       // 辉煌光环
	
			HeroSkillDarkRitual = 56,           // 邪恶祭祀
			HeroSkillFrostArmor = 57,           // 霜冻护甲
	
			HeroSkillShallowGrave = 58,         // 薄葬
			HeroSkillShadowWave = 59,           // 暗影波
	
			HeroSkillFireblast = 60,            // 火焰爆轰
			HeroSkillMultiCast = 61,            // 多重施法
	
			HeroSkillIlluminate = 62,           // 冲击波
			HeroSkillChakraMagic = 63,          // 查克拉
			HeroSkillGrace = 64,				// 恩惠
	
			HeroSkillRemoteMines = 65,          // 遥控炸弹
			HeroSkillFocusedDetonate = 66,      // 引爆
			HeroSkillSuicideSquad = 67,         // 自爆
	
			HeroSkillOverload = 68,             // 超负荷
			HeroSkillBallLightning = 69,        // 球状闪电
	
			HeroSkillUntouchable = 70,          // 不可侵犯
			HeroSkillEnchant = 71,              // 魅惑
			HeroSkillNaturesAttendants = 72,    // 自然之助
	
			HeroSkillHealingSpell = 73,         // 治疗术
			HeroSkillDispelWizard = 74,         // 驱散精灵
			HeroSkillMagicControl = 75;         // 魔法掌控
	}
	
	public interface SkillCategory {
		public static final int
			SkillCategoryActive = 0,		// 主动技能
			SkillCategoryPassive = 1;		// 被动技能
	}
	
	public interface SkillType {
		public static final int
			SkillTypeGeneral = 0,			// 普通技
			SkillTypeTurnLimit =1,			// 限制技
			SkillTypeRoundLimit = 2;		// 限定技
	}
	
	private static NSArray skillList;
	
	protected Integer skillId;
	private String skillName;
	protected int skillCategory;
	private int skillType;
	private String skillText;
	
	private boolean isDispellable;
	private int targetCount;
	private int damageValue;
	private int maxTargetCount;
	private int minHandCardCount;
	private int requiredSp;
	protected int transformedCardId;
	protected int transformedCardId2;
	private List<TriggerReason> triggerReasons = new ArrayList<TriggerReason>();
	
	protected String historyText;
	protected String historyText2;
	
	
	private static NSArray sharedSkillList() {
		if (null == skillList) {
			try {
				skillList = (NSArray)PropertyListParser.parse(new File(FileConstant.PLIST_HERO_SKILL_LIST));
				
			} catch (PropertyListException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return skillList;
	}
	
	public HeroSkill(Integer skillId) {
		super();
		if (skillId <= 0) return;
		this.skillId = skillId;

		NSDictionary skillInfo = (NSDictionary)sharedSkillList().array()[skillId];
		skillName = skillInfo.get(kSkillName).toString();
		skillCategory = skillInfo.get(kSkillCategory).toInteger();
		skillType = skillInfo.get(kSkillType).toInteger();
		skillText = skillInfo.get(kSkillText).toString();
		
		isDispellable = skillInfo.get(kIsDispellable).toBoolean();
		targetCount = skillInfo.get(kTargetCount).toInteger();
		damageValue = skillInfo.get(kDamageValue).toInteger();
		maxTargetCount = skillInfo.get(kMaxTargetCount).toInteger();
		transformedCardId = skillInfo.get(kTransformedCardId).toInteger();
		minHandCardCount = skillInfo.get(kMinHandCardCount).toInteger();
		requiredSp = skillInfo.get(kRequiredSp).toInteger();
		
		if (skillInfo.get(kTransformedCardId).getClass().equals(NSInteger.class)) {
			transformedCardId = skillInfo.get(kTransformedCardId).toInteger();
		} else {
//			Some hero skill may be transformed to two different cards
			List<NSObject> cardIds = skillInfo.get(kTransformedCardId).toList();
			transformedCardId = cardIds.get(0).toInteger();
			transformedCardId2 = cardIds.get(1).toInteger();
		}
		
		if (null != skillInfo.get(kTriggerReasons)) {
			List<NSObject> reasonIds = skillInfo.get(kTriggerReasons).toList();
			for (NSObject reasonId : reasonIds) {
				triggerReasons.add(TriggerReason.getEnumById(reasonId.toInteger()));
			}
		}
		
		historyText = skillInfo.get(kHistoryText).toString();
		if (null != skillInfo.get(kHistoryText2)) historyText2 = skillInfo.get(kHistoryText2).toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (skillId == ((HeroSkill) obj).getSkillId());
	}
	
	/**
	 * Getter method
	 */
	public int getSkillId() {
		return skillId;
	}
	public String getSkillName() {
		return skillName;
	}
	public int getSkillCategory() {
		return skillCategory;
	}
	public int getSkillType() {
		return skillType;
	}
	public String getSkillText() {
		return skillText;
	}
	
	public boolean isDispellable() {
		return isDispellable;
	}
	public boolean isActive() {
		return (SkillCategory.SkillCategoryActive == skillCategory);
	}
	public boolean isTurnLimitSkill() {
		return (SkillType.SkillTypeTurnLimit == skillType);
	}
	public boolean isRoundLimitSkill() {
		return (SkillType.SkillTypeRoundLimit == skillType);
	}
	public int getTargetCount() {
		return targetCount;
	}
	public boolean isTargetable() {
		return (targetCount > 0);
	}
	public int getDamageValue() {
		return damageValue;
	}
	public int getMaxTargetCount() {
		return maxTargetCount;
	}
	public int getMinHandCardCount() {
		return minHandCardCount;
	}
	public int getRequiredSp() {
		return requiredSp;
	}
	public int getTransformedCardId() {
		return transformedCardId;
	}
	public PlayingCard getTransformedCard() {
//		transformedCardId maybe changed in subclass(e.g.BallLightning)
//		return new PlayingCard(this.transformedCardId);
		return new PlayingCard(this.getTransformedCardId());
	}
	
	public List<TriggerReason> getTriggerReasons() {
		return triggerReasons;
	}
	
	public String getHistoryText() {
		return historyText;
	}
	
//	No need target and hand card to launch the skill
	public boolean isSimple() {
		return (0 == targetCount && 0 == minHandCardCount);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HeroSkill [skillId=");
		builder.append(skillId);
		builder.append(", skillName=");
		builder.append(skillName);
		builder.append(", skillCategory=");
		builder.append(skillCategory);
		builder.append(", skillType=");
		builder.append(skillType);
		builder.append(", skillText=");
		builder.append(skillText);
		builder.append(", isDispellable=");
		builder.append(isDispellable);
		builder.append(", targetCount=");
		builder.append(targetCount);
		builder.append(", damageValue=");
		builder.append(damageValue);
		builder.append("]");
		return builder.toString();
	}
	
}
