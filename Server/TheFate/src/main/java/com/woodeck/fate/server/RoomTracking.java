/**
 * 
 */
package com.woodeck.fate.server;

import java.util.ArrayDeque;

import com.electrotank.electroserver5.extensions.BaseRoomTrackingEventHandler;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.RoomValue;
import com.woodeck.fate.util.PluginConstant;
import com.woodeck.fate.util.VarConstant;

/**
 * @author Killua
 *
 */
public class RoomTracking extends BaseRoomTrackingEventHandler {
	
	@Override
	public void init(EsObjectRO parameters) {
        getApi().getLogger().debug("RoomTracking Init");
	}
	
	@Override
	public void roomCreated(RoomValue room) {
		getApi().getLogger().debug("Room created");
		
		ZonePlugin zonePlugin = (ZonePlugin) getApi().getServerPlugin(PluginConstant.PLUGIN_ZONE);
		String roomNumber = zonePlugin.fetchRoomNumber();
		ZonePlugin.rooms.put(roomNumber, room);
		
		RoomPlugin roomPlugin = (RoomPlugin) getApi().getRoomPlugin(room.getZoneId(), room.getRoomId(), PluginConstant.PLUGIN_ROOM);
		EsObject gameDetail = roomPlugin.getApi().getGameDetails();
		gameDetail.setString(VarConstant.kParamRoomNumber, roomNumber);
		gameDetail.setString(VarConstant.kParamRoomPassword, room.getPassword());
		getApi().createRoomVariable(room.getZoneId(), room.getRoomId(), VarConstant.kVarRoomSetting, gameDetail, false);
		
		EsObjectExt esObj = new EsObjectExt();
		esObj.setIntegerArray(VarConstant.kParamPickedHeroIds, new ArrayDeque<Integer>());
		getApi().createRoomVariable(room.getZoneId(), room.getRoomId(), VarConstant.kVarPickedHero, esObj, false);
	}
	
	@Override
	public void roomDestroyed(RoomValue room) {
		getApi().getLogger().debug("Room destoryed");		
		ZonePlugin.roomNumbers.add(room.getRoomName());		// Recycle room number
		ZonePlugin.rooms.remove(room.getRoomName());
	}
	
}
