/**
 * 
 */
package com.woodeck.fate.dao;

/**
 * @author Killua
 *
 */
public interface LoginHistoryDAO {

	public void	addHistory(Integer userId, String ipAddress);
	
	public void updateHistory(String userName);
}
