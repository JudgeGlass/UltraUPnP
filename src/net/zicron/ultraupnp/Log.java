package net.zicron.ultraupnp;

public class Log {
    public static void info(String message){
        System.out.println("[UltraUPNP][INFO] " + message);
    }

    public static void warn(String message){
        System.out.println("[UltraUPNP][WARN] " + message);
    }

    public static void error(String message){
        System.err.println("[UltraUPNP][ERROR] " + message);
    }
}
