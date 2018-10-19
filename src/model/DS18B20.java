package model;

import java.util.Random;

import javax.swing.Timer;

public class DS18B20 extends MeasurePoint {

	@Override
	public void setName() {
		name = "TempDS18B20";
	}

	@Override
	public void setmeasureUnit() {
		measureUnit = "stC";

	}

	@Override
	public void setValue() {
		if(!simulationMode) {
		//odczyt wartoœci z modu³u DS18B20
		}
		else {
			 Random generator = new Random();
			 value = generator.nextFloat()*100;
		}
		
	}

	@Override
	public void setHardwareError() {
		hardwareError = true;
		error = true;
	}

	@Override
	public void setSoftwareError() {
		softwareError = true;
		error = true;
	}

	
	@Override
	public void setSensorNumber() {
		// numer odczytany z modu³u
		sensorNumber = "0x0000";
	}

	@Override
	public void setSensorBus() {
		sensorBus = "1- Wire";

	}
	

	@Override
	public String toString() {
		return "DS18B20 [name=" + name + ", measureUnit=" + measureUnit + ", value=" + value + ", hardwareError="
				+ hardwareError + ", softwareError=" + softwareError + ", error=" + error + ", sensorNumber="
				+ sensorNumber + ", sensorBus=" + sensorBus + ", avg1sec=" + avg1sec + "] " +" \n";
	}
	

	
	// K o n s t r u k t o r y
	public DS18B20(String name, String measureUnit, String sensorNumber, String sensorBus, boolean simulationMode, int readCycleTime) {
		super();
		this.name = name;
		this.measureUnit = measureUnit;
		this.value = value;
		this.hardwareError = true;
		this.softwareError = true;
		this.error = true;
		this.sensorNumber = sensorNumber;
		this.sensorBus = sensorBus;
		this.simulationMode = simulationMode;
		this.readCycleTime = readCycleTime;
		Timer timer = new Timer(readCycleTime, this);
		timer.start();
	}


	public DS18B20() {
		this("defaultname", "stC", "xxxx", "1-wire", false, 1000);
	}

	public DS18B20(String name) {
		this(name, "stC", "xxxx", "1-wire", false, 1000);
	}

	
}
