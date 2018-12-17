package client;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import sql.sql;

@SuppressWarnings("serial")
public class getIP extends JFrame implements ActionListener {

	private JLabel lblIP,lblTitle;
	private JTextField txtIP;
	private JButton btnConn,btnExit;
	
	//当前SQL
	private Connection conn;
	
	public getIP(){
		setTitle("连接服务器");
		lblIP=new JLabel("IP：");
		lblTitle=new JLabel("服务器地址");
		lblTitle.setFont(new Font("微软雅黑",Font.PLAIN,30));
		txtIP=new JTextField();
		//设置默认值
		txtIP.setText("127.0.0.1");
		btnConn=new JButton("连接");
		btnExit=new JButton("退出");
		lblTitle.setBounds(50,20,270,40);
		lblIP.setBounds(30,70,50,25);
		txtIP.setBounds(70,70,150,25);
		btnConn.setBounds(40,110,70,25);
		btnExit.setBounds(140, 110, 70, 25);
		add(lblTitle);
		add(lblIP);
		add(txtIP);
		add(btnConn);
		add(btnExit);

		
		btnConn.addActionListener(this);					//监听连接按钮
		btnExit.addActionListener(this);				//监听退出按钮
		txtIP.addActionListener(this);
		setLayout(null);
		setSize(270,200);
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}
	
	//监听按钮函数
	@Override
	public void actionPerformed(ActionEvent e) {
		// 监听退出按钮
		if(e.getSource().equals(btnExit) ){
			this.dispose();
		}
		//监听登录按钮
		if(e.getSource().equals(btnConn) || e.getSource().equals(txtIP)){
			//连接SQL
			String serverIP=txtIP.getText().trim();
			if(txtIP.getText()==null  ||  txtIP.getText().trim().equals("")) {
				JOptionPane.showMessageDialog(null, "请输入ip地址！", "错误", JOptionPane.ERROR_MESSAGE);
			}else {
				conn=new sql(serverIP).connection;
				if(conn!=null) {
					try {
						conn.close();
						System.out.println("SQL关闭成功");
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					this.dispose();
					new login(serverIP);
				}
			}
		}
	}
	
}
