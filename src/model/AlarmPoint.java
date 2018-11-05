package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AlarmPoint {

	
	final public int EVENT = 0;
	final public int WARN =  1;
	final public int ERROR = 2;
	
	private LocalDate date;
	private LocalTime time;
	private String description="";
	private int type;
	//Constructor
	public AlarmPoint(LocalDate date, LocalTime time, String description, int type) {
		super();
		this.date = date;
		this.time = time;
		this.description = description;
		this.type = type;
	}
	
	
	
}
