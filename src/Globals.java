
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Globals {
    public static int userID; //initialise the user ID to connect to his session
    public static int roomToDisplay;//when booking a room, to keep the room information
    public static int bookingToDisplay;//Same that the room but for a booking
    public static LocalDate checkInDate;//Initialize the date of check-in
    public static LocalDate checkOutDate ;//Initialize the date of check-out

    public static String serverIP = "10.70.17.148"; //IP adress used for remote server connection

    public static int graphToDisplay;

    //For a booking, to keep the duration time between the check-in date and the check-out date
    public static int getDuration()
    {
        return (int) ChronoUnit.DAYS.between(checkInDate,checkOutDate);
    }

}