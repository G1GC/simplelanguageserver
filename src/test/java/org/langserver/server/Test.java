package org.langserver.server;

public class Test {
    Foo foo = new Foo();
    public void testMethod(){
        Bar bar = new Bar();
        foo.testFoo();
        bar.testBar();
    }
}

class Foo {
    public void testFoo(){
        System.out.print("Testing foo!!");
    }
}

class Bar {
    public void testBar(){
        System.out.print("Testing bar!!");
    }
}