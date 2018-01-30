
package tk.diy.monopoly.common;

import java.util.HashMap;
import java.util.ArrayList;

import tk.diy.monopoly.common.Player;
import tk.diy.monopoly.common.rand.Dice;

public class Common {
    public final static String VERSION = "0.1.0";
    public final static short DEFAULT_PORT = 1935;
    public final static int DEFAULT_THREADS = 10;
    public final static String DEFAULT_HOST = "localhost";
    public final static int DEFAULT_RESEND_LIMIT = 4;

    public final static int MIN_PLAYERS = 2;
    public final static int FIELD_COUNT = 40; 
    public final static int BONUS_CASH = 2000;
    public final static int BASE_CASH = 4000;
    public final static int INIT_CASH = BASE_CASH + BONUS_CASH;
    public final static int JAIL_FIELD = 10;
    public final static int JAIL_SENTENCE = 3;
    public final static Dice dice = new Dice(2, 6); // 2d6

    protected HashMap<Player.Color, Player> players;
    protected ArrayList<Player.Color> playerOrder;
    protected int currentPlayer;
    protected Board board;
    protected GameState state;
    protected int cashPool;

    protected Common() {
        this.players = new HashMap<Player.Color, Player>();
        this.playerOrder = new ArrayList<Player.Color>();
        this.board = new Board();
        this.state = GameState.NOT_STARTED;
    }

    public synchronized Board getBoard() {
        return this.board;
    }

    public synchronized GameState getState() {
        return this.state;
    }

    public synchronized boolean hasStarted() {
        return this.state == GameState.STARTED;
    }

    public synchronized void join(Player.Color color) throws Exception {
        if (this.hasStarted()) {
            throw new Exception("game already started");
        }
        if (this.players.containsKey(color)) {
            throw new Exception("player already in game");
        }

        this.players.put(color, new Player(color, INIT_CASH));
        this.playerOrder.add(color);
    }

    public synchronized Player remove(Player.Color color) throws Exception {
        if (!this.players.containsKey(color)) {
            throw new Exception("no player of that color");
        }

        this.playerOrder.remove(color);
        return this.players.remove(color);
    }

    public synchronized void nextPlayer() {
        this.currentPlayer = (this.currentPlayer + 1) % this.playerCount();

        while (this.player().doJail()) {
            this.currentPlayer = (this.currentPlayer + 1) % this.playerCount();
        }
    }

    public synchronized boolean hasPlayer(Player.Color color) {
        return this.players.containsKey(color);
    }

    public synchronized Player.Color playerColor() {
        return this.playerOrder.get(this.currentPlayer);
    }

    public synchronized Player player() {
        return this.players.get(this.playerColor());
    }

    public synchronized Player player(Player.Color color) throws Exception {
        if (!this.players.containsKey(color)) {
            throw new Exception("no player of that color");
        }

        return this.players.get(color);
    }

    public synchronized int playerCount() {
        return this.players.size();
    }

    public synchronized void start() throws Exception {
        if (this.hasStarted()) {
            throw new Exception("game already started");
        }

        this.state = GameState.STARTED;
        this.currentPlayer = 0;
    }

    public synchronized void end() throws Exception {
        if (!this.hasStarted()) {
            throw new Exception("game already enden");
        }

        this.state = GameState.NOT_STARTED;
        this.players.clear();
        this.playerOrder.clear();
    }

    // errors
    protected static void error(int code) {
        System.err.println("Error");
        System.exit(code);
    }

    protected static void error(int code, String msg) {
        System.err.print("Error: ");
        System.err.println(msg);
        System.exit(code);
    }

    protected static void error(int code, String msg, Exception cause) {
        System.err.print("Error: ");
        System.err.println(msg);
        cause.printStackTrace();
        System.exit(code);
    }

    protected static void error(int code, String msg, Object cause) {
        System.err.print("Error: ");
        System.err.println(msg);
        System.err.println(cause);
        System.exit(code);
    }

    protected static void warn(String msg) {
        System.err.print("Warning: ");
        System.err.println(msg);
    }

    protected static void warn(String msg, Exception cause) {
        System.err.print("Warning: ");
        System.err.println(msg);
        cause.printStackTrace();
    }

    protected static void warn(String msg, Object cause) {
        System.err.print("Warning: ");
        System.err.println(msg);
        System.err.println(cause);
    }
}
