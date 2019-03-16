package objtalk;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.lang.model.element.Element;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class SocketFrame extends JFrame {
	private JTextPane jtpMes = new JTextPane();//��Ϣ��
	private StyledDocument contentDoc = jtpMes.getStyledDocument();//ȡ���ı����������Զ��壨sas��������
	private JScrollPane jspMes = new JScrollPane(jtpMes);//Ϊ��Ϣ����ӻ�����
	private JButton btnSend = new JButton("Send");
	private JButton btnConnect = new JButton("Connect");
	private JButton btnSelectimg = new JButton("img");
	private JTextPane jtpNewMes = new JTextPane();//��Ϣ�򣨿�����ʾͼƬ�����֣�
	private JScrollPane jspNewMes = new JScrollPane(jtpNewMes);//ΪȺ�Ŀ���ӻ�����
	private StyledDocument sendDoc = jtpNewMes.getStyledDocument();
	private JPanel panSend = new JPanel();
	JPanel btnPan = new JPanel();
	private Font font = new Font("����", Font.PLAIN, 20);
	private JList<String> listClient = new JList<>();
	private JScrollPane jspClientList = new JScrollPane(listClient);
	private Socket socket;
	private ObjectOutputStream out;
	private ReadThread reader;//��ȡ��Ϣ�߳�

	public SocketFrame() {
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		init();
		getContentPane().add(jspMes);
		getContentPane().add(panSend, BorderLayout.SOUTH);
		getContentPane().add(jspClientList, BorderLayout.EAST);

	}

	public void updateListClient(ArrayList list) {//����Ⱥ���û���Ϣ
		listClient.setModel(new ClientListModel(list));
	}

	class ClientListModel extends AbstractListModel {//����list��Ϣ
		ArrayList list;

		public ClientListModel(ArrayList list) {
			super();
			this.list = list;
		}

		@Override
		public Object getElementAt(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getSize() {
			return list.size();
		}
	}

	private void init() {
		panSend.setLayout(new BorderLayout());
		panSend.add(jspNewMes,BorderLayout.CENTER);
		panSend.add(btnPan,BorderLayout.EAST);
		btnPan.add(btnSend);
		btnPan.add(btnConnect);
		btnPan.add(btnSelectimg);
		jtpMes.setEditable(false);
		jtpMes.setFont(font);
		jtpNewMes.setFont(font);
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String str = jtpNewMes.getText().trim();//�õ��ı�
				System.out.println(str);
				if (str != null && str.length() > 0 && socket != null) {
					SocketAddress address = socket.getRemoteSocketAddress();//�õ����ص�ַ
					String ip = address.toString().substring(1,
					address.toString().indexOf(":") + 1);//���ip
					SimpleAttributeSet sas = new SimpleAttributeSet();//�����洢��Ϣ��
					StyleConstants.setFontSize(sas,24);//��������
					try {
						/*senDoc��Ϣ���ݻ��Զ���������Ϣ���ȡ���󶨸��£�50��57�д���ʵ�֣�������ֻ������Ϣǰ�����ip�������û�����*/
						sendDoc.insertString(0, ip, sas);
						
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MyMessage mes = new MyMessage(true,MyMessage.MES_TYPE_PLAIN);
					mes.setContent(sendDoc);
					sendMes(mes);//������Ϣ
					try {
						sendDoc.remove(0, sendDoc.getLength());//ȥ�������е�����
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		btnConnect.addActionListener(new ActionListener() {//���ӷ�����

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(socket==null){
				try {
					socket = new Socket("10.117.45.114", 12345);//����ip�Լ�����
					reader = new ReadThread(socket);
					reader.start();
					out = new ObjectOutputStream(socket.getOutputStream());//������Ϣ������
				} catch (Exception e) {
					e.printStackTrace();
				}
				}
			}
		});
		
		btnSelectimg.addActionListener(new ActionListener() {//ѡ������ͼƬ��������
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser fc = new JFileChooser("d:");//�ļ�ѡ����
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Image", "jpg","gif");//�ļ�ɸѡ��
				fc.setFileFilter(filter);
				int i = fc.showOpenDialog(SocketFrame.this);
				if(i == JFileChooser.APPROVE_OPTION){
					try {
						Image img = ImageIO.read(fc.getSelectedFile());
						ImageIcon icon = new ImageIcon(img);
						SimpleAttributeSet sas = new SimpleAttributeSet();//����
						StyleConstants.setIcon(sas, icon);//��ͼ�����sas����
						sendDoc.insertString(sendDoc.getLength(), "icon", sas);//��sas�����ı���ʽ�����Զ���
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {//�ر������������˳����ر�
				if (out != null) {
					MyMessage mes = new MyMessage(true,MyMessage.MES_TYPE_PLAIN);
					//mes.setContent("quit");
					sendMes(mes);
					reader.stopRun();
				}
			}
		});
		
		listClient.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);
				if(e.getClickCount()==2){//˫������˽��
					new PrivateDialog(listClient.getSelectedValue().toString()).setVisible(true);
				}
			}
			
		});
	}
	
	public void append(StyledDocument sd){
		int caretPosition = jtpMes.getStyledDocument().getLength();
		caretPosition+=sd.getLength();
		try {
		for(int i=0;i<sd.getLength();i++){
			javax.swing.text.Element e = sd.getCharacterElement(i);
			if(e.getName().equals("icon")){
				contentDoc.insertString(contentDoc.getLength(), "icon", e.getAttributes());
				i+=2;
			}
			else{
				String s = sd.getText(i, 1);
				contentDoc.insertString(contentDoc.getLength(), s, e.getAttributes());
			}
		}
			contentDoc.insertString(contentDoc.getLength(), "\n", null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jtpMes.setCaretPosition(caretPosition);
	}

	public void sendMes(MyMessage m) {
		if (out != null) {
			try {
				out.reset();//��������ͬһ�����ݲ��ϸı�Ķ�����Ҫʹ��reset(��ʱΪsendDoc)
				out.writeObject(m);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class ReadThread extends Thread {
		Socket c;
		boolean flag = true;

		public ReadThread(Socket c) {
			this.c = c;
		}

		@Override
		public void run() {
			try {
				ObjectInputStream in = new ObjectInputStream((c.getInputStream()));
				MyMessage newMes = (MyMessage) in.readObject();
				while (flag) {
					switch (newMes.getMesType()) {
					case MyMessage.MES_TYPE_PLAIN:
						append(newMes.getContent());//���õ�����Ϣ��ӽ������
						break;
					case MyMessage.MES_TYPE_UPDATE_CLIENTLIST:
						updateListClient(newMes.getClientList());//������������Ϣ
						break;
					}
					//����������message�����ʼ�����´�ʹ��
					in = new ObjectInputStream((c.getInputStream()));
					newMes = (MyMessage) in.readObject();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			}

		public void stopRun() {
			flag = false;
		}
	}
	
	class PrivateDialog extends JDialog{//�����Ի���
		private JTextPane jtpPriMes = new JTextPane();
		private JScrollPane jspPriMes = new JScrollPane(jtpPriMes);
		private JButton btnPriSend = new JButton("Send");
		private JButton btnselect = new JButton("select");
		private JPanel panFun = new JPanel();
		private String ip;
		public PrivateDialog(String ip) {
			// TODO Auto-generated constructor stub
			this.ip = ip;
			this.setTitle(ip);
			this.setSize(400, 300);
			this.setLocationRelativeTo(null);
			init();
			this.add(panFun);
		}
		private void init() {
			panFun.add(jtpPriMes);
			panFun.add(btnPriSend);
			panFun.add(btnselect);
			btnPriSend.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String str  = jtpPriMes.getText().trim();
					if(str!=null&&str.length()>0&&socket!=null){
						MyMessage mes = new MyMessage(false,MyMessage.MES_TYPE_PLAIN);
						mes.setIp(ip);
						mes.setContent(jtpPriMes.getStyledDocument());
						sendMes(mes);
				}
			}
			});
			
			btnselect.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JFileChooser fc = new JFileChooser("d:");
					FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Image", "jpg","gif");//�ļ�ɸѡ��
					fc.setFileFilter(filter);
					int i = fc.showOpenDialog(SocketFrame.this);
					if(i == JFileChooser.APPROVE_OPTION){
						try {
							Image img = ImageIO.read(fc.getSelectedFile());
							ImageIcon icon = new ImageIcon(img);
							SimpleAttributeSet sas = new SimpleAttributeSet();//����
							StyleConstants.setIcon(sas, icon);//��ͼ�����sas����
							jtpPriMes.getStyledDocument().insertString(jtpPriMes.getStyledDocument().getLength(), "icon", sas);//��sas�����ı���ʽ�����Զ���
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			});
		}
	}

}
