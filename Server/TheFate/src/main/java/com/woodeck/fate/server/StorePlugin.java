/**
 * 
 */
package com.woodeck.fate.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import net.sf.plist.NSDictionary;
import net.sf.plist.NSObject;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

import com.electrotank.electroserver5.extensions.BasePlugin;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woodeck.fate.card.HeroCard;
import com.woodeck.fate.model.User;
import com.woodeck.fate.util.BeanHelper;
import com.woodeck.fate.util.FileConstant;
import com.woodeck.fate.util.PluginConstant;
import com.woodeck.fate.util.VarConstant;
import com.woodeck.fate.util.PluginConstant.Action;

/**
 * @author Killua
 *
 */
public class StorePlugin extends BasePlugin {
	
	public static final String
		kGoldCoin			= "goldCoin",
		kPrice				= "price",
		kCoinCount			= "coinCount",
		kAddCoinCount		= "addCoinCount",
		
		kVIP				= "VIP",
		kDiscount			= "discount",
		kAddEpRate			= "addEpRate",
		kAddHeroCount		= "addHeroCount",
		kFreeUsedHeros		= "freeUsedHeros",
		
		kHeroSelection		= "heroSelection",
		kPickOneHero		= "pickOneHero",
		kChangeRandomHeros	= "changeRandomHeros",
		
		kAddCandidateHero	= "addCandidateHero",
		kAddCandidateCount	= "addCandidateCount";
		
	public static final String
		kGoldCoinIds		= "goldCoinIds",
		kVIPTypeIds			= "vipTypeIds",
		kBoughtProductId	= "productId",
		kReceiptString		= "receiptString",
		kBoughtVIPTypeId	= "vipTypeId",
		kBoughtHeroId		= "heroId";
	
	public static final String
		kProductIdTierOne	= "com.thefate.tierone",
		kProductIdTierTwo	= "com.thefate.tiertwo",
		kProductIdTierThree	= "com.thefate.tierthree",
		kProductIdTierFour	= "com.thefate.tierfour",
		kProductIdTierFive	= "com.thefate.tierfive";
	
	public static final int
		VIPTypeDaily 	= 0,
		VIPTypeMonthly 	= 1,
		VIPTypeYearly 	= 2;
	
	private static NSDictionary merchandises, goldCoinData, heroSelection;
	private static List<NSObject> vipData, addCandidateHero;
	static {
		try {
			merchandises = (NSDictionary)PropertyListParser.parse(new File(FileConstant.PLIST_MERCHANDISE));
			goldCoinData = (NSDictionary) merchandises.get(kGoldCoin);
			heroSelection = (NSDictionary) merchandises.get(kHeroSelection);
			vipData = merchandises.get(kVIP).toList();
			addCandidateHero = merchandises.get(kAddCandidateHero).toList();
			
		} catch (PropertyListException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init(EsObjectRO parameters) {
		getApi().getLogger().debug("StorePlugin Init");
	}
	
	@Override
	public void request(String userName, EsObjectRO requestParameters) {
		EsObjectExt esObj = new EsObjectExt(requestParameters);
		getApi().getLogger().debug("Receive STORE plugin request from user [{}] with: {}", userName, esObj);
		
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
		User user = BeanHelper.getUserBean().getUserByName(userName);
		int ownedCoinCount = user.getGoldCoin();
		int price = 0;
		String merchandiseId = "";
		
		switch (action) {
			case ActionShowMerchandises:
				this.sendShowMerchandisesMessage(userName);
				break;
				
			case ActionShowPickingHeros:
				this.sendShowPickingHerosMessage(userName);
				break;
				
			case ActionBuyGoldCoin:
				if (this.validateReceipt(esObj.getString(kReceiptString))) {
					String productId = esObj.getString(kBoughtProductId);
					merchandiseId = productId;
					NSDictionary goldCoin = (NSDictionary) goldCoinData.get(productId);
					int coinCount = goldCoin.get(kCoinCount).toInteger();
					int addCoinCount = (user.isVip()) ? goldCoin.get(kAddCoinCount).toInteger() : 0;
					user.setGoldCoin(ownedCoinCount + coinCount + addCoinCount);
				}
				esObj.removeVariable(kReceiptString);
				this.sendPluginMessageToUser(userName, esObj);
				break;
				
			case ActionBuyHeroCard:
				int heroId = esObj.getInteger(kBoughtHeroId);
				merchandiseId = kBoughtHeroId + heroId;
				price = new HeroCard(heroId).getPrice();
				user.setGoldCoin(ownedCoinCount - price);
				BeanHelper.getUserHeroBean().updateUserHero(user.getUserId(), heroId, true);
				this.sendPluginMessageToUser(userName, esObj);
				break;
			
			case ActionBuyVIPCard: {
				int calendarField = 0;
				int vipType = esObj.getInteger(kBoughtVIPTypeId);
				merchandiseId = kVIP + vipType;
				if (VIPTypeDaily == vipType) {
					calendarField = Calendar.DATE;
				} else if (VIPTypeMonthly == vipType) {
					calendarField = Calendar.MONTH;
				} else if (VIPTypeYearly == vipType) {
					calendarField = Calendar.YEAR;
				}
				
				NSDictionary vip = (NSDictionary) vipData.get(vipType);
				price = vip.get(kPrice).toInteger();
				user.setGoldCoin(ownedCoinCount - price);
				Calendar calendar = Calendar.getInstance();
				if (user.isVip()) {
					calendar.setTime(user.getVipValidTime());
				} else {
					calendar.setTime(new Date());
				}
				calendar.add(calendarField, 1);
				user.setVipValidTime(calendar.getTime());
				
				user.setIsVip(true);
				user.setVipType((byte) vipType);
				user.setDiscount(vip.get(kDiscount).toNumber().floatValue());
				user.setAddEpRate(vip.get(kAddEpRate).toNumber().floatValue());
				user.setAddCandidateCount((byte) vip.get(kAddHeroCount).toInteger());
				
//				Update the hero list owned(Bought/Free use) by user
				List<NSObject> freeHeros = vip.get(kFreeUsedHeros).toList();
				List<Integer> heroIds = new ArrayList<Integer>(freeHeros.size());
				for (NSObject hero : freeHeros) {
					heroIds.add(hero.toInteger());
				}
				BeanHelper.getUserHeroBean().updateUserHeros(user.getUserId(), heroIds);
				this.sendPluginMessageToUser(userName, esObj);
				break;
			}
				
			case ActionPickOneHero:
				merchandiseId = kPickOneHero;
				price = heroSelection.get(kPickOneHero).toInteger();
				user.setGoldCoin(ownedCoinCount - price);
				getApi().createOrUpdateUserVariable(userName, VarConstant.kVarPickedHero, esObj);
				this.sendPluginMessageToUser(userName, esObj);
				break;
				
			case ActionChangeRandomHeros:
				price = heroSelection.get(kChangeRandomHeros).toInteger();
				user.setGoldCoin(ownedCoinCount - price);
				this.sendPluginMessageToUser(userName, esObj);
				break;
				
			case ActionBuyAdditionalCandidates:
				int addCount = esObj.getInteger(kAddCandidateCount);
				price = addCandidateHero.get(addCount).toInteger();
				user.setGoldCoin(ownedCoinCount - price);
				byte candidateCount = (byte) (user.getCandidateHeroCount() + addCount);
				user.setCandidateHeroCount(candidateCount);
				this.sendPluginMessageToUser(userName, esObj);
				break;
				
			default:
				break;
		}
		
		getApi().createOrUpdateUserVariable(userName, VarConstant.kVarUserInfo, user.mapToEsObject());
		BeanHelper.getUserBean().updateUser(user);
		if (!merchandiseId.isEmpty()) BeanHelper.getBuyHisotoryBean().addHistory(user.getUserId(), merchandiseId);
	}
	
	/**
	 * Validate receipts with the App Store
	 */
	@SuppressWarnings("unchecked")
	private boolean validateReceipt(String receiptStr) {
		if (null == receiptStr) return false;
		
		try {
			String jsonStr = String.format("{\"receipt-data\":\"%s\"}", receiptStr);
			
//			URL url = new URL("https://sandbox.itunes.apple.com/verifyReceipt");
			URL url = new URL("https://buy.itunes.apple.com/verifyReceipt");
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.connect();
			
			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(jsonStr);
			writer.flush();
			writer.close();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			if (con.getResponseCode() == 200) {
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> jsonResponse = mapper.readValue(reader, Map.class);
				String statusStr = jsonResponse.get("status").toString();
				return (Integer.parseInt(statusStr) == 0);
			}
			
			reader.close();
			con.disconnect();
			
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			getApi().getLogger().error(sw.toString());
			return false;
		}
		
		return true;
	}
	
	/**************************************************************************
	 * Message sending
	 */
	private void sendPluginMessageToUser(String userName, EsObjectExt esObj) {		
		this.getApi().sendPluginMessageToUser(userName, esObj);
		getApi().getLogger().debug("Send PLUGIN message to user [{}] with {}", userName, esObj);
	}
	
	private void sendShowMerchandisesMessage(String userName) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionShowMerchandises);
		esObj.setStringArray(kGoldCoinIds, new String[] {
				kProductIdTierOne, kProductIdTierTwo, kProductIdTierThree, kProductIdTierFour, kProductIdTierFive});
		esObj.setIntegerArray(kVIPTypeIds, new int[] {VIPTypeDaily, VIPTypeMonthly, VIPTypeYearly});
		
		this.sendPluginMessageToUser(userName, esObj);
	}
	
	private void sendShowPickingHerosMessage(String userName) {
		EsObjectExt esObj = new EsObjectExt();
		esObj.setAction(PluginConstant.kAction, Action.ActionShowPickingHeros);
		User user = BeanHelper.getUserBean().getUserByName(userName);
		esObj.setIntegerArray(PluginConstant.kParamCardIdList, user.getAvailableHeroIds());
		
		this.sendPluginMessageToUser(userName, esObj);
	}
	
}
