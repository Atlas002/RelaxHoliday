import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class mytestform {
    private JPanel mypanel;

    public mytestform() {
        btnsubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new test2().show();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("mytestform");
        frame.setSize(600,200);
        frame.setContentPane(new mytestform().mypanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

    private JTextField txtName;
    private JButton btnsubmit;
    private JLabel mylabel;
    private JLabel imageopt;


    private void createUIComponents() {
        // TODO: place custom component creation code here

        ImageIcon icon=new ImageIcon("C:\\Users\\maith\\Desktop\\Postgraduate Online Group\\france.jpg");
        imageopt=new JLabel("my image");
        Image img=icon.getImage();
        Image imageScale=img.getScaledInstance(400,100,Image.SCALE_SMOOTH);
        ImageIcon scaleIcon=new ImageIcon(imageScale);
        imageopt.setIcon(scaleIcon);
    }
}















