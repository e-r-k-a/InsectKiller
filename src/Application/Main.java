package Application;

import model.*;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("start\n");
		DS18B20 temp1 = new DS18B20("temp1");
		temp1.setSimulationMode(true);
		DS18B20 temp2 = new DS18B20("temp2");
		Measures measures = new Measures();
		measures.add(temp1);
		measures.add(temp2);
		while(true) {
			try {
				Thread.sleep(5000);
				measures.changeReadCycle(100);
				Thread.sleep(5000);
				measures.changeReadCycle(1000);
				
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
		}

	}
}
