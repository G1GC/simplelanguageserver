package org.langserver.model;

/**
 * Location - during input, it represents the file with location offset of an entity
 *          - during output, it represents the jump-to-def location of the entity given as input
 *
 */
public class Location {
    private String fileName;
    private int lineNumber;
    private int columnNumber;

    public Location() {

    }

    public Location(String fileName, int lineNum, int colNum) {
        this.fileName = fileName;
        this.lineNumber = lineNum;
        this.columnNumber = colNum;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    @Override
    public String toString() {
        return "Location{" +
                "fileName='" + fileName + '\'' +
                ", lineNumber=" + lineNumber +
                ", columnNumber=" + columnNumber +
                '}';
    }
}
