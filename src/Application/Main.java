package Application;

import model.*;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("start\n");
		DS18B20 temp1 = new DS18B20("temp1");
		temp1.setSimulationMode(true);
		DS18B20 temp2 = new DS18B20("temp2");
		
		
		try
		{
		    Thread.sleep(1100);
		}
		catch(InterruptedException ex)
		{
		    Thread.currentThread().interrupt();
		}
		
	
		temp1.toString();
		
		
		
	}

}
