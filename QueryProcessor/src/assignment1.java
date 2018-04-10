import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class assignment1 {
	
	String usr = "postgres";
	String pwd = "0119";
	String url = "jdbc:postgresql://localhost:5432/postgres";
	String cust_name, product;
	int day, month, year, quant, flag = 0;
	String[][] table = new String[500][7];

	public static void main(String[] args) {
		//args as read file named
		assignment1 dbmsass1 = new assignment1();
		dbmsass1.connect();
		dbmsass1.retreive();
	}

	// Function to connect to the database

	void connect() {
		try {
			Class.forName("org.postgresql.Driver"); // Loads the required driver
			System.out.println("Success loading Driver!");
		} catch (Exception exception) {
			System.out.println("Fail loading Driver!");
			exception.printStackTrace();
		}
	}

	// Function to retrieve from the database and process on the resultset
	// received

	void retreive() {

		try {
			Connection con = DriverManager.getConnection(url, usr, pwd); 
			// connect to the database using the password and username
			System.out.println("Success connecting server!");
			ResultSet rs; 
			// resultset object gets the set of values retrieved from the database
			boolean more;
			int i = 0, j = 0;
			Statement st = con.createStatement(); // statement created to
													// execute the query
			String ret = "select * from sales";
			rs = st.executeQuery(ret); // executing the query
			more = rs.next(); // checking if more rows available
			
			System.out.printf("%-8s", "Customer  "); // left aligned
			System.out.printf("%-7s", "Product  "); // left aligned
			System.out.printf("%-5s", "Day    " + ""); // left aligned
			System.out.printf("%-10s", "Month    "); // left aligned
			System.out.printf("%-5s", "Year   "); // left aligned
			System.out.printf("%-10s", "State    "); // left aligned
			System.out.printf("%-5s%n", "Quant  "); // left aligned
			System.out.println("========  =======  =====  ========  =====  ========  =====");
			
			while (more) {
				table[i][0] = rs.getString(1); // left aligned
				table[i][1] = rs.getString(2); // left aligned
				table[i][2] = new Integer(rs.getInt(3)).toString(); 
				table[i][3] = new Integer(rs.getInt(4)).toString(); 
				table[i][4] = new Integer(rs.getInt(5)).toString(); 
				table[i][5] = rs.getString(6); 
				table[i][6] = rs.getString(7); 
				i++;
				more = rs.next();
			}
			
			//print result of the query;
//			for (i = 0; i < 500; i++) {
//				System.out.print(i + " ");
//				for (j = 0; j < 7; j++) {
//					if(table[i]!= null){
//						System.out.print(table[i][j] + "   ");
//					}
//					else{
//						break;
//					}
//				}
//				System.out.println("");
//			}
			

//			String[][] result = new String[500][6];
			List<List> result1 = new ArrayList<>();
			List<String> custMemo = new ArrayList<String>();
//			int n = 0;
			for (i = 0; i < table.length; i++) {
				if(!custMemo.contains(table[i][0])) {
					custMemo.add(table[i][0]);
				}
			}
			System.out.println(custMemo.size());
			for (i = 0; i < custMemo.size(); i++) {//first scan for all grouping attributes
				ArrayList arrayList = new ArrayList();
				arrayList.add(custMemo.get(i));
				arrayList.add(0);
				arrayList.add(0);
				arrayList.add(0);
				arrayList.add(0);
				arrayList.add(0);
				result1.add(arrayList);
			}
			for (i = 0; i < 500; i++) {
				if(table[i][5].equals("NY")){
					int id = custMemo.indexOf(table[i][0]);
					result1.get(id).set(1, (Integer) result1.get(id).get(1) + Integer.valueOf(table[i][6]));
					result1.get(id).set(2, (Integer) result1.get(id).get(2) + 1);
				}
			}
			for (i = 0; i < 500; i++) {
				if(table[i][5].equals("NJ")){
					int id = custMemo.indexOf(table[i][0]);
					result1.get(id).set(3, (Integer)result1.get(id).get(3) + Integer.valueOf(table[i][6]));
				}
			}
			for (i = 0; i < 500; i++) {
				if(table[i][5].equals("CT")){
					int id = custMemo.indexOf(table[i][0]);
					result1.get(id).set(4, (Integer)result1.get(id).get(4) + Integer.valueOf(table[i][6]));
					result1.get(id).set(5, (Integer) result1.get(id).get(5) + 1);
				}
			}
			for(i = 0; i < result1.size(); i++) {
				System.out.print(result1.get(i).get(0) + "\t");
				System.out.print(result1.get(i).get(1) + "\t");
				System.out.print(result1.get(i).get(2) + "\t");
				System.out.print(result1.get(i).get(3) + "\t");
				System.out.print(result1.get(i).get(4) + "\t");
				System.out.println(result1.get(i).get(5));
			}
			System.out.println();
			i = 0;
			while(i < result1.size()) {
				if((Integer) result1.get(i).get(1) > 2 * (Integer) result1.get(i).get(3) || 
				Double.valueOf((Integer) result1.get(i).get(1)) / Double.valueOf((Integer) result1.get(i).get(2)) >
				Double.valueOf((Integer) result1.get(i).get(4) / Double.valueOf((Integer) result1.get(i).get(5)))) {
					i++;
				}
				else {
					result1.remove(i);
				}
					
			}
			
			for(i = 0; i < result1.size(); i++) {
				System.out.print(result1.get(i).get(0) + "\t");
				System.out.print(result1.get(i).get(1) + "\t");
				System.out.print(result1.get(i).get(2) + "\t");
				System.out.print(result1.get(i).get(3) + "\t");
				System.out.print(result1.get(i).get(4) + "\t");
				System.out.println(result1.get(i).get(5));
			}
//			result[0][0] = "cust";
//			result[0][1] = "1_sum_q";
//			result[0][2] = "1_sum_count";
//			result[0][3] = "2_sum_q";
//			result[0][4] = "3_sum_q";
//			result[0][5] = "3_sum_count";
//			for (i = 1; i <= 500; i++) {//first scan for all grouping attributes
//				if(custMemo.indexOf(table[i][0]) == -1){//new customer
//					result[n][0] = table[i][0];
//					custMemo.add(table[i][0]);
//				}
//			}
//			for (i = 1; i <= 500; i++) {//second scan for first grouping variable x
//				if(table[i][5] == "NY"){
//					result[custMemo.indexOf(table[i][0]) + 1][1] += table[i][6];
//					result[custMemo.indexOf(table[i][0]) + 1][2] += ;
//				}
//			}
//			for (i = 1; i <= 500; i++) {//third scan for second grouping variable y
//				if(table[i][5] == "NJ"){
//					result[custMemo.indexOf(table[i][0]) + 1][1] += table[i][6];
//				}
//			}
//			for (i = 1; i <= 500; i++) {//fourth scan for third grouping variable x
//				if(table[i][5] == "CT"){
//					result[custMemo.indexOf(table[i][0]) + 1][1] += table[i][6];
//				}
//			}
			
		} catch (SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
	}
}
