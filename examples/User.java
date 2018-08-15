package examples;

import dopsie.core.*;
import dopsie.exceptions.*;
import java.util.ArrayList;

/**
 * User Model
 */
public class User extends Model {

    public int id;

    @Override
    public String getTableName() {
        return "user";
    }

    @Override
    public String getPrimaryKeyName() {
        return "id";
    }
}