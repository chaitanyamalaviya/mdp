import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;

public class UDP{
    public static final InetSocketAddress RES_PI_ADDR = new InetSocketAddress("192.168.10.1",5143);
    
	public static byte[] in_buf, out_buf;
	public static DatagramSocket clientSocket;
	private static InetSocketAddress targetAddr;

    //	RobotInstructionParser parser;
	
	public static void buildSocket() {
        
		try {
			clientSocket = new DatagramSocket();
			targetAddr = RES_PI_ADDR;
			in_buf = new byte[1024];
			out_buf = new byte[1024];
            //	parser = new RobotInstructionParser();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


    
	protected static void send(String str) {
		out_buf = str.getBytes();
		try {
			DatagramPacket packet = new DatagramPacket(out_buf, out_buf.length, targetAddr);
			clientSocket.send(packet);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    
	protected static String receive() throws SocketException{
		clientSocket.setSoTimeout(20000);
		System.out.println("just enter receive function");
		DatagramPacket packet = new DatagramPacket(in_buf, in_buf.length);
		System.out.println("get the packet"+in_buf+" "+in_buf.length);
		String result;
		while (true){
			try {
				System.out.println("before the socket recieve packet");
				clientSocket.receive(packet);
				System.out.println("actually received the packet, before return the packet");
				result = new String(packet.getData(), packet.getOffset(), packet.getLength());
				Arrays.fill(in_buf,(byte)0);
				break;
			} catch (SocketTimeoutException|SocketException e) {
				System.out.println("socketTimeouthandled here");
				System.out.println("socketTimeouthandled here");
				System.out.println("socketTimeouthandled here");
				System.out.println("socketTimeouthandled here");
				System.out.println("socketTimeouthandled here");
				UDP.send("0000/");//to resend the sensor info again.
				// TODO Auto-generated catch\ block
				//e.printStackTrace();
			}
			catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			}
		}
		return result;
		//return null;
	}
//	protected static String receive2() throws SocketException{
//		clientSocket.setSoTimeout(4000);
//		System.out.println("just enter receive function");
//		DatagramPacket packet = new DatagramPacket(in_buf, in_buf.length);
//		System.out.println("get the packet"+in_buf+" "+in_buf.length);
//		String result;
//		while (true){
//			try {
//				System.out.println("before the socket recieve packet");
//				clientSocket.receive(packet);
//				System.out.println("actually received the packet, before return the packet");
//				result = new String(packet.getData(), packet.getOffset(), packet.getLength());
//				Arrays.fill(in_buf,(byte)0);
//				break;
//			} catch (SocketTimeoutException|SocketException e) {
//				System.out.println("socketTimeouthandled here");
//				System.out.println("socketTimeouthandled here");
//				System.out.println("socketTimeouthandled here");
//				System.out.println("socketTimeouthandled here");
//				System.out.println("socketTimeouthandled here");
//				UDP.send("where/");//to resend the sensor info again.
//				// TODO Auto-generated catch\ block
//				//e.printStackTrace();
//			}
//			catch (IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			}
//		}
//		return result;
//		//return null;
//	}
    
//    public static void main(String[] args){
//    	System.out.println ("Start\n");
//        Scanner sc = new Scanner(System.in);
//        String input;
//        int choice;
//        buildSocket();
//        //for(int i = 0; i <20; i++);
//        send("ready");
//        //send("ready");
//        while (true){
//            System.out.println("send(1) or receive(0): ");
//            choice = sc.nextInt();
//            if(choice>0){
//            System.out.println("Say Something: ");
//            input = sc.next();
//            send(input);}
//            else System.out.println (receive());
//            //send ("sent from pc\n");
//        }
//    }
}