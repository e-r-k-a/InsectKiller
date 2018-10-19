package model;


import java.util.TimerTask;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
 

public abstract class MeasurePoint implements ActionListener {

	String name="";
	String measureUnit="";
	float value=0;
	boolean hardwareError=true;
	boolean softwareError=true;
	boolean error=true;
	String sensorNumber="";
	String sensorBus="";
	boolean simulationMode=false;
	int readCycleTime = 1000;// in ms
	
	float avg1sec;
		@Override
		public void actionPerformed(ActionEvent e) {
			this.setValue(); //odczyt wartoœci przez czujnik
			 System.out.println(this.toString());	
		}		
		
		
	

	public abstract void setName();
	public abstract void setmeasureUnit();
	public abstract void setValue();
	public abstract void setHardwareError();
	public abstract void setSoftwareError();
	public abstract void setSensorNumber();
	public abstract void setSensorBus();

	public void setSimulationMode(boolean simulationMode) {
		this.simulationMode = simulationMode;

	};
	
	//set read cycle time
	public void setReadCycleTime(int readCycleTime) {
		this.readCycleTime = readCycleTime;

	};
	
	

	
}

