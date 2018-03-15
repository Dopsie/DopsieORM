package Core.ORM;

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
    public static void sql(String query) {
        
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
}