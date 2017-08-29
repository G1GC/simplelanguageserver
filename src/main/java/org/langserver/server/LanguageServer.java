package org.langserver.server;

import org.langserver.model.Location;

import java.io.File;
import java.util.Set;

/**
 *
 * LanguageServer interface that describes the
 * operations this server can perform
 */
public interface LanguageServer {

    /**
     *
     * given a location, returns the corresponding "jump-to-def"
     * location (where the entity is declared)
     *
     * @param projectDir
     * @param inputLocation
     * @return
     */
    Location jumpToDef(File projectDir, Location inputLocation);

    /**
     *
     * given a location (a character offset in a file), returns the "hover tooltip"
     * (context for the entity at that location;
     * this would typically be type information and/or doc string)
     *
     * @param projectDir
     * @param inputLocation
     * @return
     */
    String hoverText(File projectDir, Location inputLocation);

    /**
     *
     * given a location, returns all the locations where the entity
     * at that location is referenced (including its declaration)
     *
     * @param projectDir
     * @param inputLocation
     * @return
     */
    Set<Location> references(File projectDir, Location inputLocation);
}
