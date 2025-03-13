package controller;

import java.util.Hashtable;

import View.Chatt;

public class ResultMenu {
   private String Chocie;
   private String SelectItem;
   private Hashtable<Integer, String> res = new Hashtable<Integer,String>();
   //빅맥에 사이드 메뉴는 감자튀김와 음료는 콜라 맞으신가요?
    //빅맥 단품 맞으신가요?
   
   
   
   public ResultMenu(Chatt ch) {
	      Chocie = ch.kioSay();
	      SelectItem = ch.userSay();
	      System.out.println("초키: "+Chocie);
	      System.out.println("위와 같이 주문한 내용이 맞습니까 ?");
   }

   /* 사용자 선택한 메뉴 저장하는 공간 */
   public Hashtable<Integer,String> choice() {
       
	      if(SelectItem.matches(".*아메리카노.*")){
	    	res.put(1, "아메리카노");  
	      }
	      if(SelectItem.matches(".*카페라테.*")){
	    	  res.put(1, "카페라테");
	      }
	      if(SelectItem.matches(".*소세지빵.*")){
	    	  res.put(2, "소세지빵");
	      }
	      if(SelectItem.matches(".*크로크무슈.*")){
	    	  res.put(2, "크로크무슈");
	      }
	      /*
	      if(Chocie.matches(".*콜라.*")){
	    	  res.put(3,"콜라");
	      }
	      if(Chocie.matches(".*사이다.*")){
	    	  res.put(3, "사이다");
	      }
	      */
	      System.out.println("***** Hash ******");
	      System.out.println("SelectItem["+ SelectItem +"]");
	      System.out.println("res.size["+ res.size() +"]");
	      System.out.println("***** Hash ******");
	      return res;
	   }   
   //라벨에 들어가는 부분
   public Hashtable<Integer,String> label() {
       
      if(Chocie.matches(".*아메리카노.*")){
    	res.put(1, "아메리카노");  
      }
      if(Chocie.matches(".*카페라테.*")){
    	  res.put(1, "카페라테");
      }
      if(Chocie.matches(".*소세지빵.*")){
    	  res.put(2, "소세지빵");
      }
      if(Chocie.matches(".*크로크무슈.*")){
    	  res.put(2, "크로크무슈");
      }
      /*
      if(Chocie.matches(".*콜라.*")){
    	  res.put(3,"콜라");
      }
      if(Chocie.matches(".*사이다.*")){
    	  res.put(3, "사이다");
      }
      */
      System.out.println("***** Hash ******");
      System.out.println("Chocie["+ Chocie +"]");
      System.out.println("res.size["+ res.size() +"]");
      System.out.println("***** Hash ******");
      return res;
   }
   
   public String sendTODB() {
	   return Chocie;
   }
   //사진
   public String photo() {
      int photo = 0; // 1 = 빅맥세트,2 = 불고기 세트 ,3 = 새우 세트  , 4 = 빅맥 단품, 5 = 불고기 단품, 6 = 새우 단품
      
      if(Chocie.matches(".*빅맥.*")){
         photo = 4;
      }
      if(Chocie.matches(".*불고기버거.*")){
         photo = 5;
      }
      if(Chocie.matches(".*새우버거.*")){
         photo = 6;
      }
      if(Chocie.matches(".*감자튀김.*")){
         photo -= 3;
      }
      if(Chocie.matches(".*치즈스틱.*")){
         photo -= 3;
      }
      
      if(photo == 1) {
    	  return "../image/set_1.jpg";
      }
      
      if(photo == 2) {
    	  return "../image/set_3.jpg";
      }
      if(photo == 3) {
    	  return "../image/set_2.jpg";
      }
      if(photo == 4) {
    	  return "../image/hamburger_1.jpg";
      }
      if(photo == 5) {
    	  return "../image/hamburger_3.jpg";
      }
      if(photo == 6) {
    	  return "../image/hamburger_2.jpg";
      }
      return "";
   }
} 