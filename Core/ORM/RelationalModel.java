package Core.ORM;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public static Object sql(String query) {
        Object result = null;
        try {
            Connection cnx = DataBaseManager.getInstance().getConnection();
            result = cnx.createStatement().executeQuery(query);
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