package com.woodeck.fate.mapper;

import com.woodeck.fate.model.User;

public interface UserMapper {
	
	int deleteByPrimaryKey(Integer userId);

	int insert(User record);

	int insertSelective(User record);

	int updateByPrimaryKeySelective(User record);

	int updateByPrimaryKeyWithBLOBs(User record);

	int updateByPrimaryKey(User record);

	User selectByUserName(String userName);
	
	int selectUserCount();
	
}