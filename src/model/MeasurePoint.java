package model;


import java.util.TimerTask;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
 

public abstract class MeasurePoint {//implements ActionListener {

	String name="";
	String measureUnit="";
	float value=0;
	boolean hardwareError=true;
	String sensorNumber="";
	String sensorBus="";
	boolean simulationMode=false;
	
	float avg1sec;
		
	

	public abstract void setName();
	public abstract void setmeasureUnit();
	public abstract void setValue();
	public abstract void setHardwareError();
	public abstract void setSensorNumber();
	public abstract void setSensorBus();

	public void setSimulationMode(boolean simulationMode) {
		this.simulationMode = simulationMode;

	};
	
}

