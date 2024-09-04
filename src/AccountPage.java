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

public class AccountPage {
    protected JPanel panel;
    private JLabel pageName;
    private JLabel firstName;
    private JLabel lastName;
    private JLabel email;
    private JLabel phoneNumber;
    private JLabel status;
    private JLabel title;
    private JLabel firstName_field;
    private JLabel lastName_field;
    private JLabel email_field;
    private JLabel phoneNumber_field;
    private JLabel status_field;
    private JButton accountButton;
    private JButton createRoomButt;
    private JButton DisplayStatButton;
    private JComboBox relaxHolidaysComboBox;
    private String boxResult;

    public AccountPage() {

        //set buttons invisble by default
        accountButton.setVisible(false);
        createRoomButt.setVisible(false);
        DisplayStatButton.setVisible(false);
        relaxHolidaysComboBox.setVisible(false);

        if (Globals.userID != -1) {
            try {

                // Establishing connnection to the database
                //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");

                Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );


                Statement st = connection.createStatement(); //delcaring the statement used for the querry
                String query = "SELECT first_name, last_name, phone_number, email ,emp_status FROM person WHERE person_ID = " + Globals.userID; //querry with the userID to get the relevant information from the DB
                ResultSet rs = st.executeQuery(query); //executing the querry


                while (rs.next()) { //if we find a user with the aforementioned ID in the DB

                    //we set the Swing textFields with the result of the querry
                    firstName_field.setText(rs.getString("first_name"));
                    lastName_field.setText(rs.getString("last_name"));
                    phoneNumber_field.setText(rs.getString("phone_number"));
                    email_field.setText(rs.getString("email"));
                    int empStat = rs.getInt("emp_status");
                    if (empStat == 1)
                    {
                        status_field.setText("Employee");
                        accountButton.setText("View All Bookings");
                        accountButton.setVisible(true);
                        createRoomButt.setVisible(true);
                        DisplayStatButton.setVisible(true);
                        relaxHolidaysComboBox.setVisible(true);
                    }
                    else
                    {
                        status_field.setText("Customer");
                        accountButton.setText("My Bookings");
                        accountButton.setVisible(true);
                    }



                    accountButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                                Bookings bookings = new Bookings();
                                bookings.setLocationRelativeTo(null);
                                bookings.setVisible(true);

                                JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(panel);
                                frameOld.dispose(); //close the windows


                        }
                    });

                    createRoomButt.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if(empStat ==1) //if the user is an employee
                            {
                                JFrame frame = new JFrame("Create Room");
                                frame.setContentPane(new CreateRoom().GenPane);
                                //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                frame.pack();
                                frame.setLocationRelativeTo(null);
                                frame.setVisible(true);
                                JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(panel);
                                frameOld.dispose(); //close the windows
                            }

                        }
                    });

                    DisplayStatButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if(empStat ==1) //if the user is an employee
                            {
                                boxResult = (String) relaxHolidaysComboBox.getSelectedItem();


                                switch (boxResult) {
                                    case "Room: Distribution by location":
                                        Globals.graphToDisplay = 1;
                                        break;
                                    case "Bookings: Distribution by room":
                                        Globals.graphToDisplay = 2;
                                        break;
                                    case "Bookings: Distribution by capacity":
                                        Globals.graphToDisplay = 3;
                                        break;
                                }

                                // Create an instance of PieChartPage
                                PieChartPage pieChartPage = new PieChartPage();

                                pieChartPage.pack();
                                pieChartPage.setLocationRelativeTo(null);
                                pieChartPage.setVisible(true);
                            }

                        }
                    });



                }

                connection.close(); // Close database connection

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }



    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("AccountPage");
        AccountPage accountPage = new AccountPage();
        frame.setContentPane(new AccountPage().panel);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();


        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
