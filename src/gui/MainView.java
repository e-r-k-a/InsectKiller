package gui;

import Application.Main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;

import org.jfree.chart.ChartPanel;

import model.Alarm;
import model.AlarmEvent;
import model.TempChart;

public class MainView extends JFrame {

	private Main w; // dostęp do wszystkich danych

	JLabel ltTempMin, ltTempMax, ltControllerError, ltLimiterError, ltControllerOutput, ltLimiterOutput, ltLimiterTc;
	JLabel ltZadana, ltMax, lblDuration, lblPresure, ltControllerKp, ltControllerTc, ltLimiterKp, lblControllerTd;

	JTabbedPane tabbedPane;

	JPanel panelSterowanie, panelRaport, panelRegulacja, panelPomiary, panelZdarzenia, panelWykres;

	JTable tabMeasurments;

	JScrollPane scrollPane, scrollPaneZdarzenia, scrollPaneRaport;

	JTextPane textPaneZdarzenia, textPaneRaport;

	JTextField tfZadana, tfMaxTemp, tfControllerKp, tfControllerTc, tfControllerTd, tfLimiterKp, tfLimiterTc;

	JButton btnStart, btnPause, btnStop;

	TempChart lineChart;

	public MainView(Main w) {
		this.w = w;

		setSize(723, 402);
		setTitle("INSECT KILLER!!!!!!");
		BorderLayout bl = new BorderLayout();
		getContentPane().setLayout(bl);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 687, 342);
		getContentPane().add(tabbedPane);

		// ==== B U T T O N Y ===========
		btnStart = new JButton("Rozpocznij grzanie");
		btnStart.setFont(new Font("Calibri", Font.PLAIN, 18));
		btnStart.setBounds(51, 21, 200, 100);
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((Main.mode == Main.MODE_STOP) || (Main.mode == Main.MODE_PAUSE)) {
					Main.mode = Main.MODE_HEATING; // zmiana trybu
					String str = "Rozpoczęcie grzania";// wpisanie do zdarzeń
					// wpisanie do systemu alarmów
					Alarm.aL.alarmExceeded(
							new AlarmEvent(1, str, AlarmEvent.TYPE_EVENT, LocalDate.now(), LocalTime.now()));
				}
				// przewinięcie
				scrollPaneRaport.getVerticalScrollBar().setValue(scrollPaneRaport.getVerticalScrollBar().getMaximum());
			}
		});

		btnPause = new JButton("Zatrzymaj grzanie");
		btnPause.setFont(new Font("Calibri", Font.PLAIN, 18));
		btnPause.setBounds(51, 173, 200, 100);
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Main.mode == Main.MODE_HEATING) {
					Main.mode = Main.MODE_PAUSE; // zmiana trybu
					String str = "Proces grzania wstrzymany";
					// wpisanie do systemu alarmów
					Alarm.aL.alarmExceeded(
							new AlarmEvent(3, str, AlarmEvent.TYPE_EVENT, LocalDate.now(), LocalTime.now()));// wpisanie
				}
				// przewinięcie
				scrollPaneRaport.getVerticalScrollBar().setValue(scrollPaneRaport.getVerticalScrollBar().getMaximum());

			}
		});

		btnStop = new JButton("Zakończ grzanie");
		btnStop.setFont(new Font("Calibri", Font.PLAIN, 18));
		btnStop.setBounds(376, 21, 200, 100);
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((Main.mode == Main.MODE_HEATING) || (Main.mode == Main.MODE_PAUSE)) {
					Main.mode = Main.MODE_STOP; // zmiana trybu
					String str = "Wyłączone grzanie - nie osiągnięta temperatura";
					// wpisanie do systemu alarmów
					Alarm.aL.alarmExceeded(
							new AlarmEvent(2, str, AlarmEvent.TYPE_EVENT, LocalDate.now(), LocalTime.now()));
				}
				// przewinięcie
				scrollPaneRaport.getVerticalScrollBar().setValue(scrollPaneRaport.getVerticalScrollBar().getMaximum());
			}
		});

		JButton btnEnd = new JButton("Koniec");
		btnEnd.setFont(new Font("Calibri", Font.PLAIN, 18));
		btnEnd.setBounds(376, 173, 200, 100);

		// ======= ! B U T T O N Y ================
		// ======== L A B E L E ==================
		lblDuration = new JLabel("Czas do zakończenia grzania");
		lblDuration.setBounds(223, 284, 267, 19);

		lblPresure = new JLabel("Ciśnienie atmosferyczne");
		lblPresure.setBounds(223, 306, 267, 19);

		ltZadana = new JLabel("Temp. zadana");
		ltZadana.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltZadana.setBounds(11, 11, 128, 19);

		ltMax = new JLabel("Temp. maksymalna");
		ltMax.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltMax.setBounds(202, 6, 176, 29);

		ltTempMin = new JLabel("Temperatura minimalna");
		ltTempMin.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltTempMin.setBounds(10, 62, 190, 29);

		ltTempMax = new JLabel("Temperatura maksymalna");
		ltTempMax.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltTempMax.setBounds(202, 62, 213, 29);

		ltControllerError = new JLabel("Uchyb regulatora");
		ltControllerError.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltControllerError.setBounds(10, 123, 157, 29);

		ltLimiterError = new JLabel("Uchyb ogranicznika");
		ltLimiterError.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltLimiterError.setBounds(202, 123, 190, 29);

		ltControllerOutput = new JLabel("Wyjście regulatora");
		ltControllerOutput.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltControllerOutput.setBounds(10, 184, 182, 29);

		ltLimiterOutput = new JLabel("Wyjście ogranicznika");
		ltLimiterOutput.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltLimiterOutput.setBounds(202, 184, 176, 29);

		ltControllerKp = new JLabel("Kp regulatora");
		ltControllerKp.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltControllerKp.setBounds(11, 228, 128, 19);

		ltControllerTc = new JLabel("Tc regulatora");
		ltControllerTc.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltControllerTc.setBounds(11, 264, 128, 19);

		lblControllerTd = new JLabel("Td regulatora");
		lblControllerTd.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblControllerTd.setBounds(11, 300, 128, 19);

		
		ltLimiterKp = new JLabel("Kp ogranicznika");
		ltLimiterKp.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltLimiterKp.setBounds(229, 228, 128, 19);

		ltLimiterTc = new JLabel("Tc ogranicznika");
		ltLimiterTc.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ltLimiterTc.setBounds(229, 258, 128, 19);
		// ======== !!! L A B E L E ==================
		
		// ========Text Fieldy =======================
		tfZadana = new JTextField();
		tfZadana.setBounds(137, 7, 50, 29);
		tfZadana.setText(Double.toString(w.zadTemp));
		tfZadana.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					w.zadTemp = Double.parseDouble(tfZadana.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błącd konwersji na double");
				}
				// wpisanie wartosci po konwersji lub poprzedniej
				tfZadana.setText(Double.toString(w.zadTemp));
			}
		});

		tfMaxTemp = new JTextField();
		tfMaxTemp.setBounds(388, 7, 70, 29);
		tfMaxTemp.setText(Double.toString(w.maxTemp));
		tfMaxTemp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					w.maxTemp = Double.parseDouble(tfMaxTemp.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błąd konwersji na double");
				}
				tfMaxTemp.setText(Double.toString(w.maxTemp));
			}
		});

		tfControllerKp = new JTextField();
		tfControllerKp.setBounds(137, 224, 37, 29);
		tfControllerKp.setText(Double.toString(w.kp));
		tfControllerKp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					w.kp = Double.parseDouble(tfControllerKp.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błąd konwersji na double");
				}
				tfControllerKp.setText(Double.toString(w.kp));
			}
		});

		tfControllerTc = new JTextField();
		tfControllerTc.setBounds(137, 258, 50, 29);
		tfControllerTc.setText(Double.toString(w.Tc));
		tfControllerTc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					w.Tc = Double.parseDouble(tfControllerTc.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błąd konwersji na double");
				}
				tfControllerTc.setText(Double.toString(w.Tc));
			}
		});

		tfControllerTd = new JTextField();
		tfControllerTd.setBounds(137, 296, 50, 29);
		tfControllerTd.setText(Double.toString(w.Td));
		tfControllerTd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					w.Td = Double.parseDouble(tfControllerTd.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błąd konwersji na double");
				}
				tfControllerTd.setText(Double.toString(w.Td));
			}
		});
		
		tfLimiterKp = new JTextField();
		tfLimiterKp.setBounds(355, 224, 50, 29);
		tfLimiterKp.setText(Double.toString(w.limKp));
		tfLimiterKp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					w.limKp = Double.parseDouble(tfLimiterKp.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błąd konwersji na double");
				}
				tfLimiterKp.setText(Double.toString(w.limKp));
			}
		});

		tfLimiterTc = new JTextField();
		tfLimiterTc.setBounds(355, 254, 50, 29);
		tfLimiterTc.setText(Double.toString(w.limTc));
		tfLimiterTc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					w.limTc = Double.parseDouble(tfLimiterTc.getText());
				} catch (NumberFormatException ev) {
					ev.printStackTrace();
					System.err.println("błąd konwersji na double");
				}
				tfLimiterTc.setText(Double.toString(w.limTc));
			}
		});
		// ========!!! Text Fieldy =======================

		// ======== P A N E L E =================
		panelSterowanie = new JPanel();
		tabbedPane.addTab("Sterowanie", null, panelSterowanie, null);
		panelSterowanie.setLayout(null);
		panelSterowanie.add(btnStart);
		panelSterowanie.add(btnStop);
		panelSterowanie.add(btnPause);
		panelSterowanie.add(btnEnd);
		panelSterowanie.add(lblDuration);
		panelSterowanie.add(lblPresure);

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
		panelRegulacja.add(ltZadana);
		panelRegulacja.add(ltMax);
		panelRegulacja.add(ltTempMin);
		panelRegulacja.add(ltTempMax);
		panelRegulacja.add(ltControllerError);
		panelRegulacja.add(ltLimiterError);
		panelRegulacja.add(ltControllerOutput);
		panelRegulacja.add(ltLimiterOutput);
		panelRegulacja.add(ltControllerKp);
		panelRegulacja.add(ltControllerTc);
		panelRegulacja.add(ltLimiterKp);
		panelRegulacja.add(ltLimiterTc);
		panelRegulacja.add(lblControllerTd);
				
		panelRegulacja.add(tfZadana);
		panelRegulacja.add(tfMaxTemp);
		panelRegulacja.add(tfControllerKp);
		panelRegulacja.add(tfControllerTc);
		panelRegulacja.add(tfControllerTd);
		panelRegulacja.add(tfLimiterKp);
		panelRegulacja.add(tfLimiterTc);

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
		panelZdarzenia.add(scrollPaneZdarzenia);

		textPaneZdarzenia = new JTextPane();
		textPaneZdarzenia.setEditable(false);// okno tylko do wyświetlania zdarzeń
		scrollPaneZdarzenia.setViewportView(textPaneZdarzenia);

		// panel wykres
		panelWykres = new JPanel();
		tabbedPane.addTab("Wykres", null, panelWykres, null);
		panelWykres.setLayout(new BorderLayout());
		lineChart = new TempChart("temperatury", w.w1measures);
		ChartPanel chartPanel = new ChartPanel(lineChart.chart);
		panelWykres.add(chartPanel, BorderLayout.CENTER);

	}

	private void checkButtonColor() {
		// ustalenie koloru Buttonów w zależności od mode
		switch (w.mode) {
		case Main.MODE_STOP:
			btnStart.setBackground(Color.LIGHT_GRAY);
			btnStop.setBackground(Color.RED);
			btnPause.setBackground(Color.LIGHT_GRAY);
			break;
		case Main.MODE_HEATING:
			btnStart.setBackground(Color.GREEN);
			btnStop.setBackground(Color.LIGHT_GRAY);
			btnPause.setBackground(Color.LIGHT_GRAY);
			break;
		case Main.MODE_PAUSE:
			btnStart.setBackground(Color.LIGHT_GRAY);
			btnStop.setBackground(Color.LIGHT_GRAY);
			btnPause.setBackground(Color.YELLOW);
			break;
		}
	}

	public void update() {
		checkButtonColor();
		ltTempMin.setText("temp minimalna = " + Main.actualMinimal);
		ltTempMax.setText("temp maksymalna = " + Main.actualMaximal);
		ltControllerError.setText("uchyb reg = " + Main.controllerError);
		ltLimiterError.setText("uchyb ogr = " + Main.limiterError);
		// ltControllerOutput.setText("wyjście reg. = " + pidController.getYh());
		// ltLimiterOutput.setText("wyjście ogr. = " + pidLimiter.getYh());
		// kolorowanie
		ltControllerOutput.setOpaque(true);
		ltLimiterOutput.setOpaque(true);
		/*
		 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! if (pidController.getMode() ==
		 * PIDController.PID_MAN) { // steruje limiter wiec
		 * ltControllerOutput.setBackground(Color.RED);
		 * ltLimiterOutput.setBackground(Color.GREEN); } else {
		 * ltControllerOutput.setBackground(Color.GREEN);
		 * ltLimiterOutput.setBackground(Color.RED); } !!!!!!!!
		 */

		// uaktualnienie pomiaru ciśnienia na zakładce sterowania
		// lblPresure.setText(String.format());
		// dodanie odpowiedniej liczby wierszu
		DefaultTableModel model = (DefaultTableModel) tabMeasurments.getModel();
		// usuwamy wszystkie
		int rowCount = model.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			int tmp = model.getRowCount();
			model.removeRow(0);
		}

		for (int i = 0; i < w.w1measures.count(); i++) {
			int tmp = w.w1measures.count();
			model.addRow(new Object[] { i + 1, w.w1measures.measureList.get(i).getName(),
					String.format("%.2f", w.w1measures.measureList.get(i).getValue()) });
		}

		// uaktualnienie ===== W Y K R E S ========= na zakładce wykres
		// dodanie kolejnej serii tylko poprzez dodanie serii i opisu
		ArrayList<Double> values = new ArrayList<Double>();
		ArrayList<String> descriptions = new ArrayList<String>();
		values.add(w.zadTemp);
		descriptions.add("temp. zadana");
		values.add(w.output);
		descriptions.add("wyjście");
		lineChart.update(values, descriptions);

	}

	public void addAlarm(String str, Style styl, AlarmEvent e) {

		try {
			Document doc = textPaneZdarzenia.getDocument();
			Document docRaport = textPaneRaport.getDocument();

			// DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
			// String str = e.getDate().toString() + " " + e.getTime().format(dtf) + ", id="
			// + e.getid() + ", "
			// + e.getDesc() + "\n";
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

}
