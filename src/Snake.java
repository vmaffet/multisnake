import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.util.*;

public class Snake implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4249349465027592581L;
	
	LinkedList<Point2D.Double> body;
	int dir, score, identifier;
	boolean dead, grow;
	
	public Snake (Point2D.Double st, int id) {
		body= new LinkedList<Point2D.Double>();
		body.add(st);
		body.add(new Point2D.Double(st.getX(), st.getY()+5*30));
		updateScore();
		identifier= id>4?0:id;
		dir= 0; // 0: UP then clockwise
		dead= false;
		grow= false;
	}
	
	public Snake (LinkedList<Point2D.Double> bod, int d, int s, int id, boolean dea, boolean g) {
		body= bod;
		dir= d;
		score= s;
		identifier= id;
		dead= dea;
		grow= g;
	}
	
	@SuppressWarnings("unchecked")
	public Snake clone () {
		return new Snake ((LinkedList<Point2D.Double>)body.clone(), dir, score, identifier, dead, grow);
	}
	
	public void display (Graphics g, ImageObserver obs) {
		int lastD= getDirection(body.get(0), body.get(1));
		double d;
		for (int i= 0; i<body.size(); i++) {
			if (i == 0) {
				g.drawImage(Client_MultiSnake.imgHeads[identifier][lastD], (int)body.get(i).getX(), (int)body.get(i).getY(), obs);
			} else if (i == body.size()-1) {
				g.drawImage(Client_MultiSnake.imgTails[identifier][lastD], (int)body.get(i).getX(), (int)body.get(i).getY(), obs);
			} else {
				if (getDirection(body.get(i), body.get(i+1)) <= 1) {
					lastD+= 4;
				} 
				g.drawImage(Client_MultiSnake.imgTurns[identifier][lastD], (int)body.get(i).getX(), (int)body.get(i).getY(), obs);
				lastD= getDirection(body.get(i), body.get(i+1));
			}
			if (i != body.size()-1) {
				if (lastD%2 == 0) {
					d= body.get(i).getY()-body.get(i+1).getY();
					for (int j= 1; j<Math.abs(d)/30; j++) {
						g.drawImage(Client_MultiSnake.imgBody[identifier][lastD], (int)body.get(i).getX(), (int)(body.get(i).getY()-d/Math.abs(d)*j*30), obs);
					}
				} else {
					d= body.get(i).getX()-body.get(i+1).getX();
					for (int j= 1; j<Math.abs(d)/30; j++) {
						g.drawImage(Client_MultiSnake.imgBody[identifier][lastD], (int)(body.get(i).getX()-d/Math.abs(d)*j*30), (int)body.get(i).getY(), obs);
					}
				}
			}
		}
		/*g.setColor(Color.red);
		for (int i= 0; i<body.size()-1; i++) {
			g.drawLine((int)body.get(i).getX(), (int)body.get(i).getY(), (int)body.get(i+1).getX(), (int)body.get(i+1).getY());
		}*/
	}
	
	public int getDirection (Point2D.Double from, Point2D.Double to) {
		double dx= to.getX()-from.getX();
		double dy= to.getY()-from.getY();
		if (dx == 0) {
			if (dy < 0) {
				return 2;
			} else {
				return 0;
			}
		} else if (dx < 0) {
			return 1;
		} else {
			return 3;
		}
	}
	
	public void move () {
		if (getDirection(body.get(0), body.get(1)) != dir) {
			body.addFirst(body.getFirst());
		}
		int s= body.size();
		if (dir%2 == 0) {
			body.set(0, new Point2D.Double(body.getFirst().getX(),  body.getFirst().getY()+30*(dir-1)));
		} else {
			body.set(0, new Point2D.Double(body.getFirst().getX()+30*(2-dir),  body.getFirst().getY()));
		}
		if (!grow) {
			int d= getDirection(body.get(s-2), body.getLast());
			if (d%2 == 0) {
				body.set(s-1, new Point2D.Double(body.getLast().getX(),  body.getLast().getY()+30*(d-1)));
			} else {
				body.set(s-1, new Point2D.Double(body.getLast().getX()+30*(2-d),  body.getLast().getY()));
			}
			if (body.getLast().equals(body.get(s-2))) {
				body.remove(s-2);
			}
		} else {
			grow= false;
		}
	}
	
	public int snakeCollision (Snake s) {
		Line2D.Double l;
		for (int i= 0; i<s.body.size()-1; i++) {
			if (i == 0) {
				if (body.getFirst().equals(s.body.getFirst()) && body.getLast().equals(s.body.getLast())) {
					continue;
				}
			}
			l= new Line2D.Double(s.body.get(i), s.body.get(i+1));
			if (l.ptSegDist(body.getFirst()) == 0) {
				if (!body.getLast().equals(s.body.getLast())) {
					if (body.getFirst().distance(s.body.getFirst()) == 0) {
						if (score > s.score) {
							s.putAway();
							grow =true;
						} else if (score == s.score) {
							s.putAway();
							putAway();
						} else {
							putAway();
							s.grow =true;
						}
						return -1;
					} else if (body.getFirst().distance(s.body.getFirst()) <= 30) {
						l= new Line2D.Double(body.get(0), body.get(1));
						if (l.ptSegDist(s.body.getFirst()) == 0) {
							if (score > s.score) {
								s.putAway();
								grow =true;
							} else if (score == s.score) {
								s.putAway();
								putAway();
							} else {
								putAway();
								s.grow =true;
							}
							return -1;
						}
						s.putAway();
						grow =true;
						return -1;
					}
					grow= true;
				}
				return i;
			}
		}
		return -1;
	}
	
	public boolean pointOnSnake (Point2D.Double p) {
		Line2D.Double l;
		for (int i= 0; i<body.size()-1; i++) {
			l= new Line2D.Double(body.get(i), body.get(i+1));
			if (l.ptSegDist(p) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public void cutSnake (int n, Point2D.Double p) {
		do {
			body.removeLast();
		} while (body.size() > n+1);
		int d= getDirection(body.get(n), p);
		if (d%2 == 0) {
			p.setLocation(p.getX(), p.getY()+30*(d-1));
		} else {
			p.setLocation(p.getX()+30*(2-d), p.getY());
		}
		if (body.getLast().equals(p)) {
			body.set(n, p);
		} else {
			body.addLast(p);
		}
	}
	
	public void handleMovementCasualties (Snake[] tab, Items[] it) {
		int c;
		for (int i= 0; i< tab.length; i++) {
			c= snakeCollision(tab[i]);
			if (c != -1) {
				tab[i].cutSnake(c, new Point2D.Double(body.getFirst().getX(), body.getFirst().getY()));
			}
		}
		Point2D.Double f= body.getFirst();
		if (f.getX() < 0 || f.getY() < 0 || f.getX() >= Server_MultiSnake.width || f.getY() >= Server_MultiSnake.height) {
			dead= true;
		}
		for (int j= 0; j<it.length; j++) {
			if (f.equals(it[j].pos)) {
				grow= true;
				it[j].respectPositioning(tab, it);
				break;
			}
		}
	}
	
	public void updateScore () {
		double s= 0;
		for (int i= 0; i<body.size()-1; i++) {
			s+= body.get(i).distance(body.get(i+1));
		}
		score= (int)s/30;
	}
	
	public void putAway () {
		dead= true;
		body.clear();
		body.add(new Point2D.Double(Server_MultiSnake.width+10, 0));
		body.add(new Point2D.Double(Server_MultiSnake.width+10, 10));
	}
	
	public String toString () {
		return String.format("mort : %b dir : %d score : %d head : %s tail : %s", dead, dir, score, body.getFirst(), body.getLast());
	}
	
}