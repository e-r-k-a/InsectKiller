package model;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.LocalTime;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.io.w1.W1Device;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class LPS25HB extends MeasurePoint {

	I2CBus bus;
	I2CDevice device;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//przerwanie timera (trzeba odczytac pomiar i zapisać wynik)
		byte[] data = new byte[3];
		try {
			device.read(0x28 | 0x80, data, 0, 3);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		double pressure = (((data[2] & 0xff) * 65536) + ((data[1] & 0xff) * 256) + (data[0] & 0xff)) / 4096.0;
		this.setValue(pressure);
		this.setHardwareError(false);	
		LocalTime lt=LocalTime.now();
		this.setMeasureTime(lt);
	//	System.out.println("czas pomiaru: " + this.getMeasureTime() + " name=" + this.getName() + " value=" + this.getValue());
	}
	
	// K o n s t r u k t o r y
		public LPS25HB(String name, int sensorAddr, boolean simulationMode) {
			super();
			this.setName(name);
			this.setMeasureUnit("hPa");
			this.setValue(0.0);
			this.setHardwareError(true);
			this.setSensorNumber(Integer.toString(sensorAddr));
			this.setSensorBus("I2C");
			this.setSimulationMode(simulationMode);
			this.getTimer().start();//wystartowanie timera pomiaru
			//this.measurePoint = measurePoint;
			//this.w1Device = w1Device;
		}
		
	
	
}
