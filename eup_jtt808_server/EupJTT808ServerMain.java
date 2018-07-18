package eup_jtt808_server;

import eup_jtt808_server.command.*;

public class EupJTT808ServerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		EupJTT808Server jtt808_server = new EupJTT808Server();
//		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//			public void run() {				
//				jtt808_server.dispose();
//			}
//		}, "Shutdown-thread"));
		TestJTT808Message();
		//TestByteOperation();
	}

	public static void TestJTT808Message () {
		JTT808Message m = new JTT808Message();
		byte[] heartbeat_byte_message = {
				0x7E, 0x00, 0x02, 0x00, 0x00, 0x01, 0x33, 0x00, 0x31, 0x27, 0x07, 0x1B, (byte)0xFF, (byte)0xC5, 0x7E				
				};
		byte[] dev_register_byte_message = {
				0x7E, 0x01, 0x00, 0x00, 0x2D, 0x01, 0x33, 0x00, 0x31, 0x27, 0x07, 0x00, 0x0A, 0x00, 0x2C, 0x01, 
				0x2F, 0x37, 0x30, 0x31, 0x31, 0x31, 0x42, 0x53, 0x4A, 0x2D, 0x41, 0x36, 0x2D, 0x42, 0x44, 0x00, 
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30, 0x33, 0x31, 0x32, 0x37, 0x30,				
				0x37, 0x01, (byte)0xD4, (byte)0xC1, 0x42, 0x38, 0x38, 0x38, 0x38, 0x38, 0x45, 0x7E
			 	};
		try {
			//m.ParseMessage(heartbeat_byte_message);
			m.ParseMessage(dev_register_byte_message);
			EupJTT808MessageObject obj = m.PopMessage();
			if (obj != null) {
				obj.DumpConsole();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void TestByteOperation () {
		byte b1 = 0x00;
		byte b2 = (byte)0xFF;
		b1 ^= (b2 & 0xFF);
		System.out.printf("%d\n", b1);		
	}
}
