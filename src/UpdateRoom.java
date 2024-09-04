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

public class UpdateRoom {
    protected JPanel GenPane;
    private JLabel Title;
    private JTextField roomNameField;
    private JTextArea roomDescriptionTextArea;
    private JTextField priceTextField;
    private JTextField capacityTextField;
    private JButton saveButton;
    private JButton deleteButton;
    private JLabel Subtitle;
    private JComboBox Chooselocation;

    public UpdateRoom() {
        try {

           // Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );

            //setting up the query string
            String query = "SELECT room_number, price, capacity, location, description FROM room WHERE room_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query); //establishing sql statement
            preparedStatement.setInt(1, Globals.roomToDisplay); //setting the variable in the statement
            ResultSet resultSet = preparedStatement.executeQuery(); //executing the querry

            //if a result is found
            while (resultSet.next()) {
                //filling the Swing labels with the result of the querry
                roomNameField.setText(resultSet.getString("room_number"));
                roomDescriptionTextArea.setText(resultSet.getString("description"));
                capacityTextField.setText(resultSet.getString("capacity"));
                priceTextField.setText(resultSet.getString("price"));
                String currentLocation = resultSet.getString("location");

                //setting the option of the combo box by default depending on the result of the querry
                switch (currentLocation) {
                    case "London":
                        Chooselocation.setSelectedItem("London");
                        break;
                    case "Paris":
                        Chooselocation.setSelectedItem("Paris");
                        break;
                    case "Rome":
                        Chooselocation.setSelectedItem("Rome");
                        break;
                }
            }

            //creatin a listener for the save button
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    try
                    {
                        //setting up the update SQL query
                        String query = "UPDATE room SET room_number=?, description=?, capacity=?, price=?, location=? WHERE room_ID="+ Globals.roomToDisplay;
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        //filling the statements variables from the modified fields
                        preparedStatement.setString(1, roomNameField.getText());
                        preparedStatement.setString(2, roomDescriptionTextArea.getText());
                        preparedStatement.setInt(3, Integer.parseInt(capacityTextField.getText()));
                        preparedStatement.setDouble(4, Integer.parseInt(priceTextField.getText()));
                        preparedStatement.setString(5, (String) Chooselocation.getSelectedItem());


                        //check if the room was successfully updated
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(null, "Room updated successfully.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update room.");
                        }
                        //closing the update room page
                        JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(GenPane);
                        frameOld.dispose(); //close the windows
                    }
                    catch (SQLException ee) {
                        ee.printStackTrace();
                    }

                }
            });

            //creating a listener for the delete button
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Delete from room table
                        String deleteRoomQuery = "DELETE FROM room WHERE room_ID=?";
                        PreparedStatement deleteRoomStatement = connection.prepareStatement(deleteRoomQuery);
                        deleteRoomStatement.setInt(1, Globals.roomToDisplay);

                        // Delete related bookings
                        String deleteBookingsQuery = "DELETE FROM booking WHERE room_id=?";
                        PreparedStatement deleteBookingsStatement = connection.prepareStatement(deleteBookingsQuery);
                        deleteBookingsStatement.setInt(1, Globals.roomToDisplay);

                        // Execute delete statements
                        int roomRowsAffected = deleteRoomStatement.executeUpdate();
                        int bookingRowsAffected = deleteBookingsStatement.executeUpdate();

                        if (roomRowsAffected > 0 || bookingRowsAffected > 0) {
                            JOptionPane.showMessageDialog(null, "Room and related bookings deleted successfully.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to delete room and related bookings.");
                        }

                        // Close resources after delete operations
                        deleteRoomStatement.close();
                        deleteBookingsStatement.close();

                        JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(GenPane);
                        frameOld.dispose(); //close the windows

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });


            // Close resources
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("UpdateRoom");
        frame.setContentPane(new UpdateRoom().GenPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
