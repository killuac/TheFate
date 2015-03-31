/**
 * 
 */
package com.woodeck.fate.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executor;

import com.electrotank.electroserver5.extensions.BasePlugin;
import com.electrotank.electroserver5.extensions.ChainAction;
import com.electrotank.electroserver5.extensions.api.ScheduledCallback;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.ReadOnlyRoomVariable;
import com.electrotank.electroserver5.extensions.api.value.ReadOnlyUserVariable;
import com.electrotank.electroserver5.extensions.api.value.RoomValue;
import com.electrotank.electroserver5.extensions.api.value.UserEnterContext;
import com.electrotank.electroserver5.extensions.api.value.UserValue;
import com.woodeck.fate.card.HeroCard.HeroCardId;
import com.woodeck.fate.util.Constant;
import com.woodeck.fate.util.PluginConstant;
import com.woodeck.fate.util.PluginConstant.Action;
import com.woodeck.fate.util.VarConstant;

/**
 * @author Killua
 * This class will handle all interactions including request/response/event between client and elctroserver.
 */
public class RoomPlugin extends BasePlugin implements ScheduledCallback {
	
	private Executor executor;
	private Process process = null;
	
	private int executionId = -1;
	private int zoneId;
	private int roomId;
	private String roomOwner;
	
	public boolean isNPCForUser(String userName) {
		ReadOnlyUserVariable userVar = getApi().getUserVariable(userName, VarConstant.kVarUserInfo);
		return userVar.getValue().getBoolean(VarConstant.kParamIsNPC, false);
	}
	
	protected final void execute( Runnable command ) {
        if (null == executor) {
            throw new IllegalStateException("Executor not initialized");
        }
        executor.execute(command);
    }
	
	@Override
	public void init(EsObjectRO parameters) {
        getApi().getLogger().debug("RoomPlugin Init");
//        executor = (Executor) getApi().acquireManagedObject(ExecutorFactory.FACTORY_NAME, null);
	}
	
	@Override
	public void request(String userName, EsObjectRO requestParameters) {
		EsObjectExt esObj = new EsObjectExt(requestParameters);
		getApi().getLogger().debug("Receive ROOM plugin request from user [{}] with: {}", userName, esObj);
		
		try {
			this.handleRequest(userName, esObj);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			getApi().getLogger().error(sw.toString());
		}
		
//		execute(new Runnable() {
//            @Override
//            public void run() {
//            	// Maybe execute database operation in second thread pool.
//            }
//        });
	}
	
	@Override
	public ChainAction userEnter(UserEnterContext context) {		
		this.zoneId = getApi().getZoneId();
		this.roomId = getApi().getRoomId();
		int userCount = getApi().getUsersInRoom(zoneId, roomId).size();
		if (userCount == 1 && isNPCForUser(context.getUserName())) {
			return ChainAction.Fail;
		}
		
		Boolean isRoomOwner = (userCount == 1);
		if (isRoomOwner) this.roomOwner = context.getUserName();
		
		EsObject esObj = new EsObject();
		esObj.setBoolean(VarConstant.kParamIsRoomOwner, isRoomOwner);
		esObj.setBoolean(VarConstant.kParamIsReady, isRoomOwner);
		getApi().createOrUpdateUserVariable(context.getUserName(), VarConstant.kVarUserStatus, esObj);
		
		return ChainAction.OkAndContinue;
	}
	
	@Override
	public void userDidEnter(String userName) {
		List<UserValue> users = (List<UserValue>) getApi().getUsersInRoom(zoneId, roomId);
		getApi().getLogger().debug("User {} join room", userName);
		getApi().getLogger().debug("User count {} in room", users.size());
		
		boolean isRoleMode = getApi().getGameDetails().getBoolean(PluginConstant.kParamIsRoleMode);
		int roomCapacity = getApi().getRoom(zoneId, roomId).getCapacity();
		if (!isRoleMode && users.size() < roomCapacity) {
			this.scheduleExcution();
		} else {
			this.cancelScheduledExectuion();
		}
	}
	
	private void scheduleExcution() {
		this.executionId = getApi().scheduleExecution(Constant.WAITING_PLAYER_TIME, 1, this);
	}
	
	private void cancelScheduledExectuion() {
		getApi().cancelScheduledExecution(this.executionId);
	}
	
	@Override
	/**
	 * Add NPC user if waiting for interval time
	 */
	public void scheduledCallback() {
		getApi().getLogger().debug("Run scheduled callback - Add NPC");		
		RoomValue room = getApi().getRoom(zoneId, roomId);
		String gameType = (room.getCapacity() == 2) ? PluginConstant.GAMETYPE_NEWBIE : PluginConstant.GAMETYPE_VERSUS;
		
		try {
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", "Client.jar", gameType);
			pb.directory(new File("extensions/TheFate"));
			this.process = pb.start();
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			getApi().getLogger().error(sw.toString());
		}
	}
	
	@Override
//	If the exit user is room owner, need set next player as room owner. Always first user is room owner.
	public void userExit(String userName) {
		getApi().getLogger().debug("User {} exits room", userName);
		this.updateUserVariable(userName);
	}
	
	private void updateUserVariable(String userName) {
		List<UserValue> users = (List<UserValue>) getApi().getUsersInRoom(zoneId, roomId);
		getApi().getLogger().debug("User count {} in room", users.size());
		
//		Room owner left room, need change owner with another user
		if (!users.isEmpty() && userName.equals(roomOwner)) {
			this.roomOwner = users.get(0).getUserName();
			EsObject esObj = new EsObject();
			esObj.setBoolean(VarConstant.kParamIsRoomOwner, true);
			esObj.setBoolean(VarConstant.kParamIsReady, true);
			getApi().createOrUpdateUserVariable(roomOwner, VarConstant.kVarUserStatus, esObj);
		}
		
		ReadOnlyUserVariable userVar = getApi().getUserVariable(userName, VarConstant.kVarPickedHero);
		int heroId = (null != userVar) ? userVar.getValue().getInteger(VarConstant.kParamPickedHeroId, 0) : 0;
		if (heroId != HeroCardId.HeroCardNull) this.updatePickedHeroIds(heroId);
		
		getApi().deleteUserVariable(userName, VarConstant.kVarUserStatus);
		getApi().deleteUserVariable(userName, VarConstant.kVarPickedHero);
		
//		Schedule add NPC or destroy all NPC users if no real user
		for (UserValue user : users) {
			if (!isNPCForUser(user.getUserName())) {
				this.scheduleExcution(); return;
			}
		}
		
		if (null != process) this.process.destroy();
	}
	
	@Override
	public void userKicked(String userName, String message) {
		getApi().getLogger().debug("User {} was kicked", userName);
		this.updateUserVariable(userName);
	}
	
	@Override
	public void destroy() {
		getApi().getLogger().debug("Room plugin destoryed");
		this.cancelScheduledExectuion();
	}
	
	/**************************************************************************
	 * Request handling
	 */
	private void handleRequest(String userName, EsObjectExt esObj) {
		this.sendPublicMessageToRoomFromPlugin(userName, esObj);
		Action action = Action.getEnumById(esObj.getInteger(PluginConstant.kAction, -1));
		
		switch (action) {
			case ActionPickOneHero:
				this.sendPickOneHeroMessage(userName, esObj);
				break;
				
			case ActionChangeRandomHeros:
				GamePlugin plugin = (GamePlugin) getApi().getRoomPlugin(zoneId, roomId, PluginConstant.PLUGIN_GAME);
				plugin.getGame().dealCandidateHeroToPlayer(plugin.getGame().getPlayerByName(userName));
				break;
				
			default:
				break;
		}
	}
	
	/**************************************************************************
	 * Message sending
	 */	
	private void sendPluginMessageToUser(String userName, EsObjectExt esObj) {		
		this.getApi().sendPluginMessageToUser(userName, esObj);
		getApi().getLogger().debug("Send PLUGIN message to user [{}] with {}", userName, esObj);
	}
	
	private void sendPublicMessageToRoomFromPlugin(String userName, EsObjectExt esObj) {
		this.getApi().sendPublicMessageToRoomFromPlugin(
				userName,
				getApi().getZoneId(),
				getApi().getRoomId(),
				"",
				esObj,
				false,
				false);
		
		getApi().getLogger().debug("Send PUBLIC message to room by user [{}] with {} ", userName, esObj);
	}
	
	private void sendPickOneHeroMessage(String userName, EsObjectExt esObj) {
		int heroId = esObj.getInteger(VarConstant.kParamPickedHeroId);
		boolean isPicked = getPickedHeroIds().contains(heroId);
		if (!isPicked) this.updatePickedHeroIds(heroId);
		
		esObj.setBoolean(PluginConstant.kParamHeroIsPicked, isPicked);
		this.sendPluginMessageToUser(userName, esObj);
	}
	
	private Deque<Integer> getPickedHeroIds() {		
		ReadOnlyRoomVariable roomVal = getApi().getRoomVariable(zoneId, roomId, VarConstant.kVarPickedHero);
		int[] heroIdArray = roomVal.getValue().getIntegerArray(VarConstant.kParamPickedHeroIds);
		return EsObjectExt.intDequeMapping(heroIdArray);
	}
	
	private void updatePickedHeroIds(int heroId) {
		Deque<Integer> pickedHeroIds = getPickedHeroIds();
		if (pickedHeroIds.contains(heroId)) {
			pickedHeroIds.remove(heroId);
		} else {
			pickedHeroIds.add(heroId);
		}
		
		ReadOnlyRoomVariable roomVal = getApi().getRoomVariable(zoneId, roomId, VarConstant.kVarPickedHero);
		EsObjectExt value = new EsObjectExt();
		value.setIntegerArray(VarConstant.kParamPickedHeroIds, pickedHeroIds);
		getApi().updateRoomVariable(zoneId, roomId, roomVal.getName(), true, value, false, false);
	}
	
}
