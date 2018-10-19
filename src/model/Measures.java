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

public class Measures implements ActionListener {
	public ArrayList<MeasurePoint> measureList = new ArrayList<MeasurePoint>();
	private int cycleTime = 1000;
	private Timer timer = new Timer(cycleTime, this);
	
	//odczyt wszystkich czujników i wypelnienie pól obiektów
	private void getDS18B20() {
		
		
		
		W1Master master = new W1Master();
		List<W1Device> w1Devices = master.getDevices(TmpDS18B20DeviceType.FAMILY_CODE);
		for (W1Device device : w1Devices) {
		    //this line is enought if you want to read the temperature
		    System.out.println("Temperature: " + ((TemperatureSensor) device).getTemperature());
		    //returns the temperature as double rounded to one decimal place after the point

		    try {
		        System.out.println("1-Wire ID: " + device.getId() +  " value: " + device.getValue());
		        //returns the ID of the Sensor and the  full text of the virtual file
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		
		
		
		System.out.println("jest odczyt\n");
	}
	
	//dodanie kolejnego czujnika do listy obs³ugiwanych
	public void add(MeasurePoint measurePoint) {
		measureList.add(measurePoint);
	}
	
	//konstruktor
	public Measures() {
		super();
		timer.start();	
	}

	//zmiana czêstotliwoœci odczytu wejœæ
	public void changeReadCycle(int newReadCycle) {
		cycleTime = newReadCycle;
		timer.setDelay(cycleTime);		
	}
	
//przerwanie od zegara
	@Override
	public void actionPerformed(ActionEvent arg0) {
		getDS18B20();//odczyt temperatur 
		
	}
	
	
	

}
