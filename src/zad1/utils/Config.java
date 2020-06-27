package zad1.utils;

public class Config {

    public static final int MAIN_SERVER_PORT = 5704;

    public static final String MAIN_SERVER_HOSTNAME = "localhost";

    private Config() {

    }

    public static void log(String msg) {
        System.out.println(msg);
    }

}
