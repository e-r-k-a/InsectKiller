package model;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.*;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Application.Main;

/*klasa reprezentująca wykres czasowy
 * wszystkich temperatur z obiektu Measures
 */
public class TempChart {

	private int axisTime = 60;// [s]
	private int seriesCount = 0;
	private ArrayList<Double> values = new ArrayList<Double>();
	public JFreeChart chart;
	private Measures measures;
	// private TimeSeries seriesMin = new TimeSeries("temp. min");
	// private TimeSeries seriesMax = new TimeSeries("temp. max");

	private TimeSeriesCollection data = new TimeSeriesCollection();

	public TempChart(String title, Measures measures) {
		super();
		this.measures = measures;
		chart = ChartFactory.createTimeSeriesChart("wykres temperatur", "Czas", "Temperatura", data, true, true, false);
		final XYPlot plot = chart.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(axisTime * 1000); // seconds->ms
		axis = plot.getRangeAxis();
		axis.setRange(0.0, 100.0);
	}
	
	public void setValues(ArrayList<Double> values) {
		this.values  = values;
	}

	public void update(ArrayList<Double> values, ArrayList<String> descriptions) {
		final Millisecond now = new Millisecond();
		while(values.size() > seriesCount) { //jesli więcej serii niż 2 to trzeba
			data.addSeries(new TimeSeries(descriptions.get(data.getSeriesCount())));
			seriesCount++;
		}
		for(int i=0; i<seriesCount; i++) {
			data.getSeries(i).add(now, values.get(i));
		}
	}

}
