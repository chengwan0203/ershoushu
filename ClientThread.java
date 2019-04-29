package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import Database.SQLite;
import Model.Book;
import Model.User;

public class ClientThread extends Thread {
	private Socket client;
	private SQLite db;
	
	public ClientThread(Socket client, SQLite db) {
		this.client = client;
		this.db = db;
	}

	@Override
	public void run() {
		try {
			// Initialize the db.
			PrintWriter outBuf = new PrintWriter(client.getOutputStream(), true);
			ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
			BufferedReader readBuf = new BufferedReader(new InputStreamReader(client.getInputStream()));
			ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
			String ip = client.getInetAddress().getHostAddress();
			System.out.println(ip + " is connectiong.");
			while (true) {
				// Get the ask from client
				String kind = readBuf.readLine();
				System.out.println(kind);
				if (kind.equals("QueryAll")) {
					ArrayList<Book> books = db.queryAll();
					oos.writeObject(books);
				} else if (kind.equals("Login")) {
					String account = readBuf.readLine();
					String password = readBuf.readLine();
					User user = db.login(account, password);
					oos.writeObject(user);
				} else if (kind.equals("Register")) {
					User user = (User) ois.readObject();
					outBuf.println(db.register(user));
				} else if (kind.equals("Delete")) {
					Book book = (Book) ois.readObject();
					outBuf.println(db.deleteBook(book));
				} else if (kind.equals("Sell")) {
					Book book = (Book) ois.readObject();
					outBuf.println(db.insertNewBook(book));
				} else if (kind.equals("Buy")) {
					String buyerAccount = readBuf.readLine();
					Book book = (Book) ois.readObject();
					outBuf.println(db.buyBook(book, buyerAccount));
				} else if (kind.equals("subject")) {
					String subject = readBuf.readLine();
					outBuf.println(db.addSubject(subject));
				} else if (kind.equals("QueryAllSubjects")) {
					ArrayList<String> books = db.queryAllSubjects();
					oos.writeObject(books);
				} else if (kind.equals("Recharge")) {
					String account = readBuf.readLine();
					double amount = Double.parseDouble(readBuf.readLine());
					outBuf.println(db.recharge(account, amount));
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
}
