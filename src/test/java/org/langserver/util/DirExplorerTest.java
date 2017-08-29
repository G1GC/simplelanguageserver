package org.langserver.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirExplorerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File src;
    private File model;
    private File service;
    private File controller;
    private File modelFile;
    private File serviceFile;
    private File controllerFile;

    @Before
    public void setUp() throws Exception {
        src = folder.newFolder("src","main","java");
        model = folder.newFolder("src","main","java","test","model");
        service = folder.newFolder("src","main","java","test","service");
        controller = folder.newFolder("src","main","java","test","controller");
        modelFile =  new File(model.getPath()+"/Model.java");
        serviceFile = new File(service.getPath()+"/Service.java");
        controllerFile = new File(service.getPath()+"/Controller.java");
        modelFile.createNewFile();
        serviceFile.createNewFile();
        controllerFile.createNewFile();
    }

    @After
    public void tearDown() throws Exception {
        folder.delete();
    }

    @Test
    public void testExplore() throws Exception {
        List<String> fileNames = new ArrayList<>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file)->{
            fileNames.add(file.getName());

        }).explore(src);
        assertEquals(fileNames.size(), 3);
        assertTrue(fileNames.contains("Service.java"));
        assertTrue(fileNames.contains("Model.java"));
        assertTrue(fileNames.contains("Controller.java"));
    }

    @Test
    public void testExploreFilter() throws Exception {
        List<String> fileNames = new ArrayList<>();
        new DirExplorer((level, path, file) -> path.endsWith("Service.java"), (level, path, file)->{
            fileNames.add(file.getName());
        }).explore(src);
        assertTrue(fileNames.contains("Service.java"));
        assertFalse(fileNames.contains("Model.java"));
    }

}