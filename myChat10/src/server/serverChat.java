package server;

import sql.sql;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.List;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import data.all;


@SuppressWarnings("serial")
public class serverChat extends JFrame implements Runnable{

	private JScrollPane scrollPane,eastScrollPane;
	private JTextArea txtContent;
	private JPanel southPanel;
	private JTextField txtTalk;
	private JButton btnSend;
	private List list;
	//�ͻ����û�����
	private ServerSocket serverUser;	
	//���������
	private Map<Integer, Socket> clients = new HashMap<Integer, Socket>();
	//�ͻ��˷���������Ϣ
	private String temp = "";
	//��ǰSQL
	private sql conn;
	all data=new all();
	
	public serverChat(int port,String serverIP) throws SQLException{
		data.setPort(port);
		setTitle(data.getPort()+"�������� - �����");
		//�߽�ֲ�
		setLayout(new BorderLayout());
		//�����ı���
		txtContent=new JTextArea();
		//�������
		scrollPane=new JScrollPane(txtContent);
		//ʵ�����б��������ʾ10����¼�����Զ�ѡ
		list=new List(10,true);
		//�������
		eastScrollPane=new JScrollPane(list);
		//�����ı���
		txtTalk=new JTextField(28);
		//������Ϣ��ť
		btnSend=new JButton("����");	
		//�������Ͱ�ť
		btnSend.setActionCommand("sendMsg");
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
		
		//����SQL
		conn=new sql(serverIP);
		new selectUserExist().start();
		//����˴������ò��ɼ�
		setVisible(false);
	}
	
	private void closeServer() throws SQLException, IOException {
		//���SQL���¸÷���������û�
		conn.deleteRoomAllUser(data.getPort());
		//���SQL���¸÷���
		conn.deleteRoom(data.getPort());
		//�ر�SQL
		conn.closeConnection();
		//������������
		clients.clear();
		//�ر�serverSocket
		serverUser.close();
		//�رմ���
		this.dispose();
	}
	
	//����һ��ServerSocket�����û�����
    public void listenClient() {
        try {
        	serverUser = new ServerSocket(data.getPort());
            // ʹ���̣߳�server���Խ�������Socket����������server��accept����������ʽ��
           Thread t1=new Thread(this);
           t1.start();
        } catch (Exception e) {
        	//JOptionPane.showMessageDialog(null, "�����û�����ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
            System.out.println("�����û�����ʧ�ܣ�");
        	e.printStackTrace();
        }
    }

    //��ӡmsg����
    public void apppendMsg(String msg){
        this.txtContent.append(msg+"\r\n");
    }

    //����msg���ݵ����пͻ���
    public void sendMsgToAll(Socket fromSocket, String msg) {
        Set<Integer> keset = this.clients.keySet();
        java.util.Iterator<Integer> iter = keset.iterator();
        while(iter.hasNext()){
            int key = iter.next();
            Socket socket = clients.get(key);
            if(socket != fromSocket){
                try {
                    if(socket.isClosed() == false){
                        if(socket.isOutputShutdown() == false){
                            Writer writer = new OutputStreamWriter(socket.getOutputStream());
                            writer.write(msg);
                            writer.flush();
                        }
                    }
                } catch (SocketException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    //��ǰ�����������к���
	@Override
	public void run(){
		try{
			//ѭ�������ͻ�������
			while (true) {
		        System.out.println("�����������ڼ���������");
		        //�����������γ�����
		        Socket socket = serverUser.accept();
		        clients.put(socket.getPort(), socket);
		        temp = "�û���"+socket.getPort()+"������";
		        //���ô�ӡmsg���ݺ���
		        this.apppendMsg(temp);
		        //���������̣߳���ȡ�ͻ�����Ϣ
		        new server(socket, this).start();
		     }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//�̣߳�ѭ����ѯ�÷����Ƿ�����û�
		class selectUserExist extends Thread{
			@SuppressWarnings("deprecation")
			@Override
		    public void run() {    
		        while (true) {    
		            try {
		            	if(!conn.selectRoomUserExist(data.getPort())){
		            		closeServer();
		            		this.stop();
		            		System.out.println("�̹߳ر�");
		            	}
					} catch (SQLException e1) {
						//JOptionPane.showMessageDialog(null, "��ѯ�û���ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
						System.out.println("��ѯ�û���ʧ�ܣ�");
						e1.printStackTrace();
					} catch (IOException e) {
						//JOptionPane.showMessageDialog(null, "�رշ�����ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
						System.out.println("�رշ�����ʧ�ܣ�");
						e.printStackTrace();
					}
		            // �߳����� 
		            try {
		            	//ִ��ʱ����
		                Thread.sleep(3000);    
		            } catch (InterruptedException e) {
		            	//JOptionPane.showMessageDialog(null, "ѭ����ѯ�û���ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
		            	System.out.println("ѭ����ѯ�û���ʧ�ܣ�");
		            	e.printStackTrace();    
		            }    
		        }    
		    }
		}
	
	//���̣߳���ȡ�ͻ�����Ϣ
	class server extends Thread{
	    private Socket socket = null;
	    private serverChat server = null;
	    private InputStreamReader reader = null;
	    char chars[] = new char[64];
	    int len;
	    private String temp = null;
	    public server(Socket socket, serverChat server) {
	        this.socket = socket;
	        this.server = server;
	        init();
	    }
	    
	    //��Ϣ��ȡ����
	    private void init(){
	        try {
	            reader = new InputStreamReader(socket.getInputStream());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    @SuppressWarnings("deprecation")
		@Override
	    public void run() {
	        System.out.println("��ȡ�ͻ�����Ϣ�߳̿�ʼ����");
	        while(true){
	            try {
	                System.out.println("�߳�"+this.getId()+":��ʼ�ӿͻ��˶�ȡ���ݡ���>");
	                while ((len = ((Reader) reader).read(chars)) != -1) {
	                    temp = new String(chars, 0, len);
	                    System.out.println("���Կͻ���"+socket.getPort()+"����Ϣ:" +temp);
	                    server.apppendMsg("���Կͻ���"+socket.getPort()+"����Ϣ:" +temp);
	                    server.sendMsgToAll(this.socket, temp);
	                }
	                if(socket.getKeepAlive() == false){
	                    ((Reader) reader).close();
 	                    //temp = "�߳�"+this.getId()+"����>�ر�";
	                    //System.out.println(temp);
	                    temp = "�ͻ���"+socket.getPort()+":�˳�";
	                    //��ӡmsg����
	                    server.apppendMsg(temp);
	                    socket.close();
	                    this.stop();
	                }
	            } catch (Exception e) {
	            	System.out.println("��ȡ�ͻ�����Ϣʧ��1");
	            	//JOptionPane.showMessageDialog(null, "��ȡ�ͻ�����Ϣʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
	                e.printStackTrace();
	                try {
	                    ((Reader) reader).close();
	                    socket.close();
	                } catch (IOException e1) {
	                	System.out.println("��ȡ�ͻ�����Ϣʧ��2");
	                	//JOptionPane.showMessageDialog(null, "��ȡ�ͻ�����Ϣʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
	                    e1.printStackTrace();
	                }
	            }
	        }
	    }
	}
}



