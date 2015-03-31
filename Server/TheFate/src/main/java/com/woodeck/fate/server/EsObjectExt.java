/**
 * 
 */
package com.woodeck.fate.server;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.List;

import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.woodeck.fate.game.GameResult;
import com.woodeck.fate.game.History;
import com.woodeck.fate.game.Game.GameState;
import com.woodeck.fate.util.PluginConstant;
import com.woodeck.fate.util.PluginConstant.RoomFindResult;
import com.woodeck.fate.util.VarConstant;
import com.woodeck.fate.util.PluginConstant.Action;
import com.woodeck.fate.util.PluginConstant.UpdateReason;

/**
 * @author Killua
 *
 */
public class EsObjectExt extends EsObject {

	private static final long serialVersionUID = 1L;	// Generated automatically
	
	
	public EsObjectExt() {
		super();
	}
	
	public EsObjectExt(EsObjectRO esObj) {
		super();
		int actionId = esObj.getInteger(PluginConstant.kAction);
		this.addAll(esObj);
		this.setAction(PluginConstant.kAction, Action.getEnumById(actionId));
	}
	
	public void setAction(String name, Action value) {
		super.setInteger(name, value.id);
		if (Action.ActionNull != value) this.setString("actionText", value.text);
	}
	
	public void setRoomFindResult(String name, RoomFindResult value) {
		super.setInteger(name, value.ordinal());
	}
	
	public void setGameState(String name, GameState value) {
		super.setInteger(name, value.id);
	}
	
	public void setUpdateReason(String name, UpdateReason value) {
		super.setInteger(name, value.id);
	}
	
	public void setFormatDouble(String name, Double value) {
		double formatValue = Double.parseDouble(new DecimalFormat("#.#").format(value));
		super.setDouble(name, formatValue);
	}
	
	public void setDate(String name, Date value) {
		super.setString(name, DateFormat.getDateInstance().format(value));
	}
	
	/**
	 * Array data mapping
	 */
	public static Deque<Integer> intDequeMapping(int[] array) {
		Deque<Integer> deque = new ArrayDeque<Integer>(array.length);
		for (int i = 0; i < array.length; i++) {
			deque.add(array[i]);
		}
		return deque;
	}
	
	public static List<Integer> intListMapping(int[] array) {
		List<Integer> list = new ArrayList<Integer>(array.length);
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}
	
	public static Deque<String> stringDequeMapping(String[] array) {
		Deque<String> deque = new ArrayDeque<String>(array.length);
		for (int i = 0; i < array.length; i++) {
			deque.add(array[i]);
		}
		return deque;
	}
	
	public static List<String> stringListMapping(String[] array) {
		List<String> list = new ArrayList<String>(array.length);
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}
	
	public static int[] intArrayMapping(Deque<Integer> deque) {
		int[] array = new int[deque.size()];
//		NOTE: if use pollFirst(), the element will be removed from deque.
//		for (int i = 0; i < array.length; i++) {
//			array[i] = deque.pollFirst();
//		}
		int i = 0;
		for (Integer value : deque) {
			array[i] = value;
			i++;
		}
		return array;
	}
	
	public static int[] intArrayMapping(List<Integer> list) {
		int[] array = new int[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	
	public static String[] stringArrayMapping(Deque<String> deque) {
		String[] array = new String[deque.size()];
		int i = 0;
		for (String value : deque) {
			array[i] = value;
			i++;
		}
		return array;
	}
	
	public static String[] stringArrayMapping(List<String> list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	
	/**
	 * Array data setting
	 */
	public void setIntegerArray(String name, int value) {
		Deque<Integer> deque = new ArrayDeque<Integer>(Arrays.asList(value));
		super.setIntegerArray(name, intArrayMapping(deque));
	}
	
	public void setIntegerArray(String name, Deque<Integer> value) {
		super.setIntegerArray(name, intArrayMapping(value));
	}
	
	public void setIntegerArray(String name, List<Integer> value) {
		super.setIntegerArray(name, intArrayMapping(value));
	}
	
	public void setStringArray(String name, Deque<String> value) {
		super.setStringArray(name, stringArrayMapping(value));
	}
	
	public void setStringArray(String name, List<String> value) {
		super.setStringArray(name, stringArrayMapping(value));
	}
	
	public void setStringArray(String name, String value) {
		String[] array = {value};
		super.setStringArray(name, array);
	}
	
	/**
	 * Set ExObject with game history
	 */
	public void setGameHistory(AbstractMap<String, List<History>> histories) {
		for (String heroName : histories.keySet()) {
			EsObject[] esObjArray = new EsObject[histories.size()];
			
			for (int i = 0; i < histories.size(); i++) {
				EsObjectExt esObj = new EsObjectExt();
				esObj.setAction(PluginConstant.kAction, histories.get(heroName).get(i).getAction());
				esObj.setDate(PluginConstant.kParamHistoryDate, histories.get(heroName).get(i).getDateTime());
				esObj.setString(PluginConstant.kParamHistoryText, histories.get(heroName).get(i).getText());
				esObjArray[i] = esObj;
			}
			
			this.setEsObjectArray(heroName, esObjArray);
		}
	}
	
	/**
	 * Set ExObject with game result
	 */
	public void setGameResult(String name, List<GameResult> gameResults) {
		EsObject[] esObjArray = new EsObject[gameResults.size()];
		
		for (int i = 0; i < gameResults.size(); i++) {
			GameResult gameResult = gameResults.get(i);
			EsObject esObj = new EsObject();
			esObj.setString(VarConstant.kParamUserName, gameResult.getUserName());
			esObj.setString(VarConstant.kParamNickName, gameResult.getNickName());
			esObj.setInteger(PluginConstant.kParamSelectedHeroId, gameResult.getHeroId());
			esObj.setInteger(PluginConstant.kParamCardId, gameResult.getRoleId());
			esObj.setBoolean(PluginConstant.kParamIsAlive, gameResult.isAlive());
			esObj.setBoolean(PluginConstant.kParamIsEscaped, gameResult.isEscaped());
			esObj.setInteger(PluginConstant.kParamKillEnemyCount, gameResult.getKillEnemyCount());
			esObj.setInteger(PluginConstant.kParamDoubleKillCount, gameResult.getDoubleKillCount());
			esObj.setInteger(PluginConstant.kParamTripleKillCount, gameResult.getTripleKillCount());
			esObj.setInteger(PluginConstant.kParamRewardGoldCoin, gameResult.getRewardGoldCoin());
			esObj.setInteger(PluginConstant.kParamGotExpPoint, gameResult.getGotExpPoint());
			esObj.setInteger(PluginConstant.kParamAddExpPoint, gameResult.getAddExpPoint());
			
			esObjArray[i] = esObj;
		}
		
		this.setEsObjectArray(name, esObjArray);
	}
	
}
