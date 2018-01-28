
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

    public Handler(Server host, boolean root, Socket conn, int resendLimit) {
        this.host = host;
        this.conn = conn;
        this.resendLimit = resendLimit;
        this.root = root;

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
            return this.protocol.recv(false, false, null, this.host.getState());
        } else {
            return this.protocol.recv(this.root, this.host.playerColor() == this.self.color, this.host.playerColor(), this.host.getState());
        }
    }

    private String name() {
        if (this.self != null) {
            return this.self.color.toName();
        } else {
            return this.conn.getInetAddress().toString();
        }
    }

    public void run() {
        try {
            while (true) {
                Request req = this.recv();

                if (req instanceof Request.Echo) {
                    this.send(new Request.EchoResponse(((Request.Echo) req).message));
                    this.host.log(this.name(), ((Request.Echo) req).message);
                } else if (req instanceof Request.Disconnect) {
                    if (this.self != null) {
                        this.host.remove(this.self.color);
                    }
                    this.host.log(this.name(), "* disconnected *");
                    break;
                } else if (req instanceof Request.Shutdown) {
                    this.protocol.close();
                    this.conn.close();
                    this.host.shutdown();
                    this.host.log(this.name(), "* shutdown *");
                    break;
                // game state elements start here
                } else if (req instanceof Request.Join) {
                    this.host.join(((Request.Join) req).color);
                    this.self = this.host.player(((Request.Join) req).color);
                    this.send(new Request.JoinResponse(((Request.Join) req).color, true));
                    this.host.log(this.name(), "* joined *");
                } else if (req instanceof Request.Start) {
                    boolean hasPlayers = this.host.debug && this.host.playerCount() > 0
                                     || !this.host.debug && this.host.playerCount() > Common.MIN_PLAYERS;
                    if (hasPlayers) {
                        this.host.start();
                        this.send(new Request.StartResponse(true));
                    } else {
                        this.send(new Request.StartResponse(false));
                    }
                    this.host.log(this.name(), "* starting game *");
                    this.host.log("* starting game *");
                } else if (req instanceof Request.End) {
                    this.host.end();
                    this.send(new Request.EndResponse(true));
                    this.host.log(this.name(), "* ending game *");
                    this.host.log("* ending game *");
                } else if (req instanceof Request.Move) {
                    int count = Server.dice.rand();
                    this.self.move(count);
                    this.send(new Request.MoveResponse(count));
                    this.host.log(this.name(), "* moving by " + count + " steps *");
                } else {
                    throw new Exception("unimplemented request");
                }
            }
        } catch (IOException e) {
            Server.warn("io", e);
        } catch (Exception e) {
            Server.warn("unknown error", e);
        }
    }
}
