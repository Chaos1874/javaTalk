package objtalk;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.text.StyledDocument;

/*
 * 该消息类分为两种
 * 一种是实际消息内容
 * 还有一种是当前群聊的成员信息
 * */

public class MyMessage implements Serializable {// 序列化&&反序列化（用于被传输的对象）
	public static final long serialVersionUID = 1l;
	public static final int MES_TYPE_PLAIN = 1;//文本消息
	public static final int MES_TYPE_UPDATE_CLIENTLIST = 2;//更新用户列表消息

	private StyledDocument content ;//消息主题（例如图片，文字）
	private ArrayList<String> clientList;//当前群聊成员信息
	private int mesType = -1;
	private boolean ifmass=true;//判断是群聊消息还是私聊信息
	private String ip="null";//私发message对象ip
	private String usename = "null";

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public MyMessage(Boolean ifmass,int mesType) {
		this.ifmass=ifmass;
		this.mesType = mesType;
	}

	public int getMesType() {
		return mesType;
	}

	public void setMesType(int mesType) {
		this.mesType = mesType;
	}

	public StyledDocument getContent() {
		return content;
	}

	public void setContent(StyledDocument content) {
		this.content = content;
	}

	public ArrayList<String> getClientList() {
		return clientList;
	}

	public void setClientList(ArrayList<String> clientList) {
		this.clientList = clientList;
	}

	public boolean getisIfmass() {
		return ifmass;
	}

	public void setIfmass(boolean ifmass) {
		this.ifmass = ifmass;
	}
}
