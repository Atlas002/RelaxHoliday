import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Bookings extends JFrame {

    protected JPanel reservationsPanel; // Panel to hold reservations
    protected Connection connection; // Database connection
    private int userStat;
    private String query;
    private JLabel yourBookingsLabel;
    private JPanel titlePanel;
    private JLabel relaxHolidaysLabel;

    public Bookings() {



        setSize(600, 400); // Set the size of the JFrame

        // Set the background color
        Color backgroundColor = new Color(180, 218, 255);
        getContentPane().setBackground(backgroundColor);


        try {
            // Establish connection to the database
            //connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");

            connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't connect to the database"); // Error message if connection fails
            System.exit(1);
        }

        // Create a JPanel for titles
        titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS)); // Use BoxLayout with vertical alignment
        titlePanel.setBackground(backgroundColor); // Set background color for titles
        add(titlePanel, BorderLayout.NORTH);

        // JLabel for "Relax Holidays"
        relaxHolidaysLabel = new JLabel("Relax Holidays");
        relaxHolidaysLabel.setFont(new Font("Baskerville Old Face", Font.PLAIN, 20));
        relaxHolidaysLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(relaxHolidaysLabel);

        // JLabel for "Your bookings"
        yourBookingsLabel = new JLabel("Your bookings");
        yourBookingsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        yourBookingsLabel.setHorizontalAlignment(SwingConstants.CENTER); // Horizontally center the label
        titlePanel.add(yourBookingsLabel);

        // Panel to display reservations
        reservationsPanel = new JPanel();
        reservationsPanel.setBackground(backgroundColor); // Set background color for reservations panel
        reservationsPanel.setLayout(new BoxLayout(reservationsPanel, BoxLayout.Y_AXIS)); // Use BoxLayout with vertical alignment
        JScrollPane scrollPane = new JScrollPane(reservationsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scroll bar
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Never show horizontal scroll bar
        add(scrollPane, BorderLayout.CENTER);

        reservationsPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add empty border for padding

        // Load and display existing reservations
        loadAndDisplayReservations(); // Load reservations for a specific user ID

        setLocationRelativeTo(null);
        setVisible(true); // Set the JFrame visible
    }

    // Method to load and display reservations for a given customer ID
    private void loadAndDisplayReservations() {
        try {
            // SQL query to retrieve booking information for a given customer ID
            String StatusQuery = "SELECT emp_status FROM person WHERE person_ID =" + Globals.userID;
            PreparedStatement StatusStatement = connection.prepareStatement(StatusQuery);
            ResultSet StatusResultSet = StatusStatement.executeQuery();

            while (StatusResultSet.next())
            {
                userStat = StatusResultSet.getInt("emp_status");
            }

            if(userStat==1) //if user is employee
            {
                setTitle("All bookings"); // Set the title of the JFrame
                yourBookingsLabel.setText("All bookings");
                //we fetch every booking existing in the databse
                query = "SELECT booking.customer_ID, person.first_name, person.last_name, booking.check_in_date, booking.check_out_date, room.room_number, booking.booking_id,  booking.special_request " +
                        "FROM booking " +
                        "JOIN person ON booking.customer_ID = person.person_ID " +
                        "JOIN room ON booking.room_ID = room.room_ID ";
            }
            else //if user is customer
            {
                setTitle("Your bookings"); // Set the title of the JFrame
                yourBookingsLabel.setText("Your bookings");
                //we fetch only the customer's booking
                query = "SELECT booking.customer_ID, person.first_name, person.last_name, booking.check_in_date, booking.check_out_date, room.room_number,  booking.special_request " +
                        "FROM booking " +
                        "JOIN person ON booking.customer_ID = person.person_ID " +
                        "JOIN room ON booking.room_ID = room.room_ID " +
                        "WHERE booking.customer_ID = "+ Globals.userID;
            }

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery(); //fecthing the query for the corresponding statement

            // Iterate through the result set and create reservation panels
            while (resultSet.next()) {
                boolean isSpecialReq= false; //we assume there is no special request by default

                //storing every part of the request into strings
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String arrivalDate = resultSet.getString("check_in_date");
                String goingDate = resultSet.getString("check_out_date");
                String roomNumber = resultSet.getString("room_number");
                String specialRequest= null; //initializing special request string as null

                if(resultSet.getString("special_request")!=null) //we check if the query result for the special request has found something
                {
                    isSpecialReq= true; //if it did, we set the boolean flag as true
                    specialRequest =    resultSet.getString("special_request"); //we store the result of the query in the string
                }


                int currentResID ; //we initialize the variable storing the ID of the current booking that is being created

                if(userStat==1) //if the user is an employee
                {
                    //we store the result from the query
                    currentResID = resultSet.getInt("booking_id");
                }
                else //if he is a customer
                {
                    //we set it as -1 to not use it in the code
                    currentResID= -1;
                }

                //we call the fucntion to create the panel for the reservation with all the infos from the query
                JPanel reservationPanel = createReservationPanel(firstName, lastName, arrivalDate, goingDate, roomNumber, userStat, currentResID,specialRequest, isSpecialReq);
                reservationsPanel.add(reservationPanel); //we add the panel
                reservationsPanel.add(Box.createVerticalStrut(10)); // Add vertical spacing between reservations
            }

            statement.close(); // Close the statement
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error"); // Show error message if an SQL exception occurs
        }
    }

    // Method to create a panel for a reservation
    private JPanel createReservationPanel(String firstName, String lastName, String arrivalDate, String goingDate, String roomNumber, int userStat, int resID, String specialRequest, boolean isSpecialReq) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 80)); // Set preferred size for each reservation panel
        Color panelColor = new Color(142, 187, 255); // Define panel color
        panel.setBackground(panelColor); // Set background color for the reservation panel
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add a black border
        panel.setLayout(new GridLayout(4, 1)); // Use GridLayout with 4 rows and 1 column

        // Labels to display reservation information
        JLabel nameLabel = new JLabel("Name: " + firstName + " " + lastName);
        JLabel dateLabel = new JLabel("Check in date: " + arrivalDate);
        JLabel goingLabel = new JLabel("Check out date: " + goingDate);
        JLabel roomLabel = new JLabel("Room number: " + roomNumber);

        JLabel reqLabel= null; //label storing the possible special request

        if (isSpecialReq) //if the current booking has a special request
        {
            reqLabel = new JLabel("Special request :" + specialRequest); //we load the special request from the query into the label
        }

        // Add labels to the reservation panel

        if (userStat==1) //if the user is an employee
        {
            //we add the relevant labels to the panel
            panel.add(nameLabel);
            panel.add(dateLabel);
            panel.add(roomLabel);
            panel.add(goingLabel);
            if (isSpecialReq) //if there is a special request
            {
                //we add it's label to the panel
                panel.add(reqLabel);
            }


            //we create a new button to link to the update booking page
            JButton updateButton = new JButton("Update Booking");

            //we create it's action listener
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {


                    Globals.bookingToDisplay= resID; //we set the ID of the booking to be displayed as the ID of the current booking

                    JFrame frame = new JFrame("Update Booking");
                    frame.setContentPane(new UpdateBooking().GenPane);
                    //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                    dispose(); //close the windows







                }
            });

            panel.add(updateButton);

        }
        else //if the user is a customer
        {
            //we add the relevant panels
            panel.add(nameLabel);
            panel.add(roomLabel);
            panel.add(dateLabel);
            panel.add(goingLabel);
            if (isSpecialReq) //if there is a special request
            {
                //we add it's panel
                panel.add(reqLabel);
            }
        }

        return panel; // Return the reservation panel
    }

    // Main method to execute the program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Bookings::new); // Create and show the JFrame on the Event Dispatch Thread
    }
}
