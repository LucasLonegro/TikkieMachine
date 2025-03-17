public class Main {
    public static void main(String[] args) {
        DebtCollector debtCollector = new DebtCollector();

        debtCollector.addDebtor("Clara", 992);
        debtCollector.addDebtor("Lucas", 50);
        debtCollector.addDebtor("Martijn", 113);
        debtCollector.addDebtor("David", 0);
        debtCollector.addDebtor("Miu", 200); // haven't said
        debtCollector.addDebtor("Kohei", 200); // haven't said
        debtCollector.solve();
        System.out.println(debtCollector);
    }
}