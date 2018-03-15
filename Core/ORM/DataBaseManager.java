package Core.ORM;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import Config.*;
/**
 * DataBaseManager
 */
public class DataBaseManager {
    private static DataBaseManager instance = null ;
    private String url ;
    private Connection cnx ;
    public DataBaseManager() {
        try {
            url =  "jdbc:mysql://" + Defines.host + ":"+ Defines.port +"/" + Defines.dbName ;
            cnx = DriverManager.getConnection(url, Defines.user, Defines.password);
        }catch (SQLException ex) {
            System.out.println("Error connecting to Database");
        }
    }
    public static DataBaseManager getInstance () {
        if (instance == null ) {
            instance = new  DataBaseManager();
         }
        return instance; 
    }
    public Connection getConnection() {
        return this.cnx;
    }
}