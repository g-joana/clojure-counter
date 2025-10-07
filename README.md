# clojure-counter
A minimal app focused on persisting data with Datomic built in Clojure and Clojurescript

Requirements:
- Clojure CLI - 1.12.2.1565
- Java JRE, JDK - 21.0.8
- Node - v18.19.1

## Running locally
Clone repo:
```
git clone git@github.com:g-joana/clojure-counter.git && cd clojure-counter
```
Change to project's directory:
```
cd clojure-counter
```

### Building the app

Build server uberjar:
```
clojure -T:build uber
```
> creates a jar file: target/counter-server-0.0.1-standalone.jar

Compile Clojurescript:
``` 
clojure -M:front
```
> output files of compiled js at: resources/public/js


### Running app

Run back-end:
```
java -jar target/counter-server-0.0.1-standalone.jar
```
> starts back-end server on http://localhost:8080/

Run front-end:
```
npx http-server resources/public/ -p 9000
```
> starts front-end on http://localhost:9000/