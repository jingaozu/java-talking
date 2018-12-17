package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import data.all;
import sql.sql;

import java.net.UnknownHostException;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
//���ڻ�ȡ����ʱ��

@SuppressWarnings("serial")
public class chat extends JFrame implements ActionListener{

	private JScrollPane scrollPane,eastScrollPane;
	private JTextArea txtContent;
	private JPanel southPanel;
	private JTextField txtTalk;
	private JButton btnSend;
	private List list;
	//���ÿͻ���Socket�����client������ֵ��null
	private Socket client=null;
	//����Write�����writer������ֵ��null
	private Writer writer=null;
	//��ǰSQL
	private sql conn;
	all data=new all();
	
	public chat(String userName,int port,String serverIP) throws SQLException{
		data.setUserName(userName);
		data.setPort(port);
		data.setServerIP(serverIP);
		setTitle(data.getPort()+"��������");
		//�߽�ֲ�
		setLayout(new BorderLayout());
		//�����ı���
		txtContent=new JTextArea();
		txtContent.setEditable(false);
		//�������
		scrollPane=new JScrollPane(txtContent);
		//ʵ�����б��������ʾ10����¼�����Զ�ѡ
		list=new List(10,true);
		//�������
		eastScrollPane=new JScrollPane(list);
		//�����ı���
		txtTalk=new JTextField(25);
		//������Ϣ��ť
		btnSend=new JButton("����");
		//�������Ͱ�ť���ı���
		btnSend.addActionListener(this);
		txtTalk.addActionListener(this);
		//�������ϱ���壬����Ϊ������
		southPanel= new JPanel(new FlowLayout());
		southPanel.add(txtTalk);
		southPanel.add(btnSend);
		add(eastScrollPane,"East");
		add(southPanel,"South");
		add(scrollPane,"Center");
		
		setSize(400,300);
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    txtContent.setAutoscrolls(true);
	    
		//����SQL
		conn=new sql(serverIP);
		
		try {
			//��ѯ�÷��䷿������ֵ��master
			data.setMater(conn.selectRoomMaster(data.getPort()));
			//��ѯ�÷���ip����ֵ��ip
			data.setThisIP(conn.selectRoomIp(data.getPort()));
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, "��ѯ������Ϣʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
			e2.printStackTrace();
		}
		//�򿪴���ִ�ж��̣߳��Զ������û��б�
		new userlist().start();
		//�رմ���ִ�к���
		this.addWindowListener(new WindowAdapter() {
			@Override
	          public void windowClosing(WindowEvent e)
	          {
	        	try {
	        		//��SQL�У�ɾ���÷����µĸ��û�
	        		conn.deleteRoomUser(data.getPort(),data.getUserName());
	        		//�ر�SQL
	        		conn.closeConnection();
	        		//�Ͽ�ServerScoket����
	        		client.close();
	        		JOptionPane.showMessageDialog(null, "�û��˳��ɹ���", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
	        		//�����������
	        		new home(data.getUserName(),data.getServerIP());
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "�û��˳�����ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "�û��˳�����ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
	          }
	      });
		setResizable(false);
		setVisible(true);
	}
	
	private void closeServer() {
		try {
			//���SQL���¸÷���������û�
			conn.deleteRoomAllUser(data.getPort());
			//���SQL���¸÷���
			conn.deleteRoom(data.getPort());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "����ɾ��ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		//�ر�SQL
		conn.closeConnection();
		//�رմ���
		this.dispose();
		//���ش���
		new home(data.getUserName(),data.getServerIP());
	}
	
	public void initSocket(){
        try {
            client = new Socket(data.getThisIP(), data.getPort());
            writer = new OutputStreamWriter(client.getOutputStream());
            // �������Ӻ�Ϳ����������д������
            client a=new client(client, this);
            a.start();
            this.appendMsg("�����Ϸ�����");
        } catch (UnknownHostException e) {
        	JOptionPane.showMessageDialog(null, "�������1��ɾ���÷��䣡", "����", JOptionPane.ERROR_MESSAGE);
        	closeServer();
            e.printStackTrace();
        } catch (IOException e) {
        	JOptionPane.showMessageDialog(null, "�������2��ɾ���÷��䣡", "����", JOptionPane.ERROR_MESSAGE);
        	closeServer();
        	e.printStackTrace();
        }
    }
	
	//��ӡmsg����
	public void appendMsg(String msg){
        this.txtContent.append(msg+"\r\n");
    }
	
	//��ȡ����ʱ��
	public String nowDate() {
		SimpleDateFormat nowdate = new SimpleDateFormat("HH:mm");//�������ڸ�ʽ
        return nowdate.format(new Date());// new Date()Ϊ��ȡ��ǰϵͳʱ��
	}
	
	//��������
	@Override
	public void actionPerformed(ActionEvent e) {
		// ���temp
		String temp = "";
		if(e.getSource().equals(btnSend) || e.getSource().equals(txtTalk)){
			try {
				//�������Ͱ�ť���ı�����ӻس������¼�
				if(this.txtTalk.getText()==null  ||  this.txtTalk.getText().trim().equals("")){
					JOptionPane.showMessageDialog(null, "������ϢΪ�գ�", "����", JOptionPane.ERROR_MESSAGE);
				}else {
					if((temp = this.txtTalk.getText()) != null){
						writer.write(nowDate()+"  "+data.getUserName()+"\n"+temp);
						writer.flush();
						this.appendMsg(nowDate()+"  "+data.getUserName()+"\n"+temp);
						this.txtTalk.setText("");
					}
				}
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "������ɾ���÷��䣬��Ϣ����ʧ�ܣ��˳������������", "����", JOptionPane.ERROR_MESSAGE);
	        	closeServer();
				e1.printStackTrace();
			}
		}
		
	}
	
	//�̣߳�ʵʱ�����û��б�
	class userlist extends Thread{
		@Override
	    public void run() {    
	        while (true) {    
	            try {
	            	//����û���
	            	list.removeAll();
	            	//��ӡ����
	            	list.add("������"+data.getMater());
	            	//�ж�SQL�и÷����ڴ����û�
	            	if(conn.selectRoomUser(data.getPort()) != null) {
	            		//������ӡ�û�
			           	for(Object s:conn.selectRoomUser(data.getPort())) {
			           		//���ڷ��ص�һ������Ϊ��{a=X}�����ʹ��substring������ȡ�ַ���
			           		list.add("�û���"+s.toString().substring(3,s.toString().length()-1));
			           	}
	            	}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "�����û���ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
	            // �߳����� 
	            try {
	            	//ִ��ʱ����
	                Thread.sleep(3000);    
	            } catch (InterruptedException e) {
	            	JOptionPane.showMessageDialog(null, "�����û���ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
	                e.printStackTrace();    
	            }    
	        }    
	    }
	}

	//�̣߳����ӷ����
	class client extends Thread {
	    private Socket socket = null;
	    private Reader reader = null;
	    private int len = 0;
	    char chars[] = new char[64];
	    private chat client = null;
	    private String temp = "";

	    public client(Socket socket, chat client) {
	        this.socket = socket;
	        this.client = client;
	        try {
	            reader = new InputStreamReader(socket.getInputStream());
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	    }

	    @SuppressWarnings("deprecation")
		@Override
	    public void run() {
	        super.run();
	        System.out.println("�ͻ������߳�"+this.getId()+"��ʼ����");
	        while (true) {
	            try {
	                if (socket.isClosed() == false) {
	                    if (socket.isInputShutdown() == false) {
	                        while ((len = ((Reader) reader).read(chars)) != -1) {
	                            temp = new String(chars, 0, len);
	                            client.appendMsg(temp);
	                            System.out.println();
	                        }
	                    }

	                } else {
	                    if (socket.getKeepAlive() == false) {
	                        reader.close();
	                        socket.close();
	                        this.stop();
	                    }
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	                this.stop();
	            }
	        }
	    }
	}

}

