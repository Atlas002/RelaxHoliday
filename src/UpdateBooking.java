/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

import org.jdatepicker.impl.JDatePanelImpl;

import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.swing.*;
import java.sql.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;


import static java.sql.Date.valueOf;





public class UpdateBooking {
    protected JPanel GenPane;
    private JLabel titleLabel;
    private JLabel SubtitleLabel;
    private JLabel NumberLabel;
    private JLabel NumberField;
    private JLabel LocationField;
    private JLabel LocationLabel;
    private JLabel PriceLabel;
    private JTextField PriceField;
    private JPanel DatePanel;
    private JPanel DatePanel2;
    private JLabel OGcheckInField;
    private JLabel OGcheckoutLabel;
    private JTextField SpeReqField;
    private JLabel SpeReqLabel;
    private JButton deleteButton;
    private JButton saveButton;
    private JLabel CustomerLabel;
    private JLabel CustomerNameField;
    private JLabel BookingNumberLabel;
    private JLabel BookingNumberField;
    JDatePickerImpl datePicker1;
    JDatePickerImpl datePicker2;

    private Date bufferCIN;
    private Date bufferCOUT;
    private LocalDate OGcheckIn;
    private JLabel OGcheckOutField;
    private JLabel OGcheckInLabel;
    private LocalDate OGcheckOut;

    private UtilDateModel model1;
    private UtilDateModel model2;
    private int resRoomID;

    public UpdateBooking() {

        try {
            //connecting to the database
            //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );

            //setting up the string for the sql query
            String InfoQuery = "SELECT booking.customer_ID, person.first_name, person.last_name, booking.check_in_date, booking.check_out_date, room.room_number, room.room_ID, room.location, booking.booking_id,  booking.special_request, booking.price "+
                    "FROM booking JOIN person ON booking.customer_ID = person.person_ID JOIN room ON booking.room_ID = room.room_ID "+
                    "WHERE booking.booking_id = " + Globals.bookingToDisplay;

            PreparedStatement fetchBookingStatement = connection.prepareStatement(InfoQuery);

            //executing the query
            ResultSet infoResultSet = fetchBookingStatement.executeQuery();

            //if a result for the query has been found
            while (infoResultSet.next())
            {
                //filing the swing labels with the query results
                NumberField.setText(infoResultSet.getString("room_number"));
                PriceField.setText(infoResultSet.getString("price"));
                SpeReqField.setText(infoResultSet.getString("special_request"));
                LocationField.setText(infoResultSet.getString("location"));
                BookingNumberField.setText(infoResultSet.getString("booking_id"));
                CustomerNameField.setText(infoResultSet.getString("first_name") +" "+ infoResultSet.getString("last_name"));
                OGcheckInField.setText(String.valueOf(infoResultSet.getDate("check_in_date")));
                OGcheckOutField.setText(String.valueOf(infoResultSet.getDate("check_out_date")));

                //storing the room id
                resRoomID = infoResultSet.getInt("room_ID");
                //storing the original booking dates
                bufferCIN = infoResultSet.getDate("check_in_date");
                bufferCOUT = infoResultSet.getDate("check_out_date");



                //converting the booking date from Date to LocalDate
                OGcheckIn = new Date(bufferCIN.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                OGcheckOut = new Date(bufferCOUT.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                //setting the default date of the datepickers to the original booking date
                model1.setDate(OGcheckIn.getYear(), OGcheckIn.getMonthValue() -1,OGcheckIn.getDayOfMonth());
                model2.setDate(OGcheckOut.getYear(), OGcheckOut.getMonthValue() -1,OGcheckOut.getDayOfMonth());

            }

            infoResultSet.close();
            fetchBookingStatement.close();
            connection.close();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        //creating a listener for the save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {


                    //getting the date chosen in the datepickers
                    Date selectedDate1 = (Date) datePicker1.getModel().getValue();
                    Date selectedDate2 = (Date) datePicker2.getModel().getValue();

                    //declaring LocalDate variables for the display and the saving into the database
                    LocalDate firstLocalDate;
                    LocalDate secondLocalDate;

                    if(selectedDate1==null) //if the check in date hasn't been modified
                    {
                        firstLocalDate = OGcheckIn; //we get the original check in date and we store it as the final check in date
                    }
                    else //if it has been modified
                    {
                        //we convert the date from the datepicker to LocalDate format
                        firstLocalDate = selectedDate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }
                    if(selectedDate2==null) //if the check out date hasn't been modified
                    {
                        secondLocalDate = OGcheckOut; //we get the original check out date and we store it as the final check out date
                    }
                    else //if it has been modified
                    {
                        //we convert the date from the datepicker to LocalDate format
                        secondLocalDate = selectedDate2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }

                    //calculating the duration of the stay
                    long numberOfDays = calculateDaysBetween(firstLocalDate, secondLocalDate);
                    if (numberOfDays == 0) { //if the duration of the stay is null
                        //we ask the user to select different dates
                        JOptionPane.showMessageDialog(null, "Bookings must be at least one day long.", "Error", JOptionPane.ERROR_MESSAGE);
                        return; // Exit the method
                    }

                    //if the duration of the stay is negative
                    if (numberOfDays < 0) {
                        //we swap the check in and check out dates
                        LocalDate temp = firstLocalDate;
                        firstLocalDate = secondLocalDate;
                        secondLocalDate = temp;
                        //and we recalculate the duration of the stay
                        numberOfDays = calculateDaysBetween(firstLocalDate, secondLocalDate);
                    }

                    //we check if the new dates chosen make the current booking overlap with preexisting bookings
                    if (checkForOverlappingBookings(firstLocalDate, secondLocalDate)) {
                        //if it does, we display that to the user
                        JOptionPane.showMessageDialog(null, "This room is already booked for the selected dates.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );

                    //we set up the sql query for the update
                    String updateQuery = "UPDATE booking SET price=?, special_request=?, check_in_date=?, check_out_date=? WHERE booking_id = "+ Globals.bookingToDisplay;
                    PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                    //we set the variables from the field into the statement
                    preparedStatement.setInt(1, Integer.parseInt(PriceField.getText()));
                    preparedStatement.setString(2, SpeReqField.getText());
                    preparedStatement.setDate(3, valueOf(firstLocalDate));
                    preparedStatement.setDate(4, valueOf(secondLocalDate));

                    //we execute the update
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Booking updated successfully.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update booking.");
                    }

                    preparedStatement.close();

                    connection.close();
                    Bookings bookings = new Bookings();
                    bookings.setLocationRelativeTo(null);
                    bookings.setVisible(true);

                    JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(GenPane);
                    frameOld.dispose(); //close the windows
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        //creating an action listener for the delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //connecting to the database
                    //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
                    //setting up the sql query to delete the booking
                    String deleteBookingQuery = "DELETE FROM booking WHERE booking_id ="+Globals.bookingToDisplay;
                    PreparedStatement deleteBookingStatement = connection.prepareStatement(deleteBookingQuery);
                    //executing the query
                    int bookingRowsAffected = deleteBookingStatement.executeUpdate();

                    if (bookingRowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Booking deleted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to delete booking.");
                    }

                    deleteBookingStatement.close();
                    connection.close();
                    //we open the booking page
                    Bookings bookings = new Bookings();
                    bookings.setLocationRelativeTo(null);
                    bookings.setVisible(true);

                    //We close the update room page
                    JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(GenPane);
                    frameOld.dispose(); //close the windows

                }catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });



    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Update Booking");
        frame.setContentPane(new UpdateBooking().GenPane);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    //creating the datepickers
    private void createUIComponents() {

        DatePanel = new JPanel();
        DatePanel2 = new JPanel();

        Properties properties = new Properties();
        properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");
        // Create separate UtilDateModel instances for each date picker
        model1 = new UtilDateModel();
        model2 = new UtilDateModel();




        // Create JDatePanelImpl instances for each date picker
        JDatePanelImpl datePanel1 = new JDatePanelImpl(model1, properties);
        JDatePanelImpl datePanel2 = new JDatePanelImpl(model2, properties);

        // Create separate JDatePickerImpl instances using the respective JDatePanelImpl instances
        datePicker1 = new JDatePickerImpl(datePanel1, new DateLabelFormatter());
        datePicker2 = new JDatePickerImpl(datePanel2, new DateLabelFormatter());



        DatePanel.add(datePicker1);
        DatePanel2.add(datePicker2);


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
        }



    }
    public long calculateDaysBetween(LocalDate firstDate, LocalDate secondDate) {
        // Calculate the difference in number of days
        return ChronoUnit.DAYS.between(firstDate, secondDate);
    }
    private boolean checkForOverlappingBookings(LocalDate checkInDate, LocalDate checkOutDate) throws SQLException {
        //setting  a query to count the numbers of booking overlapping with the current booking that are not the current booking
        String query = "SELECT COUNT(*) FROM booking WHERE room_ID = ? AND check_out_date > ? AND check_in_date < ? AND NOT booking_id = "+ Globals.bookingToDisplay;

        try {
            //connecting to the database
            //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
            PreparedStatement statement = connection.prepareStatement(query);

            //filling the statement variable
            statement.setInt(1, resRoomID);
            statement.setDate(2, valueOf(checkInDate));
            statement.setDate(3, valueOf(checkOutDate));

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            //we return the number of result found
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
}
