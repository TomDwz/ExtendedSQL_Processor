import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Output {
	private static void addToList(List<String> list, String element){
		list.add(element);
	}
	private static String[] concat(String[] a, String[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   String[] c= new String[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
	}
	private static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	private static String trim(String a) {
		String temp;
		if(isNumeric(a.substring(0,1))){
			temp = a.substring(3,a.length()) + "_1";
		}
		else{
			temp = a;
		}
		return temp;
	}
	
	private static ArrayList<String> avgModify(ArrayList<String> a){
		int i = 0;
		while(i<a.size()){
			if(a.get(i).contains("avg")){
				int j = i;//current index
				char groupingVariableNum = a.get(i).charAt(0);
				boolean flagSum = false;
				boolean flagCount = false;
				while(j>0 && a.get(j).charAt(0) == groupingVariableNum){
					//going backward from current position 
					if(a.get(j).contains("sum")){
						flagSum = true;
					}
					if(a.get(j).contains("count")){
						flagCount = true;
					}
					j--;
				}
				while(j<a.size() && a.get(j).charAt(0) == groupingVariableNum){
					//going forward from first aggregate of this grouping variable 
					if(a.get(j).contains("sum")){
						flagSum = true;
					}
					if(a.get(j).contains("count")){
						flagCount = true;
					}
					j++;
				}
				if (!flagSum && !flagCount){
					String[] temp = a.get(i).split("_");
					a.add(i, temp[0]+"_count_" + temp[2]);
					a.add(i, temp[0]+"_sum_" + temp[2]);
					i+=2;
				}
				else{
					if (!flagSum){//no sum to the left, add sum
						String[] temp = a.get(i).split("_");
						a.add(i, temp[0]+"_sum_" + temp[2]);
					}
					if (!flagCount){//no count to the left, add count
						String[] temp = a.get(i).split("_");
						a.add(i, temp[0]+"_count_" + temp[2]);
					}
				}
			}
			
			i++;
		}
		return a;
	}
	public static void main(String[] args) throws FileNotFoundException {
		Scanner sc = new Scanner(new File("query.txt"));
		String temp = sc.nextLine();
		ArrayList selectAttribute = new ArrayList<>(Arrays.asList(temp.split(", ")));
		temp = sc.nextLine();
		int groupingVariable = Integer.valueOf(temp);
		temp = sc.nextLine();
		String[] groupingAttribute = temp.split(", ");
		temp = sc.nextLine();
		ArrayList F_VECT = new ArrayList<>(Arrays.asList(temp.split(", ")));
		temp = sc.nextLine();
		String[] suchThat = temp.split(", ");
		temp = sc.nextLine();
		String having = temp;
		
		List<String> test = avgModify(F_VECT);
		for(int i = 0; i < test.size(); i++){
			System.out.print(test.get(i) + " ");
		}
		
		System.out.println(" ");
		//String[] col = concat(groupingAttribute, F_VECT);//columns for the MF structure
		
		
		try {
			File file = new File("C:\\Users\\Weizhe Dou\\workspace\\QueryProcessor\\src\\temp.java");
			/*
			 * If file gets created then the createNewFile() method would return
			 * true or if the file is already present it would return false
			 */
			boolean fvar = file.createNewFile();
			if (fvar) {
				System.out.println("File has been created successfully");
			} else {
				System.out.println("File already present at the specified location");
			}
		} catch (IOException e) {
			System.out.println("Exception Occurred:");
			e.printStackTrace();
		}
		
		List<String> lines = new ArrayList<String>();
		//lines.add("public class temp {\n\tpublic static void main(String[] args) {\n""\t\tSystem.out.println(\"Hello World\");\n\t}\n}");
		addToList(lines,"public class temp {");
		addToList(lines,"");
		addToList(lines,"\tpublic static void main(String[] args) {");
		addToList(lines,"\t\tSystem.out.println(\"Hello World\");");
		addToList(lines,"\t}");
		addToList(lines,"}");
		//write to file
		Path file = Paths.get("C:\\Users\\Weizhe Dou\\workspace\\QueryProcessor\\src\\temp.java");
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
