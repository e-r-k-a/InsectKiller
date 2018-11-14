package Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import model.*;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DateFormatter;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JEditorPane;

import org.jfree.chart.*;

public class Main extends JFrame implements ActionListener, MenuListener {

	// ============== P A R A M E T R Y =============
	public final boolean LAPTOP = true;
	public double zadTemp = 40.0;// wartość zadana regulatora
	public double maxTemp = 60.0;// wartość maksymalna (do ogranicznika)
	public double kp = 3.0;
	public double Tc = 700;// [ms]
	public double limKp = 10.0;
	public double limTc = 50;// [ms]
	public Duration heatingTime = Duration.ofSeconds(20);// Hours(10);

	private final String pr = "00000000976b5823";

	public W1Measures w1measures = new W1Measures();
	JLabel ltTempMin, ltTempMax, ltControllerError, ltLimiterError, ltControllerOutput, ltLimiterOutput;
	JLabel ltZadana, ltMax, lblDuration;
	JTextField tfZadana, tfMaxTemp;
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private JPanel panelSterowanie;
	private JPanel panelRaport;
	private JPanel panelRegulacja;
	private JPanel panelPomiary;
	private JPanel panelZdarzenia;
	private JPanel panelWykres;

	private JTable tabMeasurments;
	private JScrollPane scrollPane;
	private static JTextPane textPaneZdarzenia;
	private static JScrollPane scrollPaneZdarzenia;
	private static JScrollPane scrollPaneRaport;
	private static JTextPane textPaneRaport;
	private JTextField tfControllerKp;
	private JTextField tfControllerTc;
	private JTextField tfLimiterKp;
	private JTextField tfLimiterTc;
	private JButton btnStart, btnPause, btnStop;

	private TempChart lineChart;

	public final int MODE_STOP = 0;
	public final int MODE_HEATING = 1;
	public final int MODE_PAUSE = 2;

	public int mode = MODE_STOP; // tryb pracy
	public Instant startTime; // zapamietanie czasu startu grzania
	public boolean isHot = false;// 1-trwa okres wysokiej temperatury

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
			try {
				Document doc = textPaneZdarzenia.getDocument();
				Document docRaport = textPaneRaport.getDocument();
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
				String str = e.getDate().toString() + " " + e.getTime().format(dtf) + ", id=" + e.getid() + ",  "
						+ e.getDesc() + "\n";
				doc.insertString(doc.getLength(), str, styl);
				if (e.getType() == AlarmEvent.TYPE_EVENT) {
					docRaport.insertString(docRaport.getLength(), str, styl);
				}
				// przewinięcie okna Zdarzeń do końca
				scrollPaneZdarzenia.getVerticalScrollBar()
						.setValue(scrollPaneZdarzenia.getVerticalScrollBar().getMaximum());
				scrollPaneRaport.getVerticalScrollBar().setValue(scrollPaneRaport.getVerticalScrollBar().getMaximum());

			} catch (BadLocationException exc) {
				exc.printStackTrace();
			}

		}
	};
	private JLabel label;

	// KONSTRUKTOR
	public Main() {
		setSize(723, 402);
		setTitle("INSECT KILLER!!!!!!");
		BorderLayout bl = new BorderLayout();
		getContentPane().setLayout(bl);
		tabbedPane.setBounds(10, 11, 687, 342);
		getContentPane().add(tabbedPane);

		panelSterowanie = new JPanel();
		tabbedPane.addTab("Sterowanie", null, panelSterowanie, null);
		panelSterowanie.setLayout(null);

		btnStart = new JButton("Rozpocznij grzanie");
		btnStart.setFont(new Font("Calibri", Font.PLAIN, 18));
		btnStart.setBounds(51, 21, 200, 100);
		panelSterowanie.add(btnStart);
		btnStart.addActionListener(this);

		btnPause = new JButton("Zatrzymaj grzanie");
		btnPause.setFont(new Font("Calibri", Font.PLAIN, 18));
		btnPause.setBounds(51, 173, 200, 100);
		panelSterowanie.add(btnPause);
		btnPause.addActionListener(this);

		btnStop = new JButton("Zakończ grzanie");
		btnStop.setFont(new Font("Calibri", Font.PLAIN, 18));
		btnStop.setBounds(376, 21, 200, 100);
		panelSterowanie.add(btnStop);
		btnStop.addActionListener(this);

		JButton btnEnd = new JButton("Koniec");
		btnEnd.setFont(new Font("Calibri", Font.PLAIN, 18));
		btnEnd.setBounds(376, 173, 200, 100);
		panelSterowanie.add(btnEnd);
		
		lblDuration = new JLabel("New label");
		lblDuration.setBounds(223, 284, 267, 19);
		panelSterowanie.add(lblDuration);

		panelRaport = new JPanel();
		tabbedPane.addTab("Raport", null, panelRaport, null);
		panelRaport.setLayout(new BorderLayout());

		scrollPaneRaport = new JScrollPane();
		scrollPaneRaport.setBounds(0, 0, 682, 314);
		panelRaport.add(scrollPaneRaport);

		textPaneRaport = new JTextPane();
		textPaneRaport.setEditable(false);
		scrollPaneRaport.setViewportView(textPaneRaport);

		panelRegulacja = new JPanel();
		tabbedPane.addTab("Regulacja", null, panelRegulacja, null);
		panelRegulacja.setLayout(null);

		ltZadana = new JLabel("Temp. zadana");
		ltZadana.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltZadana.setBounds(11, 11, 128, 19);
		panelRegulacja.add(ltZadana);

		tfZadana = new JTextField();
		tfZadana.setBounds(137, 7, 50, 29);
		panelRegulacja.add(tfZadana);
		tfZadana.setText(Double.toString(zadTemp));

		ltMax = new JLabel("Temp. maksymalna");
		ltMax.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltMax.setBounds(202, 6, 176, 29);
		panelRegulacja.add(ltMax);

		tfMaxTemp = new JTextField();
		tfMaxTemp.setBounds(388, 7, 70, 29);
		panelRegulacja.add(tfMaxTemp);
		tfMaxTemp.setText(Double.toString(maxTemp));

		tfMaxTemp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					maxTemp = Double.parseDouble(tfMaxTemp.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błąd konwersji na double");
				}
				tfMaxTemp.setText(Double.toString(maxTemp));
			}
		});

		ltTempMin = new JLabel("Temperatura minimalna");
		ltTempMin.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltTempMin.setBounds(10, 62, 190, 29);
		panelRegulacja.add(ltTempMin);

		ltTempMax = new JLabel("Temperatura maksymalna");
		ltTempMax.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltTempMax.setBounds(202, 62, 213, 29);
		panelRegulacja.add(ltTempMax);

		ltControllerError = new JLabel("Uchyb regulatora");
		ltControllerError.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltControllerError.setBounds(10, 123, 157, 29);
		panelRegulacja.add(ltControllerError);

		ltLimiterError = new JLabel("Uchyb ogranicznika");
		ltLimiterError.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltLimiterError.setBounds(202, 123, 190, 29);
		panelRegulacja.add(ltLimiterError);

		ltControllerOutput = new JLabel("Wyjście regulatora");
		ltControllerOutput.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltControllerOutput.setBounds(10, 184, 182, 29);
		panelRegulacja.add(ltControllerOutput);

		ltLimiterOutput = new JLabel("Wyjście ogranicznika");
		ltLimiterOutput.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltLimiterOutput.setBounds(202, 184, 176, 29);
		panelRegulacja.add(ltLimiterOutput);

		JLabel ltControllerKp = new JLabel("Kp regulatora");
		ltControllerKp.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltControllerKp.setBounds(11, 228, 128, 19);
		panelRegulacja.add(ltControllerKp);

		tfControllerKp = new JTextField();
		tfControllerKp.setBounds(137, 224, 37, 29);
		tfControllerKp.setText(Double.toString(kp));

		panelRegulacja.add(tfControllerKp);
		tfControllerKp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					kp = Double.parseDouble(tfControllerKp.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błąd konwersji na double");
				}
				tfControllerKp.setText(Double.toString(kp));
			}
		});

		JLabel ltControllerTc = new JLabel("Tc regulatora");
		ltControllerTc.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltControllerTc.setBounds(11, 254, 128, 19);
		panelRegulacja.add(ltControllerTc);

		tfControllerTc = new JTextField();
		tfControllerTc.setBounds(137, 250, 50, 29);
		tfControllerTc.setText(Double.toString(Tc));
		panelRegulacja.add(tfControllerTc);
		tfControllerTc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Tc = Double.parseDouble(tfControllerTc.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błąd konwersji na double");
				}
				tfControllerTc.setText(Double.toString(Tc));
			}
		});

		JLabel LtLimiterKp = new JLabel("Kp ogranicznika");
		LtLimiterKp.setFont(new Font("Tahoma", Font.PLAIN, 12));
		LtLimiterKp.setBounds(229, 228, 128, 19);
		panelRegulacja.add(LtLimiterKp);

		tfLimiterKp = new JTextField();
		tfLimiterKp.setBounds(355, 224, 50, 29);
		tfLimiterKp.setText(Double.toString(limKp));
		panelRegulacja.add(tfLimiterKp);
		tfLimiterKp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					limKp = Double.parseDouble(tfLimiterKp.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błąd konwersji na double");
				}
				tfLimiterKp.setText(Double.toString(limKp));
			}
		});

		JLabel ltLimiterTc = new JLabel("Tc ogranicznika");
		ltLimiterTc.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltLimiterTc.setBounds(229, 258, 128, 19);
		panelRegulacja.add(ltLimiterTc);

		tfLimiterTc = new JTextField();
		tfLimiterTc.setBounds(355, 254, 50, 29);
		tfLimiterTc.setText(Double.toString(limTc));
		panelRegulacja.add(tfLimiterTc);
		tfLimiterTc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					limTc = Double.parseDouble(tfLimiterTc.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błąd konwersji na double");
				}
				tfLimiterTc.setText(Double.toString(limTc));
			}
		});

		tfZadana.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					zadTemp = Double.parseDouble(tfZadana.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błącd konwersji na double");
				}
				// wpisanie wartosci po konwersji lub poprzedniej
				tfZadana.setText(Double.toString(zadTemp));
			}
		});

		panelPomiary = new JPanel();
		tabbedPane.addTab("Pomiary", null, panelPomiary, null);
		panelPomiary.setLayout(new BorderLayout());
		scrollPane = new JScrollPane();
		panelPomiary.add(scrollPane);

		tabMeasurments = new JTable();
		scrollPane.setViewportView(tabMeasurments);
		tabMeasurments.setModel(new DefaultTableModel(new Object[][] { { null, null, null }, },
				new String[] { "L.p.", "Nazwa czujnika", "Warto\u015B\u0107" }) {
			Class[] columnTypes = new Class[] { Integer.class, Object.class, Object.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tabMeasurments.setFont(new Font("Tahoma", Font.PLAIN, 12));

		// panel zdarzenia
		panelZdarzenia = new JPanel();
		tabbedPane.addTab("Zdarzenia", null, panelZdarzenia, null);

		panelZdarzenia.setLayout(new BorderLayout());

		scrollPaneZdarzenia = new JScrollPane();
		// scrollPane_1.setBounds(33, 23, 501, 249);
		panelZdarzenia.add(scrollPaneZdarzenia);

		textPaneZdarzenia = new JTextPane();
		textPaneZdarzenia.setEditable(false);// okno tylko do wyświetlania zdarzeń
		scrollPaneZdarzenia.setViewportView(textPaneZdarzenia);

		// panel wykres
		panelWykres = new JPanel();
		tabbedPane.addTab("Wykres", null, panelWykres, null);
		panelWykres.setLayout(new BorderLayout());
		lineChart = new TempChart("temperatury", w1measures);
		ChartPanel chartPanel = new ChartPanel(lineChart.chart);
		panelWykres.add(chartPanel, BorderLayout.CENTER);
	}

	@Override
	public void menuCanceled(MenuEvent e) {
	}

	@Override
	public void menuDeselected(MenuEvent e) {
	}

	public JEditorPane getTextAreaZdarzenia() {
		return textPaneZdarzenia;
	}

	@Override
	public void menuSelected(MenuEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

	//	LocalTime l = LocalTime.now();
		String str = "";//l.getHour() + ":" + l.getMinute() + ":" + l.getSecond();

		if (source == btnStart) {
			if ((mode == MODE_STOP) || (mode == MODE_PAUSE)) {
				mode = MODE_HEATING; // zmiana trybu
				str += "Rozpoczęcie grzania";// wpisanie do zdarzeń
				Alarm.aL.alarmExceeded(new AlarmEvent(1, str, AlarmEvent.TYPE_EVENT, LocalDate.now(), LocalTime.now()));// wpisanie
																														// do
																														// systemu
																														// alarmów
			}
		}
		if (source == btnStop) {
			if ((mode == MODE_HEATING) || (mode == MODE_PAUSE)) {
				mode = MODE_STOP; // zmiana trybu
				str += "Wyłączone grzanie - nie osiągnięta temperatura";
				Alarm.aL.alarmExceeded(new AlarmEvent(2, str, AlarmEvent.TYPE_EVENT, LocalDate.now(), LocalTime.now()));// wpisanie
				// do
				// systemu
				// alarmów
			}
		}
		if (source == btnPause) {
			if (mode == MODE_HEATING) {
				mode = MODE_PAUSE; // zmiana trybu
				str += "Proces grzania wstrzymany";
				Alarm.aL.alarmExceeded(new AlarmEvent(3, str, AlarmEvent.TYPE_EVENT, LocalDate.now(), LocalTime.now()));// wpisanie
																														// do
																														// systemu
																														// alarmów
			}
		}
		checkButtonColor();
		// przewinięcie okna raportów do końca
		scrollPaneRaport.getVerticalScrollBar().setValue(scrollPaneRaport.getVerticalScrollBar().getMaximum());
	}

	private void checkButtonColor() {
		// ustalenie koloru Buttonów w zależności od mode
		switch (mode) {
		case MODE_STOP:
			btnStart.setBackground(Color.LIGHT_GRAY);
			btnStop.setBackground(Color.RED);
			btnPause.setBackground(Color.LIGHT_GRAY);
			break;
		case MODE_HEATING:
			btnStart.setBackground(Color.GREEN);
			btnStop.setBackground(Color.LIGHT_GRAY);
			btnPause.setBackground(Color.LIGHT_GRAY);
			break;
		case MODE_PAUSE:
			btnStart.setBackground(Color.LIGHT_GRAY);
			btnStop.setBackground(Color.LIGHT_GRAY);
			btnPause.setBackground(Color.YELLOW);
			break;
		}
	}

	public static void main(String[] args) {

		int czasCyklums = 500;// czas odczytu wejść i obiegu regulatora

		// współczynniki regulatora
		double Td = 0.0;
		double minOutput = 0.0;
		double maxOutput = 100.0;

		double output = 0;
		System.out.println("start\n");

		// regulator PID
		double actualMinimal = 0.0;
		double actualMaximal = 0.0;
		double controllerError = 0.0;
		double limiterError = 0.0;

		PWMOutput pwmOutput;

		Main w = new Main();
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		w.setVisible(true);

		if (!w.LAPTOP) {

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

		PIDController pidController = new PIDController(true, 500);
		PIDController pidLimiter = new PIDController(true, 500);

		// softPWM
		if (!w.LAPTOP) {
			pwmOutput = new PWMOutput(1, 100);// pin 1, period [ms]

			// zbierz pomiary
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

		int licznik = 0;
		Duration heatingPeriod = Duration.ZERO;

		while (true) {
			try {
				// if(licznik%3==0) {
				// actual +=3;
				// }

				Thread.sleep(czasCyklums);
				actualMinimal = w.w1measures.getMinMeasure().getValue();// sterowanie do warto�ci minimalnej
				actualMaximal = w.w1measures.getMaxMeasure().getValue();// ograniczenie do warto�ci maksymalnej

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
				w.lblDuration.setText("pozostały czas do zakończenia: " + w.heatingTime.minus(heatingPeriod).getSeconds() + "sek.");

				// zaktualizowanie kolorów buttonów w zależności od trybu
				w.checkButtonColor();// odpowiednie pokolorowanie buttonów na GUI

				// zaktualizowanie parametrów
				pidController.setParameters(w.kp, w.Tc, Td, minOutput, maxOutput);
				pidLimiter.setParameters(w.limKp, w.limTc, Td, minOutput, maxOutput);

				controllerError = w.zadTemp - actualMinimal;
				pidController.setError(controllerError);

				limiterError = w.maxTemp - actualMaximal;
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

				if (!w.LAPTOP) {
					pwmOutput.setPWM(output);
				}
				// wypisanie do panelu zdarzeń
				// LocalTime now = LocalTime.now();
				// w.textArea.append(LocalDate.now().toString() + " " + now.getHour() + ":" +
				// now.getMinute() + ":"
				// + now.getSecond() + "\n");
				// i na konsole
				System.out.format(
						"zad_reg=%.2f zad_ogr=%.2f temp min= %.2f temp max=%.2f uchyb reg.=%.2f wyjscie= %.2f tryb reg.=%.0f tryb ogr.= %.0f kp=%.2f Tc=%.2f kp_lim=%.2f Tc_lim=%.2f\n",
						w.zadTemp, w.maxTemp, actualMinimal, actualMaximal, controllerError, output,
						pidController.getMode(), pidLimiter.getMode(), w.kp, w.Tc, w.limKp, w.limTc);
				// wpisanie wartości*/
				w.ltTempMin.setText("temp minimalna = " + actualMinimal);
				w.ltTempMax.setText("temp maksymalna = " + actualMaximal);
				w.ltControllerError.setText("uchyb reg = " + controllerError);
				w.ltLimiterError.setText("uchyb ogr = " + limiterError);
				w.ltControllerOutput.setText("wyjście reg. = " + pidController.getYh());
				w.ltLimiterOutput.setText("wyjście ogr. = " + pidLimiter.getYh());
				// kolorowanie
				w.ltControllerOutput.setOpaque(true);
				w.ltLimiterOutput.setOpaque(true);
				if (pidController.getMode() == PIDController.PID_MAN) {
					// steruje limiter wiec
					w.ltControllerOutput.setBackground(Color.RED);
					w.ltLimiterOutput.setBackground(Color.GREEN);
				} else {
					w.ltControllerOutput.setBackground(Color.GREEN);
					w.ltLimiterOutput.setBackground(Color.RED);
				}

				// wypracowanie A L A R M Ó W
				// przekroczenie progu maksymalnego o 5stC
				if (actualMaximal > w.maxTemp + 5) {
					String str = "Temperatura maksymalna (" + String.format("%.2f", actualMaximal)
							+ "st. C) większa od dopuszczalnej";
					Alarm.aL.alarmExceeded(
							new AlarmEvent(5, str, AlarmEvent.TYPE_ALARM, LocalDate.now(), LocalTime.now()));// wpisanie
																												// do
																												// systemu
																												// alarmów

				}

				// uaktualnienie wykresy na zakładce wykres
				w.lineChart.update();
				licznik++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
