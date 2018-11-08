package model;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.*;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/*klasa reprezentująca wykres czasowy
 * wszystkich temperatur z obiektu Measures
 */
public class TempChart {

	public JFreeChart chart;
	private Measures measures;
	private XYSeries series = new XYSeries("temperatury");
	private XYSeriesCollection data = new XYSeriesCollection(series);

	private int licznikProbek = 0;

	public TempChart(String title, Measures measures) {
		super();
		this.measures = measures;
		chart = ChartFactory.createXYLineChart("wykres temperatur", "Czas", "Temperatura", data,
				PlotOrientation.VERTICAL, true, true, false);
		/*
		 * series.add(1.0, 500.2); series.add(5.0, 694.1); series.add(4.0, 100.0);
		 * series.add(12.5, 734.4); series.add(17.3, 453.2); series.add(21.2, 500.2);
		 * series.add(21.9, null);
		 */
	}

	public void update() {
		for (int i = 0; i < measures.measureList.size(); i++) {
			series.add(licznikProbek, measures.measureList.get(i).getValue());

			// seriesList.get(i).add(licznikProbek, measures.measureList.get(i).getValue());
			// dataset.addSeries(series.get(0).);.
			// addValue(measures.measureList.get(0).getValue(),
			// "temp"+Integer.toString(0),//measures.measureList.get(i).getName(),
			// Integer.toString(licznikProbek++));
			// dataset.addValue(measures.measureList.get(1).getValue(),
			// "temp"+Integer.toString(1),//measures.measureList.get(i).getName(),
			// Integer.toString(licznikProbek++));

			// dataset.addValue( 60 , "temp1" , "1" ); dataset.addValue( 50 , "temp1" ,
			// "2");
			// dataset.addValue( 50 , "temp1" , "1" ); dataset.addValue( 50 , "temp1" ,
			// "2");

		}
		licznikProbek++;
	}
}

/*
 * private DefaultCategoryDataset createDataset() { dataset = new
 * DefaultCategoryDataset(); /* dataset.addValue( 60 , "temp1" , "1" );
 * dataset.addValue( 50 , "temp1" , "2" ); dataset.addValue( 50 , "temp2" , "1"
 * ); dataset.addValue( 40 , "temp2" , "2" );
 */
// return dataset;
// }*/
// }
