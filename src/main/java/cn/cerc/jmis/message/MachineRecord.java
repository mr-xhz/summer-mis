package cn.cerc.jmis.message;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.jbean.client.LocalService;
import cn.cerc.jbean.core.DataValidateException;
import cn.cerc.jdb.core.DataSet;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.jiguang.ClientType;

/**
 * 获取用户的设备信息
 */
public class MachineRecord {
	private String corpNo;
	private String userCode;
	Map<ClientType, String> items = new HashMap<>();

	public MachineRecord(IHandle handle, String corpNo, String userCode) throws DataValidateException {
		this.corpNo = corpNo;
		this.userCode = userCode;
		this.init(handle);
	}

	private void init(IHandle handle) throws DataValidateException {
		LocalService srv = new LocalService(handle, "SvrUserLogin.getMachInfo");
		if (!srv.exec("CorpNo_", corpNo, "UserCode_", userCode)) {
			throw new RuntimeException(srv.getMessage());
		}
		DataSet dataOut = srv.getDataOut();
		DataValidateException.stopRun(String.format("帐号 %s 还未进行认证，无法发送消息", userCode), dataOut.eof());

		while (dataOut.fetch()) {
			int machineType = dataOut.getInt("MachineType_");
			String machineCode = dataOut.getString("MachineCode_");

			switch (machineType) {
			case 6:
				items.put(ClientType.IOS, machineCode);
				break;
			case 7:
				items.put(ClientType.Android, machineCode);
				break;
			default:
				throw new RuntimeException("不支持的设备类型 " + machineType);
			}
		}
	}

	public Map<ClientType, String> getItems() {
		return items;
	}

	public String getCorpNo() {
		return corpNo;
	}

	public void setCorpNo(String corpNo) {
		this.corpNo = corpNo;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

}
