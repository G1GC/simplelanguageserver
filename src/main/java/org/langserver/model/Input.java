package org.langserver.model;

import java.util.List;

/**
 * An entity to hold the input JSON.
 *
 * e.g below
 * {"sourceFolders":["src/main/java"],
 **** "location":{
 ******* "fileName":"CircuitCreationTask.java",
 ******* "lineNumber":158,
 ******* "columnNumber":69
 **** }
 *  }
 */
public class Input {
    List<String> sourceFolders;//Folders to search for the given file

    Location location;

    public List<String> getSourceFolders() {
        return sourceFolders;
    }

    public void setSourceFolders(List<String> sourceFolders) {
        this.sourceFolders = sourceFolders;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
