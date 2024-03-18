import model.*;

public class Main {

    public static void main(String[] args) {
        Animal cat = new Cat("Мурзик", 3);
        System.out.println(cat);
        cat.eat();
        Animal dog = new Dog("Эльза", 3);
        System.out.println(dog);
        dog.eat();
        Animal hamster = new Hamster("Хомка", 5);
        System.out.println(hamster);
        hamster.eat();
        Animal bear = new Bear("Миша", 5);
        System.out.println(bear);
        bear.eat();
        bear.typeAnimal();
        System.out.printf("id = %d", Animal.getId());
    }
}