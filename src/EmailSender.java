import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    public static void sendEmail(String to, String subject, String body) {
        //setting up the credentials of the gmail account used to send the emails
        final String username = "relaxholidayscustomerservice@gmail.com";
        final String password = "ogjj bjux fvtd jhrt"; //app password

        //setting up the different properties to be used to connect to GMail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.smtp.starttls.enable", "true");

        //Creating a session where we authenticate to the gmail account
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            //creating a new mail using the session
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); //Addinig the gmail account adress as sender adress
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject); //setting the subject of the email
            message.setText(body); //setting the body of the email

            Transport.send(message); //sending the email



        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}