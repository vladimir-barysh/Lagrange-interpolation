import java.io.IOException;
import java.util.*;

public class Main{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        double product, sum = 0;
        double[] x = null;
        double[] y = null;
        String fileName = "matrix.txt";

        try{
            double[][] result = ReadFromFile.ReadMatrix(fileName);
            x = result[0];
            y = result[1];
        }catch (IOException e){
            System.err.println("Error");
        }

        int size = x.length;

        System.out.println("x = {" + Arrays.toString(x) + "}");
        System.out.println("y = {" + Arrays.toString(y) + "}");

        System.out.print("Enter a point to Find it's value: ");
        double xPoint = sc.nextDouble();

        product = 1;
        // Peforming Arithmatic Operation
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (j != i) {
                    product *= (xPoint - x[j]) / (x[i] - x[j]);
                }
            }
            sum = sum + product * y[i];

            product = 1;    // Must set to 1
        }
        System.out.println("The value at point " + xPoint + " is : " + sum);

        // End of the Program
    }
}