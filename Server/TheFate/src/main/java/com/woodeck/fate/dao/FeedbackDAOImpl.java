/**
 * 
 */
package com.woodeck.fate.dao;

import com.woodeck.fate.mapper.FeedbackMapper;
import com.woodeck.fate.model.Feedback;
import com.woodeck.fate.model.User;
import com.woodeck.fate.util.BeanHelper;


/**
 * @author Killua
 *
 */
public class FeedbackDAOImpl implements FeedbackDAO {
	
	private FeedbackMapper feedbackMapper;
	
	public void setFeedbackMapper(FeedbackMapper feedbackMapper) {
		this.feedbackMapper = feedbackMapper;
	}
	
	@Override
	public void addIssue(String userName, String title, String content) {
		User user = BeanHelper.getUserBean().getUserByName(userName);
		Feedback issue = new Feedback();
		issue.setUserId(user.getUserId());
		issue.setIssueTitle(title);
		issue.setIssueContent(content);
		feedbackMapper.insertSelective(issue);
	}

}
