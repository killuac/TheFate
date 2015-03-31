package com.woodeck.fate.mapper;

import com.woodeck.fate.model.LevelExp;

public interface LevelExpMapper {
    int deleteByPrimaryKey(Byte level);

    int insert(LevelExp record);

    int insertSelective(LevelExp record);

    LevelExp selectByPrimaryKey(Byte level);
    
    int updateByPrimaryKeySelective(LevelExp record);

    int updateByPrimaryKey(LevelExp record);
}