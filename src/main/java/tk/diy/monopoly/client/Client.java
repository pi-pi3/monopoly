
package tk.diy.monopoly.client;

import java.util.Scanner;
import java.net.*;
import java.io.*;

import tk.diy.monopoly.Game.Options;
import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.common.Request;
import tk.diy.monopoly.common.Player;

public class Client extends Common implements Runnable {
    public short port;
    public String host;

    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;

    private Player self;

    public Client(Options opts) {
        super();
        this.port = opts.port;
        this.host = opts.host;
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
            Client.error(1, "no response");
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
            this.socket = new Socket(this.host, this.port);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new DataOutputStream(this.socket.getOutputStream());

            Scanner sc = new Scanner(System.in);
            String msg = "";

            while (!msg.equals(null)) {
                msg = sc.nextLine();
                Request req = Request.deserialize(msg);
                this.send(req);

                if (req instanceof Request.Echo) {
                    Request response = this.recv();
                    System.out.println(response);
                } else if (req instanceof Request.Disconnect) {
                    break;
                } else if (req instanceof Request.Shutdown) {
                    break;
                }
            }

            this.out.close();
            this.in.close();
            this.socket.close();
        } catch (IOException e) {
            Client.error(1, "io error", e);
        } catch (Exception e) {
            Client.error(1, "unknown error", e);
        }
    }
}
