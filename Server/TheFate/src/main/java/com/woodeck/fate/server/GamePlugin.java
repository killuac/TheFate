/**
 * 
 */
package com.woodeck.fate.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import com.electrotank.electroserver5.extensions.BasePlugin;
import com.electrotank.electroserver5.extensions.ChainAction;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.ReadOnlyRoomVariable;
import com.electrotank.electroserver5.extensions.api.value.ReadOnlyUserVariable;
import com.electrotank.electroserver5.extensions.api.value.UserEnterContext;
import com.electrotank.electroserver5.extensions.api.value.UserValue;
import com.woodeck.fate.game.Game;
import com.woodeck.fate.game.ScheduledWaiting;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.util.Constant;
import com.woodeck.fate.util.PluginConstant;
import com.woodeck.fate.util.PluginConstant.Action;
import com.woodeck.fate.util.PluginConstant.UpdateReason;
import com.woodeck.fate.util.VarConstant;

/**
 * @author Killua
 * This class will handle all interactions including request/response/event between client and elctroserver.
 */
public class GamePlugin extends BasePlugin {

	private Collection<UserValue> allUsers;
	private Game game;
	
	
	public Game getGame() {
		return game;
	}
	
	public boolean isNPCForUser(String userName) {
		ReadOnlyUserVariable userVar = getApi().getUserVariable(userName, VarConstant.kVarUserInfo);
		return userVar.getValue().getBoolean(VarConstant.kParamIsNPC, false);
	}
	
	@Override
	public void init(EsObjectRO parameters) {
//		Init also can receive EsObj
        getApi().getLogger().debug("GamePlugin Init");
	}
	
	@Override
	public void request(String userName, EsObjectRO requestParameters) {
		EsObjectExt esObj = new EsObjectExt(requestParameters);
		getApi().getLogger().debug("Receive GAME plugin request from user [{}] with: {}", userName, esObj);
		
		try {
			this.handleRequest(userName, esObj);
			
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			getApi().getLogger().error(sw.toString());
			
			if (game.somebodyIsDying()) {
				game.getDyingPlayer().setIsDead(true);
				game.resolvePlayerDeath(game.getDyingPlayer().getCallback());
			}
			else if (game.checkGameOver()) {
				game.gameOver();
			}
			else {
				game.backToTurnOwner();
			}
		}
	}
	
	@Override
	public ChainAction userEnter(UserEnterContext context) {
		if (null != game && !game.isOver()) {
			Player player = game.getPlayerByName(context.getUserName());
			if (player.isEscaped()) {	// Already escaped, don't allow join again.
				getApi().kickUserFromRoom(context.getUserName(), getApi().getZoneId(), getApi().getRoomId(), "");
			}
		}
		
		return ChainAction.OkAndContinue;
	}
	
	@Override
	public void userDidEnter(String userName) {
		getApi().getLogger().debug("User {} join game", userName);
		if (null == game || game.isOver()) return;
		
		Player player = game.getPlayerByName(userName);
		player.setIsHosting(false);
		
		if (null == player.getCharacter() || null == player.getHand() || null == player.getEquipment()) {
			return;
		}
		
		this.sendRejoinGameMessage(player);
		
		for (Player p : player.getGame().getAllPlayers()) {
			this.sendHeroHpSpPrivateMessage(p, player);
			this.sendHandCardCountPrivateMessage(p, player);
			this.sendEquipmentPrivateMessage(p, player);
			
			if (p.isDead()) {
				this.sendPlayerIsDeadPrivateMessage(p, player); continue;
			}
			if (p.isEscaped()) {
				this.sendPlayerIsEscapedPrivateMessage(p, player);
			}
			if (p.isDying()) {
				this.sendPlayerIsDyingPrivateMessage(p, player);
			}
		}
	}
	
	private void sendRejoinGameMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionRejoinGame);
		esObj.setInteger(PluginConstant.kParamHeroHealthPoint, player.getHealthPoint());
		esObj.setInteger(PluginConstant.kParamHeroSkillPoint, player.getSkillPoint());
		esObj.setUpdateReason(PluginConstant.kPlayerUpdateReason, UpdateReason.UpdateReasonRejoin);
		this.setHandCardIdList(player, esObj);
		this.setEquipmentList(player, esObj);
		this.setAvailableCardIdList(player, esObj);
		this.setAvailableSkillIdList(player, esObj, player.isTurnOwner());
		this.setAvailableEquipIdList(player, esObj, player.isTurnOwner());
		this.setDroppableEquipIdList(player, esObj);
		
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
//	Send private other player's info to the player who just rejoin game
	public void sendHeroHpSpPrivateMessage(Player fromPlayer, Player toPlayer) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerUpdateHero);
		esObj.setInteger(PluginConstant.kParamHeroHealthPoint, fromPlayer.getHealthPoint());
		esObj.setInteger(PluginConstant.kParamHeroSkillPoint, fromPlayer.getSkillPoint());
		this.sendPrivateMessageToUserFromPlugin(fromPlayer, toPlayer, esObj);
	}
	
	public void sendHandCardCountPrivateMessage(Player fromPlayer, Player toPlayer) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerUpdateHandCard);
		this.setHandCardCount(fromPlayer, esObj);
		this.sendPrivateMessageToUserFromPlugin(fromPlayer, toPlayer, esObj);
	}
	
	public void sendEquipmentPrivateMessage(Player fromPlayer, Player toPlayer) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerUpdateEquipment);
		esObj.setUpdateReason(PluginConstant.kPlayerUpdateReason, UpdateReason.UpdateReasonRejoin);
		esObj.setInteger(PluginConstant.kParamPlusDistance, fromPlayer.getPlusDistance());
		esObj.setInteger(PluginConstant.kParamMinusDistance, fromPlayer.getMinusDistance());
		esObj.setInteger(PluginConstant.kParamAttackRange, fromPlayer.getAttackRange());
		esObj.setIntegerArray(PluginConstant.kParamEquipmentIdList, fromPlayer.getEquipmentCardIds());
		
		this.sendPrivateMessageToUserFromPlugin(fromPlayer, toPlayer, esObj);
	}
	
	public void sendPlayerIsDyingPrivateMessage(Player fromPlayer, Player toPlayer) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerIsDying);
		this.sendPrivateMessageToUserFromPlugin(fromPlayer, toPlayer, esObj);
	}
	
	public void sendPlayerIsDeadPrivateMessage(Player fromPlayer, Player toPlayer) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerIsDead);
		esObj.setInteger(PluginConstant.kParamCardId, fromPlayer.getRoleCardId());
		this.sendPrivateMessageToUserFromPlugin(fromPlayer, toPlayer, esObj);
	}
	
	public void sendPlayerIsEscapedPrivateMessage(Player fromPlayer, Player toPlayer) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerIsEscaped);
		this.sendPrivateMessageToUserFromPlugin(fromPlayer, toPlayer, esObj);
	}
	
	@Override
	public void userExit(String userName) {
		getApi().getLogger().debug("User {} exits game", userName);
		
		if (null != game && !game.isOver()) {
			Player player = game.getPlayerByName(userName);
//			User exit game, maybe disconnect, not choose to exit game. So don't set the escape flag. Wait for rejoin.
			if (!player.isDead()) player.setIsHosting(true);
		}
	}
	
	public void kickUserFromRoom(Player player) {
		int zoneId = this.getApi().getZoneId();
		int roomId = this.getApi().getRoomId();
		this.getApi().kickUserFromRoom(player.getPlayerName(), zoneId, roomId, "");
	}
	
	@Override
	public void destroy() {
		super.destroy();
		getApi().getLogger().debug("Game plugin destoryed");
	}
	
	/**************************************************************************
	 * Request handling
	 */
	private void handleRequest(String userName, EsObjectExt esObj) {
		Action action = Action.getEnumById(esObj.getInteger(PluginConstant.kAction, -1));
//		The game state is determined by all server actions, not client action.
//		if (null != game) game.setAction(action);
		
		Player player = (null != game) ? game.getPlayerByName(userName) : null;
		
		if (Action.ActionStartGame != action &&
			Action.ActionStartRound != action &&
			Action.ActionExitGame != action &&
			null != player && player.getExecutionId() == -1) {
			return;
		}
		
		if (Action.ActionExitGame != action &&
			Action.ActionUseHeroSkill != action &&
			Action.ActionUseEquipment != action &&
			Action.ActionCancelHeroSkill != action &&
			Action.ActionCancelEquipment != action) {
			this.cancelScheduledExecution(player);
		}
		
		switch (action) {
			case ActionStartGame:
				this.getApi().setGameLockState(true);
				int zoneId = getApi().getZoneId();
				int roomId = getApi().getRoomId();
				this.allUsers = this.getApi().getUsersInRoom(zoneId, roomId);
				ReadOnlyRoomVariable roomVar = getApi().getRoomVariable(zoneId, roomId, VarConstant.kVarRoomSetting);
				
				boolean isRoleMode = getApi().getGameDetails().getBoolean(PluginConstant.kParamIsRoleMode);
				int playerCount = esObj.getInteger(PluginConstant.kParamPlayerCount);
				game = new Game(this, this.getAllUserNames(), playerCount, isRoleMode);
				game.setPlayTime(roomVar.getValue().getInteger(VarConstant.kParamPlayTime));
				game.setIsNoChatting(roomVar.getValue().getBoolean(VarConstant.kParamIsNoChatting));
				game.startGame();
				break;
				
			case ActionSelectHero:
				player.selectHero(esObj.getInteger(PluginConstant.kParamSelectedHeroId), false);
				break;
				
			case ActionChoseCardToCompare:
				player.choseCardToCompare(getCardIds(esObj).peekFirst());
				break;
				
			case ActionStartRound:
				game.startRound();
				break;
				
			case ActionStartDiscard:
				player.startDiscard();
				break;
				
			case ActionOkayToDiscard:
				player.okayToDiscard(getCardIds(esObj));
				break;
				
			case ActionUseHeroSkill:
				player.useHeroSkill(getHeroSkillId(esObj));
				break;
				
			case ActionUseEquipment:
				player.useEquipement(getEquipmentId(esObj));
				break;
				
			case ActionCancelHeroSkill:
				player.cancelUseHeroSkill();
				break;
				
			case ActionCancelEquipment:
				player.cancelUseEquipment();
				break;
				
			case ActionUseHandCard:
				player.setTargetPlayerNames(getTargetPlayerNames(esObj));
				player.useHandCard(getCardIds(esObj));
				break;
				
			case ActionChoseCardToUse:
				player.choseCardToUse(getCardIds(esObj));
				break;
				
			case ActionOkay:
				player.choseOkay();
				break;
				
			case ActionCancel:
				player.choseCancel();
				break;
				
			case ActionChoseColor:
				player.choseColor(esObj.getInteger(PluginConstant.kParamSelectedColor));
				break;
				
			case ActionChoseSuits:
				player.choseSuits(esObj.getInteger(PluginConstant.kParamSelectedSuits));
				break;
				
			case ActionChoseCardToGet:
				player.choseCardToGet(getCardIndexes(esObj), getCardIds(esObj));
				break;
				
			case ActionChoseCardToGive:
				player.choseCardToGive(getCardIds(esObj));
				break;
				
			case ActionChoseCardToRemove:
				player.choseCardToRemove(getCardIndexes(esObj), getCardIds(esObj));
				break;
				
			case ActionAssignedCard:
				player.setTargetPlayerNames(getTargetPlayerNames(esObj));
				player.assignedCard(getCardIds(esObj));
				break;
				
			case ActionChoseCardToDrop:
				player.setTargetPlayerNames(getTargetPlayerNames(esObj));
				player.choseCardToDrop(getCardIds(esObj));
				break;
				
			case ActionChoseTargetPlayer:
				player.choseTargetPlayer(getTargetPlayerNames(esObj));
				break;
				
			case ActionChoseViewPlayerRole:
				player.choseViewPlayerRole(getTargetPlayerNames(esObj));
				break;
				
			case ActionExitGame:
				player.exitGame();
				break;
				
			default:
				break;
		}
	}
	
	private Deque<String> getTargetPlayerNames(EsObject esObj) {
		return EsObjectExt.stringDequeMapping(esObj.getStringArray(PluginConstant.kParamTargetPlayerNames, new String[0]));
	}
	private Deque<Integer> getCardIds(EsObject esObj) {
		return EsObjectExt.intDequeMapping(esObj.getIntegerArray(PluginConstant.kParamCardIdList, new int[0]));
	}
	private Deque<Integer> getCardIndexes(EsObject esObj) {
		return EsObjectExt.intDequeMapping(esObj.getIntegerArray(PluginConstant.kParamCardIndexList, new int[0]));
	}
	private int getHeroSkillId(EsObject esObj) {
		return esObj.getInteger(PluginConstant.kParamHeroSkillId, 0);
	}
	private int getEquipmentId(EsObject esObj) {
		return esObj.getInteger(PluginConstant.kParamEquipmentId, 0);
	}
	
	private List<String> getAllUserNames() {
		List<String> userNames = new ArrayList<String>(allUsers.size());
		for (UserValue user : allUsers) {
			userNames.add(user.getUserName());
		}
		return userNames;
	}
	
	
	/**************************************************************************
	 * Message sending
	 */
	private void sendPluginMessageToUser(Player player, EsObjectExt esObj, boolean isWaiting) {
		game.setAction(Action.getEnumById(esObj.getInteger(PluginConstant.kAction)));
		esObj.setGameState(PluginConstant.kGameState, game.getState());
		esObj.setBoolean(PluginConstant.kParamIsNewWaiting, isWaiting);
		
		if (!player.isNPC() && !player.isHosting()) {
			this.getApi().sendPluginMessageToUser(player.getPlayerName(), esObj);
		}
		if (isWaiting) player.setExecutionId(scheduleExecution(player));
		
		getApi().getLogger().debug("Send PLUGIN message to user [{}] with {}", player.getPlayerName(), esObj);
	}
	
//	private void sendPluginMessageToUsers(List<Player> players, EsObjectExt esObj, boolean isWaiting) {
//		game.setAction(Action.getEnumById(esObj.getInteger(PluginConstant.kAction)));
//		esObj.setGameState(PluginConstant.kGameState, game.getState());
//		esObj.setBoolean(PluginConstant.kParamIsNewWaiting, isWaiting);
//		
//		Collection<String> userNames = new ArrayList<String>(players.size());
//		for (Player player : players) {
//			userNames.add(player.getPlayerName());
//			if (isWaiting) player.setExecutionId(scheduleExecution(player));
//		}
//		this.getApi().sendPluginMessageToUsers(userNames, esObj);
//		
//		getApi().getLogger().debug("Send PLUGIN message to users [{}] with {}", userNames, esObj);
//	}
	
	private void sendPluginMessageToRoom(EsObjectExt esObj, boolean isWaiting) {
		game.setAction(Action.getEnumById(esObj.getInteger(PluginConstant.kAction)));
		esObj.setGameState(PluginConstant.kGameState, game.getState());
		esObj.setBoolean(PluginConstant.kParamIsNewWaiting, isWaiting);
		
		this.getApi().sendPluginMessageToRoom(
				getApi().getZoneId(),
				getApi().getRoomId(),
				esObj);
		
		if (isWaiting) {
			for (Player player : game.getAlivePlayers()) {
				player.setExecutionId(scheduleExecution(player));
			}
		}
		
		getApi().getLogger().debug("Send PLUGIN message to room with {}", esObj);
	}
	
	private void sendPublicMessageToRoomFromUser(Player player, EsObjectExt esObj) {
		game.setAction(Action.getEnumById(esObj.getInteger(PluginConstant.kAction)));
		esObj.setGameState(PluginConstant.kGameState, game.getState());
		
		this.getApi().sendPublicMessageToRoomFromPlugin(
				player.getPlayerName(),
				getApi().getZoneId(),
				getApi().getRoomId(),
				"",
				esObj,
				false,
				false);
		getApi().getLogger().debug("Send public message to room by user [{}] with {} ", player.getPlayerName(), esObj);
		
//		分配完明置的牌，记录每个玩家得到的牌
		if (Action.ActionTableShowAssignedCard == game.getAction()) {
			int idx = 0;
			int[] cardIds = esObj.getIntegerArray(PluginConstant.kParamCardIdList, new int[]{});
			if (cardIds.length <= 0) return;
			for (Player tarPlayer : player.getTargetPlayers()) {
				esObj.setIntegerArray(PluginConstant.kParamCardIdList, cardIds[idx]);
				tarPlayer.addGameHistory(esObj); idx++;
			}
		} else {
			player.addGameHistory(esObj);	// Add game history(牌局历史记录)
		}
	}
	
	private void sendPrivateMessageToUserFromPlugin(Player fromPlayer, Player toPlayer, EsObjectExt esObj) {
		game.setAction(Action.getEnumById(esObj.getInteger(PluginConstant.kAction)));
		esObj.setGameState(PluginConstant.kGameState, game.getState());
		
		this.getApi().sendPrivateMessageToUserFromPlugin(
				fromPlayer.getPlayerName(),
				toPlayer.getPlayerName(),
				"",
				esObj,
				false);
		
		getApi().getLogger().debug("Send private message from user [{}] to user[{}] with {} ",
				fromPlayer.getPlayerName(), toPlayer.getPlayerName(), esObj);
	}
	
	/**
	 * Send invalid action to client and make it crash
	 */
	public void sendInvalidMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionNull);
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
	/**************************************************************************
	 * Send plugin message to user or room
	 */
	public void sendStartGameMessage() {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionStartGame);
		esObj.setInteger(PluginConstant.kParamDeckCardCount, game.getDeckCardCount());
		esObj.setStringArray(PluginConstant.kParamAllPlayerNames, game.getAlivePlayerNames());
		esObj.setInteger(PluginConstant.kParamCardId, game.getFateCardId());
		if (game.isRoleMode() || game.getPlayerCount() == 2) {
			esObj.setIntegerArray(PluginConstant.kParamCardIdList, game.getRoleCardIds());
		}
		this.sendPluginMessageToRoom(esObj, false);
		this.sendTableDeckCardCountMessage();
	}
	
	public void sendStartRoundMessage() {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionStartRound);
		this.sendPluginMessageToRoom(esObj, false);
	}
	
//	2V2时，拼点后自己的上家和自己是一个阵营的
	public void sendShowAllPlayersRole() {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionShowAllPlayersRole);
		esObj.setString(PluginConstant.kParamTurnOwnerName, game.getTurnOwner().getPlayerName());
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, game.getRoleCardIds());
		this.sendPluginMessageToRoom(esObj, false);
	}
	
	public void sendGameOverMessage() {
		for (Player player : game.getAllPlayers()) {
			this.cancelScheduledExecution(player);
		}
		
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionGameOver);
		esObj.setGameResult(PluginConstant.kParamVictoryResults, game.getVictoryResults());
		esObj.setGameResult(PluginConstant.kParamFailureResults, game.getFailureResults());
		this.sendPluginMessageToRoom(esObj, false);
		
		this.getApi().setGameLockState(false);
	}
	
	public void sendExitGameMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionExitGame);
		this.sendPublicMessageToRoomFromUser(player, esObj);
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
	private void setPlayTime(EsObjectExt esObj) {
		esObj.setInteger(VarConstant.kParamPlayTime, game.getPlayTime());
	}
	private void setDamageSourceName(EsObjectExt esObj) {
		esObj.setString(PluginConstant.kParamDamageSourceName, game.getDamageSourceName());
	}
	private void setTargetPlayerNames(Player player, EsObjectExt esObj) {
		esObj.setStringArray(PluginConstant.kParamTargetPlayerNames, player.getTargetPlayerNames());
	}
	private void setAvailableCardIdList(Player player, EsObjectExt esObj) {
		esObj.setIntegerArray(PluginConstant.kParamAvailableCardIdList, player.getAvailableCardIdList());
	}
	private void setAvailableSkillIdList(Player player, EsObjectExt esObj, boolean isActive) {
		esObj.setIntegerArray(PluginConstant.kParamAvailableSkillIdList, player.getAvailableSkillIdList(isActive));
	}
	private void setAvailableEquipIdList(Player player, EsObjectExt esObj, boolean isActive) {
		esObj.setIntegerArray(PluginConstant.kParamAvailableEquipIdList, player.getAvailableEquipIdList(isActive));
	}
	private void setDroppableEquipIdList(Player player, EsObjectExt esObj) {
		esObj.setIntegerArray(PluginConstant.kParamDroppableEquipIdList, player.getDroppableEquipIdList());
	}
	private void setSelectableCardCount(Player player, EsObjectExt esObj) {
		esObj.setInteger(PluginConstant.kParamSelectableCardCount, player.getSelectableCardCount());
	}
	private void setRequiredSelCardCount(Player player, EsObjectExt esObj) {
		esObj.setInteger(PluginConstant.kParamRequiredSelCardCount, player.getRequiredSelCardCount());
	}
	private void setRequiredTargetCount(Player player, EsObjectExt esObj) {
		esObj.setInteger(PluginConstant.kParamRequiredTargetCount, player.getRequiredTargetCount());
	}
	private void setMaxTargetCount(Player player, EsObjectExt esObj) {
		esObj.setInteger(PluginConstant.kParamMaxTargetCount, player.getMaxTargetCount());
	}
	private void setHandCardIdList(Player player, EsObjectExt esObj) {
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, player.getHandCardIds());
	}
	private void setHandCardCount(Player player, EsObjectExt esObj) {
		esObj.setInteger(PluginConstant.kParamHandCardCount, player.getHandCardCount());
	}
	private void setEquipmentList(Player player, EsObjectExt esObj) {
		esObj.setIntegerArray(PluginConstant.kParamEquipmentIdList, player.getEquipmentCardIds());
	}
	private void setHeroSkillId(Player player, EsObjectExt esObj) {
		esObj.setInteger(PluginConstant.kParamHeroSkillId, player.getHeroSkillId());
	}
	private void setEquipmentId(Player player, EsObjectExt esObj) {
		esObj.setInteger(PluginConstant.kParamEquipmentId, player.getEquipmentId());
	}
	private void setTransformedCardId(Player player, EsObjectExt esObj) {
		esObj.setInteger(PluginConstant.kParamTransformedCardId, player.getTransformedCardId());
	}
	private void setIsDyingTriggered(Player player, EsObjectExt esObj) {
		esObj.setBoolean(PluginConstant.kParamIsDyingTriggered, player.isDyingSkillTriggered());
	}
	
	public void sendStartTurnMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionStartTurn);
		
		this.sendPublicMessageToRoomFromUser(player, esObj);
		this.sendPluginMessageToUser(player, esObj, true);
	}
	
	public void sendPlayCardMessage(Player player, boolean isNewWaiting) {	// 是否开始新的等待计时
		game.setIsNewWaiting(isNewWaiting);
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayCard);
		esObj.setBoolean(PluginConstant.kParamIsNewWaiting, isNewWaiting);
		this.setHeroSkillId(player, esObj);
		this.setEquipmentId(player, esObj);
		this.setTransformedCardId(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
		
		this.setSelectableCardCount(player, esObj);
		this.setRequiredSelCardCount(player, esObj);
		this.setRequiredTargetCount(player, esObj);
		this.setMaxTargetCount(player, esObj);
		this.setAvailableCardIdList(player, esObj);
		this.setAvailableSkillIdList(player, esObj, true);
		this.setAvailableEquipIdList(player, esObj, true);
		this.sendPluginMessageToUser(player, esObj, isNewWaiting);
	}
	
	public void sendWaitingDispelMessage() {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionWaitingDispel);
		this.sendPluginMessageToRoom(esObj, false);
	}
	
	public void sendCancelWaitingMessage() {
		for (Player player : game.getAlivePlayers()) {
			this.cancelScheduledExecution(player.getExecutionId());
		}
		
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionCancelWaiting);
		this.sendPluginMessageToRoom(esObj, false);
	}
	
	public void sendDiscardCardMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionDiscardCard);
		this.sendPublicMessageToRoomFromUser(player, esObj);
		
		this.setSelectableCardCount(player, esObj);
		this.setRequiredSelCardCount(player, esObj);
		this.setAvailableCardIdList(player, esObj);
		this.sendPluginMessageToUser(player, esObj, true);
	}
	
	/**
	 * Message: Show card on the table
	 */
	public void sendTableDeckCardCountMessage() {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableDeckCardCount);
		esObj.setInteger(PluginConstant.kParamDeckCardCount, game.getDeckCardCount());
		this.sendPluginMessageToRoom(esObj, false);
	}
	
	public void sendDealCandidateHeroMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableDealCandidateHero);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, player.getCandidateHeros());
		this.setPlayTime(esObj);
		this.sendPluginMessageToUser(player, esObj, true);
	}
	
	public void sendShowAllSelectedHerosMessage() {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableShowAllSelectedHeros);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, game.getAllHeroIds());
		this.sendPluginMessageToRoom(esObj, false);
	}
	
	public void sendShowAllComparedCardsMessage() {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableShowAllComparedCards);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, game.getTableCardIds());
		esObj.setInteger(PluginConstant.kParamMaxFigureCardId, game.getMaxFigureCardId());
		this.sendPluginMessageToRoom(esObj, false);
		
		for (Player player : game.getAllPlayers()) {
			player.addGameHistory(esObj);	// Add game history(牌局历史记录)
		}
	}
	
	public void sendShowPlayerAllCardsMessage(Player player, Player targetPlayer) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableShowPlayerAllCards);
		this.sendPublicMessageToRoomFromUser(player, esObj);
		
		this.setHandCardCount(targetPlayer, esObj);
		this.setEquipmentList(targetPlayer, esObj);
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
	public void sendShowPlayerHandCardMessage(Player player, Player targetPlayer) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableShowPlayerHandCard);
		this.setHeroSkillId(player, esObj);		// Maybe cleared by BladeMail, need restore.
		esObj.setStringArray(PluginConstant.kParamTargetPlayerNames, targetPlayer.getPlayerName());
		this.sendPublicMessageToRoomFromUser(player, esObj);
		
		this.setHandCardCount(targetPlayer, esObj);
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
	public void sendShowPlayerEquipmentMessage(Player player, Player targetPlayer) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableShowPlayerEquipment);
		this.sendPublicMessageToRoomFromUser(player, esObj);
		
		this.setEquipmentList(targetPlayer, esObj);
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
	public void sendShowAssignedCardMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableShowAssignedCard);
		this.sendPublicMessageToRoomFromUser(player, esObj);

		esObj.setIntegerArray(PluginConstant.kParamCardIdList, player.getAssignedCardIds());
		this.setTargetPlayerNames(player, esObj);
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
	public void sendShowAssignedCardPublicMessage(Player player, Deque<Integer>cardIds) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableShowAssignedCard);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, cardIds);
		this.setTargetPlayerNames(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	/**
	 * Message: Update player data
	 */
	public void sendSelectedHeroMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerSelectedHero);
		esObj.setInteger(PluginConstant.kParamSelectedHeroId, player.getSelectedHeroId());
		esObj.setBoolean(PluginConstant.kParamIsPickedHero, player.isPickedHero());
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
	public void sendShowRoleCardMessage(Player player, int roleCardId) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerShowRoleCard);
		esObj.setInteger(PluginConstant.kParamCardId, roleCardId);
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
	public void sendDealHandCardMessage(Player player) {
		this.sendHandCardCountPublicMessage(player);
		
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerDealHandCard);
		this.setHandCardCount(player, esObj);
		this.setHandCardIdList(player, esObj);
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
	public void sendHandCardCountPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerUpdateHandCard);
		this.setHandCardCount(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendUpdateHandCardMessage(Player player, int countChanged, UpdateReason reason) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerUpdateHandCard);
		esObj.setInteger(PluginConstant.kParamCardCount, countChanged);
		esObj.setUpdateReason(PluginConstant.kPlayerUpdateReason, reason);
		this.sendHandCardCountPublicMessage(player);
		
		this.setHandCardIdList(player, esObj);
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
	public void sendUpdateEquipmentMessage(Player player, int countChanged, UpdateReason reason) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerUpdateEquipment);
		esObj.setInteger(PluginConstant.kParamCardCount, countChanged);
		esObj.setUpdateReason(PluginConstant.kPlayerUpdateReason, reason);
		esObj.setInteger(PluginConstant.kParamPlusDistance, player.getPlusDistance());
		esObj.setInteger(PluginConstant.kParamMinusDistance, player.getMinusDistance());
		esObj.setInteger(PluginConstant.kParamAttackRange, player.getAttackRange());
		this.setEquipmentList(player, esObj);
		
		this.sendPublicMessageToRoomFromUser(player, esObj);
		this.sendPluginMessageToUser(player, esObj, false);
	}
	
	/**
	 * Message: Choosing card, color or suits
	 */
	public void sendChooseCardToCompareMessage() {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseCardToCompare);
		esObj.setInteger(PluginConstant.kParamSelectableCardCount, Constant.DEFAULT_SELECTABLE_CARD_COUNT);
		esObj.setInteger(PluginConstant.kParamRequiredSelCardCount, Constant.DEFAULT_SELECTABLE_CARD_COUNT);
		this.sendPluginMessageToRoom(esObj, true);
	}
	
	public void sendChooseCardToUseMessage(Player player, boolean isNewWaiting) {
		game.setIsNewWaiting(isNewWaiting);
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseCardToUse);
		esObj.setBoolean(PluginConstant.kParamIsDyingTriggered, false);
		this.setHeroSkillId(player, esObj);
		this.setEquipmentId(player, esObj);
		this.setTransformedCardId(player, esObj);
		this.setDamageSourceName(esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
		
		this.setSelectableCardCount(player, esObj);
		this.setRequiredSelCardCount(player, esObj);
		this.setRequiredTargetCount(player, esObj);
		this.setMaxTargetCount(player, esObj);
		this.setAvailableCardIdList(player, esObj);
		this.setAvailableSkillIdList(player, esObj, false);
		this.setAvailableEquipIdList(player, esObj, false);
		if (game.getTurnOwner().isNPC()) {
			this.setTargetPlayerNames(game.getTurnOwner(), esObj);
		}
		this.sendPluginMessageToUser(player, esObj, isNewWaiting);
	}
	
	public void sendChooseColorMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseColor);
		
		this.sendPublicMessageToRoomFromUser(player, esObj);
		this.sendPluginMessageToUser(player, esObj, true);
	}
	
	public void sendChooseSuitsMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseSuits);
		
		this.sendPublicMessageToRoomFromUser(player, esObj);		
		this.sendPluginMessageToUser(player, esObj, true);
	}
	
	public void sendChooseCardToGetMessage(Player player, boolean isGreeding) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseCardToGet);
		this.sendPublicMessageToRoomFromUser(player, esObj);
		
		esObj.setBoolean(PluginConstant.kParamIsGreeding, isGreeding);
		this.setSelectableCardCount(player, esObj);
		this.setRequiredSelCardCount(player, esObj);
		this.sendPluginMessageToUser(player, esObj, true);
	}
	
	public void sendChooseCardToGiveMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseCardToGive);
		this.sendPublicMessageToRoomFromUser(player, esObj);
		
		this.setSelectableCardCount(player, esObj);
		this.setRequiredSelCardCount(player, esObj);
		this.setAvailableCardIdList(player, esObj);
		this.sendPluginMessageToUser(player, esObj, true);
	}
	
	public void sendChooseCardToRemoveMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseCardToRemove);
		this.setDamageSourceName(esObj);
		this.setTargetPlayerNames(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
		
		this.setSelectableCardCount(player, esObj);
		this.setRequiredSelCardCount(player, esObj);
		this.sendPluginMessageToUser(player, esObj, true);
	}
	
	public void sendChooseCardToAssignMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseCardToAssign);
		this.setPlayTime(esObj);
		
		this.sendPublicMessageToRoomFromUser(player, esObj);
		this.sendPluginMessageToUser(player, esObj, true);
	}
	
	public void sendChooseCardToDropMessage(Player player, boolean isNewWaiting) {
		game.setIsNewWaiting(isNewWaiting);
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseCardToDrop);
		esObj.setBoolean(PluginConstant.kParamIsRequiredDrop, player.isRequiredDrop());
		this.setIsDyingTriggered(player, esObj);
		this.setHeroSkillId(player, esObj);
		this.setEquipmentId(player, esObj);
		this.setDamageSourceName(esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
		
		this.setSelectableCardCount(player, esObj);
		this.setRequiredSelCardCount(player, esObj);
		this.setRequiredTargetCount(player, esObj);
		this.setMaxTargetCount(player, esObj);
		this.setAvailableCardIdList(player, esObj);
		this.setDroppableEquipIdList(player, esObj);	// 通过弃任意牌发动技能
		this.sendPluginMessageToUser(player, esObj, isNewWaiting);
	}
	
	public void sendChooseOkayOrCancelMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseOkayOrCancel);
		esObj.setBoolean(PluginConstant.kParamIsStrengthening, player.isStrengthening());
		this.setIsDyingTriggered(player, esObj);
		this.setPlayTime(esObj);	//e.g.WildAxe(choose left or right)
		this.setHeroSkillId(player, esObj);
		this.setEquipmentId(player, esObj);
		
		this.sendPublicMessageToRoomFromUser(player, esObj);
		this.sendPluginMessageToUser(player, esObj, true);
	}
	
	public void sendChooseTargetPlayerMessage(Player player, boolean isNewWaiting) {
		game.setIsNewWaiting(isNewWaiting);
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseTargetPlayer);
		esObj.setBoolean(PluginConstant.kParamIsRequiredTarget, player.isRequiredTarget());
		esObj.setBoolean(PluginConstant.kParamIsNoNeedCard, true);
		this.setRequiredTargetCount(player, esObj);
		this.setMaxTargetCount(player, esObj);
		this.setHeroSkillId(player, esObj);
		this.setEquipmentId(player, esObj);
		
		this.sendPublicMessageToRoomFromUser(player, esObj);
		this.sendPluginMessageToUser(player, esObj, isNewWaiting);
	}
	
	/**
	 * Message: Choose draw 2 cards or view another player's role
	 */
	public void sendChooseDrawCardOrViewRoleMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChooseDrawCardOrViewRole);
		esObj.setBoolean(PluginConstant.kParamIsNoNeedCard, true);
		this.setRequiredTargetCount(player, esObj);
		this.setMaxTargetCount(player, esObj);
		
		this.sendPublicMessageToRoomFromUser(player, esObj);
		this.sendPluginMessageToUser(player, esObj, true);
	}
	
	/**************************************************************************
	 * Send public message by user
	 */
	public void sendDrawCardPublicMessage(Player player, int cardCount) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionDrawCard);
		esObj.setInteger(PluginConstant.kParamCardCount, cardCount);		
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendOkayPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionOkay);
		esObj.setBoolean(PluginConstant.kParamIsStrengthened, player.isStrengthening());
		this.setHeroSkillId(player, esObj);
		this.setEquipmentId(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendCancelPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionCancel);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendStartDiscardPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionStartDiscard);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendOkayToDiscardPublicMessage(Player player, Deque<Integer> cardIds) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionOkayToDiscard);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, cardIds);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendTurnEndPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTurnEnd);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendUseHandCardPublicMessage(Player player, Deque<Integer> cardIds) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionUseHandCard);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, cardIds);
		this.setTargetPlayerNames(player, esObj);
		this.setHeroSkillId(player, esObj);
		this.setEquipmentId(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendUseHeroSkillPublicMessage(Player player, int skillId) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionUseHeroSkill);
		esObj.setInteger(PluginConstant.kParamHeroSkillId, skillId);
		this.setTransformedCardId(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendUseEquipmentPublicMessage(Player player, int equipmentId) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionUseEquipment);
		esObj.setInteger(PluginConstant.kParamEquipmentId, equipmentId);
		this.setTransformedCardId(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendClearHeroSkillPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionClearHeroSkill);
		
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendClearEquipmentPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionClearEquipment);
		
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	/**
	 * Public message: Update player data
	 */
	public void sendUpdateHeroPublicMessage(Player player, int hpChanged, int spChanged, boolean isSunder) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerUpdateHero);
		esObj.setInteger(PluginConstant.kParamHeroHealthPoint, player.getHealthPoint());
		esObj.setInteger(PluginConstant.kParamHeroSkillPoint, player.getSkillPoint());
		esObj.setInteger(PluginConstant.kParamHeroHpChanged, hpChanged);
		esObj.setInteger(PluginConstant.kParamHeroSpChanged, spChanged);
		esObj.setBoolean(PluginConstant.kParamIsSunder, isSunder);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendGetCardFromTablePublicMessage(Player player, Deque<Integer> cardIds) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerGetCardFromTable);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, cardIds);
		this.setTargetPlayerNames(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendSkillOrEquipmentTriggeredPublicMessage(Player player, int skillId, int equipId) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerSkillOrEquipmentTriggered);
		esObj.setInteger(PluginConstant.kParamHeroSkillId, skillId);
		esObj.setInteger(PluginConstant.kParamEquipmentId, equipId);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendPlayerIsDyingPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerIsDying);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendPlayerIsDeadPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerIsDead);
		esObj.setInteger(PluginConstant.kParamCardId, player.getRoleCardId());
		esObj.setBoolean(PluginConstant.kParamIsFirstBlood, game.isFirstBlood());
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendPlayerIsEscapedPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionPlayerIsEscaped);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	/**
	 * Public message: Show card on the table
	 */
	public void sendFaceDownCardFromDeckPublicMessage(Player player, int count) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableFaceDownCardFromDeck);
		esObj.setInteger(PluginConstant.kParamCardCount, count);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendFaceUpTableCardPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableFaceUpTheCard);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, player.getJudgedCard().getCardId());
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendRevealOneCardFromDeckPublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionTableRevealOneCardFromDeck);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, player.getJudgedCard().getCardId());
		
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	/**
	 * Public message: Chose card, color or suits
	 */
	public void sendChoseCardToUsePublicMessage(Player player, Deque<Integer> cardIds) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChoseCardToUse);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, cardIds);
		this.setHeroSkillId(player, esObj);
		this.setEquipmentId(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendChoseColorPublicMessage(Player player, int color) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChoseColor);
		esObj.setInteger(PluginConstant.kParamSelectedColor, color);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendChoseSuitsPublicMessage(Player player, int suits) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChoseSuits);
		esObj.setInteger(PluginConstant.kParamSelectedSuits, suits);
		this.sendPublicMessageToRoomFromUser(player, esObj);		
	}
	
	public void sendChoseCardToGetPublicMessage(Player player, int cardCount, Deque<Integer> cardIds) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChoseCardToGet);
		esObj.setInteger(PluginConstant.kParamCardCount, cardCount);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, cardIds);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendChoseCardToGivePublicMessage(Player player, int cardCount) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChoseCardToGive);
		esObj.setInteger(PluginConstant.kParamCardCount, cardCount);
		this.sendPublicMessageToRoomFromUser(player, esObj);		
	}
	
	public void sendChoseCardToRemovePublicMessage(Player player, Deque<Integer> cardIds) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChoseCardToRemove);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, cardIds);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendAssignedCardublicMessage(Player player) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionAssignedCard);
		this.sendPublicMessageToRoomFromUser(player, esObj);		
	}
	
	public void sendChoseCardToDropPublicMessage(Player player, Deque<Integer> cardIds) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChoseCardToDrop);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, cardIds);
		this.setTargetPlayerNames(player, esObj);
		this.setHeroSkillId(player, esObj);
		this.setEquipmentId(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendChoseTargetPlayerPublicMessage(Player player, Deque<String> tarPlayerNames) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChoseTargetPlayer);
		esObj.setStringArray(PluginConstant.kParamTargetPlayerNames, tarPlayerNames);
		this.setHeroSkillId(player, esObj);
		this.setEquipmentId(player, esObj);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	public void sendChoseViewPlayerRole(Player player, Deque<String> tarPlayerNames) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionChoseViewPlayerRole);
		esObj.setStringArray(PluginConstant.kParamTargetPlayerNames, tarPlayerNames);
		this.sendPublicMessageToRoomFromUser(player, esObj);
	}
	
	/**************************************************************************
	 * Schedule execution
	 */
    public int scheduleExecution(Player player) {
    	this.cancelScheduledExecution(player.getExecutionId());
    	player.setExecutionId(-1);
    	
    	getApi().getLogger().debug("Start schedule execution");
    	int waitingTime = (player.isNPC() || player.isHosting()) ?
    			Constant.DEFAULT_AI_PLAY_TIME*1000 : (game.getPlayTime()+Constant.EXTRA_WAITING_TIME)*1000;
        return getApi().scheduleExecution(waitingTime, 1, new ScheduledWaiting(player));
    }
    
    public void cancelScheduledExecution(int callback_id) {
        this.getApi().cancelScheduledExecution(callback_id);
        getApi().getLogger().debug("Cancel scheduled execution");
    }
    
    public void cancelScheduledExecution(Player player) {
    	if (null != player) {
	    	this.cancelScheduledExecution(player.getExecutionId());
			player.setExecutionId(-1);
    	}
    }
    
}
