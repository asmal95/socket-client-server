package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;


public class Server {
	private static Logger LOG = Logger.getLogger(Server.class);
	private Socket socket;
    
	public Server(Socket socket) {
		this.socket = socket;
	}
	
	public void start() {
        try (
        		ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        		ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        	) {
        	    String request = (String) input.readObject();
        	    //обработка данных. ѕросто заменим все 'a' на 'A'
    	    	request = request.replaceAll("a", "A");
    	    	output.writeObject(request);
        } catch (ClassNotFoundException ex) {
        	LOG.error(ex);
        } catch (IOException ex) {
        	LOG.error(ex);
        }
	}
}
