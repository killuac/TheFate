package com.woodeck.fate.model;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.List;

import com.woodeck.fate.card.HeroCard;
import com.woodeck.fate.card.RoleCard;
import com.woodeck.fate.server.EsObjectExt;
import com.woodeck.fate.util.BeanHelper;
import com.woodeck.fate.util.Constant;
import com.woodeck.fate.util.VarConstant;

public class User {
	
	private boolean isNPC = false;
	public void setIsNPC(boolean isNPC) {
		this.isNPC = isNPC;
	}
	
    private Integer userId;

    private String userName = "";

    private String password = "";

    private String nickName = "";

    private Byte gender = 0;

    private String email = "";

    private Boolean isVip = false;	// Object need init
    
    private Byte vipType = -1;
    
    private float discount = 0.0f;

    private float addEpRate = 0.0f;

    private Date vipValidTime = new Date();

    private Integer goldCoin = 0;

    private Byte level = 1;

    private Integer expPoint = 0;

    private Byte candidateHeroCount = 3;
    
    private Byte addCandidateCount = 0;
    
    private Integer sentinelVictoryCount = 0;

    private Integer scourgeVictoryCount = 0;

    private Integer neutralVictoryCount = 0;

    private Integer sentinelFailureCount = 0;

    private Integer scourgeFailureCount = 0;

    private Integer neutralFailureCount = 0;

    private Integer escapeCount = 0;

    private Integer killEnemyCount = 0;

    private Integer doublekillCount = 0;

    private Integer triplekillCount = 0;

    private Date createTime = new Date();

    private byte[] avatar = new byte[]{};

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return (null != nickName) ? nickName : userName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isVip() {
    	return (isVip && (null != vipValidTime) && vipValidTime.compareTo(new Date()) >= 0);
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }
    
    public Byte getVipType() {
    	return vipType;
    }
    
    public void setVipType(Byte vipType) {
    	this.vipType = (byte) Math.max(this.vipType, vipType);
    }

    public float getDiscount() {
        return (this.isVip()) ? discount : 0.0f;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getAddEpRate() {
        return (this.isVip()) ? addEpRate : 0.0f;
    }

    public void setAddEpRate(float addEpRate) {
        this.addEpRate = addEpRate;
    }

    public Date getVipValidTime() {
        return vipValidTime;
    }

    public void setVipValidTime(Date vipValidTime) {
        this.vipValidTime = vipValidTime;
    }

    public Integer getGoldCoin() {
        return goldCoin;
    }

    public void setGoldCoin(Integer goldCoin) {
        this.goldCoin = goldCoin;
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
    	this.level = (byte) Math.min(Math.max(level, 1), Constant.MAX_PLAYER_LEVEL);
    }

    public Integer getExpPoint() {
        return expPoint;
    }

    public void setExpPoint(Integer expPoint) {
    	int maxExp = BeanHelper.getLevelExpBean().getExperienceByLevel(Constant.MAX_PLAYER_LEVEL);
        this.expPoint = Math.min(Math.max(expPoint, 0), maxExp);
    }

    public Byte getCandidateHeroCount() {
        return candidateHeroCount;
    }
    
    public void setCandidateHeroCount(Byte candidateHeroCount) {
        this.candidateHeroCount = (byte) Math.min(candidateHeroCount, Constant.MAX_CANDIDATE_HERO_COUNT);
    }
    
    public Byte getAddCandidateCount() {
    	return (this.isVip()) ? addCandidateCount : 0;
    }
    
    public void setAddCandidateCount(Byte addCandidateCount) {
    	this.addCandidateCount = addCandidateCount;
    }

    public Integer getSentinelVictoryCount() {
        return sentinelVictoryCount;
    }

    public void setSentinelVictoryCount(Integer sentinelVictoryCount) {
        this.sentinelVictoryCount = sentinelVictoryCount;
    }

    public Integer getScourgeVictoryCount() {
        return scourgeVictoryCount;
    }

    public void setScourgeVictoryCount(Integer scourgeVictoryCount) {
        this.scourgeVictoryCount = scourgeVictoryCount;
    }

    public Integer getNeutralVictoryCount() {
        return neutralVictoryCount;
    }

    public void setNeutralVictoryCount(Integer neutralVictoryCount) {
        this.neutralVictoryCount = neutralVictoryCount;
    }

    public Integer getSentinelFailureCount() {
        return sentinelFailureCount;
    }

    public void setSentinelFailureCount(Integer sentinelFailureCount) {
        this.sentinelFailureCount = sentinelFailureCount;
    }

    public Integer getScourgeFailureCount() {
        return scourgeFailureCount;
    }

    public void setScourgeFailureCount(Integer scourgeFailureCount) {
        this.scourgeFailureCount = scourgeFailureCount;
    }

    public Integer getNeutralFailureCount() {
        return neutralFailureCount;
    }

    public void setNeutralFailureCount(Integer neutralFailureCount) {
        this.neutralFailureCount = neutralFailureCount;
    }

    public Integer getEscapeCount() {
        return escapeCount;
    }

    public void setEscapeCount(Integer escapeCount) {
        this.escapeCount = escapeCount;
    }

    public Integer getKillEnemyCount() {
        return killEnemyCount;
    }

    public void setKillEnemyCount(Integer killEnemyCount) {
        this.killEnemyCount = killEnemyCount;
    }

    public Integer getDoublekillCount() {
        return doublekillCount;
    }

    public void setDoublekillCount(Integer doublekillCount) {
        this.doublekillCount = doublekillCount;
    }

    public Integer getTriplekillCount() {
        return triplekillCount;
    }

    public void setTriplekillCount(Integer triplekillCount) {
        this.triplekillCount = triplekillCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
    
	/**
	 * 	Below methods for updating user information easily
	 */
    public void win(RoleCard card) {
    	if (card.isSentinel()) {
    		this.sentinelVictoryCount++;
    	} else if (card.isScourge()) {
    		this.scourgeVictoryCount++;
    	} else {
    		this.neutralVictoryCount++;
    	}
    }
    
    public void fail(RoleCard card) {
    	if (card.isSentinel()) {
    		this.sentinelFailureCount++;
    	} else if (card.isScourge()) {
    		this.scourgeFailureCount++;
    	} else {
    		this.neutralFailureCount++;
    	}
    }
    
    public void escaped() {
    	this.escapeCount++;
    }
    
    public void addKillEnemyCount(int count) {
    	this.killEnemyCount += count;
    }
    public void addDoublekillCount(int count) {
    	this.doublekillCount += count;
    }
    public void addTriplekillCount(int count) {
    	this.triplekillCount += count;
    }
    
    public int getAllCandidateHeroCount() {
    	return this.getCandidateHeroCount() + this.getAddCandidateCount();
    }
    
    private int getLevelUpExp() {
    	return BeanHelper.getLevelExpBean().getExperienceByLevel((byte) (level+1));
    }
    
    public EsObjectExt mapToEsObject() {
    	EsObjectExt esObj = new EsObjectExt();
    	esObj.setString(VarConstant.kParamUserName, userName);
    	esObj.setString(VarConstant.kParamNickName, this.getNickName());
//		esObj.setByteArray(VarConstant.kParamUserAvatar, avatar);
		esObj.setInteger(VarConstant.kParamUserLevel, level);
		esObj.setBoolean(VarConstant.kParamIsVIP, this.isVip());
		esObj.setFloat(VarConstant.kParamDiscount, this.getDiscount());
		esObj.setFloat(VarConstant.kParamAddEpRate, this.getAddEpRate());
		esObj.setDate(VarConstant.kParamVIPValidTime, vipValidTime);
		esObj.setInteger(VarConstant.kParamGoldCoin, goldCoin);
		esObj.setInteger(VarConstant.kParamExpPoint, expPoint);
		esObj.setInteger(VarConstant.kParamLevelUpExp, this.getLevelUpExp());
		esObj.setInteger(VarConstant.kParamSumKillEnemyCount, killEnemyCount);
		esObj.setInteger(VarConstant.kParamSumDoubleKillCount, doublekillCount);
		esObj.setInteger(VarConstant.kParamSumTripleKillCount, triplekillCount);
		
		int victoryCount = sentinelVictoryCount + scourgeVictoryCount + neutralVictoryCount;
		esObj.setInteger(VarConstant.kParamVictoryCount, victoryCount);
		int failureCount = sentinelFailureCount + scourgeFailureCount + neutralFailureCount;
		esObj.setInteger(VarConstant.kParamFailureCount, failureCount);
		esObj.setInteger(VarConstant.kParamEscapeCount, escapeCount);
		
		double totalCount = victoryCount + failureCount + escapeCount;
		double roleTotalCount = sentinelVictoryCount + sentinelFailureCount + escapeCount;
		double rate = (roleTotalCount > 0) ? sentinelVictoryCount/roleTotalCount : 0;
		esObj.setFormatDouble(VarConstant.kParamSentinelWinRate, rate*100);
		
		roleTotalCount = scourgeVictoryCount + scourgeFailureCount + escapeCount;
		rate = (roleTotalCount > 0) ? scourgeVictoryCount/roleTotalCount : 0;
		esObj.setFormatDouble(VarConstant.kParamScourgeWinRate, rate*100);
		
		roleTotalCount = neutralVictoryCount + neutralFailureCount + escapeCount;
		rate = (roleTotalCount > 0) ? neutralVictoryCount/roleTotalCount : 0;
		esObj.setFormatDouble(VarConstant.kParamNeutralWinRate, rate*100);
		
		rate = (totalCount > 0) ? victoryCount/totalCount : 0;
		esObj.setFormatDouble(VarConstant.kParamWinRate, rate*100);
		
		rate = (totalCount > 0) ? escapeCount/totalCount : 0;
		esObj.setFormatDouble(VarConstant.kParamEscapeRate, rate*100);
		
		esObj.setIntegerArray(VarConstant.kParamOwnHeroIds, this.getOwnHeroIds());
		
		esObj.setBoolean(VarConstant.kParamIsNPC, isNPC);
		
		return esObj;
    }
    
    public Deque<Integer> getOwnHeroIds() {
    	List<UserHero> ownHeros = BeanHelper.getUserHeroBean().getUserHerosByUserId(userId);
		Deque<Integer> ownHeroIds = new ArrayDeque<Integer>(ownHeros.size());
		for (UserHero userHero : ownHeros) {
			if (userHero.isPermanent() || this.isVip()) {
				ownHeroIds.add(userHero.getHeroId());
			} else {
				BeanHelper.getUserHeroBean().deleteUserHero(userId, userHero.getHeroId());	// 免费使用过期
			}
		}
		
		return ownHeroIds;
    }
    
    public Deque<Integer> getAvailableHeroIds() {
    	Deque<Integer> availableHeroIds = new ArrayDeque<Integer>();
		for (int heroId = 1; heroId < HeroCard.cardCount(); heroId++) {
			HeroCard heroCard = new HeroCard(heroId);
			if (heroCard.isFree()) availableHeroIds.add(heroId);
		}
		
		availableHeroIds.removeAll(Arrays.asList(9, 23, 24, 30));	// TODO
		availableHeroIds.addAll(this.getOwnHeroIds());
		return availableHeroIds;
    }
    
}