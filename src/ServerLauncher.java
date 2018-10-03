import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ServerLauncher extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2110680727300708442L;
	
	JLabel nbr, text, img;
	JButton plus, minus, go;
	static int Nbr;
	
	public static void main (String[] args) {
		new ServerLauncher();
	}
	
	public ServerLauncher() {
		super("Server - MultiSnake v3.5");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 300);
		setResizable(false);
		setLayout(null);
		
		Nbr= 1;
		
		plus= new JButton();
		plus.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (Nbr < 5) {
					Nbr++;
				}
				nbr.setText(Integer.toString(Nbr));
			}
		});
		plus.setSize(50, 50);
		plus.setLocation(170, 100);
		plus.setIcon(new ImageIcon("img/up.png"));
		plus.setBackground(new Color(238,238,238));
		plus.setBorder(BorderFactory.createRaisedBevelBorder());
		
		minus= new JButton();
		minus.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (Nbr > 1) {
					Nbr--;
				}
				nbr.setText(Integer.toString(Nbr));
			}
		});
		minus.setSize(50, 50);
		minus.setLocation(170, 160);
		minus.setIcon(new ImageIcon("img/down.png"));
		minus.setBackground(new Color(238,238,238));
		minus.setBorder(BorderFactory.createRaisedBevelBorder());
		
		go= new JButton();
		go.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				setVisible(false);
				Thread thread= new Thread(new Starter(Nbr));
				thread.start();
			}
		});
		go.setSize(80, 80);
		go.setLocation(320, 115);
		go.setIcon(new ImageIcon("img/power.png"));
		go.setBackground(new Color(238,238,238));
		go.setBorder(BorderFactory.createRaisedBevelBorder());
		
		nbr= new JLabel("1");
		nbr.setHorizontalAlignment(SwingConstants.CENTER);
		nbr.setSize(60, 60);
		nbr.setLocation(90, 125);
		nbr.setFont(new Font(nbr.getFont().getName(), 0, 50));
		nbr.setOpaque(true);
		nbr.setBorder(BorderFactory.createRaisedBevelBorder());
		
		text= new JLabel("Entrez le nombre de joueurs :");
		text.setHorizontalAlignment(SwingConstants.CENTER);
		text.setSize(280, 30);
		text.setLocation(110, 20);
		text.setFont(new Font(text.getFont().getName(), 0, 20));
		text.setOpaque(true);
		text.setBorder(BorderFactory.createRaisedBevelBorder());
		
		img= new JLabel();
		img.setSize(500, 300);
		img.setLocation(0, 0);
		img.setIcon(new ImageIcon("img/launch.png"));
		img.setOpaque(true);
		
		add(plus);
		add(minus);
		add(go);
		add(nbr);
		add(text);
		add(img);
		
		setVisible(true);
	}
	
	public class Starter implements Runnable {
		
		int NbPlayer;
		
		public Starter (int n) {
			NbPlayer= n;
		}
		 
		public void run () {
			new Server_MultiSnake(NbPlayer);
		}
	}

}
