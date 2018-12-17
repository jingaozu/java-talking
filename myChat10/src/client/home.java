package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.all;
import server.serverChat;
import sql.sql;

@SuppressWarnings("serial")
public class home extends JFrame implements ActionListener, MouseListener{

	private JScrollPane scrollPane;
	private JPanel southPanel;
	private JButton btnCreate,btnGo;
	private List classlist;	
	//��ǰSQL
	private sql conn;
	all data=new all();

	public home(String userName,String serverIP){
		data.setUserName(userName);
		data.setServerIP(serverIP);
		setTitle("������� - �û���"+data.getUserName());
		//�߽�ֲ�
		setLayout(new BorderLayout());
		//�����б�������ʾ10����¼�������Զ�ѡ
		classlist=new List(10,false);
		//����������
		scrollPane=new JScrollPane(classlist);
		//������Ϣ��ť
		btnCreate=new JButton("����");
		btnCreate.setEnabled(false);
		btnGo=new JButton("����");
		//�������Ͱ�ť
		btnCreate.addActionListener(this);
		//�������뷿�䰴ť
		btnGo.addActionListener(this);
		classlist.addMouseListener(this);
		//�������ϱ���壬����Ϊ������
		southPanel= new JPanel(new FlowLayout());
		southPanel.add(btnCreate);
		southPanel.add(btnGo);
		add(southPanel,"South");
		add(scrollPane,"Center");
		setSize(400,300);
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		try {
			String thisIP=InetAddress.getLocalHost().getHostAddress();
			data.setThisIP(thisIP);
			btnCreate.setEnabled(true);
		} catch (UnknownHostException e2) {
			JOptionPane.showMessageDialog(null, "��ȡ����ipʧ�ܣ����ɴ������䣡", "����", JOptionPane.ERROR_MESSAGE);
			e2.printStackTrace();
		}
		
		//����SQL
		conn=new sql(serverIP);
		//�򿪴���ִ�ж��̣߳��Զ����·����б�
		new homelist().start();
		//�رմ������к���
		this.addWindowListener(new WindowAdapter() {
			@Override
	          public void windowClosing(WindowEvent e)
	          {
	        	try {
	        		//���ø��û�����
					conn.updateUserFalse(data.getUserName());
					System.out.println("�û�"+data.getUserName()+"�˳��ɹ�");
					//�ر����ݿ�
					conn.closeConnection();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
	          }
	      });
		setResizable(false);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//����������ť-�½��ɹ����Զ����뷿�䲢�رմ���
		if(e.getSource().equals(btnCreate)){
			try {
				//�½�chat���ͱ���������ʼֵ��null
				chat chatGo = null;
				//������������˿ں�
				int classPort=new Random().nextInt(65535-10001)+10001;
				//��SQL����÷��䣺��ǰ�û����Ͷ˿�
				conn.insertRoom(data.getUserName(), classPort,data.getThisIP());
				//�������������������˿ں�
				serverChat serverchat=new serverChat(classPort,data.getServerIP());
				//�����ͻ�������
				serverchat.listenClient();
				//SQL����ָ����������Ӹ��û�
				conn.insertRoomUser(classPort, data.getUserName());
				//����÷��䲢���뵱ǰ�û����Ͷ˿ں�
				chatGo = new chat(data.getUserName(),classPort,data.getServerIP());
				//��������
				chatGo.initSocket();
				//�رմ�������
				this.dispose();
			} catch (SQLException e2) {
				JOptionPane.showMessageDialog(null, "��������ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
				e2.printStackTrace();
			}
		}
		//�������뷿�䰴ť
		if(e.getSource().equals(btnGo)){
			//�½�chat���ͱ���������ʼֵ��null
			try {
				chat chatGo = null;
				//��ѡ�������Ƴ�
				int f=classlist.getSelectedItem().toString().length();
				//��ȡ������
				String classMaster=classlist.getSelectedItem().substring(0,f-3);
				//ͨ����������ѯport
				int classPort2=conn.selectRoomPort(classMaster);
				//SQL����ָ����������Ӹ��û�
				conn.insertRoomUser(classPort2, data.getUserName());
				//����÷��䲢���뵱ǰ�û����Ͷ˿ں�
				chatGo = new chat(data.getUserName(),classPort2,data.getServerIP());
				//��������
				chatGo.initSocket();
				//�رմ���
				this.dispose();
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(null, "���뷿��ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
			catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "���뷿��ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		//����classlist
		if(e.getSource().equals(classlist)) {
			//�ж��Ƿ�ѡ����Чlndex
			if(classlist.getSelectedIndex() >= 0) {
				//�Ƿ�˫��
				if(e.getClickCount() == 2) {
					//�½�chat���ͱ���������ʼֵ��null
					try {
						chat chatGo = null;
						//��ѡ�������Ƴ�
						int f=classlist.getSelectedItem().toString().length();
						//��ȡ������
						String classMaster=classlist.getSelectedItem().substring(0,f-3);
						//ͨ����������ѯport
						int classPort2=conn.selectRoomPort(classMaster);
						//SQL����ָ����������Ӹ��û�
						conn.insertRoomUser(classPort2, data.getUserName());
						//����÷��䲢���뵱ǰ�û����Ͷ˿ں�
						chatGo = new chat(data.getUserName(),classPort2,data.getServerIP());
						//��������
						chatGo.initSocket();
						//�رմ���
						this.dispose();
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(null, "���뷿��ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, "���뷿��ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	
	//���̣߳�ʵʱ���´�����ǰ���з���
	class homelist extends Thread{
		//���߳�ִ�к���
	    @Override
	    public void run() {
	    	//ѭ��
	        while (true) {    
	            try {
	            	//��������б�
	            	classlist.removeAll();
	            	//�жϲ�ѯ�Ƿ񷿼�Ϊ��
	            	if(conn.selectRoomPort() != null) {
	            		//����д�뷿��ţ����˿ں�
			           	for(Object s:conn.selectRoomMaster()) {
			           		classlist.add(s.toString().substring(3,s.toString().length()-1)+"�ķ���");
			           	}
	            	}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "����ˢ��ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
	            // �߳����� 
	            try {
	            	//ִ��ʱ����
	                Thread.sleep(3000);    
	            } catch (InterruptedException e) {
	            	JOptionPane.showMessageDialog(null, "����ˢ��ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
	                e.printStackTrace();    
	            }    
	        }    
	    }
	}


	
}


