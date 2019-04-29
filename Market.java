import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import Model.Book;
import Model.User;

public class Market extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	private final int WIDTH = 800;
	private final int HEIGHT = 600;
	
	private JPanel controlPane;
	private JPanel bookPane;
	private List<Book> books;
	private List<String> subjects;
	
	private JButton login;
	private JButton change;
	private JButton classify;
	private JButton priceInterval;
	private JButton sold;
	private JButton addSubject;
	private JButton recharge;
	private JLabel balance;
	
	public static Client client;

	private boolean logged;
	private boolean isSeller;

	private JButton register;

	private File imageFile;
	public static User user;
	
	public Market() throws IOException {
		books = new ArrayList<>();
		logged = false;
		isSeller = false;
		this.setTitle("Book Market");
		login = new JButton("로그인");
		login.addActionListener(this);
		change = new JButton("도서등록");
		change.addActionListener(this);
		classify = new JButton("도서분류(학과별)");
		classify.addActionListener(this);
		priceInterval = new JButton("가격구간");
		priceInterval.addActionListener(this);
		sold = new JButton("판매");
		sold.addActionListener(this);
		addSubject = new JButton("학과추가");
		addSubject.addActionListener(this);
		recharge = new JButton("충전");
		recharge.addActionListener(this);
		balance = new JLabel("Balance: ");
		this.setBounds((SCREEN_WIDTH - WIDTH) / 2, (SCREEN_HEIGHT - HEIGHT) / 2, WIDTH, HEIGHT);
		bookPane = new JPanel();
		bookPane.setPreferredSize(new Dimension(400, 600));
		bookPane.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 0));
		controlPane = new JPanel();
		controlPane.setPreferredSize(new Dimension(150, 600));
		controlPane.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 20));
		controlPane.add(login);
		this.add(controlPane, BorderLayout.WEST);
		this.add(new JScrollPane(bookPane), BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			showAllBooks();
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Client cannot connect to server, check the internet and try again");
		}
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public void showAllBooks() throws IOException, ClassNotFoundException {
		if (client == null) {
			try {
				client = new Client();
			} catch(ConnectException e) {
				JOptionPane.showMessageDialog(this, "Client cannot connect to server, check the internet and try again");
			}
		}
		if (client != null) {
			books = client.QueryAll();
			subjects = client.QueryAllSubjects();
			for (Book book : books) {
				bookPane.add(new ListItem(book));
			}
			bookPane.revalidate();
			bookPane.repaint();
		}
	}
	
	public User login(String account, String password, int identity) {
		if (client == null) {
			try {
				client = new Client();
			} catch(IOException e) {
				JOptionPane.showMessageDialog(this, "Client cannot connect to server, check the internet and try again");
			}
		}
		if (client != null) {
			try {
				User user = null;
				if (identity == 2) {
					if (account.equals("admin") && password.equals("admin"))
						user = new User(account, password, null, 0, null, null, null, 0);
				} else {
					user = client.logIn(account, password);
				}
				if (user != null) {
					controlPane.removeAll();
					controlPane.add(login);
					login.setText("로그아웃");
					logged = true;
					if (identity == 0) {
						controlPane.add(change);
						controlPane.add(classify);
						change.setText("도서판매");
						isSeller = false;
						controlPane.add(priceInterval);
						balance.setText("Balance: " + user.getBalance());
						controlPane.add(balance);
					} else if (identity == 1) {
						controlPane.add(change);
						controlPane.add(classify);
						change.setText("도서구매");
						isSeller = true;
						controlPane.add(sold);
						balance.setText("Balance: " + user.getBalance());
						controlPane.add(balance);
					} else {
						controlPane.add(addSubject);
						controlPane.add(recharge);
					}
				}
				return user;
			} catch (ClassNotFoundException e) {
				JOptionPane.showMessageDialog(this, "Account or password is wrong!");
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		try {
			new Market();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == login) {
			if (!logged) {
				Market.this.setEnabled(false);
				JFrame logFrame;
				JLabel title;
				JLabel account;
				JLabel password;
				JRadioButton seller;
				JRadioButton buyer;
				JRadioButton admin;
				ButtonGroup identity;
				logFrame = new JFrame();
				logFrame.setTitle("Log In");
				logFrame.setSize(350, 220);
				logFrame.setVisible(true);
				logFrame.setLocation(500, 250);
				logFrame.setResizable(false);
				logFrame.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent we) {
						Market.this.setEnabled(true);
						logFrame.dispose();
					}
				});

				JTextField textAccount = new JTextField();
				JPasswordField textPassword = new JPasswordField();
				title = new JLabel("The center of log in");
				account = new JLabel("아이디:");
				password = new JLabel("비밀번호: ");
				seller = new JRadioButton("판매");
				buyer = new JRadioButton("구매");
				admin = new JRadioButton("관리자");
				identity = new ButtonGroup();
				identity.add(seller);
				identity.add(buyer);
				identity.add(admin);
				buyer.setSelected(true);
				JButton loginConfirm = new JButton("로그인");
				register = new JButton("회원가입");

				GroupLayout layout = new GroupLayout(logFrame.getContentPane());
				logFrame.getContentPane().setLayout(layout);
				GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
				hGroup.addGap(5);
				hGroup.addGroup(layout.createParallelGroup().addComponent(account).addComponent(password));
				hGroup.addGap(5);
				hGroup.addGroup(layout.createParallelGroup().addComponent(title).addComponent(textAccount)
						.addComponent(textPassword).addComponent(seller).addComponent(buyer).addComponent(admin)
						.addGroup(layout.createSequentialGroup().addComponent(loginConfirm).addGap(20).addComponent(register)));
				hGroup.addGap(5);
				layout.setHorizontalGroup(hGroup);
				GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
				vGroup.addGap(10);
				vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(title));
				vGroup.addGap(20);
				vGroup.addGroup(layout.createParallelGroup().addComponent(account).addComponent(textAccount));
				vGroup.addGap(5);
				vGroup.addGroup(layout.createParallelGroup().addComponent(password).addComponent(textPassword));
				vGroup.addGap(5);
				vGroup.addGroup(layout.createParallelGroup().addComponent(seller));
				vGroup.addGap(5);
				vGroup.addGroup(layout.createParallelGroup().addComponent(buyer));
				vGroup.addGap(5);
				vGroup.addGroup(layout.createParallelGroup().addComponent(admin));
				vGroup.addGap(5);
				vGroup.addGroup(layout.createParallelGroup().addComponent(loginConfirm).addComponent(register));
				vGroup.addGap(10);
				layout.setVerticalGroup(vGroup);
				loginConfirm.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (textAccount.getText().equals("") || textPassword.getPassword().length == 0) {
							JOptionPane.showMessageDialog(logFrame, "Account and password cannot be empty!");
						} else {
							User user;
							if (buyer.isSelected()) {
								user = login(textAccount.getText(), new String(textPassword.getPassword()), 0);
							} else if (seller.isSelected()) {
								user = login(textAccount.getText(), new String(textPassword.getPassword()), 1);
							} else {
								user = login(textAccount.getText(), new String(textPassword.getPassword()), 2);
							}
							if (user == null) {
								JOptionPane.showMessageDialog(logFrame, "Account or password is wrong or account does not exist!");
							} else {
								Market.user = user;
								logFrame.dispose();
								Market.this.setEnabled(true);
							}
						}
					}
					
				});
				register.addActionListener(this);
				logFrame.pack();
				logFrame.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(Market.this, "Goodbye");
				logged = false;
				login.setText("로그인");
				controlPane.removeAll();
				controlPane.add(login);
				controlPane.repaint();
			}
		} else if (e.getSource() == register) {
			JFrame registerFrame;
			JLabel title;
			JLabel account;
			JLabel password;
			JLabel name;
			JLabel birthday;
			JLabel subject;
			JLabel phoneNo;
			JRadioButton male;
			JRadioButton female;
			ButtonGroup sex;
			registerFrame = new JFrame();
			registerFrame.setTitle("Register");
			registerFrame.setSize(350, 220);
			registerFrame.setVisible(true);
			registerFrame.setLocation(500, 250);
			registerFrame.setResizable(false);

			JTextField textAccount = new JTextField();
			JPasswordField textPassword = new JPasswordField();
			JTextField textName = new JTextField();
			JComboBox<String> textSubject = new JComboBox<String>();
			for(String s : subjects) {
				textSubject.addItem(s);
			}
			//textSubject.setSelectedIndex(0);
			JTextField textBirthday = new JTextField();
			JTextField textPhoneNo = new JTextField();
			title = new JLabel("The center of register");
			account = new JLabel("아이디: ");
			password = new JLabel("비밀번호: ");
			name = new JLabel("닉네임: ");
			birthday = new JLabel("<html>출생년월:<br />(yyyy-MM-dd)</html>");
			subject = new JLabel("학과: ");
			phoneNo = new JLabel("전화번호: ");
			account = new JLabel("아이디: ");
			password = new JLabel("비밀번호: ");
			male = new JRadioButton("남");
			female = new JRadioButton("여");
			sex = new ButtonGroup();
			sex.add(male);
			sex.add(female);
			male.setSelected(true);
			JButton cancel = new JButton("취소");
			JButton registerConfirm = new JButton("회원가입");

			GroupLayout layout = new GroupLayout(registerFrame.getContentPane());
			registerFrame.getContentPane().setLayout(layout);
			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
			hGroup.addGap(5);
			hGroup.addGroup(layout.createParallelGroup().addComponent(account).addComponent(password).addComponent(name)
					.addComponent(birthday).addComponent(subject).addComponent(phoneNo));
			hGroup.addGap(5);
			hGroup.addGroup(layout.createParallelGroup().addComponent(title).addComponent(textAccount)
					.addComponent(textPassword).addComponent(textName).addComponent(textBirthday)
					.addComponent(textSubject).addComponent(textPhoneNo)
					.addGroup(layout.createSequentialGroup().addComponent(male).addGap(20).addComponent(female))
					.addGroup(layout.createSequentialGroup().addComponent(cancel).addGap(20).addComponent(registerConfirm)));
			hGroup.addGap(5);
			layout.setHorizontalGroup(hGroup);
			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
			vGroup.addGap(10);
			vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(title));
			vGroup.addGap(20);
			vGroup.addGroup(layout.createParallelGroup().addComponent(account).addComponent(textAccount));
			vGroup.addGap(5);
			vGroup.addGroup(layout.createParallelGroup().addComponent(password).addComponent(textPassword));
			vGroup.addGap(5);
			vGroup.addGroup(layout.createParallelGroup().addComponent(name).addComponent(textName));
			vGroup.addGap(5);
			vGroup.addGroup(layout.createParallelGroup().addComponent(birthday).addComponent(textBirthday));
			vGroup.addGap(5);
			vGroup.addGroup(layout.createParallelGroup().addComponent(subject).addComponent(textSubject));
			vGroup.addGap(5);
			vGroup.addGroup(layout.createParallelGroup().addComponent(phoneNo).addComponent(textPhoneNo));
			vGroup.addGap(5);
			vGroup.addGroup(layout.createParallelGroup().addComponent(male).addComponent(female));
			vGroup.addGap(5);
			vGroup.addGroup(layout.createParallelGroup().addComponent(cancel).addComponent(registerConfirm));
			vGroup.addGap(10);
			layout.setVerticalGroup(vGroup);
			registerConfirm.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (textAccount.getText().equals("") || textPassword.getPassword().length == 0
							|| textName.getText().equals("") || textBirthday.getText().equals("")
							|| subjects.get(textSubject.getSelectedIndex()).equals("") || textPhoneNo.getText().equals("")) {
						JOptionPane.showMessageDialog(registerFrame, "All fields cannot be empty!");
					} else {
						try {
							User user;
							if (female.isSelected()) {
								user = new User(textAccount.getText(), new String(textPassword.getPassword())
										, textName.getText(), 1, new Date(new SimpleDateFormat("yyyy-MM-dd").parse(textBirthday.getText()).getTime())
										, subjects.get(textSubject.getSelectedIndex()), textPhoneNo.getText(), 0);
							} else {
								user = new User(textAccount.getText(), new String(textPassword.getPassword())
										, textName.getText(), 2, new Date(new SimpleDateFormat("yyyy-MM-dd").parse(textBirthday.getText()).getTime())
										, subjects.get(textSubject.getSelectedIndex()), textPhoneNo.getText(), 0);
							}
							try {
								int result = client.Regist(user);
								if (result == 1) {
									JOptionPane.showMessageDialog(registerFrame, "Connection Success!");
									registerFrame.dispose();
								} else {
									JOptionPane.showMessageDialog(registerFrame, "Account is already existing!");
								}
							} catch (IOException e) {
								JOptionPane.showMessageDialog(registerFrame, "Connection lost!");
							}
						} catch (ParseException e) {
							JOptionPane.showMessageDialog(registerFrame, "Date has wrong format!");
						}
					}
				}
				
			});
			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					registerFrame.dispose();
				}
				
			});
			registerFrame.pack();
			registerFrame.setVisible(true);
		} else if (e.getSource() == change) {
			if (isSeller) {
				change.setText("판매전환");
				isSeller = false;
				controlPane.remove(sold);
				controlPane.add(priceInterval);
				controlPane.repaint();
			} else {
				change.setText("구매전환");
				isSeller = true;
				controlPane.remove(priceInterval);
				controlPane.add(sold);
				controlPane.repaint();
			}
		} else if (e.getSource() == classify) {
			Collections.sort(books, new Comparator<Book>() {

				@Override
				public int compare(Book arg0, Book arg1) {
					return arg0.getSubject().compareTo(arg1.getSubject());
				}
				
			});
			bookPane.removeAll();
			for (Book book : books) {
				try {
					bookPane.add(new ListItem(book));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			bookPane.revalidate();
			bookPane.repaint();
		} else if (e.getSource() == priceInterval) {
			String lower = JOptionPane.showInputDialog(this, "Input the lower bounds", "Price Interval", JOptionPane.INFORMATION_MESSAGE);
			String upper = JOptionPane.showInputDialog(this, "Input the upper bounds", "Price Interval", JOptionPane.INFORMATION_MESSAGE);
			try {
				double lBounds = Double.parseDouble(lower);
				double uBounds = Double.parseDouble(upper);
				bookPane.removeAll();
				for (Book book : books) {
					if (book.getPrice() >= lBounds && book.getPrice() <= uBounds) {
						try {
							bookPane.add(new ListItem(book));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				bookPane.revalidate();
				bookPane.repaint();
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(Market.this, "Lower and upper bounds must be float numbers");
			}
		} else if (e.getSource() == addSubject) {
			String subject = JOptionPane.showInputDialog(this, "Input the new subject", "Add Subject", JOptionPane.INFORMATION_MESSAGE);
			try {
				if (client.addSubect(subject) == 1)
					JOptionPane.showMessageDialog(Market.this, "Add subject success");
				else 
					JOptionPane.showMessageDialog(Market.this, "Add subject failed");
			} catch (HeadlessException e1) {
				JOptionPane.showMessageDialog(Market.this, "Add subject failed");
				e1.printStackTrace();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(Market.this, "Add subject failed");
				e1.printStackTrace();
			}
		} else if (e.getSource() == recharge) {
			String account = JOptionPane.showInputDialog(this, "Input the recharge account", "Recharge", JOptionPane.INFORMATION_MESSAGE);
			String amount = JOptionPane.showInputDialog(this, "Input the amount", "Recharge", JOptionPane.INFORMATION_MESSAGE);
			try {
				double money = Double.parseDouble(amount);
				int result = client.recharge(account, money);
				if (result > 0)
					JOptionPane.showMessageDialog(Market.this, "Recharge success");
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(Market.this, "Lower and upper bounds must be float numbers");
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(Market.this, "IO Exceptions");
			}
		} else if (e.getSource() == sold) {
			JFrame sellFrame;
			JLabel title;
			JLabel name;
			JLabel price;
			JLabel subject;
			JLabel picture;
			JLabel pictureName;
			JButton choose;
			imageFile = null;
			sellFrame = new JFrame();
			sellFrame.setTitle("Sell Book");
			sellFrame.setSize(350, 220);
			sellFrame.setVisible(true);
			sellFrame.setLocation(500, 250);
			sellFrame.setResizable(false);

			JTextField textName = new JTextField();
			JTextField textPrice = new JTextField();
			JComboBox<String> textSubject = new JComboBox<String>();
			for(String s : subjects) {
				textSubject.addItem(s);
			}
			title = new JLabel("The center of register");
			name = new JLabel("도서이름: ");
			subject = new JLabel("학과: ");
			price = new JLabel("가격: ");
			picture = new JLabel("사진: ");
			pictureName = new JLabel("");
			choose = new JButton("선택");
			JButton cancel = new JButton("취소");
			JButton sellConfirm = new JButton("판매");

			GroupLayout layout = new GroupLayout(sellFrame.getContentPane());
			sellFrame.getContentPane().setLayout(layout);
			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
			hGroup.addGap(5);
			hGroup.addGroup(layout.createParallelGroup().addComponent(name).addComponent(subject).addComponent(price)
					.addComponent(picture));
			hGroup.addGap(5);
			hGroup.addGroup(layout.createParallelGroup().addComponent(title).addComponent(textName)
					.addComponent(textSubject).addComponent(textPrice)
					.addGroup(layout.createSequentialGroup().addComponent(choose).addGap(20).addComponent(pictureName))
					.addGroup(layout.createSequentialGroup().addComponent(cancel).addGap(20).addComponent(sellConfirm)));
			hGroup.addGap(5);
			layout.setHorizontalGroup(hGroup);
			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
			vGroup.addGap(10);
			vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(title));
			vGroup.addGap(20);
			vGroup.addGroup(layout.createParallelGroup().addComponent(name).addComponent(textName));
			vGroup.addGap(5);
			vGroup.addGroup(layout.createParallelGroup().addComponent(subject).addComponent(textSubject));
			vGroup.addGap(5);
			vGroup.addGroup(layout.createParallelGroup().addComponent(price).addComponent(textPrice));
			vGroup.addGap(5);
			vGroup.addGroup(layout.createParallelGroup().addComponent(picture).addComponent(choose).addComponent(pictureName));
			vGroup.addGap(5);
			vGroup.addGroup(layout.createParallelGroup().addComponent(cancel).addComponent(sellConfirm));
			vGroup.addGap(10);
			layout.setVerticalGroup(vGroup);
			choose.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
					// filter the files
					FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg", "gif", "png");
					fileChooser.addChoosableFileFilter(filter);
					int result = fileChooser.showSaveDialog(null);
					// if the user click on save in Jfilechooser
					if (result == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();
						String path = selectedFile.getAbsolutePath();
						imageFile = new File(path);
						pictureName.setText(path);
					}
					// if the user click on save in Jfilechooser
					else if (result == JFileChooser.CANCEL_OPTION) {
						JOptionPane.showMessageDialog(sellFrame, "No file select!");
					}
				}
				
			});
			sellConfirm.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						String name = textName.getText();
						String subject = subjects.get(textSubject.getSelectedIndex());
						double price = Double.parseDouble(textPrice.getText());
						if (name.equals("") || subject.equals("") 
								|| textPrice.getText().equals("") || price < 0 || imageFile == null) {
							JOptionPane.showMessageDialog(sellFrame, "All fields cannot be empty!");
						} else {
							Book book = new Book(subject, name, price, user.getAccount(), Files.readAllBytes(imageFile.toPath()), user.getPhoneNo());
							try {
								int result = client.sell(book);
								if (result == 1) {
									JOptionPane.showMessageDialog(sellFrame, "Sell book Success!");
									sellFrame.dispose();
								} else {
									JOptionPane.showMessageDialog(sellFrame, "Sell book failed");
								}
							} catch (IOException e) {
								JOptionPane.showMessageDialog(sellFrame, "Connection lost!");
							}
						}
					} catch(NumberFormatException e) {
						JOptionPane.showMessageDialog(sellFrame, "Price has to be float numbers!");
					} catch(IOException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(sellFrame, "IO Exception");
					}
				}
				
			});
			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					sellFrame.dispose();
				}
				
			});
			sellFrame.pack();
			sellFrame.setVisible(true);
		}
	}

}
