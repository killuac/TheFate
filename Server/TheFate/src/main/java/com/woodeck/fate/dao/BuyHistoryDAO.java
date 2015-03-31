/**
 * 
 */
package com.woodeck.fate.dao;

import java.util.List;

import com.woodeck.fate.model.BuyHistory;

/**
 * @author Killua
 *
 */
public interface BuyHistoryDAO {

	public void	addHistory(Integer userId, String merchandiseId);
	
	public List<BuyHistory> getHistoriesByMerchandiseId(String merchandiseId);
	
}
