package Core.ORM;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import Helpers.Exceptions.*;

/**
 * RelationalModel
 */
public class RelationalModel {

    public static String tableName = "";
    protected String primaryKey = "Id";
    protected boolean isNew = false;

    public RelationalModel(String primaryKey) {
        this.primaryKey = primaryKey;
        this.isNew = true;
    }

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


    public DataBaseCollection update() {

        return null;
    };

    public void insert() {

    }

    public void save() {
        if (this.isNew()) {
            this.insert();
            this.isNew = false;
        } else
            this.update();
    }

    protected <T extends RelationalModel> ArrayList<T> hasMany(Class<T> clazz) throws ModelException {
        try {
            return new ArrayList<T>(Arrays.asList(clazz.cast(clazz.newInstance()), 
                                                  clazz.cast(clazz.newInstance()),
                                                  clazz.cast(clazz.newInstance())));
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends RelationalModel> T hasOne(Class<T> clazz) throws ModelException {
        try {
            return clazz.cast(clazz.newInstance());
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends RelationalModel> T belongsTo(Class<T> clazz) throws ModelException {
        try {
            return clazz.cast(clazz.newInstance());
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends RelationalModel> ArrayList<T> belongsToMany(Class<T> clazz) throws ModelException {
        try {
            return new ArrayList<T>(Arrays.asList(clazz.cast(clazz.newInstance()), 
                                                  clazz.cast(clazz.newInstance()),
                                                  clazz.cast(clazz.newInstance())));
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }
}