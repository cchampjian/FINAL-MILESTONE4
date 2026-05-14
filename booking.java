package animalsanctuarysystem;

public class Booking {
    int id, userId, pax;
    String category, date, status;

    public Booking(int id, int userId, String category, String date, int pax, String status) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.date = date;
        this.pax = pax;
        this.status = status;
    }
}