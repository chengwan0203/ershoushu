package Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Model.Book;
import Model.User;

public class SQLite {
	Connection conn;

	// Create a new database at the source directory.
	public SQLite() {
		try {
			//Find the driver for sqlite database
			Class.forName("org.sqlite.JDBC");
			File file = new File("/Users/chengwanbai/Desktop/BookMarket");
			//Create the folder if it not exist.
			if (!file.exists()) {
				file.mkdirs();
				//Make connection with sqlite database
				conn = DriverManager.getConnection("jdbc:sqlite:/Users/chengwanbai/Desktop/BookMarket/BookMarket.db");
				//Create sql statement.
				//Create three table to store all the data.
				Create("CREATE TABLE user(id varchar(20) primary key," 
						+ "password varchar(20),"
						+ "name varchar(20),"
						+ "birthday date,"
						+ "subject varchar(50),"
						+ "phoneNo varchar(20),"
						+ "balance decimal(20,2),"
						+ "sex integer)");
				Create("CREATE TABLE book(subject varchar(50)," 
						+ "name varchar(50)," 
						+ "price decimal(10,2),"
						+ "owner_account varchar(20),"
						+ "isSold integer,"
						+ "buyer_account varchar(20),"
						+ "photo BLOB)");
				Create("CREATE TABLE admin(account varchar(20) primary key," 
						+ "password varchar(20)," 
						+ "sex integer,"
						+ "birthday date)");
				Create("CREATE TABLE subject(subject varchar(20) primary key)");
			} else {
				conn = DriverManager.getConnection("jdbc:sqlite:/Users/chengwanbai/Desktop/BookMarket/BookMarket.db");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Create a new table.
	public boolean Create(String sql) {
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			return ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public int register(User user) {
		String sql = "insert into user values(?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, user.getAccount());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getName());
			ps.setDate(4, user.getBirthday());
			ps.setString(5, user.getSubject());
			ps.setString(6, user.getPhoneNo());
			ps.setDouble(7, user.getBalance());
			ps.setInt(8, user.getSex());
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int insertNewBook(Book book) {
		String sql = "insert into book values(?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, book.getSubject());
			ps.setString(2, book.getName());
			ps.setDouble(3, book.getPrice());
			ps.setString(4, book.getOwner());
			ps.setInt(5, 0);
			ps.setString(6, null);
			ps.setBytes(7, book.getImage());
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int deleteBook(Book book) {
		String sql = "delete from where name = ? and owner_account = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, book.getName());
			ps.setString(2, book.getOwner());
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int buyBook(Book book, String buyer) {
		String sql = "update book set isSold = 1, buyer_account = ? where name = ? and owner_account = ?";
		String sql2 = "update user set balance = balance - ? where id = ?";
		String sql3 = "update user set balance = balance + ? where id = ?";
		try {
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			ps2.setDouble(1, book.getPrice());
			ps2.setString(2, buyer);
			int result = ps2.executeUpdate();
			PreparedStatement ps3 = conn.prepareStatement(sql3);
			ps3.setDouble(1, book.getPrice());
			ps3.setString(2, book.getOwner());
			result = result & ps3.executeUpdate();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, buyer);
			ps.setString(2, book.getName());
			ps.setString(3, book.getOwner());
			return result & ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public User login(String account, String pw) {
		String sql = "select * from user where id=?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, account);
			ResultSet result = ps.executeQuery();
			if (result.next()) {
				return new User(result.getString("id"), result.getString("password")
						, result.getString("name"), result.getInt("sex")
						, result.getDate("birthday"), result.getString("subject")
						, result.getString("phoneNo"), result.getDouble("balance"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<Book> queryAll() {
		ArrayList<Book> books = new ArrayList<>();
		String sql = "select book.name, book.subject, price, owner_account, photo, user.phoneNo from book, user where isSold=0 and book.owner_account=user.id";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet result = ps.executeQuery();
			while (result.next()) {
				books.add(new Book(result.getString("subject"), result.getString("name"), result.getDouble("price")
						, result.getString("owner_account"), result.getBytes("photo"), result.getString("phoneNo")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return books;
	}
	
	public int addSubject(String subject) {
		String sql = "select * from subject where subject=?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, subject);
			ResultSet result = ps.executeQuery();
			if (!result.next()) {
				sql = "insert into subject values(?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, subject);
				return ps.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int recharge(String account, double balance) {
		String sql = "update user set balance = balance + ? where id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setDouble(1, balance);
			ps.setString(2, account);
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	//Close the statement and connection
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> queryAllSubjects() {
		ArrayList<String> subjects = new ArrayList<>();
		String sql = "select * from subject";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet result = ps.executeQuery();
			while (result.next()) {
				subjects.add(result.getString("subject"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subjects;
	}
}
