package View;

import java.util.ArrayList;
import com.google.cloud.dialogflow.v2.QueryResult;


import controller.DetectIntentTexts;
import controller.Playaudio;

public class Listener {
	public String str;
	public ArrayList<String> texts = new ArrayList<>();
	public Chatt ch = new Chatt();

	Listener(){
		
	}
    public Listener(String str) throws Exception {
		this.str = str;
		Speech(str);
    }
    
   public Listener(int i, String str) throws Exception{
	   this.str = str;
	   if(i == 2 && (str.matches(".*세트.*")||str.matches(".*배트.*"))) {
		   ch.userChatt(str);
	   }
	   else if(i == 2 && str.matches(".*단품.*")) {
		   ch.userChatt(str);
	   }
	   else {
		   Say(str);
	   }
   }
   public void Say(String str) throws Exception{
//		DetectIntentTexts a = new DetectIntentTexts();
			String projectId = "if028-399001";
		//  String sessionId = UUID.randomUUID().toString();
			String sessionId = "101";
			String languageCode = "ko-kr";

			QueryResult res;
			
			texts.clear();
			String set = str;
			texts.add(set);
		
			try {
				res = DetectIntentTexts.detectIntentWithTexttoSpeech(projectId, texts, sessionId, languageCode);
			
				ch.kioChatt(res.getFulfillmentText());
				ch.userChatt(set);
				ch.detectIntent(res.getIntent().getDisplayName());
				new Playaudio().run();
				
				
			}catch(Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
   }
    
	
	public void Speech(String str) throws Exception {
		
	//	DetectIntentTexts a = new DetectIntentTexts();
		String projectId = "if028-399001";
	//  String sessionId = UUID.randomUUID().toString();
		String sessionId = "101";
		String languageCode = "ko-kr";
		QueryResult res;
		
		
		texts.clear();
		String set = str;
		texts.add(set);
		
		try {
			res = DetectIntentTexts.detectIntentWithTexttoSpeech(projectId, texts, sessionId, languageCode);
		
			ch.kioChatt(res.getFulfillmentText());
			ch.userChatt(set);
			ch.detectIntent(res.getIntent().getDisplayName());
			
			new Playaudio().run();
			
		}catch(Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
	}

	public String kiochatt() {
		return ch.kioSay();
	}
	public String userchatt() {
		return ch.userSay();
	}
}