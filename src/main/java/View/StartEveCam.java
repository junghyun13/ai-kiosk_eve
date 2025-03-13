package View;

import java.awt.Dimension;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;

import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import javax.swing.BorderFactory;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.SwingConstants;


public class StartEveCam extends JFrame implements Runnable, ThreadFactory {

	private static final long serialVersionUID = 6441489157408381878L;

	private Executor executor = Executors.newSingleThreadExecutor(this);

	private Webcam webcam = null;
	private WebcamPanel panel = null;
	private JTextArea textarea = null;
	private String QRresult = "";
	Dimension size =null;
	JLabel lblArmLabel = null;

	public StartEveCam() {
		super();
		setVisible(true);
		setTitle("Kiosk_AgeDetectCamera"); 

	    getContentPane().setLayout(null); 
	    getContentPane().setBackground(Color.white);

	    setTitle("EveKiosk-AgeDetect");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   
	    
	    //Dimension size = WebcamResolution.QVGA.getSize();
	    size = WebcamResolution.QVGA.getSize();

	    webcam = Webcam.getWebcams().get(0);
	    webcam.setViewSize(size);
	    
	    panel = new WebcamPanel(webcam);
	    
	    panel.setBounds(40, 200, 500, 400);
	    panel.setFPSDisplayed(true);

	    textarea = new JTextArea();
	    textarea.setEnabled(false);
	    textarea.setEditable(false);
	    textarea.setBounds(121, 625, 350, 23); // setting bounds manually
	    textarea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); 
	    textarea.setVisible(false);
	    
	    lblArmLabel = new JLabel("얼굴을 카메라로 인식하여 주세요.");
	    lblArmLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    lblArmLabel.setForeground(new Color(0, 0, 255));
	    lblArmLabel.setFont(new Font("굴림", Font.BOLD, 22));
	    lblArmLabel.setBounds(49, 20, 501, 238);
	    getContentPane().add(lblArmLabel);

	    getContentPane().add(panel);
	    getContentPane().add(textarea);
	    
	    JButton btnNewButton_2 = new JButton("주문하기");
	    btnNewButton_2.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		String QRresult = textarea.getText();
	    		webcam.close();
	    		//EveMenuWin m = new EveMenuWin();
				EveMenuWin.main("", QRresult);		
				setVisible(false);
	    	}
	    });
	    btnNewButton_2.setBackground(new Color(153, 102, 204));
	    btnNewButton_2.setForeground(new Color(255, 255, 255));
	    btnNewButton_2.setFont(new Font("굴림", Font.PLAIN, 25));
	    btnNewButton_2.setBounds(0, 732, 585, 80);
	    getContentPane().add(btnNewButton_2);
	    
	    pack();
	    this.setSize(600,850); // Setting size after calling pack()
	    setVisible(true);
	    executor.execute(this);
	}
	
	@Override
	public void run() {
		File file = new File("cam-eve.ts");

		IMediaWriter writer = ToolFactory.makeWriter(file.getName());
		//Dimension size = WebcamResolution.QVGA.getSize();

		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);

		webcam.open(true);

		long start = System.currentTimeMillis();
		
		try {

			for (int i = 0; i < 20; i++) {
	
				BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
				IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
	
				IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
				//frame.setKeyFrame(i == 0);
				frame.setQuality(0);
				writer.encodeVideo(0, frame);
	
				// 10 FPS
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}finally {
            writer.close(); // sarxos: don't forgot to close writer
            // when done with recording
        }
		
		System.out.println("Video recorded in file: " + file.getAbsolutePath());
		lblArmLabel.setText("나이 측정중입니다... 잠시만 기다려 주세요!!!");

		run_pythonScript();
	}
	
	

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, "AgeDetecting");
		t.setDaemon(true);
		return t;
	}

	public static void main(String[] args) {
		new StartEveCam();
	
	}
	// QR Code 인식 결과
	public String getQRresult() {
		return QRresult;
	}
	
	public int run_pythonScript() {
    	
		int f_ages = 0;
        try {
            // 파이썬 스크립트의 경로
            String pythonScriptPath = "C:\\work\\eclipse-workspace\\opencv-face-detector-master\\facenet-eve.py"; 
            
            // 파이썬 실행 명령, 스크립트 경로
            String[] cmd = new String[2];
            cmd[0] = "python";  // 파이썬 실행명령. 만약 python3을 사용한다면 "python3"로 변경해야 합니다.
            cmd[1] =  pythonScriptPath;

            // 프로세스 빌더를 사용하여 파이썬 스크립트를 실행합니다.
            ProcessBuilder pb = new ProcessBuilder(cmd);
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            int exitval = process.waitFor();

            // 파이썬 스크립트의 출력 내용을 읽어와 자바 콘솔에 출력
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(),"UTF-8"));
      
            String line;
            int ret_ages =0;

            while ((line = in.readLine()) != null) {
                System.out.println(line); // 표준출력에 쓴다
                
                if( line !=null ) {
                	ret_ages = Integer.parseInt(line);
                	if( f_ages <  ret_ages )
                		f_ages = ret_ages;
                }
            }
            System.out.println("Result Age["+ f_ages +"]"); 
         
            if(exitval !=0){
                //비정상종료
                System.out.println("비정상종료");
            }   
            
            //if( f_ages == null || f_ages.)
            
			textarea.setText( Integer.toString(f_ages) );
			lblArmLabel.setText("추정나이는 "+ f_ages +"대입니다 !!!");
			JOptionPane.showMessageDialog(null, f_ages+"대 나이로 측정되었습니다\n주문화면으로 이동하시기 바랍니다");
			webcam.close();
			
			EveMenuWin.main("", Integer.toString(f_ages));		
			setVisible(false);
			

        } catch(Exception e) {
            e.printStackTrace();
        }
        return f_ages;
    }
}