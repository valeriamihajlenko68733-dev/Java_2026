import java.io.*;
import java.util.Arrays;

class User implements Serializable {
    String name;
    int age;
    
    User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

public class Main {

    
    // Стандартный метод equals 
    public boolean equals(Object obj) {
        return (this == obj);
    }
    
    // Глубокое сравнение через сериализацию
    static boolean deepEqualsViaSerialization(Object o1, Object o2) {
        byte[] b1 = serialize(o1);
        byte[] b2 = serialize(o2);
        return Arrays.equals(b1, b2);
    }
    
    // Глубокое копирование через сериализацию
    static <T> T deepCopyViaSerialization(T obj) {
        byte[] bytes = serialize(obj);
        return deserialize(bytes);
    }
    
    //ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ СЕРИАЛИЗАЦИИ 
    
    private static byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T deserialize(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    

    
    public static void main(String[] args) {
        User user1 = new User("Алексей", 25);
        User user2 = new User("Алексей", 25);
        
        System.out.println("user1.equals(user2): " + user1.equals(user2)); 
        System.out.println("deepEqualsViaSerialization: " + deepEqualsViaSerialization(user1, user2)); // true
        

        User copy = deepCopyViaSerialization(user1);
        System.out.println("\nГлубокое копирование:");
        System.out.println("  Оригинал: " + user1.name + ", " + user1.age);
        System.out.println("  Копия: " + copy.name + ", " + copy.age);
        System.out.println("  Это разные объекты: " + (user1 != copy));
    }
}