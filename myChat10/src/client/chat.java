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
//用于获取当地时间

@SuppressWarnings("serial")
public class chat extends JFrame implements ActionListener{

	private JScrollPane scrollPane,eastScrollPane;
	private JTextArea txtContent;
	private JPanel southPanel;
	private JTextField txtTalk;
	private JButton btnSend;
	private List list;
	//设置客户端Socket类变量client并赋初值：null
	private Socket client=null;
	//设置Write类变量writer并赋初值：null
	private Writer writer=null;
	//当前SQL
	private sql conn;
	all data=new all();
	
	public chat(String userName,int port,String serverIP) throws SQLException{
		data.setUserName(userName);
		data.setPort(port);
		data.setServerIP(serverIP);
		setTitle(data.getPort()+"号聊天室");
		//边界分布
		setLayout(new BorderLayout());
		//多行文本框
		txtContent=new JTextArea();
		txtContent.setEditable(false);
		//滚动面板
		scrollPane=new JScrollPane(txtContent);
		//实例化列表框，设置显示10条记录，可以多选
		list=new List(10,true);
		//滚动面板
		eastScrollPane=new JScrollPane(list);
		//单行文本框
		txtTalk=new JTextField(25);
		//发送信息按钮
		btnSend=new JButton("发送");
		//监听发送按钮，文本框
		btnSend.addActionListener(this);
		txtTalk.addActionListener(this);
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
	    txtContent.setAutoscrolls(true);
	    
		//连接SQL
		conn=new sql(serverIP);
		
		try {
			//查询该房间房主并赋值给master
			data.setMater(conn.selectRoomMaster(data.getPort()));
			//查询该房间ip并赋值给ip
			data.setThisIP(conn.selectRoomIp(data.getPort()));
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, "查询房间信息失败！", "错误", JOptionPane.ERROR_MESSAGE);
			e2.printStackTrace();
		}
		//打开窗口执行多线程：自动更新用户列表
		new userlist().start();
		//关闭窗口执行函数
		this.addWindowListener(new WindowAdapter() {
			@Override
	          public void windowClosing(WindowEvent e)
	          {
	        	try {
	        		//在SQL中，删除该房间下的该用户
	        		conn.deleteRoomUser(data.getPort(),data.getUserName());
	        		//关闭SQL
	        		conn.closeConnection();
	        		//断开ServerScoket连接
	        		client.close();
	        		JOptionPane.showMessageDialog(null, "用户退出成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
	        		//返回聊天大厅
	        		new home(data.getUserName(),data.getServerIP());
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "用户退出房间失败！", "错误", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "用户退出连接失败！", "错误", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
	          }
	      });
		setResizable(false);
		setVisible(true);
	}
	
	private void closeServer() {
		try {
			//清空SQL表下该房间的所有用户
			conn.deleteRoomAllUser(data.getPort());
			//清空SQL表下该房间
			conn.deleteRoom(data.getPort());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "房间删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		//关闭SQL
		conn.closeConnection();
		//关闭窗口
		this.dispose();
		//返回大厅
		new home(data.getUserName(),data.getServerIP());
	}
	
	public void initSocket(){
        try {
            client = new Socket(data.getThisIP(), data.getPort());
            writer = new OutputStreamWriter(client.getOutputStream());
            // 建立连接后就可以往服务端写数据了
            client a=new client(client, this);
            a.start();
            this.appendMsg("已连上服务器");
        } catch (UnknownHostException e) {
        	JOptionPane.showMessageDialog(null, "房间错误1，删除该房间！", "错误", JOptionPane.ERROR_MESSAGE);
        	closeServer();
            e.printStackTrace();
        } catch (IOException e) {
        	JOptionPane.showMessageDialog(null, "房间错误2，删除该房间！", "错误", JOptionPane.ERROR_MESSAGE);
        	closeServer();
        	e.printStackTrace();
        }
    }
	
	//打印msg内容
	public void appendMsg(String msg){
        this.txtContent.append(msg+"\r\n");
    }
	
	//获取当地时间
	public String nowDate() {
		SimpleDateFormat nowdate = new SimpleDateFormat("HH:mm");//设置日期格式
        return nowdate.format(new Date());// new Date()为获取当前系统时间
	}
	
	//监听函数
	@Override
	public void actionPerformed(ActionEvent e) {
		// 清空temp
		String temp = "";
		if(e.getSource().equals(btnSend) || e.getSource().equals(txtTalk)){
			try {
				//监听发送按钮，文本框添加回车发送事件
				if(this.txtTalk.getText()==null  ||  this.txtTalk.getText().trim().equals("")){
					JOptionPane.showMessageDialog(null, "发送信息为空！", "错误", JOptionPane.ERROR_MESSAGE);
				}else {
					if((temp = this.txtTalk.getText()) != null){
						writer.write(nowDate()+"  "+data.getUserName()+"\n"+temp);
						writer.flush();
						this.appendMsg(nowDate()+"  "+data.getUserName()+"\n"+temp);
						this.txtTalk.setText("");
					}
				}
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "房主已删除该房间，信息发送失败，退出到聊天大厅！", "错误", JOptionPane.ERROR_MESSAGE);
	        	closeServer();
				e1.printStackTrace();
			}
		}
		
	}
	
	//线程：实时更新用户列表
	class userlist extends Thread{
		@Override
	    public void run() {    
	        while (true) {    
	            try {
	            	//清空用户表
	            	list.removeAll();
	            	//打印房主
	            	list.add("房主："+data.getMater());
	            	//判断SQL中该房间内存在用户
	            	if(conn.selectRoomUser(data.getPort()) != null) {
	            		//遍历打印用户
			           	for(Object s:conn.selectRoomUser(data.getPort())) {
			           		//由于返回的一条数据为：{a=X}，因此使用substring函数截取字符串
			           		list.add("用户："+s.toString().substring(3,s.toString().length()-1));
			           	}
	            	}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "更新用户表失败！", "错误", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
	            // 线程设置 
	            try {
	            	//执行时间间隔
	                Thread.sleep(3000);    
	            } catch (InterruptedException e) {
	            	JOptionPane.showMessageDialog(null, "更新用户表失败！", "错误", JOptionPane.ERROR_MESSAGE);
	                e.printStackTrace();    
	            }    
	        }    
	    }
	}

	//线程：连接服务端
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
	        System.out.println("客户端子线程"+this.getId()+"开始工作");
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

