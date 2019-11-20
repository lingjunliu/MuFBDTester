package MuFBDTesting;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class ParseXML {
	
	public static HashMap<String, ArrayList<String>> InputInterface = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, ArrayList<String>> OutputInterface = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, ArrayList<String>> InoutInterface = new HashMap<String, ArrayList<String>>();
	
	Document doc;
	
	void loadInterface(String filePath) {
		try {
			 InputInterface.clear();
			 OutputInterface.clear();
			 InoutInterface.clear();
	         File inputFile = new File(filePath);
	         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         doc = dBuilder.parse(inputFile);
	         //System.out.println("----------------------------");
	         NodeList nlist = doc.getElementsByTagName("inputVars");
	         parse(nlist, InputInterface);
	         //System.out.println("----------------------------");
	         nlist = doc.getElementsByTagName("outputVars");
	         parse(nlist, OutputInterface);
	         //System.out.println("----------------------------");
	         nlist = doc.getElementsByTagName("inOutVars");
	         parse(nlist, InoutInterface);
	         
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	}
	
	void parse(NodeList nlist, HashMap<String, ArrayList<String>> Interface) {
		for(int i = 0; i < nlist.getLength(); i++) {
	       	 Element e = (Element) nlist.item(i);
	       	 for(int j = 0; j < e.getElementsByTagName("variable").getLength(); j++) {
		       	 Element variable = (Element) e.getElementsByTagName("variable").item(j);
		       	 //System.out.println(variable.getAttribute("name"));
		       	 String name = variable.getAttribute("name");
		       	 String dataType = "";
		       	 String testcase = "";
		       	 if(variable.getElementsByTagName("type").getLength()!=0) {
		       		 Element type = (Element) variable.getElementsByTagName("type").item(0);
		       		 if(type.getElementsByTagName("INT").getLength()!=0) {
		       			 //System.out.println("INT");
		       			 dataType = "INT";
		       		 }
		       		 if(type.getElementsByTagName("BOOL").getLength()!=0) {
		       			 //System.out.println("BOOL");
		       			 dataType = "BOOL";
		       		 }
		       		if(type.getElementsByTagName("REAL").getLength()!=0) {
		      			 //System.out.println("REAL");
		      			 dataType = "REAL";
		      		 }
		       	 }
		       	 else {
		       		 dataType = "NULL";
		       	 }
		       	 if(variable.getElementsByTagName("initialValue").getLength()!=0) {
		       		 Element initialValue = (Element) variable.getElementsByTagName("initialValue").item(0);
		       		 Element simpleValue = (Element)initialValue.getElementsByTagName("simpleValue").item(0);
		       		 //System.out.println(simpleValue.getAttribute("value"));
		       		 testcase = simpleValue.getAttribute("value");
		       	 }
		       	 else {
		       		 testcase = "NULL";
		       	 }
		       	 ArrayList<String> values = new ArrayList<String>();
		       	 values.add(dataType);
		       	 values.add(testcase);
		       	 Interface.put(name, values);
	       	 }
        }
	}
}
