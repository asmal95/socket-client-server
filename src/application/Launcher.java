package application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import server.Listener;


/**
 * Выполняет запуск приложения. Позволяет выбрать режим работы (клиент или сервер или оба)
 * @author Andrew
 *
 */
public class Launcher extends JFrame {
	private static Logger LOG = Logger.getLogger(Launcher.class);
	public static void main(String[] args) {
		Launcher launch = new Launcher();
		launch.setVisible(true);
	}
	
	public Launcher() {
		super("Launcher");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel root = new JPanel();
		//this.get
		root.setSize(160, 200);
		//root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.setLayout(null);
		
		
		
		JButton clientButton = new JButton("Клиент");
		clientButton.setBounds(20, 20, 100, 20);
		
		JButton serverButton = new JButton("Сервер");
		serverButton.setBounds(20, 50, 100, 20);
		
		clientButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ClientFrame client = new ClientFrame();
				client.setVisible(true);
			}
		});
		
		serverButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Listener listener = new Listener(645);
					listener.start();
				} catch (IOException ex) {
					LOG.error(ex);
				}
			}
		});
		
		root.add(clientButton);
		root.add(serverButton);
		
		this.setContentPane(root);
		this.setSize(160, 140);
	}
}
