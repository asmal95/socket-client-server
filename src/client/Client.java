package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.log4j.Logger;
/**
 * ƒанный клиент организует соединение с сервером и обмен
 * с ним сообщений. ќбмен сообщений происходит по принципу запрос-ответ.
 * ѕри каждом новом запросе устанавливаетс¤ новое соединение. ¬ данном случае это кажетс¤ оптимальным, так как
 * запрос выполн¤етс¤ с одной конкретной задачей - обработать данные и получить ответ.
 * @author Andrew
 *
 */
public class Client {
	private static Logger LOG = Logger.getLogger(Client.class);
	private int port;
	private InetAddress address;
    public Client(String host, int port) {
    	if (port < 0 || port > 65535) {
    		LOG.debug("Invalid port value: " + port);
    		throw new IllegalArgumentException("Invalid port value: " + port);
    	}
    	this.port = port;
    	try {
			address = InetAddress.getByName(host);
		} catch (UnknownHostException ex) {
			LOG.debug("Invalid host name", ex);
			throw new IllegalArgumentException(ex);
		}
    }

    public Object sendRequest(Object req, int timeout) throws ClientRequestException {
    	long startTime = new Date().getTime();
    	try (
    			Socket connection = new Socket(address, port);	
    			ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
    			ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
    			) {
    		output.writeObject(req);
        	while (new Date().getTime() < startTime + timeout) {
        		Object inputObject;
				if ((inputObject = input.readObject()) != null) {
        			return inputObject;
        		}
        	}
    	} catch (IOException ex) {
			LOG.debug("IOException", ex);
			throw new ClientRequestException(ex);
		} catch (ClassNotFoundException ex) {
			LOG.debug("ClassNotFoundException", ex);
			throw new ClientRequestException(ex);
		}
    	throw new ClientRequestException("Timeout limited");
    }
    public Object sendRequest(Object req) throws ClientRequestException {
    	return sendRequest(req, 3000);
    }
}
