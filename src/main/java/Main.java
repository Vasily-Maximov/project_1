import model.Animal;
import model.Cat;
import model.Dog;
import model.Hamster;

public class Main {

    public static void main(String[] args) {
        Animal cat = new Cat("Мурзик", 3);
        System.out.println(cat);
        Animal dog = new Dog("Эльза", 3);
        System.out.println(dog);
        Animal hamster = new Hamster("Хомка", 5);
        System.out.println(hamster);
    }
}
