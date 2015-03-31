/**
 * 
 */
package com.woodeck.fate.player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.card.PlayingCard.CardColor;
import com.woodeck.fate.card.PlayingCard.CardSuits;
import com.woodeck.fate.card.PlayingCard.PlayingCardEnum;
import com.woodeck.fate.card.RoleCard;
import com.woodeck.fate.equipment.EquipmentCard;
import com.woodeck.fate.game.Game;
import com.woodeck.fate.game.History;
import com.woodeck.fate.game.Security;
import com.woodeck.fate.handcard.HandCard;
import com.woodeck.fate.heroskill.BaseSkill;
import com.woodeck.fate.heroskill.HeroSkill.HeroSkillId;
import com.woodeck.fate.player.Character.CharacterListener;
import com.woodeck.fate.player.Equipment.EquipmentListener;
import com.woodeck.fate.player.Hand.HandListener;
import com.woodeck.fate.server.EsObjectExt;
import com.woodeck.fate.server.GamePlugin;
import com.woodeck.fate.util.Constant;
import com.woodeck.fate.util.PluginConstant.UpdateReason;

/**
 * @author Killua
 *
 */
public class Player implements CharacterListener, HandListener, EquipmentListener {
	
	/**
	 * Hero skill and equipment will be triggered by different trigger reason
	 */
	public enum TriggerReason {
		TriggerReasonNull (-1),
		TriggerReasonAttack (0),			// 使用攻击时
		TriggerReasonBeAttacked (1),		// 受到攻击时
		TriggerReasonCastMagic (2),			// 使用魔法牌、S技能牌或可驱散的技能时
		TriggerReasonBeCastMagic (3),		// 成为魔法牌的目标
		TriggerReasonBeAttackedByMagic (4),	// 被神灭斩/野性之斧攻击
		TriggerReasonBeDamage (5),			// 受到伤害时
		TriggerReasonBeDamage2 (12),		// 受到伤害时(≥2)
		TriggerReasonBeDamaged (6),			// 受到伤害后
		TriggerReasonAttackDamage (7),		// 攻击造成伤害时
		TriggerReasonAttackDamaged (13),	// 攻击造成伤害后
		TriggerReasonBeAttackDamaged (8),	// 受到攻击伤害后
		TriggerReasonAnyPlayerDying (9),	// 任意角色处于濒死状态
		TriggerReasonAnyPlayerDead (10),	// 任意角色死亡
		TriggerReasonDrawingCard (11),		// 摸牌阶段
		TriggerReasonPlayingCard (31),		// 出牌阶段
		TriggerReasonDiscardingCard (14),	// 弃牌阶段
		TriggerReasonJudging (15),			// 判定生效前
		TriggerReasonJudged (30),			// 判定生效后
		TriggerReasonHpRestored (16),		// 恢复血量
		TriggerReasonAnyAttack (17),		// 任意玩家使用攻击时
		TriggerReasonAnyEvasion (18),		// 任意玩家打出闪避时
		TriggerReasonAnyBeDamage (26),		// 任意玩家受到伤害时
		TriggerReasonAnyBeDamaged (19),		// 任意玩家受到伤害后
		TriggerReasonBeDispelled (20),		// 被驱散抵消后
		TriggerReasonDispelEnd (29),		// 询问驱散结束
		TriggerReasonMagicResolved (21),	// 魔法牌结算完
		TriggerReasonKilledPlayer (22),		// 杀死1名角色后
		TriggerReasonGotSp (23),			// 获得怒气
		TriggerReasonStartingTurn (24),		// 回合开始阶段
		TriggerReasonEndingTurn (25),		// 回合结束阶段
		TriggerReasonWaitingDispel (27),	// 等待驱散
		TriggerReasonPlayerIsDying (28);	// 玩家自己处于濒死状态
		
	    public final int id;
	    private static Map<Integer, TriggerReason> map;
	    
	    private TriggerReason(int id) {
	    	this.id = id;
	    	registerEnum(this);
	    }
	    
	    private static void registerEnum(TriggerReason reason) {
//	    	Must check, otherwise, create new instance for every reason enum.
	    	if (null == map)
	    		map = new HashMap<Integer, TriggerReason>();
	    	map.put(reason.id, reason);
	    }
	    
	    public static TriggerReason getEnumById(int key) {
	    	return map.get(key);
	    }
	}
	
	public interface PlayerListener {
		public void onHeroSelected(Player player);
		public void onCardCompared(Player player);
	}
	private PlayerListener listener;
	
	
	private Game game;
	private String playerName;
	private boolean isNPC;
	private boolean isHosting;		// 托管
	
	private int executionId = -1;
	
	private RoleCard roleCard;
	private Character character;
	private Hand hand;
	private Equipment equipment = new Equipment(this);
	
	private Deque<Integer> candidateHeros;
	private Deque<String> targetPlayerNames = new ArrayDeque<String>();
	
	private Deque<HandCard> usedCards = new ArrayDeque<HandCard>();
	private Deque<Integer> greededCardIds = new ArrayDeque<Integer>();
	private Deque<Integer> assignedCardIds = new ArrayDeque<Integer>();
	private int drawCardCount = Constant.DEFAULT_DRAW_CARD_COUNT;
	private int selectableCardCount = Constant.DEFAULT_SELECTABLE_CARD_COUNT;
	private int requiredSelCardCount;
	private int requiredTargetCount = 0;
	private int maxTargetCount = 0;
	private int selectedColor = CardColor.CardColorNull;
	private int selectedSuits = CardSuits.CardSuitsNull;
	private boolean isPickedHero;		// 是否点选了英雄
	private boolean isStrengthening;	// 询问是否强化
	private boolean isDisarming;
	private boolean isTimeLocking;
	private boolean isSirenSonging;		// Reset until the effect is end
	private boolean isRequiredDrop;
	private boolean isRequiredTarget;
	private boolean isContinueResolveDamage;
	private boolean isCanceledDispel;	// 是否已经取消使用驱散
	private boolean isResolved;			// 是否结算完毕
	private boolean isMadeChoice;		// 是否做了选择回应(比如"求药")
	private boolean isDead;
	
	private boolean isFreeAttack;								// 是否可使用任意次数的攻击
	private int attackLimit = Constant.DEFAULT_ATTACK_LIMIT;	// 攻击次数上限
	private int attackedTimes;									// 已使用的攻击数量
	private int attackDamage = Constant.DEFAULT_ATTACK_DAMAGE;	// 攻击可以造成的伤害值
	private int attackDamageExtra;								// 额外的攻击伤害(只影响1次攻击的效果)
	private int damagedValue;									// 受到的伤害值
	private int beDamagedSp;									// 受到伤害获得的SP
	private int dealDamagedSp;									// 造成伤害获得的SP
	private int plusDistance;	// +1
	private int minusDistance;	// -1
	private int attackRange = Constant.DEFAULT_ATTACK_RANGE;
	private int usedEvasionCount;
	private int usedSalveCount;									// 有角色处于濒死状态时，打出了几张药膏解救
	private int killCountPerTurn;
	
	private PlayingCard judgedCard;
	private BaseSkill heroSkill;
	private EquipmentCard equipmentCard;
	private Callback callback;
	private Player sirenTarget;			// The player who is target of siren
	
	private int killEnemyCount;
	private int doubleKillCount;
	private int tripleKillCount;
	private boolean isEscaped;
	private boolean isVictory;
	
	private List<History> gameHistory = new ArrayList<History>();
	
	
	public Player(Game game, String playerName, boolean isNPC) {
		this.game = game;
		this.playerName = playerName;
		this.isNPC = this.isHosting = isNPC;
		
		equipment.setListener(this);	// Register listener
	}
	
	@Override
	public boolean equals(Object obj) {
		return playerName.equals(((Player) obj).getPlayerName());
	}
	
	/**
	 * Reset value after one card is resolved(结算完)
	 */
	public void resetValueAfterResolved() {
		targetPlayerNames.clear();
		usedCards.clear();
		greededCardIds.clear();
		assignedCardIds.clear();
		drawCardCount = Constant.DEFAULT_DRAW_CARD_COUNT;
		selectableCardCount = Constant.DEFAULT_SELECTABLE_CARD_COUNT;
		requiredSelCardCount = 0;
		requiredTargetCount = 0;
		maxTargetCount = 0;
		isStrengthening = false;
		isDisarming = false;
		isTimeLocking = false;
		isRequiredDrop = false;
		isRequiredTarget = false;
		isContinueResolveDamage = false;
		isCanceledDispel = false;
		isMadeChoice = false;
		isResolved = false;
		selectedColor = CardColor.CardColorNull;
		selectedSuits = CardSuits.CardSuitsNull;
		damagedValue = 0;
		beDamagedSp = 0;
		dealDamagedSp = 0;
		attackDamageExtra = 0;
		usedEvasionCount = 0;
		judgedCard = null;
		this.setHeroSkill(null);
		this.setEquipmentCard(null);
		callback = null;
		character.resetTriggerFlag();
		equipment.resetTriggerFlag();
		this.makeAllHandCardsUnavailable();
	}
	
	public void resetValueAfterTurnEnd() {
		this.resetValueAfterResolved();
		isFreeAttack = false;
		attackLimit = Constant.DEFAULT_ATTACK_LIMIT;
		attackedTimes = 0;
		attackDamage = Constant.DEFAULT_ATTACK_DAMAGE;
		character.resetSkillUsedTimes();
		equipment.resetEquipmentUsedTimes();
		killCountPerTurn = 0;
	}
	
	public void resetTargetPlayerResolveFlag() {
		for (Player tarPlayer : this.getTargetPlayers()) {
			tarPlayer.isResolved = false;
		}
	}
	
	/**************************************************************************
	 * Getter and Setter method
	 */
	public Game getGame() {
		return game;
	}
	public GamePlugin getGamePlugin() {
		return game.getPlugin();
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public boolean isNPC() {
		return isNPC;
	}
	public void setIsNPC(boolean isNPC) {
		this.isNPC = isNPC;
	}
	
	public boolean isHosting() {
		return isHosting;
	}
	public void setIsHosting(boolean isHosting) {
		this.isHosting = isHosting;
	}
	
	public boolean isTurnOwner() {
		return (null != game.getTurnOwner() && this.equals(game.getTurnOwner()));
	}
	
	public boolean isDamageSource() {
		return this.equals(game.getDamageSource());
	}
	
	public boolean isTarget() {
		return (game.getTurnOwner().getTargetPlayerNames().contains(playerName));
	}
	
//	Register player listener
	public void setListener(PlayerListener listener) {
		this.listener = listener;
	}
	
	public int getExecutionId() {
		return executionId;
	}
	public void setExecutionId(int executionId) {
		this.executionId = executionId;
	}
	
	public RoleCard getRoleCard() {
		return roleCard;
	}
	public void setRoleCard(RoleCard roleCard) {
		this.roleCard = roleCard;
	}
	public int getRoleCardId() {
		return roleCard.getCardId();
	}
	
	public Character getCharacter() {
		return character;
	}
	public String getHeroName() {
		return character.getHeroCard().getCardName();
	}
	
	public Hand getHand() {
		return hand;
	}
	
	public Equipment getEquipment() {
		return equipment;
	}
	
	public Deque<Integer> getCandidateHeros() {
		return candidateHeros;
	}
	public void setCandidateHeros(Deque<Integer> candidateHeros) {
		this.candidateHeros = candidateHeros;
	}
	
//	获得目标玩家列表：从turnOwner开始，按照座位顺序逆时针方向获取(为了使目标玩家按顺序逐个结算)
	public Deque<String> getTargetPlayerNames() {
		return targetPlayerNames;
	}
	public void setTargetPlayerNames(Deque<String> targetPlayerNames) {
		if (!targetPlayerNames.isEmpty()) {
			for (String tarName : targetPlayerNames) {
				Player tarPlayer = game.getPlayerByName(tarName);
//				Check if the target player is SirenSonging
				if (tarPlayer.isSirenSonging) targetPlayerNames.remove(tarName);
			}
			if (targetPlayerNames.isEmpty()) {
				game.backToTurnOwner();
			} else {
				this.targetPlayerNames = targetPlayerNames;
			}
		}
	}
	public Deque<Player> getTargetPlayers() {
		Deque<Player> targetPlayers = new ArrayDeque<Player>(targetPlayerNames.size());
		for (String name : this.targetPlayerNames) {
			targetPlayers.add(game.getPlayerByName(name));
		}
		return targetPlayers;
	}
	public Player getTargetPlayer() {
		return this.getTargetPlayers().peekFirst();
	}
//	Call below two method if need resolve target player one by one
	public Deque<Player> getSortedTargetPlayers() {
		Deque<Player> targetPlayers = new ArrayDeque<Player>(targetPlayerNames.size());
		for (Player player : game.getSortedAlivePlayers()) {
			if (targetPlayerNames.contains(player.playerName))
				targetPlayers.add(player);
		}
		return targetPlayers;
	}
	public Player getNotResolvedTargetPlayer() {
		for (Player tarPlayer : this.getSortedTargetPlayers()) {
			if (!tarPlayer.isResolved)
				return tarPlayer;
		}
		return null;
	}
	
	public int getSelectedHeroId() {
		if (null == character) {		
			// Start game, player hasn't select hero, but another escaped that leads to game over.
			this.selectHero(this.getCandidateHeros().peekFirst(), false);
		}
		return character.getHeroId();
	}
	public String getSelectedHeroName() {
		return character.getHeroCard().getCardName();
	}
	
	public Deque<Integer> getHandCardIds() {
		return hand.getCardIds();
	}
	public Deque<HandCard> getHandCards() {
		return hand.getCards();
	}
	public Deque<HandCard> getSameSuitsHandCards() {
		Deque<HandCard> cards = new ArrayDeque<HandCard>();
		List<Integer> cardSuits = Arrays.asList(0, 1, 2, 3);
		for (Integer suits : cardSuits) {
			for (HandCard card : this.getHandCards()) {
				if (card.getCardSuits() == suits) {
					cards.add(card);
				}
			}
			if (cards.size() > 1) return cards;
		}
		return cards;
	}
	
	public int getHandCardCount() {
		return this.getHandCardIds().size();
	}
	public int getHandCardCountByColor(int cardColor) {
		Deque<HandCard> cards = new ArrayDeque<HandCard>();
		for (HandCard card : this.getHandCards()) {
			if (card.getCardColor() == cardColor) {
				cards.add(card);
			}
		}
		return cards.size();
	}
	public int getHandCardCountBySuits(int cardSuits) {
		Deque<HandCard> cards = new ArrayDeque<HandCard>();
		for (HandCard card : this.getHandCards()) {
			if (card.getCardSuits() == cardSuits) {
				cards.add(card);
			}
		}
		return cards.size();
	}
	
	public int getHandSizeLimit() {
		return character.getHandSizeLimit();
	}
	
	public int getHealthPoint() {
		return character.getHealthPoint();
	}
	public int getHpLimit() {
		return character.getHpLimit();
	}
	
	public int getSkillPoint() {
		return character.getSkillPoint();
	}
	public int getSpLimit() {
		return character.getSpLimit();
	}
	
	public Deque<Integer> getEquipmentCardIds() {
		return equipment.getCardIds();
	}
	
	public int getEquipmentCount() {
		return this.getEquipmentCardIds().size();
	}
	
	public Deque<Integer> getGreededCardIds() {
		return greededCardIds;
	}
	public void setGreededCardIds(Deque<Integer> greededCardIds) {
		this.greededCardIds = greededCardIds;
	}
	
	public Deque<Integer> getAssignedCardIds() {
		return assignedCardIds;
	}
	public void setAssignedCardIds(Deque<Integer> assignedCardIds) {
		this.assignedCardIds = assignedCardIds;
	}
	
	public int getDrawCardCount() {
		return drawCardCount;
	}
	public void setDrawCardCount(int drawCardCount) {
		this.drawCardCount = drawCardCount;
	}
	public int getSelectableCardCount() {
		return selectableCardCount;
	}
	public void setSelectableCardCount(int selectableCardCount) {
		this.selectableCardCount = selectableCardCount;
	}
	public int getRequiredSelCardCount() {
		return requiredSelCardCount;
	}
	public void setRequiredSelCardCount(int requiredSelCardCount) {
		this.requiredSelCardCount = requiredSelCardCount;
		this.selectableCardCount = requiredSelCardCount;
	}
	public int getRequiredTargetCount() {
		return requiredTargetCount;
	}
	public void setRequiredTargetCount(int requiredTargetCount) {
		this.requiredTargetCount = requiredTargetCount;
		this.maxTargetCount = requiredTargetCount;
	}
	public int getMaxTargetCount() {
		return maxTargetCount;
	}
	public void setMaxTargetCount(int maxTargetCount) {
		this.maxTargetCount = maxTargetCount;
	}
	
	public int getSelectedColor() {
		return selectedColor;
	}
	public int getSelectedSuits() {
		return selectedSuits;
	}
	
	public boolean isPickedHero() {
		return isPickedHero;
	}
	
	public boolean isStrengthening() {
		return isStrengthening;
	}
	public void setIsStrengthening(boolean isStrengthening) {
		this.isStrengthening = isStrengthening;
	}
	
	public boolean isDisarming() {
		return isDisarming;
	}
	public void setIsDisarming(boolean isDisarming) {
		this.isDisarming = isDisarming;
	}
	
	public boolean isTimeLocking() {
		return isTimeLocking;
	}
	public void setIsTimeLocking(boolean isTimeLocking) {
		this.isTimeLocking = isTimeLocking;
	}
	
	public boolean isSirenSonging() {
		return isSirenSonging;
	}
	public void setIsSirenSong(boolean isSirenSonging) {
		this.isSirenSonging = isSirenSonging;
	}
	
	public boolean isRequiredDrop() {
		return isRequiredDrop;
	}
	public void setIsRequiredDrop(boolean isRequiredDrop) {
		this.isRequiredDrop = isRequiredDrop;
	}
	
	public boolean isRequiredTarget() {
		return isRequiredTarget;
	}
	public void setIsRequiredTarget(boolean isRequiredTarget) {
		this.isRequiredTarget = isRequiredTarget;
	}
	
	public boolean isCanceledDispel() {
		return isCanceledDispel;
	}
	public void setIsCanceledDispel(boolean isCanceledDispel) {
		this.isCanceledDispel = isCanceledDispel;
	}
	
	public boolean isContinueResolveDamage() {
		return isContinueResolveDamage;
	}
	
	public boolean isMadeChoice() {
		return isMadeChoice;
	}
	public void setIsMadeChoice(boolean isMadeChoice) {
		this.isMadeChoice = isMadeChoice;
	}
	
	public boolean isResolved() {
		return isResolved;
	}
	public void setIsResolved(boolean isResolved) {
		this.isResolved = isResolved;
	}
	
	public boolean isDying() {
//		Check character if is null for rejoin game
		return (null != character && this.getHealthPoint() <= 0);
	}
	public boolean isDyingSkillTriggered() {
		return (null != heroSkill && heroSkill.isDyingTriggered());
	}
	
	public boolean isDead() {
		return isDead;
	}
	public void setIsDead(boolean isDead) {
		game.getPlugin().sendPlayerIsDeadPublicMessage(this);
		
		this.isDead = isDead;
		this.character.clearHpSp();
		
		game.getPlugin().sendOkayToDiscardPublicMessage(this, getHandCardIds());
		game.getTableCardIds().addAll(getHandCardIds());
		this.hand.removeAllCardIds(UpdateReason.UpdateReasonTable);
		
		game.getPlugin().sendOkayToDiscardPublicMessage(this, getEquipmentCardIds());
		game.getTableCardIds().addAll(getEquipmentCardIds());
		this.equipment.removeAllCardIds(UpdateReason.UpdateReasonTable);
	}
	
	public boolean isFreeAttack() {
		return isFreeAttack;
	}
	public void setIsFreeAttack(boolean isFreeAttack) {
		this.isFreeAttack = isFreeAttack;
	}
	public int getAttackedTimes() {
		return attackedTimes;
	}
	public void setAttackedTimes(int attackedTimes) {
		this.attackedTimes = attackedTimes;
	}
	public int getAttackLimit() {
		return attackLimit;
	}
	public void setAttackLimit(int attackLimit) {
		this.attackLimit = attackLimit;
	}
	public int getAttackDamage() {
		return attackDamage+attackDamageExtra;
	}
	public void setAttackDamage(int attackDamage) {
		this.attackDamage = attackDamage;
	}
	public int getAttackDamageExtra() {
		return attackDamageExtra;
	}
	public void setAttackDamageExtra(int attackDamageExtra) {
		this.attackDamageExtra = attackDamageExtra;
	}
	
	public int getDamagedValue() {
		return damagedValue;
	}
	public int getBeDamagedSp() {
		return beDamagedSp;
	}
	public void setBeDamagedSp(int beDamagedSp) {
		this.beDamagedSp = beDamagedSp;
	}
	public int getDealDamagedSp() {
		return dealDamagedSp;
	}
	public void setDealDamagedSp(int dealDamagedSp) {
		this.dealDamagedSp = dealDamagedSp;
	}
	
	public int getPlusDistance() {
		return plusDistance;
	}
	public void setPlusDistance(int plusDistance) {
		this.plusDistance = plusDistance;
	}
	public int getMinusDistance() {
		return minusDistance;
	}
	public void setMinusDistance(int minusDistance) {
		this.minusDistance = minusDistance;
	}
	public int getAttackRange() {
		return (attackRange + Math.abs(minusDistance));
	}
	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}
	
	public int getUsedEvasionCount() {
		return usedEvasionCount;
	}
	public void updateUsedEvasionCount(int usedEvasionCount) {
		if (null != this.heroSkill) {
			// If the transformed card is Evasion or LotharsEdge
			if (!heroSkill.getTransformedCard().isEvasion() && !heroSkill.getTransformedCard().isEquipment()) return;
		} else if (null != this.equipmentCard) {
			if (!equipmentCard.getTransformedCard().isEvasion()) return;
		} else {
			if (!this.getFirstUsedCard().isEvasion()) return;
		}
		
		this.usedEvasionCount += usedEvasionCount;
	}
	
	public int getUsedSalveCount() {
		return usedSalveCount;
	}
	
	public int getKillCountPerTurn() {
		return killCountPerTurn;
	}
	public void addKillCountPerTurn(int killCountPerTurn) {
		this.killCountPerTurn += killCountPerTurn;
	}
	
	public PlayingCard getJudgedCard() {
		return judgedCard;
	}
	public void setJudgedCard(PlayingCard judgedCard) {
		this.judgedCard = judgedCard;
	}
	
	public BaseSkill getHeroSkill() {
		return heroSkill;
	}
	public void setHeroSkill(BaseSkill heroSkill) {
		this.heroSkill = heroSkill;
		if (null == heroSkill) game.getPlugin().sendClearHeroSkillPublicMessage(this);
	}
	public int getHeroSkillId() {
		return (null != heroSkill) ? heroSkill.getSkillId() : HeroSkillId.HeroSkillNull;
	}
	public BaseSkill getHeroSkillBySkillId(int skillId) {
		return character.getHeroSkillBySkillId(skillId);
	}
	
	public EquipmentCard getEquipmentCard() {
		return equipmentCard;
	}
	public void setEquipmentCard(EquipmentCard equipmentCard) {
		this.equipmentCard = equipmentCard;
		if (null == heroSkill) game.getPlugin().sendClearEquipmentPublicMessage(this);
	}
	public int getEquipmentId() {
		return (null != equipmentCard) ? equipmentCard.getCardId() : PlayingCardEnum.PlayingCardNull.id;
	}
	public EquipmentCard getEquipmentCardByCardId(int cardId) {
		return equipment.getEquipmentByCardId(cardId);
	}
	
	public int getTransformedCardId() {
		if (null != this.heroSkill) {
			return heroSkill.getTransformedCardId();
		} else if (null != this.equipmentCard) {
			return equipmentCard.getTransformedCardId();
		}
		return PlayingCardEnum.PlayingCardNull.id;
	}
	
	public Callback getCallback() {
		return callback;
	}
	public void setCallback(Callback callback) {
		this.callback = callback;
	}
	
	public Player getSirenTarget() {
		return sirenTarget;
	}
	public void setSirenTarget(Player sirenTarget) {
		this.sirenTarget = sirenTarget;
	}
	
	public boolean isOnlyUseCard() {
		return (null == this.heroSkill && null == this.equipmentCard);
	}
	
	public boolean hasDispel() {
		for (HandCard card : getHandCards()) {
			if (card.isDispel()) return true;
		}
		return false;
	}
	
	public boolean hasEvasion() {
		for (HandCard card : getHandCards()) {
			if (card.isEvasion()) return true;
		}
		return false;
	}
	
	public boolean hasAttack() {
		for (HandCard card : getHandCards()) {
			if (card.isAttack()) return true;
		}
		return false;
	}
	
	/**************************************************************************
	 * Game result: Getter and Setter method
	 */	
	public int getKillEnemyCount() {
		return killEnemyCount;
	}
	public void addKillEnemyCount(int killEnemyCount) {
		this.killEnemyCount += killEnemyCount;
	}

	public int getDoubleKillCount() {
		return doubleKillCount;
	}
	public void addDoubleKillCount(int doubleKillCount) {
		this.doubleKillCount += doubleKillCount;
	}

	public int getTripleKillCount() {
		return tripleKillCount;
	}
	public void addTripleKillCount(int tripleKillCount) {
		this.tripleKillCount += tripleKillCount;
	}

	public boolean isEscaped() {
		return isEscaped;
	}
	public void setIsEscaped(boolean isEscaped) {
		this.isEscaped = isEscaped;
		
		if (getGame().checkGameOver()) {
			this.getGame().gameOver(); return;
		}
		
//		The player is turn starting or need playing, but he escaped from game. So reschedule.
		game.getPlugin().sendPlayerIsEscapedPublicMessage(this);
		if (executionId != -1) executionId = game.getPlugin().scheduleExecution(this);
	}

	public boolean isVictory() {
		return isVictory;
	}
	public void setIsVictory(boolean isVictory) {
		this.isVictory = isVictory;
	}
	
	public List<History> getGameHistory() {
		return gameHistory;
	}
	public void addGameHistory(EsObjectExt esObj) {
		History history = new History(this, esObj);
		if (!history.getText().isEmpty()) gameHistory.add(history);
	}
	
	/**************************************************************************
	 * Used card updating and getting
	 */
	public void addUsedCard(int cardId) {
		this.usedCards.add(hand.cardByCardId(cardId));
	}
	
	public void addUsedCards(Deque<Integer> cardIds) {
		this.usedCards.addAll(hand.cardsByCardIds(cardIds));
	}
	
	public Deque<HandCard> getUsedCards() {
		return usedCards;
	}
	
	public HandCard getFirstUsedCard() {
		return this.getUsedCards().peekFirst();
	}
	public int getFirstUsedCardId() {
		return (null != getFirstUsedCard()) ? getFirstUsedCard().getCardId() : 0;
	}
	
	public HandCard getLastUsedCard() {
		return this.getUsedCards().peekLast();
	}
	public int getLastUsedCardId() {
		return (null != getLastUsedCard()) ? getLastUsedCard().getCardId() : 0;
	}
	
	public Deque<HandCard> getLastUsedCardsByCount(int count) {
		Deque<HandCard> cards = new ArrayDeque<HandCard>(count);
		Iterator<HandCard> itr = getUsedCards().descendingIterator();
		int index = 0;
		while (itr.hasNext()) {
			if (index >= count) break;
			cards.add(itr.next()); index++;
		}
		return cards;
	}
	
	public int getUsedCardCount() {
		return usedCards.size();
	}
	
	/**************************************************************************
	 * Select hero and initial character with hero id
	 * Call method onHeroSelected of listener to deal hand card
	 */
	public void selectHero(int heroId, boolean isPicked) {
		this.isPickedHero = isPicked;
		
		character = new Character(heroId, this);
		character.setListener(this);
		
		listener.onHeroSelected(this);
		this.candidateHeros = null;		// Free memory
	}
	
	public void initHandWithCardIds(Deque<Integer> cardIds) {
//		cardIds.addAll(Arrays.asList(100));	// TODO: Hard code
		hand = new Hand(this, cardIds);
		hand.setListener(this);
	}
	
	public void choseCardToCompare(Integer cardId) {
		this.addUsedCard(cardId);
//		Remove the compared card from hand until all players completed selection
//		hand.removeCardId(cardId, PluginConstant.kUpdateReasonTable);
		listener.onCardCompared(this);
	}
	
	public void removeComapredCardFromHand() {
		hand.removeCardId(usedCards.peekFirst().getCardId(), UpdateReason.UpdateReasonTable);
	}
	
	/**
	 * Listener method implementation
	 */
	@Override
	public void onHeroHpSpChanged(Character character, int hpChanged, int spChanged, boolean isSunder) {
		game.getPlugin().sendUpdateHeroPublicMessage(this, hpChanged, spChanged, isSunder);
	}
	
	@Override
	public void onHandCardChanged(Hand hand, int countChanged, UpdateReason reason) {
		game.getPlugin().sendUpdateHandCardMessage(this, countChanged, reason);
	}
	
	@Override
	public void onEquipmentChanged(Equipment equipment, int countChanged, UpdateReason reason) {
		game.getPlugin().sendUpdateEquipmentMessage(this, countChanged, reason);
	}
	
	/**************************************************************************
	 * When a target player is resolved finish, Check if still have not resolved player.
	 * Back to turn owner until all target players are resolved finish
	 */
	public void finishResolve(Callback callback) {
//		getGame().getLogger().debug("Call stack trace: {}", Arrays.toString(Thread.currentThread().getStackTrace()));		
		this.isResolved = true;
		
		if (null == callback) {
			game.backToTurnOwner(callback);
		} else {
			callback.resolveContinue();
		}
	}
	
	public void makeChoice() {		// Save somebody and greed will call the method
		this.isMadeChoice = true;
		
		if (game.somebodyIsDying()) {
			game.askForNextHelper(game.getDyingPlayer().callback);
		} else {
			if (null != game.getDyingPlayer()) {
				game.resetAllPlayersMadeChoice();
//				game.getDyingPlayer().continueResolveDamage(callback);
//				Continue resolve dying player's callback, not the player who used HealingSalve.
				game.getDyingPlayer().continueResolveDamage(game.getDyingPlayer().callback);
			} else {
				if (null != this.callback) callback.resolveContinue();
			}
		}
	}
	
	/**************************************************************************
	 * Start turn, play card and discard card
	 */
	public void startTurn() {
		game.getPlugin().sendStartTurnMessage(this);
		game.setDamageSource(game.getTurnOwner());
		
		if (!this.checkHeroSkillAndEquipment(TriggerReason.TriggerReasonStartingTurn) &&
			!this.checkHeroSkillAndEquipment(TriggerReason.TriggerReasonDrawingCard)) {
			this.drawCard(drawCardCount);
			this.playCard();
		}
	}
	
	public void startDrawCardAndPlay() {
		if (!this.checkHeroSkillAndEquipment(TriggerReason.TriggerReasonDrawingCard)) {
			this.drawCard(drawCardCount);
			this.playCard();
		}
	}
	
	public void drawCard(int count) {
		Deque<Integer> cardIds = game.fetchPlayingCardIds(count);
		game.getPlugin().sendDrawCardPublicMessage(this, count);
		hand.addCardIds(cardIds, UpdateReason.UpdateReasonDefault);
	}
	
	public void playCard() {
		if (!this.checkHeroSkill(TriggerReason.TriggerReasonPlayingCard)) {
			this.resetValueAfterResolved();
			this.makeActiveHandCardAvailable();
			game.setDamageSource(game.getTurnOwner());	// Damage source is turn owner by default
			game.getPlugin().sendPlayCardMessage(this, true);
		}
	}
	
	public void startDiscard() {
		game.getPlugin().sendStartDiscardPublicMessage(this);
		this.discardCard();
	}
	
	public void discardCard() {
		this.setRequiredSelCardCount(getHandCardCount() - getHandSizeLimit());
		if (this.requiredSelCardCount > 0) {
			this.makeAllHandCardsAvailable();
			game.getPlugin().sendDiscardCardMessage(this);
		} else {
			if (this.checkHeroSkill(TriggerReason.TriggerReasonEndingTurn)) return;
			
			if (this.isTimeLocking) {
				game.resetSirenTargetOfPlayer(this);
				game.resetValue();
				this.resetValueAfterTurnEnd();
				this.startTurn();
			} else {
				game.turnToNextPlayer();
			}
		}
	}
	
	public void okayToDiscard(Deque<Integer> cardIds) {
		game.getPlugin().sendOkayToDiscardPublicMessage(this, cardIds);
		
		hand.removeCardIds(cardIds, UpdateReason.UpdateReasonTable);
		game.getTableCardIds().addAll(cardIds);
		this.discardCard();
	}
	
//	Fetch hand cardIds to discard
	public Deque<Integer> fetchHandCardIds(int count) {
		Deque<Integer> cardIds = new ArrayDeque<Integer>(count);
		List<Integer> handCardIds = new ArrayList<Integer>(getHandCardIds());
		for (int i = 0; i < count; i++) {
			cardIds.add(handCardIds.get(i));
		}
		return cardIds;
	}
	
	/**************************************************************************
	 * NPC start play card or use hero skill
	 */
	public void autoActivePlayCard() {
		Deque<Integer> skillIds = this.getAvailableSkillIdList(true);
		if (skillIds.size() > 0) {
			this.useHeroSkill(skillIds.peekFirst()); return;
		}
		
		Deque<Integer> equipIds = this.getAvailableEquipIdList(true);
		if (equipIds.size() > 0) {
			this.useEquipement(equipIds.peekFirst()); return;
		}
		
		Deque<Integer> cardIds = this.getAvailableCardIdList();
		if (cardIds.size() == 0) {
			this.startDiscard();
			return;
		}
		
//		First use equipment card, then use other cards.
		int usedCardId = cardIds.peekFirst();
		for (Integer cardId : cardIds) {
			if (hand.cardByCardId(cardId).isEquipment()) {
				usedCardId = cardId; break;
			}
		}
		HandCard card = hand.cardByCardId(usedCardId);
		if (card.isTargetable()) card.selectTargetByNPC();
		this.useHandCard(new ArrayDeque<Integer>(Arrays.asList(usedCardId)));
	}
	
	public void autoPassivePlayCard(Deque<Integer> cardIds) {
		Deque<Integer> skillIds = this.getAvailableSkillIdList(false);
		if (skillIds.size() > 0) {
			this.useHeroSkill(skillIds.peekFirst()); return;
		}
		
		Deque<Integer> equipIds = this.getAvailableEquipIdList(false);
		if (equipIds.size() > 0) {
			this.useEquipement(equipIds.peekFirst()); return;
		}
		
		this.autoChoseCardToUse(cardIds);
	}
	
//	自动使用闪避时必须要能抵消来源的卡牌效果(比如只有1张闪避，但需要打出2张才能抵消，这时不会自动打出)
//	自动使用驱散时目标必须是自己，不要驱散自己使用的魔法牌
	public void autoChoseCardToUse(Deque<Integer> cardIds) {
		int lastTabCardId = game.getLastTableCardId();
		int lastUsedCardId = this.getLastUsedCardId();
		
		if (this.isTarget() && cardIds.size() > 0 &&
			(null == this.heroSkill || lastUsedCardId == 0 || lastTabCardId != lastUsedCardId)) {
			this.choseCardToUse(cardIds);
		} else {
			this.choseCancel();
		}
	}
	
//	Select the target player with minimal HP
	public Player selectMinHpTargetPlayer(boolean isPartner) {
		Player tarPlayer = null;
		List<Player> tarPlayers = (isPartner) ? game.getPartnerPlayers() : game.getOpponentPlayers();
		for (Player player : tarPlayers) {
			if (null == tarPlayer || player.getHealthPoint() < tarPlayer.getHealthPoint()) {
				tarPlayer = player;
			}
		}
		this.targetPlayerNames.add(tarPlayer.getPlayerName());
		
		return tarPlayer;
	}
	
//	Select the target player with maximum HP
	public Player selectMaxHpTargetPlayer(boolean isPartner) {
		Player tarPlayer = null;
		List<Player> tarPlayers = (isPartner) ? game.getPartnerPlayers() : game.getOpponentPlayers();
		for (Player player : tarPlayers) {
			if (null == tarPlayer || player.getHealthPoint() > tarPlayer.getHealthPoint()) {
				tarPlayer = player;
			}
		}
		this.targetPlayerNames.add(tarPlayer.getPlayerName());
		
		return tarPlayer;
	}
	
//	选中手牌上限和手牌数的差值最小的玩家作为目标
	public Player selectMinDeltaCardCountTargetPlayer(boolean isPartner) {
		Player tarPlayer = game.getOpponentPlayers().get(0);
		List<Player> tarPlayers = (isPartner) ? game.getPartnerPlayers() : game.getOpponentPlayers();
		for (Player player : tarPlayers) {
			int deltaCount = player.getHandSizeLimit() - player.getHandCardCount();
			int tarDeltaCount = tarPlayer.getHandSizeLimit() - tarPlayer.getHandCardCount();
			if (null == tarPlayer || deltaCount < tarDeltaCount) {
				tarPlayer = player;
			}
		}
		this.targetPlayerNames.add(tarPlayer.getPlayerName());
		
		return tarPlayer;
	}
	
//	Select the target player whose hand card count is not 0
	public void selectHandCardTargetPlayer() {
		Player tarPlayer = this.selectMinHpTargetPlayer(false);
		if (tarPlayer.getHandCardCount() == 0) {
			targetPlayerNames.clear();
			for (Player p : game.getOpponentPlayers()) {
				if (p.getHandCardCount() > 0) {
					targetPlayerNames.add(p.getPlayerName()); break;
				}
			}
		}
	}
	
//	Select the target player who equipped equipment card
	public void selectEquipmentTargetPlayer() {
		Player tarPlayer = this.selectMinHpTargetPlayer(false);
		if (tarPlayer.getEquipmentCount() == 0) {
			targetPlayerNames.clear();
			for (Player p : game.getOpponentPlayers()) {
				if (p.getEquipmentCount() > 0) {
					targetPlayerNames.add(p.getPlayerName()); break;
				}
			}
		}
	}
	
	public boolean isAttackRangeEnough(Player tarPlayer) {
		int halfCount = (int) Math.floor(game.getAlivePlayerCount()/2.0);
		int index = game.getSortedAlivePlayers().indexOf(tarPlayer);
		int distance = 1;
		if (index <= halfCount) {
			distance += tarPlayer.getPlusDistance() + index - 1;
		} else {
			distance += tarPlayer.getPlusDistance() + game.getAlivePlayerCount()-index - 1;
		}
		
		return (this.getAttackRange() >= distance);
	}
	
	/**************************************************************************
	 * Send public message and also cancel the scheduled execution
	 * 1. Check if there is hero skill or equipment can be triggered
	 * 2. Use hand card with selected hero skill or equipment
	 * 3. Check if there is dispel can be triggered
	 * 4. Using card logic: Turn owner player use hand card
	 */
	public void useHandCard(Deque<Integer> cardIds) {
		this.addUsedCards(cardIds);
		if (Security.checkCheating(game, this)) return;
		
		game.getPlugin().sendUseHandCardPublicMessage(this, cardIds);
		hand.removeCardIds(cardIds, UpdateReason.UpdateReasonDefault);
		game.getTableCardIds().addAll(cardIds);
		
//		Use hand card with selected hero skill or equipment
		if (null != this.heroSkill) {
			game.getLogger().debug("Use hero skill: {}", heroSkill);
			this.heroSkill.resolveUse();
			return;
		}
		if (null != this.equipmentCard) {
			this.equipmentCard.resolveUse();
			return;
		}
		
		HandCard card = this.getFirstUsedCard();
		game.getLogger().debug("Use hand card: {}", card);
//		Check if equip equipment card
		if (card.isEquipment()) {
			Player equippedPlayer = (targetPlayerNames.isEmpty()) ? this : this.getTargetPlayer();
			equippedPlayer.equipment.addCardId(card.getCardId(), UpdateReason.UpdateReasonDefault);
			game.backToTurnOwner();
			return;
		}
		
		if (card.isTargetable() && getTargetPlayerNames().isEmpty()) {
			game.backToTurnOwner(); return;
		}
		
		if (!this.checkHeroSkill(TriggerReason.TriggerReasonCastMagic, card)) {
			this.handleUseHandCard(card);
		}
	}

	public void handleUseHandCard(HandCard card) {
		if (card.isStrengthenable() && getSkillPoint() > 0) {
			card.resolveStrengthen(null);
		} else {
			if (card.isSuperSkill()) character.updateHeroHpSp(0, -card.getRequiredSp());
			this.resolveUseHandCard(card);
		}
	}
	
	public void resolveUseHandCard(HandCard card) {
		if (card.isMagicCard() && this.checkTargetEquipment(TriggerReason.TriggerReasonBeCastMagic, card)) return;
		
		if(card.isDispellable()) {		// Ask for dispel
			game.askForDispel(card);
		} else {
			card.resolveUse();
		}
	}
	
	public boolean checkTargetEquipment(TriggerReason reason, Callback callback) {
		for (Player tarPlayer : this.getTargetPlayers()) {
			if (tarPlayer.checkEquipment(reason, callback))
				return true;
		}
		return false;
	}
	
	/**************************************************************************
	 * Choosing card logic: Target/Other player chose card to use/response
	 * Hero skill and equipment: Ditto
	 */
	public void choseCardToUse(Deque<Integer> cardIds) {
		if (null == cardIds || cardIds.size() == 0) this.choseCancel();
		
		this.addUsedCards(cardIds);
		if (Security.checkCheating(game, this)) return;
		
		game.getPlugin().sendChoseCardToUsePublicMessage(this, cardIds);
		hand.removeCardIds(cardIds, UpdateReason.UpdateReasonTable);
		game.getTableCardIds().addAll(cardIds);
		
		if (game.isWaitingDispel()) {
			this.resolveUseDispel(this.getLastUsedCard());
			return;
		}
		
		if (game.somebodyIsDying()) {
			usedSalveCount = cardIds.size();
			this.getLastUsedCard().resolveUse();
			return;
		}
		
		if (null != this.heroSkill) {
			game.getLogger().debug("Triggered skill: {}", heroSkill);
			this.updateUsedEvasionCount(cardIds.size() / heroSkill.getMinHandCardCount());	// e.g. BallLightning
			this.heroSkill.resolveUse();
		}
		else if (null != this.equipmentCard) {
			this.updateUsedEvasionCount(cardIds.size() / equipmentCard.getMinHandCardCount());
			this.equipmentCard.resolveUse();
		}
		else {
			this.updateUsedEvasionCount(cardIds.size());
			this.getLastUsedCard().resolveUse();
		}
	}
	
	private void resolveUseDispel(HandCard card) {
		if (null != this.heroSkill && heroSkill.getTransformedCard().isDispel()) {
			game.getLogger().debug("Triggered skill: {}", heroSkill);
			this.heroSkill.resolveUse();
		} else if (null != this.equipmentCard && equipmentCard.getTransformedCard().isDispel()) {
			this.equipmentCard.resolveUse();
		} else {
			card.resolveUse();
		}
	}
	
//	由1~n张卡牌转化后的新卡牌
	public HandCard getTransoformedCard(int cardId, int count) {
		Deque<HandCard> usedCards = this.getLastUsedCardsByCount(count);
		HandCard transformedCard = HandCard.newCardWithCardId(cardId, this);
		int color = usedCards.peekFirst().getCardColor();
		for (HandCard card : usedCards) {
			if (card.getCardColor() != color)	// 多张颜色不同视为无色
				transformedCard.setCardSuits(CardSuits.CardSuitsNull);
		}
		if (1 == count) transformedCard.setCardFigure(usedCards.peekFirst().getCardFigure());
		return transformedCard;
	}
	
//	Push the guess or judge card onto the tableCardIds, need call peekFirst while getting.
	public void guessCardColor() {
		this.judgedCard = new PlayingCard(game.fetchPlayingCardIds(1).poll());
		game.getTableCardIds().add(judgedCard.getCardId());
		game.getPlugin().sendFaceDownCardFromDeckPublicMessage(this, 1);
		game.getPlugin().sendChooseColorMessage(this);
	}
	
	public void getLastCardFromTable() {
		this.getCardsFromTable(1, false);
	}
	
//	e.g. If attack is transformed by hero skill or equipment, maybe can get 2 cards.
	public void getCardsFromTable(int count, boolean fromFirst) {
		Deque<Integer> cardIds = new ArrayDeque<Integer>();		
		for (int i = 0; i < count; i++) {
			if (fromFirst) {
				cardIds.add(game.getTableCardIds().pollFirst());
			} else {
				cardIds.add(game.getTableCardIds().pollLast());
			}
		}
		game.getPlugin().sendGetCardFromTablePublicMessage(this, cardIds);
		hand.addCardIds(cardIds, UpdateReason.UpdateReasonTable);
	}
	
	public void getPreviousCardsFromTable(int count) {
		game.getTableCardIds().addFirst(game.getTableCardIds().pollLast());
		this.getCardsFromTable(count, false);
	}
	
	public void revealOneCardFromDeck(Callback callback) {
		this.judgedCard = new PlayingCard(game.fetchPlayingCardIds(1).poll());
		game.getTableCardIds().add(judgedCard.getCardId());
		game.getPlugin().sendRevealOneCardFromDeckPublicMessage(this);
		
		if (!game.checkEverybodyHeroSkill(TriggerReason.TriggerReasonJudging, callback) &&
			!game.checkEverybodyHeroSkill(TriggerReason.TriggerReasonJudged, callback)) {
			callback.resolveJudge();
		}
	}
	
	/**************************************************************************
	 * Select hero skill or equipment to use
	 */
	public void useHeroSkill(int skillId) {
		this.equipmentCard = null;
		this.heroSkill = character.getHeroSkillBySkillId(skillId);
		if (null != heroSkill && heroSkill.isSimple()) {
			this.getGamePlugin().cancelScheduledExecution(this);
		}
		
		this.requiredTargetCount = heroSkill.getTargetCount();
		this.maxTargetCount = heroSkill.getMaxTargetCount();
		game.getPlugin().sendUseHeroSkillPublicMessage(this, skillId);
		heroSkill.makeHandCardAvailable();
		if (this.isNPC) {
			heroSkill.resolveSelectByNPC();
		} else {
			heroSkill.resolveSelect();
		}
	}
	
	public void useEquipement(int equipmentId) {
//		this.heroSkill = null;	// e.g. Frostmourne and BladesOfAttack can used/triggered at the same time.
		this.equipmentCard = equipment.getEquipmentByCardId(equipmentId);
		
		this.requiredTargetCount = equipmentCard.getTargetCount();
		this.maxTargetCount = this.requiredTargetCount;
		game.getPlugin().sendUseEquipmentPublicMessage(this, equipmentId);
		equipmentCard.makeHandCardAvailable();
		if (this.isNPC) {
			equipmentCard.resolveSelectByNPC();
		} else {
			equipmentCard.resolveSelect();
		}
	}
	
	public void cancelUseHeroSkill() {
		this.heroSkill = null;
		this.sendCancelMessage();
	}
	
	public void cancelUseEquipment() {
		this.equipmentCard = null;
		this.sendCancelMessage();
	}
	
	private void sendCancelMessage() {
		if (this.isTurnOwner()) {
			this.resetValueAfterResolved();
			this.makeActiveHandCardAvailable();
			game.getPlugin().sendPlayCardMessage(this, this.isNPC);
		}
		else {
//			Maybe need 2 selectable card count before, so don't reset it to default. (e.g. EyeOfSkadi)
//			this.resetValueAfterResolved();
			if (game.isWaitingDispel() || game.somebodyIsDying()) {
				this.makePassiveHandCardAvailable();
			} else {
				this.makeEvasionCardAvailable();
			}
			this.requiredSelCardCount = 0;
			game.getPlugin().sendChooseCardToUseMessage(this, this.isNPC);
		}
	}
	
	/**************************************************************************
	 * Okay logic: Player chose okay (e.g. Use hero skill/equipment)
	 * Okay to use hero skill or equipment
	 */
	public void choseOkay() {
		game.getPlugin().sendOkayPublicMessage(this);
		
		if (null != this.heroSkill) {
			game.getLogger().debug("Triggered skill: {}", heroSkill);
			this.heroSkill.resolveOkay();
		} else if (null != this.equipmentCard) {
			this.equipmentCard.resolveOkay();
		} else {
			this.getFirstUsedCard().resolveOkay();
		}
	}
	
	/**************************************************************************
	 * Cancel logic: Player chose cancel
	 * 1. Check if the dispel is cancelled
	 * 2. Cancel hero skill or equipment
	 * 3. Check turn owner hero skill before cancel
	 */
	public void choseCancel() {
		game.getPlugin().sendCancelPublicMessage(this);
		
//		Chose draw 2 cards after killed one player
//		Maybe triggered QuellingBlade before, chose cancel it. So need check if player is damage source.
		if (game.isDeathResolving() && this.isDamageSource()) {
			this.drawCard(Constant.KILL_REWARD_DRAW_CARD_COUNT);
			game.continueResolveAfterPlayerDead();
			return;
		}
		
//		Cancel dispel(Don't use it)
		if (game.isWaitingDispel()) {
			this.cancelDispel(); return;
		}
		
		if (game.somebodyIsDying()) {
			if (null != this.heroSkill && this.heroSkill.isDyingTriggered()) {
				this.heroSkill.resolveCancel();
			} else {
				this.makeChoice();
			}
			return;
		}
		
		if (null != this.heroSkill) {
			game.getLogger().debug("Triggered skill: {}", heroSkill);
			this.heroSkill.resolveCancel(); return;
		}
		if (null != this.equipmentCard) {
			this.equipmentCard.resolveCancel(); return;
		}
		
		if (this.isStrengthening) {
			this.getFirstUsedCard().resolveCancel();
		} else {
			this.resolveCancelByTarget();
		}
	}
	
	/**
	 * Chose view another player's role after killed one player
	 */
	public void choseViewPlayerRole(Deque<String> tarPlayerNames) {
		game.getPlugin().sendChoseViewPlayerRole(this, tarPlayerNames);
		
//		Don't overwrite original targets, maybe some targets not finish resolve.
//		this.targetPlayerNames = tarPlayerNames;
		Player tarPlayer = game.getPlayerByName(tarPlayerNames.peekLast());	// Need get last one
		game.getPlugin().sendShowRoleCardMessage(this, tarPlayer.getRoleCardId());
		game.continueResolveAfterPlayerDead();
	}
	
	public void resolveCancelByTarget() {
//		Check damage source player's hero skill
		Player damageSource = game.getDamageSource();
		if (null != damageSource.heroSkill) {
			game.getLogger().debug("Used skill: {}", damageSource.heroSkill);
			damageSource.heroSkill.resolveCancelByTarget(this);
			return;
		} else if (null != damageSource.equipmentCard) {
			damageSource.equipmentCard.resolveCancelByTarget(this);
			return;
		}
		
//		发动了"战争饥渴"，目标选择了取消，没有打出任何卡牌，这时应该结算技能，不是结算卡牌
		Player turnOwner = game.getTurnOwner();
		if (null != turnOwner.heroSkill) {
			turnOwner.heroSkill.resolveCancelByTarget(this);
		} else {
			damageSource.getFirstUsedCard().resolveCancelByTarget(this);
		}
	}
	
	public void cancelDispel() {
		this.isCanceledDispel = true;
		if (game.isEverybodyCanceledDispel()) {
			game.resetCancelDispel();	// Maybe trigger next dispel waiting(Resolve not finished yet)
			game.setIsWaitingDispel(false);
			game.continueResolveAfterDispel();
		}
	}
	
	public void updateHeroHpSp(int hpChanged, int spChanged, Callback callback) {
		if (hpChanged < -1 && checkHeroSkill(TriggerReason.TriggerReasonBeDamage2, callback)) {
			this.damagedValue = Math.abs(hpChanged);
			return;
		}
		
		if (hpChanged < 0 && checkHeroSkill(TriggerReason.TriggerReasonBeDamage, callback)) {
			return;
		}
		
		spChanged = Math.min(spChanged, getSpLimit()-getSkillPoint());	// Can't exceed limit
		this.character.updateHeroHpSp(hpChanged, spChanged);
		if (hpChanged < 0) {
			this.callback = callback;
			game.setDamagedPlayer(this);
			if (spChanged > 0) this.beDamagedSp = spChanged;
		} else if (spChanged > 0) {
			this.dealDamagedSp = spChanged;
		}
		
		if (this.isDying()) {
			this.resolvePlayerDying(callback); return;
		}
		
		if (hpChanged > 0) {
			if (checkHeroSkill(TriggerReason.TriggerReasonHpRestored, callback)) return;
		}
		if (spChanged > 0) {
			if (checkHeroSkill(TriggerReason.TriggerReasonGotSp, callback)) return;
		}
		if (hpChanged < 0) {
			this.character.resetDamagedTriggerFlag();
			this.equipment.resetDamagedTriggerFlag();
			this.continueResolveDamage(callback); return;
		}
		
		this.finishResolve(callback);
	}
	
	public void continueResolveDamage(Callback callback) {
		this.isContinueResolveDamage = true;
		
//		e.g.Purification
		if (game.checkEverybodyHeroSkill(TriggerReason.TriggerReasonAnyBeDamaged, callback)) {
			return;
		}
		
//		Attack card
		if (callback.isCard() && ((HandCard)callback).isAttack() &&
			(game.getDamageSource().checkHeroSkillAndEquipment(TriggerReason.TriggerReasonAttackDamaged, callback) ||
			this.checkHeroSkill(TriggerReason.TriggerReasonBeAttackDamaged, callback))) {
			return;
		}
		
//		e.g.WaveOfTerror
		if (dealDamagedSp > 0 && checkHeroSkill(TriggerReason.TriggerReasonGotSp, callback)) return;
		
//		e.g.SpikedCarapace, Warpath and BladeMail
		if (this.checkHeroSkillAndEquipment(TriggerReason.TriggerReasonBeDamaged, callback)) {
			return;
		}
		
		this.isContinueResolveDamage = false;
		this.finishResolve(callback);
	}
	
	public void resolvePlayerDying(Callback callback) {
		game.setDyingPlayer(this);
		
		if (!this.checkHeroSkillAndEquipment(TriggerReason.TriggerReasonPlayerIsDying, callback)) {
			game.askForNextHelper(callback);
		}
	}
	
	public void choseColor(int color) {
		this.selectedColor = color;
		game.getPlugin().sendChoseColorPublicMessage(this, color);
		
		if (null != this.heroSkill) {
			this.heroSkill.resolveResult();
		} else {
			this.getFirstUsedCard().resolveResult();
		}
	}
	
	public void choseSuits(int suits) {
		this.selectedSuits = suits;
		game.getPlugin().sendChoseSuitsPublicMessage(this, suits);
		
		this.getFirstUsedCard().resolveResult();
	}
	
	public void choseCardToGet(Deque<Integer> cardIdxes, Deque<Integer> cardIds) {
		game.getPlugin().sendChoseCardToGetPublicMessage(this, cardIdxes.size(), cardIds);
		
		Player turnOwner = game.getTurnOwner();
		Player tarPlayer = turnOwner.getTargetPlayer();
		Player player = (this.isTurnOwner()) ? tarPlayer : turnOwner;
		if (null != this.heroSkill) {
			game.getLogger().debug("Triggered skill: {}", heroSkill);
			this.heroSkill.resolveResult(player, cardIdxes, cardIds);
		} else {
			turnOwner.getFirstUsedCard().resolveResult(player, cardIdxes, cardIds);
		}
	}
	
//	Greed or draw card from target player
	public Deque<Integer> getCardIdsByCardIndexes(Deque<Integer> cardIdxes) {
		Deque<Integer> cardIds = new ArrayDeque<Integer>(cardIdxes.size());
		List<Integer> handCardIds = new ArrayList<Integer>(this.getHandCardIds());
		Collections.shuffle(handCardIds);
		for (Integer index : cardIdxes) {
			cardIds.add(handCardIds.get(index));
		}
		return cardIds;
	}
	
	public void choseCardToGive(Deque<Integer> cardIds) {
		game.getPlugin().sendChoseCardToGivePublicMessage(this, cardIds.size());
		
		this.getFirstUsedCard().resolveContinue(cardIds);
	}
	
	public void choseCardToRemove(Deque<Integer> cardIdxes, Deque<Integer> cardIds) {
		Player tarPlayer = this.getTargetPlayer();
		if (cardIds.isEmpty()) {
			cardIds = tarPlayer.getCardIdsByCardIndexes(cardIdxes);
			tarPlayer.hand.removeCardIds(cardIds, UpdateReason.UpdateReasonTable);
		} else {
			tarPlayer.equipment.removeCardIds(cardIds, UpdateReason.UpdateReasonTable);
		}
		game.getPlugin().sendChoseCardToRemovePublicMessage(this, cardIds);
		game.getTableCardIds().addAll(cardIds);
		
		if (null != this.heroSkill) {
			game.getLogger().debug("Triggered skill: {}", heroSkill);
			this.heroSkill.resolveResult(tarPlayer, cardIdxes, cardIds);
		} else {
			this.getFirstUsedCard().resolveContinue();
		}
	}
	
	public void showCardAndChooseToAssign() {
		this.assignedCardIds = game.fetchPlayingCardIds(game.getPlayerCount());
		game.getPlugin().sendShowAssignedCardMessage(this);
		this.chooseCardToAssign();
	}
	
	public void chooseCardToAssign() {
		int waitingTime = game.getPlayTime();
		game.setPlayTime(Constant.DEFAULT_CARD_ASSIGNING_TIME);
		game.getPlugin().sendChooseCardToAssignMessage(this);
		game.setPlayTime(waitingTime);
	}
	
	public void assignedCard(Deque<Integer> cardIds) {
		game.getPlugin().sendAssignedCardublicMessage(this);
		if (null != this.heroSkill) {
			game.getLogger().debug("Triggered skill: {}", heroSkill);
			this.heroSkill.resolveResult(cardIds);
		} else {
			this.getFirstUsedCard().resolveResult(cardIds);
		}
	}
	
	public void assignCardToEachPlayer(Deque<Integer> cardIds) {
		Deque<Player> players = new ArrayDeque<Player>(game.getSortedAlivePlayers());
		for (Integer cardId : cardIds) {
			Player player = players.pollFirst();
			player.getHand().addCardId(cardId, UpdateReason.UpdateReasonDefault);
		}
	}
	
	public void assignCardToPlayers(Deque<Integer> cardIds, Deque<Player> tarPlayers) {		
		for (Integer cardId : cardIds) {
			Player player = tarPlayers.pollFirst();
			player.getHand().addCardId(cardId, UpdateReason.UpdateReasonDefault);
		}
	}
	
	public void choseCardToDrop(Deque<Integer> cardIds) {
		game.getPlugin().sendChoseCardToDropPublicMessage(this, cardIds);
		
		Deque<Integer> handCardIds = new ArrayDeque<Integer>();
		Deque<Integer> equipCardIds = new ArrayDeque<Integer>();
		for (Integer cardId : cardIds) {
			if (hand.getCardIds().contains(cardId)) {
				handCardIds.add(cardId);
			} else {
				equipCardIds.add(cardId);
			}
		}
		hand.removeCardIds(handCardIds, UpdateReason.UpdateReasonTable);
		equipment.removeCardIds(equipCardIds, UpdateReason.UpdateReasonTable);
		
		game.getTableCardIds().addAll(cardIds);
		
		if (null != this.heroSkill) {
			game.getLogger().debug("Triggered skill: {}", heroSkill);
			this.heroSkill.resolveUse(cardIds);
		} else if (null != this.equipmentCard) {
			this.equipmentCard.resolveUse(cardIds);
		} else {
			if (null != game.getDamageSource().heroSkill) {
				this.finishResolve(game.getDamageSource().heroSkill);
			} else {
				game.getTurnOwner().getFirstUsedCard().resolveContinue();
			}
		}
	}
	
	public void choseTargetPlayer(Deque<String> tarPlayerNames) {
		game.getPlugin().sendChoseTargetPlayerPublicMessage(this, tarPlayerNames);
		
		if (null != this.heroSkill) {
			game.getLogger().debug("Triggered skill: {}", heroSkill);
			this.targetPlayerNames = tarPlayerNames;
			this.heroSkill.resolveUse();
		}
	}
	
	/**************************************************************************
	 * Player chose to exit the game
	 */
	public void exitGame() {
		if (game.isOver()) {
			game.getPlugin().sendExitGameMessage(this);
		} else {
			game.getPlugin().kickUserFromRoom(this);
		}
		
		if (!this.isDead) {
			this.isHosting = true;
			this.setIsEscaped(true);
		}
	}
	
	/**************************************************************************
	 * Turn owner gets available card id list while his turn starting
	 * Active:
	 * 1. Get available id list according to used hero skill
	 * 2. Get available id list according to used equipment
	 * 3.1. The card must be used actively(主动使用，如闪避/驱散只能被动使用)
	 * 3.2. If it is Attack, check whether the attack time reach to limit.
	 * 3.3. If it is HealingSalve, check whether the HP value is full.
	 * 3.4. If it is super skill card, check whether the SP value is enough.
	 * Passive:
	 * 1. Get available dispel card if is waiting dispel
	 * 2. Get available id list according to triggered hero skill/equipment
	 * 3. Get available id list according to turn owner used hero skill/card
	 * 4. Target/Other players get available card id list while they are being attacked/magic
	 * e.g. If one player is attacked, only evasion can be selected to use.
	 */
	public Deque<Integer> getAvailableCardIdList() {
		Deque<Integer> cardIds = new ArrayDeque<Integer>();
		for (HandCard card : this.getHandCards()) {
			if (card.isAvailable())
				cardIds.add(card.getCardId());
		}
		return cardIds;
	}
	
	public void makeAllHandCardsAvailable() {
		for (HandCard card : this.getHandCards()) {
			card.setIsAvailable(true);
		}
	}
	
	public void makeAllHandCardsUnavailable() {
		for (HandCard card : this.getHandCards()) {
			card.setIsAvailable(false);
		}
	}
	
	public void makeActiveHandCardAvailable() {
		this.makeAllHandCardsUnavailable();
		
		for (HandCard card : this.getHandCards()) {
			card.setIsAvailable(card.isActiveUsable());
		}
	}
	
	public void makePassiveHandCardAvailable() {
		this.makeAllHandCardsUnavailable();
		
		for (HandCard card : this.getHandCards()) {
			card.setIsAvailable(card.isPassiveUsable());
		}
	}
	
	public void makeHandCardAavailableByColor(int color) {
		this.makeAllHandCardsUnavailable();
		
		for (HandCard card : this.getHandCards()) {
			card.setIsAvailable(card.getCardColor() == color);
		}
	}
	
	public void makeHandCardAavailableBySuits(int suits) {
		this.makeAllHandCardsUnavailable();
		
		for (HandCard card : this.getHandCards()) {
			card.setIsAvailable(card.getCardSuits() == suits);
		}
	}
	
	public void makeAttackCardAvailable() {
		this.makeAllHandCardsUnavailable();
		
		for (HandCard card : this.getHandCards()) {
			card.setIsAvailable(card.isAttack());
		}
	}
	
	public void makeEvasionCardAvailable() {
		this.makeAllHandCardsUnavailable();
		
		for (HandCard card : this.getHandCards()) {
			card.setIsAvailable(card.isEvasion());
		}
	}
	
	/**************************************************************************
	 * Get available hero skill id list (Active or Passive)
	 */
	public Deque<Integer> getAvailableSkillIdList(boolean isActive) {
		Deque<Integer> skillIds = new ArrayDeque<Integer>();
		for (BaseSkill skill : character.getHeroSkills()) {
			boolean isAvailable = (isActive) ? skill.isActiveLaunchable() : skill.isPassiveLaunchable();
			if (isAvailable && !this.isDying()) skillIds.add(skill.getSkillId());
		}
		return skillIds;
	}
	
//	Trigger the hero skill which can be transformed to 'Attack' card
//	(e.g. 发动"战争饥渴"，目标需要使用1张攻击)
	public void triggerAttackHeroSkillBySkill(Callback callback) {
		for (BaseSkill skill : character.getHeroSkills()) {
			PlayingCard card = new PlayingCard(skill.getTransformedCardId());
			if (null != card && card.isAttack() && !character.isJuggernaut()) {
				skill.setCallback(callback);
				skill.setIsPassiveLaunchable(true);
			}
		}
	}
	
	/**
	 * Get available equipment id list (Active or Passive)
	 */
	public Deque<Integer> getAvailableEquipIdList(boolean isActive) {
		Deque<Integer> cardIds = new ArrayDeque<Integer>();
		for (EquipmentCard card : equipment.getCards()) {
			boolean isAvailable = (isActive) ? card.isActiveLaunchable() : card.isPassiveLaunchable();
			if (isAvailable && !this.isDying()) cardIds.add(card.getCardId());
		}
		return cardIds;
	}
	
	public Deque<Integer> getDroppableEquipIdList() {
		Deque<Integer> cardIds = new ArrayDeque<Integer>();
		for (EquipmentCard card : equipment.getCards()) {
			if (card.isDroppable()) {
				cardIds.add(card.getCardId());
			}
		}
		return cardIds;
	}
	
//	Ditto
	public void triggerAttackEquipmentBySkill(Callback callback) {
		for (EquipmentCard equip : equipment.getCards()) {
			PlayingCard card = new PlayingCard(equip.getTransformedCardId());
			if (null != card && card.isAttack()) {
				equip.setCallback(callback);
				equip.setIsPassiveLaunchable(true);
			}
		}
	}
	
	/**************************************************************************
	 * Check if hero skill or equipment can be triggered
	 */
	public boolean checkHeroSkillAndEquipment(TriggerReason reason) {
		return this.checkHeroSkillAndEquipment(reason, null);
	}
	public boolean checkHeroSkillAndEquipment(TriggerReason reason, Callback callback) {
		return (this.checkHeroSkill(reason, callback) || this.checkEquipment(reason, callback));
	}
	
	public boolean checkHeroSkill(TriggerReason reason) {
		return this.checkHeroSkill(reason, null);
	}
	public boolean checkHeroSkill(TriggerReason reason, Callback callback) {
		return character.checkTrigger(reason, callback);
	}
	
	public boolean checkEquipment(TriggerReason reason) {
		return this.checkEquipment(reason, null);
	}
	public boolean checkEquipment(TriggerReason reason, Callback callback) {
		return equipment.checkTrigger(reason, callback);
	}
	
}
