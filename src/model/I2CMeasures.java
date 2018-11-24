package model;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import java.io.IOException;


public class I2CMeasures {

	private List<I2CDevice> i2cDeviceList = new ArrayList<I2CDevice>();
	private I2CBus bus;			// Create I2C bus
	private I2CDevice device;	// Get I2C device, LPS25HB I2C address is 0x5C(72)
	
	
	//konstruktor
		public I2CMeasures(int busId) {
			super();
			try {
				bus = I2CFactory.getInstance(busId);
				device= bus.getDevice(0x5C);//wyszukanie LPS25HB
			} catch (UnsupportedBusNumberException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(device != null) {
				LPS25HB lps25hb = new LPS25HB("ciśnienie", 0x5C, false);
				i2cDeviceList.add((I2CDevice)lps25hb);
			}
		}


		public List<I2CDevice> getI2cDeviceList() {
			return i2cDeviceList;
		}
	
}
