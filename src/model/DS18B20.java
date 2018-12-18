package model;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Timer;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;

public class DS18B20 extends MeasurePoint {

	// private W1Measures w1Measures; //kopia referencji zeby wiedziec gdzie pisac
	// wyniki
	private W1Device w1Device; // kopia referencji w obiekcie W1Measures
	private boolean readError; // true jeśli trea błąd odczytu
	// private MeasurePoint measurePoint;//kopia ref gdzie zapisywać

	/*
	 * @Override public String toString() { return "DS18B20 [name=" + this.getName()
	 * + ", MeasureUnit=" + getMeasureUnit() + ", value=" + getValue() +
	 * ", hardwareError=" + getHardwareError() + ", sensorNumber=" +
	 * getSensorNumber() + ", sensorBus=" + getSensorBus() "+ "] "; }
	 */

	@Override
	public void actionPerformed(ActionEvent e) {
		// przerwanie timera (trzeba odczytac pomiar i zapisać wynik)
		this.setHardwareError(false);
		try {
			this.setValue(((TemperatureSensor) w1Device).getTemperature());// odczyt wartości i zapisanie do pomiaru
			this.readError = false;
		} catch (NullPointerException ev) {
			if (!readError) {
				// System.out.println("Błąd odczytu temperatury: " + readError);
				Alarm.aL.alarmExceeded(new AlarmEvent(8, "Błąd odczytu temperatury - " + this.getName(),
						AlarmEvent.TYPE_ALARM, LocalDate.now(), LocalTime.now()));// wpisanie do systemu alarmów
				this.setHardwareError(true);
				this.readError = true;
			}

		}

		LocalTime lt = LocalTime.now();
		this.setMeasureTime(lt);
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
		this.getTimer().start();// wystartowanie timera pomiaru
		// this.measurePoint = measurePoint;
		this.w1Device = w1Device;
		this.readError = false;
	}

}
