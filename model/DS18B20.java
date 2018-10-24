package model;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Timer;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;

public class DS18B20 extends MeasurePoint {
	
//	private W1Measures w1Measures; //kopia referencji zeby wiedziec gdzie pisac wyniki
	private W1Device w1Device; //kopia referencji w obiekcie W1Measures
//	private MeasurePoint measurePoint;//kopia ref gdzie zapisywać

	

/*	@Override
	public String toString() {
		return "DS18B20 [name=" + this.getName() + ", MeasureUnit=" + getMeasureUnit() + ", value=" + getValue() + ", hardwareError="
				+ getHardwareError() +  ", sensorNumber="
				+ getSensorNumber() + ", sensorBus=" + getSensorBus() "+ "] ";
	}
	*/
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//przerwanie timera (trzeba odczytac pomiar i zapisać wynik)
		this.setValue(((TemperatureSensor)w1Device).getTemperature());//odczyt wartości i zapisanie do pomiaru
		this.setHardwareError(false);	
		System.out.println("name=" + this.getName() + " value=" + this.getValue());
	}
	

	

	// K o n s t r u k t o r y
	public DS18B20(String name, String sensorNumber, boolean simulationMode, W1Device w1Device) {
		super();
		this.setName(name);
		this.setMeasureUnit("st C");
		this.setValue(0.0);
		this.setHardwareError(true);
		this.setSensorNumber(sensorNumber);
		this.setSensorBus("1-Wire");
		this.setSimulationMode(simulationMode);
		this.getTimer().start();//wystartowanie timera pomiaru
		//this.measurePoint = measurePoint;
		this.w1Device = w1Device;
	}
	
	

	
	
	
	
	
	
	
}
