/**
 * 
 */
package com.woodeck.fate.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.electrotank.electroserver5.extensions.BaseLoginEventHandler;
import com.electrotank.electroserver5.extensions.ChainAction;
import com.electrotank.electroserver5.extensions.LoginContext;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.woodeck.fate.model.User;
import com.woodeck.fate.util.BeanHelper;
import com.woodeck.fate.util.Constant;
import com.woodeck.fate.util.VarConstant;

/**
 * @author Killua
 *
 */
public class LoginHandler extends BaseLoginEventHandler {
	
	public static final String
		kParamIsRegister	= "isRegister",
		kParamIPAddress		= "ipAddress",
		kParamLoginResult	= "loginResult";
	
	public enum LoginResult {
		UserNameEmpty,
		UserNameTaken,
		UserNotFound,
		WrongPassword;
	}
	
	@Override
	public void init(EsObjectRO parameters) {
		getApi().getLogger().debug("LoginHandler Init");
	}
	
	@Override
	public ChainAction executeLogin(LoginContext context) {		
		String userName = context.getUserName();
		String password = context.getPassword();
				
		if (null == userName && null == password) {
			context.setUserName(Constant.GUEST_USER_NAME_PREFIX + (int)(Math.random()*1000000));
			return ChainAction.OkAndContinue;
		}
		
//		Optional: Such as avatar name or a boolean indicating that this is a new user.
        EsObjectRO request = context.getRequestParameters();
//		The response esObj can be read by the client from the LoginResponse.
        EsObject response = context.getResponseParameters();
        
        if (null == userName || userName.isEmpty()) {
        	response.setInteger(kParamLoginResult, LoginResult.UserNameEmpty.ordinal());
        	return ChainAction.Fail;
        }
        
//      Check if is NPC
        if (request.getBoolean(VarConstant.kParamIsNPC, false)) {
        	User user = new User();
        	user.setIsNPC(true);
        	user.setNickName(userName);
        	context.addUserVariable(VarConstant.kVarUserInfo, user.mapToEsObject());
        	return ChainAction.OkAndContinue;
        }
        
//      Check if register a new user
        if (null != request && request.getBoolean(kParamIsRegister)) {
        	if (!this.registerNewUser(userName, password)) {
        		response.setInteger(kParamLoginResult, LoginResult.UserNameTaken.ordinal());
        		return ChainAction.Fail;
        	}
        }
        
//		Assume that this other user is a ghost. Evict doesn't really send the evicted user any message.
		if (getApi().isUserLoggedIn(userName)) {
			getApi().evictUserFromServer(userName, response);
		}
		
//		Login: Check userName and password
		User user = BeanHelper.getUserBean().getUserByName(userName);
		if (null == user) {
			response.setInteger(kParamLoginResult, LoginResult.UserNotFound.ordinal());
    		return ChainAction.Fail;
		}
		
		String hashedPassword = this.generateHashedPassword(password);
		if (!user.getPassword().equals(hashedPassword)) {
			response.setInteger(kParamLoginResult, LoginResult.WrongPassword.ordinal());
    		return ChainAction.Fail;
		}
		
		context.addUserVariable(VarConstant.kVarUserInfo, user.mapToEsObject());
		BeanHelper.getLoginHistoryBean().addHistory(user.getUserId(), request.getString(kParamIPAddress, ""));
		
		return ChainAction.OkAndContinue;
	}
	
	private boolean registerNewUser(String userName, String password) {		
//      Check the userName if has been taken
        if (null != BeanHelper.getUserBean().getUserByName(userName)) {
        	return false;
        }
        
//		If not fill password while registering, set the userName as default password.
        if (null == password || password.isEmpty()) {
        	password = userName;
        }
        
        String hashedPassword = this.generateHashedPassword(password);
        if (!hashedPassword.equals(password)) {
        	BeanHelper.getUserBean().createUser(userName, hashedPassword);
        }
        
        return true;
	}
	
	private String generateHashedPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());		// Add password bytes to digest
			byte[] bytes = md.digest();
			
//			This bytes[] has bytes in decimal format. Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		}
		catch (NoSuchAlgorithmException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			getApi().getLogger().error(sw.toString());
			return password;
		}
	}
	
/**	Method createOrUpdateUserVariable doesn't work in the LoginEventHandler */
//	private void createUserVariable(String userName) {
//		User user = BeanHelper.getUserBean().getUserByName(userName);
//		getApi().createOrUpdateUserVariable(user.getUserName(), VarConstant.kVarUserInfo, user.mapToEsObject());
//	}
	
}
