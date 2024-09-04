/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
import java.sql.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
public class Login {
    private JLabel Name;
    private JLabel password;
    private JTextField nameInput;
    private JTextField passwordInput;
    protected JPanel myPanel;
    private JLabel connectionShow;
    private JLabel SignIn;
    private JButton signInButton;
    private JLabel RH;
    private JPasswordField passwordField1;
    private JButton submitButton;

    public Login() {

        passwordField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitButton.doClick();
                }
            }
        });

        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("infoFormCustomer");
                frame.setContentPane(new infoFormCustomer().gen);

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(myPanel);
                frameOld.dispose(); //close the windows
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = nameInput.getText();
                String password = new String(passwordField1.getPassword());

                try {

                    // create a connection to the database
                    //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );

                    // Search for the user in the person table
                    PreparedStatement statement = connection.prepareStatement("SELECT person_ID FROM person WHERE email = ? AND password = ? AND emp_status = FALSE");
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
                        JOptionPane.showMessageDialog(myPanel, "Invalid email or password.");
                    }

                    // Close the resources
                    resultSet.close();
                    statement.close();
                    connection.close();

                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(myPanel);
                    frame.dispose(); //close the windows

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(myPanel, "Error: " + ex.getMessage());
                }
            }
        });
    }

    public static void main() {

        JFrame frame = new JFrame("Login");
        frame.setContentPane(new Login().myPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
