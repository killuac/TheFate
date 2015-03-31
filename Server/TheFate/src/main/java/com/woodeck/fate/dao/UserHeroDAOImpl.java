/**
 * 
 */
package com.woodeck.fate.dao;

import java.util.List;

import com.woodeck.fate.mapper.UserHeroMapper;
import com.woodeck.fate.model.UserHero;

/**
 * @author Killua
 *
 */
public class UserHeroDAOImpl implements UserHeroDAO {

	private UserHeroMapper userHeroMapper;
	
	public void setUserHeroMapper(UserHeroMapper userMapper) {
		this.userHeroMapper = userMapper;
	}
	
	@Override
	public List<UserHero> getUserHerosByUserId(Integer userId) {
		return userHeroMapper.selectUserHerosByUserId(userId);
	}

	@Override
	public int updateUserHero(Integer userId, Integer heroId, Boolean isPermanent) {
		UserHero userHero = new UserHero();
		userHero.setUserId(userId);
		userHero.setHeroId(heroId);
		userHero.setIsPermanent(isPermanent);
		
		boolean isExisted = false;
		List<UserHero> userHeros = this.getUserHerosByUserId(userId);
		for (UserHero uh : userHeros) {
			if (uh.getHeroId() == heroId) {
				isExisted = true; break;
			}
		}
		
		if (isExisted) {
			return (isPermanent) ? userHeroMapper.updateByPrimaryKeySelective(userHero) : 0;
		} else {
			return userHeroMapper.insertSelective(userHero);
		}
	}

	@Override
	public int updateUserHeros(Integer userId, List<Integer> heroIds) {
		int rtnCode = 0;
		for (Integer heroId : heroIds) {
			rtnCode = this.updateUserHero(userId, heroId, false);
		}
		return rtnCode;
	}

	@Override
	public int deleteUserHero(Integer userId, Integer heroId) {
		return userHeroMapper.deleteByPrimaryKey(userId, heroId);
	}

	@Override
	public int deleteUserHeros(Integer userId, List<Integer> heroIds) {
		int rtnCode = 0;
		for (Integer heroId : heroIds) {
			rtnCode = this.deleteUserHero(userId, heroId);
		}
		return rtnCode;
	}

}
