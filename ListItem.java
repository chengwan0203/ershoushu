import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Model.Book;

public class ListItem extends JPanel {

	private static final long serialVersionUID = 1L;
	private Book book;
	private Dimension boundary;

	public ListItem(Book book) throws IOException {
		this.book = book;
		boundary = new Dimension(80, 80);
	    this.setPreferredSize(new Dimension(400, 100));
	    this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					if (Market.user.getAccount().equals("admin")) {
						int result = JOptionPane.showConfirmDialog(ListItem.this, "Delete this book?");
						if (result == JOptionPane.YES_OPTION) {
							Market.client.delete(book);
						}
					} else {
						if (!Market.user.getAccount().equals(book.getOwner())) {
							int result = JOptionPane.showConfirmDialog(ListItem.this, "Buy this book?");
							if (result == JOptionPane.YES_OPTION) {
								if (Market.user.getBalance() >= book.getPrice())
									Market.client.buy(book, Market.user.getAccount());
								else
									JOptionPane.showMessageDialog(ListItem.this, "Balance is not enough to buy this book.");
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    	
	    });
	}

	@Override
	public void paint(Graphics g) {
		ByteArrayInputStream bis = new ByteArrayInputStream(book.getImage());
	    BufferedImage image;
		try {
			image = ImageIO.read(bis);
			Image bookImage = null;
		    if (image.getWidth() >= image.getHeight()) {
		    	double scale = image.getWidth() / boundary.width;
		    	bookImage = image.getScaledInstance(boundary.width, (int)(image.getHeight() / scale), Image.SCALE_DEFAULT);
		    } else {
		    	double scale = image.getHeight() / boundary.height;
		    	bookImage = image.getScaledInstance((int)(image.getWidth() / scale), boundary.height, Image.SCALE_DEFAULT);
		    } 
			g.drawImage(bookImage, 10, 10, null);
			g.drawString(book.getPhone(), 100, 55);
			g.drawString(book.getSubject(), 100, 70);
			g.drawString(book.getOwner(), 250, 70);
			g.drawString("Price:" + book.getPrice(), 300, 70);
			g.setFont(new Font("Tahoma", Font.BOLD, 20));
			g.drawString(book.getName(), 100, 35);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
