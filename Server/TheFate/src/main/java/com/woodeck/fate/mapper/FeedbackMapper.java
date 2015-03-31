package com.woodeck.fate.mapper;

import com.woodeck.fate.model.Feedback;

public interface FeedbackMapper {
    int deleteByPrimaryKey(Integer feedbackId);

	int insert(Feedback record);

	int insertSelective(Feedback record);

	Feedback selectByUserId(Integer userId);

	int updateByPrimaryKeySelective(Feedback record);

	int updateByPrimaryKeyWithBLOBs(Feedback record);

	int updateByPrimaryKey(Feedback record);
}