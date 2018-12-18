package Application;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;

import model.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CDevice;

import gui.MainView;
import gui.WebServer;

public class Main {

	// ============== P A R A M E T R Y =============
	public final static boolean LAPTOP = true;
	public static boolean simulation = true;// flaga włączenia modelu
	public double zadTemp = 40.0;// wartość zadana regulatora
	public double maxTemp = 60.0;// wartość maksymalna (do ogranicznika)
	public double kp = 20;
	public double Tc = 300;// [ms]
	public double Td = 9000;

	public double limKp = 10.0;
	public double limTc = 50;// [ms]
	public Duration heatingTime = Duration.ofSeconds(20);// Hours(10);

	private String pr = "00000000976b5823";

	public W1Measures w1measures = new W1Measures();
	public I2CMeasures i2cMeasures;// = new I2CMeasures(I2CBus.BUS_1);
	static public double actualMinimal = 0.0;
	static public double actualMaximal = 0.0;
	static public double controllerError = 0.0;
	static public double limiterError = 0.0;
	static public double output = 0;
	static public double tempSym = 0;

	static MainView mv;

	static String durationTime = "pozostały czas do zakończenia: ";

	public final static int MODE_STOP = 0;
	public final static int MODE_HEATING = 1;
	public final static int MODE_PAUSE = 2;

	public static int mode = MODE_STOP; // tryb pracy
	public Instant startTime; // zapamietanie czasu startu grzania
	public boolean isHot = false;// 1-trwa okres wysokiej temperatury
	private Simulation sim = new Simulation();
	public static PIDController pidController = new PIDController(true, 500);
	public static PIDController pidLimiter = new PIDController(true, 500);


	public static Alarm alarm = new Alarm();
	public static AlarmListener alarmListener = new AlarmListener() {
		@Override
		public void alarmExceeded(AlarmEvent e) {
			// Przyszedł alarm z systemu alarmów
			// więc wpisywany do zakładki Alarmy i jeśli event to dodatkowo do raporty
			StyleContext context = new StyleContext();
			Style styleEvent = context.addStyle("event", null);
			Style styleWarning = context.addStyle("warning", null);
			Style styleAlarm = context.addStyle("alarm", null);
			Style styl;
			StyleConstants.setForeground(styleEvent, Color.BLACK);
			StyleConstants.setForeground(styleWarning, Color.BLUE);
			StyleConstants.setForeground(styleAlarm, Color.RED);

			switch (e.getType()) {
			case AlarmEvent.TYPE_EVENT:
				styl = styleEvent;
				break;
			case AlarmEvent.TYPE_WARNING:
				styl = styleWarning;
				break;
			case AlarmEvent.TYPE_ALARM:
				styl = styleAlarm;
				break;
			default:
				styl = styleAlarm;
				break;
			}
			// zapis do okna Alarmy i Raport (jeśli alarm jest eventem)
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
			String str = e.getDate().toString() + " " + e.getTime().format(dtf) + ", id=" + e.getid() + ",  "
					+ e.getDesc() + "\n";
			mv.addAlarm(str, styl, e);

		}
	};

	// KONSTRUKTOR
	public Main() {
		mv = new MainView(this);
		mv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mv.setVisible(true);

	}

	public static void main(String[] args) {

		int czasCyklums = 500;// czas odczytu wejść i obiegu regulatora

		// współczynniki regulatora
		double minOutput = 0.0;
		double maxOutput = 100.0;

		System.out.println("start\n");

		PWMOutput pwmOutput;
		final GpioController gpio;
		final GpioPinDigitalOutput stycznik;

		Main w = new Main();

		// wystartowanie serwera webSocket
		WebServer webServer = new WebServer();
		webServer.start();// nasłuchiwanie na porcie 4444

		if (!LAPTOP) {

			// ustawienie GPIO i stycznika podłączonego przez przekaik na module KKAmodRPi
			// PwrRELAY
			gpio = GpioFactory.getInstance();// utworzenie Controllera
			stycznik = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "stycznik", PinState.HIGH);// stycznik
																									// podłączony do
																									// rel1 płytki
			stycznik.setShutdownOptions(true, PinState.LOW);

			String fileName = "/proc/cpuinfo";
			File file = new File(fileName);
			Scanner scan = null;
			try {
				scan = new Scanner(file);
				int lines = 0;
				while (scan.hasNextLine()) {
					String name = scan.nextLine();
					if (name.contains("Serial")) {
						if (name.substring(10, 26).equals(w.pr)) {
							System.out.println("OK");
						} else {
							System.out.println("@");
							System.exit(0);
						}
					}
				}
				scan.close();
			} catch (FileNotFoundException ev) {

				System.err.println("ERROR");
				System.exit(0);
			}
		}


		// softPWM
		if (!LAPTOP) {
			pwmOutput = new PWMOutput(1, 100);// pin 1, period [ms]
			LPS25HB lps = new LPS25HB("cisnienie11", 0x5C, true);// dodanie odczytu ciśnienia atmosferycznego
			w.i2cMeasures.getI2cDeviceList().add((I2CDevice) lps);

			// testowanie zmiany czasu cyklu
			w.w1measures.measureList.get(0).changeReadCycle(czasCyklums);// zmiana cyklu odczytu
			w.w1measures.measureList.get(1).changeReadCycle(czasCyklums);
		} else {
			// symulacja jeśli z laptopa
			DS18B20 p1 = new DS18B20("symulacja1", "DS18B20-001", true, null);
			DS18B20 p2 = new DS18B20("symulacja2", "DS18B20-002", true, null);
			w.w1measures.measureList.add(p1);
			w.w1measures.measureList.add(p2);
			w.w1measures.measureList.get(0).setSimulationMode(true);
			w.w1measures.measureList.get(1).setSimulationMode(true);

		}

		// int licznik = 0;
		Duration heatingPeriod = Duration.ZERO;

		// ========================== L o o p ===============================
		while (true) {
			try {

				Thread.sleep(czasCyklums);
				actualMinimal = w.w1measures.getMinMeasure().getValue();// sterowanie do warto�ci minimalnej
				actualMaximal = w.w1measures.getMaxMeasure().getValue();// ograniczenie do warto�ci maksymalnej

				// obsługa stycznika zasilającego grzałki
				if (!LAPTOP) {
					if ((w.mode == w.MODE_HEATING) || (w.mode == w.MODE_PAUSE)) {
						stycznik.high();// załączenie stycznika
					} else {
						stycznik.low();// wylaczenie jesli nie ma odpowiedniego trybu
					}
				}

				// obsługa licznika czasu grzania
				if ((actualMinimal >= w.maxTemp) && (w.mode != w.MODE_STOP)) {
					if (!w.isHot) {// rozpoczecie liczenia czasu
						w.startTime = Instant.now();
						w.isHot = true;
						Alarm.aL.alarmExceeded(new AlarmEvent(6, "Osiągnięta temperatura ", AlarmEvent.TYPE_EVENT,
								LocalDate.now(), LocalTime.now()));// event startujemy czas
					} else {// ciagle grzejemy - moze już wystarczy
						heatingPeriod = Duration.between(w.startTime, Instant.now()); // czas grzania
						if (heatingPeriod.compareTo(w.heatingTime) >= 0) {
							// koniec
							Alarm.aL.alarmExceeded(new AlarmEvent(7,
									"Koniec grzania - wysoka temperatura utrzymana przez okres "
											+ w.heatingTime.toString(),
									AlarmEvent.TYPE_EVENT, LocalDate.now(), LocalTime.now()));// koniec grzania
							w.mode = w.MODE_STOP;// wylaczenie trybu grzania
						}
					}

				} else {
					w.isHot = false;
					w.startTime = Instant.now();
				}
				durationTime = "pozostały czas do zakończenia: " + w.heatingTime.minus(heatingPeriod).getSeconds()
						+ "sek.";

				// zaktualizowanie parametrów
				pidController.setParameters(w.kp, w.Tc, w.Td, minOutput, maxOutput);
				pidLimiter.setParameters(w.limKp, w.limTc, w.Td, minOutput, maxOutput);
				if (simulation) {
					controllerError = w.zadTemp - tempSym;
					limiterError = w.maxTemp - tempSym;
				} else {
					controllerError = w.zadTemp - actualMinimal;
					limiterError = w.maxTemp - actualMaximal;
				}
				pidController.setError(controllerError);
				pidLimiter.setError(limiterError);

				// wybierak minimum
				if (limiterError < 0) {// trzeba ogranicza�
					pidController.setMode(PIDController.PID_MAN);// regulator na MAN
					pidLimiter.setMode(PIDController.PID_AUTO); // ogranicznik na AUTO
					output = pidLimiter.getYh(); // na wyj�cie yh ogranicznika
					pidController.setTracking(output);// regulator �ledzi wyjscie ogranicznika
				} else { // regulacja
					pidLimiter.setMode(PIDController.PID_MAN);
					pidController.setMode(PIDController.PID_AUTO);
					output = pidController.getYh();
					pidLimiter.setTracking(output);
				}

				if (!LAPTOP) {
					pwmOutput.setPWM(output);
				}
		//		System.out.format(
		//				"zad_reg=%.2f zad_ogr=%.2f temp min= %.2f temp max=%.2f uchyb reg.=%.2f wyjscie= %.2f tryb reg.=%.0f tryb ogr.= %.0f kp=%.2f Tc=%.2f kp_lim=%.2f Tc_lim=%.2f\n",
		//				w.zadTemp, w.maxTemp, actualMinimal, actualMaximal, controllerError, output,
		//				pidController.getMode(), pidLimiter.getMode(), w.kp, w.Tc, w.limKp, w.limTc);
				// uaktualnienie GUI
				mv.update();

				// wypracowanie A L A R M Ó W
				// przekroczenie progu maksymalnego o 5stC
				if (actualMaximal > w.zadTemp + 5) {
					String str = "Temperatura maksymalna (" + String.format("%.2f", actualMaximal)
							+ "st. C) większa od dopuszczalnej";
					Alarm.aL.alarmExceeded(
							//wpisanie do systemu alarmów
							new AlarmEvent(5, str, AlarmEvent.TYPE_ALARM, LocalDate.now(), LocalTime.now()));
				}
				// uaktualnienie modelu który pracuje "równolegle"
				w.sim.setInput(output);
				w.sim.run();
				tempSym = w.sim.getOutput();

				// wysłanie do serwera www
				for (int i = 0; i < webServer.getData().size(); i++) {
					switch (i) {
					case 0:
						webServer.getData().set(i, w.zadTemp); // 0 dana do wysłania
						break;
					case 1:
						webServer.getData().set(i, w.maxTemp); // 1 dana do wysłania
						break;
					case 2:
						webServer.getData().set(i, String.format("%.2f", actualMinimal)); // 2 dana do wysłania
						break;
					case 3:
						webServer.getData().set(i, String.format("%.2f", actualMaximal)); // 3 dana do wysłania
						break;
					case 4:
						webServer.getData().set(i, String.format("%.2f", output)); // 4 dana do wysłania
						break;
					case 5: // tryb pracy
						webServer.getData().set(i, Integer.toString(w.mode)); // 5 dana do wysłania (int)
						break;
						
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
