# monopoly.java

This is more like a proof-of-concept. It is a functional Monopoly-type game
(with very little of original rules missing), but it does lack real-time
server --- client updates. Syncing is done with a sort-of cheaty way.

## Quickstart guide
Build the executable jar with `make` or `./gradlew shadowJar`.
Run `cp build/libs/monopoly-all.jar monopoly.jar` for convenience.
Run `java -jar monopoly.jar --server` to start a server on localhost. Then run any
number of clients with `java -jar monopoly.jar`.

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

Additionally, the "ask" request is used to sync the client with the server. The
client 'asks' the server about new updates, to which the server returns an
"ask-response" containing a minified, serialized version of the game state.

### Example

```
Server <-------------> Client
| <<-- "ask" -------------- |
| ---- "ask-response" --->> |
| <<-- "echo" ------------- |
| ---- "ack" ------------>> |
| ---- "echo-response" -->> |
| <<-- "resend" ----------- |
| ---- "echo-response" -->> |
| <<-- "ack" -------------- |
| <<-- "ask" -------------- |
| ---- "ask-response" --->> |
| <<-- "join" ------------- |
| ---- "ack" ------------>> |
| ---- "join-response" -->> |
| <<-- "ack" -------------- |
| <<-- "ask" -------------- |
| ---- "ask-response" --->> |
| <<-- "shutdown" --------- |
| ---- "access-denied" -->> |
| <<-- "ask" -------------- |
| ---- "ask-response" --->> |
| <<-- "disconnect" ------- |
| ---- "ack" ------------>> |
```

### Example

```
140711711\n
{"request":"echo"}\n
```

## Conclusion

The protocol created here isn't well suited for real-time, or even turn-based
games. It could however work better for document/data exchange, online chats,
version control, etc.

## HS Emden/Leer

This game was completed as my Java 101 assignment at my University, the
University of Applied Sciences Emden/Leer and turned in on the 31st January.
