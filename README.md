# Introduction
- My poor attempt to explore JavaParser APIs, to build some simple IDE features.
- A simple language server implementation, currently supports only jump-to-def of Java source code.
- It's built based on the Java Parser lib - http://javaparser.org/
- Uses sparkjava(sparkjava.com) to spin up a lightweight rest server to serve the requests
- The program builds the AST(Abstract Syntax Tree) using the JavaParser APIs
- It then applies logic on top of it using SymbolSolver - https://github.com/javaparser/javasymbolsolver, to get the Jump-To-Def location
- Based on the various types of nodes(Methods, Fields, Parameters etc.), the syntax tree is parsed and resolved to get th location of the entity def

## Bugs
- There could be some cases for which this implementation needs to be tweaked to make it work.
- For e.g, jump-to-def of object instantiation doesn't work, currently
- Thread safety is something to be taken care

## How to build?
- Go to the home directory of the project
- Maven build: mvn clean package

## How to test?
- Launch the service after building, by running the fat jar
    java -jar target/simple-language-server-1.0-SNAPSHOT.jar
- Server up! - http://localhost:4567/hello
- Post to the http endpoint "http://localhost:4567/jumpToDef" using cURL as below:
curl -H "Content-Type: application/json" -X POST -d '{"sourceFolders":["<relative path>/simplelanguageserver/src/main/java"],"location":{"fileName":"SimpleLanguageServerService.java","lineNumber":39,"columnNumber":114}}' http://localhost:4567/jumpToDef
curl -H "Content-Type: application/json" -X POST -d '{"sourceFolders":["<relative path>/simplelanguageserver/src/main/java"],"location":{"fileName":"SimpleLanguageServerService.java","lineNumber":39,"columnNumber":80}}' http://localhost:4567/jumpToDef
curl -H "Content-Type: application/json" -X POST -d '{"sourceFolders":["<relative path>/simplelanguageserver/src/main/java"],"location":{"fileName":"DirExplorer.java","lineNumber":17,"columnNumber":23}}' http://localhost:4567/jumpToDef
curl -H "Content-Type: application/json" -X POST -d '{"sourceFolders":["<relative path>/simplelanguageserver/src/main/java"],"location":{"fileName":"DirExplorer.java","lineNumber":32,"columnNumber":17}}' http://localhost:4567/jumpToDef
curl -H "Content-Type: application/json" -X POST -d '{"sourceFolders":["<relative path>/simplelanguageserver/src/main/java"],"location":{"fileName":"DirExplorer.java","lineNumber":32,"columnNumber":29}}' http://localhost:4567/jumpToDef

- Replace the <relative path> in the above request with the actual absolute path
- Returns the JSON output as below(for the first cURL request):
{"fileName":"<Path readacted>/simplelanguageserver/src/main/java/org/langserver/model/Input.java","lineNumber":30,"columnNumber":21}

- Incase of multiple source folders, specify as comma separated
- Note: The path in the source folder given as input must be all the way until the beginning of the package directory of the source, for e.g it shouldn't project root directory