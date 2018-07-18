package eup_jtt808_server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

//import Configs.MasterServerConfig;
//import ErrorLog.ErrorHandler_MasterServer;
import Intl_Eup_Socket.ClientPackageObject;
import Intl_Eup_Socket.EupTCP_Server;
import Intl_Eup_Socket.IClientPackageEvent;
import Intl_Eup_Socket.ServerParam;
import Intl_Eup_Socket.EndStringProcess.EndByte_HeadandTail;
import eup_jtt808_server.interface1.OnCmdUpdateListener;
import eup_jtt808_server.interface1.OnConnectionClosedListener;
import eup_jtt808_server.interface1.OnConnectionNumberUpdateListener;
import eup_jtt808_server.interface1.OnDVRSendCmdListener;
//import Process.LastDVRInfo;
import eup_jtt808_server.process.ReaderProcess;
import eup_jtt808_server.command.JTT808Nouns;

public class EupJTT808Server implements IClientPackageEvent {
		
	private final Charset charset = StandardCharsets.ISO_8859_1;
	private final int dataBufferSize = 1024;
	private final EndByte_HeadandTail endByte = new EndByte_HeadandTail(JTT808Nouns.SPBYTE_HEAD, JTT808Nouns.SPBYTE_HEAD);
	private final int connectTimeout = 120000;
	private final int serverPort = 20000;
	private OnConnectionNumberUpdateListener onConnectionNumberUpdateListener = null;
	private OnCmdUpdateListener onCmdUpdateListener = null;
//	private Timer connectionNumberTimer = null;
	private ReaderProcess readerProcess = null;
	private EupTCP_Server server = null;

	public EupJTT808Server() {		
		readerProcess = new ReaderProcess(onConnectionClosedListener);
		startServer();
//		createConnectionNumberTimer();
	}

	private void startServer() {
		try {			
			ServerParam serverParam = new ServerParam();
			serverParam.setCharsets(charset);
			serverParam.setDataBufferSize(dataBufferSize);
			serverParam.setServerPort(serverPort);
			serverParam.setEndDataProcess(endByte);
			serverParam.setTimeout(connectTimeout);

			server = new EupTCP_Server(serverParam);
			server.addClinetPackageEvent(this);
			server.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
//			ErrorHandler_MasterServer.getInstance().handleErrorMsg(e, "StartServer發生錯誤。");
		}
	}

	@Override
	public void onConnectConnected(ClientPackageObject clientPackage, String ipInfo) {
		try {
			readerProcess.processConnected(clientPackage);
		} catch (Exception e) {
			System.out.println(e.getMessage());
//			ErrorHandler_MasterServer.getInstance().handleErrorMsg(e, "OnConnectConnected發生錯誤。");
		}
	}

	@Override
	public void onDataReceived(ClientPackageObject clientPackage, String data, byte[] bytes) {
		try {
			readerProcess.processData(clientPackage, data, bytes);
//			if (onCmdUpdateListener != null && clientPackage.getTag() != null) {
//				LastDVRInfo dvrInfo = (LastDVRInfo) clientPackage.getTag();
//				onCmdUpdateListener.updateByRecving(dvrInfo.getMac(), data);
//			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
//			String mac = null;
//			if (clientPackage.getTag() != null)
//				mac = ((LastDVRInfo) clientPackage.getTag()).getMac();
//			ErrorHandler_MasterServer.getInstance().handleErrorMsg(e, "OnDataReceived發生錯誤。Mac=" + mac + "，SourceData=" + data);
		}
	}

	@Override
	public void onConnectClosed(ClientPackageObject clientPackage) {
		try {
			readerProcess.processConnectClosed(clientPackage);
		} catch (Exception e) {
			System.out.println(e.getMessage());
//			String mac = null;
//			if (clientPackage.getTag() != null)
//				mac = ((LastDVRInfo) clientPackage.getTag()).getMac();
//			ErrorHandler_MasterServer.getInstance().handleErrorMsg(e, "OnConnectClosed發生錯誤。Mac=" + mac);
		}
	}

	public void setOnCmdUpdateListener(OnCmdUpdateListener onCmdUpdateListener) {
		this.onCmdUpdateListener = onCmdUpdateListener;
	}

	public void setOnConnectionNumberUpdateListener(OnConnectionNumberUpdateListener onConnectionNumberUpdateListener) {
		this.onConnectionNumberUpdateListener = onConnectionNumberUpdateListener;
	}

	public OnDVRSendCmdListener getOnDVRSendCmdListener() {
		return onDVRSendCmdListener;
	}

	private OnDVRSendCmdListener onDVRSendCmdListener = new OnDVRSendCmdListener() {

		@Override
		public void sendCmd(String mac, String cmd) throws Exception {
			UUID uuid = readerProcess.getConnectionUUIDByMac(mac);
			if (uuid != null)
				server.sendDataToClient(uuid, cmd + "★");
			else
				throw new Exception("DVR目前沒有在線上，Mac=" + mac + "，CMD=" + cmd);
			if (onCmdUpdateListener != null)
				onCmdUpdateListener.updateBySending(mac, cmd);
		}
	};

	private OnConnectionClosedListener onConnectionClosedListener = new OnConnectionClosedListener() {

		@Override
		public void closeConnection(UUID uuid) {
			try {
				server.closeOneClinet(uuid);
			} catch (Exception e) {
				System.out.println(e.getMessage());
//				ErrorHandler_MasterServer.getInstance().handleErrorMsg(e, "OnConnectionClosedListener.CloseConnection發生錯誤。");
			}
		}
	};

	public void dispose() {
		try {
			server.dispose();
		} catch (Exception e) {
			System.out.println(e.getMessage());
//			ErrorHandler_MasterServer.getInstance().handleErrorMsg(e, "Dispose發生錯誤。");
		}
	}
}
