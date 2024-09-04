import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

// Main class to display a room catalog
public class BrowseRoom extends JFrame {

    private ArrayList<Room> rooms; // List to store Room objects
    private JComboBox<String> locationComboBox; // ComboBox to select location
    private JComboBox<String> sortByComboBox; // ComboBox to select sorting option
    private JPanel selectionPanel; // Global container for the selection panel

    // Constructor of the BrowseRoom class
    public BrowseRoom() {
        super("Room Catalog"); // Set the JFrame title
        setLayout(new BorderLayout()); // Use BorderLayout

        // Create the label "Room Catalog"
        JLabel labelTitle = new JLabel("Room Catalog");
        labelTitle.setFont(new Font("Arial", Font.BOLD, 20)); // Set font and size
        labelTitle.setHorizontalAlignment(SwingConstants.CENTER); // Center alignment

        JLabel labelSubtitle = new JLabel("Relax Holidays");
        labelSubtitle.setFont(new Font("Baskerville Old Face", Font.BOLD, 30));
        labelSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        // Add the label "Room Catalog" at the top of the window
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(labelSubtitle, BorderLayout.NORTH);
        titlePanel.add(labelTitle, BorderLayout.CENTER);
        titlePanel.setBackground(new Color(180, 218, 255));

        // Create the search panel to select location and sorting option
        JPanel searchPanel = new JPanel(new FlowLayout());
        String[] locations = {"All locations", "London", "Rome", "Paris"};
        locationComboBox = new JComboBox<>(locations);
        searchPanel.add(new JLabel("Select location:"));
        searchPanel.add(locationComboBox);

        String[] sortOptions = {"Default", "Capacity: High to Low", "Capacity: Low to High", "Price: High to Low", "Price: Low to High"};
        sortByComboBox = new JComboBox<>(sortOptions);
        searchPanel.add(new JLabel("Sort by:"));
        searchPanel.add(sortByComboBox);

        searchPanel.setBackground(new Color(142, 191, 248));

        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        // Add an action listener to the search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedLocation = (String) locationComboBox.getSelectedItem();
                String selectedSortOption = (String) sortByComboBox.getSelectedItem();
                fetchAndDisplayRooms(selectedLocation, selectedSortOption);
            }
        });

        // Create the global container for the selection panel
        selectionPanel = new JPanel(new BorderLayout());
        selectionPanel.add(titlePanel, BorderLayout.NORTH);
        selectionPanel.add(searchPanel, BorderLayout.CENTER);

        add(selectionPanel, BorderLayout.NORTH); // Add the global container to BorderLayout.NORTH

        // Create a panel to display rooms
        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
        roomPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add a 20-pixel margin all around
        roomPanel.setBackground(new Color(180, 218, 255));

        // Retrieve rooms from the database
        fetchAndDisplayRooms("All locations", "Default");

        for (Room room : rooms) {
            JPanel roomSubPanel = createRoomPanel(room);
            roomPanel.add(roomSubPanel);
            // Add vertical space between each room panel
            roomPanel.add(Box.createVerticalStrut(20));
        }

        // Create a JScrollPane for the room panel
        JScrollPane scrollPane = new JScrollPane(roomPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scroll bar

        // Add the JScrollPane to the center of the window
        add(scrollPane, BorderLayout.CENTER);

        pack(); // Automatically adjust the window size
        setVisible(true); // Make the JFrame visible
    }

    // Method to fetch and display rooms based on location and sorting option
    private void fetchAndDisplayRooms(String location, String sortOption) {
        // If "All locations" is selected, fetch all rooms
        if (location.equals("All locations")) {
            rooms = fetchRoomsFromDatabase();
        } else {
            rooms = fetchRoomsFromDatabase(location);
        }
        // Apply sorting based on selected option
        sortRooms(sortOption);
        // Update room display
        updateRoomDisplay();
    }

    // Method to fetch rooms from the database
    private ArrayList<Room> fetchRoomsFromDatabase() {
        ArrayList<Room> rooms = new ArrayList<>();



        try {
            // Connect to the database
            //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
            Connection conn = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM room");

            // Iterate through the results and create Room objects
            while (rs.next()) {
                int id = rs.getInt("room_ID");
                String roomNumber = rs.getString("room_number");
                double price = rs.getDouble("price");
                int capacity = rs.getInt("capacity");
                String location = rs.getString("location");
                byte[] imageBytes = rs.getBytes("image");

                // Create a new Room object with or without image
                Room room = new Room(id, roomNumber, price, capacity, location, imageBytes);
                rooms.add(room);
            }
            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    // Method to fetch rooms from the database based on location
    private ArrayList<Room> fetchRoomsFromDatabase(String location) {
        ArrayList<Room> rooms = new ArrayList<>();


        // Connect to the database
        try {
            //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
            Connection conn = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM room WHERE location = ?");

            pstmt.setString(1, location);
            ResultSet rs = pstmt.executeQuery();

            // Iterate through the results and create Room objects
            while (rs.next()) {
                int id = rs.getInt("room_ID");
                String roomNumber = rs.getString("room_number");
                double price = rs.getDouble("price");
                int capacity = rs.getInt("capacity");
                byte[] imageBytes = rs.getBytes("image");

                // Create a new Room object with or without image
                Room room = new Room(id, roomNumber, price, capacity, location, imageBytes);
                rooms.add(room);
            }
            conn.close();
            pstmt.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    // Method to create a panel for a room
    private JPanel createRoomPanel(Room room) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(142, 187, 255)); // Set panel color

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBackground(new Color(142, 187, 255)); // Background color for room information

        JLabel labelRoomNumber = new JLabel("Room Number: " + room.getRoomNumber());
        JLabel labelPrice = new JLabel("Price: " + room.getPrice() + " £/night");
        JLabel labelCapacity = new JLabel("Capacity: " + room.getCapacity());
        JLabel labelLocation = new JLabel("Location: " + room.getLocation());

        infoPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add a 20-pixel margin all around

        infoPanel.add(labelRoomNumber);
        infoPanel.add(labelPrice);
        infoPanel.add(labelCapacity);
        infoPanel.add(labelLocation);

        panel.add(infoPanel, BorderLayout.CENTER);

        // Display image on the right
        ImageIcon imageIcon = new ImageIcon(room.getImage());
        JLabel labelImage = new JLabel(imageIcon);

        panel.add(labelImage, BorderLayout.EAST);

        JButton buttonChoose = new JButton("Book");

        // Add an action listener to the book button
        buttonChoose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Globals.roomToDisplay = room.getId(); // Set the room ID to display globally
                System.out.println("Room Number: " + Globals.roomToDisplay);
                JFrame frame = new JFrame("Room"); // Create a new JFrame to display room details
                frame.setContentPane(new RoomGUI().genPanel); // Set the window content to the panel generated by RoomGUI

                frame.pack(); // Automatically adjust the window size
                frame.setLocationRelativeTo(null);
                frame.setVisible(true); // Make the window visible

                JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(selectionPanel);
                frameOld.dispose(); // Close the current window
            }
        });

        panel.add(buttonChoose, BorderLayout.SOUTH); // Add the book button at the bottom

        return panel; // Return the panel
    }


    // Method to update room display
    private void updateRoomDisplay() {
        // Recreate the room panel with new data
        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
        roomPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add a 20-pixel margin all around
        roomPanel.setBackground(new Color(180, 218, 255));

        for (Room room : rooms) {
            JPanel roomSubPanel = createRoomPanel(room);
            roomPanel.add(roomSubPanel);
            // Add vertical space between each room panel
            roomPanel.add(Box.createVerticalStrut(20));
        }

        // Remove all components from the main panel
        getContentPane().removeAll();

        // Add the global container containing the selection panel and the room panel
        add(selectionPanel, BorderLayout.NORTH);
        add(new JScrollPane(roomPanel), BorderLayout.CENTER);

        // Refresh the user interface
        revalidate();
        repaint();
    }

    // Method to sort rooms based on selected option
    private void sortRooms(String sortOption) {
        switch (sortOption) {
            case "Capacity: High to Low":
                rooms.sort((room1, room2) -> Integer.compare(room2.getCapacity(), room1.getCapacity()));
                break;
            case "Capacity: Low to High":
                rooms.sort((room1, room2) -> Integer.compare(room1.getCapacity(), room2.getCapacity()));
                break;
            case "Price: High to Low":
                rooms.sort((room1, room2) -> Double.compare(room2.getPrice(), room1.getPrice()));
                break;
            case "Price: Low to High":
                rooms.sort((room1, room2) -> Double.compare(room1.getPrice(), room2.getPrice()));
                break;
            default:
                // Default case: do not apply any sorting
                break;
        }
    }

    // Main method to execute the program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BrowseRoom()); // Create and display the JFrame on the event thread
    }
}
