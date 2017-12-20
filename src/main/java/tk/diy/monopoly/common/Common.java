
package tk.diy.monopoly.common;

import java.util.HashMap;

import tk.diy.monopoly.common.Player;

public class Common {
    public final static String VERSION = "0.1.0"; 
    public final static short DEFAULT_PORT = 1935; 
    public final static int DEFAULT_THREADS = 10; 
    public final static String DEFAULT_HOST = "localhost"; 

    public final static int FIELD_COUNT = 40; 

    protected HashMap<Player.Color, Player> players;
    protected Board board;
    protected boolean started;

    public Common() {
        this.players = new HashMap<Player.Color, Player>();
        this.board = new Board();
        this.started = false;
    }

    public static void error(int code) {
        System.err.println("Error");
        System.exit(code);
    }

    public static void error(int code, String msg) {
        System.err.print("Error: ");
        System.err.println(msg);
        System.exit(code);
    }

    public static void error(int code, String msg, Exception cause) {
        System.err.print("Error: ");
        System.err.println(msg);
        cause.printStackTrace();
        System.exit(code);
    }

    public static void error(int code, String msg, Object cause) {
        System.err.print("Error: ");
        System.err.println(msg);
        System.err.println(cause);
        System.exit(code);
    }
}
