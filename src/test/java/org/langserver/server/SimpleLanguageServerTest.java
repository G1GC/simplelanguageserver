package org.langserver.server;

import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.langserver.model.Location;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;

public class SimpleLanguageServerTest {

    private LanguageServer server;

    @Before
    public void setUp() throws Exception {
        File testFile = new File("src/test/java/org/langserver/server");
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new JavaParserTypeSolver(testFile));
        combinedTypeSolver.add(new ReflectionTypeSolver());
        server = new SimpleLanguageServer(combinedTypeSolver);
    }

    @Test
    public void testJumpToDefOfMethod() throws Exception {
        File testFile = new File("src/test/java/org/langserver/server");
        Location output = server.jumpToDef(testFile, new Location("Test.java",7,13));
        assertTrue(output.getFileName().contains("Test.java"));
        assertEquals(output.getLineNumber(),13);
        assertEquals(output.getColumnNumber(),17);
        output = server.jumpToDef(testFile, new Location("Test.java",8,13));
        assertTrue(output.getFileName().contains("Test.java"));
        assertEquals(output.getLineNumber(),19);
        assertEquals(output.getColumnNumber(),17);
    }

    @Test
    public void testJumpToDefOfField() throws Exception {
        File testFile = new File("src/test/java/org/langserver/server");
        Location output = server.jumpToDef(testFile, new Location("Test.java",7,9));
        assertTrue(output.getFileName().contains("Test.java"));
        assertEquals(output.getLineNumber(),4);
        assertEquals(output.getColumnNumber(),9);
    }

}