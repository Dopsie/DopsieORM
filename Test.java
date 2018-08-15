import dopsie.core.*;
import examples.*;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.*;
import dopsie.dataTypes.*;

/**
 * Test
 */
public class Test {

    public static void main(String[] args) {
        try {
            System.setProperty("host", "localhost");
            System.setProperty("port", "3306");
            System.setProperty("database", "esprit");
            System.setProperty("user", "root");
            System.setProperty("password", "root");

            
            ArrayList<User> x = Model.fetch(User.class).all()
                    .where("prenom", "=", "wassim")
                    .orderBy("prenom", "DESC")
                    .orderBy("nom", "ASC")
                    .execute();
            System.out.println(x);
            // User user = new User();
            // user.setAttr("nom", "Kallel");
            // user.setAttr("prenom", "Wassim");
            // user.setAttr("id", 150);
            // //user.save();
            // //System.out.println(user.testHasMany());
            // Post post = new Post();
            // post.setAttr("user_id", 150);
            // post.setAttr("id", 3);
            // //System.out.println(post.author());
            // //post.save();
            // //System.out.println(post.testHasOne());
            // System.out.println(user.post());
            // // user.setAttr("Id", 8);
            // user.setAttr("prenom", "mech Wassim");
            // user.save();
            // user.setAttr("nom", "Kallel");
            // user.setAttr("creation_date", new Core.DataTypes.Date());
            // System.out.println(user.getAllAttributes());
            // user.update();
            // ArrayList<User> data = Model.sqlQuery("Select * From personne where prenom = ?" ,
            //                                             Arrays.asList("wassim"), User.class);
            // for (User tmp : data) {
            //     System.out.println(tmp.getAttr("creation_date"));
            // }

            //ArrayList<User> data = Model.fetch(User.class).all().where("prenom", "=", "Wassim").execute();
             
            // for (User user : data) {
            //     System.out.println(user);
            // }
            // User u = Model.find(User.class, 9) ;
            // System.out.println(u.testManyToMany());
            // System.out.println(data);
            // while (data.next()) {
            //     System.out.println(data.getString(2));
            // }
            // System.out.println(Model.sqlUpdate("insert into personne (nom,prenom) values(?,?)", Arrays.asList("BenFoulen", "Foulen")));
            // System.out.println(Model.find(User.class, 2).posts());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
