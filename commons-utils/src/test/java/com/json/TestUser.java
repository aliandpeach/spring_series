package com.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonFilter("myFilter")
public class TestUser implements Serializable
{
    private static final long serialVersionUID = 5662352042763101470L;

    private String name;

    private int age;

    private String email;

    @JsonIgnore
    private String other;

    public TestUser()
    {
    }

    public TestUser(String name, int age, String email)
    {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
