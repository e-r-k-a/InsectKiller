package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import Application.Main;

public class Alarm {
	
	 public static AlarmListener aL = new MyAlarmListener();

		
	private static ArrayList<AlarmPoint> lista = new ArrayList<AlarmPoint>();
   
	public void alarmSend(int id, boolean state) {
		AlarmPoint alarmPoint = findAlarm(id);
		if(alarmPoint != null) {//jest już taki alarm
			//sprawdzenie czy zmienil sie stan
			
		}
		else {//nie ma jeszcze takiego wiec dodajemy
			
		}
		
	}
	
	public void addAlarm(AlarmPoint alarmPoint) {
		lista.add(alarmPoint);
	}
	
	
	private AlarmPoint findAlarm(int id) {
		for(int i=0; i<lista.size(); i++) {
			if(lista.get(i).getId() == id) {
				return lista.get(i);
			}
		}
		return null;
		
	}
	@Override
	public String toString() {
		String tmp = "";
		for(int i=0; i<lista.size(); i++) {
			tmp += lista.get(i).toString();
			tmp +="\n";
		}
		return tmp;
	}
	
	//inner Class
			private static class MyAlarmListener implements AlarmListener{

				@Override
				public void alarmExceeded(AlarmEvent e) {
					AlarmPoint al = new AlarmPoint(LocalDate.now(), LocalTime.now(), e.id, e.desc, e.type);
					lista.add(al);
					//System.out.println("!!!ALARM!!! " + lista.toString());//Alarmy na konsole
					//Wysłanie alarmu do GUI (zakładka zdarzenia)
						Main.alarmListener.alarmExceeded(e);//wyslanie alarmu do GUI do zakładki Zdarzenia
					
									
				}
				
			}
	
}
