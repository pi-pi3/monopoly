# monopoly.java

## Monopoly Protocol
The server/client implementation includes a simple TCP/IP based protocol.  It
uses port 1935 (year of original publication of Monopoly) by default.  Requests
are always started by the client.  The server never begins any communication, it
only responds.  When the client sends a requests, it freezes until it receives a
success/failure response from the server, which can be either an "ack"
(Acknowledge --- success), a "resend" (meaning the message was corrupted) or
"access-denied" (meaning the client doesn't have permission to perform a certain
operation).  Then, depending on the original request, the client might wait for
another message from the server, to which it will respond with either an "ack"
or "resend".  All requests are encoded in JSON.  Every message consist of two
parts: the 32-bit stringified decimal Java-hashCode followed by a newline and
the JSON-encoded request followed by a newline.

### Example

```
Server <-------------> Client
| <<-- "echo" ------------- |
| ---- "ack" ------------>> |
| ---- "echo-response" -->> |
| <<-- "resend" ----------- |
| ---- "echo-response" -->> |
| <<-- "ack" -------------- |
| <<-- "join" ------------- |
| ---- "ack" ------------>> |
| ---- "join-response" -->> |
| <<-- "ack" -------------- |
| <<-- "shutdown" --------- |
| ---- "access-denied" -->> |
| <<-- "disconnect" ------- |
| ---- "ack" ------------>> |
```

### Example

```
140711711\n
{"request":"echo"}\n
```
