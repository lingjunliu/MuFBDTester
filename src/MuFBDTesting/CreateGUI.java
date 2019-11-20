package MuFBDTesting;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import MuFBDTesting.XML_load;

public class CreateGUI extends Frame implements ActionListener {
	
	static JFrame window;
	static Container content;
	
	static JButton openButton;
	static JTextField filePath;
	static JLabel xmlStatus;
	
	public static String target;
	
	static JTextField mutantFilePath;
	static JButton mutantOpenButton;
	
	public static List<JCheckBox> constantsCheckboxs = new ArrayList<JCheckBox>();
	
	public static List<JLabel> constantLabels = new ArrayList<JLabel>();
	public static List<JTextField> constantValues = new ArrayList<JTextField>();
	
	public static JButton executeButton;
	public static JButton assessButton;
	
	static JPanel leftPanel;
	public static List<JCheckBox> mutantOperator = new ArrayList<JCheckBox>();
	JButton select;
	public static List<JTextArea> mutantPercentages = new ArrayList<JTextArea>();	
	public static JPanel panel_constant;
	
	static JPanel panel_console;
	static JTextArea console;
	static String console_text = "";
	
	YicesFileGenerator yicesFileGenerate;
	MutantGenerator mg;
	
	public static void console_flush() {
		console.setText(console_text);
		int pos = console.getText().length();
		console.setCaretPosition(pos);
		console.requestFocus();
	}

	public static void console_print(String s) {
		console_text = console_text + s;
	}

	public static void console_println(String s) {
		console_text = console_text + s + "\n";
	}
	
	private static void createPercentageSetting() {
		JTextArea percent = new JTextArea(1, 2);
		percent.setText("100");
		JLabel label = new JLabel("%");
		label.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantPercentages.add(percent);
		leftPanel.add(percent);
		leftPanel.add(label);
	}
	
	public CreateGUI() throws IOException {
		
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(760, 800);

		// window.setResizable(false);
		window.setTitle("MuFBDTester");
		window.setVisible(true);

		content = window.getContentPane();
		content.setLayout(new BorderLayout());
		
		// create the upper panel for buttons opening file and directory
		openButton = new JButton("Open...");
		openButton.addActionListener(this);
		openButton.setActionCommand("openXML");
		
		filePath = new JTextField(20);
		xmlStatus = new JLabel("");
		/*
		mutantFilePath = new JTextField(20);

		mutantOpenButton = new JButton("Open...");
		mutantOpenButton.addActionListener(this);
		mutantOpenButton.setActionCommand("mutantOpen");*/
		
		JPanel panel_XML = new JPanel();
		panel_XML.setPreferredSize(new Dimension(750, 40));
		panel_XML.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		
		JLabel label = new JLabel("    ");
		panel_XML.add(label);
		label = new JLabel("XML file : ");
		panel_XML.add(label);
		panel_XML.add(filePath);
		panel_XML.add(openButton);
		panel_XML.add(xmlStatus);
		//panel_XML.add(new JLabel("    "));
		/*
		panel_XML.add(new JLabel("Mutant Directory : "));
		panel_XML.add(mutantFilePath);
		panel_XML.add(mutantOpenButton);
		*/
		content.add("North", panel_XML);
		
		//create the left panel for constants and testcase
		leftPanel = new JPanel();
		leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		leftPanel.setPreferredSize(new Dimension(670, 2000));
		leftPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		
		label = new JLabel("                                                                     Mutant Operator                                         ");
		label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
		leftPanel.add(label);
		JCheckBox Check = new JCheckBox("  Constant Value Replacement", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		
		label = new JLabel("         ");
		label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
		leftPanel.add(label);
		Check = new JCheckBox("  Inverter Insertion or Deletion", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		Check = new JCheckBox("  Switch Inputs", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		label = new JLabel("                                      ");
		label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
		leftPanel.add(label);
		Check = new JCheckBox("  Arithmetic Block Replacement", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		Check = new JCheckBox("  Comparison Block Replacement", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		label = new JLabel("    ");
		label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
		leftPanel.add(label);
		Check = new JCheckBox("  Logic Block Replacement", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		Check = new JCheckBox("  Converter Block Replacement", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		label = new JLabel("        ");
		label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
		leftPanel.add(label);
		Check = new JCheckBox("  Numerical Block Replacement", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		Check = new JCheckBox("  Selection Block Replacement", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		label = new JLabel("          ");
		label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
		leftPanel.add(label);
		Check = new JCheckBox("  Bistable element Block Replacement", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		Check = new JCheckBox("  Edge detection Block Replacement", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		Check = new JCheckBox("  Counter Block Replacement", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		Check = new JCheckBox("  Timer Block Replacement", false);
		Check.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		mutantOperator.add(Check);
		leftPanel.add(Check);
		createPercentageSetting();
		
		
		
		for (JCheckBox ch: mutantOperator) {
			ch.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	                //System.out.println("Check");
	            	if(!ch.isSelected()) {
	            		select.setText("Select All");
	            	}
	            }
	        });
		}
		
		label = new JLabel("                                         ");
		label.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		leftPanel.add(label);
		
		select = new JButton("Select All");
		select.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		select.addActionListener(this);
		select.setActionCommand("select all");
		leftPanel.add(select);
		
		JButton automationButton = new JButton("automation");
		automationButton.addActionListener(this);
		automationButton.setActionCommand("automation");
		leftPanel.add(automationButton);
		
		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(leftPanel);
		content.add(jsp);
		
		panel_console = new JPanel();
		panel_console.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		panel_console.setPreferredSize(new Dimension(450, 225));
		
		console = new JTextArea(13, 112);
		console.setFont(new Font("Courier New", Font.PLAIN, 13));
		JScrollPane jsp3 = new JScrollPane(console);
		panel_console.add(jsp3);
		content.add("South", panel_console);
		jsp3.setPreferredSize(new Dimension(window.getWidth() - 30, 211));
		
		window.repaint();
		window.setVisible(true);
		executeButton = new JButton("Generate Mutation-Based Test Suite");
		executeButton.addActionListener(this);
		executeButton.setActionCommand("execute");
		assessButton = new JButton("Assess Coverage Level");
		assessButton.addActionListener(this);
		assessButton.setActionCommand("coverage-level");
		//yicesFileGenerate.YicesFileGenerate_original();
		
	}
	
	private static String dirPath = System.getProperty("user.dir")+"/output/";
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		String cmd = arg0.getActionCommand();
		if (cmd.equals("openXML")) {
			try {
				open(0);
				if (!filePath.getText().equals("")) {
					new File(dirPath+target).mkdirs();
					mg = new MutantGenerator();
					yicesFileGenerate = new YicesFileGenerator();
					yicesFileGenerate.YicesFileLoad(filePath.getText());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		else if (cmd.equals("mutantOpen")) {
			try {
				open(1);
				if (!filePath.getText().equals("")&&!mutantFilePath.getText().equals("")) {
					yicesFileGenerate = new YicesFileGenerator();
					yicesFileGenerate.YicesFileLoad(filePath.getText(), mutantFilePath.getText());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		else if (cmd.equals("execute")) {
			try {
				boolean executeOrNot = true;
				boolean checked = false;
				MutantGenerator.deselectAllOperator();
				for (int j = 0; j < mutantOperator.size(); j++) {
					if(mutantOperator.get(j).isSelected()) {
						checked = true;
						
						if(j==0) {
							MutantGenerator.mutantOperatorList.put("CVR", true);
						}
						else if(j==1) {
							MutantGenerator.mutantOperatorList.put("IID", true);
						}
						else if(j==2) {
							MutantGenerator.mutantOperatorList.put("SWI", true);
						}
						else if(j==3) {
							MutantGenerator.mutantOperatorList.put("ABR", true);
						}
						else if(j==4) {
							MutantGenerator.mutantOperatorList.put("CBR", true);
						}
						else if(j==5) {
							MutantGenerator.mutantOperatorList.put("LBR", true);
						}
						else if(j==6) {
							MutantGenerator.mutantOperatorList.put("ConBR", true);
							console_println("We don't suport converter block replacement.");
							console_flush();
						}
						else if(j==7) {
							MutantGenerator.mutantOperatorList.put("NBR", true);
							console_println("We don't suport numerical block replacement.");
							console_flush();
						}
						else if(j==8) {
							MutantGenerator.mutantOperatorList.put("SBR", true);
						}
						else if(j==9) {
							MutantGenerator.mutantOperatorList.put("BBR", true);
						}
						else if(j==10) {
							MutantGenerator.mutantOperatorList.put("EBR", true);
						}
						else if(j==11) {
							MutantGenerator.mutantOperatorList.put("CouBR", true);
						}
						else if(j==12) {
							MutantGenerator.mutantOperatorList.put("TBR", true);
						}
						//break;
					}
				}
				if (!checked) {
					console_println("Please select at least one mutant operator.");
					console_flush();
					executeOrNot = false;
				}
				
				if(CreateGUI.constantValues.get(0).getText().isEmpty()) {
					console_println("Please enter constants.");
					console_flush();
					executeOrNot = false;
				}
				int j = 0;
				for (int i = 0; i < XML_load.inputList.size(); i++) {
					if(ParseXML.InputInterface.get(XML_load.inputList.get(i))!=null) {
						if(XML_load.constantList.contains(XML_load.inputList.get(i))){
							if (constantValues.get(j+1).getText().isEmpty()) {
								//CreateGUI.executeButton.setEnabled(false);
								executeOrNot = false;
								System.out.println("Please enter constants");
								console_println("Please enter constants");
								console_flush();
								break;
							}
							else {
								if (ParseXML.InputInterface.get(XML_load.inputList.get(i)).get(0).equals("BOOL")) {
									if (constantValues.get(j+1).getText().equals("TRUE")||constantValues.get(j+1).getText().equals("FALSE")) {
										ParseXML.InputInterface.get(XML_load.inputList.get(i)).set(1, constantValues.get(j+1).getText());
									}
									else {
										System.out.println("Please enter a boolean value for "+XML_load.inputList.get(i));
										console_println("Please enter a boolean value for "+XML_load.inputList.get(i));
										console_flush();
										executeOrNot = false;
										break;
									}
								}
								else {
									try {
										Integer.parseInt(constantValues.get(j+1).getText());
									} catch (NumberFormatException e){
										System.out.println(e);
										executeOrNot = false;
										System.out.println("Please enter a integer for "+XML_load.inputList.get(i));
										console_println("Please enter a integer for "+XML_load.inputList.get(i));
										console_flush();
									}
									if (executeOrNot) {
										ParseXML.InputInterface.get(XML_load.inputList.get(i)).set(1, constantValues.get(j+1).getText());
									}
									else {
										break;
									}
								}
							}
						}
						j++;
					}
				}
				if(executeOrNot) {
					BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/output/" + target + "/Execution_time.txt"));
					
					long startTime = System.currentTimeMillis();
					mg.generateMutant(filePath.getText());
					
					if (!yicesFileGenerate.executed) {
						yicesFileGenerate.OriginalYicesHeader();
					}
					yicesFileGenerate.YicesFileGenerate(dirPath+target+"/");
					//yicesFileGenerate.YicesFileGenerate_original();
					long estimatedTime = System.currentTimeMillis() - startTime;
					writer.write("Time: " + estimatedTime/1000.0 + " sec\r\n");
					writer.close();
					
					console_println("Finished!");
					console_flush();
				}
				else {
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(cmd.equals("select all")) {
			if(select.getText().equals("Select All")) {
				for(int i = 0; i < mutantOperator.size(); i++) {
					mutantOperator.get(i).setSelected(true);
				}
				select.setText("Cancel selecting all");
			}
			else if(select.getText().equals("Cancel selecting all")){
				for(int i = 0; i < mutantOperator.size(); i++) {
					mutantOperator.get(i).setSelected(false);
				}
				select.setText("Select All");
			}
			MutantGenerator.deselectAllOperator();
			
		}
		else if (cmd.equals("automation")) {
			try {
				target = "FFTD";
				String filepath = "C:\\Users\\Master\\Documents\\SElab\\MuFBDTester\\input\\";
				filePath.setText(filepath + target +".xml");
				if (!filePath.getText().equals("")) {
					new File(dirPath+target).mkdirs();
					mg = new MutantGenerator();
					yicesFileGenerate = new YicesFileGenerator();
					yicesFileGenerate.YicesFileLoad(filePath.getText());
					
				}
				MutantGenerator.deselectAllOperator();
				String mOperator = "CBR";
				String percent = "100";
				int operatorIndex = 0;
				
				if(mOperator.equals("CVR"))operatorIndex = 0;
				else if(mOperator.equals("IID"))operatorIndex = 1;
				else if(mOperator.equals("SWI"))operatorIndex = 2;
				else if(mOperator.equals("ABR"))operatorIndex = 3;
				else if(mOperator.equals("CBR"))operatorIndex = 4;
				else if(mOperator.equals("LBR"))operatorIndex = 5;
				else if(mOperator.equals("TBR"))operatorIndex = 12;
				
				MutantGenerator.mutantOperatorList.put(mOperator, true);
				mutantPercentages.get(operatorIndex).setText(percent);
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/output/" + target + "/Execution_time.txt"));
				
				long startTime = System.currentTimeMillis();
				mg.generateMutant(filePath.getText());
				
				if (!yicesFileGenerate.executed) {
					yicesFileGenerate.OriginalYicesHeader();
				}
				for (int i = 1; i <= 1; i++) {
					String path = dirPath+target+"//"+mOperator+"_"+percent+"%-"+i;
					new File(path).mkdirs();
					yicesFileGenerate.YicesFileGenerate(path+"//");
				}
				//yicesFileGenerate.YicesFileGenerate_original();
				long estimatedTime = System.currentTimeMillis() - startTime;
				writer.write("Time: " + estimatedTime/1000.0 + " sec\r\n");
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			console_println("Finished!");
			console_flush();
			
		}
		else if (cmd.equals("coverage-level")) {
			try {
				XML_load.buildDPath();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	FileDialog fd;
	JFileChooser chooser;
	
	public void open(int type) throws IOException {
		String filepath = "";
		if (type == 0) {
			chooser = new JFileChooser();
			FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter(
				     "xml files (*.xml)", "xml");
			chooser.setDialogTitle("Open XML File");
			chooser.setFileFilter(xmlfilter);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
				/*System.out.println("getCurrentDirectory(): " 
			         +  chooser.getCurrentDirectory());*/
				System.out.println("getSelectedFile() : " 
			         +  chooser.getSelectedFile());
				/*console_println("getSelectedFile() : " 
			         +  chooser.getSelectedFile());
				console_flush();*/
				String[] splitName = chooser.getSelectedFile().getName().split("\\.");
				target = splitName[0];
				filepath = ""+chooser.getSelectedFile();
				filePath.setText(filepath);
			}
			else {
				System.out.println("No Selection ");
				console_println("No Selection ");
				console_flush();
			}
		}/* else if (type == 1) {
			chooser = new JFileChooser(); 
		    //chooser.setCurrentDirectory(new java.io.File("."));
		    chooser.setDialogTitle("Choose mutant directory");
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    //
		    // disable the "All files" option.
		    //
		    chooser.setAcceptAllFileFilterUsed(false);
		    //    
		    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
		    	System.out.println("getCurrentDirectory(): " 
		         +  chooser.getCurrentDirectory());
		    	System.out.println("getCurrentDirectory() : " 
		         +  chooser.getSelectedFile());
		    	filepath = ""+chooser.getSelectedFile();
		    	mutantFilePath.setText(filepath);
		    }
		    else {
		    	System.out.println("No Selection ");
		    }
		}*/
	}
}
