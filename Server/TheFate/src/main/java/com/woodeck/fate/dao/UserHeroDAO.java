/**
 * 
 */
package com.woodeck.fate.dao;

import java.util.List;

import com.woodeck.fate.model.UserHero;

/**
 * @author Killua
 *
 */
public interface UserHeroDAO {
	
	List<UserHero> getUserHerosByUserId(Integer userId);
	
	int updateUserHero(Integer userId, Integer heroId, Boolean isPermanent);
	
	int updateUserHeros(Integer userId, List<Integer> heroIds);
	
	int deleteUserHero(Integer userId, Integer heroId);
	
	int deleteUserHeros(Integer userId, List<Integer> heroIds);
	
}
