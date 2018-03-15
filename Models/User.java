package Models;

import Core.ORM.*;

/**
 * Test
 */
public class User extends Model {

    public int id;

    @Override
    public String getTableName() {
        return "not a user";
    }
}