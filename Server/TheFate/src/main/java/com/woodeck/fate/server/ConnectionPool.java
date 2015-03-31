/**
 * 
 */
package com.woodeck.fate.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import snaq.db.ConnectionPoolManager;

import com.electrotank.electroserver5.extensions.BaseManagedObjectFactory;
import com.electrotank.electroserver5.extensions.api.ManagedObjectFactoryApi;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectEntry;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;

/**
 * @author Killua
 * Manage database connection by ElectroServer's ManagedObjectFactory and DBPool framework.
 */
@Deprecated
public class ConnectionPool extends BaseManagedObjectFactory {

	public static final int DEFAULT_CONN_TIMEOUT = 5000;	// 5 second connection-delay timeout
	
	private ManagedObjectFactoryApi api;
	private ConnectionPoolManager manager;
	private final Map<String, Integer> defaultTimeout = new HashMap<String, Integer>();
	

	@Override
	public void destroy() {
        manager.release();	// Clean up the manager by releasing all connection pool resources
	}

	@Override
	public void init(EsObjectRO parameters) {
		Properties props = new Properties();
        props.put("drivers", (String) validateAndReturnEsObjectEntry(parameters, "drivers"));
        
        if(parameters.variableExists("logfile")) {
            props.put("logfile", parameters.getString("logfile"));
        }
        
        EsObject[] pools = (EsObject[]) validateAndReturnEsObjectEntry(parameters, "pools");
        for (EsObject poolParams : pools) {
            String poolName = (String) validateAndReturnEsObjectEntry(poolParams, "poolname");
            if(poolParams.variableExists("timeout")) {
                defaultTimeout.put(poolName, poolParams.getInteger("timeout"));
            }

            for (EsObjectEntry entry : poolParams) {
                String name = entry.getName();
                if (name.equals("poolname") || name.equals("timeout")) {
                    continue;
                }
                
                Object value = null;
                if (name.equals("url") || name.equals("user") || name.equals("password")) {
                	value = validateAndReturnEsObjectEntry(poolParams, name);	// Required parameters
                } else {
                	value = entry.getRawValue().toString();	// optional parameters
                }
                
                StringBuilder builder = new StringBuilder(poolName);
                builder.append(".");
                builder.append(name);
                props.put(builder.toString(), value.toString());
            }
        }
        
//      Create a new instance of the ConnectionPoolManager using the properties we just set
        ConnectionPoolManager.createInstance(props);
        
//      Get a new instance of it for use later
        try {
            manager = ConnectionPoolManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}

	@Override
	public Object acquireObject(EsObjectRO parameters) {
        String poolName = (String) validateAndReturnEsObjectEntry(parameters, "poolname");

//      Determine which timeout variable to use. If it's been passed in, use that
        int timeout;
        if (parameters.variableExists("timeout")) {
            timeout = parameters.getInteger("timeout");
        } else {
            // If there is a global default for this pool
            if (defaultTimeout.containsKey(poolName)) {
                timeout = defaultTimeout.get(poolName);
            }
            // Otherwise let's use the global default in general
            else {
                timeout = DEFAULT_CONN_TIMEOUT;
            }
        }

        Connection connection = null;
        try {
            connection = manager.getConnection(poolName, timeout);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return connection;
	}

	@Override
	public void releaseObject(Object object) {
		if(object == null) {
            throw new RuntimeException("The supplied object is null!");
        }
		
		if(!(object instanceof Connection)) {
            throw new RuntimeException("The supplied object is not of type Connection!");
        }
		
		Connection connection = (Connection) object;
		try {
			if (!connection.isClosed())
				connection.close();
		} catch (SQLException e) {
			throw new RuntimeException("Error closing connection!");
		}
	}
	
	@Override
	public ManagedObjectFactoryApi getApi() {
		return api;
	}

	@Override
	public void setApi(ManagedObjectFactoryApi api) {
		this.api = api;
	}

    private Object validateAndReturnEsObjectEntry(EsObjectRO esObject, String paramName) {
        Object results = null;
        if(!esObject.variableExists(paramName)) {
        	throw new RuntimeException("The variable '" + paramName + "' not defined - it is required!");
        } else {
            results = esObject.getRawVariable(paramName);
        }
        return results;
    }
	
}
