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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
public class LoginEmp {
    protected JPanel Gen;
    private JLabel title;
    private JLabel title2;
    private JLabel Mail;
    private JLabel psw;

    private JTextField Email;
    private JPasswordField Password;
    private JButton Valid;

    public LoginEmp() {


        Password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Valid.doClick();
                }
            }
        });

        Valid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = Email.getText();
                String password = new String(Password.getPassword());

                try {

                    // create a connection to the database
                   // Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );

                    // Search for the user in the person table
                    PreparedStatement statement = connection.prepareStatement("SELECT person_ID FROM person WHERE email = ? AND password = ? AND emp_status = TRUE");
                    statement.setString(1, email);
                    statement.setString(2, password);
                    ResultSet resultSet = statement.executeQuery();

                    // Check if the user exists
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("person_ID");

                        // Store the user ID in the Globals class
                        Globals.userID = userId;
                        System.out.println(Globals.userID);



                    } else {
                        JOptionPane.showMessageDialog(Gen, "Invalid email or password.");
                    }

                    // Close the resources
                    resultSet.close();
                    statement.close();
                    connection.close();

                    JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(Gen);
                    frameOld.dispose(); //close the windows

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Gen, "Error: " + ex.getMessage());
                }

            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("LoginEmp");
        frame.setContentPane(new LoginEmp().Gen);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
