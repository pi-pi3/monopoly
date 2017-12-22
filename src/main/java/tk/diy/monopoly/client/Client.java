
package tk.diy.monopoly.client;

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

    private Socket socket;
    private Protocol protocol;

    private Player self;

    public Client(Options opts) {
        super();
        this.port = opts.port;
        this.host = opts.host;
    }

    private void send(Request request) throws IOException, Exception {
        this.protocol.send(request);
    }

    private Request recv() throws IOException, Exception {
        return this.protocol.recv();
    }

    public void run() {
        try {
            this.socket = new Socket(this.host, this.port);
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
            this.protocol = new Protocol(in, out);

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

            this.protocol.close();
            this.socket.close();
        } catch (IOException e) {
            Client.error(1, "io error", e);
        } catch (Exception e) {
            Client.error(1, "unknown error", e);
        }
    }
}
