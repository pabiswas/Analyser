package com.nsn.tsmanalyser;

import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.jCharts.Chart;
import org.jCharts.axisChart.*;
import org.jCharts.chartData.AxisChartDataSet;
import org.jCharts.chartData.DataSeries;
import org.jCharts.chartData.PieChartDataSet;
import org.jCharts.encoders.PNGEncoder;
import org.jCharts.nonAxisChart.PieChart2D;
import org.jCharts.properties.AxisProperties;
import org.jCharts.properties.BarChartProperties;
import org.jCharts.properties.ChartProperties;
import org.jCharts.properties.LegendProperties;
import org.jCharts.properties.PieChart2DProperties;
import org.jCharts.types.ChartType;

/**
 * @author pabiswas
 *
 */
public class utils {

    public static String getTitle(String text) {
        Pattern incomingMsg = Pattern.compile("(Title)(.*)\n");
        Matcher m = incomingMsg.matcher(text);

        if (m.find()) {
            return m.group();
        } else {
            return "";
        }
    }

    public static String findPattern(String pattern, String line) {
        // TODO Auto-generated method stub
        Pattern msgPattern = Pattern.compile(pattern);
        Matcher m = msgPattern.matcher(line);
        if (m.find()) {
            line = m.group();
            return line;
        }
        return null;
    }

    public static void generateCharts(HashMap<String, Integer> hmap) throws Throwable {
        // TODO Auto-generated method stub
        basicChart(hmap);
    }

    private static void basicChart(HashMap<String, Integer> hmap) throws Throwable {
        PieChart2DProperties pieChart2DProperties = new PieChart2DProperties();
        outputChart(pieChart2DProperties, hmap, "pieChartBasic");
    }

    private static void outputChart(PieChart2DProperties pieChart2DProperties, HashMap<String, Integer> hmap, String name) throws Throwable {
        Set<Map.Entry<String, Integer>> set = hmap.entrySet();
        Iterator<Entry<String, Integer>> it = set.iterator();

//		System.out.println("Size of hmap : " + set.size());

        Random numgen = new Random();

        String[] errorLabel = new String[set.size()];
        double[] errorData = new double[set.size()];
        Paint[] colorValue = new Paint[set.size()];
        double[][] barData = new double[1][set.size()];
        int i = 0;

        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();

            errorLabel[i] = (String) me.getKey();
            errorData[i] = (Integer) me.getValue();
            colorValue[i] = new Color(numgen.nextInt(256), numgen.nextInt(256), numgen.nextInt(256));
            barData[0][i] = (Integer) me.getValue();
            i++;
        }

        //--- Pie Chart ---//
//		PieChartDataSet pieChartDataSet= new PieChartDataSet( "Errors", errorData, errorLabel, colorValue, pieChart2DProperties );
//
//		PieChart2D pieChart2D= new PieChart2D( pieChartDataSet, new LegendProperties(), new ChartProperties(), 500, 350 );
//		exportImage( pieChart2D, name );

        //--- Bar Chart --//
        String xAxisTitle = "Protocol Errors";
        String yAxisTitle = "Count";
        String title = "Protocol Error count";
        DataSeries dataSeries = new DataSeries(errorLabel, xAxisTitle, yAxisTitle, title);

        String[] legendLabels = {"Errors"};
        Paint[] paints = new Paint[]{Color.blue.darker()};
        BarChartProperties barChartProperties = new BarChartProperties();
        AxisChartDataSet axisChartDataSet = new AxisChartDataSet(barData, legendLabels, paints, ChartType.BAR, barChartProperties);
        dataSeries.addIAxisPlotDataSet(axisChartDataSet);

        ChartProperties chartProperties = new ChartProperties();
        AxisProperties axisProperties = new AxisProperties();
        LegendProperties legendProperties = new LegendProperties();
        AxisChart axisChart = new AxisChart(dataSeries, chartProperties, axisProperties, legendProperties, 1000, 300);

        exportImage(axisChart, "barChart");
    }

    static void exportImage(Chart chart, String fileName) {
        String extension = ".png";
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = new FileOutputStream(fileName + extension);
            PNGEncoder.encode(chart, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void generatePieChart(HashMap<String, Integer> statisticsMap) throws Throwable {
        PieChart2DProperties pieChart2DProperties = new PieChart2DProperties();
        Set<Map.Entry<String, Integer>> set = statisticsMap.entrySet();
        Iterator<Entry<String, Integer>> it = set.iterator();

        String[] errorLabel = new String[set.size()];
        double[] errorData = new double[set.size()];
        Paint[] colorValue = {Color.GREEN, Color.RED, Color.GRAY};
        double[][] barData = new double[1][set.size()];
        int i = 0;

        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();

            errorLabel[i] = (String) me.getKey();
            errorData[i] = (Integer) me.getValue();
            if (errorLabel[i].contains("Passed")) {
                colorValue[i] = Color.GREEN;
            } else if (errorLabel[i].contains("Failed")) {
                colorValue[i] = Color.RED;
            } else {
                colorValue[i] = Color.GRAY;
            }
            barData[0][i] = (Integer) me.getValue();
            i++;
        }

        //--- Pie Chart ---//
        PieChartDataSet pieChartDataSet = new PieChartDataSet("Errors", errorData, errorLabel, colorValue, pieChart2DProperties);

        PieChart2D pieChart2D = new PieChart2D(pieChartDataSet, new LegendProperties(), new ChartProperties(), 500, 350);
        exportImage(pieChart2D, "Statistics");
    }

    public static File readResource(String suiteName, String fileName) throws IOException, FileNotFoundException {

        InputStream inputStream = com.nsn.tsmanalyser.tsmAnalyser.class.getResourceAsStream(suiteName);
        File tmpFile = File.createTempFile(fileName, "txt");
        tmpFile.deleteOnExit();
        assert (tmpFile.exists()) : "could not create tempfile";
        OutputStream outStream = new FileOutputStream(tmpFile);
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = inputStream.read(bytes)) != -1) {
            outStream.write(bytes, 0, read);
        }
        return tmpFile;
    }

    public static void applyXslt(File xmlFile, File xsltFile, String fileName) throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
        javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(xmlFile);
        javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(xsltFile);
        javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(new File(fileName));

        javax.xml.transform.TransformerFactory transFactory = javax.xml.transform.TransformerFactory.newInstance();

        javax.xml.transform.Transformer trans = transFactory.newTransformer(xsltSource);

        trans.transform(xmlSource, result);
    }
}
