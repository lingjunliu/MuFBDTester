package MuFBDTesting;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import plcopen.inf.model.IContentHeader;
import plcopen.inf.model.IFileHeader;
import plcopen.inf.type.group.fbd.IBlock;
import plcopen.inf.type.group.fbd.IInVariable;
import plcopen.inf.type.group.fbd.IInVariableInBlock;
import plcopen.inf.type.group.fbd.IOutVariableInBlock;
import plcopen.model.ProjectImpl;
import plcopen.type.body.FBD;
import plcopen.xml.PLCModel;
import plcopen.xml.XML2Writer;

public class MutantGenerator {
	
	private int mutantID;
	
	private ArrayList<String> mutantInfoList = new ArrayList<String>(100);
	
	/*** block replacement: list of replacable blocks ***/
	private ArrayList<String> ABR2 = new ArrayList<String>();
	private ArrayList<String> ABR3 = new ArrayList<String>();
	private ArrayList<String> ABR4 = new ArrayList<String>();
	
	private ArrayList<String> CBR2 = new ArrayList<String>();
	private ArrayList<String> CBR3 = new ArrayList<String>();
	private ArrayList<String> CBR4 = new ArrayList<String>();
	
	private ArrayList<String> LBR2 = new ArrayList<String>();
	private ArrayList<String> LBR3 = new ArrayList<String>();
	private ArrayList<String> LBR4 = new ArrayList<String>();
	
	private ArrayList<String> SBR2 = new ArrayList<String>();
	private ArrayList<String> SBR3 = new ArrayList<String>();
	private ArrayList<String> SBR4 = new ArrayList<String>();
	
	private ArrayList<String> TBR = new ArrayList<String>();
	private ArrayList<String> EBR = new ArrayList<String>();
	private ArrayList<String> BBR = new ArrayList<String>();
	private ArrayList<String> CouBR = new ArrayList<String>();
	
	// Logical Block Replacement Operator-Improved
	private ArrayList<String> LRO_I = new ArrayList<String>();
	boolean LRO_I_OrNot = false;
	
	/*** mutant id list of each operator ***/
	public static ArrayList<Integer> CVR_list = new ArrayList<Integer>();
	public static ArrayList<Integer> IID_list = new ArrayList<Integer>();
	public static ArrayList<Integer> SWI_list = new ArrayList<Integer>();
	public static ArrayList<Integer> ABR_list = new ArrayList<Integer>();
	public static ArrayList<Integer> CBR_list = new ArrayList<Integer>();
	public static ArrayList<Integer> LBR_list = new ArrayList<Integer>();
//	private ArrayList<Integer> ConBR_list = new ArrayList<Integer>();
//	private ArrayList<Integer> NBR_list = new ArrayList<Integer>();
	public static ArrayList<Integer> SBR_list = new ArrayList<Integer>();
	public static ArrayList<Integer> BBR_list = new ArrayList<Integer>();
	public static ArrayList<Integer> EBR_list = new ArrayList<Integer>();
	public static ArrayList<Integer> CouBR_list = new ArrayList<Integer>();
	public static ArrayList<Integer> TBR_list = new ArrayList<Integer>();
	
	/*** key: operator name; value: selected or not ***/
	public static HashMap<String, Boolean> mutantOperatorList = new HashMap<String, Boolean>();
	
	public MutantGenerator() {
		mutantID = 0;

		ABR2.add("ADD2");
		ABR2.add("SUB");
		ABR2.add("MUL2");
		ABR2.add("DIV");
		ABR2.add("MOD");

		ABR3.add("ADD3");
		ABR3.add("MUL3");

		ABR4.add("ADD4");
		ABR4.add("MUL4");

		CBR2.add("GE2");
		CBR2.add("GT2");
		CBR2.add("LE2");
		CBR2.add("LT2");
		CBR2.add("EQ2");
		CBR2.add("NE");
		
		CBR3.add("GE3");
		CBR3.add("GT3");
		CBR3.add("LE3");
		CBR3.add("LT3");
		CBR3.add("EQ3");
		
		CBR4.add("GE4");
		CBR4.add("GT4");
		CBR4.add("LE4");
		CBR4.add("LT4");
		CBR4.add("EQ4");

		LBR2.add("AND2");
		LBR2.add("OR2");
		LBR2.add("XOR2");

		LBR3.add("AND3");
		LBR3.add("OR3");
		LBR3.add("XOR3");

		LBR4.add("AND4");
		LBR4.add("OR4");
		LBR4.add("XOR4");
		
		SBR2.add("MAX2");
		SBR2.add("MIN2");
		
		SBR3.add("MAX3");
		SBR3.add("MIN3");
		
		SBR4.add("MAX4");
		SBR4.add("MIN4");
		
		TBR.add("TON");
		TBR.add("TOF");
		TBR.add("TP");
		
		EBR.add("R_TRIG");
		EBR.add("F_TRIG");
		
		BBR.add("SR");
		BBR.add("RS");
		
		CouBR.add("CTU");
		CouBR.add("CTD");
		
		// Logical Block Replacement Operator-Improved
		LRO_I.add("AND2");
		LRO_I.add("OR2");
		LRO_I.add("XOR2");
		
		mutantOperatorList.put("CVR", false);
		mutantOperatorList.put("IID", false);
		
		mutantOperatorList.put("ABR", false);
		mutantOperatorList.put("LBR", false);
		mutantOperatorList.put("CBR", false);
		
		mutantOperatorList.put("ConBR", false);
		mutantOperatorList.put("NBR", false);
		mutantOperatorList.put("SBR", false);
		
		mutantOperatorList.put("BBR", false);
		mutantOperatorList.put("EBR", false);
		mutantOperatorList.put("CouBR", false);
		mutantOperatorList.put("TBR", false);
		
		mutantOperatorList.put("SWI", false);
		
	}
	
	private static String dirPath = System.getProperty("user.dir")+"/output/";
	
	public void generateMutant(String filePath) {
		mutantID = 0;
		
		dirPath = System.getProperty("user.dir")+"/output/";
		dirPath += CreateGUI.target + "/";
		ProjectImpl originalXML = readXML(filePath);
		
		printXMLInfo(originalXML);

		mutation(originalXML);

//		printXMLInfo(originalXML);

		writeMutantInfo(dirPath + "mutant_Info.txt");
	}
	
	public static void allOperator() {
		for (String key : mutantOperatorList.keySet()) {
			mutantOperatorList.put(key, true);
		}
	}
	
	public static void deselectAllOperator() {
		for (String key : mutantOperatorList.keySet()) {
			mutantOperatorList.put(key, false);
		}
	}
	
	private ProjectImpl readXML(String filePath) {
		ProjectImpl plcProject = null;
		plcProject = (ProjectImpl) PLCModel.readFromXML(new File(filePath));

		// Set header informations
		IFileHeader fileHeader = plcProject.getFileHeader();
		fileHeader.setCompanyName("KAIST-SELAB");
		fileHeader.setCompanyURL("http://se.kaist.ac.kr");
		fileHeader.setProductName("ResearchProduct-v130425");
		IContentHeader contentHeader = plcProject.getContentHeader();
		contentHeader.setAuthor("Donghwan");
		contentHeader.setOrganization("KAIST-SELAB");
		contentHeader.setName("MUTANT");

		return plcProject;
	}
	
	private void printXMLInfo(ProjectImpl xml) {
		System.out.println("printXMLInfo()");

		if (xml.getPOUs().size() > 1) // TODO: 만약에 size가 1보다 큰 경우 따로 처리해야 함.
			System.err.println("POUs Size: " + xml.getPOUs().size());

		for (int i = 0; i < xml.getPOUs().size(); i++) {
			FBD body = (FBD) xml.getPOUs().get(i).getBody();

			for (IBlock block : body.getBlocks()) {
				System.out.println("[BLOCK] " + block.getLocalID() + ": " + block.getTypeName() + ", "
						+ block.getOutVariables().get(0).getFormalParameter());
				for (IInVariableInBlock invar : block.getInVariables()) {
					System.out.println("\t" + invar.getFormalParameter() + ": " + invar.isNegated());
				}
				for (IOutVariableInBlock outvar : block.getOutVariables()) {
					System.out.println("\t" + outvar.getFormalParameter() + ": " + outvar.isNegated());
				}
			}
			// print constants
			String number;
			for (IInVariable invar : body.getInVariables()) {
				number = invar.getExpression().trim();
				if (isNumber(number)) {
					System.out.println("[INVAR] " + invar.getLocalID() + ": " + number);
				}
			}

		}
	}
	
	private void writeXML(ProjectImpl xml, String filePath) {
		File newfile = new File(filePath);
		try {
			XML2Writer.writeToXML(xml, newfile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.out.println("writeXML(): FATAL ERCBR");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("writeXML(): FATAL ERCBR while writing " + filePath);
			System.exit(0);
		}
	}
	
	private void writeMutantInfo(String file) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("No.\tMO\tLocalID\tmutation");
			writer.newLine();
			for (int i = 0; i < mutantInfoList.size(); i++) {
				writer.write(i + "\t" + mutantInfoList.get(i));
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("writeMutantInfo(): FATAL ERCBR");
			System.exit(0);
		}
	}
	
	private void replaceBlock(IBlock block, String operator) {
		System.out.println("replaceBlock()\t" + block.getLocalID() + ": " + block.getTypeName() + " -> " + operator);
		block.setTypeName(operator);
	}
	
	static ArrayList<String> permutation = new ArrayList<String>();
	
	private void mutation(ProjectImpl xml) {
		System.out.println("mutation()");

		String mutantOperator = "";
		String funcName;
		ArrayList<String> operatorList = null;
		for (int i = 0; i < xml.getPOUs().size(); i++) {
			FBD body = (FBD) xml.getPOUs().get(i).getBody();

			// BLOCK에 대한 Mutations: ABR, CBR, LBR, SBR, TBR, EBR, BBR, CouBR
			for (IBlock block : body.getBlocks()) {
				funcName = block.getTypeName();
				if(block.getTypeName().contains("ADD")||block.getTypeName().contains("MUL")||block.getTypeName().contains("EQ")||block.getTypeName().contains("GE")
						||block.getTypeName().contains("GT")||block.getTypeName().contains("LE")||block.getTypeName().contains("LT")||block.getTypeName().contains("AND")
						||block.getTypeName().contains("OR")||block.getTypeName().contains("XOR")||block.getTypeName().contains("MAX")||block.getTypeName().contains("MIN")) {
					funcName = block.getTypeName()+block.getInVariables().size();
				}
				else if(block.getTypeName().equals("MUX")) {
					funcName = block.getTypeName()+(block.getInVariables().size()-1);
				}
				else if(block.getTypeName().contains("CTUD")) {
					funcName = "CTUD";
					block.setTypeName("CTUD");
				}
				else if(block.getTypeName().contains("CTU")) {
					funcName = "CTU";
					block.setTypeName("CTU");
				}
				else if(block.getTypeName().contains("CTD")) {
					funcName = "CTD";
					block.setTypeName("CTD");
				}
				
				if (ABR2.contains(funcName)&&mutantOperatorList.get("ABR")) {
					// ABR2: ADD2, SUB, MUL2, DIV, MOD
					operatorList = ABR2;
					mutantOperator = "ABR2";
				} else if (ABR3.contains(funcName)&&mutantOperatorList.get("ABR")) {
					// ABR3: ADD3, MUL3
					operatorList = ABR3;
					mutantOperator = "ABR3";
				} else if (ABR4.contains(funcName)&&mutantOperatorList.get("ABR")) {
					// ABR4: ADD4, MUL4
					operatorList = ABR4;
					mutantOperator = "ABR4";
				} else if (CBR2.contains(funcName)&&mutantOperatorList.get("CBR")) {
					// CBR2: GE2, GT2, LE2, LT2, EQ2, NE
					operatorList = CBR2;
					mutantOperator = "CBR2";
				} else if (CBR3.contains(funcName)&&mutantOperatorList.get("CBR")) {
					// CBR3: GE3, GT3, LE3, LT3, EQ3
					operatorList = CBR3;
					mutantOperator = "CBR3";
				} else if (CBR4.contains(funcName)&&mutantOperatorList.get("CBR")) {
					// CBR4: GE4, GT4, LE4, LT4, EQ4
					operatorList = CBR4;
					mutantOperator = "CBR4";
				} else if (LBR2.contains(funcName)&&mutantOperatorList.get("LBR")) {
					// LBR2: AND2, OR2, XOR2
					operatorList = LBR2;
					mutantOperator = "LBR2";
				} else if (LBR3.contains(funcName)&&mutantOperatorList.get("LBR")) {
					// LBR3: AND3, OR3, XOR3
					operatorList = LBR3;
					mutantOperator = "LBR3";
				} else if (LBR4.contains(funcName)&&mutantOperatorList.get("LBR")) {
					// LBR4: AND4, OR4, XOR4
					operatorList = LBR4;
					mutantOperator = "LBR4";
				} else if (SBR2.contains(funcName)&&mutantOperatorList.get("SBR")) {
					// SBR2: MIN2, MAX3
					operatorList = SBR2;
					mutantOperator = "SBR2";
				} else if (SBR3.contains(funcName)&&mutantOperatorList.get("SBR")) {
					// SBR3: MIN3, MAX3
					operatorList = SBR3;
					mutantOperator = "SBR3";
				} else if (SBR4.contains(funcName)&&mutantOperatorList.get("SBR")) {
					// SBR4: MIN4, MAX4
					operatorList = SBR4;
					mutantOperator = "SBR4";
				} else if (TBR.contains(funcName)&&mutantOperatorList.get("TBR")) {
					// TBR: TP, TON, TOF
					operatorList = TBR;
					mutantOperator = "TBR";
				} else if (EBR.contains(funcName)&&mutantOperatorList.get("EBR")) {
					// EBR: R_TRIG, F_TRIG
					operatorList = EBR;
					mutantOperator = "EBR";
				} else if (BBR.contains(funcName)&&mutantOperatorList.get("BBR")) {
					// BBR: SR, RS
					operatorList = BBR;
					mutantOperator = "BBR";
				} else if (CouBR.contains(funcName)&&mutantOperatorList.get("CouBR")) {
					// CoBR: CTU, CTD
					operatorList = CouBR;
					mutantOperator = "CoBR";
				} else if(funcName.equals("ABS") || funcName.equals("MOVE")) {
					// ABS, MOVE: 다른 연산자와 바꿀 수 없음
					continue;
				} else {
					operatorList = null;
					System.out.println(block.getTypeName());
//					throw new Error();
//					System.exit(0);
				}
				
				if(operatorList!=null)
				for (String operator : operatorList) {		
					// if it's the same block just pass
					if (operator.equals(funcName))
						continue;

					if (CouBR.contains(funcName)){
						for(IInVariableInBlock input: block.getInVariables()) {
							if(input.getFormalParameter().equals("CU")) {
								input.setFormalParameter("CD");
							}
							else if(input.getFormalParameter().equals("CD")) {
								input.setFormalParameter("CU");
							}
							else if(input.getFormalParameter().equals("R")) {
								input.setFormalParameter("LD");
							}
							else if(input.getFormalParameter().equals("LD")) {
								input.setFormalParameter("R");
							}
							
							System.out.println(input.getFormalParameter());
						}
						mutantInfoList.add(mutantID,
								mutantOperator + "\t" + block.getLocalID() + "\t" + block.getTypeName() + " -> " + operator);
						CouBR_list.add(mutantID);
						replaceBlock(block, operator);
						writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
						replaceBlock(block, funcName);
						for(IInVariableInBlock input: block.getInVariables()) {
							if(input.getFormalParameter().equals("CU")) {
								input.setFormalParameter("CD");
							}
							else if(input.getFormalParameter().equals("CD")) {
								input.setFormalParameter("CU");
							}
							else if(input.getFormalParameter().equals("R")) {
								input.setFormalParameter("LD");
							}
							else if(input.getFormalParameter().equals("LD")) {
								input.setFormalParameter("R");
							}
							
							System.out.println(input.getFormalParameter());
						}
					} else if (BBR.contains(funcName)){
						for(IInVariableInBlock input: block.getInVariables()) {
							if(input.getFormalParameter().equals("S1")) {
								input.setFormalParameter("S");
							}
							else if(input.getFormalParameter().equals("S")) {
								input.setFormalParameter("S1");
							}
							else if(input.getFormalParameter().equals("R")) {
								input.setFormalParameter("R1");
							}
							else if(input.getFormalParameter().equals("R1")) {
								input.setFormalParameter("R");
							}
							
							System.out.println(input.getFormalParameter());
						}
						mutantInfoList.add(mutantID,
								mutantOperator + "\t" + block.getLocalID() + "\t" + block.getTypeName() + " -> " + operator);
						BBR_list.add(mutantID);
						replaceBlock(block, operator);
						writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
						replaceBlock(block, funcName);
						for(IInVariableInBlock input: block.getInVariables()) {
							if(input.getFormalParameter().equals("S1")) {
								input.setFormalParameter("S");
							}
							else if(input.getFormalParameter().equals("S")) {
								input.setFormalParameter("S1");
							}
							else if(input.getFormalParameter().equals("R")) {
								input.setFormalParameter("R1");
							}
							else if(input.getFormalParameter().equals("R1")) {
								input.setFormalParameter("R");
							}
							
							System.out.println(input.getFormalParameter());
						}
					} else if(funcName.contains("SUB")||funcName.contains("DIV")||funcName.contains("MOD")||funcName.contains("ADD")||funcName.contains("MUL")
							||funcName.contains("EQ")||funcName.contains("GE")||funcName.contains("GT")||funcName.contains("LE")||funcName.contains("LT")||funcName.contains("NE")
							||funcName.contains("AND")||funcName.contains("OR")||funcName.contains("XOR")||funcName.contains("MAX")||funcName.contains("MIN")) {
						mutantInfoList.add(mutantID,
								mutantOperator + "\t" + block.getLocalID() + "\t" + block.getTypeName() + " -> " + operator);
						
						if(mutantOperator.contains("ABR")) {
							ABR_list.add(mutantID);
						}
						else if(mutantOperator.contains("CBR")) {
							CBR_list.add(mutantID);
						}
						else if(mutantOperator.contains("LBR")) {
							LBR_list.add(mutantID);
						}
						else if(mutantOperator.contains("SBR")) {
							SBR_list.add(mutantID);
						}
						
						if(isNumber(operator.substring(operator.length()-1))) {
							replaceBlock(block, operator.substring(0, operator.length()-1));
						}
						else {
							replaceBlock(block, operator);
						}
						System.out.println("operator:  "+operator.substring(0, operator.length()-1));
						writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
						if(isNumber(funcName.substring(funcName.length()-1))) {
							replaceBlock(block, funcName.substring(0, funcName.length()-1));
						}
						else {
							replaceBlock(block, funcName);
						}
						
						System.out.println("funcName:  "+funcName);
					} else  { // TON, TOF, TP, R_TRIG, F_TRIG
						mutantInfoList.add(mutantID,
								mutantOperator + "\t" + block.getLocalID() + "\t" + block.getTypeName() + " -> " + operator);
						if(mutantOperator.equals("EBR")) {
							EBR_list.add(mutantID);
						}
						else if(mutantOperator.equals("TBR")) {
							TBR_list.add(mutantID);
						}
						replaceBlock(block, operator);
						writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
						replaceBlock(block, funcName);
					} 
				}
				
				/*** Logical Block Replacement Operator-Improved ***/
				if(LRO_I_OrNot && LRO_I.contains(funcName)) {
					// Logical block -> SR
					block.setInstanceName("SR_Manual");
					for(IInVariableInBlock input: block.getInVariables()) {
						if(input.getFormalParameter().equals("IN1")) {
							input.setFormalParameter("S1");
						}
						else if(input.getFormalParameter().equals("IN2")) {
							input.setFormalParameter("R");
						}
						System.out.println(input.getFormalParameter());
					}
					for (IOutVariableInBlock outvar : block.getOutVariables()) {
						if(outvar.getFormalParameter().equals("OUT")) {
							outvar.setFormalParameter("Q1");
						}
					}
					mutantInfoList.add(mutantID,
							"LRO-I" + "\t" + block.getLocalID() + "\t" + block.getTypeName() + " -> " + "SR");
					replaceBlock(block, "SR");
					writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
					// SR -> RS
					replaceBlock(block, funcName.substring(0, funcName.length()-1));
					for(IInVariableInBlock input: block.getInVariables()) {
						if(input.getFormalParameter().equals("S1")) {
							input.setFormalParameter("S");
						}
						else if(input.getFormalParameter().equals("R")) {
							input.setFormalParameter("R1");
						}
						System.out.println(input.getFormalParameter());
					}
					mutantInfoList.add(mutantID,
							"LRO-I" + "\t" + block.getLocalID() + "\t" + block.getTypeName() + " -> " + "RS");
					replaceBlock(block, "RS");
					writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
					// RS -> original logical block
					replaceBlock(block, funcName.substring(0, funcName.length()-1));
					block.setInstanceName(null);
					for(IInVariableInBlock input: block.getInVariables()) {
						if(input.getFormalParameter().equals("S")) {
							input.setFormalParameter("IN1");
						}
						else if(input.getFormalParameter().equals("R1")) {
							input.setFormalParameter("IN2");
						}
						System.out.println(input.getFormalParameter());
					}
					for (IOutVariableInBlock outvar : block.getOutVariables()) {
						if(outvar.getFormalParameter().equals("Q1")) {
							outvar.setFormalParameter("OUT");
						}
					}
				}
				
				/*** Switch Inputs ***/
				if(mutantOperatorList.get("SWI")) {
					if (BBR.contains(funcName)) {
						String tempInput[] = new String[2];
						int order = 0;
						for (IInVariableInBlock invar: block.getInVariables()) {
							tempInput[order] = invar.getFormalParameter();
							order++;
						}
						order = 0;
						for (IInVariableInBlock invar: block.getInVariables()) {
							invar.setFormalParameter(tempInput[1-order]);
							order++;
						}
						mutantInfoList.add(mutantID,
								"SWI" + "\t" + block.getLocalID() + "\t" + block.getTypeName() + " -> " + "INPUT SWITCH ");
						SWI_list.add(mutantID);
						writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
						order = 0;
						for (IInVariableInBlock invar: block.getInVariables()) {
							invar.setFormalParameter(tempInput[order]);
							order++;
						}
					}
					else if(funcName.equals("SUB") || funcName.equals("DIV") || funcName.equals("MOD") ||
							funcName.contains("GT") || funcName.contains("GE") || funcName.contains("LT") || funcName.contains("LE") ||
							funcName.equals("SEL") || funcName.contains("MUX")){
						
						String order = "";
						permutation = new ArrayList<String>();
						
						for (IInVariableInBlock invar: block.getInVariables()) {
							if(!invar.getFormalParameter().equals("G")&&!invar.getFormalParameter().equals("K")) {
								order += invar.getFormalParameter().substring(invar.getFormalParameter().length()-1, invar.getFormalParameter().length());
							}
						}
						String correctOrder = order;
						perm1(order);
						for (String per: permutation) {
							if (!per.equals(correctOrder)) {
								System.out.println(per);
								int n = 0;
								for (IInVariableInBlock invar: block.getInVariables()) {
									if(!invar.getFormalParameter().equals("G")&&!invar.getFormalParameter().equals("K")) {
										invar.setFormalParameter("IN" + per.charAt(n));
										n++;
									}
								}
								
								mutantInfoList.add(mutantID,
										"SWI" + "\t" + block.getLocalID() + "\t" + block.getTypeName() + " -> " + "INPUT SWITCH " + per);
								SWI_list.add(mutantID);
								writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
								
								int StringOrder = 0;
								for (IInVariableInBlock invar: block.getInVariables()) {
									if(!invar.getFormalParameter().equals("G")&&!invar.getFormalParameter().equals("K")) {
										invar.setFormalParameter("IN" + correctOrder.charAt(StringOrder));
										StringOrder++;
									}
								}
							}
						}
					}
				}
			}
			// INVAR in BLOCK 에 대한 Mutations: IID (Inverter Insertion & Deletion)
			mutantOperator = "IID";
			boolean inverter;
			if(mutantOperatorList.get("IID")) {
				for (IBlock block : body.getBlocks()) {
					funcName = block.getTypeName();
	
					// AND, OR인 경우에 모든 invar와 outvar에 대해서 (added SR, RS, R_TRIG, F_TRIG to support all the boolean edges)
					if (funcName.contains("AND") || funcName.contains("OR") || funcName.contains("XOR")
							|| funcName.contains("SR") || funcName.contains("RS")
							|| funcName.contains("R_TRIG") || funcName.contains("F_TRIG")) {
						for (IInVariableInBlock invar : block.getInVariables()) {
							inverter = invar.isNegated();
							mutantInfoList.add(mutantID,
									mutantOperator + "\t" + block.getLocalID() + "\t" + invar.getFormalParameter() + "\t"
											+ inverter + " -> " + !inverter);
							IID_list.add(mutantID);
							invar.setNegated(!inverter);
							writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
							invar.setNegated(inverter);
						}
						
						for (IOutVariableInBlock outvar : block.getOutVariables()) {
							inverter = outvar.isNegated();
							mutantInfoList.add(mutantID,
									mutantOperator + "\t" + block.getLocalID() + "\t" + outvar.getFormalParameter() + "\t"
											+ inverter + " -> " + !inverter);
							IID_list.add(mutantID);
							outvar.setNegated(!inverter);
							writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
							outvar.setNegated(inverter);
						}
					}
					// SEL인 경우에 G inval에 대해서만
					else if (funcName.contains("SEL")) {
						IInVariableInBlock invar = block.getInVariables().get(0);
						inverter = invar.isNegated();
						mutantInfoList.add(mutantID,
								mutantOperator + "\t" + block.getLocalID() + "\t" + invar.getFormalParameter() + "\t"
										+ inverter + " -> " + !inverter);
						IID_list.add(mutantID);
						invar.setNegated(!inverter);
						writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
						invar.setNegated(inverter);
					}
					// Relational Operator인 경우에 output에 대해서
					else if (funcName.contains("GT")||funcName.contains("GE")||funcName.contains("LT")||funcName.contains("LE")||funcName.contains("EQ")||funcName.contains("NE")) {
						for (IOutVariableInBlock outvar : block.getOutVariables()) {
							inverter = outvar.isNegated();
							mutantInfoList.add(mutantID,
									mutantOperator + "\t" + block.getLocalID() + "\t" + outvar.getFormalParameter() + "\t"
											+ inverter + " -> " + !inverter);
							IID_list.add(mutantID);
							outvar.setNegated(!inverter);
							writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
							outvar.setNegated(inverter);
						}
					}
					else if (funcName.contains("TON") || funcName.contains("TOF") || funcName.contains("TP")) {
						IInVariableInBlock invar = block.getInVariables().get(0);
						inverter = invar.isNegated();
						mutantInfoList.add(mutantID,
								mutantOperator + "\t" + block.getLocalID() + "\t" + invar.getFormalParameter() + "\t"
										+ inverter + " -> " + !inverter);
						IID_list.add(mutantID);
						invar.setNegated(!inverter);
						writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
						invar.setNegated(inverter);
						
						IOutVariableInBlock outvar = block.getOutVariables().get(0);
						inverter = outvar.isNegated();
						mutantInfoList.add(mutantID,
								mutantOperator + "\t" + block.getLocalID() + "\t" + outvar.getFormalParameter() + "\t"
										+ inverter + " -> " + !inverter);
						IID_list.add(mutantID);
						outvar.setNegated(!inverter);
						writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
						outvar.setNegated(inverter);
					}
					else if (funcName.contains("CTUD")) {
						for (int n = 0; n < 4; n++) {
							IInVariableInBlock invar = block.getInVariables().get(n);
							inverter = invar.isNegated();
							mutantInfoList.add(mutantID,
									mutantOperator + "\t" + block.getLocalID() + "\t" + invar.getFormalParameter() + "\t"
											+ inverter + " -> " + !inverter);
							IID_list.add(mutantID);
							invar.setNegated(!inverter);
							writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
							invar.setNegated(inverter);
						}
						for (int m = 0; m < 2; m++) {
							IOutVariableInBlock outvar = block.getOutVariables().get(m);
							inverter = outvar.isNegated();
							mutantInfoList.add(mutantID,
									mutantOperator + "\t" + block.getLocalID() + "\t" + outvar.getFormalParameter() + "\t"
											+ inverter + " -> " + !inverter);
							IID_list.add(mutantID);
							outvar.setNegated(!inverter);
							writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
							outvar.setNegated(inverter);
						}
					}
					else if (funcName.contains("CTU") || funcName.contains("CTD")) {
						for (int n = 0; n < 2; n++) {
							IInVariableInBlock invar = block.getInVariables().get(n);
							inverter = invar.isNegated();
							mutantInfoList.add(mutantID,
									mutantOperator + "\t" + block.getLocalID() + "\t" + invar.getFormalParameter() + "\t"
											+ inverter + " -> " + !inverter);
							IID_list.add(mutantID);
							invar.setNegated(!inverter);
							writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
							invar.setNegated(inverter);
						}
						
						IOutVariableInBlock outvar = block.getOutVariables().get(0);
						inverter = outvar.isNegated();
						mutantInfoList.add(mutantID,
								mutantOperator + "\t" + block.getLocalID() + "\t" + outvar.getFormalParameter() + "\t"
										+ inverter + " -> " + !inverter);
						IID_list.add(mutantID);
						outvar.setNegated(!inverter);
						writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
						outvar.setNegated(inverter);
					}
				}
			}
			// INVAR에 대한 Mutations: CVR (Constant Value Replacement)
			mutantOperator = "CVR";
			String number = "";
			int oldNumber, newNumber;
			if(mutantOperatorList.get("CVR")) {
				for (IInVariable invar : body.getInVariables()) {
					number = invar.getExpression().trim();
					if (isNumber(number)) {
						oldNumber = Integer.parseInt(number);
						int[] numbers = {oldNumber-1, oldNumber-2, oldNumber+1, oldNumber+2};
						for(int index = 0; index < numbers.length; index++) {
							newNumber = numbers[index];
							mutantInfoList.add(mutantID, mutantOperator + "\t" + invar.getLocalID() + "\t" + number + " -> "
									+ newNumber);
							CVR_list.add(mutantID);
							invar.setExpression(newNumber + "");
							writeXML(xml, dirPath + "mutant_" + String.format("%04d", mutantID++) + ".xml");
							invar.setExpression(number);
						}
					}
				}
			}
		}
		
	}
	
	public  static void perm1(String s) { perm1("", s); }
	private static void perm1(String prefix, String s) {
	    int n = s.length();
	    if (n == 0) permutation.add(prefix);
	    else {
	        for (int i = 0; i < n; i++)
	        	perm1(prefix + s.charAt(i), s.substring(0, i) + s.substring(i+1, n));
	    }
	
	}
	
	private boolean isNumber(String str) {
		return Pattern.matches("[0-9]+", str);
	}
}
