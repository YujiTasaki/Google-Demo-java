package jp.co.kke.Lockstatedemo.bean.lock;

public class LockResAccessPersonsInfo {
	LockResAccessPersonsDataInfo data;

	public LockResAccessPersonsDataInfo getData() {
		return data;
	}

	public void setData(LockResAccessPersonsDataInfo data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LockResAccessPersonsInfo [data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}



}
