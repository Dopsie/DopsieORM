package dopsie.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import dopsie.exceptions.*;

/**
 * Relational Model
 */
public abstract class RelationalModel {

    public static String tableName = "";
    protected boolean isNew = false;
    protected Boolean isModified = false;
    protected HashMap<String, Object> attributes;

    protected abstract Object getAttr(String columnName);

    protected abstract void setAttr(String columnName, Object value);

    public boolean isNew() {
        return this.isNew;
    }

    public String getPrimaryKeyName() {
        return "id";
    }

    /**
     * Delete an entry.
     * Instance method that deletes an entry by its primaryKey.  
     */
    public void delete() throws ModelException {
        try {
            String queryStatement = "Delete From " + this.getTableName() + " WHERE " + this.getPrimaryKeyName()
                    + " = ? ;";
            ArrayList<Object> args = new ArrayList<Object>();
            args.add(this.attributes.get(this.getPrimaryKeyName()));
            sqlUpdate(queryStatement, args);
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    /**
     * Execute an sql query without data binding. 
     * @param  query the sql query string, to be executed.  
     * @return      java.sql.ResultSet : the result of the executeQuery function
     */
    public static ResultSet sqlQuery(String query) throws UnsupportedDataTypeException, ModelException {
        return sqlQuery(query, new ArrayList<>());
    }

    /**
     * Execute an sql query with data binding. 
     * @param  query the sql query string, to be executed.  
     * @param  args the List of args to be binded with the query before execution.  
     * @return      java.sql.ResultSet : the result of the executeQuery function
     */
    public static ResultSet sqlQuery(String query, List args) throws UnsupportedDataTypeException, ModelException {
        ResultSet result = null;
        try {
            Connection cnx = DataBaseManager.getInstance().getConnection();
            PreparedStatement statement = cnx.prepareStatement(query);
            statement = setPerparedStatementArgs(statement, args);
            result = statement.executeQuery();
        } catch (SQLException e) {
            throw new ModelException(e.getMessage());
        }
        return result;
    }

    /**
     * Execute an sql query with data binding. 
     * @param  query the sql query string, to be executed.  
     * @param  args the List of args to be binded with the query before execution.  
     * @param  model   
     * @return      ArayList : the result of the execution of the query as a 
     *              list of instances from the specified model
     * @exception  ModelException : the returned data after the query exceution
     *             can be uncompatible with the model passed as parameter. 
     *              
     */
    public static <T extends Model> ArrayList<T> sqlQuery(String query, List args, Class<T> model)
            throws UnsupportedDataTypeException, ModelException {
        ArrayList<T> result = new ArrayList<T>();
        try {
            ResultSet data = sqlQuery(query, args);

            // Getting column count using ResultSetMetaData
            ResultSetMetaData metaData = data.getMetaData();
            int columnsCount = metaData.getColumnCount();

            // Create a map with column names and types
            HashMap<String, String> columns = new HashMap<String, String>();
            for (int i = 1; i <= columnsCount; i++) {
                String columnLabel = metaData.getColumnLabel(i);
                String columnType = metaData.getColumnTypeName(i);
                columns.put(columnLabel, columnType);
            }

            // Loop through results
            while (data.next()) {
                T object = model.newInstance();
                object.isNew = false;
                // loop through all cells using column details
                fillModel(data, columns, object);
                result.add(object);
            }
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
        return result;
    }
    /**
     * Get the primarykey result of auto-increment or new insert :
     * @param statemnt: The java.sql.PreparedStatement the query and the 
     *                  metadata of the last execution.
     * @return an Object containing the primarykey value if exist else null.
     */
    public static int getPrimaryKeyAfterInsert(PreparedStatement statement) throws SQLException {

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        }
        return 0;
    }
    
    /**
     * Executing an sqlUpdate (insert and update fields in the database)
     * @param query: the sql query to be executed with specification of the 
     *               fields that are going to be replaced as question mark (?).
     * @param args: the list of values that will be binded to the sql query in order
     *              to be prepared.
     * @return int: The value of the primarykey of the updated or newly 
     *              inserted entry.
     */
    public static int sqlUpdate(String query, List args) throws UnsupportedDataTypeException, ModelException{
        System.out.println("Query " + query);
        System.out.println("Args " + args);
        int createdPrimaryKey = 0;
        try {
            Connection cnx = DataBaseManager.getInstance().getConnection();
            PreparedStatement statement = cnx.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement = setPerparedStatementArgs(statement, args);
            statement.executeUpdate();
            if (getPrimaryKeyAfterInsert(statement) != 0) {
                createdPrimaryKey = getPrimaryKeyAfterInsert(statement);
            }
        } catch (SQLException e) {
            throw new ModelException(e.getMessage());
        }
        return createdPrimaryKey;
    }
    /**
     * Get the Table Name from Model instance.
     * Can be overridded in child classes in order to change the table name.
     * @return String containing the name of the database table. 
     *          default: return simple class name.
     */
    public String getTableName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    /**
     * Save entity :
     *      creates an new entry if the instance is newly created and never saved before.
     *      updates the entry that matches the primarykey of the instance if modified.
     */
    public void save() throws ModelException, UnsupportedDataTypeException{
        if (this.isNew()) {
            int primaryKey = this.insert();
            this.setPrimaryKeyAfterInsert(primaryKey);
            this.isNew = false;
        } else {
            this.update();
        }
        this.isModified = false;
    }

    /**
     * Set the primarykey of a newly created instance after save.
     * newly created instance will have usually an new primarykey
     * after insertion to database if not specified before.
     */
    private void setPrimaryKeyAfterInsert(int primaryKey) {
        if (primaryKey != 0) {
            this.setAttr(this.getPrimaryKeyName(), primaryKey);
        }
    }

    /**
     * Defines a relationship - has many - between Models.
     * @param theClass: the model in relation with caller 
     *                  model.( containing the foreignKey)
     * @param foreignkey: the name of the foreignkey column.
     * 
     * @return an array of all instances of a specified Model 
     *          where the foreignKey of the caller model matches the foreignKey 
     *          in the requested Model.
     */
    protected <T extends Model> ArrayList<T> hasMany(Class<T> theClass, String foreignKey) throws ModelException {
        try {
            int primaryKey = (int) this.getAttr(this.getPrimaryKeyName());
            return Model.fetch(theClass).all().where(foreignKey, "=", Integer.toString(primaryKey)).execute();
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    /**
     * Defines a relationship - has many - between Models.
     * @param theClass: the model in relation with caller 
     *                  model.( containing the foreignKey ) 
     * foreignKey : default to "classname_id" all in lowercase
     * @return an array of all instances of a specified Model 
     *          where the foreignKey of the caller model matches the foreignKey in the requested Model.
     */
    protected <T extends Model> ArrayList<T> hasMany(Class<T> theClass) throws ModelException {
        try {
            return hasMany(theClass, this.getClass().getSimpleName().toLowerCase() + "_id");
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    /**
     * Defines a relationship - has One - between Models.
     * @param theClass: the model in relation with the caller 
     *                  model.( containing the foreignKey ) 
     * foreignKey : default to "classname_id" all in lowercase
     * @return a unique instance of the specified Model in relation with the 
     *          caller Model.
     */
    protected <T extends Model> T hasOne(Class<T> theClass) throws ModelException {
        try {
            return this.hasOne(theClass, theClass.getSimpleName().toLowerCase() + "_id");
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    /**
     * Defines a relationship - has One - between Models.
     * @param theClass: the model in relation with the caller 
     *                  model.( containing the foreignKey ) 
     * @param foreignkey: the name of the foreignkey column.
     * @return a unique instance of the specified Model in relation with the 
     *          caller Model.
     */
    protected <T extends Model> T hasOne(Class<T> theClass, String foreignKey) throws ModelException {
        try {
            return Model.find(theClass, (int) this.getAttr(foreignKey));
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    /**
     * Defines a relationship - Belongs To - between Models.
     * @param theClass: the model in relation with the caller 
     *                  model.( containing the foreignKey ) 
     * ForeignKeyName : default to "classname_id" in lower case.
     * @return a unique instance of the specified Model in relation with the 
     *          caller Model.
     */
    protected <T extends Model> T belongsTo(Class<T> theClass) throws ModelException {
        try {
            return belongsTo(theClass, this.getClass().getSimpleName().toLowerCase() + "_id");
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    /**
     * Defines a relationship - Belongs To - between Models.
     * @param theClass: the model in relation with the caller 
     *                  model.( containing the foreignKey ) 
     * @param foreignkey: the name of the foreignkey column.
     * @return a unique instance of the specified Model in relation with the 
     *          caller Model.
     */
    protected <T extends Model> T belongsTo(Class<T> theClass, String foreignKey) throws ModelException {
        try {
            int primaryKey = (int) this.getAttr(this.getPrimaryKeyName());
            return theClass.cast(
                    Model.fetch(theClass).all().where(foreignKey, "=", Integer.toString(primaryKey)).execute().get(0));
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    /**
     * Defines a relationship - Belongs To Many - between Models.
     * @param theClass: the model in relation with the caller 
     *                  model.( containing the foreignKey ) 
     * ForeignKeyName : default to "classname_id" in lower case.
     * @return  ArrayList: an array of all instances of a specified Model where the foreignKey 
     *          of the caller model matches the foreignKey in the requested Model.
     */
    protected <T extends Model> ArrayList<T> belongsToMany(Class<T> theClass) throws ModelException {
        try {
            return belongsToMany(theClass, this.getClass().getSimpleName().toLowerCase() + "_id");
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    /**
     * Defines a relationship - Belongs To Many - between Models.
     * @param theClass: the model in relation with the caller 
     *                  model.( containing the foreignKey ) 
     * @param foreignkey: the name of the foreignkey column.
     * @return  ArrayList: an array of all instances of a specified Model where the foreignKey 
     *          of the caller model matches the foreignKey in the requested Model.
     */
    protected <T extends Model> ArrayList<T> belongsToMany(Class<T> theClass, String foreignKey) throws ModelException {
        try {
            int primaryKey = (int) this.getAttr(this.getPrimaryKeyName());
            return Model.fetch(theClass).all().where(foreignKey, "=", Integer.toString(primaryKey)).execute();
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    /**
     * Defines a relationship - Mant To Many - between Models.
     * @param theClass: the model in relation with the caller 
     *                  model.( containing the foreignKey ) 
     * @param pivotModel: The model of relationship between the Models
     * @param foreignKeyFromOtherClass: the foreignKey in the pivot table of the other class of relationship.
     * @param foreignKeyFromThisClass: the foreignKey of the caller class in the pivot table.
     * @return  ArrayList: an array of all instances of a specified Model where the foreignKey 
     *          of the caller model matches the foreignKey in the pivot Model.
     */
    protected <B extends Model, T extends Model> ArrayList<T> manyToMany(Class<T> theClass, Class<B> pivotModel,
            String foreignKeyFromOtherClass, String foreignKeyFromThisClass) throws ModelException {
        try {
            ArrayList<B> pivotResult;
            ArrayList<T> result = new ArrayList<T>();
            pivotResult = this.belongsToMany(pivotModel, foreignKeyFromThisClass);
            for (B var : pivotResult) {
                result.add(Model.find(theClass, (int) var.getAttr(foreignKeyFromOtherClass)));
            }
            return result;
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    /**
     * Defines a relationship - Mant To Many - between Models.
     * @param theClass: the model in relation with the caller 
     *                  model.( containing the foreignKey ) 
     * @param pivotModel: The model of relationship between the Models
     * @param foreignKeyFromOtherClass: the foreignKey in the pivot table of the other class of relationship.
     * foreignKeyFromThisClass: default to "classname_id" in lowercase.
     * @return  ArrayList: an array of all instances of a specified Model where the foreignKey 
     *          of the caller model matches the foreignKey in the pivot Model.
     */
    protected <B extends Model, T extends Model> ArrayList<T> manyToMany(Class<T> theClass, Class<B> pivotModel,
            String foreignKeyFromOtherClass) throws ModelException {
        try {
            return manyToMany(theClass, pivotModel, foreignKeyFromOtherClass,
                    this.getClass().getSimpleName().toLowerCase() + "_id");
        } catch (Exception e) {
            throw new ModelException(e.getMessage());
        }
    }

    /**
     * Defines a relationship - Mant To Many - between Models.
     * @param theClass: the model in relation with the caller 
     *                  model.( containing the foreignKey ) 
     * @param pivotModel: The model of relationship between the Models
     * foreignKeyFromOtherClass: default to "classname_id" in lowercase.
     * foreignKeyFromThisClass: default to "classname_id" in lowercase.
     * @return  ArrayList: an array of all instances of a specified Model where the foreignKey 
     *          of the caller model matches the foreignKey in the pivot Model.
     */
    protected <B extends Model, T extends Model> ArrayList<T> manyToMany(Class<T> theClass, Class<B> pivotModel)
            throws ModelException {
        try {
            return manyToMany(theClass, pivotModel, theClass.getSimpleName().toLowerCase() + "_id",
                    this.getClass().getSimpleName().toLowerCase() + "_id");
        } catch (Exception e) {

            throw new ModelException(e.getMessage());
        }
    }
    
    /**
     * From a Resultset (result of query execution)  and the names of the columns/types 
     *  populate the fields of Model instance
     * @param data: the result of a quesry execution 
     * @param columns: a hashmap containing the names and the types of the columns fetched from database
     * @param object: the model object that will be populated with data.
     * @return  ArrayList: an array of all instances of a specified Model where the foreignKey 
     *          of the caller model matches the foreignKey in the pivot Model.
     */
    private static <T extends Model> void fillModel(ResultSet data, HashMap<String, String> columns, T object)
            throws SQLException {
        for (Map.Entry cell : columns.entrySet()) {
            String key = (String) cell.getKey();
            String value = (String) cell.getValue();
            if (value == "TEXT" || value == "VARCHAR") {
                object.setAttr(key, data.getString(key));
            } else if (value == "BIGINT") {
                object.setAttr(key, data.getLong(key));
            } else if (value == "DATE" || value == "DATETIME") {
                object.setAttr(key, data.getDate(key));
            } else if (value == "INT" || value == "TINYINT" || value == "SMALLINT" || value == "MEDIUMINT") {
                object.setAttr(key, data.getInt(key));
            } else if (value == "FLOAT") {
                object.setAttr(key, data.getFloat(key));
            } else if (value == "DOUBLE") {
                object.setAttr(key, data.getDouble(key));
            } else if (value == "TIME") {
                object.setAttr(key, data.getTime(key));
            }
        }
    }
    /**
     * Update an entry in the database from a new modified instance of a model.
     */
    private void update() throws UnsupportedDataTypeException,ModelException{
        ArrayList<String> args = new ArrayList<String>();
        for (Map.Entry<String, Object> attr : this.attributes.entrySet()) {
            args.add(" " + attr.getKey() + " = ?");
        }
        String queryString = "UPDATE " + this.getTableName() + " SET " + String.join(",", args) + " WHERE "
                + this.getPrimaryKeyName() + " = " + this.getAttr(this.getPrimaryKeyName());
        for(Map.Entry e: this.attributes.entrySet()) {
            if(e.getValue() == null) {
                e.setValue("NULL");
            }
        }
        this.attributes.values().forEach(System.out::println);
        sqlUpdate(queryString, new ArrayList(this.attributes.values()));
    }
    /**
     * Insert newly created entry in the database from a new instance of a model.
     * @return The primaryKey of the newly added entry.
     */
    private int insert() {
        int result = 0;
        try {
            String columnsNames = String.join(",", this.attributes.keySet());
            String[] wildcards = new String[this.attributes.size()];
            Arrays.fill(wildcards, "?");
            String wildcardsString = String.join(",", Arrays.asList(wildcards));
            String queryString = "INSERT INTO " + this.getTableName() + " (" + columnsNames + ") VALUES ("
                    + wildcardsString + ")";
            this.attributes.values().forEach(System.out::println);
            result = sqlUpdate(queryString, new ArrayList(this.attributes.values()));
        } catch (Exception e) {
            System.out.println("Error inserting new entry");
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * Set prepared statement arguments.
     * @param statement the sql statment that will be prepared and binded with data. 
     * @param args a list of values that will be binded to the statement.
     * 
     * @return The new prepared statement.
     */
    private static PreparedStatement setPerparedStatementArgs(PreparedStatement statement, List args)
            throws SQLException, UnsupportedDataTypeException {
        for (int index = 0; index < args.size(); index++) {
            int pstIndex = index + 1;
            if (args.get(index) instanceof String) {
                statement.setString(pstIndex, (String) args.get(index));
            } else if (args.get(index) instanceof Integer) {
                statement.setInt(pstIndex, (Integer) args.get(index));
            } else if (args.get(index) instanceof Double) {
                statement.setDouble(pstIndex, (Double) args.get(index));
            } else if (args.get(index) instanceof Long) {
                statement.setLong(pstIndex, (Long) args.get(index));
            } else if (args.get(index).getClass().getSimpleName().equals("Blob")) {
                statement.setBlob(pstIndex, (java.sql.Blob) args.get(index));
            } else if (args.get(index).getClass().getSimpleName().equals("Date")) {
                statement.setDate(pstIndex, (java.sql.Date) args.get(index));
            } else if (args.get(index).getClass().getSimpleName().equals("Time")) {
                statement.setTime(pstIndex, (java.sql.Time) args.get(index));
            } else if (args.get(index).getClass().getSimpleName().equals("Timestamp")) {
                statement.setTimestamp(pstIndex, (java.sql.Timestamp) args.get(index));
            } else {
                throw new UnsupportedDataTypeException(
                        "Unsupported dataType : " + args.get(index).getClass().getSimpleName());
            }
        }
        return statement;
    }

}