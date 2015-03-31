package com.woodeck.fate.mapper;

import java.util.List;

import com.woodeck.fate.model.UserHero;

import org.apache.ibatis.annotations.Param;

public interface UserHeroMapper {
	int deleteByPrimaryKey(@Param("userId") Integer userId, @Param("heroId") Integer heroId);

	int insert(UserHero record);

	int insertSelective(UserHero record);

	int updateByPrimaryKeySelective(UserHero record);

	int updateByPrimaryKey(UserHero record);

	List<UserHero> selectUserHerosByUserId(@Param("userId") Integer userId);
}