import java.sql.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
public class infoFormCustomer {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField cityField;
    private JPasswordField passwordField;
    private JButton submitButton;
    protected JPanel gen;
    private JLabel title;
    private JLabel title2;
    private JLabel FirstName;
    private JLabel LastName;
    private JLabel Email;
    private JLabel Phone;
    private JLabel address;
    private JLabel City;
    private JTextField FirstNameField;
    private JTextField LastNameField;
    private JTextField PhoneField;
    private JLabel Password;



    public infoFormCustomer() {

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitButton.doClick();
                }
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Récupérer les valeurs des champs
                String firstName = FirstNameField.getText();
                String lastName = LastNameField.getText();
                String email = emailField.getText();
                String phone = PhoneField.getText();
                String address = addressField.getText();
                String city = cityField.getText();
                String pswd = new String(passwordField.getPassword());

                // Check if any field is empty
                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || city.isEmpty() || pswd.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Exit the method
                }

                Connection conn = null;
                try {
                    // db parameters - relaxholidays is the name of the database
                    conn = DriverManager.getConnection("jdbc:mysql://"+Globals.serverIP+":3306/relaxholidays", "ArthurTest", "" );
                    //checking if user already exist
                    String sqlCheck = "SELECT email FROM person WHERE email = ?";
                    PreparedStatement statementCheck = conn.prepareStatement(sqlCheck);
                    statementCheck.setString(1, email);
                    ResultSet resultSetCheck = statementCheck.executeQuery();

                    if(resultSetCheck.next()) {
                        JOptionPane.showMessageDialog(null, "Email already used, log in to access account.", "Error", JOptionPane.ERROR_MESSAGE);
                        return; // Exit the method
                    }

                    // creating sql query
                    String sql = "INSERT INTO person (first_name, last_name, email, password, phone_number, address, city,emp_status ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = conn.prepareStatement(sql);
                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, email);
                    statement.setString(4, pswd);
                    statement.setString(5, phone);
                    statement.setString(6, address);
                    statement.setString(7, city);
                    statement.setBoolean(8, false);

                    // Exécuter la requête d'insertion
                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Les données ont été insérées avec succès !");
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
                JFrame frameOld = (JFrame) SwingUtilities.getWindowAncestor(gen);
                frameOld.dispose(); //close the windows
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("infoFormCustomer");
        frame.setContentPane(new infoFormCustomer().gen);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
