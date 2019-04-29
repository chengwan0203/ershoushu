package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import Database.SQLite;

public class Server {
	private ServerSocket server;
	private Socket client;
	private SQLite db;
	private Map<Thread, StackTraceElement[]> map;

	public Server() throws IOException {
		db = new SQLite();
		server = new ServerSocket(10000);
		while (true) {
			try {
				System.out.println("Server Started");
				System.out.println("Client Number: " + (Thread.getAllStackTraces().size() - 5));
				// Calculate the quantity of client
				map = Thread.getAllStackTraces();
				while (map.size() > 100) {
					new PrintWriter(client.getOutputStream(), true).println("Wait");
				}
				// Connect the client and server
				client = server.accept();
				new ClientThread(client, db).start();
			} catch (IOException e) {
				client.close();
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			new Server();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
