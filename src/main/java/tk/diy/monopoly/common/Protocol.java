
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

    public void send(Request request) throws IOException, Exception {
        String msg = request.toString();
        int cksum = msg.hashCode();
        this.out.writeBytes(String.valueOf(cksum) + '\n');
        this.out.writeBytes(msg + '\n');

        /* check if acknowledged */
        String resp = this.in.readLine();
        Request response = Request.deserialize(resp);
        if (!(response instanceof Request.Acknowledge)) {
            throw new Exception("no response");
        }
    }

    public Request recv() throws IOException, Exception {
        Request req = null;

        while (req == null) {
            int cksum = Integer.parseInt(this.in.readLine());
            String msg = this.in.readLine();
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
