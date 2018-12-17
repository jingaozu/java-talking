package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import data.all;
import sql.sql;

@SuppressWarnings("serial")
public class sign extends JFrame implements ActionListener {

	private JLabel lblName,lblPass,lblTitle,lblMsg;
	private JTextField txtName,txtPass;
	private JButton btnSign,btnReturn;
	//当前SQL
	private sql conn;
	
	all data=new all();
	
	public sign(String serverIP){
		data.setServerIP(serverIP);
		setTitle("注册");
		lblName=new JLabel("帐号：");
		lblPass=new JLabel("密码：");
		lblTitle=new JLabel("注册账号");
		lblTitle.setFont(new Font("微软雅黑",Font.PLAIN,30));
		lblMsg=new JLabel();
		txtName=new JTextField();
		txtPass=new JTextField();
		btnSign=new JButton("注册");
		btnReturn=new JButton("取消");
		
		lblTitle.setBounds(70,20,270,40);
		lblName.setBounds(50,70,50,25);
		txtName.setBounds(100,70,150,25);
		lblPass.setBounds(50,105,50,25);
		txtPass.setBounds(100,105,150,25);
		btnSign.setBounds(60,140,70,25);
		btnReturn.setBounds(170, 140, 70, 25);
		lblMsg.setBounds(50,170,170,25);
		lblMsg.setForeground(Color.red);
		
		add(lblTitle);
		add(lblName);
		add(lblPass);
		add(txtName);
		add(txtPass);
		add(btnSign);
		add(btnReturn);
		add(lblMsg);
		
		btnReturn.addActionListener(this);					//监听取消按钮
		btnSign.addActionListener(this);				//监听注册按钮
		setLayout(null);
		setSize(325,230);
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//连接SQL
		conn=new sql(data.getServerIP());
		//关闭窗口运行该函数
		this.addWindowListener(new WindowAdapter() {
	          @Override
	          public void windowClosing(WindowEvent e)
	          {
	        	  //关闭SQL
	             conn.closeConnection();
	          }
	      });
		setResizable(false);
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// 监听取消按钮
		if(e.getSource().equals(btnReturn)){
			this.dispose();
			conn.closeConnection();
			new login(data.getServerIP());
		}
		//监听注册按钮
		if(e.getSource().equals(btnSign)){
			try {
				//查询SQL中是否存在name
				if(conn.selectUserName(txtName.getText()))
				{
					JOptionPane.showMessageDialog(null, "该用户已注册！", "错误", JOptionPane.ERROR_MESSAGE);
				}
				//插入账号密码到SQL中
				else if(conn.insertUser(txtName.getText(), txtPass.getText()))
				{
					JOptionPane.showMessageDialog(null, "注册成功！", "正确", JOptionPane.INFORMATION_MESSAGE);
					//关闭窗口
					this.dispose();
					//退出数据库
					conn.closeConnection();
					//返回登录界面
					new login(data.getServerIP());
				}
			} catch (HeadlessException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
}
