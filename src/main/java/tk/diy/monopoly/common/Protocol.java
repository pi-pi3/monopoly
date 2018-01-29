
package tk.diy.monopoly.common;

import java.net.*;
import java.io.*;
import java.util.Optional;

import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.common.Request;

public class Protocol {
    private BufferedReader in;
    private DataOutputStream out;
    private int resendLimit;
    private int resendCount;
    private long ping;

    public Protocol(BufferedReader in, DataOutputStream out) {
        this.in = in;
        this.out = out;
        this.resendLimit = Common.DEFAULT_RESEND_LIMIT;
        this.resendCount = 0;
    }

    public Protocol(BufferedReader in, DataOutputStream out, int resendLimit) {
        this(in, out);
        this.resendLimit = resendLimit;
    }

    public long getPing() {
        return this.ping;
    }

    public void close() throws IOException {
        this.in.close();
        this.out.close();
    }

    public void resend() throws IOException, Exception {
        if (this.resendCount >= this.resendLimit) {
            throw new Exception("too many resend requests");
        }

        this.resendCount++;
        Request req = new Request.Resend();
        this.out.writeBytes(req.toString() + '\n');
    }

    public void ack() throws IOException {
        this.resendCount = 0;

        Request req = new Request.Acknowledge();
        this.out.writeBytes(req.toString() + '\n');
    }

    public Request notYourTurn(Player.Color currentPlayer) throws IOException {
        this.resendCount = 0;

        Request req = new Request.NotYourTurn(currentPlayer);
        this.out.writeBytes(req.toString() + '\n');
        return req;
    }

    public Request accessDenied() throws IOException {
        this.resendCount = 0;

        Request req = new Request.AccessDenied();
        this.out.writeBytes(req.toString() + '\n');
        return req;
    }

    public Request gameStarted() throws IOException {
        this.resendCount = 0;

        Request req = new Request.GameStarted();
        this.out.writeBytes(req.toString() + '\n');
        return req;
    }

    public Request gameNotStarted() throws IOException {
        this.resendCount = 0;

        Request req = new Request.GameNotStarted();
        this.out.writeBytes(req.toString() + '\n');
        return req;
    }

    public Request send(Request request) throws IOException, Exception {
        long t0 = System.currentTimeMillis();

        String msg = request.toString();
        int cksum = msg.hashCode();
        Request response = null;

        while (response == null || response instanceof Request.Resend) {
            this.out.writeBytes(String.valueOf(cksum) + '\n');
            this.out.writeBytes(msg + '\n');

            /* check if acknowledged */
            String resp = this.in.readLine();

            response = Request.deserialize(resp);
            if (response instanceof Request.Acknowledge) {
                break; // do nothing
            } else if (response instanceof Request.Resend) {
                continue; // do nothing
            } else if (response instanceof Request.AccessDenied) {
                break; // do nothing
            } else if (response instanceof Request.NotYourTurn) {
                break; // do nothing
            } else if (response instanceof Request.GameStarted) {
                break; // do nothing
            } else if (response instanceof Request.GameNotStarted) {
                break; // do nothing
            } else if (response instanceof Request.Wait) {
                int timeout = ((Request.Wait) response).timeout; // TODO
                Request notify = this.recv();
                if (notify instanceof Request.Notify) {
                    // proceed without error
                    response = new Request.Acknowledge();
                    break;
                }
                throw new Exception("response is not Notify");
            } else {
                throw new Exception("no response");
            }
        }

        long t1 = System.currentTimeMillis();
        this.ping = t1 - t0;

        return response;
    }

    public Request recv() throws IOException, Exception {
        return this.recv(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public Request recv(boolean isRoot) throws IOException, Exception {
        return this.recv(Optional.of(isRoot), Optional.empty(), Optional.empty(), Optional.of(GameState.NOT_STARTED));
    }

    public Request recv(boolean isRoot, Player.Color player, Player.Color currentPlayer, GameState currentState) throws IOException, Exception {
        return this.recv(Optional.of(isRoot), Optional.of(player), Optional.of(currentPlayer), Optional.of(currentState));
    }

    private Request recv(Optional<Boolean> isRoot, Optional<Player.Color> player, Optional<Player.Color> currentPlayer, Optional<GameState> currentState) throws IOException, Exception {
        Request req = null;

        while (req == null) {
            String msg_cksum = this.in.readLine();
            String msg = this.in.readLine();

            if (msg_cksum == null || msg == null) {
                throw new Exception("connection ended unexpectadly");
            }

            int cksum = Integer.parseInt(msg_cksum);
            if (msg.hashCode() == cksum) {
                req = Request.deserialize(msg);

                if (isRoot.isPresent()
                 && req.rootRequired() && !isRoot.get()) {
                    req = this.accessDenied();
                }
                if (currentState.isPresent()
                 && req.stateRequired() != GameState.NONE
                 && req.stateRequired() != currentState.get()) {
                    if (currentState.get() == GameState.STARTED) {
                        req = this.gameStarted();
                    } else {
                        req = this.gameNotStarted();
                    }
                }
                if (player.isPresent() && currentPlayer.isPresent() 
                 && req.turnRequired()
                 && player.get() != currentPlayer.get()) {
                    req = this.notYourTurn(currentPlayer.get());
                }

                this.ack();
            } else {
                this.resend();
            }
        }

        return req;
    }
}
