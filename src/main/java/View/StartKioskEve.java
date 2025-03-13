package View;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class StartKioskEve extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartKioskEve frame = new StartKioskEve();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StartKioskEve() {
		setVisible(true);
		
		setTitle("Kiosk_Eve"); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 600, 850);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JLabel click = new JLabel("화면을 터치해주세요");
		click.setVerticalAlignment(SwingConstants.BOTTOM);
		click.setHorizontalAlignment(SwingConstants.CENTER);
		click.setFont(new Font("굴림", Font.PLAIN, 40));
		click.setBounds(12, 10, 574, 793);
		contentPane.add(click);
		
		JLabel lblNewLabel = new JLabel("WAS");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(new Color(153, 102, 204));
		lblNewLabel.setFont(new Font("굴림", Font.PLAIN, 60));
		lblNewLabel.setBounds(183, 322, 230, 112);
		contentPane.add(lblNewLabel);
		
		
		click.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				click.setVisible(false);
				
				new StartEveCam();
				setVisible(false);
			}
		});

	}
}
