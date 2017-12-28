
package tk.diy.monopoly.common;

import java.util.HashMap;

import org.json.JSONObject;

import tk.diy.monopoly.common.Player;

public abstract class Request {
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
    }

    public static class Resend extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"resend\"}");
        public JSONObject serializeInner() { return req; }
        public static Resend deserialize(JSONObject req) throws Exception { return new Resend(); }
    }

    public static class Acknowledge extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"ack\"}");
        public JSONObject serializeInner() { return req; }
        public static Acknowledge deserialize(JSONObject req) throws Exception { return new Acknowledge(); }
    }

    public static class AccessDenied extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"access-denied\"}");
        public JSONObject serializeInner() { return req; }
        public static AccessDenied deserialize(JSONObject req) throws Exception { return new AccessDenied(); }
    }

    public static class Disconnect extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"disconnect\"}");
        public JSONObject serializeInner() { return req; }
        public static Disconnect deserialize(JSONObject req) throws Exception { return new Disconnect(); }
    }

    public static class Shutdown extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"shutdown\"}");
        public JSONObject serializeInner() { return req; }
        public static Shutdown deserialize(JSONObject req) throws Exception { return new Shutdown(); }
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
    }

    public static class Start extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"start\"}");
        public JSONObject serializeInner() { return req; }
        public static Start deserialize(JSONObject req) throws Exception { return new Start(); }
    }

    public static class End extends Request {
        private static final JSONObject req = new JSONObject("{\"request\":\"end\"}");
        public JSONObject serializeInner() { return req; }
        public static End deserialize(JSONObject req) throws Exception { return new End(); }
    }

    public static class Dice extends Request {
        public int count;

        public Dice(int count) {
            this.count = count;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "dice");
            req.put("count", this.count);
            return req;
        }

        public static Dice deserialize(JSONObject req) throws Exception {
            return new Dice(req.getInt("count"));
        }
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
    }

    public static class Build extends Request {
        public int id;
        public int count;

        public Build(int id, int count) {
            this.id = id;
            this.count = count;
        }

        public JSONObject serializeInner() {
            JSONObject req = new JSONObject();
            req.put("request", "build");
            req.put("id", this.id);
            req.put("count", this.count);
            return req;
        }

        public static Build deserialize(JSONObject req) throws Exception {
            return new Build(req.getInt("id"), req.getInt("count"));
        }
    }

    protected abstract JSONObject serializeInner();

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
        } else if (request.equals("ack")) {
            return Acknowledge.deserialize(data);
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
        } else if (request.equals("end")) {
            return End.deserialize(data);
        } else if (request.equals("dice")) {
            return Dice.deserialize(data);
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
