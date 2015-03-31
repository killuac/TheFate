/**
 * 
 */
package com.woodeck.fate.server;

import com.electrotank.electroserver5.extensions.BaseUserVariableEventHandler;
import com.electrotank.electroserver5.extensions.ChainAction;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;
import com.electrotank.electroserver5.extensions.api.value.UserVariableUpdateContext;
import com.woodeck.fate.model.User;
import com.woodeck.fate.util.BeanHelper;
import com.woodeck.fate.util.VarConstant;

/**
 * @author Killua
 *
 */
public class UserVariableHandler extends BaseUserVariableEventHandler {
	
	@Override
	public void init(EsObjectRO parameters) {
		getApi().getLogger().debug("UserVariableHandler Init");
	}
	
	@Override
	public ChainAction userVariableCreated(UserVariableUpdateContext context) {
		getApi().getLogger().debug("User name: {}", context.getUserName());
        getApi().getLogger().debug("Create variable name: {}", context.getVariableName());
        getApi().getLogger().debug("Variable value: {}", context.getVariableValue());
        return ChainAction.OkAndContinue;
	}
	
	@Override
	public ChainAction userVariableDeleted(UserVariableUpdateContext context) {
		getApi().getLogger().debug("User name: {}", context.getUserName());
        getApi().getLogger().debug("Delete variable name: {}", context.getVariableName());
        getApi().getLogger().debug("Variable old value: {}", context.getOldVariableValue());
        return ChainAction.OkAndContinue;
	}
	
	@Override
	public ChainAction userVariableUpdated(UserVariableUpdateContext context) {
		getApi().getLogger().debug("User name: {}", context.getUserName());
        getApi().getLogger().debug("Update variable name: {}", context.getVariableName());
        getApi().getLogger().debug("Variable value: {}", context.getVariableValue());
        getApi().getLogger().debug("Variable old value: {}", context.getOldVariableValue());
        
        if (context.getVariableName().equals(VarConstant.kVarUserInfo)) {
			User user = BeanHelper.getUserBean().getUserByName(context.getUserName());
			user.setNickName(context.getVariableValue().getString(VarConstant.kParamNickName));
//			user.setAvatar(context.getVariableValue().getByteArray(VarConstant.kParamUserAvatar));
			BeanHelper.getUserBean().updateUser(user);
        }
		
        return ChainAction.OkAndContinue;
	}
	
}
