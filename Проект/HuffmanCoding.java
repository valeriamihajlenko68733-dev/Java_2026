import java.io.*;
import java.util.*;

public class HuffmanCoding {
    
    // Узел дерева Хаффмана
    static class Node implements Comparable<Node> {
        int frequency;
        byte data;
        Node left, right;
        boolean isLeaf;
        
        Node(byte data, int frequency) {
            this.data = data;
            this.frequency = frequency;
            this.isLeaf = true;
        }
        
        Node(Node left, Node right) {
            this.left = left;
            this.right = right;
            this.frequency = left.frequency + right.frequency;
            this.isLeaf = false;
        }
        
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }
    
    
    public void compress(String inputFile, String outputFile) throws IOException {
        // 1. Читаем файл и считаем частоты символов
        byte[] data = readFile(inputFile);
        Map<Byte, Integer> frequencies = calculateFrequencies(data);
        
        // 2. Строим дерево Хаффмана
        Node root = buildHuffmanTree(frequencies);
        
        // 3. Генерируем коды
        Map<Byte, String> codes = new HashMap<>();
        generateCodes(root, "", codes);
        
        // 4. Кодируем данные
        StringBuilder encodedData = new StringBuilder();
        for (byte b : data) {
            encodedData.append(codes.get(b));
        }
        
        // 5. Сохраняем в файл
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputFile))) {
            // Сохраняем словарь
            dos.writeInt(frequencies.size()); // N
            
            for (Map.Entry<Byte, Integer> entry : frequencies.entrySet()) {
                byte symbol = entry.getKey();
                String code = codes.get(symbol);
                
                dos.writeByte(symbol);           // символ
                dos.writeByte(code.length());    // длина кода
                writeBits(dos, code);             // сам код
            }
            
            // Сохраняем данные
            int bitLength = encodedData.length();
            dos.writeInt(bitLength);              // общее количество битов
            writeBits(dos, encodedData.toString()); // данные
        }
    }
    
    public void decompress(String inputFile, String outputFile) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(inputFile));
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            
            // 1. Читаем словарь
            int dictSize = dis.readInt();
            Map<String, Byte> reverseCodes = new HashMap<>();
            
            for (int i = 0; i < dictSize; i++) {
                byte symbol = dis.readByte();
                int codeLength = dis.readByte();
                String code = readBits(dis, codeLength);
                reverseCodes.put(code, symbol);
            }
            
            // 2. Читаем закодированные данные
            int bitLength = dis.readInt();
            String encodedData = readBits(dis, bitLength);
            
            // 3. Декодируем
            StringBuilder currentCode = new StringBuilder();
            for (char bit : encodedData.toCharArray()) {
                currentCode.append(bit);
                if (reverseCodes.containsKey(currentCode.toString())) {
                    fos.write(reverseCodes.get(currentCode.toString()));
                    currentCode = new StringBuilder();
                }
            }
        }
    }
    
    private byte[] readFile(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            return data;
        }
    }
    
    private Map<Byte, Integer> calculateFrequencies(byte[] data) {
        Map<Byte, Integer> frequencies = new HashMap<>();
        for (byte b : data) {
            frequencies.put(b, frequencies.getOrDefault(b, 0) + 1);
        }
        return frequencies;
    }
    
    private Node buildHuffmanTree(Map<Byte, Integer> frequencies) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        
        for (Map.Entry<Byte, Integer> entry : frequencies.entrySet()) {
            pq.offer(new Node(entry.getKey(), entry.getValue()));
        }
        
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            pq.offer(new Node(left, right));
        }
        
        return pq.poll();
    }
    
    private void generateCodes(Node node, String code, Map<Byte, String> codes) {
        if (node.isLeaf) {
            codes.put(node.data, code.isEmpty() ? "0" : code);
            return;
        }
        
        generateCodes(node.left, code + "0", codes);
        generateCodes(node.right, code + "1", codes);
    }
    
    private void writeBits(DataOutputStream dos, String bits) throws IOException {
        int bytes = (bits.length() + 7) / 8;
        byte[] buffer = new byte[bytes];
        
        for (int i = 0; i < bits.length(); i++) {
            if (bits.charAt(i) == '1') {
                int byteIndex = i / 8;
                int bitIndex = 7 - (i % 8);
                buffer[byteIndex] |= (1 << bitIndex);
            }
        }
        
        dos.write(buffer);
    }
    
    private String readBits(DataInputStream dis, int count) throws IOException {
        int bytes = (count + 7) / 8;
        byte[] buffer = new byte[bytes];
        dis.readFully(buffer);
        
        StringBuilder bits = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int byteIndex = i / 8;
            int bitIndex = 7 - (i % 8);
            int bit = (buffer[byteIndex] >> bitIndex) & 1;
            bits.append(bit);
        }
        
        return bits.toString();
    }
}