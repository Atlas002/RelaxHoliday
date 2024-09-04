import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PieChartPage extends JFrame {

    protected ChartPanel chartPanel;

    public PieChartPage() {
        super();

        // Fetch data from MySQL database
        DefaultPieDataset dataset = fetchDataFromDatabase();

        String titleString = null;

        switch (Globals.graphToDisplay)
        {
            case 1:
                titleString="Hotel Room Distribution by Location";
                break;

            case 2:
                titleString="Number of Bookings Distribution by Room Number";
                break;

            case 3:
                titleString="Number of Bookings Distribution by Room Capacity";
                break;
        }

        // Create a pie chart based on the dataset
        JFreeChart chart = ChartFactory.createPieChart(
                titleString,
                dataset,
                true, // Include legend
                true,
                false
        );

        // Create a panel to display the chart
         chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        // Add the chart panel to the frame
        setContentPane(chartPanel);
    }

    private DefaultPieDataset fetchDataFromDatabase() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        try {
            // Establish connection to the MySQL database
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
            // Execute SQL query
            Statement statement = connection.createStatement();
            String sqlQuery = null;
            ResultSet resultSet =null;
            switch (Globals.graphToDisplay)
            {
                case 1: //Hotel Room Distribution by Location
                     sqlQuery = "SELECT location, COUNT(*) AS room_count FROM room GROUP BY location";
                    resultSet = statement.executeQuery(sqlQuery);

                    // Populate dataset with query results
                    while (resultSet.next()) {
                        String location = resultSet.getString("location");
                        int roomCount = resultSet.getInt("room_count");
                        dataset.setValue(location, roomCount);
                    }
                    break;

                case 2://Booking distribution by room
                     sqlQuery = "SELECT r.room_number, COUNT(*) AS booking_count " +
                             "FROM booking b " +
                             "JOIN room r ON b.room_id = r.room_ID " +
                             "GROUP BY r.room_number";
                     resultSet = statement.executeQuery(sqlQuery);

                    // Populate dataset with query results
                    while (resultSet.next()) {
                        int roomNumber = resultSet.getInt("room_number");
                        int bookingCount = resultSet.getInt("booking_count");
                        dataset.setValue("Room " + roomNumber, bookingCount);
                    }

                    break;

                case 3:
                    sqlQuery = "SELECT r.capacity, COUNT(*) AS booking_count " +
                            "FROM booking b " +
                            "JOIN room r ON b.room_id = r.room_ID " +
                            "GROUP BY r.capacity";
                    resultSet = statement.executeQuery(sqlQuery);

                    while (resultSet.next()) {
                        int capacity = resultSet.getInt("capacity");
                        int bookingCount = resultSet.getInt("booking_count");
                        dataset.setValue("Capacity " + capacity, bookingCount);
                    }

                    break;


            }



            // Close the connection
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PieChartPage page = new PieChartPage();
            page.setSize(800, 600);
            page.setLocationRelativeTo(null);
            page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            page.setVisible(true);
        });
    }
}