package common;

import io.github.cdimascio.dotenv.Dotenv;


public class Settings {
    private static final Dotenv dotenv = Dotenv.load();

    public static final String POSTGRES_USER = dotenv.get("POSTGRES_USER");
    public static final String POSTGRES_PASSWORD = dotenv.get("POSTGRES_PASSWORD");
    public static final String POSTGRES_SERVER = dotenv.get("POSTGRES_SERVER");
    public static final String POSTGRES_PORT = dotenv.get("POSTGRES_PORT");

    public static String getDatabaseUrl(String dbName) {
        return String.format(
                "jdbc:postgresql://%s:%s/%s?user=%s&password=%s",
                POSTGRES_SERVER,
                POSTGRES_PORT,
                dbName,
                POSTGRES_USER,
                POSTGRES_PASSWORD
        );
    }
}
