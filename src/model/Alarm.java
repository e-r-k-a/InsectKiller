package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import Application.Main;

/*
 * Dodawanie alarmu polega na dodaniu w klasie w której występuje alarm
 * Alarm.aL.alarmExceeded(new AlarmEvent(id_alarmu, opis alarmu, typ alarmu, data wystąpienia, czas wystąpienia);// wpisanie do systemu alarmów
 * Alarm.aL.alarmExceeded(new AlarmEvent(1, "rozpoczecie grzania", AlarmEvent.TYPE_EVENT, LocalDate.now(), LocalTime.now()));// wpisanie do systemu alarmów
 *
 *  id alarmów:
 *	1 - "rozpoczecie grzania", Main
 *  2 - "nie osiagnieta temperatura", Main
 *  3 - "proces grzania wstrzymany", Main
 *  5, "temperatura maksymalna większa od dopuszczalnej", Main
 *  6, "osiągnięta temperatura ", Main 
 *  7, "koniec grzania - wysoka temperatura utrzymana przez okres ",Main
 */
public class Alarm {
	public static AlarmListener aL = new MyAlarmListener();//likstener do wyłapywania alarmów od innych
	private static ArrayList<AlarmPoint> lista = new ArrayList<AlarmPoint>();//lista do gromadzenia alarmów od wszystkich klas

	public void addAlarm(AlarmPoint alarmPoint) {
		lista.add(alarmPoint);
	}


	@Override
	public String toString() {
		String tmp = "";
		for (int i = 0; i < lista.size(); i++) {
			tmp += lista.get(i).toString();
			tmp += "\n";
		}
		return tmp;
	}

	// inner Class
	private static class MyAlarmListener implements AlarmListener {

		@Override
		public void alarmExceeded(AlarmEvent e) {
			AlarmPoint al = new AlarmPoint(LocalDate.now(), LocalTime.now(), e.id, e.desc, e.type);
			lista.add(al);
			// System.out.println("!!!ALARM!!! " + lista.toString());//Alarmy na konsole
			// Wysłanie alarmu do GUI (zakładka zdarzenia)
			Main.alarmListener.alarmExceeded(e);// wyslanie alarmu do GUI do zakładki Zdarzenia

		}

	}

}
