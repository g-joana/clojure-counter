# clojure-counter
A minimal app focused on persisting data with Datomic built in Clojure and Clojurescript

Versions used:
- Clojure CLI - 1.12.2.1565
- Java - 21.0.8
- Node (client server) - v18.19.1

## Running locally

### Building the app
Pre-requisits:
- Node
- Java JRE, JDK
- Clojure CLI

Build server uberjar:
```
clojure -T:build uber
```
 -> creates a jar file: target/counter-server-0.0.1-standalone.jar

Compile Clojurescript:
``` 
clojure -M:front
```
 -> output files of compiled js at: resources/public/js


### Running server + client (hosted by npx)
Pre-requisits:
- Node
- Java JRE

Run:
```
java -jar target/counter-server-0.0.1-standalone.jar & npx http-server resources/public/ -p 9000
```
-> starts app on http://localhost:9000/

the counter server starts on the background and a node server hosts the client on port 9000