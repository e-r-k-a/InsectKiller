package model;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;

import org.jfree.chart.*;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class TempChart {
	
	public JFreeChart chart;
	private Measures measures;
	private DefaultCategoryDataset dataset; 
	
	public TempChart(String title, Measures measures) {
		super();
		this.measures = measures;
		chart = ChartFactory.createLineChart(
					 "wykres temperatur",
			         "Czas","Temperatura",
			         createDataset(),
			         PlotOrientation.VERTICAL,
			         true,true,false);		
	}
		
	public void update() {
		 for(int i=0; i<measures.measureList.size(); i++) {
	    	  dataset.addValue( measures.measureList.get(i).getValue() , "temp"+measures.measureList.get(i).getName() , Integer.toString(i));    	  
	      }
	}

	private DefaultCategoryDataset createDataset( ) {
		      dataset = new DefaultCategoryDataset( );
		 /*     dataset.addValue( 60 , "temp1" ,  "1" );
		      dataset.addValue( 50 , "temp1" ,  "2" );
		      dataset.addValue( 50 , "temp2" ,  "1" );
		      dataset.addValue( 40 , "temp2" ,  "2" );*/
		      return dataset;
	}	
}
