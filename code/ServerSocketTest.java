package objtalk;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.omg.CORBA.SystemException;


public class ServerSocketTest {
	ServerSocket server;
	HashSet<Socket> clientSet = new HashSet<>();

	public ServerSocketTest() {
		try {
			server = new ServerSocket(12345);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void work() {
		int no = 0;//���ӷ������ĸ���
		try {
			while (true) {
				Socket client = server.accept();
				clientSet.add(client);
				SendUpdateClientList();
				no++;
				new ClientThread(client, no).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void SendUpdateClientList() {//�����û��������Ϣ���û��˳��ͼ���ļ���
			MyMessage mes = new MyMessage(true,MyMessage.MES_TYPE_UPDATE_CLIENTLIST);
			mes.setClientList(getClientList());
			massMes(mes);
	}

	public void massMes(MyMessage mes) {//Ⱥ����Ϣ
		Iterator<Socket> it = clientSet.iterator();
		while (it.hasNext()) {
			sendMes(it.next(), mes);
		}
	}
	
	public void singleMes(MyMessage mes){//������Ϣ
		for(Socket s : clientSet){
			if(s.getRemoteSocketAddress().toString().equals(mes.getIp())){//String�еȱ�����equals
				sendMes(s, mes);
				break;
			}
		}
	}

	public void sendMes(Socket s, MyMessage mes) {
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(mes);
			out.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public ArrayList<String> getClientList() {
		ArrayList<String> list = null;
		if (clientSet.size() > 0) {
			list = new ArrayList<String>();
			Iterator<Socket> it = clientSet.iterator();
			int index = 0;
			while (it.hasNext()) {
				list.add(it.next().getRemoteSocketAddress().toString());
			}
		}
		return list;
	}

	class ClientThread extends Thread {
		Socket c;
		int no;

		public ClientThread(Socket c, int no) {
			super();
			this.c = c;
			this.no = no;
		}

		@Override
		public void run() {
			try (ObjectInputStream in = new ObjectInputStream((c.getInputStream()));) {
				MyMessage newMes = (MyMessage) in.readObject();
				while (newMes.getContent()!=null) {//���Ͻ��շ�������Ϣ
					if(newMes.getisIfmass()==true){
					massMes(newMes);
					System.out.println(newMes.getContent().getText(0,newMes.getContent().getLength() ));
					}
					else{
						singleMes(newMes);
					}
					newMes = (MyMessage) in.readObject();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					c.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				clientSet.remove(c);//�û��˳���
				SendUpdateClientList();
			}
		}
	}

	public static void main(String[] args) {
		new ServerSocketTest().work();
	}

}
