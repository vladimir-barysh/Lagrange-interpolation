import java.io.*;
import java.util.*;

public class ReadFromFile {

    public static double[][] ReadMatrix(String fileName) throws IOException {
        List<Double> xList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String line;
            while ((line = br.readLine()) != null){
                String[] values = line.trim().split("\\s+");
                if (values.length != 2){
                    throw new IOException("Некорректный ввод строки");
                }
                xList.add(Double.parseDouble(values[0]));
                yList.add(Double.parseDouble(values[1]));
            }
        }catch (IOException e){
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }

        double[] x = xList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] y = yList.stream().mapToDouble(Double::doubleValue).toArray();

        return new double[][]{x,y};
    }
}