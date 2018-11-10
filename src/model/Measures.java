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

	// konstruktor
	public Measures() {
		super();
	}

	// maxMeasures - wybiera maksymaln� warto�� z listy pomiar�w
	public MeasurePoint getMaxMeasure() {
		int indexMax = 0;
		Double valueMax = Double.MIN_VALUE;
		for (int i = 0; i < measureList.size(); i++) {
			if (measureList.get(i).getValue() > valueMax) {
				indexMax = i;
				valueMax = measureList.get(i).getValue();
			}
		}
		return measureList.get(indexMax);
	}

	// minMeasures - wybiera minimalna warto�� z listy pomiar�w
	public MeasurePoint getMinMeasure() {
		int indexMin = 0;
		Double valueMin = Double.MAX_VALUE;
		for (int i = 0; i < measureList.size(); i++) {
			if (measureList.get(i).getValue() < valueMin) {
				indexMin = i;
				valueMin = measureList.get(i).getValue();
			}
		}
		return measureList.get(indexMin);
	}

	@Override
	public String toString() {
		String ret = "";
		for (int i = 0; i < measureList.size(); i++) {
			ret += measureList.get(i).getSensorNumber() + "==>" + measureList.get(i).getValue() + ";  ";
		}
		// System.out.println(" " + measureList.size());
		return ret;
	}

}
