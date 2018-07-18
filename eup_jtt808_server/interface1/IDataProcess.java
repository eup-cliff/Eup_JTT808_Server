package eup_jtt808_server.interface1;

import Intl_Eup_Socket.ClientPackageObject;

public interface IDataProcess {

	public void processConnected(ClientPackageObject clientPackageObject) throws Exception;

	public void processConnectClosed(ClientPackageObject clientPackageObject) throws Exception;

	public void processData(ClientPackageObject clientPackage, String data, byte[] bytes) throws Exception;

}
