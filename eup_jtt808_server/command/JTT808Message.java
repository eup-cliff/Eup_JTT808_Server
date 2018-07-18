package eup_jtt808_server.command;

import java.util.LinkedList;
import eup_jtt808_server.command.JTT808Nouns;
import eup_jtt808_server.command.EupJTT808MessageObject;

public class JTT808Message {
	private static final int MINIMUM_JTT808_MESSAGE_LEN = 10;
	
	private LinkedList<EupJTT808MessageObject> messageList = new LinkedList<EupJTT808MessageObject>();

	public boolean ParseMessage (byte[] byteMessage) throws Exception
	{
		try {
			byte[] extracted_message = ExtractMessage(byteMessage);
			if (extracted_message == null) {
				return false;
			}
			EupJTT808MessageObject obj = ObjectFromExtractedMessage(extracted_message);
			messageList.addLast(obj);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public EupJTT808MessageObject PopMessage () {		
		return messageList.pop();		
	}	
	
	public EupJTT808MessageObject ObjectFromExtractedMessage (byte[] data) throws Exception
	{				
		EupJTT808MessageObject obj = new EupJTT808MessageObject();		
		obj.SetMessageID(IntegerFromWord(data[0], data[1]));
		int message_body_attr = IntegerFromWord(data[2], data[3]);		
		// Message Body Attributes
		obj.SetMessageBodyAttrBodyLength(message_body_attr & 0x01FF);
		obj.SetMessageBodyAttrEncryptMethod((message_body_attr >> 10) & 0x0007);		
		boolean is_split_pack = ((message_body_attr >> 13) & 0x0001) == 1 ? true : false;
		obj.SetMessageBodyAttrIsSplitPack(is_split_pack);
		obj.SetMessageBodyAttrVersion((message_body_attr >> 14) & 0x0001);
		//
		obj.SetProcotolVersion(data[4]);
		// Phone Number
		byte[] phone_number_bytes = new byte[JTT808Nouns.PHONE_NUMBER_LENGTH];
		System.arraycopy(data, 5, phone_number_bytes, 0, JTT808Nouns.PHONE_NUMBER_LENGTH);
		obj.SetPhoneNumberBytes(StringFromBCD5(phone_number_bytes));
		// 
		obj.SetMessageSerial(IntegerFromWord(data[10], data[11]));
		int message_head_len = 0;		
		if (is_split_pack) {
			message_head_len = 12;
			obj.SetMessagePackNumber(IntegerFromWord(data[12], data[13]));
			obj.SetMessagePackSerial(IntegerFromWord(data[14], data[15]));			
		} else {
			message_head_len = 16;			
		}
		int message_body_len = obj.GetMessageBodyLength();
		if ((message_head_len + message_body_len) != data.length) {
			Exception ne = new Exception(String.format("JTT808 Message body length wrong. head_len=%d, body_len=%d, data_len=%d", message_head_len, message_body_len, data.length));
			throw ne;
		}		
		byte[] message_body = new byte[message_body_len];
		System.arraycopy(data, message_head_len, message_body, 0, message_body_len);
		obj.SetMessageBody(message_body);
		return obj;
	}
	
	public static byte[] CreateMessage (byte[] raw_byte_list)
	{
		// Usage: create JTT808 message (apply escaped character)
		
		try {
			int len = 0;
			byte[] tmp_byte_list = new byte[raw_byte_list.length * 2];
			for (int i = 0; i < raw_byte_list.length; i++) {
				byte b = raw_byte_list[i];
				if (b == JTT808Nouns.SPBYTE_HEAD) {
					tmp_byte_list[len++] = JTT808Nouns.ESBYTE_HEAD;
					tmp_byte_list[len++] = JTT808Nouns.SPBYTE_TAIL;
				} else if (b == JTT808Nouns.ESBYTE_HEAD) {
					tmp_byte_list[len++] = b;
					tmp_byte_list[len++] = JTT808Nouns.ESBYTE_TAIL;
				} else {
					tmp_byte_list[len++] = b;
				}
			}
			byte[] output_byte_list = new byte[len];
			System.arraycopy(tmp_byte_list, 0, output_byte_list, 0, len);
			return output_byte_list;
		} catch (Exception e) {
			throw e;
		}
	}	
	
	public static byte[] ExtractMessage (byte[] input_byte_list) throws Exception
	{
		// input JTT808 message format: 
		//
		// [7E] [Escaped(MessageHead)] [Escaped(MessageBody)] [Check] [7E]
		//		
		// output: 
		//
		// [MessageHead] [MessageBody]
		//
		// Usage: extract JTT808 message 
		// 
		// 1. remove head identification byte
		// 2. replace escaped character  
		// 3. check the checksum
		// 4. remove tail identification byte
		
		try {
			if (input_byte_list.length < MINIMUM_JTT808_MESSAGE_LEN) {
				Exception ne = new Exception("JTT808 Message too short");
				throw ne;
			}
			if (input_byte_list[0] != JTT808Nouns.SPBYTE_HEAD) {
				Exception ne = new Exception("JTT808 Message should start with identification byte");
				throw ne;
			}
			if (input_byte_list[input_byte_list.length - 1] != JTT808Nouns.SPBYTE_HEAD) {
				Exception ne = new Exception("JTT808 Message should end with identification byte");				
				throw ne;
			}
			int len = 0;
			boolean is_format_error = false;			
			byte[] tmp_byte_list = new byte[input_byte_list.length];
			for (int i = 1; i < input_byte_list.length - 2; i++) {
				byte b = input_byte_list[i];
				byte c = input_byte_list[i + 1];
				if (b == JTT808Nouns.ESBYTE_HEAD) {
					if (c == JTT808Nouns.SPBYTE_TAIL) {
						tmp_byte_list[len++] = JTT808Nouns.SPBYTE_HEAD;
					} else if (c == JTT808Nouns.ESBYTE_TAIL) {
						tmp_byte_list[len++] = JTT808Nouns.ESBYTE_HEAD;
					} else {
						Exception ne = new Exception(String.format("JTT808 Message found invalid escaped rule. (0x%02x, 0x%02x)", b, c));				
						throw ne;
					}
					i++;
				} else {
					tmp_byte_list[len++] = b;
				}
			}
			if (is_format_error) {
				return null;
			}
			byte[] output_byte_list = new byte[len];
			System.arraycopy(tmp_byte_list, 0, output_byte_list, 0, len);
			byte checksum = 0x00; 
			for (int i = 0; i < output_byte_list.length; i++) {
				checksum ^= output_byte_list[i];
			}
			byte input_checksum = input_byte_list[input_byte_list.length - 2];
			if (checksum != input_checksum) {
				Exception ne = new Exception(String.format("JTT808 Message checksum wrong. (0x%02x, 0x%02x)", input_checksum, checksum));				
				throw ne;
			}
			return output_byte_list;
		} catch (Exception e) {
			throw e;
		}				
	}
	
	private int IntegerFromWord (byte b1, byte b2) {
		return (((b1 & 0xFF) << 8) + (b2 & 0xFF));
	}
	
	private String StringFromBCD5 (byte[] bcd_bytes) {
		String str = "";
		for (int i = 0; i < bcd_bytes.length; i++) {
			Byte b = bcd_bytes[i];
			str += String.format("%d%d", (b >> 4), (b & 0xF));
		}
		return str;
	}
}
