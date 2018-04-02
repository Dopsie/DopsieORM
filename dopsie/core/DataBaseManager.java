package dopsie.core;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * DataBaseManager
 */
public class DataBaseManager {
    private static DataBaseManager instance = null ;
    private String url ;
    private Connection cnx ;
    public DataBaseManager() {
        String host = System.getProperty("host");
        String port = System.getProperty("port");
        String database = System.getProperty("database");
        String user = System.getProperty("user");
        String password = System.getProperty("password");
        try {
            url =  "jdbc:mysql://" + host + ":"+ port +"/" + database ;
            cnx = DriverManager.getConnection(url, user, password);
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