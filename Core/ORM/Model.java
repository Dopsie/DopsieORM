package Core.ORM;

import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Class;
import java.lang.reflect.*;
import Helpers.Exceptions.*;
import Models.*;

/**
 * Model
 */
public class Model extends RelationalModel {

    private ArrayList<ArrayList<String>> conditionStack;

    public Model(String primaryKey) {
        super(primaryKey);
        conditionStack = new ArrayList<ArrayList<String>>();
    }

    public Model() {
        super("Id");
        conditionStack = new ArrayList<ArrayList<String>>();
    }

    public ArrayList<Field> getAttributes() {
        Field[] fields = getClass().getFields();
        for (Field var : fields) {
            System.out.println(var.getName());
            System.out.println(var.getType());
        }
        return null;
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
}