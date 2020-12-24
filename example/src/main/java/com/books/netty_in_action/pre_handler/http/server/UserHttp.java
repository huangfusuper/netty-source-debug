package com.books.netty_in_action.pre_handler.http.server;

public class UserHttp {
    private String name;
    private Integer age;

    public UserHttp() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public UserHttp(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
