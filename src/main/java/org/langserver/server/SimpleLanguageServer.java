package org.langserver.server;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.Position;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserSymbolDeclaration;
import com.github.javaparser.symbolsolver.model.declarations.MethodDeclaration;
import com.github.javaparser.symbolsolver.model.declarations.ValueDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import org.langserver.model.Location;
import org.langserver.util.DirExplorer;
import org.langserver.util.NodeIterator;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 *
 * LanguageServer implementation, currently supports only jump-to-def,others are yet to be implemented.
 * Implementation is based on JavaParser. @see <a href="https://github.com/javaparser/javaparser">https://github.com/javaparser/javaparser</a>
 */
public class SimpleLanguageServer implements LanguageServer{

    private CombinedTypeSolver combinedTypeSolver;

    SimpleLanguageServer(CombinedTypeSolver combinedTypeSolver){
        this.combinedTypeSolver = combinedTypeSolver;
    }

    int getColumn(Optional<TokenRange> tokenRange, String identifier) {
        return tokenRange.map(t -> {
            int column = -1;
            JavaToken temp = t.getBegin();
            while (!temp.getText().equals(identifier) && temp.getNextToken().isPresent())
                temp = temp.getNextToken().get();
            if (temp.getText().equals(identifier) && temp.getRange().isPresent())
                column = temp.getRange().get().begin.column;
            return column;
        }).orElse(-1);
    }

    String getFileName(Node node) {
        Node temp = node;
        while (temp.getParentNode().isPresent() && !(temp instanceof CompilationUnit)) {
            temp = temp.getParentNode().get();
        }
        if (temp instanceof CompilationUnit) {
            CompilationUnit compilationUnit = (CompilationUnit) temp;
            if (compilationUnit.getStorage().isPresent())
                return compilationUnit.getStorage().get().getPath().toString();
        }
        return "";
    }

    void processMethodDeclaration(Node node, Location outputLocation){
        if (node.getParentNode().get() instanceof MethodCallExpr) {
            MethodDeclaration correspondingDeclaration = JavaParserFacade.get(combinedTypeSolver).solve((MethodCallExpr) node.getParentNode().get()).getCorrespondingDeclaration();
            if (correspondingDeclaration instanceof JavaParserMethodDeclaration) {
                JavaParserMethodDeclaration declaration = (JavaParserMethodDeclaration) correspondingDeclaration;
                Optional<Position> nodePosition = declaration.getWrappedNode().getBegin();
                if (nodePosition.isPresent()) {
                    String identifierName = ((SimpleName) node).getIdentifier();
                    int col = getColumn(declaration.getWrappedNode().getTokenRange(), identifierName);
                    String fileName = getFileName(declaration.getWrappedNode());
                    outputLocation.setLineNumber(nodePosition.get().line);
                    outputLocation.setColumnNumber(col);
                    outputLocation.setFileName(fileName);
                }
            }
        }
    }

    void processFieldDeclaration(ValueDeclaration valueDeclaration, String identifierName, Location outputLocation){
        JavaParserFieldDeclaration declaration = (JavaParserFieldDeclaration) valueDeclaration;
        Optional<Position> nodePosition = declaration.getWrappedNode().getBegin();
        if (nodePosition.isPresent()) {
            outputLocation.setLineNumber(nodePosition.get().line);
            outputLocation.setColumnNumber(getColumn(declaration.getWrappedNode().getTokenRange(), identifierName));
            outputLocation.setFileName(getFileName(declaration.getWrappedNode()));
        }
    }

    void processSymbolDeclaration(ValueDeclaration valueDeclaration, String identifierName, Location outputLocation){
        JavaParserSymbolDeclaration declaration = (JavaParserSymbolDeclaration) valueDeclaration;
        Optional<Position> nodePosition = declaration.getWrappedNode().getBegin();
        if (nodePosition.isPresent()) {
            outputLocation.setLineNumber(nodePosition.get().line);
            outputLocation.setColumnNumber(getColumn(declaration.getWrappedNode().getTokenRange(), identifierName));
            outputLocation.setFileName(getFileName(declaration.getWrappedNode()));
        }
    }

    void processParameterDeclaration(ValueDeclaration valueDeclaration, String identifierName, Location outputLocation){
        JavaParserParameterDeclaration declaration = (JavaParserParameterDeclaration) valueDeclaration;
        Optional<Position> nodePosition = declaration.getWrappedNode().getBegin();
        if (nodePosition.isPresent()) {
            outputLocation.setLineNumber(nodePosition.get().line);
            outputLocation.setColumnNumber(getColumn(declaration.getWrappedNode().getTokenRange(), identifierName));
            outputLocation.setFileName(getFileName(declaration.getWrappedNode()));
        }
    }

    public Location jumpToDef(File projectDir, Location inputLocation) {
        final Location outputLocation = new Location();
        new DirExplorer((level, path, file) -> path.endsWith(inputLocation.getFileName()), (level, path, file) -> {
            try {
                new NodeIterator(node -> {
                    Position pos = node.getBegin().orElse(new Position(-1,-1));
                    if (pos.line == inputLocation.getLineNumber() && pos.column == inputLocation.getColumnNumber()) {
                        if (node instanceof SimpleName) {
                            processMethodDeclaration(node,outputLocation);
                        } else if (node instanceof NameExpr) {
                            ValueDeclaration valueDeclaration = JavaParserFacade.get(combinedTypeSolver).solve((NameExpr) node).getCorrespondingDeclaration();
                            String identifierName = ((NameExpr) node).getNameAsString();
                            if (valueDeclaration instanceof JavaParserFieldDeclaration) {
                                processFieldDeclaration(valueDeclaration,identifierName,outputLocation);
                            } else if (valueDeclaration instanceof JavaParserSymbolDeclaration) {
                                processSymbolDeclaration(valueDeclaration,identifierName,outputLocation);
                            } else if (valueDeclaration instanceof JavaParserParameterDeclaration) {
                                processParameterDeclaration(valueDeclaration,identifierName,outputLocation);
                            }
                        }
                    }
                    return true;
                }).explore(JavaParser.parse(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).explore(projectDir);
        return outputLocation;
    }

    @Override
    public String hoverText(File projectDir, Location inputLocation) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public Set<Location> references(File projectDir, Location inputLocation) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
