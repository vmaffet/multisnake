import java.awt.geom.Point2D;
import java.io.Serializable;

public class GameData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4518536293471374296L;
	
	Snake[] players;
	Point2D.Double[] it;
	int tps;
	boolean end, running, un, deux, trois;
	
	public GameData (Snake[] p, Point2D.Double[] items, int tp, boolean e, boolean r, boolean u, boolean d, boolean t) {
		players= new Snake[p.length];
		for (int i= 0; i<players.length; i++) {
			players[i]= p[i].clone();
		}
		it= items;
		tps= tp;
		end= e;
		running= r;
		un= u;
		deux= d;
		trois= t;
	}
}
