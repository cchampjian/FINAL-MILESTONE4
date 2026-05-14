package animalsanctuarysystem;

public class GCashPayment extends PaymentFramework {

    private String gcashNum;

    public GCashPayment(String customerName, String transactionId,
                        double originalAmount, double discountRate,
                        String gcashNum) {
        super(customerName, transactionId, originalAmount, discountRate);
        this.gcashNum = gcashNum;
    }

    @Override
    public boolean validatePayment() throws Exception {
        if (gcashNum == null || gcashNum.trim().isEmpty()) {
            throw new Exception("GCash number is required.");
        }
        if (!gcashNum.matches("\d{11}")) {
            throw new Exception("Invalid GCash number. Must be 11 digits (e.g. 09XXXXXXXXX).");
        }
        return true;
    }

    @Override
    public double applyDiscount() {
        return originalAmount - (originalAmount * discountRate);
    }

    @Override
    public void finalizeTransaction(double totalAmount) throws Exception {
        System.out.printf("  GCash Number : %s%n", gcashNum);
        System.out.printf("  Total Paid   : PHP %,.2f%n", totalAmount);
        System.out.println("  Status       : PAID");
    }
}