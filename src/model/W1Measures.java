package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Master;
import com.pi4j.io.w1.W1Device;

public class W1Measures extends Measures implements ActionListener{

	private W1Master master = new W1Master();
	private List<W1Device> w1DeviceList = new ArrayList<W1Device>();
	private int watchdogTime = 10000;//co 10sekund sprawdzanie czy jest coś się zmieniło na W1
	private Timer watchdog = new Timer(watchdogTime, this);
	
	public W1Master getW1Master(){
		return master;
	}
//	public abstract void readDevice(W1Device device); //odczyt urządzenia W1

	//konstruktor
	public W1Measures() {
		super();
		w1DeviceList = master.getDevices(); //odczyt listy wszytkich urządzeń W1
		for(int i=0; i < w1DeviceList.size(); i++){
			W1Device tmpDevice = w1DeviceList.get(i);
			DS18B20 ds18B20 = new DS18B20(tmpDevice.getName(), tmpDevice.getId(), false, tmpDevice);
			measureList.add(ds18B20);
		}
		watchdog.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		master.checkDeviceChanges();
		w1DeviceList = master.getDevices();//ponowny odczyt
//		System.out.println("JEST!!!" + w1DeviceList.size() + "   "+ measureList.size());
		
		if(w1DeviceList.size() > measureList.size()){
			
			System.out.println("dodany czujnik!!!!!!!!!!!!!!!!!");
		}
		if(w1DeviceList.size() > measureList.size()){
			
			System.out.println("zniknął czujnik!!!!!!!!!!!!!!!!!");
		}
		
		
	}
	
	public int count() {
		return measureList.size();
	}

	
	
	
	
	
	
	
}
