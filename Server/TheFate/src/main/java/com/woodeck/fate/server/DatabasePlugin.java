/**
 * 
 */
package com.woodeck.fate.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.electrotank.electroserver5.extensions.BasePlugin;
import com.electrotank.electroserver5.extensions.api.value.EsObject;
import com.electrotank.electroserver5.extensions.api.value.EsObjectRO;

/**
 * @author Killua
 * Handle database SQL-CRUD operation by JDBC connection directly, not use any persistence framework.
 */
@Deprecated
public class DatabasePlugin extends BasePlugin {

	public static final String FACTORY_NAME = "ManagedConnectionPool";
	public static final String
		kParamPoolName 		= "poolname",
		kParamResults		= "results",
		kParamDataType		= "dataType",
		kParamColumnName	= "columnName",
		kParamColumnValue	= "columnValue";
	
    private String poolname;

    @Override
    public void init(EsObjectRO parameters) {
        poolname = parameters.getString(kParamPoolName);
        getApi().getLogger().debug("Using database pool: {}", poolname);
    }

    /**
     * Executes any valid read only SQL query on the database, 
     * returning a ResultSet.
     * 
     * WARNING: Don't use this with any String that comes from a 
     * client because it would run the risk of an SQL injection attack.
     * 
     * @param query valid SQL query
     * @return ResultSet from the query, as an EsObject
     */
    @SuppressWarnings("finally")
	public EsObject doQuery(String query) throws SQLException {
        getApi().getLogger().debug("Query called with string: {}", query);
        
        EsObject resultSetObj = new EsObject();
        Connection conn = null;
        try {
            EsObject esDB = new EsObject();
            esDB.setString(kParamPoolName, poolname);
            
            conn = (Connection) getApi().acquireManagedObject(FACTORY_NAME, esDB);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet result = stmt.executeQuery(query);
            if (result != null)  resultSetObj = resultSetToEsObject(result);
            
        } catch (SQLException e) {
            getApi().getLogger().debug("SQL exception: " + e.getMessage());
            Logger.getLogger(DatabasePlugin.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (conn != null) conn.close();
            return resultSetObj;
        }
    }
    
    /**
     * Inserts a row into a database table.  The EsObject[] parameters 
     * contains the data to be inserted, with each column value stored
     * as a separate EsObject.  Each EsObject will have one variable named 
     * DATATYPE with integer value of java.sql.Types.INTEGER, java.sql.Types.VARCHAR, 
     * java.sql.Types.DOUBLE or java.sql.Types.BOOLEAN.  It will have a second
     * variable named COLUMN, with the name of the column.  
     * The third variable is named VALUE, with the value to be stored in that column.
     * 
     * WARNING: If a String parameter comes from a client it needs to be validated 
     * before being used here or it would run the risk of an SQL injection attack.
     * 
     * @param tableName name of the database table
     * @param obj EsObject[] containing the data for the row to be inserted
     * @return true for successful insertion
     */
    public boolean insertRow(String tableName, EsObject[] parameters) throws SQLException {
        EsObject esDB = new EsObject();
        esDB.setString(kParamPoolName, poolname);
        
        String insertString = buildInsertString(tableName, parameters);
        if (insertString == null) return false;

        Connection conn = null;
        try {
            conn = (Connection) getApi().acquireManagedObject(FACTORY_NAME, esDB);
            Statement stmt = conn.createStatement();
            int rows = stmt.executeUpdate(insertString);
            if (conn != null) conn.close();
            return (rows > 0);
            
        } catch (Exception e) {
            getApi().getLogger().debug("Error attempting to insert: {}", insertString);
            if (conn != null) conn.close();
            return false;
        }
    }
    
    /**
     * Updates a single field of a row in a database.  
     * Each EsObject (key, field) will have one variable named 
     * DATATYPE with integer value of java.sql.Types.INTEGER, java.sql.Types.VARCHAR, 
     * java.sql.Types.DOUBLE or java.sql.Types.BOOLEAN.  It will have a second
     * variable named COLUMN, with the name of the column.  
     * The third variable is named VALUE, with the value of that column (current
     * value for the key, value to be stored for the field).
     * 
     * WARNING: If a String parameter comes from a client it needs to be validated 
     * before being used here or it would run the risk of an SQL injection attack.
     * 
     * @param tableName name of the table
     * @param keyObj EsObject used to identify the row(s) to update
     * @param fldObj EsObject giving the value to store in the field
     * @return true if no errors occurred
     */
    public boolean updateField(String tableName, EsObject keyObj, EsObject fldObj) throws SQLException {
        ResultSet resultSet = null;
        Connection conn = null;
        try {
            EsObject esDB = new EsObject();
            esDB.setString(kParamPoolName, poolname);
            
            conn = (Connection) getApi().acquireManagedObject("ManagedConnectionPool", esDB);
            Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            
            String keyName = keyObj.getString(kParamColumnName);
            String keyValue = getValueString(keyObj);
            String fieldName = fldObj.getString(kParamColumnName);
            
            StringBuffer querySb = new StringBuffer("select ");
            querySb.append(keyName);
            querySb.append(", ");
            querySb.append(fieldName);
            querySb.append(" from ");
            querySb.append(tableName);
            querySb.append(" where ");
            querySb.append(keyName);
            querySb.append(" = ");
            querySb.append(keyValue);
            resultSet = stmt.executeQuery(querySb.toString());
            
            while (resultSet.next()) {
                switch (fldObj.getInteger(kParamDataType)) {
                    case Types.INTEGER:
                        resultSet.updateInt(fieldName, fldObj.getInteger(kParamDataType));
                        break;
                    case Types.VARCHAR:
                        resultSet.updateString(fieldName, fldObj.getString(kParamDataType));
                        break;
                    case Types.DOUBLE:
                        resultSet.updateDouble(fieldName, fldObj.getDouble(kParamDataType));
                        break;
                    case Types.BOOLEAN:
                        resultSet.updateBoolean(fieldName, fldObj.getBoolean(kParamDataType));
                        break;
                    // there are lots more possible Types we could add here
                }
                resultSet.updateRow();
            }
            if (conn != null) conn.close();
            return true;
            
        } catch (Exception e1) {
            try {
                if (resultSet != null) resultSet.cancelRowUpdates();
                if (conn != null) conn.close();
                return false;
            } catch (Exception e2) {
                if (conn != null) conn.close();
                return false;
            }
        }
    }
    
    /**
     * Executes any valid SQL command on the database. 
     * 
     * WARNING: Don't use this with any String that comes from a 
     * client because it would run the risk of an SQL injection attack.
     * 
     * @param sqlCommand
     * @return true if no errors occurred
     */
    public boolean executeSQL(String sqlCommand) throws SQLException {
        EsObject esDB = new EsObject();
        esDB.setString(kParamPoolName, poolname);
        
        Connection conn = null;
        try {
            conn = (Connection) getApi().acquireManagedObject(FACTORY_NAME, esDB);
            Statement stmt = conn.createStatement();
            stmt.execute(sqlCommand);
            if (conn != null) conn.close();
            return true;
        } catch (Exception e) {
            getApi().getLogger().debug("Error attempting to execute SQL: " + sqlCommand);
            if (conn != null) conn.close();
            return false;
        }
    }
    
    private String buildInsertString(String tableName, EsObject[] parameters) {
    	StringBuffer insertSb = new StringBuffer("insert into ");
    	insertSb.append(tableName);
    	StringBuffer columnSb = new StringBuffer(" (");
    	StringBuffer valueSb = new StringBuffer(" (");
        
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
            	columnSb.append(", ");
                valueSb.append(", ");
            }
            try {
                EsObject obj = parameters[i];
                columnSb.append(obj.getString(kParamColumnName));
                valueSb.append(getValueString(obj));
            } catch (Exception e) {
                getApi().getLogger().debug("Error reading column {}", i);
                return null;
            }
        }
        insertSb.append(columnSb);
        insertSb.append(") values ");
        insertSb.append(valueSb);
        insertSb.append(")");
        
        return insertSb.toString();
    }
    
    private String getValueString(EsObject obj) {
    	StringBuffer valueSb = new StringBuffer();
        switch (obj.getInteger(kParamDataType)) {
            case Types.INTEGER:
            	valueSb.append(obj.getInteger(kParamColumnValue));
                break;
            case Types.VARCHAR:
                valueSb.append("'");
                valueSb.append(obj.getString(kParamColumnValue));
                valueSb.append("'");
                break;
            case Types.DOUBLE:
                valueSb.append(obj.getDouble(kParamColumnValue));
                break;
            case Types.BOOLEAN:
                valueSb.append(obj.getBoolean(kParamColumnValue));
                break;
            // there are lots more possible Types we could add here
            }
        return valueSb.toString();
    }
    
    /**
     * Converts a ResultSet to an EsObject containing an array of EsObjects,
     * one for each row of the ResultSet.
     * 
     * @param resultSet ResultSet from a query
     * @return EsObject with the data from the ResultSet
     */
    private EsObject resultSetToEsObject(ResultSet resultSet) {
        EsObject esObj = new EsObject();
        List<EsObject> list = new ArrayList<EsObject>();
        try {
            ResultSetMetaData metadata = resultSet.getMetaData();
            int numColumns = metadata.getColumnCount();
            while (resultSet.next()) {
                EsObject thisObj = new EsObject();
                for (int i = 1; i <= numColumns; i++) {
                    String columnName = metadata.getColumnName(i);
                    switch (metadata.getColumnType(i)) {
                        case Types.INTEGER:
                            thisObj.setInteger(columnName, resultSet.getInt(i));
                            break;
                        case Types.VARCHAR:
                            thisObj.setString(columnName, resultSet.getString(i));
                            break;
                        case Types.DOUBLE:
                            thisObj.setDouble(columnName, resultSet.getDouble(i));
                            break;
                        case Types.BOOLEAN:
                            thisObj.setBoolean(columnName, resultSet.getBoolean(i));
                            break;
                        // there are lots more possible Types we could add here
                    }
                }
                list.add(thisObj);
            }
            EsObject[] array = new EsObject[list.size()];
            array = list.toArray(array);
            esObj.setEsObjectArray(kParamResults, array);
        } catch (SQLException e) {
            getApi().getLogger().debug("Error trying to read the ResultSet.");
        }
        return esObj;
    }
    
}
