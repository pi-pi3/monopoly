
package tk.diy.monopoly.server;

import java.net.*;
import java.io.*;

import tk.diy.monopoly.server.Server;
import tk.diy.monopoly.common.Request;

public class Handler implements Runnable {
    private final Server host;
    private final Socket conn;

    private BufferedReader in;
    private DataOutputStream out;

    public Handler(Server host, Socket conn) {
        this.host = host;
        this.conn = conn;

        try {
            this.in = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
            this.out = new DataOutputStream(this.conn.getOutputStream());
        } catch (IOException e) {
            Server.error(1, "io", e);
        }
    }

    private void resend() throws IOException {
        Request req = new Request.Resend();
        this.out.writeBytes(req.toString() + '\n');
    }

    private void ack() throws IOException {
        Request req = new Request.Acknowledge();
        this.out.writeBytes(req.toString() + '\n');
    }

    private void send(Request request) throws IOException, Exception {
        String msg = request.toString();
        int cksum = msg.hashCode();
        this.out.writeBytes(String.valueOf(cksum) + '\n');
        this.out.writeBytes(msg + '\n');

        /* check if acknowledged */
        String resp = this.in.readLine();
        Request response = Request.deserialize(resp);
        if (!(response instanceof Request.Acknowledge)) {
            Server.error(1, "no response");
        }
    }

    private Request recv() throws IOException, Exception {
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

    public void run() {
        try {
            while (true) {
                Request req = this.recv();

                if (req instanceof Request.Echo) {
                    this.send(new Request.EchoResponse(((Request.Echo) req).message));
                } else if (req instanceof Request.Disconnect) {
                    break;
                } else if (req instanceof Request.Shutdown) {
                    this.out.close();
                    this.in.close();
                    this.conn.close();
                    this.host.shutdown();
                    break;
                }
            }
        } catch (IOException e) {
            Server.error(1, "io", e);
        } catch (Exception e) {
            Server.error(1, "unknown error", e);
        }
    }
}
