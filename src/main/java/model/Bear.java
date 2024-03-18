package model;

public class Bear extends Animal {

    public Bear(String name, int age) {
        super(name, age);
    }

    @Override
    public String toString() {
        return "Bear{" +
                "name='" + getName() + '\'' +
                ", age=" + getAge() +
                '}';
    }

    @Override
    public void eat() {
        System.out.println("eat bear");
    }

    public static void typeAnimal() {
        System.out.println("Я животное - медведь");
    }
}
