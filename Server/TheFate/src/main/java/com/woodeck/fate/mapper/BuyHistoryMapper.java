package com.woodeck.fate.mapper;

import java.util.List;

import com.woodeck.fate.model.BuyHistory;

public interface BuyHistoryMapper {
    int deleteByPrimaryKey(Integer buyId);

    int insert(BuyHistory record);

    int insertSelective(BuyHistory record);

    List<BuyHistory> selectByMerchandiseId(String merchandiseId);

    int updateByPrimaryKeySelective(BuyHistory record);

    int updateByPrimaryKey(BuyHistory record);
}