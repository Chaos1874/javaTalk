package objtalk;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.text.StyledDocument;

/*
 * ����Ϣ���Ϊ����
 * һ����ʵ����Ϣ����
 * ����һ���ǵ�ǰȺ�ĵĳ�Ա��Ϣ
 * */

public class MyMessage implements Serializable {// ���л�&&�����л������ڱ�����Ķ���
	public static final long serialVersionUID = 1l;
	public static final int MES_TYPE_PLAIN = 1;//�ı���Ϣ
	public static final int MES_TYPE_UPDATE_CLIENTLIST = 2;//�����û��б���Ϣ

	private StyledDocument content ;//��Ϣ���⣨����ͼƬ�����֣�
	private ArrayList<String> clientList;//��ǰȺ�ĳ�Ա��Ϣ
	private int mesType = -1;
	private boolean ifmass=true;//�ж���Ⱥ����Ϣ����˽����Ϣ
	private String ip="null";//˽��message����ip
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
