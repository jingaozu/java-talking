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
	//��ǰSQL
	private sql conn;
	
	all data=new all();
	
	public sign(String serverIP){
		data.setServerIP(serverIP);
		setTitle("ע��");
		lblName=new JLabel("�ʺţ�");
		lblPass=new JLabel("���룺");
		lblTitle=new JLabel("ע���˺�");
		lblTitle.setFont(new Font("΢���ź�",Font.PLAIN,30));
		lblMsg=new JLabel();
		txtName=new JTextField();
		txtPass=new JTextField();
		btnSign=new JButton("ע��");
		btnReturn=new JButton("ȡ��");
		
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
		
		btnReturn.addActionListener(this);					//����ȡ����ť
		btnSign.addActionListener(this);				//����ע�ᰴť
		setLayout(null);
		setSize(325,230);
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//����SQL
		conn=new sql(data.getServerIP());
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// ����ȡ����ť
		if(e.getSource().equals(btnReturn)){
			this.dispose();
			conn.closeConnection();
			new login(data.getServerIP());
		}
		//����ע�ᰴť
		if(e.getSource().equals(btnSign)){
			try {
				//��ѯSQL���Ƿ����name
				if(conn.selectUserName(txtName.getText()))
				{
					JOptionPane.showMessageDialog(null, "���û���ע�ᣡ", "����", JOptionPane.ERROR_MESSAGE);
				}
				//�����˺����뵽SQL��
				else if(conn.insertUser(txtName.getText(), txtPass.getText()))
				{
					JOptionPane.showMessageDialog(null, "ע��ɹ���", "��ȷ", JOptionPane.INFORMATION_MESSAGE);
					//�رմ���
					this.dispose();
					//�˳����ݿ�
					conn.closeConnection();
					//���ص�¼����
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
