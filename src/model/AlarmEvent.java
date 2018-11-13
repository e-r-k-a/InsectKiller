package model;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;

public class AlarmEvent {//extends ActionEvent {

	public final static int TYPE_EVENT = 0;
	public final static int TYPE_WARNING = 1;
	public final static int TYPE_ALARM = 2;
	
	
	
	String desc;
	int id;
	int type;
	LocalDate date;
	LocalTime time;
	
	 public AlarmEvent(int id, String desc, int type, LocalDate date, LocalTime time) {
	        this.id = id;
	        this.desc = desc;
	        this.type = type;
	        this.date = date;
	        this.time = time;
	    }

	    public int getid() {
	        return id;
	    }

	    public String getDesc() {
	        return desc;
	    }
	    
	    public int getType() {
	    	return type;
	    }

		public LocalDate getDate() {
			return date;
		}

		public LocalTime getTime() {
			return time;
		}
}
