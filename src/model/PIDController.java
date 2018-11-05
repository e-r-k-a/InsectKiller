package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class PIDController implements ActionListener{
	//parametry
	 private double kp = 0.1;         // proportional value determines the reaction to the current error
	 private double ti = 500;      // [ms]     integral value determines the reaction based on the sum of recent errors
	 private double td = 0.0;        	// derivative value determines the reaction based on the rate at which the error has been changing
	 private double highLimit = 100;   // high limit output signal
	 private double lowLimit =  0; 	// low limit output signal
	 private int cycleTime = 100;               // czas cyklu PID domy�lnie 100ms
	 //wejścia
	 private int mode = PID_MAN;
	 private double tracking;
	 private boolean stop, reset;
	 
	 //sta�e
	 public static final int PID_MAN = 0;
	 public static final int PID_AUTO = 1;
	   
	 //pola lokalne
	 private double uchyb,uchybOld = 0;                      // proportional term
	 private double delE;               // derivitive term
	 private double yh = 0;					//controller output signal
	 private Timer cycleTimer =  new Timer(cycleTime, this);
	 private double Yp,Yi,Yd;
	 private double delta,calka, rozniczka, rozniczkaOld;
	 private double rTM_Lag=0.0, CyklDoLag;
	 
	 /*konstruktor */
	 public PIDController(boolean auto, int msCycle) {
		 cycleTime = msCycle;
	 	 cycleTimer.setDelay(msCycle);
		 cycleTimer.start();
		 this.mode = PID_AUTO;
	     this.uchyb = uchyb;
	 }
	 /*ustawienie parametrów*/
	 public void setParameters(double kp, double ti, double td, double lowLimit, double highLimit){
		 this.kp = kp;
		 this.ti = ti;
		 this.td = td;
		 this.highLimit = highLimit;
		 this.lowLimit = lowLimit;
	 }

	@Override
	public void actionPerformed(ActionEvent e) {
		if(mode == PID_AUTO) {
			delE = ( uchyb -  uchybOld);
			//wzmocnienie
			Yp =  uchyb *  kp;
			//ca�kowanie
			if(( ti != 0.0) && !stop){
				delta =  kp * ( uchyb +  uchybOld) / 2 *  cycleTime /  ti;
            	Yi =  calka +  delta;
			}
			//r�niczkowanie
			if( rTM_Lag != 0.0) {
				CyklDoLag =  cycleTime /  rTM_Lag;
				if( CyklDoLag > 1) {//ograniczenie bo przy przekroczeniu niestabilne
                	CyklDoLag = 1;
				}
				rozniczka =  kp * ( td /  rTM_Lag) *  delE +  rozniczkaOld * (1.0 -  CyklDoLag);
			}
			calka =  Yi;
			rozniczkaOld =  rozniczka;
			uchybOld =  uchyb;
			yh =  Yp +  Yi +  Yd;
			if( yh >  highLimit) {
				calka =  calka -  delta;
				yh =  highLimit;
			}
			if( yh <  lowLimit) {
				calka =  calka -  delta;// minus bo delta ujemna wi�c trzeba doda�
				yh =  lowLimit;
			}
		}
		else { //tracking 
			calka =  tracking;
			yh =  tracking;
		}
   
		if( reset) {
			rozniczka = 0;
        	rozniczkaOld = 0;
        	calka = 0.0;
		}
  	}
	
	public double getYh() {
		return yh;
	}
	public double getMode() {
		return mode;
	}
	public void setError(double error) {
		uchyb = error;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public void setTracking(double tracking) {
		this.tracking = tracking;
	}
	

}
