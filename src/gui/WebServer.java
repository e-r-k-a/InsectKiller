package gui;

import Application.Main;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WebServer extends WebSocketServer {

	private ArrayList<Object> webData = new ArrayList<Object>();
	private static int TCP_PORT = 4444;
	private static int WWW_DATA_COUNT = 7;// liczba wymienianych danych z www
	private Set<WebSocket> conns;

	public WebServer() {
		super(new InetSocketAddress(TCP_PORT));
		conns = new HashSet<>();
		// dodanie pustych elementów do listy. Wypełnianie ich w main
		for (int i = 0; i < WWW_DATA_COUNT; i++) {
			webData.add(new Object());
		}
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conns.add(conn);
		System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		conns.remove(conn);
		System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		switch (message) {
		case "readReq": //www czeka na wysłanie paczki

			// System.out.println("Message from client: " + message);
			for (WebSocket sock : conns) {
				String msg = "";
				for (Object i : webData) {
					i.toString();
					msg += i.toString() + ";";
				}
				sock.send(msg);
			}
			;
			break;
		case "startReq": //na www wcisniety klawisz start
		//	System.out.println("START");
			Main.mode = Main.MODE_HEATING; 
			break;
		case "stopReq":
		//	System.out.println("STOP");
			Main.mode = Main.MODE_STOP;
			break;
		case "pauseReq":
	//		System.out.println("Pausa");
			Main.mode = Main.MODE_PAUSE;
			break;
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		// ex.printStackTrace();
		if (conn != null) {
			conns.remove(conn);
			// do some thing if required
			System.out.println("błąd");
		}
		System.out.println("błąd");
		// System.out.println("ERROR from " +
		// conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onStart() {
		System.out.println("onStart");
	}

	public ArrayList<Object> getData() {
		return webData;
	}
}
