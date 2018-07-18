package eup_jtt808_server.process;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jettison.json.JSONObject;

//import Commands.DVRCommand;
//import Commands.DVRCommandFactory;
//import Commands.InitReportCmd;
//import Configs.MasterServerConfig;
import DB_CTMS_Center_Object.EnumType.SyncSettings_SettingWayType;
import DB_Eup_StreamServer_Object.tb_dvr_command_log;
import DB_Factory_Operate.TwOperate_CTMS_Center_Factory;
import DB_Factory_Operate.TwOperate_EupStreamServer_Factory;
import DB_Factory_Operate.TwOperate_Eup_Log_Factory;
//import ErrorLog.ErrorHandler_MasterServer;
//import Helpers.DVRNouns;
//import Helpers.ServletCommunication;
import Intl_Eup_Socket.ClientPackageObject;
import Intl_Eup_SyncSettings.Eup_SyncSettingsController;
import Time_Component.Eup_DateTime;
import eup_jtt808_server.interface1.IDataProcess;
import eup_jtt808_server.interface1.OnConnectionClosedListener;
import eup_jtt808_server.command.*;

public class ReaderProcess implements IDataProcess {

	private final Object synced = new Object();
	private Eup_SyncSettingsController syncSettingsController;
//	private DvrSyncCmdBuilder syncCmdBuilder = new DvrSyncCmdBuilder();
//	private DVRCommandFactory dvrCommandFactory;
	private OnConnectionClosedListener onConnClosedListener;
	private Map<String, UUID> macToUUID = new ConcurrentHashMap<>();
	private Map<UUID, ClientPackageObject> clientsMap = new ConcurrentHashMap<>();
	private JTT808Message jtt808MessagePool = new JTT808Message();

	public ReaderProcess(OnConnectionClosedListener onConnClosedListener) {
		this.onConnClosedListener = onConnClosedListener;
//		syncSettingsController = new Eup_SyncSettingsController(DVRNouns.MASTER_SERVER_NAME, port, SyncSettings_SettingWayType.MASTER_SERVER,
//				ErrorHandler_MasterServer.getInstance(), TwOperate_CTMS_Center_Factory.getInstance(), TwOperate_Eup_Log_Factory.getInstance());
//		syncSettingsController.init(5, clientsMap, syncCmdBuilder, 2000, true);
//		dvrCommandFactory = new DVRCommandFactory(syncSettingsController);
	}

	@Override
	public void processConnected(ClientPackageObject clientPackage) throws Exception {
	}

	@Override
	public void processData(ClientPackageObject clientPackageObject, String data, byte[] bytes) throws Exception {
		jtt808MessagePool.ParseMessage(bytes);		
//		DVRCommand dvrCommand = dvrCommandFactory.createCommand(data);
//		if (dvrCommand instanceof InitReportCmd) {
//			try {
//				InitReportCmd initReportCmd = (InitReportCmd) dvrCommand;
//				initReportCmd.processRecivedCmd(clientPackageObject);
//				clientPackageObject.setTag(
//						new LastDVRInfo(initReportCmd.getMac(), initReportCmd.getUnicode(), initReportCmd.getCmdId(), initReportCmd.getCarSetType()));
//				clientsMap.put(clientPackageObject.getId(), clientPackageObject);
//				synchronized (synced) {
//					macToUUID.put(initReportCmd.getMac(), clientPackageObject.getId());
//				}
//				// 上線同步設定，此處理必須在macToUUID與clientsMap更新之後
//				checkInitSetting(clientPackageObject, initReportCmd.getMac(), initReportCmd.getUnicode(), initReportCmd.getCarSetType());
//				// 通知Servlet車機上線了，此處理必須在macToUUID與clientsMap更新之後
//				SendDvrOnlineDelay10s(initReportCmd.getMac());
//			} catch (Exception e) {
//				clientsMap.remove(clientPackageObject.getId());
//				onConnClosedListener.closeConnection(clientPackageObject.getId());
//				throw e;
//			}
//		} else {
//			// 第一筆不是InitReport
//			if (clientPackageObject.getTag() == null) {
//				clientsMap.remove(clientPackageObject.getId());
//				onConnClosedListener.closeConnection(clientPackageObject.getId());
//				return;
//			}
//			dvrCommand.processRecivedCmd(clientPackageObject);
//		}
	}

	// 尋找未轉到正版的server
	@SuppressWarnings("unused")
	private void forTestServer(String mac) {
		try {
			PrintStream printStream = new PrintStream(new FileOutputStream("./MacOnline.txt", true));
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("date_time", new Eup_DateTime().getDateTimeString());
			jsonObject.put("mac", mac);
			printStream.println(jsonObject.toString());
			printStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkInitSetting(ClientPackageObject clientPackageObject, String mac, String unciode, String carSetType) {
		try {
//			if (!MasterServerConfig.getInstance().isCheckInitSetting())
//				return;

			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(10 * 1000);
						syncSettingsController.checkInitSetting(clientPackageObject, unciode, carSetType);
					} catch (Exception e) {
//						ErrorHandler_MasterServer.getInstance().handleErrorMsg(e, "checkInitSetting發生錯誤，Mac=" + mac);
					}
				}
			});
			thread.setDaemon(true);
			thread.start();
		} catch (Exception e) {
//			ErrorHandler_MasterServer.getInstance().combineException(e, "checkInitSetting發生錯誤");
		}
	}

	private void SendDvrOnlineDelay10s(String mac) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(10 * 1000);
//					ServletCommunication servletCommunication = new ServletCommunication();
//					servletCommunication.noticeServlet(mac, DVRNouns.sDVR_ONLINE);
				} catch (Exception e) {
//					ErrorHandler_MasterServer.getInstance().handleErrorMsg(e, "SendDvrOnlineDelay10s發生錯誤，Mac=" + mac);
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void processConnectClosed(ClientPackageObject clientPackageObject) throws Exception {
		clientsMap.remove(clientPackageObject.getId());
		if (clientPackageObject.getTag() == null)
			return;

//		LastDVRInfo dvrInfo = (LastDVRInfo) clientPackageObject.getTag();
//		String mac = dvrInfo.getMac();
//		String initCmdId = dvrInfo.getInitCmdId();

//		int success = TwOperate_EupStreamServer_Factory.getInstance().getDvrStatus().updateStatusWhenDVROfflineByMac(mac,
//				MasterServerConfig.getInstance().getMac(), clientPackageObject.getId().toString());
//		if (success > 0) {
//			synchronized (synced) {
//				if (macToUUID.containsKey(mac) && macToUUID.get(mac).equals(clientPackageObject.getId()))
//					macToUUID.remove(mac);
//			}
//			// 通知servlet清空live和playback的狀態
//			ServletCommunication servletCommunication = new ServletCommunication();
//			servletCommunication.noticeServlet(mac, DVRNouns.sCLEAR_DVR_LIVE_STATUS);
//			servletCommunication.noticeServlet(mac, DVRNouns.sCLEAR_DVR_PLAYBACK_STATUS);
//		}
//		TwOperate_EupStreamServer_Factory.getInstance().getDvrCommandLog().insertDVRLog(buildOfflineLogCmd(mac, initCmdId));
	}

	private tb_dvr_command_log buildOfflineLogCmd(String mac, String initCmdId) throws Exception {
		JSONObject sendCmd = new JSONObject();
//		sendCmd.put(DVRNouns.CMD, DVRNouns.DVR_OFFLINE);
//		sendCmd.put(DVRNouns.CMD_ID, initCmdId);
//
//		JSONObject recvCmd = new JSONObject();
//		recvCmd.put(DVRNouns.CMD, DVRNouns.DVR_OFFLINE);
//		recvCmd.put(DVRNouns.CMD_ID, initCmdId);
//		recvCmd.put(DVRNouns.STATUS, DVRNouns.OK);

		tb_dvr_command_log dvrLog = new tb_dvr_command_log();
		dvrLog.setMac(mac);
		dvrLog.setCmd_id(initCmdId);
//		dvrLog.setCmd_name(DVRNouns.DVR_OFFLINE);
//		dvrLog.setIp(MasterServerConfig.getInstance().getIp());
//		dvrLog.setPort(MasterServerConfig.getInstance().getDvrPort());
//		dvrLog.setCmd_catalog(DVRNouns.aSYSTEM_INT);
//		dvrLog.setCmd_send_time(new Date());
//		dvrLog.setCmd_send_status(DVRNouns.tOK);
//		dvrLog.setCmd_send_string(sendCmd.toString());
//		dvrLog.setCmd_recv_time(new Date());
//		dvrLog.setCmd_recv_status(DVRNouns.tOK);
//		dvrLog.setCmd_recv_string(recvCmd.toString());
		return dvrLog;
	}

	public UUID getConnectionUUIDByMac(String mac) {
		return macToUUID.get(mac);
	}
}
