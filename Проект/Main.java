import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
            return;
        }

        String command = args[0];

        try {
            if (command.equalsIgnoreCase("-c") || command.equalsIgnoreCase("--compress")) {
                if (args.length < 2) {
                    System.out.println(" Ошибка: укажите входной файл");
                    printUsage();
                    return;
                }
                String inputFile = args[1];
                String outputFile = args.length > 2 ? args[2] : inputFile + ".huff";
                
                System.out.println(" Кодирование файла: " + inputFile);
                HuffmanCoding huffman = new HuffmanCoding();
                huffman.compress(inputFile, outputFile);
                System.out.println(" Файл закодирован: " + outputFile);
                printStats(inputFile, outputFile);
            } 
            else if (command.equalsIgnoreCase("-d") || command.equalsIgnoreCase("--decompress")) {
                if (args.length < 2) {
                    System.out.println(" Ошибка: укажите входной файл");
                    printUsage();
                    return;
                }
                String inputFile = args[1];
                String outputFile = args.length > 2 ? args[2] : inputFile.replace(".huff", "_decoded.txt");
                
                System.out.println(" Декодирование файла: " + inputFile);
                HuffmanCoding huffman = new HuffmanCoding();
                huffman.decompress(inputFile, outputFile);
                System.out.println(" Файл декодирован: " + outputFile);
            } 
            else if (command.equalsIgnoreCase("-t") || command.equalsIgnoreCase("--test")) {
                runTests();
            }
            else {
                System.out.println(" Неизвестная команда: " + command);
                printUsage();
            }
        } catch (Exception e) {
            System.err.println(" Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println("Использование:");
        System.out.println("  java Main -c <входной_файл> [выходной_файл]  - кодирование");
        System.out.println("  java Main -d <входной_файл> [выходной_файл]  - декодирование");
        System.out.println("  java Main -t                                   - тестирование");
        System.out.println();
        System.out.println("Примеры:");
        System.out.println("  java Main -c test.txt encoded.bin");
        System.out.println("  java Main -d encoded.bin decoded.txt");
        System.out.println("  java Main -t");
    }

    private static void printStats(String inputFile, String outputFile) throws IOException {
        File inFile = new File(inputFile);
        File outFile = new File(outputFile);
        
        long inSize = inFile.length();
        long outSize = outFile.length();
        double ratio = (1 - (double) outSize / inSize) * 100;
        
        System.out.println();
        System.out.println(" Статистика сжатия:");
        System.out.println("  Исходный размер: " + inSize + " байт");
        System.out.println("  Сжатый размер:   " + outSize + " байт");
        System.out.printf("  Сжатие:          %.2f%%\n", ratio);
        System.out.println("  Коэффициент:     " + (double) inSize / outSize);
    }

    private static void runTests() throws Exception {
        System.out.println(" Запуск тестирования алгоритма Хаффмана\n");
        
        // Тест 1: 10 одинаковых символов
        System.out.println(" Тест 1: 10 одинаковых символов");
        System.out.println("-".repeat(50));
        createTestFile("test1.txt", "1111111111");
        testFile("test1.txt");
        
        // Тест 2: 3 разных символа (10, 5, 5)
        System.out.println("\n Тест 2: 3 разных символа (10x '1', 5x '2', 5x '3')");
        System.out.println("-".repeat(50));
        createTestFile("test2.txt", "11111111112222233333");
        testFile("test2.txt");
        
        // Тест 3: class-файл
        System.out.println("\n Тест 3: Бинарный файл (.class)");
        System.out.println("-".repeat(50));
        
        // Проверяем, существует ли Main.class
        File classFile = new File("Main.class");
        if (classFile.exists()) {
            testFile("Main.class");
        } else {
            System.out.println("  Main.class не найден, сначала скомпилируйте программу");
            System.out.println("   Выполните: javac Main.java HuffmanCoding.java");
        }
        
        System.out.println("\n Все тесты завершены!");
    }
    
    private static void createTestFile(String filename, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        }
        System.out.println("  Создан тестовый файл: " + filename + " (" + content.length() + " байт)");
    }
    
    private static void testFile(String filename) throws Exception {
        HuffmanCoding huffman = new HuffmanCoding();
        
        // Кодирование
        String encoded = filename + ".huff";
        huffman.compress(filename, encoded);
        
        // Декодирование
        String decoded = filename + ".decoded";
        huffman.decompress(encoded, decoded);
        
        // Проверка
        if (filesEqual(filename, decoded)) {
            System.out.println("   Кодирование/декодирование успешно!");
        } else {
            System.out.println("   Ошибка: декодированный файл не совпадает с оригиналом!");
        }
        
        printStats(filename, encoded);
        
        // Показываем содержимое для текстовых файлов
        if (filename.endsWith(".txt")) {
            showContent(filename, decoded);
        }
    }
    
    private static boolean filesEqual(String file1, String file2) throws IOException {
        try (FileInputStream fis1 = new FileInputStream(file1);
             FileInputStream fis2 = new FileInputStream(file2)) {
            
            int b1, b2;
            while ((b1 = fis1.read()) != -1 && (b2 = fis2.read()) != -1) {
                if (b1 != b2) return false;
            }
            return fis1.read() == fis2.read();
        }
    }
    
    private static void showContent(String original, String decoded) throws IOException {
        try (BufferedReader reader1 = new BufferedReader(new FileReader(original));
             BufferedReader reader2 = new BufferedReader(new FileReader(decoded))) {
            String origContent = reader1.readLine();
            String decContent = reader2.readLine();
            System.out.println("  Оригинал:  \"" + origContent + "\"");
            System.out.println("  Декодировано: \"" + decContent + "\"");
        }
    }
}