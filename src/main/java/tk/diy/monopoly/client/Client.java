
package tk.diy.monopoly.client;

import java.util.Optional;
import java.util.Scanner;
import java.net.*;
import java.io.*;

import tk.diy.monopoly.Game.Options;
import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.common.Request;
import tk.diy.monopoly.common.Player;
import tk.diy.monopoly.common.Protocol;

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
        return this.protocol.recv(false, false, null);
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

            Shell sh = new Shell("> ", System.in);
            Optional<Request> request = Optional.empty();

            while (!sh.quitEh()) {
                Request req;

                while (!request.isPresent()) {
                    try {
                        request = sh.nextRequest();
                    } catch (Exception e) {
                        System.err.println("an error occured while parsing input: ");
                        System.err.println(e);
                    }
                }

                req = request.get();
                Request resp = this.send(req);

                if (resp instanceof Request.Acknowledge) {
                    if (req instanceof Request.Echo) {
                        Request.EchoResponse response = (Request.EchoResponse) this.recv();
                        System.out.println(response);
                    } else if (req instanceof Request.Disconnect) {
                        this.remove(this.self.color);
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
                            Client.error(2, "Couldn't join game. It presumably already started.");
                        }
                    } else if (req instanceof Request.Move) {
                        Request.MoveResponse response = (Request.MoveResponse) this.recv();
                        int count = response.count;
                        this.self.move(count);
                        // TODO: field interaction
                        // i.e. buy building, prison, nothing, etc.
                    }
                } else if (resp instanceof Request.NotYourTurn) {
                    System.err.println("It's not your turn now. Please wait.");
                } else {
                    throw new Exception("unknown exception");
                }
            }

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
}
