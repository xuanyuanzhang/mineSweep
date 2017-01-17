import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.Timer; 


public class TimerTest extends JFrame implements ActionListener{
	private JLabel jlTime = new JLabel();  
    private Timer timer;  
  
    public TimerTest() {  
        setTitle("Timer test");  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        setSize(1800, 800);  
        add(jlTime);  
          
        timer = new Timer(500, this);  
        timer.start();  
        setVisible(true);  
    }  
  
    /**  
     * 
     */  
    @Override  
    public void actionPerformed(ActionEvent e) {  
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Date date = new Date();  
        jlTime.setText(format.format(date));  
  
    }  
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TimerTest t = new TimerTest();
	}

}
