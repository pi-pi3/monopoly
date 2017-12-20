
package tk.diy.monopoly.server;

import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import tk.diy.monopoly.Game.Options;
import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.server.Handler;

public class Server extends Common implements Runnable {
    public short port;
    public String host;
    public int threads;

    private ServerSocket socket;
    private ExecutorService pool;
    private boolean shouldShutdown;

    public Server(Options opts) {
        super();
        this.port = opts.port;
        this.host = opts.host;
        this.threads = opts.threads;
        this.shouldShutdown = false;
    }

    public synchronized void shutdown() {
        if (this.shouldShutdown) {
            return;
        }

        this.shouldShutdown = true;
        try {
            this.socket.close();
        } catch (IOException e) {
            Server.error(1, "io error", e);
        }
    }

    private void terminate() {
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
    }

    public void run() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(this.host);
        } catch (UnknownHostException e) {
            Server.error(1, "unknown host", e);
        }

        try {
            this.socket = new ServerSocket(this.port, 0, addr);
        } catch (IOException e) {
            Server.error(1, "io error", e);
        }

        this.pool = Executors.newFixedThreadPool(this.threads);

        try {
            while (true) {
                Socket conn = this.socket.accept();
                this.pool.execute(new Handler(this, conn));
            }
        } catch (IOException e) {
            if (!this.shouldShutdown) {
                Server.error(1, "io error", e);
            }
        }

        this.terminate();
    }
}
