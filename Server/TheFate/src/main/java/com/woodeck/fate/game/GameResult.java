/**
 * 
 */
package com.woodeck.fate.game;

/**
 * @author Killua
 *
 */
public class GameResult {
	
	private String userName;
	private String nickName;
	private int heroId;
	private int roleId;
	private boolean isAlive;
	private boolean isEscaped;
	private int killEnemyCount;
	private int doubleKillCount;
	private int tripleKillCount;
	private int gotExpPoint;
	private int addExpPoint;
	private int rewardGoldCoin;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public boolean isEscaped() {
		return isEscaped;
	}
	public void setEscaped(boolean isEscaped) {
		this.isEscaped = isEscaped;
	}
	public int getKillEnemyCount() {
		return killEnemyCount;
	}
	public void setKillEnemyCount(int killEnemyCount) {
		this.killEnemyCount = killEnemyCount;
	}
	public int getDoubleKillCount() {
		return doubleKillCount;
	}
	public void setDoubleKillCount(int doubleKillCount) {
		this.doubleKillCount = doubleKillCount;
	}
	public int getTripleKillCount() {
		return tripleKillCount;
	}
	public void setTripleKillCount(int tripleKillCount) {
		this.tripleKillCount = tripleKillCount;
	}
	public int getGotExpPoint() {
		return gotExpPoint;
	}
	public void setGotExpPoint(int gotExpPoint) {
		this.gotExpPoint = gotExpPoint;
	}
	public int getAddExpPoint() {
		return addExpPoint;
	}
	public void setAddExpPoint(int addExpPoint) {
		this.addExpPoint = addExpPoint;
	}
	public int getRewardGoldCoin() {
		return rewardGoldCoin;
	}
	public void setRewardGoldCoin(int rewardGoldCoin) {
		this.rewardGoldCoin = rewardGoldCoin;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GameResult [userName=");
		builder.append(userName);
		builder.append(", nickName=");
		builder.append(nickName);
		builder.append(", heroId=");
		builder.append(heroId);
		builder.append(", roleId=");
		builder.append(roleId);
		builder.append(", isAlive=");
		builder.append(isAlive);
		builder.append(", isEscaped=");
		builder.append(isEscaped);
		builder.append(", killEnemyCount=");
		builder.append(killEnemyCount);
		builder.append(", doubleKillCount=");
		builder.append(doubleKillCount);
		builder.append(", tripleKillCount=");
		builder.append(tripleKillCount);
		builder.append(", gotExpPoint=");
		builder.append(gotExpPoint);
		builder.append(", addExpPoint=");
		builder.append(addExpPoint);
		builder.append(", rewardGoldCoin=");
		builder.append(rewardGoldCoin);
		builder.append("]");
		return builder.toString();
	}
	
}
