package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import com.pi4j.component.temperature.*;

public class Measures {
	public ArrayList<MeasurePoint> measureList = new ArrayList<MeasurePoint>();
	
	
	
	
	
	//konstruktor
	public Measures() {
		super();
		
		
	}

	

	@Override
	public String toString() {
		String ret = "";
		for(int i=0; i < measureList.size(); i++){
			ret += measureList.get(i).getSensorNumber() + "==>" + measureList.get(i).getValue() +";  ";
		}
		//System.out.println("  " + measureList.size());
		return ret;
	}
	
	
	

}
