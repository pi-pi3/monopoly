
package tk.diy.monopoly.server;

import java.net.*;
import java.io.*;

import tk.diy.monopoly.server.Server;
import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.common.Request;
import tk.diy.monopoly.common.Protocol;
import tk.diy.monopoly.common.Protocol.ProtocolRequest;
import tk.diy.monopoly.common.Player;
import tk.diy.monopoly.common.field.Field;
import tk.diy.monopoly.common.building.Building;

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

    private ProtocolRequest recv() throws IOException, Exception {
        if (this.host.hasStarted()) {
            return this.protocol.recv(this.root, this.self.color, this.host.playerColor(), this.host.getState()); // XXX that updates these variables
        } else {
            return this.protocol.recv(this.root);
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
            outer:
            while (true) {
                ProtocolRequest tmp = this.recv();
                Request req = tmp.req;
                Request ans = tmp.ans;

                if (req instanceof Request.Ask) {
                    continue;
                }

                if (ans instanceof Request.Acknowledge) {
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
                        this.host.log(this.name(), "* shutdown *");
                        this.host.shutdown();
                        break;
                    // game state elements start here
                    } else if (req instanceof Request.Join) {
                        Player.Color color = ((Request.Join) req).color;
                        if (!this.host.hasPlayer(color)) {
                            this.host.join(color);
                            this.self = this.host.player(color);
                            this.send(new Request.JoinResponse(color, true));
                            this.host.log(this.name(), "* joined *");
                        } else {
                            this.send(new Request.JoinResponse(color, false));
                            this.host.log(this.name(), "* <" + color.toName() + "> is already in game *");
                        }
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

                        int position = this.self.position();
                        Field field = this.host.getBoard().get(position);
                        this.host.log(this.name(), "* now on \"" + field.name() + "\" *");
                        switch (field.visit(this.self)) {
                            case VISITING:
                                this.host.log(this.name(), "* visiting *");
                                break;
                            case INCOME:
                                this.host.log(this.name(), "* income *");
                                break;
                            case PAYED:
                                this.host.log(this.name(), "* payed *");
                                break;
                            case BANKRUPT:
                                this.host.log(this.name(), "* bankrupt *");
                                // TODO
                                break outer;
                            case CANBUY:
                                Building building = field.getBuilding();
                                int cost = building.cost();
                                this.host.log(this.name(), "* canbuy *");
                                Request.Buy buy = (Request.Buy) this.recv().req;
                                if (buy.buy) {
                                    this.self.buy(building);
                                    this.host.log(this.name(), "* bought \"" + building.name() + "\" for " + cost + "€ *");
                                }
                                break;
                            case CANBUILD:
                                this.host.log(this.name(), "* canbuild *");
                                break;
                            case IN_JAIL:
                                this.host.log(this.name(), "* in_jail *");
                                break;
                            case GOTO_JAIL:
                                this.host.log(this.name(), "* goto_jail *");
                                break;
                        }

                        this.host.nextPlayer();
                    } else {
                        throw new Exception("unimplemented request");
                    }
                } else if (ans instanceof Request.AccessDenied) {
                    this.host.log(this.name(), "* access denied *");
                } else if (ans instanceof Request.NotYourTurn) {
                    this.host.log(this.name(), "* not your turn *");
                } else if (ans instanceof Request.GameStarted) {
                    this.host.log(this.name(), "* game has already started *");
                } else if (ans instanceof Request.GameNotStarted) {
                    this.host.log(this.name(), "* game has not started yet *");
                } else {
                    throw new Exception("unimplemented request");
                }
            }
        } catch (IOException e) {
            Server.warn("io", e);
        } catch (Exception e) {
            Server.warn("unknown error", e);
        }

        if (this.self != null) {
            try {
                this.host.remove(this.self.color);
            } catch (Exception _e) {
                // unreachable
                _e.printStackTrace();
            }
        }
    }
}
