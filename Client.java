import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Model.Book;
import Model.User;

public class Client {
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public Client() throws UnknownHostException, IOException {
		Init();
	}
	
	//Initialize the client and get connection with server
	public void Init() throws UnknownHostException, IOException {
		if (socket == null) {
			socket = new Socket("127.0.0.1", 10000);
		}
		out = new PrintWriter(socket.getOutputStream(), true);
		oos = new ObjectOutputStream(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		ois = new ObjectInputStream(socket.getInputStream());
	}

	//The method will query all books from server
	public ArrayList<Book> QueryAll() throws ClassNotFoundException, IOException {
		out.println("QueryAll");
		@SuppressWarnings("unchecked")
		ArrayList<Book> result = (ArrayList<Book>) ois.readObject();
		return result;
	}

	//The method will regist to server
	public int Regist(User user) throws IOException {
		out.println("Register");
		oos.writeObject(user);
		return Integer.parseInt(in.readLine());
	}

	//The method will delete a book
	public int delete(Book book) throws IOException {
		out.println("Delete");
		oos.writeObject(book);
		return Integer.parseInt(in.readLine());
	}

	//The method will sell a book
	public int sell(Book book) throws IOException {
		out.println("Sell");
		oos.writeObject(book);
		return Integer.parseInt(in.readLine());
	}

	//The method will buy a book
	public int buy(Book book, String buyerAccount) throws IOException {
		out.println("Buy");
		out.println(buyerAccount);
		oos.writeObject(book);
		return Integer.parseInt(in.readLine());
	}
	
	//The method will log in server
	public User logIn(String accountInput, String password) throws ClassNotFoundException {
		out.println("Login");
		out.println(accountInput);
		out.println(password);
		try {
			User user = (User) ois.readObject();
			return user;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	//The method will regist to server
	public int addSubect(String subject) throws IOException {
		out.println("subject");
		out.println(subject);
		return Integer.parseInt(in.readLine());
	}

	//The method will recharge a user
	public int recharge(String account, double amount) throws IOException {
		out.println("Recharge");
		out.println(account);
		out.println(amount);
		return Integer.parseInt(in.readLine());
	}

	//The method will query all subjects from server
	public ArrayList<String> QueryAllSubjects() throws ClassNotFoundException, IOException {
		out.println("QueryAllSubjects");
		@SuppressWarnings("unchecked")
		ArrayList<String> result = (ArrayList<String>) ois.readObject();
		return result;
	}
}
