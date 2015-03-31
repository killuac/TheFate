/**
 * 
 */
package com.woodeck.fate.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.electrotank.electroserver5.extensions.BasePlugin;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.RoomValue;
import com.electrotank.electroserver5.extensions.api.value.UserValue;
import com.woodeck.fate.util.BeanHelper;
import com.woodeck.fate.util.PluginConstant;
import com.woodeck.fate.util.PluginConstant.RoomFindResult;
import com.woodeck.fate.util.VarConstant;
import com.woodeck.fate.util.PluginConstant.Action;

/**
 * @author Killua
 *
 */
public class ZonePlugin extends BasePlugin {
	
	public static final Deque<String> roomNumbers;
	static {
		List<String> idList = new ArrayList<String>();
		for (Integer i = 1000; i < 9999; i++) {
			idList.add(i.toString());
		}
		Collections.shuffle(idList);
		roomNumbers = new ArrayDeque<String>(idList);
	}
	public static final AbstractMap<String, RoomValue> rooms = new ConcurrentHashMap<String, RoomValue>();
	
		
	public String fetchRoomNumber() {
		return roomNumbers.pop();
	}
	
	@Override
	public void init(EsObjectRO parameters) {
		getApi().getLogger().debug("ZonePlugin Init");
	}
	
	@Override
	public void request(String userName, EsObjectRO requestParameters) {
		EsObjectExt esObj = new EsObjectExt(requestParameters);
		getApi().getLogger().debug("Receive ZONE plugin request from user [{}] with: {}", userName, esObj);
		
		try {
			this.handleRequest(userName, esObj);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			getApi().getLogger().error(sw.toString());
		}
	}
	
	/**************************************************************************
	 * Request handling
	 */
	private void handleRequest(String userName, EsObjectExt esObj) {
		Action action = Action.getEnumById(esObj.getInteger(PluginConstant.kAction, -1));
		
		switch (action) {				
			case ActionRejoinRoom: {
				this.sendRejoinRoomMessage(userName, esObj);
				break;
			}
			
			case ActionFindRoom: {
				this.sendFindRoomMessage(userName, esObj);
				break;
			}
				
			case ActionFeedback:
				String title = esObj.getString(PluginConstant.kParamIssueTitle);
				String content = esObj.getString(PluginConstant.kParamIssueContent);
				BeanHelper.getFeedbackBean().addIssue(userName, title, content);
				this.sendFeedbackMessage(userName);
				break;
				
			default:
				break;
		}
	}
	
	private boolean isAvaiableForRoom(RoomValue roomVal) {
		if (null == roomVal) return false;
		Collection<UserValue> users = getApi().getUsersInRoom(roomVal.getZoneId(), roomVal.getRoomId());
		return (users.size() > 0 && users.size() < roomVal.getCapacity());
	}
	
//	private boolean hasAvailableRoomInGameType(String gameType) {
//		GameRO[] gameList = getApi().findGames(gameType, false, null);
//		for (int i = 0; i < gameList.length; i++) {
////			if (gameList[i].isPasswordProtected()) continue;	// Doesn't work for updating password
//			int zoneId = gameList[i].getZoneId();
//			int roomId = gameList[i].getRoomId();
//			RoomValue roomValue = getApi().getRoom(zoneId, roomId);
//			
//			String password = roomValue.getPassword();
//			if (null != password && password.length() > 0) continue;
//			
//			if (this.isAvaiableForRoom(roomValue)) return true;
//		}
//		return false;
//	}
	
	/**************************************************************************
	 * Message sending
	 */
	private void sendPluginMessageToUser(String userName, EsObjectExt esObj) {		
		this.getApi().sendPluginMessageToUser(userName, esObj);
		getApi().getLogger().debug("Send PLUGIN message to user [{}] with {}", userName, esObj);
	}
	
	private void sendRejoinRoomMessage(String userName, EsObjectExt esObj) {
		int zoneId = esObj.getInteger(PluginConstant.kParamZoneId);
		int roomId = esObj.getInteger(PluginConstant.kParamRoomId);
		String password = esObj.getString(VarConstant.kParamRoomPassword, "");
		
		RoomFindResult findResult;
		RoomValue roomValue = getApi().getRoom(zoneId, roomId);
		if (this.isAvaiableForRoom(roomValue) && password.equals(roomValue.getPassword())) {
			findResult = RoomFindResult.RoomFindResultFound;
		} else {
			findResult = RoomFindResult.RoomFindResultNotFound;	// Maybe room capacity is full when rejoin
		}
		esObj.setRoomFindResult(PluginConstant.kRoomFindResult, findResult);
		
		this.sendPluginMessageToUser(userName, esObj);
	}
	
	private void sendFindRoomMessage(String userName, EsObjectExt esObj) {
		RoomFindResult findResult;
		RoomValue room = rooms.get(esObj.getString(VarConstant.kParamRoomNumber));
		if (null != room) {
//			The rooms in the buffer won't be updated after client update some room variables
//			So need get newest room value by zone id and room id
			RoomValue roomValue = getApi().getRoom(room.getZoneId(), room.getRoomId());
			String password = esObj.getString(VarConstant.kParamRoomPassword, "");
			if (password.equals(roomValue.getPassword())) {
				findResult = RoomFindResult.RoomFindResultFound;
			} else {
				findResult = RoomFindResult.RoomFindResultWrongPassword;
			}
			esObj.setInteger(PluginConstant.kParamZoneId, room.getZoneId());
			esObj.setInteger(PluginConstant.kParamRoomId, room.getRoomId());
		} else {
			findResult = RoomFindResult.RoomFindResultNotFound;
		}
		esObj.setRoomFindResult(PluginConstant.kRoomFindResult, findResult);
		
		this.sendPluginMessageToUser(userName, esObj);
	}
	
	private void sendFeedbackMessage(String userName) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionFeedback);
		this.sendPluginMessageToUser(userName, esObj);
	}
	
}
