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
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import data.all;
import sql.sql;

@SuppressWarnings("serial")
public class login extends JFrame implements ActionListener {

	private JLabel lblName,lblPass,lblTitle,lblMsg;
	private JTextField txtName;
	private JPasswordField txtPass;
	private JButton btnLogin,btnSign;
	//当前SQL
	private sql conn;
	
	all data=new all();
	
	public login(String serverIP){
		data.setServerIP(serverIP);
		setTitle("登录");
		lblName=new JLabel("帐号：");
		lblPass=new JLabel("密码：");
		lblTitle=new JLabel("网络聊天室");
		lblTitle.setFont(new Font("微软雅黑",Font.PLAIN,30));
		lblMsg=new JLabel();
		txtName=new JTextField();
		txtPass=new JPasswordField();
		btnLogin=new JButton("登录");
		btnSign=new JButton("注册");
		
		lblTitle.setBounds(70,20,270,40);
		lblName.setBounds(50,70,50,25);
		txtName.setBounds(100,70,150,25);
		lblPass.setBounds(50,105,50,25);
		txtPass.setBounds(100,105,150,25);
		btnLogin.setBounds(60,140,70,25);
		btnSign.setBounds(170, 140, 70, 25);
		lblMsg.setBounds(50,170,170,25);
		lblMsg.setForeground(Color.red);
		
		add(lblTitle);
		add(lblName);
		add(lblPass);
		add(txtName);
		add(txtPass);
		add(btnLogin);
		add(btnSign);
		add(lblMsg);
		
		btnSign.addActionListener(this);					//监听注册按钮
		btnLogin.addActionListener(this);				//监听登录按钮
		txtName.addActionListener(this);
		txtPass.addActionListener(this);
		setLayout(null);
		setSize(325,230);
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//连接SQL
		conn=new sql(serverIP);
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
	
	//监听按钮函数
	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		// 监听注册按钮
		if(e.getSource().equals(btnSign)){
			this.dispose();
			conn.closeConnection();
			new sign(data.getServerIP());
		}
		//监听登录按钮
		if(e.getSource().equals(btnLogin) || e.getSource().equals(txtName) || e.getSource().equals(txtPass)){
			 //检验输入框的值是否可查询SQL
			if(txtName.getText()==null  ||  txtName.getText().trim().equals("")){
				JOptionPane.showMessageDialog(null, "用户名不得为空！", "错误", JOptionPane.ERROR_MESSAGE);
			}else if(txtPass.getText()==null || txtPass.getText().trim().equals("")){
				JOptionPane.showMessageDialog(null, "密码不得为空！", "错误", JOptionPane.ERROR_MESSAGE);
			} else
				try {
					//SQL中验证账号密码
					if(conn.selectUser(txtName.getText(),txtPass.getText())){
						//SQL中验证该账号是否不在线
						if(conn.selectUserOnline(txtName.getText())==0) {
							//SQL中设置该账号上线
							conn.updateUserTrue(txtName.getText());
							this.dispose();
							conn.closeConnection();
							new home(txtName.getText(),data.getServerIP());
						}else
							JOptionPane.showMessageDialog(null, "用户已经在线！", "错误", JOptionPane.ERROR_MESSAGE);
					}else{
						JOptionPane.showMessageDialog(null, "请输入正确的账号或密码！", "错误", JOptionPane.ERROR_MESSAGE);
					}
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		}
	}
	
}
