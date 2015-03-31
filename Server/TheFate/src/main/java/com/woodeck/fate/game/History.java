/**
 * 
 */
package com.woodeck.fate.game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.plist.NSDictionary;
import net.sf.plist.NSObject;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.PropertyListParser;

import com.woodeck.fate.card.PlayingCard;
import com.woodeck.fate.player.Player;
import com.woodeck.fate.server.EsObjectExt;
import com.woodeck.fate.util.Constant;
import com.woodeck.fate.util.FileConstant;
import com.woodeck.fate.util.PluginConstant;
import com.woodeck.fate.util.PluginConstant.Action;

/**
 * @author Killua
 *
 */
public class History {
	
	private static NSDictionary actionHistory;
	static {
		try {
			actionHistory = (NSDictionary)PropertyListParser.parse(new File(FileConstant.PLIST_ACTION_HISTORY));
			
		} catch (PropertyListException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Player player;
	private Action action;
	private Date dateTime = new Date();
	private String text = "";
	
	public History(Player player, EsObjectExt esObj) {
		this.player = player;
		this.init(esObj);
	}
	
	private void init(EsObjectExt esObj) {
		this.action = player.getGame().getAction();
		NSObject textObj = actionHistory.get(Integer.toString(action.id));
		if (null == textObj) return;
		
		List<NSObject> textList;
		
		this.text = textObj.toString();
		List<String> parameters = new ArrayList<String>();
		int cardCount = esObj.getInteger(PluginConstant.kParamCardCount, 0);
		int[] cardIds = esObj.getIntegerArray(PluginConstant.kParamCardIdList, null);
		
		switch (action) {
			case ActionDrawCard:
				parameters.add(Integer.toString(cardCount));
				break;
				
			case ActionOkayToDiscard:
				if (null != cardIds && cardIds.length > 0) {
					parameters.add(Integer.toString(cardIds.length));
				} else {
					this.text = "";
				}
				break;
				
			case ActionUseHandCard:
			case ActionChoseCardToUse:
			case ActionChoseCardToDrop:
			case ActionChoseTargetPlayer:
			case ActionOkay:
				if (null != player.getHeroSkill()) {
					this.text = player.getHeroSkill().getHistoryText();
					if (null != cardIds && cardIds.length > 0) {
						parameters.add(Integer.toString(cardIds.length));	// 用几张牌来发动技能
					}
				} 
				if (null != player.getEquipmentCard()) {					// 先发动了主动技能，后使用散失之刃
					this.text = player.getEquipmentCard().getHistoryText();
					if (null != cardIds && cardIds.length > 0) {
						parameters.add(Integer.toString(cardIds.length));
					}
				}
				if (null == player.getHeroSkill() && null == player.getEquipmentCard()) {
					if (Action.ActionUseHandCard == action) {										// 正常使用卡牌
						if (player.getFirstUsedCard().isEquipment()) {
							Player tarPlayer = player.getTargetPlayer();
							String tarHeroName = (null != tarPlayer) ? tarPlayer.getHeroName() : Constant.HERO_NAME_SELF;
							parameters.add(tarHeroName);
						} else {
							this.text = player.getFirstUsedCard().getHistoryText();
							this.addTargetHeroNameToParameterList(parameters);	
						}
					}
					else if (Action.ActionOkay == action){
						textList = textObj.toList();
						this.text = (player.isStrengthening()) ? textList.get(0).toString() : "";	// 强化了该卡牌
					}
					else if (Action.ActionChoseCardToUse == action) {
						if (null != cardIds && cardIds.length > 0) {
							parameters.add(Integer.toString(cardIds.length));	// 用几张药膏救人
						}
					}
				}
				break;
				
			case ActionUseHeroSkill:	// 直接使用技能(不需要指定目标，也不需要卡牌)
				if (player.getHeroSkill().isSimple()) {
					this.text = player.getHeroSkill().getHistoryText();
				} else {
					this.text = "";
				}
				break;
				
			case ActionPlayerSkillOrEquipmentTriggered:		// 强制触发
				int skillId = esObj.getInteger(PluginConstant.kParamHeroSkillId);
				int equipId = esObj.getInteger(PluginConstant.kParamEquipmentId);
				if (skillId > 0) {
					this.text = player.getHeroSkillBySkillId(skillId).getHistoryText();
				} else if (equipId > 0) {
					this.text = player.getEquipmentCardByCardId(equipId).getHistoryText();
				}
				break;
				
			case ActionChoseCardToGet:
				textList = textObj.toList();
				if (null == cardIds) {
					this.text = textList.get(0).toString();		// 抽取手牌
				} else {
					this.text = textList.get(1).toString();		// 抽取装备
				}
				if (0 == player.getTargetPlayers().size()) {    // 被贪婪的玩家抽取TurnOwner的手牌
                    player.getTargetPlayerNames().add(player.getGame().getTurnOwner().getPlayerName());
                }
			case ActionChoseCardToGive:
			case ActionChoseCardToRemove:
				this.addTargetHeroNameToParameterList(parameters);
				cardCount = (cardCount > 0) ? cardCount : cardIds.length;
				parameters.add(Integer.toString(cardCount));
				break;
				
			case ActionChoseColor:
				parameters.add(Integer.toString(player.getSelectedColor()));
				break;
				
			case ActionChoseSuits:
				parameters.add(Integer.toString(player.getSelectedSuits()));
				break;
				
			case ActionTableShowAllComparedCards:
				this.addCardTextToParameterList(new int[] {player.getFirstUsedCardId()}, parameters);
				break;
				
			case ActionPlayerUpdateHero:
				textList = textObj.toList();
				int hp = esObj.getInteger(PluginConstant.kParamHeroHealthPoint);
				int sp = esObj.getInteger(PluginConstant.kParamHeroSkillPoint);
				int hpChanged = esObj.getInteger(PluginConstant.kParamHeroHpChanged);
				int spChanged = esObj.getInteger(PluginConstant.kParamHeroSpChanged);
				boolean isSunder = esObj.getBoolean(PluginConstant.kParamIsSunder);
				
				if (hpChanged == 0 && spChanged == 0) this.text = "";
				if (hpChanged < 0) this.text = (spChanged > 0) ? textList.get(0).toString() : textList.get(1).toString();
				if (hpChanged > 0) this.text = textList.get(2).toString();
				if (spChanged > 0 && hpChanged >= 0) this.text = textList.get(3).toString();
				if (spChanged < 0) this.text = (player.isTurnOwner()) ? textList.get(4).toString() : textList.get(5).toString();
				
				if (isSunder) {
                    this.text = textList.get(6).toString();
                    parameters.add(Integer.toString(hp));
                    parameters.add(Integer.toString(sp));
                }
				
				if (Math.abs(hpChanged) > 0) parameters.add(Integer.toString(Math.abs(hpChanged)));
                if (Math.abs(spChanged) > 0) parameters.add(Integer.toString(Math.abs(spChanged)));
				break;
				
			default:
				break;
		}
		
		this.addCardTextToParameterList(cardIds, parameters);
		this.addTargetHeroNameToParameterList(parameters);
		this.replaceCharacterWithParameters(parameters);
	}
	
	private void addCardTextToParameterList(int[]cardIds, List<String> parameters) {
		if (null != cardIds && cardIds.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < cardIds.length; i++) {
				PlayingCard card = new PlayingCard(cardIds[i]);
				sb.append (Constant.DELIMITER_SIGN);
				sb.append(card.getCardFullText());
			}
			parameters.add(sb.substring(1));	// 去掉第一个分隔符
		}
	}
	
	private void addTargetHeroNameToParameterList(List<String> parameters) {
		if (this.player.getTargetPlayerNames().size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (Player tarPlayer : player.getTargetPlayers()) {
				sb.append (Constant.DELIMITER_SIGN);
				sb.append(tarPlayer.getSelectedHeroName());
			}
			parameters.add(sb.substring(1));
		}
	}
	
	public Action getAction() {
		return action;
	}
	
	public Date getDateTime() {
		return dateTime;
	}
	
	public String getText() {
		return text.trim();
	}
	
	/**
	 * Replace Ampersand(&) with parameters
	 */
	public void replaceCharacterWithParameters(List<String> parameters) {
		for (String param : parameters) {
			int index = this.text.indexOf(Constant.REPLACE_SIGN);
			if (index > 0) {
				this.text = text.replaceFirst(Constant.REPLACE_SIGN, param);
			} else {
				break;
			}
		}
	}
	
}
