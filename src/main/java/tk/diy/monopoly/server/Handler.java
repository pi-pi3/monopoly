
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

    public Handler(Server host, Socket conn, int resendLimit) {
        this.host = host;
        this.conn = conn;
        this.resendLimit = resendLimit;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
            DataOutputStream out = new DataOutputStream(this.conn.getOutputStream());
            this.protocol = new Protocol(in, out);
        } catch (IOException e) {
            Server.error(1, "io", e);
        }
    }

    private void send(Request request) throws IOException, Exception {
        this.protocol.send(request);
    }

    private Request recv() throws IOException, Exception {
        return this.protocol.recv();
    }

    public void run() {
        try {
            while (true) {
                Request req = this.recv();

                if (req instanceof Request.Echo) {
                    this.send(new Request.EchoResponse(((Request.Echo) req).message));
                } else if (req instanceof Request.Disconnect) {
                    this.host.remove(this.self.color);
                    break;
                } else if (req instanceof Request.Shutdown) {
                    this.protocol.close();
                    this.conn.close();
                    this.host.shutdown();
                    break;
                // game state elements start here
                } else if (req instanceof Request.Join) {
                    // if running in debug mode, allow one player
                    boolean hasPlayers = this.host.debug && this.host.playerCount() >= Common.MIN_PLAYERS
                                     || !this.host.debug && this.host.playerCount() > 0;
                    if (!this.host.hasStarted() && hasPlayers) {
                        this.host.join(((Request.Join) req).color);
                        this.send(new Request.JoinResponse(((Request.Join) req).color, true));
                        this.self = this.host.player(((Request.Join) req).color);
                    } else {
                        // TODO: more elaborate fail message
                        this.send(new Request.JoinResponse(((Request.Join) req).color, false));
                    }
                }
            }
        } catch (IOException e) {
            Server.error(1, "io", e);
        } catch (Exception e) {
            Server.error(1, "unknown error", e);
        }
    }
}
