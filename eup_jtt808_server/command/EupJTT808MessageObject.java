package eup_jtt808_server.command;

public class EupJTT808MessageObject {
	private int messageID;	
	private int messageBodyAttrVersion;
	private boolean messageBodyAttrIsSplitPack;
	private int messageBodyAttrEncryptMethod;  // 0: unencrypted, 1: RSA encrypted
	private int messageBodyAttrBodyLength;
	private int protocolVersion;	
	private String phoneNumberString;
	private int messageSerial;
	private int messagePackNumber;
	private int messagePackSerial;
	private byte[] messageBodyBytes;
	
	public void DumpConsole () {
		System.out.printf("message id        : %d\n", messageID);
		System.out.printf("body attr version : %d\n", messageBodyAttrVersion);
		System.out.printf("is split          : %b\n", messageBodyAttrIsSplitPack);
		System.out.printf("body length       : %d\n", messageBodyAttrBodyLength);
		System.out.printf("encrypt           : %d\n", messageBodyAttrEncryptMethod);		
		System.out.printf("protocol version  : %d\n", protocolVersion);
		System.out.printf("phone number      : %s\n", phoneNumberString);
		System.out.printf("message serial    : %d\n", messageSerial);
		System.out.printf("pack number       : %d\n", messagePackNumber);
		System.out.printf("pack serial       : %d\n", messagePackSerial);
	}
	
	public void SetMessageID (int ID) {
		this.messageID = ID;
	}
	
	public void SetMessageBodyAttrVersion (int version) {
		this.messageBodyAttrVersion = version;
	}
	
	public void SetMessageBodyAttrIsSplitPack (boolean isSplitPack) {
		this.messageBodyAttrIsSplitPack = isSplitPack;
	}
	
	public void SetMessageBodyAttrEncryptMethod (int method) {
		this.messageBodyAttrEncryptMethod = method;
	}
	
	public int GetMessageBodyLength () {
		return this.messageBodyAttrBodyLength;		
	}
	
	public void SetMessageBodyAttrBodyLength (int length) {
		this.messageBodyAttrBodyLength = length;
	}
	
	public void SetProcotolVersion (int version) {
		this.protocolVersion = version;
	}
	
	public void SetPhoneNumberBytes (String phoneNumberString) {
		this.phoneNumberString = phoneNumberString;		
	}
	
	public void SetMessageSerial (int serial) {
		this.messageSerial = serial;
	}
	
	public void SetMessagePackNumber (int packNumber) {
		this.messagePackNumber = packNumber;
	}
	
	public void SetMessagePackSerial (int serial) {
		this.messagePackSerial = serial;
	}
	
	public void SetMessageBody (byte[] messageBody) {
		this.messageBodyBytes = messageBody.clone();		
	}
}
