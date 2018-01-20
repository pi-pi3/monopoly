
package tk.diy.monopoly.common;

import java.net.*;
import java.io.*;

import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.common.Request;

public class Protocol {
    private BufferedReader in;
    private DataOutputStream out;
    private int resendLimit;
    private int resendCount;

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

    public Request send(Request request) throws IOException, Exception {
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
                return response;
            } else if (response instanceof Request.AccessDenied) {
                throw new Exception("access denied");
            } else if (response instanceof Request.Wait) {
                int timeout = ((Request.Wait) response).timeout; // TODO
                Request notify = this.recv();
                if (notify instanceof Request.Notify) {
                    // proceed without error
                    return new Request.Acknowledge();
                }
                throw new Exception("response is not Notify");
            } else if (response instanceof Request.NotYourTurn) {
                return response;
            } else {
                throw new Exception("no response");
            }
        }

        // should be unreachable
        return null;
    }

    public Request recv() throws IOException, Exception {
        return this.recv(true, null);
    }

    public Request recv(boolean myTurn, Player.Color currentPlayer) throws IOException, Exception {
        Request req = null;

        while (req == null) {
            int cksum = Integer.parseInt(this.in.readLine());
            String msg = this.in.readLine();
            if (!myTurn) {
                return this.notYourTurn(currentPlayer);
            }
            if (msg.hashCode() == cksum) {
                req = Request.deserialize(msg);
                this.ack();
            } else {
                this.resend();
            }
        }

        return req;
    }
}
