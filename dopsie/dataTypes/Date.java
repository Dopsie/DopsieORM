package dopsie.dataTypes;

/**
 * Date
 */
public class Date extends java.sql.Date{
    public Date(long date) {
        super(date);
    }

    public Date() {
        super((new java.util.Date()).getTime());
    }
}