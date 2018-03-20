package Core.ORM;

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

import Helpers.Exceptions.*;

/**
 * RelationalModel
 */
public class RelationalModel {

    public static String tableName = "";
    protected boolean isNew = false;
    protected Boolean isModified = false;

    public boolean isNew() {
        return this.isNew;
    }

    public static DataBaseCollection retreiveByField(String modelName, String fieldName, Object value) {
        try {
            Class ModelClass = Class.forName("Models." + modelName);

        } catch (ClassNotFoundException e) {
            
        }
        return null;
    }

    public static DataBaseCollection retreiveByPk(String modelName, Object pk) {
        try {
            Class ModelClass = Class.forName("Models." + modelName);

        } catch (ClassNotFoundException e) {

        }
        return null;
    };

    public static void deleteByPk(String modelName, Object pk) {
        try {
            Class ModelClass = Class.forName("Models." + modelName);

        } catch (ClassNotFoundException e) {
            
        }
    }

    public String getPrimaryKeyName() {
        return "id";
    }

    private static PreparedStatement setPerparedStatementArgs(PreparedStatement statement, List args) throws SQLException, UnsupportedDataTypeException{
        for(int index = 0; index < args.size(); index++) {
            int pstIndex = index + 1;
            if(args.get(index) instanceof String) {
                statement.setString(pstIndex, (String)args.get(index));
            } else if(args.get(index) instanceof Integer) {
                statement.setInt(pstIndex, (Integer)args.get(index));
            } else if(args.get(index) instanceof Double) {
                statement.setDouble(pstIndex, (Double)args.get(index));
            } else if(args.get(index) instanceof Long) {
                statement.setLong(pstIndex, (Long)args.get(index));
            } else if(args.get(index).getClass().getSimpleName().equals("Blob")) {
                statement.setBlob(pstIndex, (java.sql.Blob)args.get(index));
            } else if(args.get(index).getClass().getSimpleName().equals("Date")) {
                statement.setDate(pstIndex, (java.sql.Date)args.get(index));
            } else if(args.get(index).getClass().getSimpleName().equals("Time")) {
                statement.setTime(pstIndex, (java.sql.Time)args.get(index));
            } else if(args.get(index).getClass().getSimpleName().equals("Timestamp")) {
                statement.setTimestamp(pstIndex, (java.sql.Timestamp)args.get(index));
            } else {
                throw new UnsupportedDataTypeException("Unsupported dataType : " + args.get(index).getClass().getSimpleName());
            }
        }
        return statement;
    }

    public static ResultSet sqlQuery(String query) throws UnsupportedDataTypeException{
        return sqlQuery(query, new ArrayList<>());
    }

    public static ResultSet sqlQuery(String query, List args) throws UnsupportedDataTypeException{
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

    public static <T extends Model> ArrayList<T> sqlQuery(String query, List args, Class<T> model) throws UnsupportedDataTypeException, ModelException {
        ArrayList<T> result = new ArrayList<T>();
        try {
            ResultSet data = sqlQuery(query, args);

            // Getting column count using ResultSetMetaData
            ResultSetMetaData metaData = data.getMetaData();
            int columnsCount = metaData.getColumnCount();

            // Create a map with column names and types
            HashMap<String,String> columns = new HashMap<String,String>();
            for(int i = 1; i <= columnsCount; i++) {
                String columnLabel = metaData.getColumnLabel(i);
                String columnType = metaData.getColumnTypeName(i);
                columns.put(columnLabel, columnType);
            }

            // Loop through results
            while (data.next()) {
                T object = model.newInstance();
                // loop through all cells using column details
                for (Map.Entry cell : columns.entrySet()) {
                    if(cell.getValue() == "TEXT" || cell.getValue() == "VARCHAR") {
                        object.setAttr((String)cell.getKey(), data.getString((String)cell.getKey()));
                    } else if(cell.getValue() == "DATE" || cell.getValue() == "DATETIME") {
                        object.setAttr((String)cell.getKey(), data.getDate((String)cell.getKey()));
                    } else if(cell.getValue() == "INT" || 
                              cell.getValue() == "TINYINT" || 
                              cell.getValue() == "SMALLINT" ||
                              cell.getValue() == "MEDIUMINT" ||
                              cell.getValue() == "BIGINT") {
                        object.setAttr((String)cell.getKey(), data.getInt((String)cell.getKey()));
                    } else if(cell.getValue() == "FLOAT") {
                        object.setAttr((String)cell.getKey(), data.getFloat((String)cell.getKey()));
                    } else if(cell.getValue() == "DOUBLE") {
                        object.setAttr((String)cell.getKey(), data.getDouble((String)cell.getKey()));
                    } else if(cell.getValue() == "TIME") {
                        object.setAttr((String)cell.getKey(), data.getTime((String)cell.getKey()));
                    }
                }
                result.add(object);
            }
        } catch (UnsupportedDataTypeException e) {
            throw new UnsupportedDataTypeException("UnsupportedDataTypeException");
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
        return result;
    }

    
    public static int sqlUpdate(String query, List args) throws UnsupportedDataTypeException{
        int result = 0;
        try {
            Connection cnx = DataBaseManager.getInstance().getConnection();
            PreparedStatement statement = cnx.prepareStatement(query);
            statement = setPerparedStatementArgs(statement, args);
            result = statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error Executing query");
        }
        return result;
    }

}