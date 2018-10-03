import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ClientLauncher extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1898127629067334887L;
	
	JTextField tf;
	JButton go;
	JLabel lab, img;
	
	public static void main (String[] args) {
		new ClientLauncher();
	}

	public ClientLauncher () {
		super("Client - MultiSnake v3.5");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 300);
		setResizable(false);
		setLayout(null);
		
		tf= new JTextField("127.0.0.1");
		tf.setSize(200, 30);
		tf.setLocation(150, 100);
		tf.setHorizontalAlignment(SwingUtilities.CENTER);
		
		go= new JButton();
		go.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				setVisible(false);
				new Client_MultiSnake(tf.getText());
			}
		});
		go.setSize(75, 75);
		go.setLocation(212, 150);
		go.setIcon(new ImageIcon("img/start.png"));
		go.setBorder(BorderFactory.createRaisedBevelBorder());
		go.setBackground(new Color(238,238,238));
		
		lab= new JLabel("Entrez l'adresse du serveur :");
		lab.setHorizontalAlignment(SwingConstants.CENTER);
		lab.setSize(200, 30);
		lab.setLocation(150, 60);
		lab.setBorder(BorderFactory.createRaisedBevelBorder());
		lab.setOpaque(true);
		
		img= new JLabel();
		img.setSize(500, 300);
		img.setLocation(0, 0);
		img.setIcon(new ImageIcon("img/launch.png"));
		img.setOpaque(true);
		
		add(lab);
		add(tf);
		add(go);
		add(img);
		
		setVisible(true);
	}
	
	
}
