/**
 * 
 */
package com.woodeck.fate.dao;

import com.woodeck.fate.mapper.LevelExpMapper;

/**
 * @author Killua
 *
 */
public class LevelExpDAOImpl implements LevelExpDAO {
	
	private LevelExpMapper levelExpMapper;
	
	public void setLevelExpMapper(LevelExpMapper levelExpMapper) {
		this.levelExpMapper = levelExpMapper;
	}
	
	@Override
	public Integer getExperienceByLevel(Byte level) {
		return levelExpMapper.selectByPrimaryKey(level).getExperience();
	}

}
