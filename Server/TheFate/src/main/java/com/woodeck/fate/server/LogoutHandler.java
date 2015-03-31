/**
 * 
 */
package com.woodeck.fate.server;

import com.electrotank.electroserver5.extensions.BaseLogoutEventHandler;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.ReadOnlyUserVariable;
import com.woodeck.fate.util.BeanHelper;
import com.woodeck.fate.util.VarConstant;

/**
 * @author Killua
 *
 */
public class LogoutHandler extends BaseLogoutEventHandler {
	
	@Override
	public void init(EsObjectRO parameters) {
		getApi().getLogger().debug("LogoutHandler Init");
	}
	
	@Override
	public void executeLogout(String userName) {
		ReadOnlyUserVariable userVal = getApi().getUserVariable(userName, VarConstant.kVarUserInfo);
//		Update login history if not NPC user
		if (!userVal.getValue().getBoolean(VarConstant.kParamIsNPC, false)) {
			BeanHelper.getLoginHistoryBean().updateHistory(userName);
		}
	}
	
}
