
package tk.diy.monopoly.client;

import java.io.*;
import java.util.Optional;
import java.util.ArrayList;

import tk.diy.monopoly.common.Request;
import tk.diy.monopoly.common.Player;

public class Shell {
    private Optional<String> ps1;
    private InputStream in;
    private boolean shouldClose;
    private boolean closedEh;
    private boolean quitEh;

    public Shell(InputStream in) {
        this(null, in, true);
    }

    public Shell(String ps1, InputStream in) {
        this(ps1, in, true);
    }

    public Shell(String ps1, InputStream in, boolean shouldClose) {
        this.ps1 = Optional.ofNullable(ps1);
        this.in = in;
        this.shouldClose = shouldClose;
        this.closedEh = false;
        this.quitEh = false;
    }

    public boolean ask() throws IOException {
        String ans = this.nextLine(Optional.of("")).trim().toLowerCase();
        switch (ans) {
            case "y":
            case "yes":
            case "t":
            case "true":
            case "1":
                return true;
            default:
                return false;
        }
    }

    public String nextLine() throws IOException {
        return this.nextLine(this.ps1);
    }

    public String nextLine(Optional<String> ps1) throws IOException {
        if (this.quitEh) {
            return null;
        }

        StringBuilder line = new StringBuilder();
        ps1.ifPresent((String ps) -> System.out.print(ps));
        boolean backslash = false;
        while (true) {
            int b = this.in.read();
            if (b < 0) {
                break;
            }

            char c = (char) b;
            if (!backslash) {
                if (c == '\\') {
                    backslash = true;
                } else if (c == '\n') {
                    break;
                } else {
                    line.append(c);
                }
            } else {
                if (c == '\n') {
                    // nothing; ignore both the backslash and lf
                } else {
                    line.append('\\');
                    line.append(c);
                }
                backslash = false;
            }
        }

        return line.toString();
    }

    public Request nextRequest() throws IOException, Exception {
        if (this.quitEh) {
            return null;
        }

        String line = "";
        while (line.trim().isEmpty()) {
            line = this.nextLine();
        }
        String[] args = line.split("[ \t]");
        String arg0 = args[0];

        String[] argv = new String[args.length-1];
        System.arraycopy(args, 1, argv, 0, argv.length);

        return this.parse(arg0, argv);
    }
    
    // TODO: add the entire rest
    private Request parse(String arg0, String[] argv) throws Exception {
        switch (arg0) {
            case "echo": // echo [message...]
                String message = String.join(" ", argv);
                return new Request.Echo(message);
            case "disconnect": // disconnect
                return new Request.Disconnect();
            case "shutdown": // shutdown (requires root)
                return new Request.Shutdown();
            case "join": // join <color>
                if (argv.length == 0) {
                    throw new Exception("missing argument <color>");
                }
                Player.Color color;
                try {
                    color = Player.Color.fromName(argv[0]);
                } catch (Exception e) {
                    throw new Exception('"' + argv[0] + "\" is not a valid color");
                }
                return new Request.Join(color);
            case "start": // start (requires root)
                return new Request.Start();
            case "end": // end (requires root)
                return new Request.End();
            case "move": // move
                return new Request.Move();
            case "show": // show
                return new Request.Show();
            case "help": // help
                return new Request.Help();
            default:
                throw new Exception("invalid command: " + arg0);
        }
    }

    public void quit() {
        this.quitEh = true;
        this.close();
    }

    public boolean quitEh() {
        return this.quitEh;
    }

    public void close() {
        if (this.shouldClose && !this.closedEh) {
            try {
                this.in.close();
            } catch (IOException e) {
                Client.error(1, "an unknown io error occured", e);
            } finally {
                this.closedEh = true;
            }
        }
    }

    public void finalize() {
        this.close();
    }
}
