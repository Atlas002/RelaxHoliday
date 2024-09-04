import com.toedter.calendar.JDateChooser;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Date;

public class fordate {
    private JPanel datepanel;
    private JPanel mainpanel;
    private JButton button1;
    JDatePickerImpl datePicker;

    public fordate() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date selectedDate = (Date) datePicker.getModel().getValue();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

// Format the selectedDate into a String
                String dateString = dateFormat.format(selectedDate);
                System.out.println(selectedDate);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("mytestform");
        frame.setSize(600,200);
        frame.setContentPane(new fordate().mainpanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        datepanel=new JPanel();
        // TODO: place custom component creation code here
        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");

        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());


       datepanel.add(datePicker);
    }

private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

    private final String pattern = "yyyy-MM-dd";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormat.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormat.format(cal.getTime());
        }
        return "";
    }}}