package model;

public class Hamster extends Animal {

    public Hamster(String name, int age) {
        super(name, age);
    }

    @Override
    public String toString() {
        return "Hamster{" +
                "name='" + getName() + '\'' +
                ", age=" + getAge() +
                '}';
    }
}