package eup_jtt808_server.interface1;

public interface OnCmdUpdateListener {
	public void updateBySending(String mac, String cmd);

	public void updateByRecving(String mac, String cmd);
}
