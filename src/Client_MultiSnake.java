import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Client_MultiSnake extends JFrame implements KeyListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3408996120047528320L;
	
	static final int width= 30*30;
	static final int height= 30*30;
	static final short port= 6789;
	
	BufferedImage back, wallpaper, pause, screen, iUn, iDeux, iTrois;
	Graphics gBack;
	
	Snake[] players;
	Items[] it;
	boolean end, running, readydisp, un, deux, trois;
	int tps;
	int standingD;
	
	static BufferedImage[][] imgHeads, imgTails, imgBody, imgTurns;
	static BufferedImage apple;
	
	Socket connection;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	public static void main (String[] args) {
		new Client_MultiSnake("127.0.0.1");
	}
	
	public Client_MultiSnake (String host) {
		super("Client - MultiSnake v3.5");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(width, height+50);
		setResizable(false);
		addKeyListener(this);
		
		back= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		gBack= back.getGraphics();
		try {
			wallpaper= ImageIO.read(new File("img/wall.png"));
			pause= ImageIO.read(new File("img/pause.png"));
			screen= ImageIO.read(new File("img/scoreScreen.png"));
			apple= ImageIO.read(new File("img/apple.png"));
			iUn= ImageIO.read(new File("img/un.png"));
			iDeux= ImageIO.read(new File("img/deux.png"));
			iTrois= ImageIO.read(new File("img/trois.png"));
        } catch(Exception err) {
           System.out.println("Problem finding images");            
           System.exit(0);    
        }
		
		imgHeads= new BufferedImage[5][4];
		imgTails= new BufferedImage[5][4];
		imgBody= new BufferedImage[5][4];
		imgTurns= new BufferedImage[5][8];
		for (int i= 0; i<imgHeads.length; i++) {
			getImages(i);
		}
		
		readydisp= false;
			
		connectWithServer(host);
		
		setVisible(true);
		setSize(getWidth()+getInsets().left+getInsets().right, getHeight()+getInsets().top+getInsets().bottom);
	}
	
	public void getImages(int id) {
		String tag= Integer.toString(id);
		try {
			imgHeads[id][0]= ImageIO.read(new File(tag+"/headU.png"));
			imgHeads[id][1]= ImageIO.read(new File(tag+"/headR.png"));
			imgHeads[id][2]= ImageIO.read(new File(tag+"/headD.png"));
			imgHeads[id][3]= ImageIO.read(new File(tag+"/headL.png"));
			imgTails[id][0]= ImageIO.read(new File(tag+"/tailU.png"));
			imgTails[id][1]= ImageIO.read(new File(tag+"/tailR.png"));
			imgTails[id][2]= ImageIO.read(new File(tag+"/tailD.png"));
			imgTails[id][3]= ImageIO.read(new File(tag+"/tailL.png"));
			imgBody[id][0]= ImageIO.read(new File(tag+"/bodyU.png"));
			imgBody[id][1]= ImageIO.read(new File(tag+"/bodyR.png"));
			imgBody[id][2]= ImageIO.read(new File(tag+"/bodyD.png"));
			imgBody[id][3]= ImageIO.read(new File(tag+"/bodyL.png"));
			imgTurns[id][0]= ImageIO.read(new File(tag+"/turnRU.png")); // comes and goes
			imgTurns[id][1]= ImageIO.read(new File(tag+"/turnUR.png"));
			imgTurns[id][2]= ImageIO.read(new File(tag+"/turnRD.png"));
			imgTurns[id][3]= ImageIO.read(new File(tag+"/turnUL.png"));
			imgTurns[id][4]= ImageIO.read(new File(tag+"/turnLU.png"));
			imgTurns[id][5]= ImageIO.read(new File(tag+"/turnDR.png"));
			imgTurns[id][6]= ImageIO.read(new File(tag+"/turnLD.png"));
			imgTurns[id][7]= ImageIO.read(new File(tag+"/turnDL.png"));
        } catch(Exception err) {
           System.out.println("Problem finding images "+tag);            
           System.exit(0);    
        }
	}
	
	public void paint (Graphics g) {
		if (readydisp) {
			gBack.drawImage(wallpaper, 0, 0, this);
			String s= "";
			for (int i= 0; i<players.length; i++) {
				players[i].display(gBack, this);
				s+= String.format("P%d : %d   ", i, players[i].score);
			}
			players[0].display(gBack, this);
			for (int j= 0; j<it.length; j++) {
				it[j].display(gBack, this); 
			}
			if (!(running || end)) {
				if (un) {
					gBack.drawImage(iUn, (width-iUn.getWidth())/2, (height-iUn.getHeight())/2, this);
				} else if (deux) {
					gBack.drawImage(iDeux, (width-iDeux.getWidth())/2, (height-iDeux.getHeight())/2, this);
				} else if (trois) {
					gBack.drawImage(iTrois, (width-iTrois.getWidth())/2, (height-iTrois.getHeight())/2, this);
				} else {
					gBack.drawImage(pause, (width-pause.getWidth())/2, (height-pause.getHeight())/2, this);
				}
			} else if (end) {
				gBack.drawImage(screen, (width-screen.getWidth())/2, (height-screen.getHeight())/2, this);
				gBack.setColor(new Color(0,0,255));
				Font f= new Font("Rockwell", 0, 45);
				gBack.setFont(f);
				LinkedList<String> t= getBestPlayers();
				gBack.drawString("Best Player(s) :", (width-screen.getWidth())/2+40, (height-screen.getHeight())/2+60);
				for (int i= 0; i<t.size(); i++) {
					gBack.drawString(t.get(i), (width-screen.getWidth())/2+40, (height-screen.getHeight())/2+60*(i+2));
				}
			}
			g.setColor(new Color(202,202,202));
			g.fillRect(getInsets().left, getInsets().top, width, 50);
			g.setColor(new Color(154,154,154));
			g.fillRect(getInsets().left, getInsets().top+50-12, width, 12);
			g.setColor(new Color(0,0,255));
			Font f= new Font("TimesNewRoman", 0, 20);
			g.setFont(f);
			g.drawString(String.format("%02d:%02d |", (int)Math.floor(tps/1000), (int)(0.1*tps)-100*(int)Math.floor(tps/1000)), getInsets().left+10, getInsets().top+27);
			g.drawString(s, getInsets().left+80, getInsets().top+27);
			g.drawImage(back, getInsets().left, getInsets().top+50, this);
		} else {
			g.setColor(Color.black);
			g.fillRect(0, 0, getWidth(), getHeight());
			Font f= new Font("Rockwell", 0, 40);
			g.setFont(f);
			g.setColor(Color.white);
			g.drawString("Waiting for other people to connect...", 100, getHeight()/2);
		}
		
	}
	
	public LinkedList<String> getBestPlayers () {
		int bestScore= -1;
		LinkedList<String> s= new LinkedList<String>();
		for (int i= 0; i<players.length; i++) {
			if (players[i].score > bestScore && !players[i].dead) {
				bestScore= players[i].score;
				s.clear();
				s.add(String.format("Player %d", i));
			} else if (players[i].score == bestScore && !players[i].dead) {
				s.add(String.format("Player %d", i));
			}
		}
		s.add(String.format("Final score : %d", bestScore));
		if (bestScore == -1) {
			s.clear();
			s.add("Nobody...");
		}
		return s;
	}
	
	public void updateFromServer (GameData gd) {
		readydisp= true;
		this.players= gd.players;
		it= new Items[gd.it.length];
		for (int i= 0; i<gd.it.length; i++) {
			it[i]= new Items(gd.it[i]);
		}
		this.end= gd.end;
		this.running= gd.running;
		this.tps= gd.tps;
		this.un= gd.un;
		this.deux= gd.deux;
		this.trois= gd.trois;
		repaint();
	}
	
	public void connectWithServer (String ip) {
		try {
			connection= new Socket(InetAddress.getByName(ip), port);
			out= new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in= new ObjectInputStream(connection.getInputStream());
			GetServerInfo gsi= new GetServerInfo(in, this);
			Thread thread= new Thread(gsi);
			thread.start();
		} catch (Exception e) {
			System.out.println("Problem connecting to server");
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				if (standingD != 0) {
					standingD= 0;
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (standingD != 1) {
					standingD= 1;
				}
				break;
			case KeyEvent.VK_DOWN:
				if (standingD != 2) {
					standingD= 2;
				}
				break;
			case KeyEvent.VK_LEFT:
				if (standingD != 3) {
					standingD= 3;
				}
				break;
			case KeyEvent.VK_Z:
				if (standingD != 0) {
					standingD= 0;
				}
				break;
			case KeyEvent.VK_D:
				if (standingD != 1) {
					standingD= 1;
				}
				break;
			case KeyEvent.VK_S:
				if (standingD != 2) {
					standingD= 2;
				}
				break;
			case KeyEvent.VK_Q:
				if (standingD != 3) {
					standingD= 3;
				}
				break;
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			default:
				return;
		}
		try{		
			out.writeInt(standingD);
			out.flush();
		} catch (Exception ex) {
			System.out.println("Problem sending info to server");
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	public class GetServerInfo implements Runnable {
		
		ObjectInputStream in;
		Client_MultiSnake client;
		
		public GetServerInfo (ObjectInputStream input, Client_MultiSnake c) {
			in= input;
			client= c;
		}

		@Override
		public void run() {
			while(true) {
				try {
					GameData data= (GameData)in.readObject();
					client.updateFromServer(data);
					data= null;
				} catch (Exception e) {
					System.out.println("Problem reading from server");
				}
			}
		}
		
	}
	
}
