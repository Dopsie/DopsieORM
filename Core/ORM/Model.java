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
    // ConditionStack containing the list of conditions from the built query.
    private ArrayList<ArrayList<String>> conditionStack;

    /**
     * Initializes the newly created Model instance : 
     *  setting the state of instance to new. (To be inserted into the database later)
     *  initializes the conditionStack and the attributes with empty lists.
     */
    public Model() {
        super();
        this.isNew = true;
        conditionStack = new ArrayList<ArrayList<String>>();
        attributes = new HashMap<String, Object>();
    }
    /**
     * @return A hashmap of attributes name => value 
     */
    public HashMap<String, Object> getAllAttributes() {
        return this.attributes;
    }

    /**
     * Get the value of an attribute by name.
     * @param columnName the name of the attribute.
     * @return Object containing the value of the requested attribute.
     */
    public Object getAttr(String columnName) {
        return this.attributes.get(columnName);
    }

    /**
     * Set value of an attribute
     * @param columnName the name of the attribute to be updated or added.
     * @param value the value of the attribute
     */
    public void setAttr(String columnName, Object value) {
        this.isModified = true;
        this.attributes.put(columnName, value);
    }

    /**
     * Add a where condition to the conditionStack.
     * @param arg1 The name of the column that will be matched
     * @param arg2 The searched value.
     * @param operator The comparaison operator.

     */
    public Model where(String arg1, String operator, String arg2) {
        ArrayList<String> condition = new ArrayList<String>(Arrays.asList(arg1, operator, arg2));
        conditionStack.add(condition);
        return this;
    }

    /**
     * The Model to be fetched.
     */
    public static <T> T fetch(Class<T> theClass) throws ModelException {
        try {
            return theClass.cast(theClass.newInstance());
        } catch (Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }
    /**
     * Get the model as it is to start building the query. 
     */
    public Model all() {
        return this;
    }

    /**
     * Find an entry by primaryKey 
     * @param theClass The Model class from where the data will be fetched.
     * @param id the value of the primaryKey 
     */
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

    /**
     * Build the query from the condition stack.
     */
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

    @Override
    public String toString() {
        String returnedValue = "=============================\n";
        for (Map.Entry var : this.attributes.entrySet()) {
            returnedValue += (var.getKey() + " : " + var.getValue() + '\n');
        }
        return returnedValue;
    }
}