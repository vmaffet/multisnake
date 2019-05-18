import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;


public class Server_MultiSnake extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1745691907479641481L;
	
	static final int width= 30*30;
	static final int height= 30*30;
	
	static int NbrPlayers= 1;
	static final int GameTime= 60*1000;
	static int NbrItems= 5;
	
	Snake[] players;
	Timer tim1;
	Items[] it;
	int[] standingDs;
	int tps;
	boolean end, un, deux, trois;
	
	JButton pause, restart;
	JLabel disp, img;
	
	ServerSocket server;
	ObjectOutputStream[] outs;
	
	Server_MultiSnake me= this;
		
	public static void main (String[] args) {
		new Server_MultiSnake(1);
	}
	 
	public Server_MultiSnake (int n) {
		super("Server - MultiSnake v3.5");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 320);
		setResizable(false);
		setLayout(null);
		
		NbrPlayers= n;
		NbrItems*= NbrPlayers;
		
		tim1= new Timer(75, this);
		standingDs= new int[NbrPlayers];
		un= false;
		deux= false;
		trois= false;
		
		pause= new JButton();
		restart= new JButton();
		pause.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (tim1.isRunning()) {
					tim1.stop();
					sendToClients();
				} else {
					if (!end && !(un || deux || trois)) {
						Decompte dc= new Decompte(me);
						Thread thread= new Thread(dc);
						thread.start();
					}
				}
			}
		});
		restart.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				play();
			}
		});
		pause.setSize(110, 110);
		restart.setSize(110, 110);
		pause.setLocation(93, 150);
		restart.setLocation(297, 150);
		pause.setEnabled(false);
		restart.setEnabled(false);
		pause.setIcon(new ImageIcon("img/play_stop.png"));
		restart.setIcon(new ImageIcon("img/restart.png"));
		pause.setBackground(new Color(238,238,238));
		restart.setBackground(new Color(238,238,238));
		pause.setBorder(BorderFactory.createRaisedBevelBorder());
		restart.setBorder(BorderFactory.createRaisedBevelBorder());
		
		disp= new JLabel();
		disp.setSize(400, 100);
		disp.setLocation(50, 20);
		disp.setHorizontalAlignment(SwingConstants.CENTER);
		disp.setBorder(BorderFactory.createRaisedBevelBorder());
		disp.setOpaque(true);
		
		img= new JLabel();
		img.setSize(500, 300);
		img.setLocation(0, 0);
		img.setIcon(new ImageIcon("img/launch.png"));
		img.setOpaque(true);
		
		
		add(disp);
		add(pause);
		add(restart);
		add(img);
		
		setVisible(true);
		getEveryBodyConnected();
		play();
	}
	
	public void getEveryBodyConnected() {
		try {
			server= new ServerSocket(6789, 20);
			
			String connectionInfo = "Connect to:<br>";
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
	        for (NetworkInterface netint : Collections.list(nets)) {
	            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
    	        if (inetAddresses.hasMoreElements()) {
    	            String adress = "";
    	            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
    	                if (inetAddress instanceof Inet4Address)
    	                    adress = inetAddress.getHostAddress();
                    }
    	            if (!adress.equals("")) {
    	                connectionInfo += String.format("%s: [%s]<br>", netint.getDisplayName(), adress);
    	            }
    	        }
	        }
			
			Socket connection;
			outs= new ObjectOutputStream[NbrPlayers];
			for (int i= 0; i<NbrPlayers; i++) {
				disp.setText(String.format("<html>%s<br>Waiting for %d person(s) to connect</html>", connectionInfo, NbrPlayers-i));
				repaint();
				connection= server.accept();
				outs[i]= new ObjectOutputStream(connection.getOutputStream());
				outs[i].flush();
				ObjectInputStream in= new ObjectInputStream(connection.getInputStream());
				Players p= new Players(i, in, this);
				Thread thread= new Thread(p);
				thread.start();
			}
			disp.setText("Ready to start");
			pause.setEnabled(true);
			restart.setEnabled(true);
		} catch (Exception e) {
			disp.setText("Erreur, fermez toutes les applis java puis relancer");
			e.printStackTrace();
		}
	}

	public void play () {
		players= new Snake[NbrPlayers];
		standingDs= new int[players.length];
		for (int i= 0; i<players.length; i++) {
			players[i]= new Snake(new Point2D.Double(Math.floor(width*(i+1)/30/(players.length+1))*30, height/2), i);
			standingDs[i]= 0;
		}
		it= new Items[NbrItems];
		for (int i= 0; i<it.length; i++) {
			it[i]=new Items(players, it);
		}
		end= false;
		tps= GameTime;
		
		tim1.stop();
		sendToClients();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		disp.setText("Enjoy !");
		tps-= tim1.getDelay();
		end= stop();
		if (end) {
			tps+= tim1.getDelay();
			tim1.stop();
			sendToClients();
			return;
		}
		for (int i= 0; i<players.length; i++) {
			if (!players[i].dead) {
				if (Math.abs(players[i].dir-standingDs[i]) != 2) {
					players[i].dir= standingDs[i];
				}
				players[i].move();
			}
		}
		for (int j= 0; j<players.length; j++) {
			if (!players[j].dead) {
				players[j].handleMovementCasualties(players, it);
			}
		}
		for (int k= 0; k<players.length; k++) {
			players[k].updateScore();
		}
		
		sendToClients();
	}
	
	public void sendToClients() {
		Point2D.Double[] items= new Point2D.Double[it.length];
		for (int j= 0; j<items.length; j++) {
			items[j]= it[j].pos;
		}
		GameData data= new GameData (players, items, tps, end, tim1.isRunning(), un, deux, trois);
		for (int i= 0; i<outs.length; i++) {
			try {
				outs[i].writeObject(data);
				outs[i].flush();
			} catch (Exception e) {
				System.out.println("fail to send data to "+i);
				e.printStackTrace();
			}
		}
	}
	
	public boolean stop () {
		if (tps < 0) {
			return true;
		}
		if (players.length == 1) {
			return players[0].dead;
		} else {
			int k= 0;
			for (int i= 0; i<players.length; i++) {
				if (!players[i].dead) {
					k++;
				}
				if (k >= 2) {
					return false;
				}
			}
			return true;
		}
	}
	
	public void changeDir (int id, int d) {
		standingDs[id]= d;
	}
	
	public class Players implements Runnable {
		
		ObjectInputStream in;
		int playerID;
		Server_MultiSnake server;
		
		public Players (int id, ObjectInputStream input, Server_MultiSnake s) {
			in= input;
			playerID= id;
			server= s;
		}

		@Override
		public void run() {
			while (true) {
				try {
					int d= in.readInt();
					server.changeDir(playerID, d);
				} catch (Exception e) {
					System.out.println("problem reading info from client");
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public class Decompte implements Runnable {

		Server_MultiSnake game;
		
		public Decompte (Server_MultiSnake g) {
			game= g;
		}
		
		public void run() {
			try {
				game.trois= true;
				game.sendToClients();
				Thread.sleep(1000);
				game.deux= true;
				game.trois= false;
				game.sendToClients();
				Thread.sleep(1000);
				game.un= true;
				game.deux= false;
				game.sendToClients();
				Thread.sleep(1000);
				game.un= false;
				tim1.start();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
