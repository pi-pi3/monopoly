
package tk.diy.monopoly.client;

import java.util.Optional;
import java.util.Scanner;
import java.util.ArrayList;
import java.net.*;
import java.io.*;

import tk.diy.monopoly.Game.Options;
import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.common.Request;
import tk.diy.monopoly.common.Player;
import tk.diy.monopoly.common.Protocol;
import tk.diy.monopoly.common.field.Field;
import tk.diy.monopoly.common.field.Field.Visit;
import tk.diy.monopoly.common.building.Building;

public class Client extends Common implements Runnable {
    public short port;
    public String host;
    public int resendLimit;

    private Socket socket;
    private Protocol protocol;

    private Player self;

    public Client(Options opts) {
        super();
        this.port = opts.port;
        this.host = opts.host;
        this.resendLimit = opts.resendLimit;
    }

    private Request send(Request request) throws IOException, Exception {
        return this.protocol.send(request);
    }

    private Request recv() throws IOException, Exception {
        return this.protocol.recv().req;
    }

    public void run() {
        short port = this.port;
        String host = this.host;
        int resendLimit = this.resendLimit;

        try {
            this.socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
            this.protocol = new Protocol(in, out, resendLimit);

            Shell sh = new Shell("> ", System.in, false);

            outer:
            while (!sh.quitEh()) {
                Request req = sh.nextRequest();

                if (req instanceof Request.Show) {
                    if (this.self != null) {
                        System.out.println(": " + this.self.color.toName());
                        System.out.println("Cash: " + this.self.getCash() + '€');
                        int position = this.self.position();
                        Field field = this.board.get(position);
                        System.out.println("Position: " + position + " \"" + field.name() + '"');
                        if (this.self.inJail()) {
                            System.out.println("You're now in jail for another " + this.self.getJail() + " rounds.");
                        }
                        ArrayList<Building> owned = this.self.getOwned();
                        System.out.println(" * Owned buildings");
                        for (int i = 0; i < owned.size(); i++) {
                            Building building = owned.get(i);
                            System.out.println(" #" + i + ": " + building.name());
                            System.out.println("  Current rent: " + building.rent() + '€');
                            int stage = building.getStage();
                            if (stage > 1 && stage < 6) {
                                System.out.println("  " + (stage - 1) + " Houses");
                                if (stage < 5) {
                                    System.out.println("  The next one costs " + building.cost() + '€');
                                } else {
                                    System.out.println("  A Hotel costs " + building.cost() + '€');
                                }
                            } else if (stage == 6) {
                                System.out.println("  1 Hotel");
                            } else {
                                System.out.println("  The first House costs " + building.cost() + '€');
                            }
                        }
                    } else {
                        Client.warn("Cannot do that. The game has not started yet.");
                    }
                    continue;
                }

                this.send(new Request.Ask()); // this is a hack that updates... look for XXX in Handler.java
                // basically now every communication attempt starts with an ask
                // request which updates the handler
                Request resp = this.send(req);

                if (resp instanceof Request.Acknowledge) {
                    if (req instanceof Request.Echo) {
                        Request.EchoResponse response = (Request.EchoResponse) this.recv();
                        long ping = this.protocol.getPing();
                        if (ping == 0) {
                            System.out.println("ping < 1ms");
                        } else {
                            System.out.println("ping: " + ping + "ms");
                        }
                        String msg = response.message;
                        if (msg != null && msg.length() > 0) {
                            System.out.println(msg);
                        }
                    } else if (req instanceof Request.Disconnect) {
                        if (this.self != null) {
                            this.remove(this.self.color);
                        }
                        break;
                    } else if (req instanceof Request.Shutdown) {
                        break;
                    // game state elements start here
                    } else if (req instanceof Request.Join) {
                        Request.JoinResponse response = (Request.JoinResponse) this.recv();
                        if (response.success) {
                            this.join(response.color);
                            this.self = this.player(((Request.Join) req).color);
                        } else {
                            Client.warn("Couldn't join game.");
                        }
                    } else if (req instanceof Request.Start) {
                        Request.StartResponse response = (Request.StartResponse) this.recv();
                        if (response.success) {
                            this.start();
                        } else {
                            Client.warn("Couldn't start game. Are enough players in game?");
                        }
                    } else if (req instanceof Request.End) {
                        Request.EndResponse response = (Request.EndResponse) this.recv();
                        if (response.success) {
                            this.end();
                        } else {
                            Client.warn("Couldn't end game.");
                        }
                    } else if (req instanceof Request.Move) {
                        Request.MoveResponse response = (Request.MoveResponse) this.recv();
                        int count = response.count;
                        Client.say("You rolled " + count);
                        this.self.move(count);

                        int position = this.self.position();
                        Field field = this.board.get(position);
                        Client.say("You're on the \"" + field.name() + "\" field");
                        switch (field.visit(this.self)) {
                            case VISITING:
                                Client.say("You pass through, unable to do a thing.");
                                break;
                            case INCOME:
                                Client.say("Bonus income!");
                                break;
                            case PAYED:
                                Client.say("You payed rent.");
                                break;
                            case BANKRUPT:
                                Client.say("You payed rent and went bankrupt!");
                                break outer;
                            case CANBUY:
                                Building building = field.getBuilding();
                                int cost = building.cost();
                                Client.say("Would you like to buy this building? [y/N]");
                                Client.say("Its cost is " + cost + "€");
                                boolean buy = sh.ask();
                                this.send(new Request.Buy(buy));
                                if (buy) {
                                    this.self.buy(building);
                                    Client.say("You bought \"" + building.name() + "\" for " + cost + "€");
                                }
                                break;
                            case CANBUILD:
                                Client.say("Would you like to upgrade this building? [y/N]");
                                break;
                            case IN_JAIL:
                                Client.say("You're still in jail. Please wait.");
                                break;
                            case GOTO_JAIL:
                                Client.say("For your crimes you are sent to jail for 3 rounds!");
                                break;
                        }

                        this.nextPlayer();
                    } else {
                        throw new Exception("unimplemented request");
                    }
                } else if (resp instanceof Request.AccessDenied) {
                    Client.warn("You do not have permission to do that.");
                } else if (resp instanceof Request.NotYourTurn) {
                    Client.warn("It's not your turn now. Please wait.");
                } else if (resp instanceof Request.GameStarted) {
                    Client.warn("Cannot do that. The game has already started.");
                } else if (resp instanceof Request.GameNotStarted) {
                    Client.warn("Cannot do that. The game has not started yet.");
                } else {
                    throw new Exception("unimplemented request");
                }
            }

            sh.quit();
            this.protocol.close();
            this.socket.close();
        } catch (IOException e) {
            Client.error(1, "io error", e);
        } catch (Exception e) {
            Client.error(1, "unknown error", e);
        }
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

    public static void say(String msg) {
        System.out.println(msg);
    }
}
