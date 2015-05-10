package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Listener {
	private static Logger LOG = Logger.getLogger(Listener.class);
	private ServerSocket serverSocket;
	
	private List<Server> servers = new ArrayList<Server>();
	
	public Listener (int port) throws IOException {
		if (port < 0 || port > 65535) {
    		LOG.error("Invalid port value: " + port);
    		throw new IllegalArgumentException("Invalid port value: " + port);
    	}
		
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException ex) {
			LOG.error(ex);
			throw ex;
		}	
	}
	
	public void start() {
		Thread thr = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Socket clientSocket = serverSocket.accept();
						
						final Server server = new Server(clientSocket);
						servers.add(server);
						new Thread(new Runnable() {
							@Override
							public void run() {
								server.start();
								servers.remove(server);
							}
						}).start();
					} catch (IOException ex) {
						LOG.error(ex);
					}
				}
			}
		});
		thr.setDaemon(true);
		thr.start();
	}
}
