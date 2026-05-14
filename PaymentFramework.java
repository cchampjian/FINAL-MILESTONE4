package animalsanctuarysystem;

public abstract class PaymentFramework {

    protected static final double TAX_RATE = 0.12;

    protected String customerName;
    protected String transactionId;
    protected double originalAmount;
    protected double discountRate;

    public PaymentFramework(String customerName, String transactionId,
                            double originalAmount, double discountRate) {
        this.customerName = customerName;
        this.transactionId = transactionId;
        this.originalAmount = originalAmount;
        this.discountRate = discountRate;
    }

    public abstract boolean validatePayment() throws Exception;
    public abstract double applyDiscount();
    public abstract void finalizeTransaction(double totalAmount) throws Exception;

    public double applyTax(double amount) {
        return amount + (amount * TAX_RATE);
    }

    public void processInvoice() {
        try {
            printHeader();

            System.out.println("  [STEP 1] VALIDATING PAYMENT...");
            if (!validatePayment()) throw new Exception("Payment validation failed.");
            System.out.println("  OK Payment method is valid.\n");

            System.out.println("  [STEP 2] APPLYING DISCOUNT...");
            double discountedAmount = applyDiscount();
            double discountValue = originalAmount - discountedAmount;
            System.out.printf("  Discount Applied      : PHP %,.2f%n", discountValue);
            System.out.printf("  Amount After Discount : PHP %,.2f%n%n", discountedAmount);

            System.out.println("  [STEP 3] APPLYING 12% VAT...");
            double vat = discountedAmount * TAX_RATE;
            double totalAmount = applyTax(discountedAmount);
            System.out.printf("  VAT Amount   : PHP %,.2f%n", vat);
            System.out.printf("  Total Amount : PHP %,.2f%n%n", totalAmount);

            System.out.println("  [STEP 4] FINALIZING TRANSACTION...");
            finalizeTransaction(totalAmount);

        } catch (Exception e) {
            System.out.println("\n  ERROR: " + e.getMessage());
        } finally {
            System.out.println("\nInvoice processing complete.");
            printFooter();
        }
    }

    private void printHeader() {
        System.out.println("=====================================");
        System.out.println("        PAYMENT INVOICE SYSTEM       ");
        System.out.println("=====================================");
        System.out.printf("Transaction ID : %s%n", transactionId);
        System.out.printf("Customer Name  : %s%n", customerName);
        System.out.printf("Original Amount: PHP %,.2f%n", originalAmount);
        System.out.println("-------------------------------------");
    }

    private void printFooter() {
        System.out.println("=====================================");
    }
}