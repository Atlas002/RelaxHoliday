/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

import javax.swing.*;
import java.net.URLDecoder;
import java.sql.*;
import java.awt.event.*;
import java.io.*;
import java.util.Base64;


public class CreateRoom {
    protected JPanel GenPane;
    private JLabel Tilte;
    private JLabel createRoomLabel;
    private JLabel roomNumberLabel;
    private JLabel priceLabel;
    private JLabel numberOfGuestsLabel;
    private JLabel descriptionLabel;
    private JLabel locationLabel;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JComboBox comboBox1;
    private JButton createButton;
    private JLabel imageLabel;
    private JButton imageButton;
    private JTextArea textArea1;
    private String imageData64;

    public CreateRoom()
    {
        imageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Récupérer les valeurs des champs

                String roomNumber = textField1.getText();
                String price =textField2.getText();
                String capacity = textField3.getText();
                String location = (String) comboBox1.getSelectedItem();
                String description = textArea1.getText();




                Connection conn = null;
                try {

                    // create a connection to the database
                    //conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/relaxholidays", "root", "");
                    conn = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );

                    // Créer une déclaration pour l'insertion
                    String sql = "INSERT INTO room (room_number, price, capacity, location, description, image) VALUES (?,?,?,?,?,?)";

                    PreparedStatement statement = conn.prepareStatement(sql);
                    statement.setString(1, roomNumber);
                    statement.setString(2, price);
                    statement.setString(3, capacity);
                    statement.setString(4, location);
                    statement.setString(5, description);
                    statement.setBytes(6, Base64.getDecoder().decode(imageData64));




                    // Exécuter la requête d'insertion
                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Les données ont été insérées avec succès !");
                        JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(GenPane);
                        frameOld.dispose(); //close the windows
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (conn != null)
                            conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                byte[] imageData = getImageData(selectedFile.getAbsolutePath());
                ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
                imageLabel.setIcon(icon);

                // Convert the byte array to a Base64 string for easier display
                imageData64 = Base64.getEncoder().encodeToString(imageData);

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error occurred while loading image.");
            }
        }
    }

    private byte[] getImageData(String filePath) throws IOException {
        File selectedFile = new File(filePath);
        byte[] imageData = new byte[(int) selectedFile.length()];
        try (FileInputStream fis = new FileInputStream(selectedFile)) {
            if (fis.read(imageData) == -1) {
                throw new IOException("Could not read the entire file");
            }
        }
        return imageData;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("CreateRoom");
        frame.setContentPane(new CreateRoom().GenPane);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
