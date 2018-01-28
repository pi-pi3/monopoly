
package tk.diy.monopoly.server;

import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import tk.diy.monopoly.Game.Options;
import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.common.Player;
import tk.diy.monopoly.server.Handler;

public class Server extends Common implements Runnable {
    public short port;
    public String host;
    public int threads;
    public int resendLimit;
    public final boolean debug;

    private ServerSocket socket;
    private ExecutorService pool;
    private boolean shouldShutdown;
    private boolean isRunning;

    public Server(Options opts) {
        super();
        this.port = opts.port;
        this.host = opts.host;
        this.threads = opts.threads;
        this.shouldShutdown = false;
        this.isRunning = false;
        this.resendLimit = opts.resendLimit;
        this.debug = opts.debug;
    }

    public synchronized void shutdown() {
        if (this.shouldShutdown) {
            return;
        }

        this.log("* server shutting down *");

        this.shouldShutdown = true;
        try {
            this.socket.close();
        } catch (IOException e) {
            Server.error(1, "io error", e);
        }
    }

    private void terminate() {
        this.log("* server terminating *");

        this.pool.shutdown();

        try {
            if (!this.pool.awaitTermination(60, TimeUnit.SECONDS)) {
                this.pool.shutdownNow();
                if (!this.pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Error: pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            this.pool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        try {
            this.socket.close();
        } catch (IOException e) {
            Server.error(1, "io error", e);
        }

        this.isRunning = false;
    }

    public void run() {
        synchronized (this) {
            if (this.isRunning) {
                Server.warn("cannot run server twice");
                return;
            }
            this.isRunning = true;
        }
        short port = this.port;
        String host = this.host;
        int threads = this.threads;
        int resendLimit = this.resendLimit;

        InetAddress addr = null;

        try {
            addr = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            Server.error(1, "unknown host", e);
        }

        try {
            this.socket = new ServerSocket(port, 0, addr);
        } catch (IOException e) {
            Server.error(1, "io error", e);
        }

        this.log("* starting socket on " + host + ":" + port + " *");

        this.pool = Executors.newFixedThreadPool(threads);

        this.log("* running with " + threads + " threads *");

        try {
            while (true) {
                Socket conn = this.socket.accept();
                boolean isRoot = conn.getInetAddress().equals(addr);
                if (isRoot) {
                    this.log(conn.getInetAddress().toString(), "* connected as root *");
                } else {
                    this.log(conn.getInetAddress().toString(), "* connected *");
                }
                this.pool.execute(new Handler(this, isRoot, conn, resendLimit));
            }
        } catch (IOException e) {
            if (!this.shouldShutdown) {
                Server.error(1, "io error", e);
            }
        }

        this.terminate();

        this.log("* fin *");
    }

    public void log(String msg) {
        this.log("host", msg);
    }

    public void log(Player.Color color, String msg) {
        this.log(color.toName(), msg);
    }

    public void log(InetAddress addr, String msg) {
        this.log(addr.toString(), msg);
    }

    public void log(String name, String msg) {
        Date now = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        String logline = MessageFormat.format("[{0}] <{1}>: {2}", fmt.format(now), name, msg);
        System.out.println(logline);
    }

    public static void error(int code) {
        Common.error(code);
    }

    public static void error(int code, String msg) {
        Common.error(code, msg);
    }

    public static void error(int code, String msg, Exception cause) {
        Common.error(code, msg, cause);
    }

    public static void error(int code, String msg, Object cause) {
        Common.error(code, msg, cause);
    }

    public static void warn(String msg) {
        Common.warn(msg);
    }

    public static void warn(String msg, Exception cause) {
        Common.warn(msg, cause);
    }

    public static void warn(String msg, Object cause) {
        Common.warn(msg, cause);
    }
}
