/**
 * 
 */
package com.woodeck.fate.server;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.electrotank.electroserver5.extensions.BasePlugin;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;

/**
 * @author Killua
 *
 */
public class UserPlugin extends BasePlugin {
	
	@Override
	public void init(EsObjectRO parameters) {
		getApi().getLogger().debug("UserPlugin Init");
	}
	
	@Override
	public void request(String userName, EsObjectRO requestParameters) {
		EsObjectExt esObj = new EsObjectExt(requestParameters);
		getApi().getLogger().debug("Receive USER plugin request from user [{}] with: {}", userName, esObj);
		
		try {
			this.handleRequest(userName, esObj);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			getApi().getLogger().error(sw.toString());
		}
	}
	
	/**************************************************************************
	 * Request handling
	 */
	private void handleRequest(String userName, EsObjectExt esObj) {
//		Action action = Action.getEnumById(esObj.getInteger(PluginConstant.kAction, -1));
		
	}
	
}
