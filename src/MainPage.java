import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;



public class MainPage {
    JPanel GenPanel; // Main page panel
    private JPanel ChooseBar; // Area storing all other page buttons
    private JButton loginCusto; // Button redirecting to customer login page
    private JButton book; // Button redirecting to room catalog page
    private JLabel title; // Page title
    private JButton signIn; // Button redirecting to sign-in page
    private JButton loginEmployee; // Button redirecting to employee login page
    private JPanel imagePanel; // Panel for images
    private JPanel BackPanel; // Back panel
    private JLabel copyright; // Copyright label
    private JButton Account; // Button redirecting to account page
    private JButton randoRoom1;
    private JButton randoRoom2;
    private JButton randoRoom3;

    private int[] roomIDs ={0,0,0};

    // Action listeners for the buttons
    public MainPage() {

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
            String query = "SELECT image, room_ID FROM room WHERE image IS NOT NULL ORDER BY RAND() LIMIT 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            int i=0;

            while(resultSet.next())
            {
                Blob imageBlob = resultSet.getBlob("image");


                byte[] imageData = imageBlob.getBytes(1, (int) imageBlob.length());
                if(imageData!=null && imageData.length>0)
                {
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
                    BufferedImage image = ImageIO.read(bis);
                    if (image != null) {
                        ImageIcon icon = new ImageIcon(image);
                        switch (i)
                        {
                            case 0:

                                randoRoom1.setIcon(icon);

                                break;

                            case 1:
                                randoRoom2.setIcon(icon);

                                break;

                            case 2:
                                randoRoom3.setIcon(icon);

                                break;

                        }
                        roomIDs[i] = resultSet.getInt("room_ID");
                        i++;
                    }
                }






            }


            statement.close();
            resultSet.close();
            connection.close();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }


        loginCusto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Opens customer login window
                JFrame frame = new JFrame("Login");
                frame.setContentPane(new Login().myPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        loginEmployee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Opens employee login window
                JFrame frame = new JFrame("LoginEmp");
                frame.setContentPane(new LoginEmp().Gen);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        signIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Opens sign-in window
                JFrame frame = new JFrame("infoFormCustomer");
                frame.setContentPane(new infoFormCustomer().gen);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        book.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Opens room catalog window
                BrowseRoom catalogueChambres = new BrowseRoom();
                catalogueChambres.setLocationRelativeTo(null);
                catalogueChambres.setVisible(true);
            }
        });

        Account.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Opens account page
                JFrame frame = new JFrame("Account Page");
                frame.setContentPane(new AccountPage().panel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        randoRoom1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                //open random room 1

                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
                    String query = "SELECT COUNT(*) FROM room WHERE room_ID = "+roomIDs[0];
                    PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();
                    resultSet.next();
                    int roomExists = resultSet.getInt(1);

                    if(roomExists==0)
                    {
                        JOptionPane.showMessageDialog(null, "This room does not exists anymore.", "Error", JOptionPane.ERROR_MESSAGE);

                    }
                    else {
                        Globals.roomToDisplay = roomIDs[0];
                        System.out.println("Room Number: " + Globals.roomToDisplay);
                        JFrame frame = new JFrame("Room"); // Create a new JFrame to display room details
                        frame.setContentPane(new RoomGUI().genPanel); // Set the window content to the panel generated by RoomGUI

                        frame.pack(); // Automatically adjust the window size
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true); // Make the window visible
                    }
                    resultSet.close();
                    statement.close();
                    connection.close();



                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }




            }
        });

        randoRoom2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
                    String query = "SELECT COUNT(*) FROM room WHERE room_ID = "+roomIDs[1];
                    PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();
                    resultSet.next();
                    int roomExists = resultSet.getInt(1);

                    if(roomExists==0)
                    {
                        JOptionPane.showMessageDialog(null, "This room does not exists anymore.", "Error", JOptionPane.ERROR_MESSAGE);

                    }
                    else {
                        Globals.roomToDisplay = roomIDs[1];
                        System.out.println("Room Number: " + Globals.roomToDisplay);
                        JFrame frame = new JFrame("Room"); // Create a new JFrame to display room details
                        frame.setContentPane(new RoomGUI().genPanel); // Set the window content to the panel generated by RoomGUI

                        frame.pack(); // Automatically adjust the window size
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true); // Make the window visible
                    }
                    resultSet.close();
                    statement.close();
                    connection.close();



                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        randoRoom3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //open random room 1
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
                    String query = "SELECT COUNT(*) FROM room WHERE room_ID = "+roomIDs[2];
                    PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();
                    resultSet.next();
                    int roomExists = resultSet.getInt(1);

                    if(roomExists==0)
                    {
                        JOptionPane.showMessageDialog(null, "This room does not exists anymore.", "Error", JOptionPane.ERROR_MESSAGE);

                    }
                    else {
                        Globals.roomToDisplay = roomIDs[2];
                        System.out.println("Room Number: " + Globals.roomToDisplay);
                        JFrame frame = new JFrame("Room"); // Create a new JFrame to display room details
                        frame.setContentPane(new RoomGUI().genPanel); // Set the window content to the panel generated by RoomGUI

                        frame.pack(); // Automatically adjust the window size
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true); // Make the window visible
                    }
                    resultSet.close();
                    statement.close();
                    connection.close();



                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }

    public static void main(String[] args) {
        // Initialize the user ID to -1 because nobody's logged in
        Globals.userID = -1;
        Globals.roomToDisplay = -1;
        Globals.bookingToDisplay = -1;
        System.out.println(Globals.userID);

        // Main page setup
        JFrame frame = new JFrame("MainPage");
        frame.setContentPane(new MainPage().GenPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}