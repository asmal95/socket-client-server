package application;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import client.Client;
import client.ClientRequestException;

/**
 * Главное окно клиентского приложения. Тут осуществляется выбор файлов, чтение из файлов, отправка и запись ответа.
 * Для отправки файла использует класс Client.
 * @author Andrew
 *
 */
public class ClientFrame extends JFrame {
	private static Logger LOG = Logger.getLogger(ClientFrame.class);
	private Client client = new Client("localhost", 645);
	
	private JFileChooser openDialog = new JFileChooser();
	private JFileChooser saveDialog = new JFileChooser();
	private JButton openButton = new JButton("Input file");
	private JButton saveButton = new JButton("Output file");
	private JButton sendButton = new JButton("Send");
	private File inputFile;
	private File outputFile;
	private StatusPanel statusPanel = new StatusPanel();
	
    
	public ClientFrame() {
		super("Клиент");
		
	    setLayout(new FlowLayout());
	    setSize(300, 160);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    setLocationRelativeTo(null);
	    
	    init();
	    updateInfo();
	}
	//Инициализирует обработчики событий кнопок
	private void init() {
		//выбор отправляемого файла
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openDialog.showOpenDialog(ClientFrame.this);
				inputFile = openDialog.getSelectedFile();
				updateInfo();
			}
		});
		//выбор файла, в который будет созранен результат
	    saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDialog.showOpenDialog(ClientFrame.this);
				outputFile = saveDialog.getSelectedFile();
				updateInfo();
			}
		});
	    //инициализирует отправку файла на сервер
	    sendButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	if (inputFile == null || outputFile == null) {
	        		updateInfo();
	        		return;
	        	}
	        	new Thread(new Runnable() {
					public void run() {
						sendFile();
					}
				}).start();
	        }
	    });
	    
	    add(openButton);
	    add(sendButton);
	    add(saveButton);
	    add(statusPanel);
	}
	//выполняет проверку, выбраны ли файлы. Выводит информацию в статус-панель, если не выбранны файлы
	private void updateInfo() {
		statusPanel.setInput(inputFile != null?inputFile.getName():"File no select");
		statusPanel.setOutput(outputFile != null?outputFile.getName():"File no select");
		if (inputFile == null && outputFile == null) {
			statusPanel.setMessage("Please, select all files");
    	} else if (inputFile == null) {
    		statusPanel.setMessage("Please, select input file");
    	} else if (outputFile == null) {
    		statusPanel.setMessage("Please, select output file");
    	} else {
    		statusPanel.setMessage("");
    	}
		
	}
	//выполняет саму отправку файла, запис результата в выходной файл.
	private void sendFile() {
		try (
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
				BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		) {
			StringBuilder builder = new StringBuilder();
			String buff;
			while ((buff = reader.readLine()) != null) {
				builder.append(buff);
				builder.append(System.lineSeparator());
			}
			String resp = (String) client.sendRequest(builder.toString());
			writer.write(resp);
			writer.flush();
			statusPanel.setMessage("OK");
		} catch (IOException ex) {
			LOG.error(ex);
			statusPanel.setMessage(ex.getCause().toString());
		} catch (ClientRequestException ex) {
			LOG.error(ex);
			statusPanel.setMessage(ex.getCause().toString());
		}
	}
	private class StatusPanel extends JPanel {
		private JLabel inText = new JLabel("Input: ");
		private JLabel outText = new JLabel("Output: ");
		private JLabel messText = new JLabel("Message: ");

		private JLabel inValue = new JLabel(".");
		private JLabel outValue = new JLabel(".");
		private JLabel messValue = new JLabel(".");
		
		public StatusPanel() {
			this.setSize(280, 100);
			setLayout(new GridLayout(3, 2));
			add(inText);
			add(inValue);
			add(outText);
			add(outValue);
			add(messText);
			add(messValue);
		}
		public void setInput(String in) {
			inValue.setText(in);
		}
		public void setOutput(String out) {
			outValue.setText(out);
		}
		public void setMessage(String mess) {
			messValue.setText(mess);
		}
	}
}
