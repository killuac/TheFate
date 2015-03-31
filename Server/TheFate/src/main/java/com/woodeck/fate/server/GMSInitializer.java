/**
 * 
 */
package com.woodeck.fate.server;

import com.electrotank.electroserver5.extensions.BaseExtensionLifecycleEventHandler;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.ExtensionComponentConfiguration;
import com.electrotank.electroserver5.extensions.api.value.GameConfiguration;
import com.electrotank.electroserver5.extensions.api.value.RoomConfiguration;
import com.woodeck.fate.util.Constant;
import com.woodeck.fate.util.PluginConstant;
import com.woodeck.fate.util.VarConstant;

/**
 * @author Killua
 *
 */
public class GMSInitializer extends BaseExtensionLifecycleEventHandler {

	@Override
	public void init(EsObjectRO parameters) {
		getApi().getLogger().debug("GMSInitializer Init");
		this.initOneGame(PluginConstant.GAMETYPE_NEWBIE, 2);
		this.initOneGame(PluginConstant.GAMETYPE_VERSUS, 4);
		this.initOneGame(PluginConstant.GAMETYPE_JUNIOR_SIX, 6);
		this.initOneGame(PluginConstant.GAMETYPE_SENIOR_SIX, 6);
		this.initOneGame(PluginConstant.GAMETYPE_JUNIOR_EIGHT, 8);
		this.initOneGame(PluginConstant.GAMETYPE_SENIOR_EIGHT, 8);
	}
	
	private void initOneGame(String gameType, int capacity) {
		ExtensionComponentConfiguration roomPlugin = new ExtensionComponentConfiguration();
		roomPlugin.setExtensionName(getApi().getExtensionName());
		roomPlugin.setHandle(PluginConstant.PLUGIN_ROOM);
		roomPlugin.setName(roomPlugin.getHandle());
		
		ExtensionComponentConfiguration gamePlugin = new ExtensionComponentConfiguration();
		gamePlugin.setExtensionName(getApi().getExtensionName());
		gamePlugin.setHandle(PluginConstant.PLUGIN_GAME);
		gamePlugin.setName(gamePlugin.getHandle());
		
		RoomConfiguration roomConfig = new RoomConfiguration();
		roomConfig.setCapacity(capacity);
		roomConfig.setNonOperatorUpdateAllowed(true);
		roomConfig.setNonOperatorVariableUpdateAllowed(true);
		roomConfig.addPlugin(roomPlugin);
		roomConfig.addPlugin(gamePlugin);
		
		GameConfiguration gameConfig = new GameConfiguration();
		gameConfig.setReceivingRoomListUpdates(true);
        gameConfig.setReceivingRoomVariableUpdates(true);
        gameConfig.setReceivingUserListUpdates(true);
        gameConfig.setReceivingUserVariableUpdates(true);
        gameConfig.setReceivingVideoEvents(false);
        gameConfig.setRoomConfiguration(roomConfig);
        
        EsObject esObj = new EsObject();
        int playTime = Constant.DEFAULT_PLAY_TIME;
        if (PluginConstant.GAMETYPE_NEWBIE == gameType ||
        	PluginConstant.GAMETYPE_JUNIOR_SIX == gameType ||
        	PluginConstant.GAMETYPE_JUNIOR_EIGHT == gameType) {
        	playTime += 5;
        }
        esObj.setInteger(VarConstant.kParamPlayTime, playTime);
		esObj.setBoolean(VarConstant.kParamIsNoChatting, false);
		esObj.setBoolean(PluginConstant.kParamIsRoleMode, isRoleModeForGameType(gameType));
        gameConfig.setInitialGameDetails(esObj);
        
        getApi().registerGameConfiguration(gameType, gameConfig);
	}
	
	private boolean isRoleModeForGameType(String gameType) {
		return (PluginConstant.GAMETYPE_NEWBIE != gameType &&
				PluginConstant.GAMETYPE_VERSUS != gameType);
	}
	
}
