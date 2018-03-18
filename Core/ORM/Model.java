package Core.ORM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.lang.Class;
import java.lang.reflect.*;
import Helpers.Exceptions.*;
import Models.*;

/**
 * Model
 */
public class Model extends RelationalModel {

    private ArrayList<ArrayList<String>> conditionStack;
    private HashMap<String, Object> attributes;

    public Model(String primaryKey) {
        super(primaryKey);
        conditionStack = new ArrayList<ArrayList<String>>();
        attributes = new HashMap<String, Object>();
    }

    public Model() {
        super("Id");
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

    public static <T> T find(Class<T> theClass, int id) throws ModelException {
        try {
            return theClass.cast(theClass.newInstance());
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    public void execute() {
        System.out.println("table = " + this.getTableName());
        this.conditionStack.stream().forEach(System.out::println);
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
            String queryString = "UPDATE " + 
                                this.getTableName() + 
                                " SET " + 
                                String.join(",", args) + 
                                " WHERE " + 
                                this.primaryKey + 
                                " = " + 
                                this.getAttr( this.primaryKey);
            System.out.println(queryString);
            this.attributes.values().forEach(System.out::println);
            RelationalModel.sqlUpdate(queryString, new ArrayList(this.attributes.values()));
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    };

    public void insert() {
        try {
            String columnsNames = String.join(",", this.attributes.keySet());
            String[] wildcards = new String[this.attributes.size()];
            Arrays.fill(wildcards,"?");
            String wildcardsString = String.join(",", Arrays.asList(wildcards));
            String queryString = "INSERT INTO " + 
                                this.getTableName() + 
                                " (" + 
                                columnsNames + 
                                ") VALUES (" +
                                wildcardsString +
                                ")";
            System.out.println(queryString);
            this.attributes.values().forEach(System.out::println);
            RelationalModel.sqlUpdate(queryString, new ArrayList(this.attributes.values()));
        } catch(Exception e) {
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

}