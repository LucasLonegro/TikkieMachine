import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class DebtCollector {

    private double totalBalance = 0;
    private final Map<String, Node> debtors;

    public DebtCollector() {
        this.debtors = new TreeMap<>();
    }


    public void addDebtor(String name, double paid) {
        totalBalance += paid;
        debtors.put(name, new Node(name, this, paid));
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void solve() {
        Iterator<Map.Entry<String, Node>> debtorsIter = debtors();
        Iterator<Map.Entry<String, Node>> creditorsIter = creditors();

        if (!creditorsIter.hasNext() || !debtorsIter.hasNext()) {
            return;
        }

        Map.Entry<String, Node> creditor = creditorsIter.next();
        double canBePaid = -creditor.getValue().owes();

        while (debtorsIter.hasNext()) {
            Map.Entry<String, Node> debtor = debtorsIter.next();
            double leftToPay = debtor.getValue().owes();
            while (leftToPay > 0) {
                if (canBePaid > leftToPay) {
                    canBePaid -= leftToPay;
                    creditor.getValue().inPayments.put(debtor.getValue(), leftToPay);
                    debtor.getValue().outPayments.put(creditor.getValue(), leftToPay);
                    leftToPay = 0;
                } else {
                    leftToPay -= canBePaid;
                    creditor.getValue().inPayments.put(debtor.getValue(), canBePaid);
                    debtor.getValue().outPayments.put(creditor.getValue(), canBePaid);
                    if (!creditorsIter.hasNext())
                        break;
                    creditor = creditorsIter.next();
                    canBePaid = -creditor.getValue().owes();
                }
            }
        }
    }

    private Iterator<Map.Entry<String, Node>> debtors() {
        return debtors.entrySet().stream().filter(a -> a.getValue().owes() > 0).iterator();
    }

    private Iterator<Map.Entry<String, Node>> creditors() {
        return debtors.entrySet().stream().filter(a -> a.getValue().owes() < 0).iterator();
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder("The total paid is ").append(totalBalance)
                .append("\n").append("There are ").append(debtors.size()).append(" people")
                .append("\n").append("Each must pay ").append(totalBalance / debtors.size());

        return debtors.values().stream().reduce(ans.append("\n"), StringBuilder::append, StringBuilder::append).toString();
    }

    private static class Node {
        private final String name;
        private final DebtCollector debtCollector;
        private final double paid;
        private final Map<Node, Double> outPayments;
        private final Map<Node, Double> inPayments;

        public Node(String name, DebtCollector debtCollector, double paid) {
            this.name = name;
            this.debtCollector = debtCollector;
            this.paid = paid;
            this.outPayments = new HashMap<>();
            this.inPayments = new HashMap<>();
        }

        public String getName() {
            return name;
        }

        public DebtCollector getDebtCollector() {
            return debtCollector;
        }

        public double getPaid() {
            return paid;
        }

        public void addPayment(Node node, double payment) {
            outPayments.merge(node, payment, Double::sum);
        }

        public double owes() {
            return debtCollector.totalBalance / debtCollector.debtors.size() - paid;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(name).append(", paid: ").append(paid);
            if (owes() > 0) {
                builder.append(", owes: ").append(owes()).append("\n");
            } else {
                builder.append(", is owed: ").append(-owes()).append("\n");
            }
            if (!outPayments.isEmpty()) {
                builder.append("Must pay:\n");
                builder.append(outPayments.entrySet().stream().reduce(new StringBuilder(), (a, b) -> a.append(b.getValue()).append(" to ").append(b.getKey().name).append("\n"), StringBuilder::append)).append("\n");
            }
            if (!inPayments.isEmpty()) {
                builder.append("Is paid:\n");
                builder.append(inPayments.entrySet().stream().reduce(new StringBuilder(), (a, b) -> a.append(b.getValue()).append(" by ").append(b.getKey().name).append("\n"), StringBuilder::append)).append("\n");
            }
            return builder.toString();
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

    }
}
