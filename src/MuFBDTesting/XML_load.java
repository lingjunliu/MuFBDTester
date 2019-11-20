package MuFBDTesting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.lang.Math;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import MuFBDTesting.CreateGUI;
import MuFBDTesting.ParseXML;
//import FBDTester.CreateGUI;
import PLC_related.*;
import RandomPool.Item;
import Structure.TestCase;
import plcopen.inf.model.IPOU;
import plcopen.inf.model.IVariable;
import plcopen.inf.type.IConnection;
import plcopen.inf.type.IDataType;
import plcopen.inf.type.IPosition;
import plcopen.inf.type.IVariableList;
import plcopen.inf.type.group.elementary.IElementaryType;
import plcopen.inf.type.group.fbd.IBlock;
import plcopen.inf.type.group.fbd.IInVariable;
import plcopen.inf.type.group.fbd.IInVariableInBlock;
import plcopen.inf.type.group.fbd.IOutVariable;
import plcopen.inf.type.group.fbd.IOutVariableInBlock;
import plcopen.model.DataTypeType;
import plcopen.model.POUInterface;
import plcopen.model.ProjectImpl;
import plcopen.type.body.FBD;
import plcopen.xml.PLCModel;

/**
 * @author donghwan-lab
 *
 */
public class XML_load {
	
	// * Added by donghwan
	private static int testSuiteID = 1;
	private static int iteration = 1; // Iteration index for random play.
	private static float coverageLevel = 0;
	private static int testSuiteSize;
	private static int iterationCount;
	// * Declaration end
	
	public static boolean mutant = false;

	static String model = "";
	static boolean BC, ICC, CCC;

	static drawPanel panel_draw; // Draw panel for displaying diagram.
	static boolean silence = true; // silence flag.
	static int SCAN_TIME = 50;
	// static String LOG_PATH = "C:\\FBDT\\log_def.txt";
	static BufferedWriter writer;
	
	static JLabel constantTitle;
	static JLabel constantValueTitle;

	static int colorcount = 0;
	
	
	boolean focused_connection = false;

	private File file;
	static ProjectImpl PLCProject = null;
	private static int indexOfTopModule;
	private final static List<JCheckBox> outvarsCheckboxs = new ArrayList<JCheckBox>();
	private final static List<Element> elements = new ArrayList<Element>();
	private final static List<IInVariable> inputVariables = new ArrayList<IInVariable>();
	private final static List<IOutVariable> outputVariables = new ArrayList<IOutVariable>();
	private final static Set<Element> blockOutVars = new HashSet<Element>();
	private final static HashMap<String,LogicStatement> oneDepthFunctionCalcs = new HashMap<String, LogicStatement>();
	private final static List<IOutVariable> connectedOutputVars = new ArrayList<IOutVariable>();
	private final static Set<Connection> feedbackConnections = new HashSet<Connection>();
	public static int setIter=1;
	private static int maxIter = 1;
	private final static List<IBlock> blocks = new ArrayList<IBlock>();
	private final static List<DPCStore> functionDPCs = new ArrayList<DPCStore>();
	private final static List<FunctionVariable> functionBlockLocalVars = new ArrayList<FunctionVariable>();
	private final static List<FunctionVariable> functionBlockPreVars = new ArrayList<FunctionVariable>();
	private final static List<Connection> connections = new ArrayList<Connection>();
	public final static List<Element> invars = new ArrayList<Element>();
	public final static List<DPCMacro> DPCMacros = new ArrayList<DPCMacro>();
	private final static List<Element> outvars = new ArrayList<Element>();
	private final static List<functionCalcLibrary> functionCalcLibs = new ArrayList<functionCalcLibrary>();
	static Element dataPath[] = new Element[10000];
	static Connection DPConn[] = new Connection[10000];
	static int DPathCounter;
	private final static List<DPath> allPaths = new ArrayList<DPath>();
	private final static List<DPath> DPaths = new ArrayList<DPath>();
	private final static List<DPath> ICC_DPaths = new ArrayList<DPath>();
	private final static List<DPath> CCC_DPaths = new ArrayList<DPath>();
	static DPath currentDPath;
	public static List<DPCLibrary> DPCLibs = new ArrayList<DPCLibrary>();
	static int dpathCount = 0;
	static String prevDpathOutvar = "";
	static String testPath = "";
	static boolean rewriteDpcNoRecurse = false;
	static int[][] numOfEachTC = new int[100000][10];
	
	String OriginalFilePath;
	
	/**
	 * XML 파일을 불러오는 함수.
	 * 
	 * @param filePath
	 */
	void loadXML(String filePath) {
		OriginalFilePath = filePath;
		initialize();

		try {
			
			writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/output/"+"Execution_log.txt"/*CreateGUI.LogFilePath.getText()*/));
			//Thread.sleep(5000);
			file = new File(filePath);
			
			PLCProject = (ProjectImpl) PLCModel.readFromXML(file);

			getDPCLibrary();
			
			getFunctionCalculationLibrary();
			if(!mutant) {
				CreateGUI.console_println("Opening writer...");
				CreateGUI.console_println("Creating PLC Object...");
				CreateGUI.console_println("Loading library...");
				CreateGUI.console_println(" DPC library");
				CreateGUI.console_println(" Function calculation library");
				CreateGUI.console_println("Reading XML...");
				CreateGUI.console_flush();
			}
			getBasicInfo(); // Invar, outvar, block 정보 및 연결 정보들을 추출한다.
			//CreateGUI.console_println("Creating Checkbox...");
			//drawOutvarsCheckbox();
			if(!mutant) {
				CreateGUI.console_println("Load success.");
				CreateGUI.xmlStatus.setText("Load success");
				CreateGUI.xmlStatus.setForeground(Color.blue);
				CreateGUI.console_flush();
			}
			console_flush();
		} catch (Exception e) {
			
			CreateGUI.console_println("Load failed.");
			CreateGUI.xmlStatus.setText("Load failed");
			CreateGUI.xmlStatus.setForeground(Color.red);
			CreateGUI.console_println("======EXCEPTION======");
			CreateGUI.console_println(e.toString());
			CreateGUI.console_println("=====================");
			CreateGUI.console_flush();
			e.printStackTrace();
			console_flush();
			System.exit(-1);
		}
	}
	
	void initialize() {
		if(!mutant) {
			if(constantTitle!=null) {
				CreateGUI.leftPanel.remove(constantTitle);
				CreateGUI.leftPanel.remove(constantValueTitle);
			}
			
			for (JCheckBox check : CreateGUI.constantsCheckboxs) {
				CreateGUI.leftPanel.remove(check);
			}
			for (JLabel check : CreateGUI.constantLabels) {
				CreateGUI.leftPanel.remove(check);
			}
			for (JTextField check : CreateGUI.constantValues) {
				CreateGUI.leftPanel.remove(check);
			}
			CreateGUI.window.repaint();
			CreateGUI.window.setVisible(true);
			constantList.clear();
			CreateGUI.constantsCheckboxs.clear();
			
			CreateGUI.constantLabels.clear();
			CreateGUI.constantValues.clear();
		}
		
		/*
		for (JCheckBox check : outvarsCheckboxs) {
			CreateGUI.leftPanel.remove(check);
		}
		String s = CreateGUI.font.getText();
		StringTokenizer tok = new StringTokenizer(s, ",");
		String fontname = tok.nextToken();
		String fontsize = tok.nextToken();
		fontsize = fontsize.trim();
		int fontAttr;
		fontAttr = Font.PLAIN;
		
		if (CreateGUI.bold.isSelected())
			fontAttr = Font.BOLD;
		Font font = new Font(fontname, fontAttr, Integer.parseInt(fontsize));
		panel_draw.g2d.setFont(font);
		CreateGUI.console_text = "";
		CreateGUI.console_flush();*/
		if(!mutant) {
			CreateGUI.console_println("Initializing...");
			CreateGUI.console_flush();
		}
		//colorcount = 0;
		file = null;
		PLCProject = null;
		indexOfTopModule = 0;
		outvarsCheckboxs.clear();
		elements.clear();
		inputVariables.clear();
		outputVariables.clear();
		blocks.clear();
		functionDPCs.clear();
		functionBlockLocalVars.clear();
		functionBlockPreVars.clear();
		connections.clear();
		invars.clear();
		DPCMacros.clear();
		outvars.clear();
		functionCalcLibs.clear();
		DPathCounter = 0;
		
		allPaths.clear();
		DPaths.clear();
		ICC_DPaths.clear();
		CCC_DPaths.clear();
		DPCLibs.clear();
		oneDepthFunctionCalcs.clear();
		dpathCount = 0;
		prevDpathOutvar = "";
		//testPath = "C:\\Users\\Master\\Documents\\SE\\input\\FFTD.txt";
		rewriteDpcNoRecurse = false;
		setIter=1;
	}
	
	//HashMap<String, String> initialValues = new HashMap<String, String>();
	ParseXML parse_xml = new ParseXML();
	
	void getBasicInfo() {
		//panel_draw.repaint();

		indexOfTopModule = 0;
		IPOU POU = PLCProject.getPOUs().get(indexOfTopModule);
		FBD ld = (FBD) POU.getBody();

		console_println("Project Name : " + PLCProject.getProjectName());
		console_println("Project Type : " + POU.getBodyType());

		console_println("========INVARS========"); // In Variables
		for (IInVariable inVar : ld.getInVariables()) {
			inVar.setInitialLocalID(inVar.getLocalID());

			Element elem = new Element(Element.INVAR, inVar.getLocalID());
			elem.invar = inVar;

			elements.add(elem);
			inputVariables.add(inVar);

			console_println(inVar.getLocalID() + " " + inVar.getExpression());
		}
		
		console_println("========OUTVARS========"); // Out Variables
		for (IOutVariable outVar : ld.getOutVariables()) {
			outVar.setInitialLocalID(outVar.getLocalID());

			Element elem = new Element(Element.OUTVAR, outVar.getLocalID());
			for(Element el: elements){
				if(el.type == Element.INVAR)
					if(el.invar.getExpression().equals(outVar.getExpression())){
						String prevOut = outVar.getExpression();
						outVar.setExpression(prevOut+"_out");
					}
				if(el.type == Element.OUTVAR)
					if(el.outvar.getExpression().equals(outVar.getExpression())){
						String prevOut = outVar.getExpression();
						outVar.setExpression(prevOut+"_out");
					}
			}

			elem.outvar = outVar;

			elements.add(elem);
			outputVariables.add(outVar);
			

			console_println(outVar.getLocalID() + " " + outVar.getExpression());
		}

		console_println("========BLOCKS========"); // Blocks
		for (IBlock block : ld.getBlocks()) {
			block.setInitialLocalID(block.getLocalID());
			if(block.getTypeName().equals("ADD")||block.getTypeName().equals("MUL")||block.getTypeName().equals("EQ")||block.getTypeName().equals("GE")
					||block.getTypeName().equals("GT")||block.getTypeName().equals("LE")||block.getTypeName().equals("LT")||block.getTypeName().equals("AND")
					||block.getTypeName().equals("OR")||block.getTypeName().equals("XOR")||block.getTypeName().equals("MAX")||block.getTypeName().equals("MIN")) {
				block.setTypeName(block.getTypeName()+block.getInVariables().size());
			}
			if(block.getTypeName().equals("MUX")) {
				block.setTypeName(block.getTypeName()+(block.getInVariables().size()-1));
			}
			if(block.getTypeName().contains("CTU")) {
				block.setTypeName("CTU");
			}
			if(block.getTypeName().contains("CTD")) {
				block.setTypeName("CTD");
			}
			if(block.getTypeName().contains("CTUD")) {
				block.setTypeName("CTUD");
			}

			Element elem = new Element(Element.BLOCK, block.getLocalID());
			elem.block = block;

			elements.add(elem);
			blocks.add(block);
			console_println(block.getLocalID() + " " + block.getTypeName());
		}

		console_println("==============CONNECTIONS===================");
		for (IBlock block : blocks) {
			// 먼저 [Invar | Block] <======> [Block] 형식의 Connection을 추출한다.
			Element nextelem = getElementById(block.getLocalID());
			for (IInVariableInBlock inVar : block.getInVariables()) {
				for (IConnection conn : inVar.getConnectionPointIn().getConnections()) {
					Element prevelem = getElementById(conn.getRefLocalID());
					if (prevelem == null)
						continue;
					prevelem.nextElement = nextelem;
					nextelem.prevElement = prevelem;
					// nextelem은 BLOCK 형식이고, prevelem은 invar 또는 block이다.
					// 무엇인지 알 수 없으므로 여기서 결정한다.
					Connection newCon = new Connection(prevelem.LocalID, (prevelem.type == Element.BLOCK) ? (conn.getFormalParam())
							: (prevelem.invar.getExpression()), nextelem.LocalID, inVar.getFormalParameter());
					if (inVar.isNegated()) { // Negation 처리.
						newCon.negated = true;
						console_print(" ~ ");
					}
					connections.add(newCon); // 추출된 연결 정보들은 connections 에 들어간다.
					prevelem.FormalParam = inVar.getFormalParameter();

					if(prevelem.FormalParam.equals("S") || prevelem.FormalParam.equals("S1") || prevelem.FormalParam.equals("R") || prevelem.FormalParam.equals("R1")|| prevelem.FormalParam.equals("CLK"))
						setIter = 2;
					if (prevelem.type == Element.BLOCK) {
						console_println(prevelem.LocalID + prevelem.block.getTypeName() + " <-> " + nextelem.LocalID + " "
								+ nextelem.block.getTypeName());

						/*-----------block의 output의 타입을 알아내어 hashset에 넣어둔다. block output value define할때 필요함 */
						Element blockOutVar = new Element(Element.OUTVAR, prevelem.block.getLocalID());
						String prevBlockName = prevelem.block.getTypeName();
						//StringTokenizer blockNameTok = new StringTokenizer(prevBlockName, "_");
						String conprevBlockName; //= blockNameTok.nextToken();
						if(prevBlockName.endsWith("_BOOL"))
							conprevBlockName = prevBlockName.substring(0, prevBlockName.length()-5);
						else if(prevBlockName.endsWith("_DINT"))
							conprevBlockName = prevBlockName.substring(0, prevBlockName.length()-5);
						else if(prevBlockName.endsWith("_REAL"))
							conprevBlockName = prevBlockName.substring(0, prevBlockName.length()-5);
						else
							conprevBlockName = prevBlockName;
						/*
						for(DPCLibrary dpclib : DPCLibs){
							if(dpclib.functionName.equals(prevBlockName) || dpclib.functionName.equals(conprevBlockName)){
								blockOutVar.value = dpclib.functionName+prevelem.block.getLocalID()+"_"+dpclib.outVar.toLowerCase();
								if(dpclib.boolOutput)
									blockOutVar.valueType = Element.BOOLEAN;
								else
									blockOutVar.valueType = Element.INTEGER;
								//blockOutVars.add(blockOutVar);
								break;
							}
						}
						*/
						//-------------------------------------------------------------------------------------*/

					} else if (prevelem.type == Element.INVAR) {
						console_println(prevelem.LocalID + " " + prevelem.invar.getExpression() + " <-> " + nextelem.LocalID + " "
								+ nextelem.block.getTypeName());
					}
				}
			}

		}
		
		
		for (IOutVariable outVariable : outputVariables) {
			// [Block] <====> [Outvar] 연결을 추출한다.
			// [Invar] <====> [Outvar] 라는 connection은 무시.
			Element nextelem = getElementById(outVariable.getLocalID());
			for (IConnection conn : outVariable.getConnectionPointIn().getConnections()) {

				Element prevelem = getElementById(conn.getRefLocalID());
				if (prevelem == null)
					continue;
				// nextelem : outvar
				// prevelem : block
				IBlock prevblock = prevelem.block;
				IInVariable prev = prevelem.invar;
				if(prevblock != null){
					Connection newCon = new Connection(prevelem.LocalID, conn.getFormalParam(), nextelem.LocalID, outVariable.getExpression());
					if (outVariable.isNegated()) {
						newCon.negated = true;
						console_print(" ~ ");
					}
					connections.add(newCon);
					console_println("Block " + prevblock.getTypeName() + " : ");
					console_println(conn.getFormalParam() + " / " + outVariable.getExpression());

					console_println(prevelem.LocalID + " " + prevelem.block.getTypeName() + " <-> " + nextelem.LocalID + " "
							+ nextelem.outvar.getExpression());
					prevelem.nextElement = nextelem;
					nextelem.prevElement = prevelem;
				}
				if(prev != null) {
					Connection newCon = new Connection(prevelem.LocalID, prev.getExpression(), nextelem.LocalID, outVariable.getExpression());

					connections.add(newCon);
					//console_println("Block " + prevblock.getTypeName() + " : ");
					//console_println(conn.getFormalParam() + " / " + outVariable.getExpression());

					//console_println(prevelem.LocalID + " " + prevelem.block.getTypeName() + " <-> " + nextelem.LocalID + " "
					//		+ nextelem.outvar.getExpression());
					prevelem.nextElement = nextelem;
					nextelem.prevElement = prevelem;
				}
			}
		}
		/*
		for (IBlock block : blocks) {
			for (IOutVariableInBlock OutVar : block.getOutVariables()) {
				if (OutVar.isNegated()) {
					for (Connection c: connections) {
						if (c.start == block.getLocalID()) {
							System.out.println("Negated: "+c.start);
							c.negated = !c.negated;
							System.out.println(c.negated);
						}
					}
				}
			}
		}*/
		for (Connection c: connections) {
			System.out.println("start: "+c.startParam);
			System.out.println("end: "+c.endParam);
		}
		for (Element elem : elements) {
			if (elem.type == elem.BLOCK)
				continue;
			boolean existInput = false;
			boolean existOutput = false;
			for (Connection conn : connections) {
				if (conn.start == elem.LocalID)
					existInput = true;
				if (conn.end == elem.LocalID)
					existOutput = true;
				if (existInput && existOutput)
					break;
			}
			// 각각의 Element에 대해, element의 input만 있으면 outvar,
			// output만 있으면 invar로 간주하여 invars와 outvars에 삽입한다.
			if (existInput && !existOutput)
				invars.add(elem);
			if (!existInput && existOutput)
				outvars.add(elem);
		}
		
		console_println("====================================================");
		console_println("=================LOAD COMPLETE======================");
		console_println("====================================================");
		console_println("");
		console_println("=== Detected input variables ===");
		for (Element elem : invars) {
			console_println(" (" + elem.LocalID + ") " + elem.invar.getExpression());
		}
		console_println("=== Detected output variables ===");
		for (Element elem : outvars) {
			console_println(" (" + elem.LocalID + ") " + elem.outvar.getExpression());
		}
		
		if(mutant==false) {
			parse_xml.loadInterface(OriginalFilePath);
			inputList = new ArrayList<String>();
			for ( Element i :invars) {
				try {
					Integer.parseInt(i.invar.getExpression());
				} catch (NumberFormatException e){
					inputList.add(i.invar.getExpression());
					System.out.println(e);
				}
			}
			
			set = new HashSet<String>(inputList);
			set.remove("TRUE");
			set.remove("FALSE");
			inputList = new ArrayList<String>(set);
			for (String i: set) {
				System.out.println(i);
			}
			CreateGUI.console_println("Creating constants Checkbox...");
			CreateGUI.console_flush();
			checkDataType();
			drawLeftPanel();
		}
		//drawPicture(-1);
	}
	
	static Set<String> set;
	static List<String> inputList;
	static List<String> constantList = new ArrayList<String>();
	
	private static void checkDataType() {
		//for(IInVariable inVariable : inputVariables){
		System.out.println("---------------check------------------");
		
		for(String input: inputList) {
			System.out.println(input);
			for(IInVariable inVariable : inputVariables){
				if(inVariable.getExpression().equals(input)) {
					String blockType = "";
					String endParam = "";
					for(Connection c : connections){
						if(c.start == inVariable.getLocalID()){
							System.out.println(c.start);
							endParam = c.endParam;
							System.out.println(c.endParam);
							for (IBlock block : blocks) {
								if(c.end == block.getLocalID()) {
									blockType = block.getTypeName();
									System.out.println(block.getTypeName());
								}
							}
						}
					}
					if(endParam.equals("PT")||endParam.equals("PV")) {
						constantList.add(input);
					}
					/*
					if(!ParseXML.InputInterface.containsKey(input)) {
						ArrayList<String> values = new ArrayList<String>();
						values.add("NULL");
						values.add("NULL");
						ParseXML.InputInterface.put(input, values);
					}
					if(endParam.equals("PT")||endParam.equals("PV")) {
						constantList.add(input);
						if(!ParseXML.InputInterface.get(input).get(0).equals("INT")){
							ParseXML.InputInterface.get(input).set(0, "INT");
						}
						if(!ParseXML.InputInterface.get(input).get(1).equals("NULL")){
							if(Character.isLetter(ParseXML.InputInterface.get(input).get(1).charAt(0))){
								ParseXML.InputInterface.get(input).set(1, "NULL");
							}
						}
					}
					else if(endParam.equals("K")) {
						//constantList.add(input);
						if(!ParseXML.InputInterface.get(input).get(0).equals("INT")){
							ParseXML.InputInterface.get(input).set(0, "INT");
						}
						if(!ParseXML.InputInterface.get(input).get(1).equals("NULL")){
							if(Character.isLetter(ParseXML.InputInterface.get(input).get(1).charAt(0))){
								ParseXML.InputInterface.get(input).set(1, "NULL");
							}
						}
					}
					else if(endParam.equals("G")||endParam.equals("S1")||endParam.equals("R")||endParam.equals("S")||endParam.equals("R1")||endParam.equals("CLK")||endParam.equals("CU")||endParam.equals("CD")||endParam.equals("LD")) {
						if(!ParseXML.InputInterface.get(input).get(0).equals("BOOL")){
							ParseXML.InputInterface.get(input).set(0, "BOOL");
						}
						if(!ParseXML.InputInterface.get(input).get(1).equals("NULL")){
							if(!Character.isLetter(ParseXML.InputInterface.get(input).get(1).charAt(0))){
								ParseXML.InputInterface.get(input).set(1, "NULL");
							}
						}
					}
					
					else if(blockType.contains("ABS")||blockType.contains("ADD")||blockType.contains("MUL")||blockType.contains("SUB")||blockType.contains("DIV")||blockType.contains("MOD")||blockType.contains("GT")||blockType.contains("GE")
							||blockType.contains("LT")||blockType.contains("LE")||blockType.contains("MAX")||blockType.contains("MIN")||blockType.contains("LIMIT")) {
						if(!ParseXML.InputInterface.get(input).get(0).equals("INT")){
							ParseXML.InputInterface.get(input).set(0, "INT");
						}
						if(!ParseXML.InputInterface.get(input).get(1).equals("NULL")){
							if(Character.isLetter(ParseXML.InputInterface.get(input).get(1).charAt(0))){
								ParseXML.InputInterface.get(input).set(1, "NULL");
							}
						}
					}
					else if(blockType.contains("AND")||blockType.contains("OR")||blockType.contains("XOR")||blockType.contains("NOT")) {
						if(!ParseXML.InputInterface.get(input).get(0).equals("BOOL")){
							ParseXML.InputInterface.get(input).set(0, "BOOL");
						}
						if(!ParseXML.InputInterface.get(input).get(1).equals("NULL")){
							if(!Character.isLetter(ParseXML.InputInterface.get(input).get(1).charAt(0))){
								ParseXML.InputInterface.get(input).set(1, "NULL");
							}
						}
					}*/
					break;
				}
			}
		}
	}
	
	private static void drawLeftPanel() {
		JPanel leftPanel = CreateGUI.leftPanel;
		constantTitle = new JLabel("                                                          ======= Constants =======                                                          ");
		constantTitle.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
		leftPanel.add(constantTitle);
		JCheckBox cycleCheck = new JCheckBox("Scan Cycle", true);
		cycleCheck.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		cycleCheck.setEnabled(false);
		CreateGUI.constantsCheckboxs.add(cycleCheck);
		leftPanel.add(cycleCheck);
		for (String elem : inputList) {
			if (constantList.contains(elem)) {
				JCheckBox check = new JCheckBox(elem, true);
				check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
				check.setEnabled(false);
				CreateGUI.constantsCheckboxs.add(check);
				leftPanel.add(check);
			}
			else {
				if (ParseXML.InputInterface.get(elem) != null) {
					JCheckBox check = new JCheckBox(elem, false);
					check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
					CreateGUI.constantsCheckboxs.add(check);
					leftPanel.add(check);
				}
			}
		}
		constantValueTitle = new JLabel("                                             ======= Set values for constants =======                                             ");
		constantValueTitle.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
		leftPanel.add(constantValueTitle);
		//leftPanel.add(new JLabel("                        ------- Inputs -------                        "));
		JLabel input = new JLabel("Scan Cycle: INT");
		CreateGUI.constantLabels.add(input);
		leftPanel.add(input);
		JTextField value = new JTextField(20);
		value.setText("50");
		CreateGUI.constantValues.add(value);
		leftPanel.add(value);
		for (String elem : inputList) {
			if (constantList.contains(elem)) {
				input = new JLabel(elem+": "+ParseXML.InputInterface.get(elem).get(0));
				CreateGUI.constantLabels.add(input);
				leftPanel.add(input);
				value = new JTextField(20);
				if(!ParseXML.InputInterface.get(elem).get(1).equals("NULL"))
					value.setText(ParseXML.InputInterface.get(elem).get(1));
				CreateGUI.constantValues.add(value);
				leftPanel.add(value);
			}
			else {
				//System.out.println("draw" + elem);
				if (ParseXML.InputInterface.get(elem) != null) {
					input = new JLabel(elem+": "+ParseXML.InputInterface.get(elem).get(0));
				//else if(ParseXML.InoutInterface.get(elem) != null)
					//input = new JLabel(elem+": "+ParseXML.InoutInterface.get(elem).get(0));
					input.setVisible(false);
					CreateGUI.constantLabels.add(input);
					leftPanel.add(input);
					value = new JTextField(20);
					value.setVisible(false);
					CreateGUI.constantValues.add(value);
					leftPanel.add(value);
				}
			}
		}
		leftPanel.add(new JLabel("                                                            "));
		leftPanel.add(CreateGUI.executeButton);
		leftPanel.add(CreateGUI.assessButton);
		for (JCheckBox ch: CreateGUI.constantsCheckboxs) {
			ch.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                //System.out.println("Check");
	            	if (CreateGUI.constantLabels.get(CreateGUI.constantsCheckboxs.indexOf(ch)).isVisible()) {
	            		constantList.remove(inputList.get(CreateGUI.constantsCheckboxs.indexOf(ch)-1));
	            		CreateGUI.constantLabels.get(CreateGUI.constantsCheckboxs.indexOf(ch)).setVisible(false);
	            		CreateGUI.constantValues.get(CreateGUI.constantsCheckboxs.indexOf(ch)).setVisible(false);
	            	}
	            	else {
	            		constantList.add(inputList.get(CreateGUI.constantsCheckboxs.indexOf(ch)-1));
	            		CreateGUI.constantLabels.get(CreateGUI.constantsCheckboxs.indexOf(ch)).setVisible(true);
	            		CreateGUI.constantValues.get(CreateGUI.constantsCheckboxs.indexOf(ch)).setVisible(true);
	            	}
	            }
	        });
		}
		/*
		for (int i = 0; i < inputList.size(); i++) {
			if(constantList.contains(inputList.get(i))){
				if (CreateGUI.constantValues.get(i+1).getText().isEmpty()) {
					CreateGUI.executeButton.setEnabled(false);
					break;
				}
			}
		}*/
		/*
		for (JTextField t: CreateGUI.constantValues) {
			t.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CreateGUI.executeButton.setEnabled(true);
					for (int i = 0; i < inputList.size(); i++) {
						if(constantList.contains(inputList.get(i))){
							if (CreateGUI.constantValues.get(i+1).getText().isEmpty()) {
								CreateGUI.executeButton.setEnabled(false);
			  					break;
			  				}
			  			}
			  		}
				}
			});
		}*/
		/*
		leftPanel.add(new JLabel("                      ------- Outputs -------                      "));
		for (Element elem : outvars) {
			JLabel output = new JLabel(elem.outvar.getExpression()+": ");
			leftPanel.add(output);
			value = new JTextField(20);
			leftPanel.add(value);
		}
		*/
		CreateGUI.window.repaint();
		CreateGUI.window.setVisible(true);
	}
	
		/**
	 * yices 파일 시작부분에 공통적으로 들어가는 내용에 대한 출력
	 * @author donghwan
	 * @param fileName yices 파일 이름
	 */
	public static String inputs[];
	public static String itypes[];
	public static String constants[];
	public static String cTypes[];
	public static String outputs[];
	public static String otypes[];
	public static String inouts[];
	public static String scanCycle;
	//public static int maxDelay = 0;
	
	private static void loadProgramInfoFile() throws IOException {
		
		scanCycle = CreateGUI.constantValues.get(0).getText();
		
		constants = constantList.stream().toArray(String[]::new);
		List<String> cTypeList = new ArrayList<String>();
		for(String c: constantList) {
			cTypeList.add(ParseXML.InputInterface.get(c).get(1));
		}
		cTypes = cTypeList.stream().toArray(String[]::new);
		
		List<String> inputsAfter = new ArrayList<String>(inputList);
		
		for(String c: constantList) {
			inputsAfter.remove(c);
		}
		inputs = inputsAfter.stream().toArray(String[]::new);
		
		List<String> outputList = new ArrayList<String>();
		for (Element elem : outvars) {
			outputList.add(elem.outvar.getExpression());
		}
		outputs = outputList.stream().toArray(String[]::new);
		
		List<String> inoutList = new ArrayList<String>();
		for (int i=0; i < inputs.length; i++) {
			for (int j=0; j < outputs.length; j++){
				if ((inputs[i]+"_out").equals(outputs[j])){
					inoutList.add(inputs[i]);
				}
			}
		}
		inouts = inoutList.stream().toArray(String[]::new);
		/*
		BufferedReader testFile = new BufferedReader(new FileReader(testPath));
		String thisLine = "";
		

		while ((thisLine = testFile.readLine()) != null) {
			if(thisLine.startsWith("### inputs")) {
				thisLine = testFile.readLine();
				inputs = thisLine.split(", ");
			} else if(thisLine.startsWith("### test")) {
				thisLine = testFile.readLine();
				itypes = thisLine.split("\t");
			} else if(thisLine.startsWith("### scan")){
				thisLine = testFile.readLine();
				scanCycle = thisLine;
			} else if(thisLine.startsWith("### constants")){
				thisLine = testFile.readLine();
				constants = thisLine.split(", ");
			} else if(thisLine.startsWith("### cTypes")){
				thisLine = testFile.readLine();
				cTypes = thisLine.split("\t");
			} else if(thisLine.startsWith("### outputs")){
				thisLine = testFile.readLine();
				outputs = thisLine.split(", ");
			} else if(thisLine.startsWith("### oTypes")){
				thisLine = testFile.readLine();
				otypes = thisLine.split("\t");
			}
		}
		testFile.close();
		
		System.out.println("-----------Riensha-----------");
		for ( String i :inputs) {
			System.out.println(i);
		}
		
		System.out.println("-----------Riensha-----------");
		
		int inoutArrayNum=0;
		inouts = new String[inputs.length];
		for(int i = 0; i < inputs.length; i++)
			for(int j = 0 ; j < outputs.length; j++)
				if(inputs[i].equals(outputs[j])){
					inouts[inoutArrayNum]= inputs[i];
					inoutArrayNum++;
					outputs[j] = outputs[j]+"_out";
				}
		*/
		/*
		System.out.println("-----------Riensha-----------");
		for ( String i :inputs) {
			System.out.println(i);
		}
		System.out.println("-----------Riensha-----------");
		for ( String i :outputs) {
			System.out.println(i);
		}
		*/
	}
	
	/**
	 * @author donghwan
	 * @param testPath 테스트 문서를 읽어서 input의 tpye을 정함
	 * 
	 * 비슷한 내용이 여러군데에서 쓰이고 있음.
	 * testdoc.txt 내용을 읽어서 데이터의 type을 결정해야 하기 때문에 필요함.
	 * 
	 */
	static void loadTestFile(String testDoc) {
		
		for(String str: ParseXML.InoutInterface.keySet()) {
			//ArrayList<String> values = new ArrayList<String>();
			//values.add(ParseXML.InoutInterface.get(str).get(0));
			//values.add(ParseXML.InoutInterface.get(str).get(1));
			ParseXML.InputInterface.put(str, ParseXML.InoutInterface.get(str));
		}
		
		for(String testcase: ParseXML.InputInterface.keySet()) {
			for (Element elem: invars) {
				if (testcase.equals(elem.invar.getExpression())) {
					if(ParseXML.InputInterface.get(testcase).get(0).equals("INT"))
						elem.valueType = Element.INTEGER;
					if(ParseXML.InputInterface.get(testcase).get(0).equals("BOOL")) 
						elem.valueType = Element.BOOLEAN;
					if(ParseXML.InputInterface.get(testcase).get(0).equals("REAL")) 
						elem.valueType = Element.REAL;
					if(!ParseXML.InputInterface.get(testcase).get(1).equals("NULL")) 
						elem.value = ParseXML.InputInterface.get(testcase).get(1);
				}
			}
		}
		for (Element elem: outvars) {
			if (elem.outvar.getExpression().contains("_out")) {
				String str = elem.outvar.getExpression().substring(0, elem.outvar.getExpression().length()-4);
				if (ParseXML.InoutInterface.get(str)!=null) {
					ArrayList<String> values = new ArrayList<String>();
			       	values.add(ParseXML.InoutInterface.get(str).get(0));
			       	values.add("NULL");
					ParseXML.OutputInterface.put(elem.outvar.getExpression(), values);
				}
				else if (ParseXML.OutputInterface.get(str)!=null) {
					ArrayList<String> values = new ArrayList<String>();
			       	values.add(ParseXML.OutputInterface.get(str).get(0));
			       	values.add("NULL");
					ParseXML.OutputInterface.put(elem.outvar.getExpression(), values);
				}
			}
		}
		/*
		for(String testcase: ParseXML.InoutInterface.keySet()) {
			for (Element elem: outvars) {
				if (testcase.equals(elem.invar.getExpression())) {
					if(ParseXML.InoutInterface.get(testcase).get(0).equals("INT")) 
						elem.valueType = Element.INTEGER;
					if(ParseXML.InoutInterface.get(testcase).get(0).equals("BOOL")) 
						elem.valueType = Element.BOOLEAN;
					if(!ParseXML.InoutInterface.get(testcase).get(1).equals("NULL")) 
						elem.value = ParseXML.InoutInterface.get(testcase).get(1);
				}
			}
		}
		
		for ( Element i :invars) {
			System.out.println(i.invar.getExpression());
			System.out.println(i.valueType);
			System.out.println(i.value);
		}
		*/
		/*
		try {
			BufferedReader testFile = new BufferedReader(new FileReader(testDoc));

			List<Item> testCases = new ArrayList<Item>();
			List<Item> testCCases = new ArrayList<Item>();

			int mode = -1;
			String thisLine = "";
			while ((thisLine = testFile.readLine()) != null) {
				if (thisLine.trim().length() == 0)
					continue;
				if (thisLine.startsWith("//"))
					continue;
				if (thisLine.startsWith("###")) {
					if (thisLine.contains("constants")) {
						mode = 1;
					} else if (thisLine.contains("inputs")) {
						mode = 2;
					} else if (thisLine.contains("outputs")) {
						mode = 3;
					} else if (thisLine.contains("number of test cases")) {
						mode = 4;
					} else if (thisLine.contains("test cases")) {
						mode = 5;
					} else if (thisLine.contains("cTypes")){
						mode = 6;
					}
				} else {
					switch (mode) {
					case 1:
						String[] cNames = thisLine.split(", ");
						for (int i = 0; i < cNames.length; i++)
							testCCases.add(i, new Item(cNames[i]));
						break;
					case 2: // inputs
						String[] inputNames = thisLine.split(", ");
						for (int i = 0; i < inputNames.length; i++){
							System.out.println("input["+i+"]: "+inputNames[i]);
							testCases.add(i, new Item(inputNames[i]));
						}
						System.out.println("testCase: "+testCases.size());
						break;
					case 3:
						break;
					case 4:
						break;
					case 5: // test cases
						String[] testValues = thisLine.split("\t");
						System.out.println("testValues: "+testValues.length);
						if (testValues.length != testCases.size()) {
							System.err.println("ERROR: #input != #test-values");
							System.exit(-1);
						}
						for (int i = 0; i < testCases.size(); i++) {
							for (Element var : invars) {
								if (testCases.get(i).getName().equals(var.invar.getExpression())) {
									if (Character.isLetter(testValues[i].charAt(0))) {
										var.valueType = Element.BOOLEAN;
										testCases.get(i).setType(0);
									} else if (testCases.get(i).getName().contains("CNT")) {
										var.valueType = Element.INTEGER;
										testCases.get(i).setType(1);
									} else {
										var.valueType = Element.REAL;
										testCases.get(i).setType(2);
									}
									if (testCases.get(i).getType() == 0) {
										var.value = (testCases.get(i).getValue() == 0) ? "false" : "true";
									} else {
										var.value = testCases.get(i).getValue() + "";
									}
								}
							}
						}
						break;
					case 6:
						String[] testCValues = thisLine.split("\t");
						if (testCValues.length != testCCases.size()) {
							System.err.println("ERROR: #input != #test-values");
							System.exit(-1);
						}
						for (int i = 0; i < testCCases.size(); i++) {
							for (Element var : invars) {
								if (testCCases.get(i).getName().equals(var.invar.getExpression())) {
									if (Character.isLetter(testCValues[i].charAt(0))) {
										var.valueType = Element.BOOLEAN;
										testCCases.get(i).setType(0);
									} else if (testCCases.get(i).getName().contains("CNT")) {
										var.valueType = Element.INTEGER;
										testCCases.get(i).setType(1);
									} else {
										var.valueType = Element.REAL;
										testCCases.get(i).setType(2);
									}
									if (testCCases.get(i).getType() == 0) {
										var.value = (testCCases.get(i).getValue() == 0) ? "false" : "true";

									} else {
										var.value = testCCases.get(i).getValue() + "";
									}
								}
							}
						}
						break;
					} // end of switch
				} // end of else
			} // end of while
			testFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("loadTestFile(): FATAL ERROR, file not found");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("loadTestFile(): FATAL ERROR, IOException");
			System.exit(-1);
		}*/
	}

	static String acceptNonComment(BufferedReader in) {
		String s = "";
		while (true) {
			try {
				s = in.readLine();
				if (s == null)
					return null;
				if (s.length() == 0)
					continue;
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			String substr = s.substring(0, 2);
			if (!substr.equals("//")) {
				return s;
			}
		}
	}
	static int longestPathSize = 0;
	
	static Element getElementById(long LocalID) {
		// Returns element that has Local ID as given LocalID.
		for (Element elem : elements) {
			if (elem.LocalID == LocalID)
				return elem;
		}
		return null;
	}
	
	static void getFunctionCalculationLibrary() {
		// Reads function calculation libary from FUNCTIONCALC.TXT
		// and stores into functionCalcLibs(List).
		try {
			BufferedReader in = new BufferedReader(new FileReader("lib\\Calc library_updated_20190110.txt"/*CreateGUI.libraryCalcPath.getText()*/));
			String s;
			while (true) {
				s = acceptNonComment(in);
				if (s == null)
					break;
				StringTokenizer tok = new StringTokenizer(s, ":");
				String functioninfo = tok.nextToken();
				String outvar = tok.nextToken();
				outvar = outvar.trim();
				functionCalcLibrary fcl = new functionCalcLibrary();
				fcl.outvar = outvar;
				//why had character ")" been omitted?
				StringTokenizer tok2 = new StringTokenizer(functioninfo, "(, \t");
				fcl.functionName = tok2.nextToken();
				while (tok2.hasMoreTokens())
					fcl.invars.add(tok2.nextToken());
				s = acceptNonComment(in);
				//we don't need to insert space to Calc library at the very first time...
				tok = new StringTokenizer(s, " \t");
				String exp = "";
				while (tok.hasMoreTokens())
					exp += tok.nextToken();
				fcl.calculation = exp;
				functionCalcLibs.add(fcl);
			}
			s = "";
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	public static FunctionVariable GetFunctionBlockLocalVariable(String name, long block_id) {
		for (FunctionVariable var : functionBlockLocalVars) {
			if (var.name.equals(name) && var.block_id == block_id && var.type == FunctionVariable.IN) {
				return var;
			}
		}
		FunctionVariable var = new FunctionVariable(FunctionVariable.IN, name, block_id);
		functionBlockLocalVars.add(var);
		return var;
	}

	public static FunctionVariable GetFunctionBlockPreVariable(String name, long block_id) {
		for (FunctionVariable var : functionBlockPreVars) {
			if (var.name.equals(name) && var.block_id == block_id && var.type == FunctionVariable.IN) {
				return var;
			}
		}
		FunctionVariable var = new FunctionVariable(FunctionVariable.PRE, name, block_id);
		functionBlockPreVars.add(var);
		return var;
	}
	static void getDPCLibrary() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("lib\\FC library_updated_20190110.txt"));
			String s;
			String fName;
			String fType;
			String functionName;
			String[] invar = new String[10000];
			String outvar;
			DPCLibrary dpc;
			int invars_cnt = 0;
			while (true) {
				s = acceptNonComment(in); //readline
				if (s == null)
					break;
				dpc = null;
				//System.out.println("Riensha: DPClibrary s: "+s);
				StringTokenizer tok = new StringTokenizer(s, " ,()");
				fType = tok.nextToken();
				//System.out.println("Riensha: DPClibrary fType: "+fType);
				fName = tok.nextToken();
				//System.out.println("Riensha: DPClibrary fName: "+fName);
				StringTokenizer tok2 = new StringTokenizer(fName, ";");
				functionName = tok2.nextToken();
				//System.out.println("Riensha: DPClibrary functionName: "+functionName);
				if (tok2.hasMoreTokens()) {
					outvar = tok2.nextToken();
				}
				invars_cnt = 0;
				while (tok.hasMoreTokens()) {
					invar[invars_cnt++] = tok.nextToken();
				}
				for (int i = 0; i < invars_cnt; i++) {
					dpc = new DPCLibrary();
					dpc.functionName = functionName;
					if (fType.equals("BOOL")) {
						dpc.boolOutput = true;
					} else{
						dpc.boolOutput = false;
					}
					s = acceptNonComment(in);
					tok = new StringTokenizer(s, ":");
					String inoutvars = tok.nextToken();
					String cond = tok.nextToken();
					tok2 = new StringTokenizer(inoutvars, " ()\t,");
					tok2.nextToken();
					dpc.inVar = tok2.nextToken();
					dpc.outVar = tok2.nextToken();
					tok2 = new StringTokenizer(cond, " \t");
					String condition = "";
					while (tok2.hasMoreTokens()) {
						condition += tok2.nextToken();
					}
					dpc.condition = condition;
					//System.out.println("Riensha: DPClibrary dpc.inVar: "+dpc.inVar);
					//System.out.println("Riensha: DPClibrary dpc.outVar: "+dpc.outVar);
					//System.out.println("Riensha: DPClibrary dpc.condition: "+dpc.condition);
					DPCLibs.add(dpc);
				}
			}
			s = s;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	/* Get instance of functionCalcLibrary from List------------------------------------------*/
	static functionCalcLibrary findCalcLibrary(String functionName, String outvar){
		for (functionCalcLibrary fcl : functionCalcLibs) {
			if(fcl.functionName.equals(functionName) && fcl.outvar.equals(outvar))
				return fcl;
		}
		return null;
	}
	//------------------------------------------ Get instance of functionCalcLibrary from List */

	static DPCLibrary findDPCLibrary(String functionName, String invar, String outvar) {
		for (DPCLibrary dpclib : DPCLibs) {
			if (dpclib.functionName.equals(functionName) && dpclib.inVar.equals(invar) && dpclib.outVar.equals(outvar)) {
				return dpclib;
			}
		}
		return null;
	}
	public static HashMap<Long, String> in_Ts = new HashMap<Long, String>();
	public static HashMap<Long, String> CVs = new HashMap<Long, String>();
	public static HashMap<Long, String> pre_INs = new HashMap<Long, String>();
	public static HashMap<Long, String> pre_CVs = new HashMap<Long, String>();
	public static HashMap<Long, String> pre_CLKs = new HashMap<Long, String>();
	public static HashMap<Long, String> pre_Q1s = new HashMap<Long, String>();

	static void determineLogicAndRecurse(LogicStatement parent, LogicStatement l, long end) {
		if (l == null)
			return;
		if (l.type != LogicStatement.VARIABLE) {
			determineLogicAndRecurse(l, l.L1, end);
			determineLogicAndRecurse(l, l.L2, end);
			determineLogicAndRecurse(l, l.L3, end);
			return;
		}
		if (l.variable == null) {
			return;
		}
		// Find the connected block.
		//
		// start end
		//    +-----------+    +---------+
		// A--+  outParam +----+ inParam |
		// B--+           +    |         |
		//    +-----------+    +---------+
		//
		String inParam = l.variable;
		long start = 0, next = 0;
		String outParam = null;
		boolean negated = false, nextNegated = false;

		if (inParam.equals("in_T")){
			for(Connection c : connections){
				if(c.start == end){
					next = c.end;
					for(Connection cc: connections){
						if(next == cc.end)
							nextNegated = cc.negated;
					}
					if(getElementById(next).type == Element.OUTVAR && c.startParam.equals("ET")){
						String nextName = getElementById(next).outvar.getExpression();
						//여기서 바꾸면 될듯
						in_Ts.put(next, nextName);
						LogicStatement newLogic = new LogicStatement(LogicStatement.VARIABLE, inParam+next);

						if (nextNegated) {
							LogicStatement priorLogic = newLogic;
							newLogic = new LogicStatement(LogicStatement.NOT, priorLogic);
						}
						if (parent.L1 == l)
							parent.L1 = newLogic;
						else if (parent.L2 == l)
							parent.L2 = newLogic;
						else if (parent.L3 == l)
							parent.L3 = newLogic;
						return;
					}
				}
			}
//		} else if (inParam.equals("CV")){
//			for(Connection c : connections){
//				if(c.start == end){
//					next = c.end;
//					for(Connection cc: connections){
//						if(next == cc.end)
//							nextNegated = cc.negated;
//					}
//					if(getElementById(next).type == Element.OUTVAR && c.startParam.equals("CV")){
//						String nextName = getElementById(next).outvar.getExpression();
//						//여기서 바꾸면 될듯
//						CVs.put(next, nextName); 
//						LogicStatement newLogic = new LogicStatement(LogicStatement.VARIABLE, inParam+next);
//
//						if (nextNegated) {
//							LogicStatement priorLogic = newLogic;
//							newLogic = new LogicStatement(LogicStatement.NOT, priorLogic);
//						}
//						if (parent.L1 == l)
//							parent.L1 = newLogic;
//						else if (parent.L2 == l)
//							parent.L2 = newLogic;
//						else if (parent.L3 == l)
//							parent.L3 = newLogic;
//						return;
//					}
//				}
//			}
		} else if(inParam.equals("pre_IN")){
			for(Connection c : connections){
				if(c.end == end){
					start = c.start;
					outParam = c.startParam;
					negated = c.negated;
					if(getElementById(c.start).type == Element.BLOCK){
						String startName = getElementById(start).block.getTypeName();
						String funName = "";
						//StringTokenizer tok = new StringTokenizer(startName, "_");
						//funName = tok.nextToken();
						if(startName.endsWith("_BOOL"))
							funName = startName.substring(0, startName.length()-5);
						else if(startName.endsWith("_DINT"))
							funName = startName.substring(0, startName.length()-5);
						else if(startName.endsWith("_REAL"))
							funName = startName.substring(0, startName.length()-5);
						else
							funName = startName;
						pre_INs.put(start, funName + start + "_" + outParam.toLowerCase());
						LogicStatement newLogic = new LogicStatement(LogicStatement.VARIABLE, inParam+start);

						if (negated) {
							LogicStatement priorLogic = newLogic;
							newLogic = new LogicStatement(LogicStatement.NOT, priorLogic);
						}
						if (parent.L1 == l)
							parent.L1 = newLogic;
						else if (parent.L2 == l)
							parent.L2 = newLogic;
						else if (parent.L3 == l)
							parent.L3 = newLogic;
						return;
					} 
				}
			}
		} else if(inParam.equals("pre_CLK")){
			for(Connection c : connections){
				if(c.end == end){
					start = c.start;
					outParam = c.startParam;
					negated = c.negated;
					if(getElementById(c.start).type == Element.BLOCK){
						String startName = getElementById(start).block.getTypeName();
						String funName = "";
						//StringTokenizer tok = new StringTokenizer(startName, "_");
						//funName = tok.nextToken();
						if(startName.endsWith("_BOOL"))
							funName = startName.substring(0, startName.length()-5);
						else if(startName.endsWith("_DINT"))
							funName = startName.substring(0, startName.length()-5);
						else if(startName.endsWith("_REAL"))
							funName = startName.substring(0, startName.length()-5);
						else
							funName = startName;
						pre_CLKs.put(start, funName + start + "_" + outParam.toLowerCase());
						LogicStatement newLogic = new LogicStatement(LogicStatement.VARIABLE, inParam+start);

						if (negated) {
							LogicStatement priorLogic = newLogic;
							newLogic = new LogicStatement(LogicStatement.NOT, priorLogic);
						}
						if (parent.L1 == l)
							parent.L1 = newLogic;
						else if (parent.L2 == l)
							parent.L2 = newLogic;
						else if (parent.L3 == l)
							parent.L3 = newLogic;
						return;
					}
				}
			}
		} else if(inParam.equals("pre_Q1")){
			for(Connection c : connections){
				if(c.start == end){
					start = c.start;
					outParam = c.startParam;
					negated = c.negated;
					if(getElementById(c.start).type == Element.BLOCK){
						String startName = getElementById(start).block.getTypeName();
						String funName = "";
						//StringTokenizer tok = new StringTokenizer(startName, "_");
						//funName = tok.nextToken();
						if(startName.endsWith("_BOOL"))
							funName = startName.substring(0, startName.length()-5);
						else if(startName.endsWith("_DINT"))
							funName = startName.substring(0, startName.length()-5);
						else if(startName.endsWith("_REAL"))
							funName = startName.substring(0, startName.length()-5);
						else
							funName = startName;
						if(getElementById(c.end).type == Element.OUTVAR)
							pre_Q1s.put(start, getElementById(c.end).outvar.getExpression());
						else
							pre_Q1s.put(start, funName + start + "_" + outParam.toLowerCase());
						LogicStatement newLogic = new LogicStatement(LogicStatement.VARIABLE, inParam+start);

						if (negated) {
							LogicStatement priorLogic = newLogic;
							newLogic = new LogicStatement(LogicStatement.NOT, priorLogic);
						}
						if (parent.L1 == l)
							parent.L1 = newLogic;
						else if (parent.L2 == l)
							parent.L2 = newLogic;
						else if (parent.L3 == l)
							parent.L3 = newLogic;
						return;
					}
				}
			}
		} else if(inParam.equals("pre_CV")){
			for(Connection c : connections){
				if(c.start == end && c.startParam.equals("CV")){
					start = c.start;
					outParam = c.startParam;
					negated = c.negated;
					if(getElementById(c.start).type == Element.BLOCK){
						String startName = getElementById(start).block.getTypeName();
						String funName = "";
						//StringTokenizer tok = new StringTokenizer(startName, "_");
						//funName = tok.nextToken();
						if(startName.endsWith("_BOOL"))
							funName = startName.substring(0, startName.length()-5);
						else if(startName.endsWith("_DINT"))
							funName = startName.substring(0, startName.length()-5);
						else if(startName.endsWith("_REAL"))
							funName = startName.substring(0, startName.length()-5);
						else
							funName = startName;
						if(getElementById(c.end).type == Element.OUTVAR){
							pre_CVs.put(start, getElementById(c.end).outvar.getExpression());
						}else
							pre_CVs.put(start, funName + start + "_" + outParam.toLowerCase());
						LogicStatement newLogic = new LogicStatement(LogicStatement.VARIABLE, inParam+start);

						if (negated) {
							LogicStatement priorLogic = newLogic;
							newLogic = new LogicStatement(LogicStatement.NOT, priorLogic);
						}
						if (parent.L1 == l)
							parent.L1 = newLogic;
						else if (parent.L2 == l)
							parent.L2 = newLogic;
						else if (parent.L3 == l)
							parent.L3 = newLogic;
						return;
					}
				}
			}
		}
		for (Connection c : connections) {
			if (c.end == end && c.endParam.equals(inParam)) {
				start = c.start;
				outParam = c.startParam;
				negated = c.negated;
				break;
			}
		}
		if (outParam == null)
			return;

		if (!rewriteDpcNoRecurse) {
			for (DPCStore store : functionDPCs) {
				if (store.variable.equals(inParam) && store.end_local_id == end) {
					LogicStatement newLogic = store.dpc;
					if (parent.L1 == l)
						parent.L1 = newLogic;
					else if (parent.L2 == l)
						parent.L2 = newLogic;
					else if (parent.L3 == l)
						parent.L3 = newLogic;
					return;
				}
			}
		}

		if (getElementById(start).type == Element.BLOCK) {
			String startName = getElementById(start).block.getTypeName();
			String funName = "";
			//StringTokenizer tok = new StringTokenizer(startName, "-");
			//funName = tok.nextToken();
			if(startName.endsWith("_BOOL"))
				funName = startName.substring(0, startName.length()-5);
			else if(startName.endsWith("_DINT"))
				funName = startName.substring(0, startName.length()-5);
			else if(startName.endsWith("_REAL"))
				funName = startName.substring(0, startName.length()-5);
			else
				funName = startName;
			boolean found = false;
			for (functionCalcLibrary FCL : functionCalcLibs) {
				if (FCL.functionName.equals(funName) && FCL.outvar.equals(outParam)) {
					LogicStatement newLogic;
					found = true;
					if (!rewriteDpcNoRecurse) {

						if (negated) {
							newLogic = new LogicStatement("(~" + FCL.calculation + ")");
						} else {
							newLogic = new LogicStatement(FCL.calculation);
						}
					} else {
						LogicStatement logicVar = new LogicStatement(LogicStatement.VARIABLE, funName +  start+ "_" + outParam.toLowerCase());
						if (negated) {
							newLogic = new LogicStatement(LogicStatement.NOT, logicVar);
						} else {
							newLogic = logicVar;
						}
					}
					if (parent.L1 == l)
						parent.L1 = newLogic;
					else if (parent.L2 == l)
						parent.L2 = newLogic;
					else if (parent.L3 == l)
						parent.L3 = newLogic;

					if (!rewriteDpcNoRecurse) {
						rewriteDPCWithCalculation(newLogic, start);

						DPCStore newStore = new DPCStore();
						newStore.dpc = newLogic;
						newStore.variable = inParam;
						newStore.end_local_id = end;

						functionDPCs.add(newStore);
					}

					return;
				}
			}
			if (!found) {
				System.err.println("No " + startName + " Function in Calculation Library!");
			}
		}else { // INVAR
			String startName = getElementById(start).invar.getExpression();
			char[] varstr_array = startName.toCharArray();
			boolean isalphabet = false;
			boolean isreal = false;
			LogicStatement newLogic;
			if(inParam.equals("pre_CLK") || inParam.equals("pre_Q1"))
				newLogic = new LogicStatement(LogicStatement.VARIABLE, startName+"_pre");
			else
				newLogic = new LogicStatement(LogicStatement.VARIABLE, startName);
			if (negated) {
				LogicStatement priorLogic = newLogic;
				newLogic = new LogicStatement(LogicStatement.NOT, priorLogic);
			}
			if (startName.toLowerCase().equals("true")) {
				newLogic = new LogicStatement(LogicStatement.VALUE, true);
			} else if (startName.toLowerCase().equals("false")) {
				newLogic = new LogicStatement(LogicStatement.VALUE, false);
			} else {
				for (int j = 0; j < startName.length(); j++) {
					if (Character.isLetter(varstr_array[j])) {
						isalphabet = true;
						break;
					}
					if (varstr_array[j] == '.') {
						isreal = true;
					}
					if (varstr_array[j] != '.' && !Character.isLetterOrDigit(varstr_array[j])) {
						isalphabet = true;
					}
				}
				if (!isalphabet) {
					if (isreal) {
						newLogic.type = LogicStatement.VALUE;
						newLogic.valueType = LogicStatement.REAL;
						newLogic.realValue = Double.parseDouble(startName);
					} else {
						newLogic.type = LogicStatement.VALUE;
						newLogic.valueType = LogicStatement.INTEGER;
						newLogic.intValue = Integer.parseInt(startName);
					}
				}
			}

			if (parent.L1 == l)
				parent.L1 = newLogic;
			else if (parent.L2 == l)
				parent.L2 = newLogic;
			else if (parent.L3 == l)
				parent.L3 = newLogic;

			if (!rewriteDpcNoRecurse) {
				DPCStore newStore = new DPCStore();
				newStore.dpc = newLogic;
				newStore.variable = inParam;
				newStore.end_local_id = end;

				functionDPCs.add(newStore);
			}

		}
	}
	static void rewriteDPCWithCalculation(LogicStatement functionCond, long end) {
		// rewrites the "INPUT" variable into prior function block calculations.
		LogicRecursion(functionCond, end);
	}
	static void LogicRecursion(LogicStatement l, long end) {
		// IBlock block = getElem(end).block;
		determineLogicAndRecurse(l, l.L1, end);
		determineLogicAndRecurse(l, l.L2, end);
		determineLogicAndRecurse(l, l.L3, end);
	}
	
	/**
	 * @author donghwan
	 */
	private static void dPathsClear() {
		DPaths.clear();
		ICC_DPaths.clear();
		CCC_DPaths.clear();
	}
	
	static void findDataPaths() {
		console_println("\n\n\n=== Data Paths ===");
		currentDPath = new DPath();
		DPathCounter = 0;
		for (Element elem : outvars) {
			currentDPath.endElem = elem;
			for(Connection con : connections){
				if(elem.LocalID == con.end){
					if(con.startParam.equals("ET")){
						//isOutvar = false;
						DPathRecursion(elem, 0, false);
					}
					else{
						DPathRecursion(elem, 0, true);
						DPathRecursion(elem, 0, false);
					}
				}
			}
		}
		/*
		if (DPathCounter == 0
				|| (!CreateGUI.BCTestCheck.isSelected() && !CreateGUI.ICCTestCheck.isSelected() && !CreateGUI.CCCTestCheck.isSelected())) {
			System.err.println("[Error]D-Path doesn't created or none of tests are selected.");
		}*/
	}

	static void DPathRecursion(Element elem, int depth, boolean isDpath) {
		dataPath[depth] = elem;
		boolean isOutvar = true;
		boolean pathExist = false;
		for (Connection conn : connections) {
			if (conn.end == elem.LocalID) {
				pathExist = true;
				DPConn[depth] = conn;
				DPathRecursion(getElementById(conn.start), depth + 1, isDpath);
			}
		}
		if (!pathExist) {
			currentDPath.startElem = elem;
			for (int i = depth - 1; i >= 0; i--)
				currentDPath.connections.add(DPConn[i]);
			String datapath = "<";
			for (int i = depth; i >= 0; i--) {
				Element el = dataPath[i];
				if (i != 1)
					datapath += "(" + el.LocalID + ")";
				if (el.type == el.INVAR){
					datapath += el.invar.getExpression();
				}
				else if (el.type == el.OUTVAR){
					datapath += el.outvar.getExpression();
				}
				else {
					String blockname = el.block.getTypeName();
					//StringTokenizer tok = new StringTokenizer(blockname, "-");
					//blockname = tok.nextToken();
					String funName = "";
					if(blockname.endsWith("_BOOL"))
						funName = blockname.substring(0, blockname.length()-5);
					else if(blockname.endsWith("_DINT"))
						funName = blockname.substring(0, blockname.length()-5);
					else if(blockname.endsWith("_REAL"))
						funName = blockname.substring(0, blockname.length()-5);
					else
						funName = blockname;
					if (i != 1)
						datapath += funName + "_" + DPConn[i - 1].startParam.toLowerCase();
				}
				if (i != 0 && i != 1)
					datapath += ", ";


			}
			datapath += ">";
			currentDPath.DPathID = dpathCount++;
			currentDPath.datapath_str = datapath;
			currentDPath.dpath_length = depth;
			int datapath_subindex = 1;
			for (DPath prev_dpath : DPaths) {
				if (prev_dpath.dpath_length == depth)
					datapath_subindex++;
			}
			currentDPath.dpath_subindex = datapath_subindex;
			prevDpathOutvar = dataPath[0].outvar.getExpression();
			if(isDpath)
				DPaths.add(currentDPath);
			else
				allPaths.add(currentDPath);
			DPath prevDPath = currentDPath;
			currentDPath = new DPath();
			currentDPath.endElem = prevDPath.endElem;
			DPathCounter++;
		}
	}
	
	static void sortDataPaths() {
		Collections.sort(DPaths);
		if(DPaths.isEmpty())
			System.out.println("DPaths is empty");
		DPath prev = DPaths.get(0);
		console_println("===== [D-Paths] for " + prev.endElem.outvar.getExpression() + " =====");
		for (DPath path : DPaths) {
			String datapath_print = "p" + path.dpath_length + "_" + path.dpath_subindex + " [" + path.DPathID + "] : " + path.datapath_str;
			if (prev.endElem != path.endElem) {
				console_println("===== [D-Paths] for " + path.endElem.outvar.getExpression() + " =====");
			}
			console_println(datapath_print);
			prev = path;
		}
		console_println("====================\n");
		console_flush();
	}
	static void calculateDPC() {
		console_println("Calculating DPC for each D-Path...\n");
		LogicStatement[] logicStatements = new LogicStatement[10000];
		long macro_first = 0;
		long macro_second = 0;
		int macro_uniqueid = 0;
		int temp_iter=0;
		HashMap<Long, LogicStatement> tuningBlockOrderET = new HashMap<Long, LogicStatement>();
		HashMap<Long, LogicStatement> tuningBlockOrderQ = new HashMap<Long, LogicStatement>();
		/* DPath connections iteration start --------------------------------------------------------*/
		for (DPath path : allPaths) {
			Connection con;
			boolean first = true;
			boolean previouslyAdded = false;
			Connection prevcon = null;
			String functionName, fName;
			String invar;
			String outvar;
			String dpc_str = "";
			macro_first = path.DPathID;
			macro_second = 0;
			System.out.println("p" + path.dpath_length + "_" + path.dpath_subindex + " [" + path.DPathID + "] : " + path.datapath_str);
			if (longestPathSize < path.connections.size())
				longestPathSize = path.connections.size();
			/* connection size iteration start ---------------------------------------------------------------- */
			for (int i = path.connections.size() - 1; i >= 0; i--) {
				con = path.connections.get(i);
				if (prevcon == null) {
					prevcon = con;
					continue;
				}
				macro_first = con.start;
				macro_second = con.end;
				getElementById(con.end);
				fName = getElementById(con.end).block.getTypeName();
				if(fName.endsWith("_BOOL"))
					functionName = fName.substring(0, fName.length()-5);
				else if(fName.endsWith("_DINT"))
					functionName = fName.substring(0, fName.length()-5);
				else if(fName.endsWith("_REAL"))
					functionName = fName.substring(0, fName.length()-5);
				else
					functionName = fName;
				invar = con.endParam;
				outvar = prevcon.startParam;
				System.out.println(functionName + " : " + invar + " : " +outvar );
				DPCLibrary functionDPC = findDPCLibrary(functionName, invar, outvar);
				LogicStatement functionCond;
				LogicStatement functionCond_one_depth;
				
				if (functionDPC == null) {
					console_println("### Warning : " + functionName + "(" + invar + ")->" + outvar
							+ " not found in library. DPC Calculated as TRUE.");
					functionCond = new LogicStatement(LogicStatement.VARIABLE, "true");
					functionCond_one_depth = new LogicStatement(LogicStatement.VARIABLE, "true");
				} else {
					if (functionDPC.condition.toLowerCase().equals("true")) {
						functionCond = new LogicStatement(LogicStatement.VARIABLE, "true");
						functionCond_one_depth = new LogicStatement(LogicStatement.VARIABLE, "true");
					} else if (functionDPC.condition.toLowerCase().equals("false")) {
						functionCond = new LogicStatement(LogicStatement.VARIABLE, "false");
						functionCond_one_depth = new LogicStatement(LogicStatement.VARIABLE, "false");
					} else {
						functionCond = new LogicStatement(functionDPC.condition);
						functionCond_one_depth = new LogicStatement(functionDPC.condition);
					}
					System.out.println("functionCon: "+functionCond_one_depth);
				}

				if (functionCond.type == LogicStatement.VARIABLE) {
					LogicStatement varCond = functionCond;
					functionCond = new LogicStatement(LogicStatement.EMBRACE, varCond);
				}

				if (functionCond_one_depth.type == LogicStatement.VARIABLE) {
					LogicStatement varCond_one_depth = functionCond_one_depth;
					functionCond_one_depth = new LogicStatement(LogicStatement.EMBRACE, varCond_one_depth);
				}


				//for one-depth function condition connection rewriter
				rewriteDpcNoRecurse = true;
				rewriteDPCWithCalculation(functionCond_one_depth, con.end);
				rewriteDpcNoRecurse = false;

				/* To rewrite the input and output of Calc library into corresponding variable start --------------------- */
				functionCalcLibrary functionCL = findCalcLibrary(functionName, outvar);

				LogicStatement functionCalc;
				LogicStatement functionCalcforBlock;
				if (functionCL == null) {
					console_println("### Warning : " + functionName + ")->" + outvar
							+ " not found in library. Function Calculated as TRUE.");
					functionCalc = new LogicStatement(LogicStatement.VARIABLE, "true");
				} else {
					if(functionCL.calculation.equals("in_T")){
						functionCalcLibrary functionCLforB = findCalcLibrary(functionName,"in_T");
						functionCalcforBlock = new LogicStatement(functionCLforB.calculation);
						//System.out.println("Calculation library: " + functionCLforB.calculation);

						rewriteDpcNoRecurse = true;
						rewriteDPCWithCalculation(functionCalcforBlock, con.end);
						rewriteDpcNoRecurse = false;
						functionCalcforBlock.dpcl = functionDPC;
						functionCalcforBlock.blockId = con.end;
						functionCalcforBlock.blockOrder = (path.connections.size()-1)-i;
						if(functionDPC.outVar.equals("ET")){
							tuningBlockOrderET.put(con.end, functionCalcforBlock);
							if(tuningBlockOrderQ.containsKey(con.end)){
								LogicStatement tempLs = tuningBlockOrderQ.get(con.end);
								if(tempLs.blockOrder >= functionCalcforBlock.blockOrder) 
									functionCalcforBlock.blockOrder =  tempLs.blockOrder + 0.5;
							}
						}
						oneDepthFunctionCalcs.put(functionCalcforBlock.dpcl.functionName+functionCalcforBlock.blockId+"_"+functionCalcforBlock.dpcl.outVar.toLowerCase(), functionCalcforBlock);
					}
					else{
						functionCalc = new LogicStatement(functionCL.calculation);
						System.out.println("Calculation library: " + functionCL.calculation);
						rewriteDpcNoRecurse = true;
						rewriteDPCWithCalculation(functionCalc, con.end);
						rewriteDpcNoRecurse = false;

						System.out.println("con.end: " + con.end);
						for (IBlock block : blocks) {
							for (IOutVariableInBlock OutVar : block.getOutVariables()) {
								if (block.getLocalID() == con.end && OutVar.getFormalParameter().equals(outvar)) {
									if (OutVar.isNegated()) {
										functionCalc = new LogicStatement(LogicStatement.NOT, functionCalc);
									}
								}
							}
						}
						functionCalc.dpcl = functionDPC;
						functionCalc.blockId = con.end;
						functionCalc.blockOrder = (path.connections.size()-1)-i;

						if(functionCL.functionName.equals("TON")||functionCL.functionName.equals("TOF")||functionCL.functionName.equals("TP")){
							tuningBlockOrderQ.put(con.end, functionCalc);
							if(tuningBlockOrderET.containsKey(con.end)){
								LogicStatement tempLs = tuningBlockOrderET.get(con.end);
								if(tempLs.blockOrder <= functionCalc.blockOrder) 
									tempLs.blockOrder = functionCalc.blockOrder + 0.5;
								oneDepthFunctionCalcs.put(tempLs.dpcl.functionName+tempLs.blockId+"_"+tempLs.dpcl.outVar.toLowerCase(),tempLs);
							}
						}

						if(oneDepthFunctionCalcs.containsKey(functionCalc.dpcl.functionName+functionCalc.blockId+"_"+functionCalc.dpcl.outVar.toLowerCase())){
							double preBlockOrder = oneDepthFunctionCalcs.get(functionCalc.dpcl.functionName+functionCalc.blockId+"_"+functionCalc.dpcl.outVar.toLowerCase()).blockOrder;
							if(preBlockOrder > functionCalc.blockOrder)
								functionCalc.blockOrder = preBlockOrder;
						}

						oneDepthFunctionCalcs.put(functionCalc.dpcl.functionName+functionCalc.blockId+"_"+functionCalc.dpcl.outVar.toLowerCase(), functionCalc);
					}
				}

				// ---------------------- To rewrite the input and output of Calc library into corresponding variable end */
				if (functionCond_one_depth.L1.type == LogicStatement.VARIABLE
						&& functionCond_one_depth.L1.variable.toLowerCase().equals("true")) {
					if (true) {
						if (!first)
							dpc_str =  " (and" + dpc_str;
						boolean macro_found = false;
						String macro = "";
						for (DPCMacro m : DPCMacros) {
							if (m.DPC.equals(functionCond_one_depth.YicesString(0,true))) {
								macro = m.macroname;
								macro_found = true;
							}
						}
						if (!macro_found) {
							DPCMacro m = new DPCMacro();
							m.DPC = functionCond_one_depth.YicesString(0,true);
							macro = "C" + macro_first + "_" + macro_second + "_" + macro_uniqueid;
							macro_uniqueid++;
							m.macroname = macro;
							DPCMacros.add(m);
						}
						if (first)
							dpc_str = dpc_str + " " + macro;
						else
							dpc_str = dpc_str + " " + macro+ ")";
						previouslyAdded = true;
						if (first) {
							first = false;
						}
					}
				} else {
					if (!first)
						dpc_str = " (and" + dpc_str;

					boolean macro_found = false;
					String macro = "";
					for (DPCMacro m : DPCMacros) {
						if (m.DPC.equals(functionCond_one_depth.YicesString(0,true))) {	
							macro = m.macroname;
							macro_found = true;
						}
					}
					if (!macro_found) {
						DPCMacro m = new DPCMacro();
						m.DPC = functionCond_one_depth.YicesString(0,true);
						macro = "C" + macro_first + "_" + macro_second + "_" + macro_uniqueid;
						macro_uniqueid++;
						m.macroname = macro;
						DPCMacros.add(m);
					}
					if(first)
						dpc_str = dpc_str + " " + macro;
					else
						dpc_str = dpc_str + " " + macro + ")";
					if (first) {
						first = false;
					}
					previouslyAdded = true;
				}

				//for the full path of function condition connection rewriter
				rewriteDPCWithCalculation(functionCond, con.end);

				logicStatements[i] = functionCond;
				prevcon = con;
			}
			// ----------------------------------------------------------------connection size iteration end */

			if (path.connections.size() >= 3) {
				LogicStatement L = new LogicStatement(LogicStatement.AND, logicStatements[0], logicStatements[1]);
				for (int i = 2; i < path.connections.size() - 1; i++) {
					LogicStatement L_ = new LogicStatement(LogicStatement.AND, L, logicStatements[i]);
					L = L_;
				}
				path.DPC = L;
			} else {
				path.DPC = logicStatements[0];
			}
			if (dpc_str.equals(""))
				dpc_str = "true";
			path.dpc_str = dpc_str;
			for(DPath dpath : DPaths){
				if(path.datapath_str.equals(dpath.datapath_str)){
					dpath.dpc_str = dpc_str;
					dpath.DPC = path.DPC;
				}
			}
		}
		// ------------------------------------------------------------------DPath connections iteration end */



		console_println("====================================");
		console_println(" Macros ");
		console_println("====================================");
		Collections.sort(DPCMacros);
		for (DPCMacro m : DPCMacros) {
			console_println(m.macroname + " : " + m.DPC);
		}
		console_println("\n====================================");
		console_println(" DPCs");
		console_println("====================================");

		DPath prev = DPaths.get(0);
		console_println("===== [DPCs] for " + prev.endElem.outvar.getExpression() + " =====");
		for (DPath path : DPaths) {
			if (prev.endElem != path.endElem) {
				console_println("===== [DPCs] for " + path.endElem.outvar.getExpression() + " =====");
			}
			console_println("p" + path.dpath_length + "_" + path.dpath_subindex + " [" + path.DPathID + "] : " + path.dpc_str);
			prev = path;
		}
		console_println("\nDone.");
	}
	static void console_flush() {
		// Flushes console buffer into GUI Console.
		// DO NOT always flush after console_print,
		// since it requires a lot of time.
		try {
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		//CreateGUI.console_flush();
	}

	static void console_print(String str) {
		// Saves str into console buffer if silence flag is false.
		try {
			writer.write(str);
			//if (!silence)
				//CreateGUI.console_print(str);
			System.out.print(str);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void console_println(String str) {
		// Saves str into console buffer if silence flag is false.

		try {
			writer.write(str + "\r\n");
			//if (!silence)
				//CreateGUI.console_println(str);
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	/**
	 * 외북에서 호출하는 함수.
	 * 내부적으로 generateTestSuite 함수를 호출한다.
	 * 기본적으로 TR의 분할을 위해 존재한다.
	 * 
	 * @author donghwan
	 * @param maxrun 테스트 스위트 개수 (반복 횟수)
	 * @param parameter 전체 TR 분할 파라미터 (check STVR paper)
	 * @throws IOException 

	 */
	void generateTestSuites(int maxrun, int parameter) throws IOException {
		
		// 초기화
		dPathsClear();
		loadTestFile("");
		findDataPaths();
		sortDataPaths();
		calculateDPC();
		loadProgramInfoFile();
		
		// 테스트 케이스 생성 준비
		String pre = "Mutation-Based";
		List<DPath> dPath = new ArrayList<DPath>();
		for(DPath dpath: DPaths) dpath.dPathType = 0;
		dPath.addAll(DPaths);

		//		debug(); // FIXME

		// 테스트 케이스 생성 시작
		long start, end;
		start = System.currentTimeMillis();

		ArrayList<TestCase> testSet = null;
		int size = dPath.size();
		String solutionLog = "Part\tNo.\tCov.\n";
		try {
			//				Collections.shuffle(dPath);
			iteration = 0+1;
			testSet = new ArrayList<TestCase>();
			boolean isFirstYicesHeader = true;
			yicesHeaderList.clear();
			int testSetSize = testSet.size(); // 현재 testSet의 크기를 기억했다가 그만큼만 중복을 체크한다. 즉, 새로 만들어지는 newTCs 안에서의 중복은 없다고 가정한다.
			System.out.println("Test set size: "+testSetSize);
			
			ArrayList<TestCase> newTCs = generateTestSuite(pre, dPath.subList(size*(1-1)/parameter, size*1/parameter), 1, isFirstYicesHeader);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("GenerateTestCase(): FATAL ERR");
			System.exit(-1);
		}

		end = System.currentTimeMillis();

		try {
			BufferedWriter solutionLogFile = new BufferedWriter(new FileWriter("output\\"+model+"\\"+pre+"solutionCoverageLevels.txt"));
			solutionLogFile.write(solutionLog);
			solutionLogFile.write("Time elapsed: "+( end - start )/1000.0);
			solutionLogFile.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	/**
	 * 실제로 Yices를 실행해서 SMT-based TestSuite을 만드는 함수.
	 * @author donghwan
	 * @throws IOException
	 * 
	 */
	public static ArrayList<String> yicesHeaderList = new ArrayList<String>();
	public static boolean cycleCheck = false;
	private static ArrayList<TestCase> generateTestSuite(String pre, List<DPath> dPaths, int partNo, boolean isFirstYicesHeader) throws IOException { // isFirstYicesHeader 없애기
		ArrayList<TestCase> testSuite = new ArrayList<TestCase>();
		ArrayList<DPath> assertions = new ArrayList<DPath>();
		ArrayList<Integer> unsatYicesIDs = new ArrayList<Integer>();
		ArrayList<Integer> unsatAssertIDs = new ArrayList<Integer>();
		
		boolean isSolution = false;
		boolean coveredAsserts[] = null;

		int assertID = 0;
		int totalAsserts = 0;
		int counter = 1;


		int beforeUnsatSize = 0;
		String log = "";

		// 매 반복시마다 하나의 test case 생성
		boolean isFirstYices = true, isSatisfiedAll = false;
		assertID = 0;
		totalAsserts = 0;
		assertions.clear();
		boolean isOutVar = false;
		long start = System.currentTimeMillis();
		yicesHeader(pre + "test" + partNo +"-"+ counter + ".ys", isFirstYicesHeader, isFirstYices);
		//if(isFirstYicesHeader && isFirstYices) { // isFirstYices로 바꾸기
		
		for(int tempSetIter = setIter; tempSetIter >=0; tempSetIter--) {
			/* Writing feedback in-out variables starts --------------------------------------------------------------*/

			for(int i = 0; i<inputs.length; i++){
				boolean isFeedback = false;
				for(Connection feedbackCon : feedbackConnections){
					if(feedbackCon.startParam.equals(inputs[i])){
						String definition = "";
						if(tempSetIter != 0) {
							//yicesWriter.write("(define " + "M_" + feedbackCon.startParam + "_t" + tempSetIter + "::");
							definition += "(define ";
							if(mutant==true){
								//System.out.println(mutant);
								definition += "M_";
							}
							definition += feedbackCon.startParam + "_t" + tempSetIter + "::";
						} else {
							//yicesWriter.write("(define " + "M_" + feedbackCon.startParam + "::");
							definition += "(define ";
							if(mutant==true){
								definition += "M_";
							}
							definition += feedbackCon.startParam + "::";
						}
						boolean isBool= true;
								if(getElementById(feedbackCon.start).valueType == Element.BOOLEAN)
									isBool = true;
								else
									isBool = false;
						if(isBool){
							//yicesWriter.write("bool " + "M_" + feedbackCon.endParam + "_t" + (tempSetIter+1) + ")\r\n");
							definition += "bool ";
							if(mutant==true){
								definition += "M_";
							}
							definition += feedbackCon.endParam + "_t" + (tempSetIter+1) + ")\r\n";
						}else {
							//yicesWriter.write("int " + "M_" + feedbackCon.endParam + "_t" + (tempSetIter+1) + ")\r\n");
							definition += "real ";
							if(mutant==true){
								definition += "M_";
							}
							definition += feedbackCon.endParam + "_t" + (tempSetIter+1) + ")\r\n";
						}
						isFeedback = true;
						yicesHeaderList.add(definition);
					}
				}

				if(!isFeedback&&!mutant){
					String definition = "";
					if(tempSetIter != 0) {
						//yicesWriter.write("(define " + inputs[i] + "_t" + tempSetIter + "::");
						definition += "(define " + inputs[i] + "_t" + tempSetIter + "::";
					} else {
						//yicesWriter.write("(define " + inputs[i] + "::");
						definition += "(define " + inputs[i] + "::";
					} 
					
					if(/*Character.isLetter(itypes[i].charAt(0))*/ParseXML.InputInterface.get(inputs[i]).get(0).equals("BOOL")){
						/*yicesWriter.write("bool " //+ itypes[i] 
								+ ")\r\n");*/
						definition += "bool )\r\n";
					} else{
						/*
						StringTokenizer range = new StringTokenizer(itypes[i], "-");
						String from = range.nextToken();
						String to = null;
						if (range.hasMoreTokens()) {
							to = range.nextToken();
						}
						if (to == null){
							yicesWriter.write("int " //+ itypes[i] 
									+ ")\r\n");
							definition += "int )\r\n";
						}
						else {
							//yicesWriter.write("(subrange "+ from + " " + to + "))\r\n");
							definition += "(subrange "+ from + " " + to + "))\r\n";
						}*/
						definition += "real )\r\n";
					}
					yicesHeaderList.add(definition);
				}
			}
			// --------------------------------------------------------------- Writing feedback in-out variables ends */
			
			/* Writing function calculation definitions starts ------------------------------------------------------*/
			HashMap<String, String> outVarDefs = new HashMap<String, String>();
			for(double i = longestPathSize - 1; i >= 0 ; i=i-0.5)
				for(String key : oneDepthFunctionCalcs.keySet()){
					LogicStatement ls = oneDepthFunctionCalcs.get(key);
					Element lastBlockElem = getElementById(ls.blockId); 
					for (Connection con : connections){
						String outVarDef = ""; String id="";
						Element outvarElem = getElementById(con.end);
						if(getElementById(con.start) == lastBlockElem && ls.dpcl.outVar.equals(con.startParam)){
							id = String.valueOf(con.start)+con.startParam;

							if(outvarElem.type ==Element.OUTVAR){
								if(tempSetIter != 0){
									outVarDef += "(define ";
									if(mutant==true){
										outVarDef += "M_";
									}
									outVarDef += outvarElem.outvar.getExpression() + "_t" + tempSetIter + "::";
								}
								else{
									outVarDef += "(define ";
									if(mutant==true){
										outVarDef += "M_";
									}
									outVarDef += outvarElem.outvar.getExpression() + "::";
								}
							}else{
								if(tempSetIter != 0){
									outVarDef += "(define ";
									if(mutant==true){
										outVarDef += "M_";
									}
									outVarDef += ls.dpcl.functionName + ls.blockId + "_" + ls.dpcl.outVar.toLowerCase() + "_t" + tempSetIter + "::";
								}
								else{
									outVarDef += "(define ";
									if(mutant==true){
										outVarDef += "M_";
									}
									outVarDef += ls.dpcl.functionName + ls.blockId + "_" + ls.dpcl.outVar.toLowerCase() + "::";
								}
							}
							if(ls.dpcl.boolOutput){
								String blockName = getElementById(ls.blockId).block.getTypeName();
								if(blockName.equals("TON") || blockName.equals("TOF") || blockName.equals("TP") || blockName.equals("CTU")||blockName.equals("CTD")||blockName.equals("CTUD"))
									outVarDef += "bool "+ls.YicesString(tempSetIter,true)+")\r\n";
								else
									outVarDef += "bool "+ls.YicesString(tempSetIter,false)+")\r\n";
							}else{
								String blockName = getElementById(ls.blockId).block.getTypeName();
								if(blockName.equals("TON") || blockName.equals("TOF") || blockName.equals("TP")|| blockName.equals("CTU")||blockName.equals("CTD")||blockName.equals("CTUD"))
									if(con.startParam.equals("ET"))
										outVarDef += "real "+ls.YicesString(tempSetIter,true)+")\r\n";
									else
										outVarDef += "bool "+ls.YicesString(tempSetIter,false)+")\r\n";
								else {
									String str = "";
									str = ls.YicesString(tempSetIter,false);
									if(blockName.equals("SEL")) {
										if(str.contains("true")||str.contains("false"))
											outVarDef += "bool ";
										else
											outVarDef += "real ";
									}else {
										outVarDef += "real ";
									}
									outVarDef += str + ")\r\n";
								}
							}
						}
						if(id != "" && ls.blockOrder == i){
							if(!outVarDefs.containsValue(outVarDef)){
								//yicesWriter.write(outVarDef);
								yicesHeaderList.add(outVarDef);
								outVarDefs.put(id,outVarDef);
							}
						}
					}
				}
			// -------------------------------------------------------- Writing function calculation definitions ends */
			
			List<String> invarList = new ArrayList<String>();
			
			for (IInVariable inVar : inputVariables) {
				invarList.add(inVar.getExpression());
			}
			
			String write = "";
			
			for (Connection c: connections) {
				if(invarList.contains(c.startParam)&&Arrays.asList(outputs).contains(c.endParam)) {
					write += "(define ";
					if(mutant==true){
						write += "M_";
					}
					if (tempSetIter!=0)
						write += c.endParam + "_t" + tempSetIter + "::";
					else 
						write += c.endParam + "::";
					String datatype = ParseXML.OutputInterface.get(c.endParam).get(0);
					write += datatype.toLowerCase() + " ";
					if (!Arrays.asList(inputs).contains(c.startParam)) 
						write += c.startParam.toLowerCase() + ")\r\n";
					else {
						if(mutant==true&&Arrays.asList(inouts).contains(c.startParam)){
							write += "M_";
						}
						if (tempSetIter!=0) {
							if (c.startParam.equals("TSP")||c.startParam.equals("PTSP"))
								write += c.startParam + "_out" + "_t" + tempSetIter + ")\r\n";
							else
								write += c.startParam +"_t" + tempSetIter + ")\r\n";
						}
						else {
							if (c.startParam.equals("TSP")||c.startParam.equals("PTSP"))
								write += c.startParam + "_out" + ")\r\n";
							else
								write += c.startParam + ")\r\n";
						}
					}
				}
			}
			yicesHeaderList.add(write);
			//yicesWriter.write("\r\n");
			yicesHeaderList.add("\r\n");
		}
		feedbackConnections.clear();
		/*} else {
			for(String definition: yicesHeaderList) {
				yicesWriter.write(definition);
			}
			yicesWriter.write("\r\n");
		}*/

		/* Writing DPCs and assert+ Starts --------------------------------------------------------------------------------------- */
		
		
		//yicesFooter(pre + "test" + partNo+"-"+ counter + ".ys");
			
		return testSuite;
	}
	private static void yicesHeader(String fileName, boolean yices1, boolean yices2) {
		//try {
			String write;
			if(!mutant){
				write = ";; Environment setting\r\n" + "(set-evidence! true)\r\n" + "(set-verbosity! 1)\r\n";
			}
			else{
				write = ";;Mutant\r\n";
			}
			if (yices1 && yices2) {
			
			write += ";; Rule 1-1. Define constant and variables\r\n";
			//			System.out.println("Rule 1-1");
			if(constants.length>0)
				if(!constants[0].equals(""))
					for(int i = 0 ; i < constants.length; i++){
						for(IInVariable inVariable : inputVariables){
							if(inVariable.getExpression().equals(constants[i])){
								for(Connection c : connections){
									if(c.start == inVariable.getLocalID()){
										if(c.endParam.equals("PT")){
											int delay = Integer.parseInt(cTypes[i]);
											int TONblockIter = delay / Integer.parseInt(scanCycle);
											if(setIter<TONblockIter)
												setIter = TONblockIter;
										}
										else if(c.endParam.equals("PV")){
											if(setIter<Math.abs(Integer.parseInt(cTypes[i])))
												setIter = Math.abs(Integer.parseInt(cTypes[i]));
										}
									}
								}
							}
						}
					}
			cycleCheck = true;
			
			/* Writing scan cyle and constants Starts----------------------------- */
			if(!mutant){
				write += ";; constant variables\r\n";
				write += "(define SCAN_TIME::int "+scanCycle+")\r\n";
				if(constants.length>0)
					if(!constants[0].equals(""))
						for(int i=0; i<constants.length; i++) {
							write += "(define "+constants[i].trim()+"::";
							if(Character.isLetter(cTypes[i].charAt(0)))
								write += "bool " + cTypes[i] + ")\r\n";
							else
								write += "int " + cTypes[i] + ")\r\n";
						}
			}
			write += "\r\n";
			
			// ---------------------------- Writing scan cycle and constants Ends */
			

			/*feedbackConnection starts--------------------------------------------*/
			for(int i = 0 ; i<inputs.length; i++)
				for(int j = 0; j<outputs.length; j++)
					if ((inputs[i]+"_out").equals(outputs[j])){
						Long preID=0L,nextID=0L;
						String preExpr="", nextExpr="";
						for(Element elem : elements){
							if(elem.type == Element.OUTVAR && elem.outvar.getExpression().equals(outputs[j])){
								nextID = elem.LocalID;
								nextExpr = elem.outvar.getExpression();
							}
							if(elem.type == Element.INVAR && elem.invar.getExpression().equals(inputs[i])){
								preID = elem.LocalID;
								preExpr = elem.invar.getExpression();
							}
						}
						Connection newCon = new Connection(preID,preExpr,nextID,nextExpr);
						feedbackConnections.add(newCon);
					}
			// --------------------------------------------feedbackConnection ends */

			for (Connection con : connections) {
				Element conIn = getElementById(con.start);
				Element conOut = getElementById(con.end);
				if (conIn.type == Element.INVAR) {
					//System.out.println("--------- connection ---------");
					//System.out.println(conIn.invar.getExpression());
					if (conOut.type == Element.BLOCK)
						if (conOut.block.getTypeName().equals("R_TRIG"))
							if (!mutant)
								write += "(define " + conIn.invar.getExpression() + "_t" + (setIter+2) + "::bool false)\r\n";
							//System.out.println(conOut.block.getTypeName());
				}
				else if (conIn.type == Element.BLOCK) {
					//System.out.println("--------- connection ---------");
					//System.out.println(conIn.block.getTypeName());
					if (conOut.type == Element.OUTVAR) {
						if (conIn.block.getTypeName().equals("TON") || conIn.block.getTypeName().equals("TOF") || conIn.block.getTypeName().equals("TP") || 
							conIn.block.getTypeName().equals("CTU") || conIn.block.getTypeName().equals("CTD") || conIn.block.getTypeName().equals("CTUD")) {
							for (IOutVariable outVariable : outputVariables) {
								if (outVariable.getExpression().contains(conOut.outvar.getExpression())) {
									for (IConnection icon : outVariable.getConnectionPointIn().getConnections()) {
										if (icon.getFormalParam().equals("ET") || icon.getFormalParam().equals("CV"))
											if (!mutant)
												write += "(define " + conOut.outvar.getExpression() + "_t" + (setIter+2) + "::int 0)\r\n";
											else
												write += "(define " + "M_" + conOut.outvar.getExpression() + "_t" + (setIter+2) + "::int " +
														conOut.outvar.getExpression() + "_t" + (setIter+2) + ")\r\n";
									}
									break;
								}
							}
							//write += "(define " + conOut.outvar.getExpression() + "_t" + (setIter+2) + "::int 0)\r\n";
						}
						if (conIn.block.getTypeName().equals("SR") || conIn.block.getTypeName().equals("RS"))
							if (!mutant)
								write += "(define " + conOut.outvar.getExpression() + "_t" + (setIter+2) + "::bool false)\r\n";
							else
								write += "(define " + "M_" + conOut.outvar.getExpression() + "_t" + (setIter+2) + "::bool " +
										conOut.outvar.getExpression() + "_t" + (setIter+2) + ")\r\n";
					}
				}
			}
			write += "\r\n";
			
			/* Writing inputs with subranges Starts ----------------------------- */
			if(!mutant){
				for(int i=0; i<inputs.length; i++) {
					if(setIter != 0)
						write += "(define "+inputs[i].trim()+"_t"+(setIter+1)+"::";
					else
						write += "(define "+inputs[i].trim()+"::";
					//System.out.println(inputs[i]);
					if(ParseXML.InputInterface.get(inputs[i]).get(0).equals("BOOL")) {
						write += "bool ";
						//if (ParseXML.InputInterface.get(inputs[i]).get(1).equals("NULL"))
							write += ")\r\n";
						//else
						//	write += ParseXML.InputInterface.get(inputs[i]).get(1) + ")\r\n";
					}
					else{
						write += "real ";
						write += ")\r\n";
						//if (ParseXML.InputInterface.get(inputs[i]).get(1).equals("NULL"))
						//	write += ")\r\n";
						//else
						//	write += ParseXML.InputInterface.get(inputs[i]).get(1) + ")\r\n";
					}
				}
			}
			// -------------------------------- Writing inputs with subranges Ends */
			
			/* Writing block's output variables Starts ---------------------------*/


			HashMap<String, String> outVarDefs = new HashMap<String, String>();
			for(double i = longestPathSize - 1 ; i >= 0 ; i=i-0.5)
				for(String key : oneDepthFunctionCalcs.keySet()){
					LogicStatement ls = oneDepthFunctionCalcs.get(key);
					Element lastBlockElem = getElementById(ls.blockId); 
					for (Connection con : connections){
						String outVarDef = ""; String id="";
						Element outvarElem = getElementById(con.end);
						if(getElementById(con.start) == lastBlockElem && ls.dpcl.outVar.equals(con.startParam)){
							id = String.valueOf(con.start)+con.startParam;
							if(outvarElem.type ==Element.OUTVAR){
								if(setIter != 0){
									outVarDef += "(define ";
									if(mutant==true){
										outVarDef += "M_";
									}
									outVarDef += outvarElem.outvar.getExpression() + "_t" + (setIter+1) + "::";
								}
								else{
									outVarDef += "(define ";
									if(mutant==true){
										outVarDef += "M_";
									}
									outVarDef += outvarElem.outvar.getExpression() + "::";
								}
								if(ls.dpcl.boolOutput){
									if(setIter != 0)
										outVarDef += "bool "+ls.YicesString(setIter+1,false)+")\r\n";
									else
										outVarDef += "bool "+ls.YicesString()+")\r\n";
								}else {
									//System.out.println(lastBlockElem.block.getTypeName());
									String str = "";
									if(setIter != 0)
										str = ls.YicesString(setIter+1,false);
									else
										str = ls.YicesString();
									if(lastBlockElem.block.getTypeName().equals("SEL")) {
										if(str.contains("true")||str.contains("false"))
											outVarDef += "bool ";
										else
											outVarDef += "real ";
									}else {
										outVarDef += "real ";
									}
									outVarDef += str + ")\r\n";
								}
							}else{
								if(setIter != 0){
									outVarDef += "(define ";
									if(mutant==true){
										outVarDef += "M_";
									}
									outVarDef += ls.dpcl.functionName + ls.blockId + "_" + ls.dpcl.outVar.toLowerCase() + "_t" + (setIter+1) + "::";
								}
								else{
									outVarDef += "(define ";
									if(mutant==true){
										outVarDef += "M_";
									}
									outVarDef += ls.dpcl.functionName + ls.blockId + "_" + ls.dpcl.outVar.toLowerCase() + "::";
								}
								if(ls.dpcl.boolOutput){
									if(setIter != 0)
										outVarDef += "bool "+ls.YicesString(setIter+1,false)+")\r\n";
									else
										outVarDef += "bool "+ls.YicesString()+")\r\n";
								}else{
									String str = "";
									if(setIter != 0)
										str = ls.YicesString(setIter+1,false);
									else
										str = ls.YicesString();
									if(ls.dpcl.functionName.equals("SEL")) {
										if(str.contains("true")||str.contains("false"))
											outVarDef += "bool ";
										else
											outVarDef += "real ";
									}else {
										outVarDef += "real ";
									}
									outVarDef += str + ")\r\n";
								}
							}
						}
						if(id != "" && ls.blockOrder == i){
							if(!outVarDefs.containsValue(outVarDef)){
								write += outVarDef;
								outVarDefs.put(id,outVarDef);
							}
						}
					}
				}
			
			List<String> invarList = new ArrayList<String>();
			
			for (IInVariable inVar : inputVariables) {
				invarList.add(inVar.getExpression());
			}
			
			for (Connection c: connections) {
				if(invarList.contains(c.startParam)&&Arrays.asList(outputs).contains(c.endParam)) {
					write += "(define ";
					if(mutant==true){
						write += "M_";
					}
					write += c.endParam + "_t" + (setIter+1) + "::";
					String datatype = ParseXML.OutputInterface.get(c.endParam).get(0);
					if (!Arrays.asList(inputs).contains(c.startParam)) 
						write += datatype.toLowerCase() + " " + c.startParam.toLowerCase() + ")\r\n";
					else {
						if (c.startParam.equals("TSP") || c.startParam.equals("PTSP")) {
							write += datatype.toLowerCase() + " " + c.startParam + "_out" + "_t" + (setIter+1) +")\r\n";
						}
						else {
							write += datatype.toLowerCase() + " " + c.startParam + "_t" + (setIter+1) +")\r\n";
						}
					}
				}
			}
			
			//yicesWriter.write(write + "\r\n");
			
			//yicesWriter.write(";; Rule 1-2. Define DPCs for d-paths\r\n\r\n");
			//			System.out.println("Rule 1-2");
			
			//yicesWriter.write(";; Rule 2. Assert test requirements\r\n");
			
			}
			yicesHeaderList.add(write);
			yicesHeaderList.add("\r\n");
			//yicesHeaderList.add(";; Rule 2. Assert test requirements\r\n");
		/*} catch (IOException e) {
			e.printStackTrace();
			System.err.println("FATAL ERROR: yicesHeader()");
			System.exit(-1);
		}*/
	}
	
	
	/**
	 * @author donghwan
	 * @param coveredAsserts 모든 어설트에 대해서 covered 정보를 갖는 비트벡터
	 * @param unsatYicesID yices에서 unsat 되었다고 알려준 ID
	 * @return 실제 unsat인 어설트의 ID
	 */
	private static int getUnsatAssertIDfromYicesID(boolean[] coveredAsserts, Integer unsatYicesID) {
		int index = 0;
		int length = coveredAsserts.length;
		for(int i=1; i<length; i++) {
			if(!coveredAsserts[i]) {
				index++;
				if(index == unsatYicesID) return i;
			}
		}

		return -1;
	}

	/**
	 * @author donghwan
	 * @param log 실행 라운드 및 달성된 assertion 관련 내용
	 * @param solutionStr yices 실행 결과로 나오는 솔루션
	 * @param sat 만족된 assert 개수
	 * @param max 전체 assert 개수
	 * @param fileName yices 로그 파일 이름
	 */
	private static void yicesLogWriter(String log,int sat, int max, String fileName) {
		double cov = (sat / (double) max) * 100.0;
		try {
			BufferedWriter yicesLog = new BufferedWriter(new FileWriter("output\\"+model+"\\"+fileName));
			yicesLog.write(log);
			yicesLog.write("\nTotal " + sat + " satisfied / " + max + " assertions : coverage " + String.format("%.3f", cov) + "%");
			yicesLog.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("yicesLogWriter(): FATAL ERR");
			System.exit(-1);
		}
		System.out.println(log);
	}
	/**
	 * 주어진 testSuites를 이용해서 dPath 중 몇개를 만족하는지 알려주는 함수.
	 * 
	 * @author donghwan
	 * @param testSuite
	 * @param dPath
	 * @return number of satisfied assertions
	 * 
	 */
	public static int countSatisfiedAsserts(ArrayList<TestCase> testSuite, List<DPath> dPath) {
		boolean[] isCovered = new boolean[dPath.size()];
		boolean log = false;

		for(TestCase testCase : testSuite) {
			for(String input : testCase.inputs) {
				for(Element var : invars) {
					if(input.equals(var.invar.getExpression())) {
						var.value = testCase.getValue(input);
					}
				}
			}
			for (int i = 0; i < isCovered.length; i++) {
				if(isCovered[i] == false)
					if (dPath.get(i).DPC.calculate() == 1.0)
						isCovered[i] = true;
			}
		}

		int counter = 0;
		for (int i = 0; i < isCovered.length; i++)
			if (isCovered[i] == true) {
				counter++;
				if(log) System.out.println(i + ": " + dPath.get(i).datapath_str);
			}

		if(log) System.out.println("Total Number of satisfied dPaths: " + counter);

		return counter;
	}
	
	static void buildDPath() throws IOException{
		// 초기화
		dPathsClear();
		loadTestFile("");
		findDataPaths();
		sortDataPaths();
		calculateDPC();
		loadProgramInfoFile();
		
		// 테스트 케이스 생성 준비
		List<DPath> dPath = new ArrayList<DPath>();
		for(DPath dpath: DPaths) dpath.dPathType = 0;
		dPath.addAll(DPaths);
		System.out.println("DPC start...");
		for (int i = 0; i < dPath.size(); i++) {
			System.out.println(dPath.get(i).DPC.calculate());
		}
		System.out.println("DPC end...");
	}
	ArrayList<ArrayList<Item>> readTestSuite(){
		return null;
		
	}
	
	/**
	 * TODO
	 * 이 함수와 countSatisfiedAsserts 함수가 유사한 일을 하는데 중복으로 구현됨.
	 * 추후에 이 함수를 제거하고 countSatisfiedAsserts 만을 사용하도록 할 필요가 있음.
	 * 
	 * @author donghwan
	 * @param testSuite Item으로 이루어진 testSet
	 * @param dPath
	 * @return coverage level
	 * 
	 */
	private static float assessCoverageLevel(ArrayList<ArrayList<Item>> testSuite, List<DPath> dPath) {
		boolean[] isCovered = new boolean[dPath.size()];
		if (dPath.size() == 0) return 0; // case: no criteria selected
		for (ArrayList<Item> testCase : testSuite) {
			for (int j = 0; j < testCase.size(); j++) {
				for (Element var : invars) {
					if (testCase.get(j).getName().equals(var.invar.getExpression())) {
						if (testCase.get(j).getType() == 0) {
							var.value = (testCase.get(j).getValue() == 0) ? "false" : "true";
						} else {
							var.value = testCase.get(j).getValue() + "";
						}
					}
				}
			}
			for(Element var: invars) {
				if(var.value != null && var.value.trim().equals(Integer.MIN_VALUE+"")) {
					System.err.println("Input value missing!: " + var.invar.getExpression());
					System.exit(-1);
				}
			}
			for (int i = 0; i < dPath.size(); i++) {
				if (dPath.get(i).DPC.calculate() == 1.0) {
					isCovered[i] = true;
				}
			}
		}

		int counter = 0;
		for (int i = 0; i < isCovered.length; i++)
			if (isCovered[i])
				counter++;

		return (float) counter / (float) isCovered.length;
	}
}