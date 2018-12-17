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
	//客户端用户连接
	private ServerSocket serverUser;	
	//输入输出流
	private Map<Integer, Socket> clients = new HashMap<Integer, Socket>();
	//客户端发送来的信息
	private String temp = "";
	//当前SQL
	private sql conn;
	all data=new all();
	
	public serverChat(int port,String serverIP) throws SQLException{
		data.setPort(port);
		setTitle(data.getPort()+"号聊天室 - 服务端");
		//边界分布
		setLayout(new BorderLayout());
		//多行文本框
		txtContent=new JTextArea();
		//滚动面板
		scrollPane=new JScrollPane(txtContent);
		//实例化列表框，设置显示10条记录，可以多选
		list=new List(10,true);
		//滚动面板
		eastScrollPane=new JScrollPane(list);
		//单行文本框
		txtTalk=new JTextField(28);
		//发送信息按钮
		btnSend=new JButton("发送");	
		//监听发送按钮
		btnSend.setActionCommand("sendMsg");
		//势力环南边面板，布局为流布局
		southPanel= new JPanel(new FlowLayout());
		southPanel.add(txtTalk);
		southPanel.add(btnSend);
		add(eastScrollPane,"East");
		add(southPanel,"South");
		add(scrollPane,"Center");
		
		setSize(400,300);
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		//连接SQL
		conn=new sql(serverIP);
		new selectUserExist().start();
		//服务端窗口设置不可见
		setVisible(false);
	}
	
	private void closeServer() throws SQLException, IOException {
		//清空SQL表下该房间的所有用户
		conn.deleteRoomAllUser(data.getPort());
		//清空SQL表下该房间
		conn.deleteRoom(data.getPort());
		//关闭SQL
		conn.closeConnection();
		//清空输入输出流
		clients.clear();
		//关闭serverSocket
		serverUser.close();
		//关闭窗口
		this.dispose();
	}
	
	//定义一个ServerSocket监听用户连接
    public void listenClient() {
        try {
        	serverUser = new ServerSocket(data.getPort());
            // 使用线程：server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
           Thread t1=new Thread(this);
           t1.start();
        } catch (Exception e) {
        	//JOptionPane.showMessageDialog(null, "监听用户连接失败！", "错误", JOptionPane.ERROR_MESSAGE);
            System.out.println("监听用户连接失败！");
        	e.printStackTrace();
        }
    }

    //打印msg内容
    public void apppendMsg(String msg){
        this.txtContent.append(msg+"\r\n");
    }

    //发送msg内容到所有客户端
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

    //当前窗口启动运行函数
	@Override
	public void run(){
		try{
			//循环监听客户端连接
			while (true) {
		        System.out.println("服务器端正在监听。。。");
		        //监听函数，形成阻塞
		        Socket socket = serverUser.accept();
		        clients.put(socket.getPort(), socket);
		        temp = "用户："+socket.getPort()+"已连接";
		        //调用打印msg内容函数
		        this.apppendMsg(temp);
		        //开启监听线程：获取客户端消息
		        new server(socket, this).start();
		     }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//线程：循环查询该房间是否存在用户
		class selectUserExist extends Thread{
			@SuppressWarnings("deprecation")
			@Override
		    public void run() {    
		        while (true) {    
		            try {
		            	if(!conn.selectRoomUserExist(data.getPort())){
		            		closeServer();
		            		this.stop();
		            		System.out.println("线程关闭");
		            	}
					} catch (SQLException e1) {
						//JOptionPane.showMessageDialog(null, "查询用户表失败！", "错误", JOptionPane.ERROR_MESSAGE);
						System.out.println("查询用户表失败！");
						e1.printStackTrace();
					} catch (IOException e) {
						//JOptionPane.showMessageDialog(null, "关闭服务器失败！", "错误", JOptionPane.ERROR_MESSAGE);
						System.out.println("关闭服务器失败！");
						e.printStackTrace();
					}
		            // 线程设置 
		            try {
		            	//执行时间间隔
		                Thread.sleep(3000);    
		            } catch (InterruptedException e) {
		            	//JOptionPane.showMessageDialog(null, "循环查询用户表失败！", "错误", JOptionPane.ERROR_MESSAGE);
		            	System.out.println("循环查询用户表失败！");
		            	e.printStackTrace();    
		            }    
		        }    
		    }
		}
	
	//多线程：获取客户端信息
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
	    
	    //消息获取函数
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
	        System.out.println("获取客户端消息线程开始工作");
	        while(true){
	            try {
	                System.out.println("线程"+this.getId()+":开始从客户端读取数据——>");
	                while ((len = ((Reader) reader).read(chars)) != -1) {
	                    temp = new String(chars, 0, len);
	                    System.out.println("来自客户端"+socket.getPort()+"的消息:" +temp);
	                    server.apppendMsg("来自客户端"+socket.getPort()+"的消息:" +temp);
	                    server.sendMsgToAll(this.socket, temp);
	                }
	                if(socket.getKeepAlive() == false){
	                    ((Reader) reader).close();
 	                    //temp = "线程"+this.getId()+"——>关闭";
	                    //System.out.println(temp);
	                    temp = "客户端"+socket.getPort()+":退出";
	                    //打印msg内容
	                    server.apppendMsg(temp);
	                    socket.close();
	                    this.stop();
	                }
	            } catch (Exception e) {
	            	System.out.println("获取客户端信息失败1");
	            	//JOptionPane.showMessageDialog(null, "获取客户端信息失败！", "错误", JOptionPane.ERROR_MESSAGE);
	                e.printStackTrace();
	                try {
	                    ((Reader) reader).close();
	                    socket.close();
	                } catch (IOException e1) {
	                	System.out.println("获取客户端信息失败2");
	                	//JOptionPane.showMessageDialog(null, "获取客户端信息失败！", "错误", JOptionPane.ERROR_MESSAGE);
	                    e1.printStackTrace();
	                }
	            }
	        }
	    }
	}
}



