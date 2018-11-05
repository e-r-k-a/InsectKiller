package model;
import com.pi4j.wiringpi.SoftPwm;

public class PWMOutput {
	private int pin=1;
	private int msPeriod=1000;
	
	public void setPWM(double output) {
		SoftPwm.softPwmWrite(pin, (int)((output)*(msPeriod/10))); //sterowanie PWM z dok�adno�ci� do 1%
	}
	
	//rozjasnij wyjscie
	public void rozjasnij() throws InterruptedException{
		//  for (int i = 0; i <= 100; i+=1) {
              SoftPwm.softPwmWrite(pin, 5000);//wype�nienie 100% to tyle ile jest okres w softpwmcreate
            //  System.out.println(i/100000+ " ");
              Thread.sleep(2000000);
//}
	}
	
	//konstruktor
	/* pin 1 ---> GPIO 12;   pin 2 ---> GPIO 13
	 * pin 3 ---> GPIO 15;   pin 4 ---> GPIO 16
	 */
	public PWMOutput(int pin, int msPeriod) {
		
		super();
		this.pin = pin;
		this.msPeriod = msPeriod;
		com.pi4j.wiringpi.Gpio.wiringPiSetup(); //inicjalizacja wiringPi
		SoftPwm.softPwmCreate(pin, 0, msPeriod*10);//okres  - 1 sekunda to 10000
	}
}
