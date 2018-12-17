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
	
	//��ǰSQL
	private Connection conn;
	
	public getIP(){
		setTitle("���ӷ�����");
		lblIP=new JLabel("IP��");
		lblTitle=new JLabel("��������ַ");
		lblTitle.setFont(new Font("΢���ź�",Font.PLAIN,30));
		txtIP=new JTextField();
		//����Ĭ��ֵ
		txtIP.setText("127.0.0.1");
		btnConn=new JButton("����");
		btnExit=new JButton("�˳�");
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

		
		btnConn.addActionListener(this);					//�������Ӱ�ť
		btnExit.addActionListener(this);				//�����˳���ť
		txtIP.addActionListener(this);
		setLayout(null);
		setSize(270,200);
		setLocationRelativeTo(null);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}
	
	//������ť����
	@Override
	public void actionPerformed(ActionEvent e) {
		// �����˳���ť
		if(e.getSource().equals(btnExit) ){
			this.dispose();
		}
		//������¼��ť
		if(e.getSource().equals(btnConn) || e.getSource().equals(txtIP)){
			//����SQL
			String serverIP=txtIP.getText().trim();
			if(txtIP.getText()==null  ||  txtIP.getText().trim().equals("")) {
				JOptionPane.showMessageDialog(null, "������ip��ַ��", "����", JOptionPane.ERROR_MESSAGE);
			}else {
				conn=new sql(serverIP).connection;
				if(conn!=null) {
					try {
						conn.close();
						System.out.println("SQL�رճɹ�");
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
