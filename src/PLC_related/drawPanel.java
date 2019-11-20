package PLC_related;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class drawPanel extends JPanel { /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
// JPanel???�속???�래??
	static BufferedImage tempImage;
	public static Graphics2D g2d;

	public void init(int x, int y) {
		tempImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		g2d = tempImage.createGraphics();
		g2d.setBackground(Color.white);
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, x, y);
		g2d.setColor(Color.black);
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(tempImage, 0, 0, this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); // JPanel??paintComponent ?�선 ?�출
	}

	@Override
	public void update(Graphics g) {
		g.drawImage(tempImage, 0, 0, this);
	}
}