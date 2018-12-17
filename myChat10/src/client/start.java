package client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jvnet.substance.skin.*;

public class start {

	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					 //…Ë÷√Õ‚π€
		            UIManager.setLookAndFeel(new SubstanceMistAquaLookAndFeel());
		            JFrame.setDefaultLookAndFeelDecorated(true);
		            new getIP();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		});
	}

}
