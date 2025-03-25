import java.io.IOException;
import java.util.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.title.TextTitle;

public class Main{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int mainMode = 0;

        do {
            System.out.println("В каком режиме вы хотите работать?");
            System.out.println("1 - По заданной таблице значений функции определять приближенное\n" +
                    "значение функции в некоторой точке, вводимой пользователем");
            System.out.println("2 - По заданной аналитически функции y = f (x) и массиву значений аргумента\n" +
                    "(массив читается из файла) вычислить таблицу значений функции.");
            mainMode = sc.nextInt();
        }while(mainMode != 1 && mainMode != 2);

        switch (mainMode){
            case 1:{
                Mode1();
                break;
            }
            case 2:{
                Mode2();
                break;
            }
            default: break;
        }
    }

    private static void Mode1(){
        Scanner sc = new Scanner(System.in);
        double sum = 0;
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

        System.out.println("x = " + Arrays.toString(x));
        System.out.println("y = " + Arrays.toString(y));

        System.out.print("Укажите точку для интерполяции: ");
        double xPoint = sc.nextDouble();

        sum = lagrangeInterpolation(x, y, xPoint);

        System.out.printf("Интерполированное значение: %.8f", sum);
    }

    private static void Mode2() {
        // 1) Считываем x_i из файла
        String fileName = "matrix.txt";
        double[] xPoints = null;
        try{
            double[][] result = ReadFromFile.ReadMatrix(fileName);
            xPoints = result[0];
        }catch (IOException e){
            System.err.println("Error");
        }

        // Аналитическая функция f(x). Можно изменить по желанию.
        Function f = new Function() {
            @Override
            public double apply(double x) {
                return Math.exp(x);
            }
        };

        double[] yPoints = new double[xPoints.length];
        for (int i = 0; i < xPoints.length; i++) {
            yPoints[i] = f.apply(xPoints[i]);
        }

        // Если точек слишком много - возьмём, не более 15
        int maxPoints = 15;
        if (xPoints.length > maxPoints) {
            double[] tmpX = new double[maxPoints];
            double[] tmpY = new double[maxPoints];
            // Пример: берём первые maxPoints точек
            for (int i = 0; i < maxPoints; i++) {
                tmpX[i] = xPoints[i];
                tmpY[i] = yPoints[i];
            }
            xPoints = tmpX;
            yPoints = tmpY;
        }

        double minX = xPoints[0];
        double maxX = xPoints[0];
        for (int i = 1; i < xPoints.length; i++) {
            if (xPoints[i] < minX) minX = xPoints[i];
            if (xPoints[i] > maxX) maxX = xPoints[i];
        }

        // Зададим равномерный шаг для отрисовки
        // Число точек для прорисовки графиков
        int numPlotPoints = 200;
        double step = (maxX - minX) / (numPlotPoints - 1);

        // Создаём серии для графика
        XYSeries seriesF = new XYSeries("f(x)");
        XYSeries seriesP = new XYSeries("P(x) (интерп.)");

        // Для оценки максимального отклонения
        double maxDeviation = 0.0;
        double xOfMaxDeviation = minX;

        for (int i = 0; i < numPlotPoints; i++) {
            double xx = minX + i * step;
            double fx = f.apply(xx);
            double px = lagrangeInterpolation(xPoints, yPoints, xx);

            seriesF.add(xx, fx);
            seriesP.add(xx, px);

            double deviation = Math.abs(fx - px);
            if (deviation > maxDeviation) {
                maxDeviation = deviation;
                xOfMaxDeviation = xx;
            }
        }

        // Строим график с помощью JFreeChart
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesF);
        dataset.addSeries(seriesP);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Интерполяция Лагранжа",
                "x",
                "y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Добавляем точки из файла
        XYSeries seriesFilePoints = new XYSeries("Значения аргумента");
        for (int i = 0; i < xPoints.length; i++) {
            seriesFilePoints.add(xPoints[i], yPoints[i]);
        }
        dataset.addSeries(seriesFilePoints);

        // Настраиваем отображение точек
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        // Для серии f(x) - только линия
        renderer.setSeriesShapesVisible(0, false);
        // Для серии P(x) - только линия
        renderer.setSeriesShapesVisible(1, false);
        // Для серии точек из файла - только точки (без линии)
        renderer.setSeriesLinesVisible(2, false);
        renderer.setSeriesShapesVisible(2, true);

        double size = 3.0; // Размер точки
        double delta = size / 2.0; // Смещение для центрирования
        Shape circle = new Ellipse2D.Double(-delta, -delta, size, size);
        renderer.setSeriesShape(2, circle);
        renderer.setSeriesPaint(2, Color.BLACK);

        // Выводим результат по максимальному отклонению
        // Создаем текстовую аннотацию с информацией о максимальном отклонении
        String deviationText = String.format("Максимальное отклонение: %.6f в точке x = %.6f",
                maxDeviation, xOfMaxDeviation);

        TextTitle subtitle = new TextTitle(deviationText, new Font("SansSerif", Font.PLAIN, 12));
        chart.addSubtitle(subtitle);

        // Окно для отображения
        JFrame frame = new JFrame("График интерполяции");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        ChartPanel chartPanel = new ChartPanel(chart);
        frame.setContentPane(chartPanel);
        frame.setVisible(true);
    }

    private interface Function {
        double apply(double x);
    }

    private static double lagrangeInterpolation(double[] x, double[] y, double xPoint){
        int size = x.length;
        double product, sum = 0;
        for (int i = 0; i < size; i++) {

            product = 1;
            for (int j = 0; j < size; j++) {
                if (j != i) {
                    product *= (xPoint - x[j]) / (x[i] - x[j]);
                }
            }
            sum = sum + product * y[i];

        }
        return sum;
    }
}