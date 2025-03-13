package View;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.lang.Thread.UncaughtExceptionHandler;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import com.xuggle.ferry.Logger;
import com.xuggle.ferry.Logger.Level;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
 
/**
 * 
 * Proof of concept of how to handle webcam video stream from Java
 * 
 * 
 * 
 * @author Bartosz Firyn (SarXos)
 * 
 */
public class Test_MainFrame extends JFrame implements Runnable, WebcamListener, WindowListener, UncaughtExceptionHandler,
        ItemListener, WebcamDiscoveryListener, ActionListener {
    /**
    * 
    */
    private Webcam webcam = null;
    private WebcamPanel panel = null;
    private WebcamPicker picker = null;
    private JButton picture_button;
    private JButton record_button;
    private JButton stop_button;
    private static final Logger LOG = Logger.getLogger(Test_MainFrame.class.getName());
    private volatile boolean running = false;
    Thread captureThread;
    private int i = 0;
 
    @Override
    public void run() {
        Webcam.addDiscoveryListener(this);
        setTitle("카메라 연동");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        picture_button = new JButton("사진찍기");
        record_button = new JButton("녹화시작");
        stop_button = new JButton("녹화종료");
        picture_button.addActionListener(this);
        record_button.addActionListener(this);
        stop_button.addActionListener(this);
        addWindowListener(this);
        picker = new WebcamPicker();
        picker.addItemListener(this);
        webcam = picker.getSelectedWebcam();
        if (webcam == null) {
            System.out.println("카메라를 찾을수 없다...");
            System.exit(1);
        }
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.addWebcamListener(Test_MainFrame.this);
        panel = new WebcamPanel(webcam, false);
        panel.add(picker, BorderLayout.NORTH);
        panel.add(picture_button, BorderLayout.NORTH);
        panel.add(record_button, BorderLayout.NORTH);
        panel.add(stop_button, BorderLayout.NORTH);
        // panel.setFPSDisplayed(false);
        add(panel, BorderLayout.CENTER);
        pack();
        setVisible(true);
        Thread t = new Thread() {
            @Override
            public void run() {
                panel.start();
            }
        };
        t.setName("example-starter");
        t.setDaemon(true);
        t.setUncaughtExceptionHandler(this);
        t.start();
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Test_MainFrame());
    }
 
    @Override
    public void webcamOpen(WebcamEvent we) {
        System.out.println("카메라 열렸다.");
    }
 
    @Override
    public void webcamClosed(WebcamEvent we) {
        System.out.println("카메라 닫혔다.");
    }
 
    @Override
    public void webcamDisposed(WebcamEvent we) {
        System.out.println("카메라 꺼졌다.");
    }
 
    @Override
    public void webcamImageObtained(WebcamEvent we) {
        // do nothing
    }
 
    @Override
    public void windowActivated(WindowEvent e) {
    }
 
    @Override
    public void windowClosed(WindowEvent e) {
        webcam.close();
    }
 
    @Override
    public void windowClosing(WindowEvent e) {
    }
 
    @Override
    public void windowOpened(WindowEvent e) {
    }
 
    @Override
    public void windowDeactivated(WindowEvent e) {
    }
 
    @Override
    public void windowDeiconified(WindowEvent e) {
        System.out.println("webcam viewer resumed");
        panel.resume();
    }
 
    @Override
    public void windowIconified(WindowEvent e) {
        System.out.println("webcam viewer paused");
        panel.pause();
    }
 
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.err.println(String.format("Exception in thread %s", t.getName()));
        e.printStackTrace();
    }
 
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() != webcam) {
            if (webcam != null) {
                panel.stop();
                remove(panel);
                webcam.removeWebcamListener(this);
                webcam.close();
                webcam = (Webcam) e.getItem();
                webcam.setViewSize(WebcamResolution.VGA.getSize());
                webcam.addWebcamListener(this);
                System.out.println("selected " + webcam.getName());
                panel = new WebcamPanel(webcam, false);
                // panel.setFPSDisplayed(false);
                add(panel, BorderLayout.CENTER);
                pack();
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        panel.start();
                    }
                };
                t.setName("example-stoper");
                t.setDaemon(true);
                t.setUncaughtExceptionHandler(this);
                t.start();
            }
        }
    }
 
    @Override
    public void webcamFound(WebcamDiscoveryEvent event) {
        if (picker != null) {
            picker.addItem(event.getWebcam());
        }
    }
 
    @Override
    public void webcamGone(WebcamDiscoveryEvent event) {
        if (picker != null) {
            picker.removeItem(event.getWebcam());
        }
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == picture_button) {
            System.out.println("사진 찰칵");
            // Webcam.setAutoOpenMode(true);
            BufferedImage image = Webcam.getDefault().getImage();
            // save image to JPG file
            try {
                ImageIO.write(image, "JPG", new File("녹화사진.jpg"));
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        if (e.getSource() == record_button) {
            // sarxos: make sure to disable button, otherwise it will
            // fail when clicked twice
            record_button.setEnabled(true);
            stop_button.setEnabled(true);
            running = true;
            captureThread = new Thread() {
                @Override
                public void run() {
                    File file = new File("녹화파일.ts");
                    IMediaWriter writer = ToolFactory.makeWriter(file.getName());
                    Dimension size = WebcamResolution.VGA.getSize();
                    writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);
                    webcam.open();
                    long start = System.currentTimeMillis();
                    System.out.println("녹화시작");
                    try {
                        while (running) {
                            // System.out.println("Capture frame " + i);
                            BufferedImage image = ConverterFactory.convertToType(webcam.getImage(),
                                    BufferedImage.TYPE_3BYTE_BGR);
                            IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
                            IVideoPicture frame = converter.toPicture(image,
                                    (System.currentTimeMillis() - start) * 1000);
                            // frame.setKeyFrame(i == 0);
                            frame.setQuality(0);
                            writer.encodeVideo(0, frame);
                            // 10 FPS
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                // sarxos: in case of interrupted exception,
                                // just break,
                                // return or throw, it's not severe exception,
                                // but you shall not
                                // ignore it because your application can hang
                                // in some rare
                                // cases, especially when it's multithreaded
                                // LOG.log(Level.SEVERE, null, ex);
                                break;
                            }
                        }
                    } finally {
                        writer.close(); // sarxos: don't forgot to close writer
                        // when done with recording
                    }
                    System.out.println("Video recorded in file: " + file.getAbsolutePath());
                }
            };
            // sarxos: if you are using worker threads (threads which perform
            // some
            // abstract parallel tasks) make sure to use daemon threads,
            // otherwise,
            // your app can be blocked when there are more than one non-daemon
            // thread
            // running which does not want to join
            captureThread.setDaemon(true);
            captureThread.start();
        } else if (e.getSource() == stop_button) // stop
        {
            running = false;
            System.out.println("녹화 끝");
            try {
                captureThread.join();
            } catch (InterruptedException ex) {
            }
        }
    }
}