package com.zzy.eventbus;

public class EventBean {
    private String name ;
    private int age;

    public EventBean(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "EventBean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
