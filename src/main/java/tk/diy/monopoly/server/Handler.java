
package tk.diy.monopoly.server;

import java.net.*;
import java.io.*;

import tk.diy.monopoly.server.Server;
import tk.diy.monopoly.common.Request;
import tk.diy.monopoly.common.Protocol;

public class Handler implements Runnable {
    private final Server host;
    private final Socket conn;

    private Protocol protocol;

    public Handler(Server host, Socket conn) {
        this.host = host;
        this.conn = conn;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
            DataOutputStream out = new DataOutputStream(this.conn.getOutputStream());
            this.protocol = new Protocol(in, out);
        } catch (IOException e) {
            Server.error(1, "io", e);
        }
    }

    private void send(Request request) throws IOException, Exception {
        this.protocol.send(request);
    }

    private Request recv() throws IOException, Exception {
        return this.protocol.recv();
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
                    this.protocol.close();
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
