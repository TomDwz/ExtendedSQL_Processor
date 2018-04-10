import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Q1 {

    private static String usr = "postgres";
    private static String pwd = "0119";
    private static String url = "jdbc:postgresql://localhost:5432/postgres";
    private static ArrayList<Sale> sales = new ArrayList<>();
    private static ArrayList<ArrayList> MFtable = new ArrayList<>();
    private static ArrayList<String> GroupingAttributes = new ArrayList<>();
    public static void main(String[] args) {
        //args as read file named
        Q1 dbmsass1 = new Q1();
        dbmsass1.connect();
        dbmsass1.retreive();
        process();
    }
    // Function to connect to the database

    private void connect() {
        try {
            Class.forName("org.postgresql.Driver"); // Loads the required driver
            System.out.println("Success loading Driver!");
        } catch (Exception exception) {
            System.out.println("Fail loading Driver!");
            exception.printStackTrace();
        }
    }

    private void retreive() {

        try {
            Connection con = DriverManager.getConnection(url, usr, pwd);
            System.out.println("Success connecting server!");
            ResultSet rs;
            boolean more;
            int i = 0, j = 0;
            Statement st = con.createStatement();
            String ret = "select * from sales";
            rs = st.executeQuery(ret);
            more = rs.next();
            while (more) {
                sales.add(new Sale(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getString(6), rs.getInt(7)));
                more = rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Connection URL or username or password errors!");
            e.printStackTrace();
        }
    }
    private static void process() {
        for (int i = 0; i < sales.size(); i++) {
            String str = sales.get(i).prod + ", " + sales.get(i).cust;
            if (!GroupingAttributes.contains(str)) {
                GroupingAttributes.add(str);
            }
        }
        Collections.sort(GroupingAttributes);
        for (int i = 0; i < GroupingAttributes.size(); i++) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(GroupingAttributes.get(i));
            arrayList.addAll(Arrays.asList(0, 0, 0, 0, 0.0));
            MFtable.add(arrayList);
        }
        for (int i = 0; i < sales.size(); i++) {
            if (sales.get(i).month==10) {
                int id = GroupingAttributes.indexOf(sales.get(i).prod + ", " + sales.get(i).cust);
                MFtable.get(id).set(1, (Integer) MFtable.get(id).get(1) + sales.get(i).quant);
            }
        }
        for (int i = 0; i < sales.size(); i++) {
            if (sales.get(i).month<10) {
                int id = GroupingAttributes.indexOf(sales.get(i).prod + ", " + sales.get(i).cust);
                MFtable.get(id).set(2, (Integer) MFtable.get(id).get(2) + sales.get(i).quant);
            }
        }
        for (int i = 0; i < sales.size(); i++) {
            if (sales.get(i).month!=10) {
                int id = GroupingAttributes.indexOf(sales.get(i).prod + ", " + sales.get(i).cust);
                MFtable.get(id).set(3, (Integer) MFtable.get(id).get(3) + sales.get(i).quant);
                MFtable.get(id).set(4, (Integer) MFtable.get(id).get(4) + 1);
                MFtable.get(id).set(5, Double.valueOf((Integer) MFtable.get(id).get(3)) / (Integer) MFtable.get(id).get(4));
            }
        }
        for(ArrayList a : MFtable) {
            System.out.print(((String) a.get(0)).split(", ")[0] + "\t");
            System.out.print(((String) a.get(0)).split(", ")[1] + "\t");
            System.out.print(a.get(1) + "\t");
            System.out.print(a.get(2) + "\t");
            System.out.print(a.get(3) + "\t");
            System.out.print(a.get(4) + "\t");
            System.out.print(a.get(5) + "\t");
            System.out.println();        }
    }
}

class Sale {
    public String cust;
    public String prod;
    public int day;
    public int month;
    public int year;
    public String state;
    public int quant;

    Sale(String cust, String prod, int day, int month, int year, String state, int quant) {
        this.cust = cust;
        this.prod = prod;
        this.day = day;
        this.month = month;
        this.year = year;
        this.state = state;
        this.quant = quant;
    }
}
