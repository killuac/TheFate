package com.woodeck.fate.model;

public class UserHero {
	private Integer userId;

    private Integer heroId;

    private Boolean isPermanent = false;	// 英雄是购买的(永久的)，还是VIP会员有效期内可以免费使用的

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getHeroId() {
        return heroId;
    }

    public void setHeroId(Integer heroId) {
        this.heroId = heroId;
    }

    public Boolean isPermanent() {
        return isPermanent;
    }

    public void setIsPermanent(Boolean isPermanent) {
        this.isPermanent = isPermanent;
    }
}