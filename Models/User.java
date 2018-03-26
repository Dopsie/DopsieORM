package Models;

import Core.ORM.*;
import Helpers.Exceptions.*;
import java.util.ArrayList;

/**
 * User Model
 */
public class User extends Model {

    public int id;

    @Override
    public String getTableName() {
        return "personne";
    }

    @Override
    public String getPrimaryKeyName() {
        return "id";
    }

    public ArrayList<Post> posts() throws ModelException{
        try {
            return this.hasMany(Post.class);
        } catch (Exception e) {
            throw new ModelException("Could not find relationship");
        }
    }
}