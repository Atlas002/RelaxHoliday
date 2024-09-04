import javax.swing.*;
import java.awt.*;
import java.sql.*;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.sql.Date.valueOf;

public class RoomGUI {

    private boolean isUserAdmin;
    protected JPanel genPanel;
    private JLabel title;
    private JLabel image;
    private JLabel description;
    private JButton bookButton;
    private JLabel subtitle;
    private JLabel labelName;
    private JPanel datepanel;
    private JLabel price;
    private JLabel RoomName;
    private JLabel NumberGuest;
    private JPanel datepanel2;
    private JLabel CheckIn;
    private JLabel checkOut;
    private JLabel location;
    private JLabel imageLabel;
    JDatePickerImpl datePicker1;
    JDatePickerImpl datePicker2;

    // Method to display an image from Blob in the database
    private void dispImageFromBlob() {
        try {
            // Establishing a connection to the remote database
            //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
            // Query to select the image from the room table based on room_ID
            String query = "SELECT image FROM room WHERE room_ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Globals.roomToDisplay);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Blob imageBlob = resultSet.getBlob("image"); //get the image in a blob from the database
                if (imageBlob != null) {
                    byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                    if (imageBytes != null && imageBytes.length > 0) {
                        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                        BufferedImage image = ImageIO.read(bis);
                        if (image != null) {
                            ImageIcon icon = new ImageIcon(image);
                            imageLabel.setIcon(icon); //display the image
                        } else {
                            System.err.println("The image is null after reading"); //test if problems
                        }
                    } else {
                        System.err.println("Image data is null or empty");
                    }
                } else {
                    System.err.println("Blob image is null");
                }
            } else {
                System.err.println("No image found for the specified room");
            }

            // Closing resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // Constructor
    public RoomGUI() {

        bookButton.setVisible(false);

        if (Globals.roomToDisplay != -1) {
            try {
                // Establishing a connection to the remote database
                //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
                Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );

                // Query to retrieve room information based on room_ID
                String query = "SELECT room_number, price, capacity, location, description FROM room WHERE room_ID = " + Globals.roomToDisplay;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    int capacity = resultSet.getInt("capacity");
                    RoomName.setText("Room :" + resultSet.getInt("room_number"));
                    price.setText("Price : " + resultSet.getInt("price"));
                    description.setText(resultSet.getString("description"));
                    location.setText(resultSet.getString("location"));
                    if (capacity == 1) {
                        NumberGuest.setText("for one person");
                    } else {
                        NumberGuest.setText("for " + capacity + " persons");
                    }
                }

                // Query to check user status for booking access
                query = "SELECT emp_status FROM person WHERE person_ID = " + Globals.userID;
                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    bookButton.setVisible(true);
                    int isUserEmp = resultSet.getInt("emp_status");
                    if (isUserEmp == 1) {
                        bookButton.setText("Update");
                    } else {
                        bookButton.setText("Book");
                    }

                    // ActionListener for book/update button
                    bookButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (isUserEmp == 1) {
                                JFrame frame = new JFrame("Update Room");
                                frame.setContentPane(new UpdateRoom().GenPane);
                                frame.pack();
                                frame.setLocationRelativeTo(null);
                                frame.setVisible(true);
                                JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(genPanel);
                                frameOld.dispose(); // Close the window
                            } else {
                                try {
                                    Date selectedDate1 = (Date) datePicker1.getModel().getValue();
                                    Date selectedDate2 = (Date) datePicker2.getModel().getValue();

                                    if (selectedDate1 == null || selectedDate2 == null) {
                                        JOptionPane.showMessageDialog(null, "Please select both dates.", "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }

                                    LocalDate firstLocalDate = selectedDate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    LocalDate secondLocalDate = selectedDate2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    long numberOfDays = calculateDaysBetween(firstLocalDate, secondLocalDate);

                                    if (numberOfDays == 0) {
                                        JOptionPane.showMessageDialog(null, "Bookings must be at least one day long.", "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }

                                    if (numberOfDays < 0) {
                                        LocalDate temp = firstLocalDate;
                                        firstLocalDate = secondLocalDate;
                                        secondLocalDate = temp;
                                        numberOfDays = calculateDaysBetween(firstLocalDate, secondLocalDate);
                                    }

                                    Globals.checkInDate = firstLocalDate;
                                    Globals.checkOutDate = secondLocalDate;

                                    if (checkForOverlappingBookings(Globals.checkInDate, Globals.checkOutDate)) {
                                        JOptionPane.showMessageDialog(null, "This room is already booked for the selected dates.", "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }

                                    JFrame frame = new JFrame("Booking Checkout");
                                    frame.setContentPane(new Transaction().GenPane);
                                    frame.pack();
                                    frame.setLocationRelativeTo(null);
                                    frame.setVisible(true);
                                    JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(genPanel);
                                    frameOld.dispose(); // Close the window

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    });
                }

                dispImageFromBlob();

                // Close resources
                resultSet.close();
                statement.close();
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("RoomGUI");
        frame.setContentPane(new RoomGUI().genPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Method to create UI components
    private void createUIComponents() {
        datepanel = new JPanel();
        datepanel2 = new JPanel();

        // Properties for both date pickers
        Properties properties = new Properties();
        properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");

        // Create separate UtilDateModel instances for each date picker
        UtilDateModel model1 = new UtilDateModel();
        UtilDateModel model2 = new UtilDateModel();

        // Create JDatePanelImpl instances for each date picker
        JDatePanelImpl datePanel1 = new JDatePanelImpl(model1, properties);
        JDatePanelImpl datePanel2 = new JDatePanelImpl(model2, properties);

        // Create separate JDatePickerImpl instances using the respective JDatePanelImpl instances
        datePicker1 = new JDatePickerImpl(datePanel1, new DateLabelFormatter());
        datePicker2 = new JDatePickerImpl(datePanel2, new DateLabelFormatter());

        // Add the date pickers to their respective panels
        datepanel.add(datePicker1);
        datepanel2.add(datePicker2);

        imageLabel = new JLabel();
    }

    // Method to calculate the number of days between two dates
    public long calculateDaysBetween(LocalDate firstDate, LocalDate secondDate) {
        return ChronoUnit.DAYS.between(firstDate, secondDate);
    }

    // Method to check for overlapping bookings
    private boolean checkForOverlappingBookings(LocalDate checkInDate, LocalDate checkOutDate) throws SQLException {
        String query = "SELECT COUNT(*) FROM booking WHERE room_ID = ? AND check_out_date > ? AND check_in_date < ?";

        try {
            //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "ArthurTest", "" );
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Globals.roomToDisplay);
            statement.setDate(2, valueOf(Globals.checkInDate));
            statement.setDate(3, valueOf(Globals.checkOutDate));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int overlappingBookingsCount = resultSet.getInt(1);
            connection.close();
            statement.close();
            resultSet.close();
            return overlappingBookingsCount > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Formatter class for date labels
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
        }
    }
}
