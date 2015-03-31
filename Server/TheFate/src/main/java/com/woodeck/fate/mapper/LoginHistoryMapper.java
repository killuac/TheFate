package com.woodeck.fate.mapper;

import com.woodeck.fate.model.LoginHistory;

public interface LoginHistoryMapper {
    int deleteByPrimaryKey(Integer loginId);

    int insert(LoginHistory record);

    int insertSelective(LoginHistory record);

    LoginHistory selectByUserId(Integer userId);	// Select one latest history

    int updateByPrimaryKeySelective(LoginHistory record);

    int updateByPrimaryKey(LoginHistory record);
}