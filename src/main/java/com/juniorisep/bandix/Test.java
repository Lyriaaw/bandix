package com.juniorisep.bandix;

public class Test implements Cloneable {


    private Test test;
    private String name;

    public Test(String name) {
        this.name = name;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
