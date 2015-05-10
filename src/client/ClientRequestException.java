package client;

public class ClientRequestException extends Exception {
	
	public ClientRequestException(String message) {
		super(message);
	}
	public ClientRequestException(String message, Throwable cause) {
		super(message, cause);
	}
	public ClientRequestException(Throwable cause) {
		super(cause);
	}
	public ClientRequestException() {  }
}
