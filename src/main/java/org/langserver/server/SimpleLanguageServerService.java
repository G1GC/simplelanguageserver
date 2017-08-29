package org.langserver.server;

import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.google.gson.Gson;
import org.langserver.model.Input;
import org.langserver.model.Location;
import spark.Spark;

import java.io.File;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 *
 * A simple rest service built using SparkJava. @see <a href="http://sparkjava.com/">http://sparkjava.com/</a>
 * Once started, server listens on port 4567 - <a href="http://localhost:4567/hello">http://localhost:4567/hello</a>
 * To jump-to-def,
 * curl -H "Content-Type: application/json" -X POST -d '{"sourceFolders":["src/main/java"],"location":{"fileName":"TestTask.java","lineNumber":158,"columnNumber":69}}' http://localhost:4567/jumpToDef
 *
 */
public class SimpleLanguageServerService {
    public static void main(String[] args) {
        Spark.exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });

        get("/hello", (req, res) -> "Welcome to LanguageServer, pls check the docs/source for usage!");

        Gson gson = new Gson();
        post("/jumpToDef", (request, response) -> {
            Input input = gson.fromJson(request.body(), Input.class);
            CombinedTypeSolver typeSolver = new CombinedTypeSolver();
            input.getSourceFolders().forEach(source -> typeSolver.add(new JavaParserTypeSolver(new File(source))));
            typeSolver.add(new ReflectionTypeSolver());
            for(String source:input.getSourceFolders()){
                Location outputLocation = new SimpleLanguageServer(typeSolver).jumpToDef(new File(source), input.getLocation());
                if(!outputLocation.getFileName().isEmpty())
                    return outputLocation;
            }
            response.status(404);
            return "{\"message\":\" Not Found\"}";
        }, gson::toJson);
    }
}
