package eup_jtt808_server.process;

import java.util.Date;

import org.codehaus.jettison.json.JSONObject;

//import Configs.MasterServerConfig;
import DB_Eup_StreamServer_Object.tb_dvr_command_log;
import DB_Factory_Operate.TwOperate_EupStreamServer_Factory;
//import ErrorLog.ErrorHandler_MasterServer;
//import Helpers.DVRNouns;
import eup_jtt808_server.interface1.IDataProcess;
import eup_jtt808_server.interface1.OnDVRSendCmdListener;
import Intl_Eup_Socket.ClientPackageObject;

public class SenderProcess implements IDataProcess {

	private OnDVRSendCmdListener onDVRSendCmdListener = null;

	public SenderProcess(OnDVRSendCmdListener onDVRSendCmdListener) {
		this.onDVRSendCmdListener = onDVRSendCmdListener;
	}

	@Override
	public void processConnected(ClientPackageObject clientPackageObject) throws Exception {
	}

	@Override
	public void processData(ClientPackageObject clientPackage, String data, byte[] bytes) throws Exception {
		String mac, dvrCmd, cmdId, username;
		try {
			JSONObject jsonObject = new JSONObject(data);
//			mac = jsonObject.getString(DVRNouns.sMAC);
//			dvrCmd = jsonObject.getString(DVRNouns.sDVR_CMD);
//			cmdId = jsonObject.getString(DVRNouns.sCMD_ID);
//			username = jsonObject.getString(DVRNouns.sUSERNAME);
//			sendResponseToServlet(clientPackage, buildResponseToServlet(true, cmdId));
		} catch (Exception e) {
			sendResponseToServlet(clientPackage, buildResponseToServlet(false, "Error."));
			throw e;
		}

		try {
//			if (onDVRSendCmdListener != null)
//				onDVRSendCmdListener.sendCmd(mac, dvrCmd);
//			recordSendLog(mac, username, dvrCmd, true);
		} catch (Exception e) {
//			recordSendLog(mac, username, dvrCmd, false);
			throw e;
		}
	}

	@Override
	public void processConnectClosed(ClientPackageObject clientPackageObject) throws Exception {
	}

	private String buildResponseToServlet(boolean isSuccess, String cmdId) throws Exception {
		try {
			JSONObject jsonObject = new JSONObject();
//			jsonObject.put(DVRNouns.sCMD, DVRNouns.sMASTER_DVR_COMMAND);
//			jsonObject.put(DVRNouns.sCMD_ID, cmdId);
//			jsonObject.put(DVRNouns.sSTATUS, isSuccess ? DVRNouns.sOK : DVRNouns.sFAIL);
			return jsonObject.toString();
		} catch (Exception e) {
			throw e;
//			throw ErrorHandler_MasterServer.getInstance().combineException(e, "BuildResponseToServlet發生錯誤");
		}
	}

	private void sendResponseToServlet(ClientPackageObject clientPackage, String data) throws Exception {
		try {
			String sendData = data + "★";
			clientPackage.writeData(sendData);
		} catch (Exception e) {
//			throw ErrorHandler_MasterServer.getInstance().combineException(e, "SendResponseToServlet發生錯誤");
		}
	}

	/**
	 * 紀錄DVRLog，含send
	 */
	private void recordSendLog(String mac, String userName, String cmd, boolean isSendSuccess) throws Exception {
		tb_dvr_command_log dvrLog = buildDVRLog(mac, userName, cmd, isSendSuccess);
		TwOperate_EupStreamServer_Factory.getInstance().getDvrCommandLog().insertDVRLogBySendCMD(dvrLog);
	}

	private tb_dvr_command_log buildDVRLog(String mac, String userName, String cmd, boolean isSendSuccess) throws Exception {
		JSONObject jsonObject = new JSONObject(cmd);
//		String cmdName = jsonObject.getString(DVRNouns.CMD);
//		String cmdId = jsonObject.getString(DVRNouns.CMD_ID);

		tb_dvr_command_log dvrLog = new tb_dvr_command_log();
		dvrLog.setMac(mac);
//		dvrLog.setCmd_id(cmdId);
//		dvrLog.setCmd_name(cmdName);
		dvrLog.setUsername(userName);
//		if (userName.equals(DVRNouns.aDVR))
//			dvrLog.setCmd_catalog(DVRNouns.aDVR_INT);
//		else if (userName.equals(DVRNouns.aSYSTEM))
//			dvrLog.setCmd_catalog(DVRNouns.aSYSTEM_INT);
//		else
//			dvrLog.setCmd_catalog(DVRNouns.aUSER_INT);
//		dvrLog.setIp(MasterServerConfig.getInstance().getIp());
//		dvrLog.setPort(MasterServerConfig.getInstance().getDvrPort());
		dvrLog.setCmd_send_time(new Date());
//		dvrLog.setCmd_send_status(isSendSuccess ? DVRNouns.tOK : DVRNouns.tFAIL);
		dvrLog.setCmd_send_string(cmd);
		return dvrLog;
	}
}
