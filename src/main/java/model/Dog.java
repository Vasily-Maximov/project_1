package model;

public class Dog extends Animal {

    public Dog(String name, int age) {
        super(name, age);
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + getName() + '\'' +
                ", age=" + getAge() +
                '}';
    }

    @Override
    public void eat() {
        System.out.println("eat dog");
    }
}