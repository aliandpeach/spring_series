package com.demo.lambda;

import java.util.HashMap;
import java.util.Map;

public class UserModel {

    private int age;

    private String name;

    private double weight;

    private String hairColor;


    public static Map<Integer, String> more(Integer key, String val) {
        Map<Integer, String> r = new HashMap<>();
        r.put(key, val);
        return r;
    }

    public Map<Integer, String> increment(Integer key, String val) {
        Map<Integer, String> r = new HashMap<>();
        r.put(key, val);
        return r;
    }

    public UserModel() {
    }

    public UserModel(int age, String name, double weight, String hairColor) {
        this.age = age;
        this.name = name;
        this.weight = weight;
        this.hairColor = hairColor;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", hairColor='" + hairColor + '\'' +
                '}' + "\n\r";
    }
}
