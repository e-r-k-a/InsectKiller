package model;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.jfree.chart.*;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class IChart extends JFreeChart {
	

	public IChart(Plot plot, JFreeChart lineChart, JPanel panel) {
		super(plot);
		this.lineChart = lineChart;

		//panel wykres
		tabbedPane.addTab("Wykres", null, panel, null);
		panelWykres.setLayout(new BorderLayout());	
		JFreeChart lineChart = ChartFactory.createLineChart(
					 "wykres temperatur",
			         "Czas","Temperatura",
			         createDataset(),
			         PlotOrientation.VERTICAL,
			         true,true,false);		
			ChartPanel chartPanel = new ChartPanel( lineChart );
		    panelWykres.add(chartPanel, BorderLayout.CENTER);
		}
		
		private DefaultCategoryDataset createDataset( ) {
		      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		      dataset.addValue( 15 , "temp1" , "1" );
		      dataset.addValue( 30 , "temp1" , "2" );
		      
		      dataset.addValue( 60 , "temp2" ,  "1" );
		      dataset.addValue( 20 , "temp2" ,  "2" );
		      return dataset;
		   }
		
	
}
