package Model;

import java.io.Serializable;
import java.sql.Date;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String account;
	private String pw;
	private String name;
	private int sex;
	private Date birthday;
	private String subject;
	private String phoneNo;
	private double balance;
	
	public User(String account, String pw) {
		this.account = account;
		this.pw = pw;
	}
	
	public User(String account, String pw, String name, int sex,
			Date birthday, String subject, String phoneNo, double balance) {
		this.account = account;
		this.pw = pw;
		this.name = name;
		this.sex = sex;
		this.birthday = birthday;
		this.subject = subject;
		this.phoneNo = phoneNo;
		this.balance = balance;
	}
	
	public String getAccount() {
		return this.account;
	}
	
	public String getPassword() {
		return this.pw;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getSex() {
		return this.sex;
	}
	
	public Date getBirthday() {
		return this.birthday;
	}
	
	public String getSubject() {
		return this.subject;
	}
	
	public String getPhoneNo() {
		return this.phoneNo;
	}
	
	public double getBalance() {
		return this.balance;
	}
}
