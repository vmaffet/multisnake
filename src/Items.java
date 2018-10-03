import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.io.Serializable;

public class Items implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4332447970691127576L;
	Point2D.Double pos;
	
	public Items (Snake[] s, Items[] i) {
		respectPositioning(s, i);
	}
	
	public Items (Point2D.Double p) {
		pos= p;
	}
	
	public void respectPositioning (Snake[] s, Items[] it) {
		int w= Server_MultiSnake.width/30;
		int h= Server_MultiSnake.height/30;
		boolean yup;
		Point2D.Double p;
		do {
			p= new Point2D.Double(30*(int)(Math.random()*w), 30*(int)(Math.random()*h));
			yup= false;
			for (int i= 0; i<s.length; i++) {
				if (s[i].pointOnSnake(p)) {
					yup= true;
					continue;
				}
			}
			for (int j= 0; j<it.length; j++) {
				try {
					if (p.equals(it[j].pos)) {
						yup= true;
						continue;
					}
				} catch (Exception E) {}
			}
		} while (yup);
		pos= p;
	}
	
	public void display (Graphics g, ImageObserver obs) {
		g.drawImage(Client_MultiSnake.apple, (int)pos.getX(), (int)pos.getY(), obs);
	}
}
