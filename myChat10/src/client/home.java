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
	//当前SQL
	private sql conn;
	all data=new all();

	public home(String userName,String serverIP){
		data.setUserName(userName);
		data.setServerIP(serverIP);
		setTitle("聊天大厅 - 用户："+data.getUserName());
		//边界分布
		setLayout(new BorderLayout());
		//房间列表，设置显示10条记录，不可以多选
		classlist=new List(10,false);
		//导入滚动面板
		scrollPane=new JScrollPane(classlist);
		//发送信息按钮
		btnCreate=new JButton("创建");
		btnCreate.setEnabled(false);
		btnGo=new JButton("进入");
		//监听发送按钮
		btnCreate.addActionListener(this);
		//监听进入房间按钮
		btnGo.addActionListener(this);
		classlist.addMouseListener(this);
		//势力环南边面板，布局为流布局
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
			JOptionPane.showMessageDialog(null, "获取本地ip失败，不可创建房间！", "错误", JOptionPane.ERROR_MESSAGE);
			e2.printStackTrace();
		}
		
		//连接SQL
		conn=new sql(serverIP);
		//打开窗口执行多线程：自动更新房间列表
		new homelist().start();
		//关闭窗口运行函数
		this.addWindowListener(new WindowAdapter() {
			@Override
	          public void windowClosing(WindowEvent e)
	          {
	        	try {
	        		//设置该用户下线
					conn.updateUserFalse(data.getUserName());
					System.out.println("用户"+data.getUserName()+"退出成功");
					//关闭数据库
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
		//监听创建按钮-新建成功后自动进入房间并关闭大厅
		if(e.getSource().equals(btnCreate)){
			try {
				//新建chat类型变量并赋初始值：null
				chat chatGo = null;
				//生成随机数做端口号
				int classPort=new Random().nextInt(65535-10001)+10001;
				//在SQL插入该房间：当前用户名和端口
				conn.insertRoom(data.getUserName(), classPort,data.getThisIP());
				//创建聊天服务器，传入端口号
				serverChat serverchat=new serverChat(classPort,data.getServerIP());
				//监听客户端连接
				serverchat.listenClient();
				//SQL中在指定房间中添加该用户
				conn.insertRoomUser(classPort, data.getUserName());
				//进入该房间并传入当前用户名和端口号
				chatGo = new chat(data.getUserName(),classPort,data.getServerIP());
				//建立监听
				chatGo.initSocket();
				//关闭大厅窗口
				this.dispose();
			} catch (SQLException e2) {
				JOptionPane.showMessageDialog(null, "创建房间失败！", "错误", JOptionPane.ERROR_MESSAGE);
				e2.printStackTrace();
			}
		}
		//监听进入房间按钮
		if(e.getSource().equals(btnGo)){
			//新建chat类型变量并赋初始值：null
			try {
				chat chatGo = null;
				//所选房间名称长
				int f=classlist.getSelectedItem().toString().length();
				//截取房主名
				String classMaster=classlist.getSelectedItem().substring(0,f-3);
				//通过房主名查询port
				int classPort2=conn.selectRoomPort(classMaster);
				//SQL中在指定房间中添加该用户
				conn.insertRoomUser(classPort2, data.getUserName());
				//进入该房间并传入当前用户名和端口号
				chatGo = new chat(data.getUserName(),classPort2,data.getServerIP());
				//建立连接
				chatGo.initSocket();
				//关闭大厅
				this.dispose();
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(null, "进入房间失败！", "错误", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
			catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "进入房间失败！", "错误", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		//监听classlist
		if(e.getSource().equals(classlist)) {
			//判断是否选中有效lndex
			if(classlist.getSelectedIndex() >= 0) {
				//是否双击
				if(e.getClickCount() == 2) {
					//新建chat类型变量并赋初始值：null
					try {
						chat chatGo = null;
						//所选房间名称长
						int f=classlist.getSelectedItem().toString().length();
						//截取房主名
						String classMaster=classlist.getSelectedItem().substring(0,f-3);
						//通过房主名查询port
						int classPort2=conn.selectRoomPort(classMaster);
						//SQL中在指定房间中添加该用户
						conn.insertRoomUser(classPort2, data.getUserName());
						//进入该房间并传入当前用户名和端口号
						chatGo = new chat(data.getUserName(),classPort2,data.getServerIP());
						//建立连接
						chatGo.initSocket();
						//关闭大厅
						this.dispose();
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(null, "进入房间失败！", "错误", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(null, "进入房间失败！", "错误", JOptionPane.ERROR_MESSAGE);
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
	
	//多线程：实时更新大厅当前所有房间
	class homelist extends Thread{
		//该线程执行函数
	    @Override
	    public void run() {
	    	//循环
	        while (true) {    
	            try {
	            	//情况房间列表
	            	classlist.removeAll();
	            	//判断查询是否房间为空
	            	if(conn.selectRoomPort() != null) {
	            		//遍历写入房间号，即端口号
			           	for(Object s:conn.selectRoomMaster()) {
			           		classlist.add(s.toString().substring(3,s.toString().length()-1)+"的房间");
			           	}
	            	}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "房间刷新失败！", "错误", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
	            // 线程设置 
	            try {
	            	//执行时间间隔
	                Thread.sleep(3000);    
	            } catch (InterruptedException e) {
	            	JOptionPane.showMessageDialog(null, "房间刷新失败！", "错误", JOptionPane.ERROR_MESSAGE);
	                e.printStackTrace();    
	            }    
	        }    
	    }
	}


	
}


