package Model;

import java.io.Serializable;

public class Book implements Serializable {
	private static final long serialVersionUID = 1L;
	private String subject;
	private String name;
	private double price;
	private String owner;
	private String ownerPhone;
	private byte[] image;
	
	public Book(String subject, String name, double price, String owner, byte[] image, String phone) {
		this.subject = subject;
		this.name = name;
		this.price = price;
		this.owner = owner;
		this.image = image;
		this.ownerPhone = phone;
	}
	
	public String getSubject() {
		return this.subject;
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getPrice() {
		return this.price;
	}
	
	public String getOwner() {
		return this.owner;
	}
	
	public byte[] getImage() {
		return this.image;
	}
	
	public String getPhone() {
		return this.ownerPhone;
	}
}
