package model;


import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;

import javax.swing.Timer;

import com.pi4j.io.w1.W1Device;
 
/* jedna punkt pomiarowy */
public abstract class MeasurePoint implements ActionListener {

	final public int DEFAULT_READ_CYCLE = 1000;//domyślny czas odczytu pomiaru w ms
	
	private String name="";	//nazwa pomiaru
	private String measureUnit="";//jednostka pomiaru
	private double value=0;//vartość aktualna
	private boolean hardwareError=true;//flaga błędu 1-błąd
	private String sensorNumber="";//ID czujnika
	private String sensorBus="";//typ magistrali do której przylaczony jest czujnik
	private boolean simulationMode=false;//tryb symulacji pomiaru
	private LocalTime measureTime = LocalTime.now();
	private int cycleTime = 1000;
	private Timer timer = new Timer(cycleTime, this);
	
	
	//settery
	public void setName(String name){
		this.name = name;
	}
	public void setMeasureUnit(String unit){
		this.measureUnit = measureUnit;
	}
	public void setValue(double value){
		if(!simulationMode) {
			this.value = value;
		}
		else {
			 Random generator = new Random();
			 value = generator.nextFloat()*100;
		}
	}
	public void setHardwareError(boolean error){
		hardwareError = error;
	}
	public void setMeasureTime(LocalTime localTime){
		this.measureTime = localTime;
	}
	public void setSensorNumber(String s){
		sensorNumber = s;
	}
	public void setSensorBus(String sensorBus){
		this.sensorBus = sensorBus;
	}
	//gettery
	public String getSensorNumber(){
		return sensorNumber;
	}
	public Timer getTimer(){
		return timer;
	}
	public Double getValue(){
		return value;
	}
	public String getName(){
		return name;
	}
	public LocalTime getMeasureTime() {
		return measureTime;
	}
	public void changeReadCycle(int newCycle){
		timer.stop();
		cycleTime = newCycle;
		timer.setDelay(cycleTime);
		timer.start();
		
	}
	
	public void setSimulationMode(boolean simulationMode) {
		this.simulationMode = simulationMode;

	};
	
	
	//porównanie numerów ID dwóch punktów
	public boolean compareSerial(MeasurePoint mp1,  MeasurePoint mp2) {
		if((!mp1.getSensorNumber().isEmpty())&&(mp1.getSensorNumber().equals(mp2.getSensorNumber())))	{
			return true;
		}else {
			return false;
		}
	}

}

