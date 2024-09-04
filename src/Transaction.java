/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static java.sql.Date.valueOf;


public class Transaction {

    private int roomPrice;
    protected JPanel GenPane;
    private JLabel Title;
    private JLabel RoomNumber;
    private JLabel Checkin;
    private JLabel Checkout;
    private JLabel Duration;
    private JLabel PPN;
    private JLabel Subtitle;
    private JLabel Total;
    private JRadioButton debitRadioButton;
    private JRadioButton creditRadioButton;
    private JTextField CardInfoField;
    private JLabel PayMethod;
    private JLabel CardInfo;
    private JLabel Mail;
    private JButton proceedButton;
    private JLabel customerEmail;
    private JCheckBox checkBoxMail;
    private JLabel roomNumberField;
    private JLabel checkInField;
    private JLabel checkOutField;
    private JLabel durationField;
    private JLabel ppnField;
    private JLabel totalPriceField;
    private JLabel SpecialRequest;
    private JTextField SpecialRequestField;

    public Transaction() {
        ButtonGroup paymentGroup = new ButtonGroup(); // Group for radio buttons
        paymentGroup.add(debitRadioButton);
        paymentGroup.add(creditRadioButton);
        debitRadioButton.setSelected(true);

        try {
            //connecting to the database
            //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );

            //setting up the sql query to fetch the information of the room to display
            String roomQuery = "SELECT room_number, price, capacity, location, description FROM room WHERE room_ID = " + Globals.roomToDisplay;
            Statement roomStatement = connection.createStatement();
            ResultSet roomResultSet = roomStatement.executeQuery(roomQuery); //executing the query

            while (roomResultSet.next()) {

                //filling the swing labels with the result of the query
                roomNumberField.setText(roomResultSet.getString("room_number"));
                checkInField.setText(String.valueOf(Globals.checkInDate));
                checkOutField.setText(String.valueOf(Globals.checkOutDate));
                durationField.setText(String.valueOf(Globals.getDuration()));
                ppnField.setText(roomResultSet.getString("price"));
                roomPrice = roomResultSet.getInt("price");
                totalPriceField.setText(String.valueOf(Globals.getDuration()*roomResultSet.getInt("price")));
            }

            //setting up the sql query to fetch the email of the customer
            String userQuery = "SELECT email FROM person WHERE person_ID = " + Globals.userID;
            Statement userStatement = connection.createStatement();
            ResultSet userResultSet = userStatement.executeQuery(userQuery);


            while (userResultSet.next()) {
            customerEmail.setText(userResultSet.getString("email"));
            }

            // Close resources

            roomResultSet.close();
            roomStatement.close();
            userResultSet.close();
            userStatement.close();
            connection.close();




        } catch (SQLException e) {
            e.printStackTrace();
        }


        //creating an action listener for the proceed button
        proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //connecting to the database
                    //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );

                    //setting up the sql querry to insert the new transaction into the database
                    String sql = "INSERT INTO booking (room_id, check_in_date, check_out_date, price, credit_card_info, customer_ID, special_request) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    //filling the statement variable with the filled labels
                    statement.setInt(1, Globals.roomToDisplay);
                    statement.setDate(2, valueOf(Globals.checkInDate));
                    statement.setDate(3, valueOf(Globals.checkOutDate));
                    statement.setInt(4, Globals.getDuration() * roomPrice);
                    statement.setString(5, CardInfoField.getText());
                    statement.setInt(6, Globals.userID);
                    statement.setString(7, SpecialRequestField.getText());



                     // Execute the insertion query
                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        // Check if the email checkbox is selected
                        if (checkBoxMail.isSelected()) {
                            // Send email to the customer
                            EmailSender.sendEmail(customerEmail.getText(), "Booking Confirmation", writeEmailBody(connection));
                        }


                        Globals.roomToDisplay=-1;
                        Globals.checkInDate= null;
                        Globals.checkOutDate=null;

                        JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(GenPane);
                        frameOld.dispose(); //close the windows

                    }

                    // Close resources
                    statement.close();
                    connection.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });




    }

    //method used to write the email body using the information of the user and the booking
    private String writeEmailBody(Connection connection) {
        String body = null; //initializing the body string

        try {
            //setting up the querry to fetch all the necessary information for the mail
            String emailQuery = "SELECT booking.price, booking.check_in_date, booking.check_out_date, " +
                    "person.first_name, person.last_name, room.room_number, room.location " +
                    "FROM booking " +
                    "JOIN person ON booking.customer_ID = person.person_ID " +
                    "JOIN room ON booking.room_ID = room.room_ID " +
                    "WHERE booking.customer_ID = ? AND booking.check_in_date = ? AND booking.check_out_date = ? AND booking.room_ID = ?";

            PreparedStatement statement = connection.prepareStatement(emailQuery);
            //filling the statement variable with the customer's ID, the check in and check out dates chosen as well as the room booked
            statement.setInt(1, Globals.userID);
            statement.setDate(2, valueOf(Globals.checkInDate));
            statement.setDate(3, valueOf(Globals.checkOutDate));
            statement.setInt(4, Globals.roomToDisplay);

            ResultSet resultSet = statement.executeQuery(); //executing the querry

            while (resultSet.next()) {
                //storing the query result into strings
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String roomNumber = resultSet.getString("room_number");
                String location = resultSet.getString("location");
                String arrivalDate = resultSet.getString("check_in_date");
                String goingDate = resultSet.getString("check_out_date");
                String price = resultSet.getString("price");

                //filling up the email body
                body = "Hello " + firstName + " " + lastName + "!\nYour booking for the room " + roomNumber +
                        " in " + location + " from the " + arrivalDate + " to the " + goingDate + " for a total of " +
                        price + "€ has been confirmed!\nWe look forward to seeing you there!\n\nSincerely,\nRelax Holiday's Customer Service.";


            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error"); // Show error message if an SQL exception occurs
        }

        return body; //returning the filled in string
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("Transaction");
        frame.setContentPane(new Transaction().GenPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
