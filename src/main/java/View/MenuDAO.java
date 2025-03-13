package View;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;


public class MenuDAO {

	
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost:3306/kiosk?allowPublicKeyRetrieval=true&serverTimezone=UTC&useSSL=false";

	private static final String USER = "root"; // DB ID
	private static final String PASS = "1234"; // DB 패스워드
	public static Connection con = null;
	public static String[][] tab_array; // = new String[][9]; // tab 5개, menu갯수 20, 메뉴의 컬럼수 5 
	public static String[][] all_menu_array; // = new String[][9]; // tab 5개, menu갯수 20, 메뉴의 컬럼수 5 
	public static String[][] ai_menu_array; // = new String[][9]; // tab 5개, menu갯수 20, 메뉴의 컬럼수 5 
	public static int tab_cnt =0; // tap = 키테고리 갯수
	public static int basic_menu_cnt =0; // 기본 등록된 메뉴 갯수
	public static int all_menu_cnt =0; // 추천메뉴 포함 전체 메뉴 갯수
	
	/** DB연결 메소드 */
	public static Connection getConn() {

		try {
			Class.forName(DRIVER); // 1. 드라이버 로딩
			con = DriverManager.getConnection(URL, USER, PASS); // 2. 드라이버 연결
			//System.out.println("연결 성공");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	// 텝처리를 위해 대분류 종류를 조회 ( 커피, 음료, 스무디, 디저트, 추천 )
	public void categoryList() throws SQLException {
		
		String sql = "";
		PreparedStatement pstmt;
		
		
		Connection con = getConn();
		// 전체 메뉴 갯수 구하기 for arrays 저장소 지정하기 위해
		sql = "select count(distinct category) from  kiosk.menu";
		pstmt = con.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery(); // 쿼리 날림
		rs.next();
		tab_cnt = rs.getInt(1); 				///////////// 추천 메뉴를 위해 1 추가
		tab_array = new String [tab_cnt+1][3]; 	// tab_idx, category, sub_idx count
		
		System.out.println("Tab-Category Select["+tab_cnt+"]");
		
		sql = " select tab_idx, category, count(sub_idx) from  kiosk.menu group by tab_idx, category";
		pstmt = con.prepareStatement(sql);
		rs = pstmt.executeQuery(); // 쿼리 날림
		
		int w_idx=0;
		while( rs.next() ) {
			//tabidx = rs.getInt(1);
			//tab_array[w_idx][0]= Integer.toString(tabidx);
			tab_array[w_idx][0]= rs.getString(1); // tab_idx
			tab_array[w_idx][1]= rs.getString(2); // category
			tab_array[w_idx][2]= rs.getString(3); // sub_count
			w_idx++;
		}
		//tab_cnt++;
		tab_array[tab_cnt][0] = Integer.toString(tab_cnt);
		tab_array[tab_cnt][1]= "추천"; // category
		tab_array[tab_cnt][2]= "10";    // 추천메뉴는 임시로 10개 배정함 추후 수정함
		
		/*
		for(int i=0; i< (tab_cnt+1) ; i++ ){
			for(int j=0; j < 3 ; j++ ) {
				System.out.println("AI Category Select["+i+"]["+j+"]["+tab_array[i][j]+"]");
			}
		}
		
		System.out.println("menu_cnt["+ tab_cnt +"] menu_array.length["+tab_array.length+"]");
		*/
		
		con.close();
	}
	// 전체 메뉴를 조회한다
	public void menuList() throws SQLException {
		
		String sql = "";
		PreparedStatement pstmt;
		
		Connection con = getConn();
		// 전체 메뉴 갯수 구하기 for arrays 저장소 지정하기 위해
		sql = "SELECT count(*) FROM kiosk.menu";
		pstmt = con.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery(); // 쿼리 날림
		rs.next();
		basic_menu_cnt = rs.getInt(1);
		all_menu_array = new String [basic_menu_cnt+10][10]; // 기본등록 메뉴 + 추천메뉴 예상(10개) 공간 확보
		
		sql = "SELECT idx, tab_idx, sub_idx, category, menu, imagename, allergic, age, price, calorie FROM kiosk.menu order by tab_idx, sub_idx";
		pstmt = con.prepareStatement(sql);
		rs = pstmt.executeQuery(); // 쿼리 날림
		
		int w_idx=0;// 배열 idx
		
		while( rs.next() ) {
			all_menu_array[w_idx][0]= Integer.toString(rs.getInt(1)); // 메뉴 idx
			all_menu_array[w_idx][1]= Integer.toString(rs.getInt(2)); // tab_idx
			all_menu_array[w_idx][2]= Integer.toString(rs.getInt(3)); // sub_idx
			all_menu_array[w_idx][3]= rs.getString(4); // category
			all_menu_array[w_idx][4]= rs.getString(5); // menu
			all_menu_array[w_idx][5]= rs.getString(6); // imagename
			all_menu_array[w_idx][6]= rs.getString(7); // allergic
			all_menu_array[w_idx][7]= rs.getString(8); // age
			all_menu_array[w_idx][8]= Integer.toString(rs.getInt(9)); // price
			all_menu_array[w_idx][9]= rs.getString(10); // calorie	
			
			/*
			for(int i=0; i< 10 ; i++ )
				System.out.println("All Menu Select["+w_idx+"]["+i+"]====["+all_menu_array[w_idx][i]+"]");
				*/
			
			w_idx ++;
		}		
		//System.out.println("basic_menu_cnt["+ basic_menu_cnt +"] all_menu_array.length["+all_menu_array.length+"]");
		con.close();
	}
	
	// QT, 연령대
	public void optmenuList(String p_qr, String p_ages) throws SQLException {
			
			String sql = "";
			PreparedStatement pstmt;
			Connection con = getConn();
			
			sql = "SELECT idx, tab_idx, sub_idx, category, menu, imagename, allergic, age, price, calorie FROM kiosk.menu ";
			if ( p_qr !="" && p_qr.length() > 0 )
				sql += " where allergic not like \'%"+ p_qr + "%\' ";
			if ( p_ages !="" && p_ages.length() > 0 )
				sql += " where age like \'%"+ p_ages + "%\' ";
			
			sql += " order by tab_idx, sub_idx ";
			
			System.out.println("==> optmenuList"+ sql +"]");
			
			pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery(); // 쿼리 날림
			
			int w_idx=basic_menu_cnt;// 기본 메뉴 목록에 이어 추천 메뉴 index 이어서 저장
			
			while( rs.next() ) {
				
				all_menu_array[w_idx][0]= Integer.toString(w_idx+1); // 메뉴 idx 기본 메뉴에 이어서
				all_menu_array[w_idx][1]= "4"; // tab_idx 추천을 위해 4번 이용
				all_menu_array[w_idx][2]= Integer.toString(rs.getInt(3)); // sub_idx
				all_menu_array[w_idx][3]= "추천"; // category 추천을 위해 새로 할당
				all_menu_array[w_idx][4]= rs.getString(5)+"(추천)"; // menu 이름이 중복이 될수 있어서 (추천)을 뒤에 붙임
				all_menu_array[w_idx][5]= rs.getString(6); // imagename
				all_menu_array[w_idx][6]= rs.getString(7); // allergic
				all_menu_array[w_idx][7]= rs.getString(8); // age
				all_menu_array[w_idx][8]= Integer.toString(rs.getInt(9)); // price
				all_menu_array[w_idx][9]= rs.getString(10); // calorie	
				
				/*
				for(int i=0; i< 10 ; i++ )
					System.out.println("All Menu Select["+w_idx+"]["+i+"]====["+all_menu_array[w_idx][i]+"]");
				*/
				w_idx ++;
			}
			all_menu_cnt = w_idx;
			
			//System.out.println("추천 메뉴 겡신전 ["+ tab_array[tab_cnt][2] +"]");
			// 추천 메뉴 갯수 갱신함
			tab_array[tab_cnt][2]= Integer.toString( all_menu_cnt - basic_menu_cnt );
			//System.out.println("추천 메뉴 갱신후 ["+ tab_array[tab_cnt][2] +"]");
			
			//System.out.println("all_menu_cnt["+ all_menu_cnt +"] all_menu_array.length["+all_menu_array.length+"]");
			con.close();
		}
	
	
	//주문 내역 등록 처리
	
	public static int final_Order(ArrayList<String> p_orderProduct) throws SQLException {
		Connection con = getConn();
		con.setAutoCommit(false);
		String sql = "";
		PreparedStatement pstmt;
		int order_idx =0; // 주문번호
		int rSet=0;// SQL return 변수
		
		String[][] order_item = new String[ p_orderProduct.size() ][4];
		String[] tmp_item = new String[3]; // (추천)을 삭제 처리하기 위한 변수
		
		/*
		for (int i = 0; i < p_orderProduct.size(); i++) {
			System.out.println("order["+i+"]["+ p_orderProduct.get(i) + "]");
			order_item[i]=  p_orderProduct.get(i).split( " " );
			System.out.println("1 [" + order_item[i][0] + "]");
			System.out.println("2 [" + order_item[i][1] + "]["+ order_item[i][1].length()+"]");
			System.out.println("3 [" + order_item[i][2] + "]");
		}
		*/
		
		
		
		/* 1. 오늘일자 주문번호 조회 SQL */
		sql = "select a.order_number from orderidx a where a.date = current_date()";
		pstmt = con.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery(); // 쿼리 날림
		
		
		// 1.1 오늘자 주문번호가 조회될때
		if( rs.next() ) {
			// 오늘자 주문번호를 조회하여 +1 시킨후 변수에 저장
			order_idx=rs.getInt(1)+1;
			sql = "update orderidx set order_number = "+order_idx+ " where date = current_date()";
		}else {
			// 1.2 오늘일자의 주문번호를 신규로 생성한다 1
			sql = "insert into orderidx ( date, order_number) values ( current_date(), 1 ) ";
			order_idx=1;
		}
		pstmt = con.prepareStatement(sql);
		rSet = pstmt.executeUpdate(); // 쿼리 날림
			
		if( rSet > 0) {
			System.out.println("주문번호 생성");
		}else {
			System.out.println("주문번호 생성오류 ㅜㅜ");
		}
		
		System.out.println("오늘일자 주문번호 ["+ order_idx +"]");
		
		
		for (int i = 0; i < p_orderProduct.size(); i++) {
			order_item[i]=  p_orderProduct.get(i).split( " " );
			/* 주문내역 저장 */
			sql="insert into kiosk.orderdetail ( date, order_number, menu_idx, order_count, payment_time, order_price ) ";
			sql+="select now(), "+ order_idx +", a.idx, 1, now(), price  from menu a where a.menu like ?";
			
			pstmt = con.prepareStatement(sql);
			
			if ( order_item[i][0].indexOf("(") < 0 )
			{
				tmp_item[0]= order_item[i][0].toString();

			}else {
				tmp_item=  order_item[i][0].split( "\\(" );
				//System.out.println("추천 주문 길이 ["+ tmp_item.length +"]");
			}			
			
			//System.out.println("추천 주문 항목["+tmp_item[0].toString()+"]");
			pstmt.setString(1,"%"+tmp_item[0].toString()+"%");
			
			//System.out.println("Final SQL["+sql+"]");
			
			rSet = pstmt.executeUpdate();
			if(rSet > 0) {
				System.out.println("주문내역 등록 성공");
			}
			else {
				System.out.println("주문내역 등록 실패");
				rSet = -1;
				break;
			}
				
		}
		if( rSet  > 0  ) {
			con.commit();
		}else {
			System.out.println("insert intenal error");
			con.rollback();
		}
		con.close();
		
		return rSet;
	}
	
}
