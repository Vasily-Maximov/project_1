package model;

public abstract class Animal {

     private String name;

     private int age;

     private static int id;

    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
        id++;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public static int getId() {
        return id;
    }

    abstract public void eat();

    public static void typeAnimal() {
        System.out.println("Я животное");
    }
}
