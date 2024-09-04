public class Room {
    // Private attributes of the Room class
    private int id;
    private String roomNumber;
    private double price;
    private int capacity;
    private String location;
    private byte[] image;

    // Constructor taking all attributes as parameters
    public Room(int id, String roomNumber, double price, int capacity, String location, byte[] image) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.price = price;
        this.capacity = capacity;
        this.location = location;
        this.image = image;
    }

    // Constructor overload with a version without image
    public Room(int id, String roomNumber, double price, int capacity, String location) {
        // Calling the first constructor with image parameter null
        this(id, roomNumber, price, capacity, location, null);
    }

    // Methods to get attribute values (getters)
    public int getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public double getPrice() {
        return price;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getLocation() {
        return location;
    }

    public byte[] getImage() {
        return image;
    }

    // Methods to set attribute values (setters)
    public void setId(int id) {
        this.id = id;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}