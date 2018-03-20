package Core.ORM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.lang.Class;
import java.lang.reflect.*;
import Helpers.Exceptions.*;
import Models.*;
import java.util.Map;

/**
 * Model
 */
public class Model extends RelationalModel {

    private ArrayList<ArrayList<String>> conditionStack;
    protected HashMap<String, Object> attributes;

    public Model() {
        super();
        conditionStack = new ArrayList<ArrayList<String>>();
        attributes = new HashMap<String, Object>();
    }

    public HashMap<String, Object> getAllAttributes() {
        return this.attributes;
    }

    public Object getAttr(String columnName) {
        return this.attributes.get(columnName);
    }

    public void setAttr(String columnName, Object value) {
        this.isModified = true;
        this.attributes.put(columnName, value);
    }

    public Model where(String arg1, String operator, String arg2) {
        ArrayList<String> condition = new ArrayList<String>(Arrays.asList(arg1, operator, arg2));
        conditionStack.add(condition);
        return this;
    }

    public static <T> T fetch(Class<T> theClass) throws ModelException {
        try {
            return theClass.cast(theClass.newInstance());
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    public Model all() {
        return this;
    }

    public static <T extends Model> T find(Class<T> theClass, int id) throws ModelException {
        try {
            // Model instanciated in order to get primary Key's column name
            T object = theClass.cast(theClass.newInstance());
            return theClass.cast(Model.fetch(theClass).all()
                    .where(object.getPrimaryKeyName(), "=", Integer.toString(id)).execute().get(0));
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    // retrieving data from a table 
    public ArrayList execute() throws UnsupportedDataTypeException, ModelException {
        ArrayList args = new ArrayList();
        String query = "SELECT * FROM " + this.getTableName();

        if (!conditionStack.isEmpty()) {
            query += " WHERE ";
            ArrayList<String> queryConditions = new ArrayList<String>();
            // Parsing conditions and building the condition part in the sql query
            for (ArrayList<String> conditionStatment : conditionStack) {
                queryConditions.add(conditionStatment.get(0) + " " + conditionStatment.get(1) + " ? ");
                args.add(conditionStatment.get(2));
            }
            query += String.join(",", queryConditions);
        }
        query += ";";
        return sqlQuery(query, args, this.getClass());
    }

    public String getTableName() {
        return this.getClass().getSimpleName();
    }

    public void update() {
        try {
            String columnsNames = String.join(",", this.attributes.keySet());
            ArrayList<String> args = new ArrayList<String>();
            for (Map.Entry<String, Object> attr : this.attributes.entrySet()) {
                args.add(" " + attr.getKey() + " = ?");
            }
            String queryString = "UPDATE " + this.getTableName() + " SET " + String.join(",", args) + " WHERE "
                    + this.primaryKey + " = " + this.getAttr(this.primaryKey);
            System.out.println(queryString);
            this.attributes.values().forEach(System.out::println);
            RelationalModel.sqlUpdate(queryString, new ArrayList(this.attributes.values()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert() {
        try {
            String columnsNames = String.join(",", this.attributes.keySet());
            String[] wildcards = new String[this.attributes.size()];
            Arrays.fill(wildcards, "?");
            String wildcardsString = String.join(",", Arrays.asList(wildcards));
            String queryString = "INSERT INTO " + this.getTableName() + " (" + columnsNames + ") VALUES ("
                    + wildcardsString + ")";
            System.out.println(queryString);
            this.attributes.values().forEach(System.out::println);
            RelationalModel.sqlUpdate(queryString, new ArrayList(this.attributes.values()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void save() {
        if (this.isNew()) {
            this.insert();
            this.isNew = false;

        } else {
            this.update();
        }
        this.isModified = false;
    }

    @Override
    public String toString() {
        String returnedValue = "=============================\n";
        for (Map.Entry var : this.attributes.entrySet()) {
            returnedValue += (var.getKey() + " : " + var.getValue() + '\n');
        }
        return returnedValue;
    }

    protected <T extends Model> ArrayList<T> hasMany(Class<T> clazz, String foreignKey) throws ModelException {
        try {
            int primaryKey = (int) this.getAttr(this.getPrimaryKeyName());
            return Model.fetch(clazz).all().where(foreignKey, "=", Integer.toString(primaryKey)).execute();
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> ArrayList<T> hasMany(Class<T> clazz) throws ModelException {
        try {
            return hasMany(clazz, this.getClass().getSimpleName() + "_id");
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> T hasOne(Class<T> clazz) throws ModelException {
        try {
            return this.hasOne(clazz, clazz.getSimpleName() + "_id");
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> T hasOne(Class<T> clazz, String foreignKey) throws ModelException {
        try {
            return Model.find(clazz, (int) this.getAttr(foreignKey));
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> T belongsTo(Class<T> clazz) throws ModelException {
        try {
            return belongsTo(clazz, this.getClass().getSimpleName() + "_id");
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> T belongsTo(Class<T> clazz, String foreignKey) throws ModelException {
        try {
            int primaryKey = (int) this.getAttr(this.getPrimaryKeyName());
            return clazz.cast(
                    Model.fetch(clazz).all().where(foreignKey, "=", Integer.toString(primaryKey)).execute().get(0));
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> ArrayList<T> belongsToMany(Class<T> clazz) throws ModelException {
        try {
            return belongsToMany(clazz, this.getClass().getSimpleName() + "_id");
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    protected <T extends Model> ArrayList<T> belongsToMany(Class<T> clazz, String foreignKey) throws ModelException {
        try {
            int primaryKey = (int) this.getAttr(this.getPrimaryKeyName());
            return Model.fetch(clazz).all().where(foreignKey, "=", Integer.toString(primaryKey)).execute();
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }
}