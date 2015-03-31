/**
 * 
 */
package com.woodeck.fate.game;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.plist.NSDictionary;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

import org.slf4j.Logger;

import com.electrotank.electroserver5.extensions.api.ScheduledCallback;
import com.electrotank.electroserver5.extensions.api.value.ReadOnlyUserVariable;
import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.card.HeroCard.HeroCardId;
import com.woodeck.fate.card.RoleCard;
import com.woodeck.fate.card.RoleCard.RoleCardEnum;
import com.woodeck.fate.fatecard.FateCard;
import com.woodeck.fate.fatecard.FateCard.FateCardId;
import com.woodeck.fate.game.CardDeck.CardDeckListener;
import com.woodeck.fate.model.User;
import com.woodeck.fate.player.Callback;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.player.Player.PlayerListener;
import com.woodeck.fate.player.Player.TriggerReason;
import com.woodeck.fate.server.GamePlugin;
import com.woodeck.fate.util.BeanHelper;
import com.woodeck.fate.util.Constant;
import com.woodeck.fate.util.FileConstant;
import com.woodeck.fate.util.PluginConstant.Action;
import com.woodeck.fate.util.VarConstant;

/**
 * @author Killua
 *
 */
public class Game implements CardDeckListener, PlayerListener, ScheduledCallback {
	
	public static final String
		kVictory	= "victory",
		kFailure	= "failure",
		kEscape		= "escape",
		kKillEnemy	= "killEnemy",
		kDoubleKill	= "doubleKill",
		kTripleKill	= "tripleKill";
	
	private static NSDictionary reward;
	static {
		try {
			reward = (NSDictionary)PropertyListParser.parse(new File(FileConstant.PLIST_REWARD));
			
		} catch (PropertyListException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public enum GameState {
		GameStateNull (0),
		GameStateHeroChoosing (1),		// 游戏开始阶段-选择英雄
        GameStateComparing (2),			// 拼点阶段
        GameStateTurnStarting (3),     	// 回合开始阶段
        GameStatePlaying (4),          	// 主动出牌阶段
        GameStateCardChoosing (5),     	// 被动出牌阶段
        GameStateBoolChoosing (6),		// 选择发动技能(英雄/装备/强化)
        GameStateTargetChoosing (7),	// 指定目标阶段
        GameStateColorChoosing (8),    	// 选择颜色阶段
        GameStateSuitsChoosing (9),    	// 选择花色阶段
        GameStateGetting (10),          // 抽牌阶段
        GameStateGiving (11),           // 给牌阶段
        GameStateRemoving (12),        	// 拆牌阶段
        GameStateAssigning (13),       	// 分牌阶段
        GameStateDropping (14),			// 弃/弃置卡牌
        GameStateDiscarding (15),      	// 弃牌阶段
		GameStatePlayerDying (16),		// 濒死阶段
		GameStateWaitingDispel (17),	// 等待驱散阶段
		GameStateDeathResolving (18);	// 死亡结算阶段
		
	    public final int id;	    
	    private GameState(int id) {
	    	this.id = id;
	    }
	}
	
	
	private int playTime;
	private boolean isNoChatting;
	
	private GamePlugin plugin;
	private int executionId = -1;
	
	private boolean isRoleMode;
	private List<String> userNames;
	private CardDeck cardDeck;
	private FateCard fateCard;
	private Deque<Integer> roleCardIds;
	
	private AbstractMap<String, Player> allPlayers;
	private Player turnOwner;
	private Player damageSource;	// The damage source player(Turn owner by default)
	private Player damagedPlayer;
	private Player dyingPlayer;
	
	private Deque<Player> damagedPlayers = new ArrayDeque<Player>();	// Stack: Last In First Out(LIFO)
	private Deque<Player> dispelPlayers = new ArrayDeque<Player>();		// The players who use dispel
	private Deque<Player> sirenPlayers = new ArrayDeque<Player>();		// Queue: The players who use "SirenSong" card
	
	private Action action;
	private GameState state = GameState.GameStateNull;
	
	private List<Integer> allHeroIds = new ArrayList<Integer>();		// All players selected hero id
	
	private Deque<Integer> tableCardIds = new ArrayDeque<Integer>();
	private PlayingCard maxFigureCard;		// 最大点数的卡牌
	private int startRoundCount;			// 准备好(可以开始牌局)的玩家数
	private boolean somebodyHasDispel;		// 玩家是否有驱散
	private boolean isSentWaitingDispel;	// 是否已经发送了等待驱散的消息
	private boolean isWaitingDispel;		// 是否在等待驱散
	private boolean isNewWaiting;			// 是否开始新一轮的等待
	private boolean isDeathResolving;		// 杀死一个角色后的结算阶段
	private boolean isOver;
	private boolean isFirstBlood = true;
	
	private Callback dispelCallback;		// 被驱散的魔法牌或技能(不包括驱散本身)
	
	private List<GameResult> victoryResults = new ArrayList<GameResult>();
	private List<GameResult> failureResults = new ArrayList<GameResult>();
	
	
	public Game(GamePlugin plugin, List<String> userNames, int playerCount, boolean isRoleMode) {
		this.plugin = plugin;
		this.userNames = userNames;
		this.isRoleMode = isRoleMode;
		
		allPlayers = new ConcurrentHashMap<String, Player>(playerCount);
		for (String userName : userNames) {
			Player player = new Player(this, userName, plugin.isNPCForUser(userName));
			allPlayers.put(userName, player);
		}
		
		this.getLogger().debug("Is role mode: {}", isRoleMode);
		this.getLogger().debug("Player count: {}", playerCount);
		
		// AI player
		for (int i = 0; i < playerCount-getUserCount(); i++) {
			Player player = new Player(this, Constant.AI_PLAYER_NAME_PREFIX + i, true);
			allPlayers.put(player.getPlayerName(), player);
		}
		
//		Register card deck listener. Maybe player picked hero, need remove it from hero card deck.
		this.cardDeck = new CardDeck(this);
		this.cardDeck.setListener(this);
		
		if (isRoleMode) {
			this.fateCard = FateCard.newCardWithCardId(cardDeck.fetchOneFateCardId());
			this.roleCardIds = cardDeck.fetchRoleCardIds(getPlayerCount());
		} else {
			this.fateCard = new FateCard(FateCardId.FateCardVersus);
			if (getPlayerCount() == 2) {
				this.roleCardIds = cardDeck.fetchRoleCardIds(getPlayerCount());
			} else {
				this.roleCardIds = new ArrayDeque<Integer>(Arrays.asList(1, 2, 5, 4));
			}
		}
	}

	public void resetValue() {
		damagedPlayer = null;
		dyingPlayer = null;
		somebodyHasDispel = false;
		isSentWaitingDispel = false;
		isWaitingDispel = false;
		isNewWaiting = false;
		isDeathResolving = false;
		damagedPlayers.clear();
		dispelPlayers.clear();
		sirenPlayers.clear();
		dispelCallback = null;
		
		for (Player player : this.getAlivePlayers()) {
			player.resetValueAfterResolved();
		}
		
		this.updateDiscardPile();
	}
	
	public void resetAllHeroSkills() {
		for (Player player : this.getAlivePlayers()) {
			player.getCharacter().resetTriggerFlag();
		}
	}
	
//	Reset the hero skill which can be trigger multiple times by any distinct target
	public void resetConditionalHeroSkills() {
		for (Player player : this.getAlivePlayers()) {
			player.getCharacter().resetConditionalTriggerFlag();
		}
	}
	
	public void resetAllEquipments() {
		for (Player player : this.getAlivePlayers()) {
			player.getEquipment().resetTriggerFlag();
		}
	}
	
	/**************************************************************************
	 * Getter and Setter method
	 */
	public int getPlayTime() {
		return playTime;
	}
	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}
	public boolean isNoChatting() {
		return isNoChatting;
	}
	public void setIsNoChatting(boolean isNoChatting) {
		this.isNoChatting = isNoChatting;
	}
	public boolean isRoleMode() {
		return isRoleMode;
	}
	
	public GamePlugin getPlugin() {
		return plugin;
	}
	
	public Logger getLogger() {
		return plugin.getApi().getLogger();
	}
	
	public int getFateCardId() {
		return fateCard.getCardId();
	}
	
	public Deque<Integer> getRoleCardIds() {
		return roleCardIds;
	}
	
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	
	public GameState getState() {
		if (this.isWaitingDispel) return GameState.GameStateWaitingDispel;
		if (this.somebodyIsDying()) return GameState.GameStatePlayerDying;
		if (this.isDeathResolving) return GameState.GameStateDeathResolving;
		
		switch (action) {
			case ActionTableDealCandidateHero:
			case ActionPlayerSelectedHero:
				this.state = GameState.GameStateHeroChoosing;
				break;
				
			case ActionChooseCardToCompare:
				this.state = GameState.GameStateComparing;
				break;
				
			case ActionDiscardCard:
				this.state = GameState.GameStateDiscarding;
				break;
				
			case ActionStartTurn:
				this.state = GameState.GameStateTurnStarting;
				break;
				
			case ActionPlayCard:
				this.state = GameState.GameStatePlaying;
				break;
				
			case ActionChooseCardToUse:
				this.state = GameState.GameStateCardChoosing;
				break;
				
			case ActionChooseCardToDrop:
//				Sometimes turn owner start turn and use hero skill, also will receive this action.
//				For this case(isNewWaiting is false), state still "GameStatePlaying", not "GameStateDropping".
				if (isNewWaiting) this.state = GameState.GameStateDropping;
				break;
				
			case ActionChooseOkayOrCancel:
				this.state = GameState.GameStateBoolChoosing;
				break;
				
			case ActionChooseColor:
				this.state = GameState.GameStateColorChoosing;
				break;
				
			case ActionChooseSuits:
				this.state = GameState.GameStateSuitsChoosing;
				break;
				
			case ActionChooseCardToGet:
				this.state = GameState.GameStateGetting;
				break;
				
			case ActionChooseCardToGive:
				this.state = GameState.GameStateGiving;
				break;
				
			case ActionChooseCardToRemove:
				this.state = GameState.GameStateRemoving;
				break;
				
			case ActionChooseCardToAssign:
	        	this.state = GameState.GameStateAssigning;
	        	break;
	
			case ActionChooseTargetPlayer:
//				Ditto
				if (isNewWaiting) this.state = GameState.GameStateTargetChoosing;
				break;
				
			default:
				break;
		}
		
		return state;
	}
	public void setState(GameState state) {
		this.state = state;
	}
	
	public Player getTurnOwner() {
		return turnOwner;
	}
	
	public List<Player> getAllPlayers() {
		return new ArrayList<Player>(allPlayers.values());	// Can't cast to list
	}
	public List<Player> getAlivePlayers() {
		List<Player> alivePlayers = new ArrayList<Player>();
		for (Player player : this.getAllPlayers()) {
			if (!player.isDead()) alivePlayers.add(player);
		}
		return alivePlayers;
	}
	public List<String> getAlivePlayerNames() {
		List<String> playerNames = new ArrayList<String>(getPlayerCount());
		for (Player player : this.getAllPlayers()) {
			if (!player.isDead()) playerNames.add(player.getPlayerName());
		}
		return playerNames;
	}
//	Get sorted alive players base on turn owner(It is first index)
	public List<Player> getSortedAlivePlayers() {
		if (null == turnOwner) return this.getAlivePlayers();
		
		Deque<Player> sortedPlayers = new ArrayDeque<Player>(getAlivePlayers());
		for (Player player : this.getAlivePlayers()) {
			if (player.equals(turnOwner))
				break;
			sortedPlayers.add(sortedPlayers.pop());
		}
		return new ArrayList<Player>(sortedPlayers);
	}
	public List<String> getSortedAlivePlayerNames() {
		List<String> playerNames = new ArrayList<String>(getPlayerCount());
		for (Player player : this.getSortedAlivePlayers()) {
			playerNames.add(player.getPlayerName());
		}
		return playerNames;
	}
	public int getPlayerCount() {
		return allPlayers.size();
	}
	public int getAlivePlayerCount() {
		return this.getAlivePlayers().size();
	}
	
	public List<Player> getUserPlayers() {
		List<Player> userPlayers = new ArrayList<Player>(getUserCount());
		for (Player player : this.getAllPlayers()) {
			if (!player.isNPC()) userPlayers.add(player);
		}
		return userPlayers;
	}
	public int getUserCount() {
		return userNames.size();
	}
	
	public int getAliveRealPlayerCount() {
		int count = 0;
		for (Player player : this.getAlivePlayers()) {
			if (!player.isNPC() && !player.isDead())
				count++;
		}
		return count;
	}
	
	public List<Player> getOpponentPlayers() {
		List<Player> players = new ArrayList<Player>();
		for (Player player : this.getSortedAlivePlayers()) {
			if (!player.getRoleCard().isSameWithRole(turnOwner.getRoleCard())) {
				players.add(player);
			}
		}
		return players;
	}
	
	public List<Player> getPartnerPlayers() {
		List<Player> players = new ArrayList<Player>();
		for (Player player : this.getSortedAlivePlayers()) {
			if (player.getRoleCard().isSameWithRole(turnOwner.getRoleCard())) {
				players.add(player);
			}
		}
		return players;
	}
	
	public Player getDamageSource() {
		return damageSource;
	}
	public void setDamageSource(Player damageSource) {
		if (null != damageSource) this.damageSource = damageSource;
	}
	public String getDamageSourceName() {
		return (null != this.damageSource) ? damageSource.getPlayerName() : "";
	}
	
	public Player getDamagedPlayer() {
		return damagedPlayer;
	}
	public void setDamagedPlayer(Player damagePlayer) {
		if (null != this.damagedPlayer && null != damagePlayer && !this.damagedPlayer.equals(damagePlayer)) {
			this.resetConditionalHeroSkills();
		}
		this.damagedPlayer = damagePlayer;
	}
	public Deque<Player> getDamagedPlayers() {
		return damagedPlayers;
	}
	
	public Deque<Player> getDispelPlayers() {
		return dispelPlayers;
	}
	public Deque<Player> getSirenPlayers() {
		return sirenPlayers;
	}
	
	public Player getDyingPlayer() {
		return dyingPlayer;
	}
	public void setDyingPlayer(Player dyingPlayer) {
		plugin.sendPlayerIsDyingPublicMessage(dyingPlayer);
		
//		If dying player changed, reset hero skill and check again.(Multiple players are dying)
		if (null != this.dyingPlayer && null != dyingPlayer && !this.dyingPlayer.equals(dyingPlayer)) {
			this.resetConditionalHeroSkills();
		}
		this.dyingPlayer = dyingPlayer;
	}
	
	public int getDeckCardCount() {
		return cardDeck.getRemainingCardIds().size();
	}
	
	public List<Integer> getAllHeroIds() {
		return allHeroIds;
	}
	
	public int getMaxFigureCardId() {
		return maxFigureCard.getCardId();
	}
	
	public Deque<Integer> getTableCardIds() {
		return tableCardIds;
	}
	public int getFirstTableCardId() {
		return (tableCardIds.isEmpty()) ? 0 : tableCardIds.peekFirst();
	}
	public int getLastTableCardId() {
		return (tableCardIds.isEmpty()) ? 0 : tableCardIds.peekLast();
	}
	public Deque<PlayingCard> getTableCards() {
		Deque<PlayingCard> cards = new ArrayDeque<PlayingCard>();
		for (Integer cardId : tableCardIds) {
			cards.add(new PlayingCard(cardId));
		}
		return cards;
	}
	public PlayingCard getFirstTableCard() {
		return new PlayingCard(tableCardIds.peekFirst());
	}
	public PlayingCard getLastTableCard() {
		return new PlayingCard(tableCardIds.peekLast());
	}
	public int getTableCardCount() {
		return tableCardIds.size();
	}
	
	public boolean isWaitingDispel() {
		return isWaitingDispel;
	}
	public void setIsWaitingDispel(boolean isWaitingDispel) {
		this.isWaitingDispel = isWaitingDispel;
		if (!isWaitingDispel) plugin.sendCancelWaitingMessage();
	}
	
	public boolean isDispelled() {
		return (dispelPlayers.size() > 0 && dispelPlayers.size()%2 != 0);
	}
	
	public boolean isNewWaiting() {
		return isNewWaiting;
	}
	public void setIsNewWaiting(boolean isNewWaiting) {
		this.isNewWaiting = isNewWaiting;
	}
	
	public boolean isDeathResolving() {
		return isDeathResolving;
	}
	
	public boolean isOver() {
		return isOver;
	}
	
	public boolean isFirstBlood() {
		return (this.isFirstBlood && this.isRoleMode);
	}
	
	public List<GameResult> getVictoryResults() {
		return victoryResults;
	}
	public List<GameResult> getFailureResults() {
		return failureResults;
	}
	
	/**
	 * Get game history: Every player used skills or cards(牌局记录)
	 */
	public AbstractMap<String, List<History>> getAllGameHistory() {
		AbstractMap<String, List<History>> history = new ConcurrentHashMap<String, List<History>>(getPlayerCount());
		for (Player player : this.getAllPlayers()) {
			history.put(player.getSelectedHeroName(), player.getGameHistory());
		}
		return history;
	}
	
	/**************************************************************************
	 * Get player instance by player/user name
	 */
	public Player getPlayerByName(String playerName) {
		return this.allPlayers.get(playerName);
	}
	
	public void startGame() {
		plugin.sendStartGameMessage();
		
		if (this.isRoleMode || getPlayerCount() == 2) {
			for (Player player : this.getAllPlayers()) {
				player.setRoleCard(new RoleCard(roleCardIds.pollFirst()));
			}
		}
		this.dealCandidateHero();
	}
	
	/**
	 * Deal candidate heros to each player
	 * Register player listener
	 */
	public void dealCandidateHero() {
		for (Player player : this.getAllPlayers()) {
			player.setListener(this);	// Register listener
			
			if (player.isNPC()) {
				this.dealCandidateHeroToPlayer(player);
				continue;
			}
			
			ReadOnlyUserVariable userVar = plugin.getApi().getUserVariable(player.getPlayerName(), VarConstant.kVarPickedHero);
			int pickedHeroId = (null != userVar) ? userVar.getValue().getInteger(VarConstant.kParamPickedHeroId, 0) : 0;
			if (pickedHeroId != HeroCardId.HeroCardNull) {	// Picked hero
				player.selectHero(pickedHeroId, true);
			} else {
				this.dealCandidateHeroToPlayer(player);
			}
		}
	}
	
//	Call this method when user change the random candidate heros
	public void dealCandidateHeroToPlayer(Player player) {
		int waitingTime = this.playTime;
		this.playTime = Constant.DEFAULT_HERO_SELECTION_TIME;
		
		User user = BeanHelper.getUserBean().getUserByName(player.getPlayerName());
		int candidateCount = (player.isNPC()) ? 1 : user.getAllCandidateHeroCount();
		player.setCandidateHeros(cardDeck.fetchHeroCardIdsToPlayer(candidateCount, player));
		plugin.sendDealCandidateHeroMessage(player);
		this.playTime = waitingTime;
	}
	
	public void dealHandCard() {
		for (Player player : this.getAllPlayers()) {
			player.initHandWithCardIds(fetchPlayingCardIds(Constant.DEFAULT_HAND_CARD_COUNT));
			player.makeAllHandCardsAvailable();
			plugin.sendDealHandCardMessage(player);
		}
	}
	
	public Deque<Integer> fetchPlayingCardIds(int count) {
		return cardDeck.fetchPlayingCardIds(count);
	}
	
	/**************************************************************************
	 * Listener method implementation
	 */
	@Override
	public void onHeroSelected(Player player) {
//		getLogger().debug("onHeroSelected by player: {}", player.getPlayerName());
		plugin.cancelScheduledExecution(player.getExecutionId());
		plugin.sendSelectedHeroMessage(player);
		allHeroIds.add(player.getSelectedHeroId());
		
		// 1. Show all selected heros until all players selected hero
		// 2. Deal default hand card
		// 3. Choose card to compare for determining the initial player
		if (getPlayerCount() == allHeroIds.size()) {
			allHeroIds.clear();		// Clear all hero ids, re-add by player name's sequence
			for (Player p : this.getAllPlayers()) {
				allHeroIds.add(p.getSelectedHeroId());
			}
			
			plugin.sendShowAllSelectedHerosMessage();
			this.dealHandCard();
			plugin.sendChooseCardToCompareMessage();
		}
	}
	
	@Override
	/**
	 * The initial player is turn owner(has maximum card figure)
	 */
	public void onCardCompared(Player player) {
//		getLogger().debug("onCardCompared by player: {}", player.getPlayerName());
		plugin.cancelScheduledExecution(player.getExecutionId());
		tableCardIds.add(player.getFirstUsedCardId());
		
//		First compared card id
		if (null == maxFigureCard) {
			maxFigureCard = player.getFirstUsedCard();
			this.turnOwner = player;
		}
		PlayingCard comparedCard = player.getFirstUsedCard();
		if (comparedCard.getCardFigure() > maxFigureCard.getCardFigure() ||
			(comparedCard.getCardFigure() == maxFigureCard.getCardFigure() &&
			 comparedCard.getCardSuits() > maxFigureCard.getCardSuits())) {
			maxFigureCard = comparedCard;
			this.turnOwner = player;
		}
		
		if (getPlayerCount() == tableCardIds.size()) {
			tableCardIds.clear();	// Ditto
			for (Player p : this.getAllPlayers()) {
				tableCardIds.add(p.getFirstUsedCardId());
				p.removeComapredCardFromHand();
			}
			
			plugin.sendShowAllComparedCardsMessage();
			executionId = plugin.getApi().scheduleExecution(Constant.EXTRA_WAITING_TIME*1000, 1, this);
		}
	}
	
	@Override
	public void scheduledCallback() {
		if (getUserCount() != startRoundCount) {
			this.startRound();
		} else {
			if (!this.isRoleMode && getPlayerCount() > 2) {
				plugin.sendShowAllPlayersRole();
				for (Player player : getSortedAlivePlayers()) {
					player.setRoleCard(new RoleCard(roleCardIds.pollFirst()));
				}
			}
			
			this.resetValue();
			turnOwner.startTurn();
		}
	}
	
	@Override
	public void onDeckCardChanged(CardDeck cardDeck) {
		plugin.sendTableDeckCardCountMessage();
	}
	
	/**************************************************************************
	 * Start game round (从某个玩家开始牌局)
	 * Turn owner start turn after comparison
	 */
	public void startRound() {
		startRoundCount++;
		if (getUserCount() == startRoundCount) {
			plugin.getApi().cancelScheduledExecution(executionId);
			plugin.sendStartRoundMessage();
			plugin.getApi().scheduleExecution(Constant.DEFAULT_DELAY_SHORT_TIME, 1, this);
		}
	}
	
	/**
	 * If current turn owner is the last player in player list, next turn owner should back to index 1.
	 */
	public void turnToNextPlayer() {
		this.plugin.sendTurnEndPublicMessage(turnOwner);
		this.resetValue();
		this.turnOwner.resetValueAfterTurnEnd();			// Previous turn owner need reset
		this.turnOwner = this.getNextTurnOwner(turnOwner);
		
		this.resetSirenTargetOfPlayer(turnOwner);
		if (turnOwner.isSirenSonging()) {
			turnOwner = this.getNextTurnOwner(turnOwner);	// Skip the player who is being siren
		}
		
		turnOwner.startTurn();
	}
	
	public void resetSirenTargetOfPlayer(Player player) {
		if (!sirenPlayers.isEmpty() && sirenPlayers.contains(player)) {
			sirenPlayers.remove(player);
			player.getSirenTarget().setIsSirenSong(false);	// End siren effect
			player.setSirenTarget(null);
		}
	}
	
//	If used card is normal or super skill card, call this method when back to turn owner
	public void backToTurnOwner() {
		if (this.isCompletedFateTask()) {	// Check game over(Fate task)
			this.gameOver(); return;
		}
		
		this.updateDiscardPile();
		this.resetValue();
		
		if (turnOwner.isDead()) {
			this.turnToNextPlayer();
		} else {
			turnOwner.playCard();
		}
	}
	
//	If used card is magic card, call this method for triggering hero skill.
	public void backToTurnOwner(Callback callback) {
		if (!turnOwner.checkHeroSkill(TriggerReason.TriggerReasonMagicResolved, callback)) {
			this.backToTurnOwner();
		}
	}
	
	private Player getNextTurnOwner(Player fromPlayer) {
		Player player = this.getNextPlayer(fromPlayer);
		if (player.isDead()) {
			return this.getNextTurnOwner(player);
		} else {
			return player;
		}
	}
	
	public Player getNextPlayer(Player fromPlayer) {
		int index = this.getAllPlayers().indexOf(fromPlayer) + 1;
		if (index == getPlayerCount()) index = 0;
		return this.getAllPlayers().get(index);
	}
	
	public Player getPreviousPlayer(Player fromPlayer) {
		int index = this.getAllPlayers().indexOf(fromPlayer) - 1;
		if (index < 0) index = getPlayerCount() - 1;
		return this.getAllPlayers().get(index);
	}
	
	/**************************************************************************
	 * Shift all table cards to discard pile(弃牌堆)
	 */
	private void updateDiscardPile() {
		cardDeck.addDiscardCardIds(tableCardIds);
		this.tableCardIds.clear();
	}
	
	/**************************************************************************
	 * Dispel trigger and handling
	 */	
	public boolean isEverybodyCanceledDispel() {
		for (Player player : this.getAlivePlayers()) {
			if (!player.isCanceledDispel()) return false;
		}
		return true;
	}
	
//	Reset the cancel answer which has been already chosen by all players
	public void resetCancelDispel() {
		for (Player player : this.getAlivePlayers()) {
			player.setIsCanceledDispel(false);
		}
	}
	
	private void sendWaingDispel() {
		if (!this.isSentWaitingDispel) {
			this.isSentWaitingDispel = true;
			plugin.sendWaitingDispelMessage();
		}
	}
	
	public void askForDispel(Callback callback) {
		this.somebodyHasDispel = this.isSentWaitingDispel = false;
		if (null != callback) this.dispelCallback = callback;
		
//		Maybe trigger hero skill and equipment at same time
		for (Player player : this.getAlivePlayers()) {
			boolean isSkillTriggered = player.checkHeroSkill(TriggerReason.TriggerReasonWaitingDispel);
			boolean isEquipTriggered = player.checkEquipment(TriggerReason.TriggerReasonWaitingDispel);
			
			if (isSkillTriggered || isEquipTriggered || player.hasDispel()) {
				this.somebodyHasDispel = true;
				this.isWaitingDispel = true;	// Will be used in getPassiveUsableCardIdList
				this.sendWaingDispel();
				player.setRequiredTargetCount(0);
				player.setRequiredSelCardCount(1);
				player.makePassiveHandCardAvailable();
				plugin.sendChooseCardToUseMessage(player, true);
			} else {
				player.setIsCanceledDispel(true); continue;		// Player hasn't dispel
			}
		}
		
		if (!this.somebodyHasDispel) this.continueResolveAfterDispel();
	}
	
	public void continueResolveAfterDispel() {
		this.setIsWaitingDispel(false);	// Need reset, otherwise game state is always dispelling.
		
		Player lastDispelPlayer = this.dispelPlayers.peekLast();	// e.g.DispelWizard
		if(!dispelPlayers.isEmpty() &&
			lastDispelPlayer.checkHeroSkill(TriggerReason.TriggerReasonDispelEnd, dispelCallback)) {
			return;
		}
		
		if (this.isDispelled()) {		// Dispelled
			if (!damageSource.checkHeroSkill(TriggerReason.TriggerReasonBeDispelled, dispelCallback)) {
				this.getLogger().debug("Dispelled hero skill: {}", damageSource.getHeroSkill());
				if (null != dispelCallback && dispelCallback.isSkill()) {
					dispelCallback.resolveCancel();
				} else {
					this.backToTurnOwner();
				}
			}
		} else {
			if (dispelCallback.isSkill()) {
				this.getLogger().debug("Not dispelled hero skill: {}", dispelCallback);
				dispelCallback.resolveResult();
			} else {
				dispelCallback.resolveUse();
			}
		}
	}
	
	/**************************************************************************
	 * Check every player's hero skill triggering
	 */
	public boolean checkEverybodyHeroSkill(TriggerReason reason, Callback callback) {
		for (Player player : this.getSortedAlivePlayers()) {
			if (player.checkHeroSkill(reason, callback))
				return true;
		}
		return false;
	}
	
	/**************************************************************************
	 * Check every player's equipment triggering
	 */
	public boolean checkEverybodyEquipment(TriggerReason reason, Callback callback) {
		for (Player player : this.getAlivePlayers()) {
			if (player.checkEquipment(reason, callback))
				return true;
		}
		return false;
	}
	
	/**************************************************************************
	 * Ask for help from turn owner
	 */
	public void askForHelpFromPlayer(Player player, Callback callback) {
		if (null == player) {
			this.resolvePlayerDeath(callback); return;
		}
		
		if (!this.checkEverybodyHeroSkill(TriggerReason.TriggerReasonAnyPlayerDying, callback) && 
			!damageSource.checkHeroSkillAndEquipment(TriggerReason.TriggerReasonAnyPlayerDying, callback)) {
			int requiredSalveCount = Math.abs(dyingPlayer.getHealthPoint())+1;
			player.setRequiredTargetCount(0);
			player.setRequiredSelCardCount(1);	// Select one HealingSalve at least
			player.setSelectableCardCount(requiredSalveCount);
			player.makePassiveHandCardAvailable();
			this.plugin.sendChooseCardToUseMessage(player, true);
		}
	}
	
	public void askForNextHelper(Callback callback) {
		Player helper = this.getNextHelperPlayer();
		if (null != helper) {
			this.askForHelpFromPlayer(helper, callback);
		} else {
//			Must remove dying player first, then the somebodyIsDying is false
//			this.allPlayers.remove(dyingPlayer);	// Can't remove it, need while calculating game result.
			this.resolvePlayerDeath(callback);
		}
	}
	
	public boolean somebodyIsDying() {
		for (Player player : this.getAlivePlayers()) {
			if ((null != player.getCharacter()) && player.getHealthPoint() <= 0)
				return true;
		}
		return false;
	}
	
	public Player getNextHelperPlayer() {
		for (Player player : this.getSortedAlivePlayers()) {
			if (!player.isMadeChoice() && player.getHandCardCount() > 0)
				return player;
		}
		return null;
	}
	
//	Reset for resolve next dying player
	public void resetAllPlayersMadeChoice() {
		for (Player player : this.getSortedAlivePlayers()) {
			player.setIsMadeChoice(false);
		}
	}
	
	public void resolvePlayerDeath(Callback callback) {
		this.isDeathResolving = true;
		
		dyingPlayer.setIsDead(true);
		damagedPlayers.remove(dyingPlayer);
		this.resetAllPlayersMadeChoice();
		
		this.isOver = (checkGameOver() || fateCard.checkGameOver(this, dyingPlayer));
		if (this.isOver || (!this.checkEverybodyHeroSkill(TriggerReason.TriggerReasonAnyPlayerDead, callback) &&
			!this.checkEverybodyEquipment(TriggerReason.TriggerReasonAnyPlayerDead, callback) &&
			!damageSource.checkHeroSkill(TriggerReason.TriggerReasonKilledPlayer, callback))) {
			this.continueResolveDeath();
		}
	}
	
	public void continueResolveDeath() {
		this.cumulateKillCount();
		
		if (this.isOver) {
			this.gameOver();
		} else {
			if (this.isFirstBlood()) {
				this.isFirstBlood = false;
				if (!damageSource.isDead()) damageSource.drawCard(1);	// 拿到头血，摸1张牌
			}
			
			if (this.damageSource.isDead()) {
				this.backToTurnOwner();
				return;		// 自杀
			}
			
			if (this.isRoleMode) {
				damageSource.setRequiredTargetCount(1);
				plugin.sendChooseDrawCardOrViewRoleMessage(damageSource);
			} else {
				damageSource.drawCard(Constant.KILL_REWARD_DRAW_CARD_COUNT);
				this.continueResolveAfterPlayerDead();
			}
		}
	}
	
	public boolean checkGameOver() {
		if (getAliveRealPlayerCount() == 0) {
			Player player = this.getAlivePlayers().get(0);
			player.setIsVictory(!player.isEscaped());
			this.isOver = true;
		}
		return this.isOver;
	}
	
	public void continueResolveAfterPlayerDead() {
		this.isDeathResolving = false;
		
		if (this.turnOwner.isDead()) {
			this.backToTurnOwner();
		} else {
//			e.g. Use ChaosAttack kill someone, need continue resolve obtain SP.
			dyingPlayer.finishResolve(dyingPlayer.getCallback());
		}
	}
	
//	Damage source player can not be the dying player self, and two players' role must be different.
	private void cumulateKillCount() {
		if (!damageSource.equals(dyingPlayer) || damageSource.getRoleCard() != dyingPlayer.getRoleCard()) {
			damageSource.addKillEnemyCount(1);
			damageSource.addKillCountPerTurn(1);
			
			if (2 == damageSource.getKillCountPerTurn()) {
				damageSource.addDoubleKillCount(1);
			} else if (damageSource.getKillCountPerTurn() > 2) {
				damageSource.addTripleKillCount(1);
			}
		}
	}
	
	/**************************************************************************
	 * Check and handle game over
	 */
	public boolean isCompletedFateTask() {
//		The fate task SummonerOfDeath or ParanoidMathematician completion leads to game over 
		return ((fateCard.isSummonerOfDeath() || fateCard.isParanoidMathematician()) &&
				fateCard.checkGameOver(this, null));
	}
	
	public boolean hasAlivePlayerWithRole(RoleCardEnum roleEnum) {
		for (Player player : this.getAlivePlayers()) {
			if (player.getRoleCard().getCardEnum() == roleEnum)
				return true;
		}
		return false;
	}
	
//	Get the alive neutral(from fromPlayer) player who is the winner
	public Player getAliveNeutralPlayer(Player fromPlayer) {
		if (!this.hasAlivePlayerWithRole(RoleCardEnum.RoleCardNeutral)) {
			return null;
		}
		
		if (fromPlayer.getRoleCard().isNeutral() && !fromPlayer.isDead()) {
			return fromPlayer;
		} else {
			Player player = this.getNextPlayer(fromPlayer);
			return this.getAliveNeutralPlayer(player);
		}
	}
	
	public void gameOver() {
		for (Player player : this.getAllPlayers()) {
			User user = BeanHelper.getUserBean().getUserByName(player.getPlayerName());
			String nickName = (null != user) ? user.getNickName() : player.getPlayerName();
			
//			Create game result
			GameResult gameResult = new GameResult();
			gameResult.setUserName(player.getPlayerName());
			gameResult.setNickName(nickName);
			gameResult.setHeroId(player.getSelectedHeroId());
			gameResult.setRoleId(player.getRoleCardId());
			gameResult.setAlive(!player.isDead());
			gameResult.setEscaped(player.isEscaped());
			gameResult.setKillEnemyCount(player.getKillEnemyCount());
			gameResult.setDoubleKillCount(player.getDoubleKillCount());
			gameResult.setTripleKillCount(player.getTripleKillCount());
			gameResult.setRewardGoldCoin(this.calculateRewardGoldCoin(player));
			int gotExpPoint = this.calculateExpPoint(player);
			int addExpPoint = (null != user && gotExpPoint > 0) ? (int)(gotExpPoint*user.getAddEpRate()) : 0;
			gameResult.setGotExpPoint(gotExpPoint);
			gameResult.setAddExpPoint(addExpPoint);
			if (player.isVictory()) {
				victoryResults.add(gameResult);
			} else {
				failureResults.add(gameResult);
			}
			
			if (null == user) continue;
			
//			Update user info to database and update user variable
			user.setExpPoint(user.getExpPoint() + gotExpPoint + addExpPoint);
			user.setGoldCoin(user.getGoldCoin() + this.calculateRewardGoldCoin(player));
			int experience = BeanHelper.getLevelExpBean().getExperienceByLevel(user.getLevel());
			if (user.getExpPoint() < experience) {
				byte level = (byte) Math.max((user.getLevel()-1), 1);
				experience = BeanHelper.getLevelExpBean().getExperienceByLevel(level);
				if (user.getExpPoint() < experience) {
					user.setLevel(level);
				}
			} else {
				user.setLevel((byte) (user.getLevel()+1));
			}
			user.addKillEnemyCount(player.getKillEnemyCount());
			user.addDoublekillCount(player.getDoubleKillCount());
			user.addTriplekillCount(player.getTripleKillCount());
			if (player.isEscaped()) {
				user.escaped();
			} else if (player.isVictory()) {
				user.win(player.getRoleCard());
			} else {
				user.fail(player.getRoleCard());
			}
			
			BeanHelper.getUserBean().updateUser(user);
			this.updateUserVariable(user);
		}
		
		plugin.sendGameOverMessage();
	}
	
	private void updateUserVariable(User user) {
		plugin.getApi().createOrUpdateUserVariable(user.getUserName(), VarConstant.kVarUserInfo, user.mapToEsObject());
		
		ReadOnlyUserVariable userVar = plugin.getApi().getUserVariable(user.getUserName(), VarConstant.kVarUserStatus);
		if (null != userVar) {
			userVar.getValue().setBoolean(VarConstant.kParamIsReady, false);
			plugin.getApi().createOrUpdateUserVariable(user.getUserName(), VarConstant.kVarUserStatus, userVar.getValue());
		}
		
		plugin.getApi().deleteUserVariable(user.getUserName(), VarConstant.kVarPickedHero);
	}
	
	private int calculateExpPoint(Player player) {		
		if (player.isEscaped()) {
			return reward.get(kEscape).toInteger();
		} else {
			int expPoint = (player.isVictory()) ? reward.get(kVictory).toInteger() : reward.get(kFailure).toInteger();
			int killExpPoint = player.getKillEnemyCount() * reward.get(kKillEnemy).toInteger();
			return (expPoint + killExpPoint);
		}
	}
	
	private int calculateRewardGoldCoin(Player player) {
		int doubleRewardCount = reward.get(kDoubleKill).toInteger();
		int tripleRewardCount = reward.get(kTripleKill).toInteger();
		
		return ((player.getDoubleKillCount() * doubleRewardCount) +
				(player.getTripleKillCount() * tripleRewardCount));
	}
	
}
