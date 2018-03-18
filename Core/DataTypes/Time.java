package Core.DataTypes;

/**
 * Time
 */
public class Time extends java.sql.Time{
    public Time(long time) {
        super(time);
    }

    public Time() {
        super((new java.util.Date()).getTime());
    }
}