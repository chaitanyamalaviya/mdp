package mdp.rpi.communicator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class RaspberryController {
	private static final int PORT = 5143;
	private static final String IP_ADDRESS = "192.168.2.2";
	
	private Socket socket;
	private String syntax;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
		
	public RaspberryController() {		
		try {
			socket = new Socket(IP_ADDRESS, PORT);
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataInputStream = new DataInputStream(socket.getInputStream());			
		} catch (Exception e) {
			
		}
	}
	
	public void sendMessage(String message) {
		try {
			dataOutputStream.writeUTF(message);
	     }
	     catch(Exception e) {
	        System.out.print("Whoops! It didn't work!\n");
	     }
	}
	
	public String listen() {
		try {
			syntax = dataInputStream.readUTF();
		} catch (Exception e) {
			syntax = e.toString();
		}
		return syntax;
	}
	
	public void disconnect() {
		try {
			if (dataOutputStream != null) {
				dataOutputStream.close();
			}
			if (dataInputStream != null) {
				dataInputStream.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {
			
		}
	}
}