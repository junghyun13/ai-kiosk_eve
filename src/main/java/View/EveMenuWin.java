package View;

import java.awt.EventQueue;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controller.DetectIntentTexts;
import controller.GoTTS;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import java.awt.Font;


public class EveMenuWin extends JPanel implements  ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JTable jtable_sel;
	private static DefaultTableModel dtable_list;
	private JScrollPane scrollPane_menuList;
	static ArrayList<String> orderProduct = new ArrayList<String>(); // 주문한 제품 이름
	int menu_cnt =0;
	static int font_size 	=10;
	
	JButton btnPayment = new JButton();
	JButton btnDelete = new JButton();

	//private static String QRresult = "";
	private static String YRresult = "";
	/**
	 * Launch the application.
	 */
	public static void main(String param1, String param2) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					YRresult = param2;
					System.out.println(">>>> "+ YRresult);
					
					if( Integer.parseInt(YRresult) > 40 ) font_size = 15;
					else  font_size = 10;
					
					EveMenuWin window = new EveMenuWin();
					window.frame.setVisible(true);
					window.frame.setTitle("EveKiosk-AgeDetect FontSize[" + font_size +"]");
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	protected EveMenuWin() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {
		
		// 전체 메뉴 조회
		MenuDAO menuDao = new MenuDAO();
		try {
			menuDao.menuList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		menu_cnt = MenuDAO.basic_menu_cnt;   // 기본메뉴 갯수		
		
		frame = new JFrame();
		frame.setBounds(0,0, 800, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
	
		JPanel panel_menuList = new JPanel();
		panel_menuList.setBackground(new Color(255, 255, 255));
		panel_menuList.setSize(new Dimension(770, 480));
		panel_menuList.setPreferredSize(new Dimension(770, 480));

		scrollPane_menuList = new JScrollPane(panel_menuList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		panel_menuList.setLayout(new GridLayout(0, 3, 30, 10));
		
		scrollPane_menuList.setBackground(new Color(255, 255, 255));
		scrollPane_menuList.setBounds(7, 5, 772, 490);
		scrollPane_menuList.setAutoscrolls(true);

		frame.getContentPane().add(scrollPane_menuList);
		
		makeMenuListPanel( panel_menuList );

		// 버튼 영역 패널 지정
		JPanel panel_button = new JPanel();
		panel_button.setBounds(8, 512, 770, 75);
		panel_button.setPreferredSize(new Dimension(770, 80));
		panel_button.setLayout(new GridLayout(1, 1, 100, 20));
		
		frame.getContentPane().add(panel_button);
		
		JButton btnAI = new JButton();
		
		btnAI.setBackground(new Color(255, 128, 128));
		btnAI.setForeground(new Color(0, 0, 0));
		btnAI.setFont(new Font("굴림", Font.BOLD, font_size+5));
		btnAI.setBorder(new BevelBorder(BevelBorder.RAISED, new Color(255, 128, 64), new Color(255, 128, 64), new Color(64, 128, 128), new Color(64, 128, 128)));
		btnAI.setText("음성인식");
		btnAI.setIcon(new ImageIcon("./images/mic.png"));
		
		btnAI.setBorderPainted(true);
		btnAI.setContentAreaFilled(true);
		btnAI.setFocusPainted(true);

		//btnAI.setBorderPainted(true);
		panel_button.add(btnAI);
		
		btnPayment = new JButton("결 제");
		btnPayment.setBackground(new Color(191, 191, 255));
		btnPayment.setFont(new Font("굴림", Font.BOLD, font_size+5));
		btnPayment.setBorderPainted(true);
		btnPayment.setContentAreaFilled(true);
		btnPayment.setFocusPainted(true);
		panel_button.add(btnPayment);
		
		btnDelete = new JButton("삭 제");
		btnDelete.setBackground(new Color(191, 191, 128));
		btnDelete.setFont(new Font("굴림", Font.BOLD, font_size+5));
		btnDelete.setBorderPainted(true);
		btnDelete.setContentAreaFilled(true);
		btnDelete.setFocusPainted(true);
		panel_button.add(btnDelete);
		
		// 음성주문, 결제 버튼 생성
		btnAI.setActionCommand("SpeechRec");
		btnAI.addActionListener(this);
		//btnAI.setEnabled(false);
		
		btnPayment.setActionCommand("Payment");
		btnPayment.addActionListener(this);
		btnPayment.setEnabled(false);
		
		btnDelete.setActionCommand("DeleteMenu");
		btnDelete.addActionListener(this);
		btnDelete.setEnabled(false);
		
		
		////////////////////////////////////////////
		// 하단 주문 내역 테이블 패널 지정
		JPanel panel_orderList = new JPanel();
		panel_orderList.setBounds(8, 597, 770, 160);
		panel_orderList.setPreferredSize(new Dimension(770, 155));
		
		panel_orderList.setLayout(new GridLayout(1, 1));
		frame.getContentPane().add(panel_orderList);
		
		makeSelectedTablePanel( panel_orderList );
	}
	
	/*
	 * 메뉴 목록을 Panel 인에 Jbutton, JLabel의 형태로 생성하는 함수
	 * @param panel_menuList
	 */
	protected void makeMenuListPanel(JPanel p_panel_menuList) {
				
		JPanel [] iPn 	= new JPanel[menu_cnt];
		JButton [] iBtn = new JButton[menu_cnt];  //
		JLabel [] iLbl 	= new JLabel[menu_cnt];
		String name 	= "";
		
		
		for(int i=0;i< menu_cnt ;i++) {		
			iPn[i] = new JPanel();
			iPn[i].setBackground(new Color(255, 255, 255));
			iPn[i].setLayout(new GridLayout(2, 0));

			//반복문 및 배열을 이용해서 버튼에 메뉴 이름 및 가격 입력                 
			name = MenuDAO.all_menu_array[i][4] +" "+ MenuDAO.all_menu_array[i][8]+"원";  

			iBtn[i] = new JButton();
			iBtn[i].setIcon(new ImageIcon("./images/"+MenuDAO.all_menu_array[i][5]));
			iBtn[i].setLayout(null);
			iBtn[i].setPreferredSize(new Dimension(300,300));
			
			iBtn[i].setBorderPainted(true);
			iBtn[i].setContentAreaFilled(true);
			iBtn[i].setFocusPainted(true);
			iBtn[i].setBackground(new Color(255, 255, 255));
			
			iBtn[i].setActionCommand("SelectedMenu_"+i);
			iBtn[i].addActionListener(this);
			iPn[i].add(iBtn[i]);
			
			iLbl[i] = new JLabel();
			iLbl[i].setText(name);
			iLbl[i].setLayout(null);

			iLbl[i].setFont(new Font("굴림", Font.BOLD, font_size+5));
			iLbl[i].setHorizontalTextPosition(SwingConstants.CENTER);
			iLbl[i].setHorizontalAlignment(SwingConstants.CENTER);

			iLbl[i].isForegroundSet();
			iLbl[i].setMinimumSize(new Dimension(200, 20));
			iLbl[i].setMaximumSize(new Dimension(200, 25));
			iLbl[i].setPreferredSize(new Dimension(200,20));
			iPn[i].add(iLbl[i]);

			p_panel_menuList.add(iPn[i]);
		}//for
	}
	
	/*
	 * 주문내역 테이블 생성하는 함수 DefaultTableModel, JTable
	 * @param p_panel_orderList
	 */
	protected void makeSelectedTablePanel(JPanel p_panel_orderList ) {
		
		String[] columnNames = {"품명", "가격","수량" };
		Object[][] data = new Object[0][3];
		dtable_list = new DefaultTableModel(data, columnNames);
		
		jtable_sel = new JTable(dtable_list);
		jtable_sel.setFont(new Font("굴림", Font.BOLD, font_size+3));
		jtable_sel.setRowHeight(font_size+8);
		jtable_sel.setPreferredSize(new Dimension(770, 80));
		jtable_sel.setFillsViewportHeight(true);
		JScrollPane scrollPane_table = new JScrollPane(jtable_sel);
		scrollPane_table.setFont(new Font("굴림", Font.PLAIN, font_size+3));
		scrollPane_table.setAutoscrolls(true);
		p_panel_orderList.add(scrollPane_table);
		
		// Cell Align 
		DefaultTableCellRenderer celAlignCenter = new DefaultTableCellRenderer();
		DefaultTableCellRenderer celAlignRight = new DefaultTableCellRenderer();
		celAlignCenter.setHorizontalAlignment(JLabel.CENTER);
		celAlignRight.setHorizontalAlignment(JLabel.CENTER);
		
		// Cell Width & Align
		jtable_sel.getColumn("품명").setPreferredWidth(300);
		jtable_sel.getColumn("품명").setCellRenderer(celAlignCenter);
		jtable_sel.getColumn("수량").setPreferredWidth(100);
		jtable_sel.getColumn("수량").setCellRenderer(celAlignRight);
		jtable_sel.getColumn("가격").setPreferredWidth(100);
		jtable_sel.getColumn("가격").setCellRenderer(celAlignCenter);
	}
	

	/*
	 * 각 이벤트를 처리하는 함수
	 */
	public void actionPerformed(ActionEvent e) {
		String strArr[] = new String[3];
		
		int selItem_idx=0;
		// TODO Auto-generated method stub
		System.out.println("Select Menu ["+ e.getActionCommand() +"]");
		
		if( "SpeechRec".equals(e.getActionCommand()) ) {
			System.out.println("SpeechRec CallBack!!!!");
			
			
			try {
				new GoTTS();
				GoTTS.main("주문하실 품목을 말씀해 주세요");

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			airec_try();
			
		}
		else if ( "Payment".equals(e.getActionCommand()) ) {
			order_proc();
			if( dtable_list.getRowCount() == 0 ) { 
				btnPayment.setEnabled(false);
				btnDelete.setEnabled(false);
			}else {
				btnPayment.setEnabled(true);
				btnDelete.setEnabled(true);
			}
			
		}else if( "DeleteMenu".equals(e.getActionCommand()) ){
			// 하단 선택한 목록에서 마우스 클릭후 삭제 버튼 클릭시 해당 데이터를 테이블에서 삭제하는 CallBack
			int selectedrow_idx = jtable_sel.getSelectedRow();
			//System.out.println("Delete Row Index ["+ selectedrow_idx +"]");
			
			// 1개 이상 주문목록이 있고 주문한 목록을 선택하지 않았을때 끝에서부터 삭제 
			if( selectedrow_idx == -1 &&   dtable_list.getRowCount() > 0 ) {
				dtable_list.removeRow(dtable_list.getRowCount()-1);
			}else if( selectedrow_idx > -1 ) {
				// 주문목록에서 선택적 삭제를 할때
				dtable_list.removeRow(selectedrow_idx); 
			}
			
			// 주문목록이 모두 비었으면 주문버튼과 삭제버튼 비활성
			if( dtable_list.getRowCount() == 0 ) { 
				btnPayment.setEnabled(false);
				btnDelete.setEnabled(false);
			}
			
		}
		else {
			// 메뉴들 목록 버튼을 클릭했을때 dtable 목록에 추가하는 CallBack ( 메뉴명, 가격, 수량은 1로 고정 )
			
			String s_strArr[] = e.getActionCommand().split("_");
			selItem_idx = Integer.parseInt( s_strArr[1].toString() ); 
			strArr[0]= MenuDAO.all_menu_array[selItem_idx][4]; // 메뉴
			strArr[1]= MenuDAO.all_menu_array[selItem_idx][8]; // 가격
			strArr[2]="1";                                     // 수량

			dtable_list.addRow(strArr);
			
			if( dtable_list.getRowCount() > 0 ) { 
				btnPayment.setEnabled(true);
				btnDelete.setEnabled(true);
			}
		}
		
		if( dtable_list.getRowCount() > 0 ) { 
			btnPayment.setEnabled(true);
			btnDelete.setEnabled(true);
		}
	}
	
	public static int tableInsert_byAI(String p_strArr) {
		
		String [] strArr= new String[3];
		int f_idx =-1;
		for(int idx=0; idx < MenuDAO.basic_menu_cnt; idx++ ) {
			
			if( MenuDAO.all_menu_array[idx][4].toString().contains(p_strArr) || p_strArr.contains( MenuDAO.all_menu_array[idx][4].toString() ) )
			//if( p_strArr.equals( MenuDAO.all_menu_array[idx][4].toString()) )
			{
				f_idx = idx;
				strArr[0]= MenuDAO.all_menu_array[idx][4]; // 메뉴
				strArr[1]= MenuDAO.all_menu_array[idx][8]; // 가격
				strArr[2]="1";
				dtable_list.addRow(strArr);
				System.out.printf("***** 선택 메뉴 목록에 추가 ****");
				dtable_list.fireTableStructureChanged();
				dtable_list.fireTableDataChanged();
				
				break;
			}
		}
		return f_idx;

	}
	/////// order_proc()
	// 주문 내역 저장 
	
	public void order_proc() {
		int rowcnt =0;
		rowcnt = jtable_sel.getRowCount();
		
		if( rowcnt == 0 ) {
			JOptionPane.showMessageDialog(null, "주문 내역이 없습니다\n주문하실 품목을 선택하여 주시기 바랍니다");
			return;
		}
		for (int i=0;i< rowcnt;i++) {
			System.out.println("["+jtable_sel.getValueAt(i, 0)+"]["+jtable_sel.getValueAt(i, 1)+"]["+jtable_sel.getValueAt(i, 2)+"]");
			orderProduct.add( jtable_sel.getValueAt(i, 0).toString() );
		}
		//cardPaymentSceneImage.setVisible(true);
		//insertCardButton.setVisible(true);
		int ret =0;
		try {
			ret= MenuDAO.final_Order(orderProduct);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if( ret >0 ) {
			orderProduct.clear();
			JOptionPane.showMessageDialog(null, "결제가 완료되었습니다");
			dtable_list.setRowCount(0); // 주문내역 테이블 초기화
		}else {
			JOptionPane.showMessageDialog(null, "결제 처리중 오류가 발생하였습니다 관리자에게 문의하시기 바랍니다");
		}
	}
	
	public void airec_try() {
		
		String ret_Str ="";
		try {
			ret_Str = ai_recCheck();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if( ret_Str.length() > 2 ) {
			System.out.println("*** 음성인식 결과 찾기 리턴 [" +  ret_Str +"]");
			
			String s_strArr[] = ret_Str.split("_"); 
			
			if( Integer.parseInt(s_strArr[0].toString()) == 1 ) {
				System.out.println("*** 음성인식 결과를 메뉴에서 찾은 최종 결과 ["+ s_strArr[1].toString() +"]");
				EveMenuWin.tableInsert_byAI(s_strArr[1].toString());
				
			}else if( Integer.parseInt(s_strArr[0].toString()) == 2 ) {
				// 결제 요청
				System.out.println("*** 결제 요청 *****");
				order_proc();
				
			}else {
				System.out.println("*** 음성인식 결과를 메뉴에서 찾은 최종 없음");
				
			}
		}
	}
	
	public String ai_recCheck() throws Exception {
		
		String smrec  ="";
		String rec_end ="";
		String strRec ="";
	
		
		int w_check =0;

		try {
			smrec = DetectIntentTexts.streamingMicRecognize();
			
			if( smrec.length() > 0 ) {
				new Listener(0,smrec);
				rec_end = smrec.replaceAll("\\s+","");
				        				
				for(int idx=0; idx < MenuDAO.basic_menu_cnt; idx++ ) {
	        		//System.out.println("*** Find["+rec_end+"]");
	        		if( MenuDAO.all_menu_array[idx][4].toString().contains(rec_end.substring(0, 2)) ) {
	        		//if( rec_end.equals( MenuDAO.all_menu_array[idx][4].toString()) ) {
	        			System.out.println("*** Find In["+MenuDAO.all_menu_array[idx][4].toString()+"]");
	        			strRec  =  MenuDAO.all_menu_array[idx][4].toString();
	        			w_check = 1;	        			
	        			break;
	        		}else w_check =0;
	            }
	        	
	        	if( rec_end.equals("결제") || rec_end.equals("결재") ) {
	        		strRec ="결제";
	        		w_check =2;
	        		//break;
	        	}
  			}       			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("*** streamingMicRecognize Error ["+ e.toString() +"]");
			e.printStackTrace();
			w_check =0;
			//break;
		} finally {
			
		}
		return (Integer.toString(w_check)+"_"+strRec);   // 성공여부_주문메뉴이름(결제 포함) 
		
	}
	
}
