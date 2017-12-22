
package tk.diy.monopoly;

import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.server.Server;
import tk.diy.monopoly.client.Client;

public class Game {
    public final static String VERSION_STR = "monopoly v0.1.0";
                                        // <<---------------------------------------------------------------------------->>
    public final static String HELP_MSG = "monopoly [OPTIONS] [HOST]\n\n" +
                                          "OPTIONS\n" +
                                          "  -p | --port PORT      Connects to PORT or listens on PORT.  Default: 1935.\n" +
                                          "  -s | --server         Launches as a server.\n" +
                                          "  -j | --jobs JOBS      Use at most JOBS threads in a server.  Default: 10.\n" +
                                          "       --resend-limit N Retry at most N times if a package arrives corrupted.\n" +
                                          "                        Default: 4.\n" +
                                          "  -h | --help           Prints this message and quits.\n" +
                                          "  -v | --version        Prints version and quits.\n\n" +
                                          "CREDITS & LICENSE\n" +
                                          "  Made by Szymon Walter.\n" +
                                          "  This program is free software available under the zlib/libpng license.\n" +
                                          "  https://github.com/pi-pi3/monopoly";

    public static class Options {
        enum Opt {
            NONE,
            HELP,    // -h | --help
            VERSION, // -v | --version
            PORT,    // -p | --port
            SERVER,  // -s | --server
            THREADS, // -j | --jobs
            RESEND,  //      --resend-limit
        }

        public boolean isserver;
        public boolean help;
        public boolean version;
        public short port;
        public String host;
        public int threads;
        public int resendLimit;

        public Options() {
            this.isserver = false;
            this.port = Common.DEFAULT_PORT;
            this.host = Common.DEFAULT_HOST;
            this.threads = Common.DEFAULT_THREADS;
            this.resendLimit = Common.DEFAULT_RESEND_LIMIT;
        }

        public static Options parse(String[] args) throws Exception {
            Options opts = new Options();

            Opt opt = Opt.NONE;

            for (String arg : args) {
                switch (opt) {
                    case NONE:
                        switch (arg) {
                            case "-s":
                            case "--server":
                                opts.isserver = true;
                                break;
                            case "-h":
                            case "--help":
                                opts.help = true;
                                break;
                            case "-v":
                            case "--version":
                                opts.version = true;
                                break;
                            case "-p":
                            case "--port":
                                opt = Opt.PORT;
                                break;
                            case "-j":
                            case "--jobs":
                                opt = Opt.THREADS;
                                break;
                            case "--resend-limit":
                                opt = Opt.RESEND;
                                break;
                            default:
                                if (arg.startsWith("-")) {
                                    throw new Exception("unrecognized option \"" + arg + "\"");
                                } else {
                                    opts.host = arg;
                                }
                                break;
                        }
                        break;
                    case PORT:
                        opts.port = Short.parseShort(arg);
                        opt = Opt.NONE;
                        break;
                    case THREADS:
                        opts.threads = Integer.parseInt(arg);
                        opt = Opt.NONE;
                        break;
                    case SERVER:
                        opt = Opt.NONE;
                        break;
                    case RESEND:
                        opts.resendLimit = Integer.parseInt(arg);
                        opt = Opt.NONE;
                        break;
                }
            }

            return opts;
        }
    }

    public static void help() {
        System.out.println(HELP_MSG);
        System.exit(0);
    }

    public static void version() {
        System.out.println(VERSION_STR);
        System.exit(0);
    }

    public static void main(String[] args) {
        Options opts = null;
        try {
            opts = Options.parse(args);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

        if (opts.help) {
            Game.help();
        }

        if (opts.version) {
            Game.version();
        }

        Runnable game;
        if (opts.isserver) {
            game = new Server(opts);
        } else {
            game = new Client(opts);
        }
        game.run();
    }
}
