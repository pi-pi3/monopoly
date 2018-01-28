
package tk.diy.monopoly.server;

import java.net.*;
import java.io.*;

import tk.diy.monopoly.server.Server;
import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.common.Request;
import tk.diy.monopoly.common.Protocol;
import tk.diy.monopoly.common.Player;

public class Handler implements Runnable {
    private final Server host;
    private final Socket conn;
    private final int resendLimit;

    private Protocol protocol;

    private Player self;
    private boolean root;

    public Handler(Server host, Socket conn, int resendLimit) {
        this.host = host;
        this.conn = conn;
        this.resendLimit = resendLimit;
        this.root = false; // TODO

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
            DataOutputStream out = new DataOutputStream(this.conn.getOutputStream());
            this.protocol = new Protocol(in, out);
        } catch (IOException e) {
            Server.error(1, "io", e);
        }
    }

    private Request send(Request request) throws IOException, Exception {
        return this.protocol.send(request);
    }

    private Request recv() throws IOException, Exception {
        // TODO: do this in a prettier way
        if (this.self == null) {
            return this.protocol.recv(false, false, null);
        } else {
            return this.protocol.recv(this.root, this.host.currentPlayer() == this.self.color, this.host.currentPlayer());
        }
    }

    public void run() {
        try {
            while (true) {
                Request req = this.recv();

                if (req instanceof Request.Echo) {
                    this.send(new Request.EchoResponse(((Request.Echo) req).message));
                } else if (req instanceof Request.Disconnect) {
                    if (this.self != null) {
                        this.host.remove(this.self.color);
                    }
                    break;
                } else if (req instanceof Request.Shutdown) {
                    this.protocol.close();
                    this.conn.close();
                    this.host.shutdown();
                    break;
                // game state elements start here
                } else if (req instanceof Request.Join) {
                    if (!this.host.hasStarted()) {
                        this.host.join(((Request.Join) req).color);
                        this.send(new Request.JoinResponse(((Request.Join) req).color, true));
                        this.self = this.host.player(((Request.Join) req).color);
                    } else {
                        // TODO: more elaborate fail message
                        this.send(new Request.JoinResponse(((Request.Join) req).color, false));
                    }
                } else if (req instanceof Request.Move) {
                    // TODO: game started validation
                    int count = Server.dice.rand();
                    this.self.move(count);
                    this.send(new Request.MoveResponse(count));
                }
            }
        } catch (IOException e) {
            Server.error(1, "io", e);
        } catch (Exception e) {
            Server.error(1, "unknown error", e);
        }
    }
}
