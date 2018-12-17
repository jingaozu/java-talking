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
	//��ǰSQL
	private sql conn;
	
	all data=new all();
	
	public login(String serverIP){
		data.setServerIP(serverIP);
		setTitle("��¼");
		lblName=new JLabel("�ʺţ�");
		lblPass=new JLabel("���룺");
		lblTitle=new JLabel("����������");
		lblTitle.setFont(new Font("΢���ź�",Font.PLAIN,30));
		lblMsg=new JLabel();
		txtName=new JTextField();
		txtPass=new JPasswordField();
		btnLogin=new JButton("��¼");
		btnSign=new JButton("ע��");
		
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
		
		btnSign.addActionListener(this);					//����ע�ᰴť
		btnLogin.addActionListener(this);				//������¼��ť
		txtName.addActionListener(this);
		txtPass.addActionListener(this);
		setLayout(null);
		setSize(325,230);
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//����SQL
		conn=new sql(serverIP);
		//�رմ������иú���
		this.addWindowListener(new WindowAdapter() {
	          @Override
	          public void windowClosing(WindowEvent e)
	          {
	        	  //�ر�SQL
	             conn.closeConnection();
	          }
	      });
		setResizable(false);
		setVisible(true);
	}
	
	//������ť����
	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		// ����ע�ᰴť
		if(e.getSource().equals(btnSign)){
			this.dispose();
			conn.closeConnection();
			new sign(data.getServerIP());
		}
		//������¼��ť
		if(e.getSource().equals(btnLogin) || e.getSource().equals(txtName) || e.getSource().equals(txtPass)){
			 //����������ֵ�Ƿ�ɲ�ѯSQL
			if(txtName.getText()==null  ||  txtName.getText().trim().equals("")){
				JOptionPane.showMessageDialog(null, "�û�������Ϊ�գ�", "����", JOptionPane.ERROR_MESSAGE);
			}else if(txtPass.getText()==null || txtPass.getText().trim().equals("")){
				JOptionPane.showMessageDialog(null, "���벻��Ϊ�գ�", "����", JOptionPane.ERROR_MESSAGE);
			} else
				try {
					//SQL����֤�˺�����
					if(conn.selectUser(txtName.getText(),txtPass.getText())){
						//SQL����֤���˺��Ƿ�����
						if(conn.selectUserOnline(txtName.getText())==0) {
							//SQL�����ø��˺�����
							conn.updateUserTrue(txtName.getText());
							this.dispose();
							conn.closeConnection();
							new home(txtName.getText(),data.getServerIP());
						}else
							JOptionPane.showMessageDialog(null, "�û��Ѿ����ߣ�", "����", JOptionPane.ERROR_MESSAGE);
					}else{
						JOptionPane.showMessageDialog(null, "��������ȷ���˺Ż����룡", "����", JOptionPane.ERROR_MESSAGE);
					}
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		}
	}
	
}
