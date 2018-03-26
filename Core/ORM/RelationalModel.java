package Core.ORM;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Core.ORM.Model;

import java.util.Arrays;
import java.util.HashMap;

import Helpers.Exceptions.*;

/**
 * RelationalModel
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

    public void delete() {
        try {
            String queryStatement = "Delete From " + this.getTableName() + " WHERE " + this.getPrimaryKeyName()
                    + " = ? ;";
            ArrayList<Object> args = new ArrayList<Object>();
            args.add(this.attributes.get(this.getPrimaryKeyName()));
            sqlUpdate(queryStatement, args);
        } catch (Exception e) {
            System.out.println("Error deleting entry");
        }
    }

    public static ResultSet sqlQuery(String query) throws UnsupportedDataTypeException {
        return sqlQuery(query, new ArrayList<>());
    }

    public static ResultSet sqlQuery(String query, List args) throws UnsupportedDataTypeException {
        ResultSet result = null;
        try {
            Connection cnx = DataBaseManager.getInstance().getConnection();
            PreparedStatement statement = cnx.prepareStatement(query);
            statement = setPerparedStatementArgs(statement, args);
            result = statement.executeQuery();

        } catch (SQLException e) {
            System.out.println("Error Executing query");
        }
        return result;
    }

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
                // loop through all cells using column details
                fillModel(data, columns, object);
                result.add(object);
            }
        } catch (UnsupportedDataTypeException e) {
            throw new UnsupportedDataTypeException("UnsupportedDataTypeException");
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
        return result;
    }

    public static Object getPrimaryKeyAfterInsert(PreparedStatement statement) throws SQLException {

        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        }
        return null;
    }

    public static Long sqlUpdate(String query, List args) throws UnsupportedDataTypeException {

        Long createdPrimaryKey = null;
        try {
            Connection cnx = DataBaseManager.getInstance().getConnection();
            PreparedStatement statement = cnx.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement = setPerparedStatementArgs(statement, args);
            statement.executeUpdate();
            if (getPrimaryKeyAfterInsert(statement) != null) {
                createdPrimaryKey = (Long) getPrimaryKeyAfterInsert(statement);
            }
        } catch (SQLException e) {
            System.out.println("Error Executing query");
        }
        return createdPrimaryKey;
    }

    public String getTableName() {
        return this.getClass().getSimpleName();
    }

    public void save() {
        if (this.isNew()) {
            Long primaryKey = this.insert();
            this.setPrimaryKeyAfterInsert(primaryKey);
            this.isNew = false;
        } else {
            this.update();
        }
        this.isModified = false;
    }

    private void setPrimaryKeyAfterInsert(Long primaryKey) {
        if (primaryKey != null) {
            this.setAttr(this.getPrimaryKeyName(), primaryKey);
        }
    }

    protected <T extends Model> ArrayList<T> hasMany(Class<T> theClass, String foreignKey) throws ModelException {
        try {
            int primaryKey = (int) this.getAttr(this.getPrimaryKeyName());
            return Model.fetch(theClass).all().where(foreignKey, "=", Integer.toString(primaryKey)).execute();
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> ArrayList<T> hasMany(Class<T> theClass) throws ModelException {
        try {
            return hasMany(theClass, this.getClass().getSimpleName() + "_id");
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> T hasOne(Class<T> theClass) throws ModelException {
        try {
            return this.hasOne(theClass, theClass.getSimpleName() + "_id");
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> T hasOne(Class<T> theClass, String foreignKey) throws ModelException {
        try {
            return Model.find(theClass, (int) this.getAttr(foreignKey));
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> T belongsTo(Class<T> theClass) throws ModelException {
        try {
            return belongsTo(theClass, this.getClass().getSimpleName() + "_id");
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> T belongsTo(Class<T> theClass, String foreignKey) throws ModelException {
        try {
            int primaryKey = (int) this.getAttr(this.getPrimaryKeyName());
            return theClass.cast(
                    Model.fetch(theClass).all().where(foreignKey, "=", Integer.toString(primaryKey)).execute().get(0));
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> ArrayList<T> belongsToMany(Class<T> theClass) throws ModelException {
        try {
            return belongsToMany(theClass, this.getClass().getSimpleName() + "_id");
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> ArrayList<T> belongsToMany(Class<T> theClass, String foreignKey) throws ModelException {
        try {
            int primaryKey = (int) this.getAttr(this.getPrimaryKeyName());
            return Model.fetch(theClass).all().where(foreignKey, "=", Integer.toString(primaryKey)).execute();
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

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
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <B extends Model, T extends Model> ArrayList<T> manyToMany(Class<T> theClass, Class<B> pivotModel,
            String foreignKeyFromOtherClass) throws ModelException {
        try {
            return manyToMany(theClass, pivotModel,foreignKeyFromOtherClass, this.getClass().getSimpleName().toLowerCase() + "_id");
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <B extends Model, T extends Model> ArrayList<T> manyToMany(Class<T> theClass, Class<B> pivotModel)
            throws ModelException {
        try {
            return manyToMany(theClass, 
                              pivotModel, 
                              theClass.getSimpleName().toLowerCase() + "_id",
                              this.getClass().getSimpleName().toLowerCase() + "_id");
        } catch (Exception e) {

            throw new ModelException("Couldn't create Model");
        }
    }

    private static <T extends Model> void fillModel(ResultSet data, HashMap<String, String> columns, T object)
            throws SQLException {
        for (Map.Entry cell : columns.entrySet()) {
            String key = (String) cell.getKey();
            String value = (String) cell.getValue();
            if (value == "TEXT" || value == "VARCHAR") {
                object.setAttr(key, data.getString(key));
            } else if (value == "DATE" || value == "DATETIME") {
                object.setAttr(key, data.getDate(key));
            } else if (value == "INT" || value == "TINYINT" || value == "SMALLINT" || value == "MEDIUMINT"
                    || value == "BIGINT") {
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

    private void update() {
        try {
            String columnsNames = String.join(",", this.attributes.keySet());
            ArrayList<String> args = new ArrayList<String>();
            for (Map.Entry<String, Object> attr : this.attributes.entrySet()) {
                args.add(" " + attr.getKey() + " = ?");
            }
            String queryString = "UPDATE " + this.getTableName() + " SET " + String.join(",", args) + " WHERE "
                    + this.getPrimaryKeyName() + " = " + this.getAttr(this.getPrimaryKeyName());
            this.attributes.values().forEach(System.out::println);
            sqlUpdate(queryString, new ArrayList(this.attributes.values()));
        } catch (Exception e) {
            System.out.println("Error updating model");
        }
    }

    private Long insert() {
        Long result = null;
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
        }
        return result;
    }

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