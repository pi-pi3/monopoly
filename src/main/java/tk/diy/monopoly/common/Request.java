
package tk.diy.monopoly.common;

import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONArray;

import tk.diy.monopoly.common.Player;
import tk.diy.monopoly.common.GameState;
import tk.diy.monopoly.common.building.Building;

public abstract class Request {
    public static class Show extends Request { // this is a client-side dummy request
        private static final JSONObject req = new JSONObject("{\"request\":\"show\"}");
        public JSONObject serializeInner() { return req; }
        public static Show deserialize(JSONObject req) throws Exception { return new Show(); }
        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class Ask extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"ask\"}");
        public JSONObject serializeInner() { return req; }
        public static Ask deserialize(JSONObject req) throws Exception { return new Ask(); }
        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class AskResponse extends Request {
        // minified, serializable version of the player
        public static class PlayerUnit {
            public int position;
            public int money;
            public int[] owned; // fields owned

            public JSONObject serialize() {
                JSONObject req = new JSONObject();
                req.put("position", this.position);
                req.put("money", this.money);
                req.put("owned", this.owned);
                return req;
            }

            public static PlayerUnit deserialize(JSONObject obj) {
                PlayerUnit unit = new PlayerUnit();

                unit.position = obj.getInt("position");
                unit.money = obj.getInt("money");
                JSONArray owned = obj.getJSONArray("owned");
                unit.owned = new int[owned.length()];
                for (int i = 0; i < unit.owned.length; i++) {
                    unit.owned[i] = owned.getInt(i);
                }

                return unit;
            }
        }

        public int currentPlayer;
        public PlayerUnit[] players;

        private AskResponse() {}

        public AskResponse(Common game) {
            this.currentPlayer = game.playerIndex();

            Player[] players = game.players();
            this.players = new PlayerUnit[players.length];
            for (int i = 0; i < players.length; i++) {
                Player player = players[i];

                this.players[i] = new PlayerUnit();
                this.players[i].position = player.position();
                this.players[i].money = player.getCash();

                Building[] owned = player.getOwned();
                this.players[i].owned = new int[owned.length];
                for (int j = 0; j < owned.length; j++) {
                    this.players[i].owned[j] = owned[j].index();
                }
            }
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();

            req.put("request", "ask-response");
            req.put("current-player", this.currentPlayer);
            JSONObject[] players = new JSONObject[this.players.length];
            for (int i = 0; i < players.length; i++) {
                players[i] = this.players[i].serialize();
            }

            return req;
        }

        public static AskResponse deserialize(JSONObject obj) throws Exception {
            AskResponse req = new AskResponse();

            req.currentPlayer = obj.getInt("current-player");
            JSONArray players = obj.getJSONArray("players");
            req.players = new PlayerUnit[players.length()];
            for (int i = 0; i < req.players.length; i++) {
                req.players[i] = PlayerUnit.deserialize(players.getJSONObject(i));
            }

            return req;
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class Echo extends Request {
        public String message;

        public Echo() { }

        public Echo(String message) {
            this.message = message;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "echo");
            req.putOpt("message", this.message);
            return req;
        }

        public static Echo deserialize(JSONObject req) throws Exception {
            return new Echo(req.optString("message"));
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class EchoResponse extends Request {
        public String message;

        public EchoResponse() { }

        public EchoResponse(String message) {
            this.message = message;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "echo-response");
            req.putOpt("message", this.message);
            return req;
        }

        public static EchoResponse deserialize(JSONObject req) throws Exception {
            return new EchoResponse(req.optString("message"));
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class Resend extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"resend\"}");
        public JSONObject serializeInner() { return req; }
        public static Resend deserialize(JSONObject req) throws Exception { return new Resend(); }
        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class Acknowledge extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"ack\"}");
        public JSONObject serializeInner() { return req; }
        public static Acknowledge deserialize(JSONObject req) throws Exception { return new Acknowledge(); }
        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class AccessDenied extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"access-denied\"}");
        public JSONObject serializeInner() { return req; }
        public static AccessDenied deserialize(JSONObject req) throws Exception { return new AccessDenied(); }
        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class Wait extends Request {
        public int timeout; // In milliseconds. Infinite if -1

        public Wait() {
            this(-1);
        }

        public Wait(int timeout) {
            this.timeout = timeout;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "wait");
            req.put("timeout", this.timeout);
            return req;
        }

        public static Wait deserialize(JSONObject req) throws Exception {
            return new Wait(req.getInt("timeout"));
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class Notify extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"notify\"}");
        public JSONObject serializeInner() { return req; }
        public static Notify deserialize(JSONObject req) throws Exception { return new Notify(); }
        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class NotYourTurn extends Request {
        public Player.Color currentPlayer;

        public NotYourTurn(Player.Color currentPlayer) {
            this.currentPlayer = currentPlayer;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "not-your-turn");
            req.put("current-player", this.currentPlayer.toInt());
            return req;
        }

        public static NotYourTurn deserialize(JSONObject req) throws Exception {
            return new NotYourTurn(Player.Color.fromInt(req.getInt("current-player")));
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class GameStarted extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"game-started\"}");
        public JSONObject serializeInner() { return req; }
        public static GameStarted deserialize(JSONObject req) throws Exception { return new GameStarted(); }
        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class GameNotStarted extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"game-not-started\"}");
        public JSONObject serializeInner() { return req; }
        public static GameNotStarted deserialize(JSONObject req) throws Exception { return new GameNotStarted(); }
        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class Disconnect extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"disconnect\"}");
        public JSONObject serializeInner() { return req; }
        public static Disconnect deserialize(JSONObject req) throws Exception { return new Disconnect(); }
        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class Shutdown extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"shutdown\"}");
        public JSONObject serializeInner() { return req; }
        public static Shutdown deserialize(JSONObject req) throws Exception { return new Shutdown(); }
        public boolean rootRequired() { return true; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    // game state requests
    public static class Join extends Request {
        public Player.Color color;

        public Join(Player.Color color) {
            this.color = color;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "join");
            req.put("color", this.color.toInt());
            return req;
        }

        public static Join deserialize(JSONObject req) throws Exception {
            return new Join(Player.Color.fromInt(req.getInt("color")));
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NOT_STARTED; }
    }

    public static class JoinResponse extends Request {
        public Player.Color color;
        public boolean success;

        public JoinResponse(Player.Color color, boolean success) {
            this.color = color;
            this.success = success;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "join-response");
            req.put("color", this.color.toInt());
            req.put("success", this.success);
            return req;
        }

        public static JoinResponse deserialize(JSONObject req) throws Exception {
            return new JoinResponse(
                Player.Color.fromInt(req.getInt("color")),
                req.getBoolean("success")
            );
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class Start extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"start\"}");
        public JSONObject serializeInner() { return req; }
        public static Start deserialize(JSONObject req) throws Exception { return new Start(); }
        public boolean rootRequired() { return true; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NOT_STARTED; }
    }

    public static class StartResponse extends Request {
        public boolean success;

        public StartResponse(boolean success) {
            this.success = success;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "start-response");
            req.put("success", this.success);
            return req;
        }

        public static StartResponse deserialize(JSONObject req) throws Exception {
            return new StartResponse(req.getBoolean("success"));
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class End extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"end\"}");
        public JSONObject serializeInner() { return req; }
        public static End deserialize(JSONObject req) throws Exception { return new End(); }
        public boolean rootRequired() { return true; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.STARTED; }
    }

    public static class EndResponse extends Request {
        public boolean success;

        public EndResponse(boolean success) {
            this.success = success;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "end-response");
            req.put("success", this.success);
            return req;
        }

        public static EndResponse deserialize(JSONObject req) throws Exception {
            return new EndResponse(req.getBoolean("success"));
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class Move extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"move\"}");
        public JSONObject serializeInner() { return req; }
        public static Move deserialize(JSONObject req) throws Exception { return new Move(); }
        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return true; }
        public GameState stateRequired() { return GameState.STARTED; }
    }

    public static class MoveResponse extends Request {
        public int count;

        public MoveResponse(int count) {
            this.count = count;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "move-response");
            req.put("count", this.count);
            return req;
        }

        public static MoveResponse deserialize(JSONObject req) throws Exception {
            return new MoveResponse(req.getInt("count"));
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return false; }
        public GameState stateRequired() { return GameState.NONE; }
    }

    public static class Buy extends Request {
        public boolean buy;

        public Buy(boolean buy) {
            this.buy = buy;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "buy");
            req.put("buy", this.buy);
            return req;
        }

        public static Buy deserialize(JSONObject req) throws Exception {
            return new Buy(req.getBoolean("buy"));
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return true; }
        public GameState stateRequired() { return GameState.STARTED; }
    }

    public static class Build extends Request {
        public int field;

        public Build(int field) {
            this.field = field;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "build");
            req.put("field", this.field);
            return req;
        }

        public static Build deserialize(JSONObject req) throws Exception {
            return new Build(req.getInt("field"));
        }

        public boolean rootRequired() { return false; }
        public boolean turnRequired() { return true; }
        public GameState stateRequired() { return GameState.STARTED; }
    }

    protected abstract JSONObject serializeInner();
    public abstract boolean rootRequired();
    public abstract boolean turnRequired();
    public abstract GameState stateRequired();

    public JSONObject serialize() {
        JSONObject data = this.serializeInner();
        data.put("version", Common.VERSION);
        return data;
    }

    public String toString() {
        return this.serialize().toString();
    }

    public static Request deserialize(JSONObject data) throws Exception {
        String request = data.getString("request");
        if (request.equals("echo")) {
            return Echo.deserialize(data);
        } else if (request.equals("echo-response")) {
            return EchoResponse.deserialize(data);
        } else if (request.equals("ask")) {
            return Ask.deserialize(data);
        } else if (request.equals("ask-response")) {
            return AskResponse.deserialize(data);
        } else if (request.equals("ack")) {
            return Acknowledge.deserialize(data);
        } else if (request.equals("access-denied")) {
            return AccessDenied.deserialize(data);
        } else if (request.equals("wait")) {
            return Wait.deserialize(data);
        } else if (request.equals("notify")) {
            return Notify.deserialize(data);
        } else if (request.equals("not-your-turn")) {
            return NotYourTurn.deserialize(data);
        } else if (request.equals("game-started")) {
            return GameStarted.deserialize(data);
        } else if (request.equals("game-not-started")) {
            return GameNotStarted.deserialize(data);
        } else if (request.equals("disconnect")) {
            return Disconnect.deserialize(data);
        } else if (request.equals("shutdown")) {
            return Shutdown.deserialize(data);
        } else if (request.equals("join")) {
            return Join.deserialize(data);
        } else if (request.equals("join-response")) {
            return JoinResponse.deserialize(data);
        } else if (request.equals("start")) {
            return Start.deserialize(data);
        } else if (request.equals("start-response")) {
            return StartResponse.deserialize(data);
        } else if (request.equals("end")) {
            return End.deserialize(data);
        } else if (request.equals("end-response")) {
            return EndResponse.deserialize(data);
        } else if (request.equals("move")) {
            return Move.deserialize(data);
        } else if (request.equals("move-response")) {
            return MoveResponse.deserialize(data);
        } else if (request.equals("buy")) {
            return Buy.deserialize(data);
        } else if (request.equals("build")) {
            return Build.deserialize(data);
        } else {
            throw new Exception("invalid request: \"" + request + '"');
        }
    }

    public static Request deserialize(String req) throws Exception {
        return Request.deserialize(new JSONObject(req));
    }
}
