package project;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BarChartResults extends Application {
    private List<ResultsInfo> resultsInfo = new ArrayList<>();
    private List<ResultsInfo> relevantResultInfo = new ArrayList<>();
    private List<String> allLinesResults;
    private final String pathOut = "out.txt";
    private List<String> argumentsTemplate = new ArrayList<>();
    private List<String> networks = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Line Chart Sample");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Evidence Number");
        yAxis.setLabel("Time");
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        XYChart.Series NONE = new XYChart.Series();
        NONE.setName("NONE");
        XYChart.Series Node_pruning = new XYChart.Series();
        Node_pruning.setName("Node_pruning");
        XYChart.Series Edge_pruning = new XYChart.Series();
        Edge_pruning.setName("Edge_pruning");
        XYChart.Series m_separation = new XYChart.Series();
        m_separation.setName("M_Separation");


        NONE.getData().add(new XYChart.Data(1, 515));
        NONE.getData().add(new XYChart.Data(2, 469));
        NONE.getData().add(new XYChart.Data(3, 515));
        NONE.getData().add(new XYChart.Data(4, 643));
        NONE.getData().add(new XYChart.Data(5, 1562));
        NONE.getData().add(new XYChart.Data(6, 2367));

        Node_pruning.getData().add(new XYChart.Data(1, 16));
        Node_pruning.getData().add(new XYChart.Data(2, 5));
        Node_pruning.getData().add(new XYChart.Data(3, 141));
        Node_pruning.getData().add(new XYChart.Data(4, 110));
        Node_pruning.getData().add(new XYChart.Data(5, 304));
        Node_pruning.getData().add(new XYChart.Data(6, 94));


        Edge_pruning.getData().add(new XYChart.Data(1, 297));
        Edge_pruning.getData().add(new XYChart.Data(2, 265));
        Edge_pruning.getData().add(new XYChart.Data(3, 250));
        Edge_pruning.getData().add(new XYChart.Data(4, 381));
        Edge_pruning.getData().add(new XYChart.Data(5, 776));
        Edge_pruning.getData().add(new XYChart.Data(6, 1539));


        m_separation.getData().add(new XYChart.Data(1, 313));
        m_separation.getData().add(new XYChart.Data(2, 344));
        m_separation.getData().add(new XYChart.Data(3, 390));
        m_separation.getData().add(new XYChart.Data(4, 338));
        m_separation.getData().add(new XYChart.Data(5, 787));
        m_separation.getData().add(new XYChart.Data(6, 1589));

        lineChart.setTitle("Time Monitoring for Pruning, Hepar2");
        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().addAll(NONE,Node_pruning,Edge_pruning,m_separation);

        stage.setScene(scene);
        stage.show();
    }

    /**
     *
     * @param stage
     */

    /*@Override public void start(Stage stage) {
        //initialization of results
        allLinesResults = readFile(pathOut);
        setResultsInfo();
        argumentsTemplate = getParameters().getRaw();
        System.out.print(resultsInfo);
        getNetworks();

        //stage.setTitle("Pruning: " + argumentsTemplate.get(0));
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc =
                new BarChart<String,Number>(xAxis,yAxis);
        //bc.setTitle("Pruning: " + argumentsTemplate.get(0));
        xAxis.setLabel("Network");
        xAxis.tickLabelFontProperty().set(Font.font(15));
        xAxis.tickMarkVisibleProperty();
        yAxis.setLabel("Time");
        yAxis.tickMarkVisibleProperty();



        analyzeOrders(bc,"andes.xml");

        Scene scene  = new Scene(bc,400,300);
        stage.setScene(scene);
        stage.show();

    }*/

    /**
     * Crea il grafico per una determinata rete confrontando i tre tipi di ordini
     * (topological_order, Min degree order, Min fill Order)
     */
    private void analyzeOrders(BarChart<String,Number> bc, String network) {
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Topological_Order");
        System.out.println(getTimeForOrder(network,"Topological_Order"));
        series1.getData().add(new XYChart.Data(network, getTimeForOrder(network,"Topological_Order")));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Min_Degree_Order");
        System.out.println(getTimeForOrder(network,"Min_Degree_Order"));
        series2.getData().add(new XYChart.Data(network, getTimeForOrder(network,"Min_Degree_Order")));

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Min_Fill_order");
        System.out.println(getTimeForOrder(network,"Min_Fill_order"));
        series3.getData().add(new XYChart.Data(network, getTimeForOrder(network,"Min_Fill_Order")));
        bc.getData().addAll(series1, series2, series3);

    }

    private void createLineChartForPrunings() {

    }

    private List<String> readFile(String path) {
        List<String> allLines = new ArrayList<String>();
        try {
            allLines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allLines;
    }

    private void getNetworks() {
        for (ResultsInfo res : resultsInfo) {
            if (!networks.contains(res.getNetworkName())) {
                networks.add(res.getNetworkName() + "\n" + res.getPruningType());
            }
        }
        System.out.println(networks);
    }

    private void setResultsInfo() {
        for (String result : allLinesResults) {
            String formattedResult [] = result.split(" ");
            ResultsInfo resultInfo = new ResultsInfo(formattedResult[0],formattedResult[1],
                                                    formattedResult[3],Long.parseLong(formattedResult[2]),
                                                    Integer.parseInt(formattedResult[4]),
                                                    Integer.parseInt(formattedResult[5]),
                                                    Integer.parseInt(formattedResult[6]));
            resultsInfo.add(resultInfo);
        }
    }

    private long getTimeForOrder(String network, String order) {
        for (ResultsInfo res : resultsInfo) {
            String name = res.getNetworkName();
            if (name.equals(network) && res.getOrderType().equals(order)) {
                return res.getExecutionTime();
            }
        }
        return 0;
    }



    public static void main(String[] args) {
        launch(args);
    }
}
