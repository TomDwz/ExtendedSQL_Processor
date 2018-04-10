import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Project {
    private static String usr = "postgres";
    private static String pwd = "0119";
    private static String url = "jdbc:postgresql://localhost:5432/postgres";
    private static String inputFile = "query2.txt";
    private static String outputFile = "src\\Q1.java";
    private static PrintWriter writer;
    private static ArrayList<String> F_Vects_Modified;

    public static void main(String[] args) throws IOException {
        File f = new File(outputFile);
        if(f.exists() && f.isFile()) f.delete();
        writer = new PrintWriter(outputFile, "UTF-8");
        startPart();
        processPart();
        endPart();
        writer.close();
        System.out.println("Done");
    }

    private static void startPart() throws IOException {
        writer.println("import java.sql.*;\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.Arrays;\n" +
                "import java.util.Collections;\n" +
                "\n" +
                "public class Q1 {\n" +
                "\n" +
                "    private static String usr = \"" + usr + "\";\n" +
                "    private static String pwd = \"" + pwd + "\";\n" +
                "    private static String url = \"" + url + "\";\n" +
                "    private static ArrayList<Sale> sales = new ArrayList<>();\n" +
                "    private static ArrayList<ArrayList> MFtable = new ArrayList<>();\n" +
                "    private static ArrayList<String> GroupingAttributes = new ArrayList<>();" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        //args as read file named\n" +
                "        Q1 dbmsass1 = new Q1();\n" +
                "        dbmsass1.connect();\n" +
                "        dbmsass1.retreive();\n" +
                "        process();\n" +
                "    }\n" +
                "    // Function to connect to the database\n" +
                "\n" +
                "    private void connect() {\n" +
                "        try {\n" +
                "            Class.forName(\"org.postgresql.Driver\"); // Loads the required driver\n" +
                "            System.out.println(\"Success loading Driver!\");\n" +
                "        } catch (Exception exception) {\n" +
                "            System.out.println(\"Fail loading Driver!\");\n" +
                "            exception.printStackTrace();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private void retreive() {\n" +
                "\n" +
                "        try {\n" +
                "            Connection con = DriverManager.getConnection(url, usr, pwd);\n" +
                "            System.out.println(\"Success connecting server!\");\n" +
                "            ResultSet rs;\n" +
                "            boolean more;\n" +
                "            int i = 0, j = 0;\n" +
                "            Statement st = con.createStatement();\n" +
                "            String ret = \"select * from sales\";\n" +
                "            rs = st.executeQuery(ret);\n" +
                "            more = rs.next();\n" +
                "            while (more) {\n" +
                "                sales.add(new Sale(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getString(6), rs.getInt(7)));\n" +
                "                more = rs.next();\n" +
                "            }\n" +
                "        } catch (SQLException e) {\n" +
                "            System.out.println(\"Connection URL or username or password errors!\");\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }");
    }

    private static void processPart() throws IOException {
        Scanner sc = new Scanner(new File(inputFile));
        String[] selectAttributes = sc.nextLine().split(", ");
        int numGroupingVariables = Integer.valueOf(sc.nextLine());
        String[] groupingAttributes = sc.nextLine().split(", ");
        String[] F_Vects = sc.nextLine().split(", ");
        F_Vects_Modified = avgModify2(F_Vects);
        String[] selectConditionVects = sc.nextLine().split(", ");
        String having = "";
        try {
            having = sc.nextLine();
        } catch (Exception e) {
        }
        boolean isMF = true; //Else is EMF
        for (String selectConditionVect : selectConditionVects) {
            String rightPart = selectConditionVect.split("[=<>]+")[1];
            if (!rightPart.startsWith("\'") && !rightPart.startsWith("\"")) {
                try {
                    Double.valueOf(rightPart);
                } catch (NumberFormatException e) {
                    isMF = false;
                }
            }
        }

        writer.println("    private static void process() {");
        //GroupingAttributes List
        String tem = "sales.get(i)." + groupingAttributes[0];
        for (int i = 1; i < groupingAttributes.length; i++) {
            tem += " + \", \" + " + "sales.get(i)." + groupingAttributes[i];
        }
        writer.println("        for (int i = 0; i < sales.size(); i++) {\n" +
                "            String str = " + tem + ";\n" +
                "            if (!GroupingAttributes.contains(str)) {\n" +
                "                GroupingAttributes.add(str);\n" +
                "            }\n" +
                "        }\n" +
                "        Collections.sort(GroupingAttributes);");

        //Construct MFTable
        String tem1 = "0";
        for (int i = 1; i < F_Vects_Modified.size(); i++) {
            if (F_Vects_Modified.get(i).split("_")[1].equals("avg")) {
                tem1 += ", 0.0";
            } else {
                tem1 += ", 0";
            }
        }
        writer.println("        for (int i = 0; i < GroupingAttributes.size(); i++) {\n" +
                "            ArrayList arrayList = new ArrayList();\n" +
                "            arrayList.add(GroupingAttributes.get(i));\n" +
                "            arrayList.addAll(Arrays.asList(" + tem1 + "));\n" +
                "            MFtable.add(arrayList);\n" +
                "        }");


        for (int i = 1; i <= numGroupingVariables; i++) {
            //SelectCondition
            ArrayList<String> currentSelect = new ArrayList<>();
            for (String selectConditionVect : selectConditionVects) {
                if (selectConditionVect.split("\\.")[0].equals("" + i)) {
                    currentSelect.add(selectConditionVect);
                }
            }
            String ifStatement = "";
            for (int j = 0; j < currentSelect.size(); j++) {
                Pattern p = Pattern.compile("[=<>]+");
                Matcher m = p.matcher(currentSelect.get(j));
                m.find();
                String tem3 = m.group(0);
                if (tem3.equals("=")) tem3 = "==";
                if (tem3.equals("<>")) tem3 = "!=";
                String[] tem2 = currentSelect.get(j).split("\\.")[1].split("[=<>]+");
                if (tem2[1].startsWith("\'") || tem2[1].startsWith("\"")) { //String
                    if (tem3.equals("!=")) ifStatement += "!";
                    tem2[1] = tem2[1].substring(1, tem2[1].length() - 1);
                    ifStatement += "sales.get(i)." + tem2[0] + ".equals(\"" + tem2[1] + "\")";
                } else {
                    try { //Number
                        Double.valueOf(tem2[1]);
                        ifStatement += "sales.get(i)." + tem2[0] + tem3 + tem2[1];
                    } catch (NumberFormatException e) { //Variables
                        if ("cust prod state".contains(tem2[1])) { //String variables
                            if (tem3.equals("!=")) ifStatement += "!";
                            ifStatement += "sales.get(i)." + tem2[0] +
                                    ".equals(GroupingAttributes.get(j).split(\", \")[" +
                                    Arrays.asList(groupingAttributes).indexOf(tem2[1]) + "])";
                        } else if ("day month year quant".contains(tem2[1])) { //Int variables
                            ifStatement += "sales.get(i)." + tem2[0] + tem3 +
                                    "Integer.valueOf(GroupingAttributes.get(j).split(\", \")[" +
                                    Arrays.asList(groupingAttributes).indexOf(tem2[1]) + "])";
                        }
                    }
                }
                if (j < currentSelect.size() - 1) {
                    ifStatement += " && ";
                }
            }
            if (isMF) { //MF query
                writer.println("        for (int i = 0; i < sales.size(); i++) {\n" +
                        "            if (" + ifStatement + ") {\n" +
                        "                int id = GroupingAttributes.indexOf(" + tem + ");");
            } else { //EMF query
                writer.println("        for (int i = 0; i < sales.size(); i++) {\n" +
                        "        for (int j = 0; j < MFtable.size(); j++) {\n" +
                        "            if (" + ifStatement + ") {\n" +
                        "                int id = j;");
            }

            //F_Vects belong to current grouping variables
            ArrayList<String> currentF_VECT = new ArrayList<>();
            for (String F_VECT : F_Vects_Modified) {
                if (F_VECT.split("_")[0].equals("" + i)) {
                    currentF_VECT.add(F_VECT);
                }
            }
            for (String F_Vect : currentF_VECT) {
                int colNum = F_Vects_Modified.indexOf(F_Vect) + 1;
                String type = F_Vect.split("_")[1];
                String salesCol = F_Vect.split("_")[2];
                writer.print("                MFtable.get(id).set(" + colNum + ", ");
                switch (type) {
                    case "avg":
                        int sumIndex = F_Vects_Modified.indexOf(i + "_sum_" + salesCol) + 1;
                        int countIndex = F_Vects_Modified.indexOf(i + "_count_" + salesCol) + 1;
                        writer.println("Double.valueOf((Integer) MFtable.get(id).get(" + sumIndex + ")) / (Integer) MFtable.get(id).get(" + countIndex + "));");
                        break;
                    case "count":
                        writer.println("(Integer) MFtable.get(id).get(" + colNum + ") + 1);");
                        break;
                    case "max":
                        writer.println("Math.max((Integer) MFtable.get(id).get(" + colNum + "), sales.get(i)." + salesCol + "));");
                        break;
                    case "min":
                        writer.println("Math.min((Integer) MFtable.get(id).get(" + colNum + "), sales.get(i)." + salesCol + "));");
                        break;
                    case "sum":
                        writer.println("(Integer) MFtable.get(id).get(" + colNum + ") + sales.get(i)." + salesCol + ");");
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid aggregate: " + type);
                }
            }
            if (isMF) {
                writer.println("            }\n        }");
            } else {
                writer.println("            }\n        }\n        }");
            }

        }
        //Having clause
        if (!having.equals("")) {
            having = having.replaceAll("and", "&&");
            having = having.replaceAll("or", "||");
            Pattern p = Pattern.compile("[1-9]+_(avg|count|max|min|sum)_[a-z]+");
            Matcher m = p.matcher(having);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                m.appendReplacement(sb, "(" + Project.F_VectToCode(m.group()) + ")");
            }
            m.appendTail(sb);
            String ifStatement = sb.toString();
            ifStatement = ifStatement.replaceAll("=", "==");
            ifStatement = ifStatement.replaceAll(">==", ">=");
            ifStatement = ifStatement.replaceAll("<==", "<=");
            writer.println("        int i = 0;\n" +
                    "        while(i < MFtable.size()) {\n" +
                    "            if(" + ifStatement + ") {\n" +
                    "                i++;\n" +
                    "            } else {\n" +
                    "                MFtable.remove(i);\n" +
                    "            }\n" +
                    "        }");
        }
        //Print statement
        String print = "";
        for (String selectAttribute : selectAttributes) {
            int indexG = Arrays.asList(groupingAttributes).indexOf(selectAttribute);
            int indexFV = F_Vects_Modified.indexOf(selectAttribute) + 1;
            if (indexG >= 0) {
                print += "            System.out.print(((String) a.get(0)).split(\", \")[" + indexG + "] + \"\\t\");\n";
            } else {
                print += "            System.out.print(a.get(" + indexFV + ") + \"\\t\");\n";
            }
        }
        writer.println("        for(ArrayList a : MFtable) {\n" +
                print +
                "            System.out.println();" +
                "        }");
    }

    private static void endPart() {
        writer.println("    }\n" +
                "}\n" +
                "\n" +
                "class Sale {\n" +
                "    public String cust;\n" +
                "    public String prod;\n" +
                "    public int day;\n" +
                "    public int month;\n" +
                "    public int year;\n" +
                "    public String state;\n" +
                "    public int quant;\n" +
                "\n" +
                "    Sale(String cust, String prod, int day, int month, int year, String state, int quant) {\n" +
                "        this.cust = cust;\n" +
                "        this.prod = prod;\n" +
                "        this.day = day;\n" +
                "        this.month = month;\n" +
                "        this.year = year;\n" +
                "        this.state = state;\n" +
                "        this.quant = quant;\n" +
                "    }\n" +
                "}");
    }

    private static String F_VectToCode(String F_Vect) {
        int colNum = F_Vects_Modified.indexOf(F_Vect) + 1;
        if (colNum <= 0) {
            throw new IllegalArgumentException(F_Vect + "is not in F_VECTs");
        }
        String type = F_Vect.split("_")[1];
        if (type.equals("avg")) {
            return "(Double) MFtable.get(i).get(" + colNum + ")";
        } else {
            return "(Integer) MFtable.get(i).get(" + colNum + ")";
        }
    }

    private static ArrayList<String> avgModify2(String[] strs) {
        HashSet<String> hashSet = new HashSet<>();
        for (String str : strs) {
            String[] tem = str.split("_");
            if (tem[1].equals("avg")) {
                hashSet.add(tem[0] + "_sum_" + tem[2]);
                hashSet.add(tem[0] + "_count_" + tem[2]);
            }
            hashSet.add(str);
        }
        ArrayList<String> result = new ArrayList<>(hashSet);
        Collections.sort(result, new MyComparator());
        return result;
    }
}

//Sort F_VECT by [0] then [2] then reverse [1] (put avg at last)
class MyComparator implements Comparator<String> {
    public int compare(String s1, String s2) {
        String[] tem1 = s1.split("_");
        String[] tem2 = s2.split("_");
        if (tem1[0].equals(tem2[0])) {
            if (tem1[2].equals(tem2[2])) {
                return tem2[1].compareTo(tem1[1]);
            } else {
                return tem1[2].compareTo(tem2[2]);
            }
        } else {
            if (tem1[0].equals("*")) return -1;
            else if (tem2[0].equals("*")) return 1;
            else return Integer.valueOf(tem1[0]) - Integer.valueOf(tem2[0]);
        }
    }
}
