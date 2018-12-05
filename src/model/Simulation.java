package model;

import java.time.Duration;
import java.time.Instant;

public class Simulation {

	private double input;
	private double output;
	private double Ti = 0.05;
	private double limitHi = 100.0;
	private double limitLow = 0.0;
	private Instant lastTime = Instant.now();
	double cycleTimeS = 0.1;
	double error = 0.0;
	private double calka = 0.0;

	public double getInput() {
		return input;
	}

	public void setInput(double input) {
		this.input = input;
	}

	public double getOutput() {
		return output;
	}

	public void setOutput(double output) {
		this.output = output;
	}

	public double getTi() {
		return Ti;
	}

	public void setTi(double ti) {
		Ti = ti;
	}

	@Override
	public String toString() {
		return "Symulation [input=" + input + ", output=" + output + ", Ti=" + Ti + "całka=" + calka + "]";
	}

	public void run() {

		Instant i = Instant.now();
		Duration cycleTime = Duration.between(i, lastTime);
		lastTime = Instant.now();
		cycleTimeS = cycleTime.getNano() / 1000000000.0;
		// System.out.println("cycletimeS= " + cycleTimeS);

		error = input - output;
		double delta = error * Ti * cycleTimeS;

		calka += delta;
		output = calka;
		if (output > limitHi) {
			calka = calka - delta;
			output = limitHi;
		}
		if (output < limitLow) {
			calka = calka - delta;// minus bo delta ujemna wi�c trzeba doda�
			output = limitLow;
		}
	//	System.out.println(toString());

	}
}
