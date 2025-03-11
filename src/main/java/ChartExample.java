import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;


public class ChartExample {

    public static JFreeChart createChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1, "Доход", "Январь");
        dataset.addValue(4, "Доход", "Февраль");
        dataset.addValue(3, "Доход", "Март");
        dataset.addValue(5, "Доход", "Апрель");

        return ChartFactory.createLineChart(
                "Доход по месяцам",  // Заголовок
                "Месяцы",            // Ось X
                "Доход",             // Ось Y
                dataset);
    }

    /*SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("Простой график");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JFreeChart chart = ChartExample.createChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        frame.add(chartPanel);
        frame.setVisible(true);
    });*/
}