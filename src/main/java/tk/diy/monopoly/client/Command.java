
package tk.diy.monopoly.client;

import java.util.Optional;

import tk.diy.monopoly.common.Request;

public abstract class Command {
    public static class Echo extends Command {
        private String message;

        public Echo(String message) {
            this.message = message;
        }

        public Optional<Request> execute() {
            return Optional.of(new Request.Echo(this.message));
        }
    }

    public Optional<Request> execute() {
        return Optional.empty();
    }

    public static Command parse(String arg0) throws Exception {
        return Command.parse(arg0, new String[0]);
    }

    public static Command parse(String arg0, String[] argv) throws Exception {
        switch (arg0) {
            case "echo":
                String message = null;
                if (argv.length > 0) {
                    StringBuilder mbuilder = new StringBuilder(argv[0]);
                    for (int i = 1; i < argv.length; i++) {
                        mbuilder.append(argv[i]);
                    }
                }
                return new Echo(message);
        }

        throw new Exception("invalid command name");
    }
}
