/**
 * 
 */
package com.woodeck.fate.dao;

import java.util.List;

import com.woodeck.fate.mapper.BuyHistoryMapper;
import com.woodeck.fate.model.BuyHistory;

/**
 * @author Killua
 *
 */
public class BuyHistoryDAOImpl implements BuyHistoryDAO {

	private BuyHistoryMapper buyHistoryMapper;
	
	public void setBuyHistoryMapper(BuyHistoryMapper buyHistoryMapper) {
		this.buyHistoryMapper = buyHistoryMapper;
	}
	
	@Override
	public void addHistory(Integer userId, String merchandiseId) {
		BuyHistory history = new BuyHistory();
		history.setUserId(userId);
		history.setMerchandiseId(merchandiseId);
		buyHistoryMapper.insertSelective(history);
	}
	
	@Override
	public List<BuyHistory> getHistoriesByMerchandiseId(String merchandiseId) {
		return buyHistoryMapper.selectByMerchandiseId(merchandiseId);
	}

}
