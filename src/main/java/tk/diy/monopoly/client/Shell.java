
package tk.diy.monopoly.client;

import java.io.*;
import java.util.Optional;
import java.util.regex.*;
import java.util.ArrayList;

import tk.diy.monopoly.common.Request;

public class Shell {
    private Optional<String> ps1;
    private InputStream in;
    private boolean shouldClose;
    private boolean closedEh;
    private boolean quitEh;

    private final static Pattern arg0 = Pattern.compile("^(?<=\\p{Blank}*)([a-zA-Z_]\\w*)");
    private final static Pattern argv = Pattern.compile("(\\S+)");

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

    public String nextLine() throws IOException {
        if (this.quitEh) {
            return null;
        }

        StringBuilder line = new StringBuilder();
        this.ps1.ifPresent((String ps1) -> System.out.println(ps1));
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

        String line = this.nextLine();
        Matcher matcher = Shell.arg0.matcher(line);
        if (!matcher.matches()) {
            throw new Exception("invalid command: " + line);
        }

        String arg0 = matcher.group(0);
        String[] argv = new String[0];
        matcher.reset();
        matcher.usePattern(Shell.argv);

        if (matcher.matches()) {
            int count = matcher.groupCount() - 1;
            argv = new String[count];
            for (int i = 0; i < count; i++) {
                argv[i] = matcher.group(i+1);
            }
        }

        switch (arg0) {
            case "echo":
                String message = null;
                if (argv.length > 0) {
                    StringBuilder mbuilder = new StringBuilder(argv[0]);
                    for (int i = 1; i < argv.length; i++) {
                        mbuilder.append(argv[i]);
                    }
                }
                return new Request.Echo(message);
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
