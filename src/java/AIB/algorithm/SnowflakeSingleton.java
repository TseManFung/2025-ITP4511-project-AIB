package AIB.algorithm;

public class SnowflakeSingleton {

    private static class Holder {

        static final Snowflake INSTANCE = new Snowflake(1L, 1L);
    }

    private SnowflakeSingleton() {
    }

    public static Snowflake getInstance() {
        return Holder.INSTANCE;
    }
}
// use long id = SnowflakeSingleton.getInstance().nextId();
