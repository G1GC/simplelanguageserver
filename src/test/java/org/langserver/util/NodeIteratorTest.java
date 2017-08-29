package org.langserver.util;

import com.github.javaparser.ast.Node;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;


public class NodeIteratorTest {
    @Test
    public void testExplore() throws Exception {
        Node node = mock(Node.class);
        Node child1 = mock(Node.class);
        Node child2 = mock(Node.class);
        Node child21 = mock(Node.class);
        Node child22 = mock(Node.class);
        List<Node> nodes = new ArrayList<>();
        nodes.add(child1);
        nodes.add(child2);
        List<Node> child2Nodes = new ArrayList<>();
        child2Nodes.add(child21);
        child2Nodes.add(child22);
        when(node.getChildNodes()).thenReturn(nodes);
        when(child2.getChildNodes()).thenReturn(child2Nodes);
        NodeIterator.NodeHandler handler = mock(NodeIterator.NodeHandler.class);
        when(handler.handle(node)).thenReturn(true);
        new NodeIterator(handler).explore(node);
        verify(handler, times(1)).handle(node);
        verify(handler, times(1)).handle(child1);
        verify(handler, times(1)).handle(child2);
        verify(handler, times(0)).handle(child21);
        verify(handler, times(0)).handle(child22);
    }

}