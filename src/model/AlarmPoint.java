package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AlarmPoint {

	
	final public int EVENT = 0;
	final public int WARN =  1;
	final public int ERROR = 2;
	
	private LocalDate date;
	private LocalTime time;
	private int id = 0;
	private String description="";
	private int type;
	private boolean state;

	//Constructor
	public AlarmPoint(LocalDate date, LocalTime time, int id, String description, int type) {
		super();
		this.date = date;
		this.time = time;
		this.description = description;
		this.type = type;
	}
	

	public int getId() {
		return id;
	}


	@Override
	public String toString() {
		return "AlarmPoint [date=" + date + ", time=" + time + " id=" + id +", description=" + description + ", type=" + type + "]\n";
	}
	
	
	
	
	
}
