package MuFBDTesting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Structure.TestCase;

public class YicesFileGenerator {
	
	static BufferedWriter yicesWriter;
	static XML_load xml_load = new XML_load();
	static XML_load xml_load_M = new XML_load();
	static ArrayList<String> mutantFiles = new ArrayList<String>();
	static ArrayList<Integer> mutantIDList = new ArrayList<Integer>();
	static boolean executed = false;
	static String Path = "";
	Random random = new Random();
	
	public static void getMutantFiles(File dir, ArrayList<Integer> mutantIsSelected) throws IOException {
		File[] files = dir.listFiles();
		mutantIDList = new ArrayList<Integer>();
		for (File file : files) 
			if (!file.isDirectory()) {
				if(file.getName().contains("mutant") && file.getName().contains("xml")) {
					String temp = file.getName().substring(7, 11);
					if(mutantIsSelected.contains(Integer.parseInt(temp))) {
						mutantIDList.add(Integer.parseInt(temp));
						mutantFiles.add(file.getName());
					}
				}
			}
	}
	/**
	 * yices 파일 아래부분에 공통적으로 들어가는 내용에 대한 출력
	 * @author donghwan
	 * @param fileName .ys file name
	 * 
	 */
	static String mutant_directory = System.getProperty("user.dir")+"/output/"+CreateGUI.target+"/";
	void YicesFileLoad(String filePath) throws IOException {
		
		XML_load.mutant = false;
		xml_load.loadXML(filePath);
		executed = false;
	}
	
	ArrayList<String> OriginalYicesHeaderList = new ArrayList<String>();
	void OriginalYicesHeader() throws IOException {
		
		XML_load.mutant = false;
		
		try {
			xml_load.generateTestSuites(1, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		OriginalYicesHeaderList = new ArrayList<String>(XML_load.yicesHeaderList);
	}
	
	void YicesFileGenerate(String path) throws IOException, InterruptedException {
		
		Path = path;
		
		ArrayList<Integer> mutantIsSelected = new ArrayList<Integer>();
		if(!MutantGenerator.CVR_list.isEmpty()) {
			Collections.shuffle(MutantGenerator.CVR_list);
			mutantIsSelected.addAll(MutantGenerator.CVR_list.subList(0, (int)(MutantGenerator.CVR_list.size()*Integer.parseInt(CreateGUI.mutantPercentages.get(0).getText())/100.0)));
		}

		if(!MutantGenerator.IID_list.isEmpty()) {
			Collections.shuffle(MutantGenerator.IID_list);
			mutantIsSelected.addAll(MutantGenerator.IID_list.subList(0, (int)(MutantGenerator.IID_list.size()*Integer.parseInt(CreateGUI.mutantPercentages.get(1).getText())/100.0)));
		}
		
		if(!MutantGenerator.SWI_list.isEmpty()) {
			Collections.shuffle(MutantGenerator.SWI_list);
			mutantIsSelected.addAll(MutantGenerator.SWI_list.subList(0, (int)(MutantGenerator.SWI_list.size()*Integer.parseInt(CreateGUI.mutantPercentages.get(2).getText())/100.0)));
		}
		
		if(!MutantGenerator.ABR_list.isEmpty()) {
			Collections.shuffle(MutantGenerator.ABR_list);
			mutantIsSelected.addAll(MutantGenerator.ABR_list.subList(0, (int)(MutantGenerator.ABR_list.size()*Integer.parseInt(CreateGUI.mutantPercentages.get(3).getText())/100.0)));
		}
		
		if(!MutantGenerator.CBR_list.isEmpty()) {
			Collections.shuffle(MutantGenerator.CBR_list);
			mutantIsSelected.addAll(MutantGenerator.CBR_list.subList(0, (int)(MutantGenerator.CBR_list.size()*Integer.parseInt(CreateGUI.mutantPercentages.get(4).getText())/100.0)));
		}
		
		if(!MutantGenerator.LBR_list.isEmpty()) {
			Collections.shuffle(MutantGenerator.LBR_list);
			mutantIsSelected.addAll(MutantGenerator.LBR_list.subList(0, (int)(MutantGenerator.LBR_list.size()*Integer.parseInt(CreateGUI.mutantPercentages.get(5).getText())/100.0)));
		}
		
		if(!MutantGenerator.SBR_list.isEmpty()) {
			Collections.shuffle(MutantGenerator.SBR_list);
			mutantIsSelected.addAll(MutantGenerator.SBR_list.subList(0, (int)(MutantGenerator.SBR_list.size()*Integer.parseInt(CreateGUI.mutantPercentages.get(8).getText())/100.0)));
		}
		
		if(!MutantGenerator.BBR_list.isEmpty()) {
			Collections.shuffle(MutantGenerator.BBR_list);
			mutantIsSelected.addAll(MutantGenerator.BBR_list.subList(0, (int)(MutantGenerator.BBR_list.size()*Integer.parseInt(CreateGUI.mutantPercentages.get(9).getText())/100.0)));
		}
		
		if(!MutantGenerator.EBR_list.isEmpty()) {
			Collections.shuffle(MutantGenerator.EBR_list);
			mutantIsSelected.addAll(MutantGenerator.EBR_list.subList(0, (int)(MutantGenerator.EBR_list.size()*Integer.parseInt(CreateGUI.mutantPercentages.get(10).getText())/100.0)));
		}
		
		if(!MutantGenerator.CouBR_list.isEmpty()) {
			Collections.shuffle(MutantGenerator.CouBR_list);
			mutantIsSelected.addAll(MutantGenerator.CouBR_list.subList(0, (int)(MutantGenerator.CouBR_list.size()*Integer.parseInt(CreateGUI.mutantPercentages.get(11).getText())/100.0)));
		}
		
		if(!MutantGenerator.TBR_list.isEmpty()) {
			Collections.shuffle(MutantGenerator.TBR_list);
			mutantIsSelected.addAll(MutantGenerator.TBR_list.subList(0, (int)(MutantGenerator.TBR_list.size()*Integer.parseInt(CreateGUI.mutantPercentages.get(12).getText())/100.0)));
		}
		
		mutantFiles = new ArrayList<String>();
		mutant_directory = System.getProperty("user.dir")+"/output/"+CreateGUI.target+"/";
		File mutantDir = new File(mutant_directory);
		getMutantFiles(mutantDir, mutantIsSelected);
		
		ArrayList<TestCase> testSuite = new ArrayList<TestCase>();
		BufferedWriter mutantFileWriter = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/output/" + CreateGUI.target + "/unsatMutant.txt"));
		
		for(int i=0; i<mutantFiles.size(); i++) {
			
			yicesWriter = new BufferedWriter(new FileWriter(path+mutantIDList.get(i)+".ys"));
			
			for(String definition: OriginalYicesHeaderList) {
				yicesWriter.write(definition);
			}
			yicesWriter.write("\r\n");
			
			XML_load.mutant = true;
			xml_load_M.loadXML(mutant_directory+"\\"+mutantFiles.get(i));
			try {
				xml_load_M.generateTestSuites(1, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(String definition: XML_load.yicesHeaderList) {
				yicesWriter.write(definition);
			}
			yicesWriter.write("\r\n");
			assertion();
			yicesFooter(path+CreateGUI.target+"/"+mutantIDList.get(i)+".ys");
			
			String[] result = yicesExecuter(path+mutantIDList.get(i)+".ys");
			
			String testCase = "";
			boolean equalMutant = false;
			for(String str: result) {
				if(str.contains("Error")) break;
				if(str.equals("unsat")) {
					System.out.println("equivalent mutant");
					equalMutant = true;
					mutantFileWriter.write("equivalent mutant: "+mutantIDList.get(i)+"\r\n");
					break;
				}
				else if(str.equals("sat")) {
					System.out.println("mutant");
				}
				if(str.contains("(= ")) {
					testCase += str;
				}
			}
			
			if(!testCase.equals("")) {
				TestCase tc = new TestCase(testCase);
				tc.mutantNo = i;
				testSuite.add(tc);
				System.out.println("------------testcase-------------");
				System.out.println(tc);
			}
			else {
				if(!equalMutant) {
					mutantFileWriter.write("error mutant: "+mutantIDList.get(i)+"\r\n");
				}
			}
		}
		mutantFileWriter.close();
		solutionWriter(testSuite, "MuFBD"+".txt");
		executed = true;
	}
	
	void YicesFileGenerate_original() throws IOException {
		try {
			xml_load.generateTestSuites(1, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> OriginalYicesHeaderList = new ArrayList<String>(XML_load.yicesHeaderList);
		
		yicesWriter = new BufferedWriter(new FileWriter(mutant_directory+"Original.ys"));
		
		for(String definition: OriginalYicesHeaderList) {
			yicesWriter.write(definition);
		}
		yicesWriter.write("\r\n");
			
		yicesFooter(mutant_directory+"Original.ys");
		
	}
	
	private static void assertion() {
		try {
			if (xml_load.outputs.length==1) {
				yicesWriter.write("(assert ");
				String str = xml_load.outputs[0];
				yicesWriter.write("(/= ");
				yicesWriter.write(str);
				yicesWriter.write(" M_");
				yicesWriter.write(str);
				yicesWriter.write("))\r\n");
			}
			else {
				yicesWriter.write("(assert (or");
				for(String str: xml_load.outputs) {
					yicesWriter.write("(/= ");
					yicesWriter.write(str);
					yicesWriter.write(" M_");
					yicesWriter.write(str);
					yicesWriter.write(") ");
				}
				
				yicesWriter.write("))\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("FATAL ERROR: assertion()");
			System.exit(-1);
		}
	}
	
	private static void yicesFooter(String fileName) {
		try {
			yicesWriter.write("\r\n");
			yicesWriter.write("\r\n;; Rule 3. Execute check\r\n(check)");
			yicesWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("FATAL ERROR: yicesFooter()");
			System.exit(-1);
		}
		System.err.println("Generating " + fileName + " ... Completed.");
	}

	/**
	 * executeCommand() 함수를 이용하여 yices 실행
	 * @author donghwan
	 * @param fileName yices 파일 이름
	 * 
	 */
	private static String[] yicesExecuter(String fileName) {
		ArrayList<String> commandList = new ArrayList<String>();
		commandList.add(System.getProperty("user.dir")+"/yices-1.0.40/bin/yices.exe");
		commandList.add(fileName);

		String resultStr = executeCommand(commandList, ".");
		String[] result = resultStr.split("\n");
		return result;
	}
	
	static String executeCommand(List<String> command, String workspaceFolder) {
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(workspaceFolder));

		try {

			Process process;
			process = builder.start();

			InputStream is = process.getInputStream();
			InputStream es = process.getErrorStream();
			InputStreamReader isr = new InputStreamReader(is);
			InputStreamReader esr = new InputStreamReader(es);

			final BufferedReader br = new BufferedReader(isr);
			final BufferedReader ebr = new BufferedReader(esr);
			final StringBuffer result = new StringBuffer();
			final StringBuffer errResult = new StringBuffer();

			Thread outThread = new Thread(new Runnable() {
				@Override
				public void run() {
					String line;
					try {
						while ((line = br.readLine()) != null) {
							result.append(line + "\n");
							System.out.println(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(-1);
					}
				}
			});
			outThread.start();

			Thread errThread = new Thread(new Runnable() {
				@Override
				public void run() {
					String line;
					try {
						while ((line = ebr.readLine()) != null) {
							errResult.append(line + "\n");
							System.out.println(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(-1);
					}
				}

			});
			errThread.start();

			outThread.join();
			errThread.join();

			int exitVal = process.waitFor();

			br.close();
			ebr.close();
			isr.close();
			esr.close();
			is.close();
			es.close();

			process.destroy();

			if (exitVal != 0) {
				System.out.println(result);
				System.err.println("Execution Error!-1");
				System.err.println(errResult);
				//                System.exit(exitVal);
				return "unsatisfied assertion ids";
			}

			return result.toString();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Execution Error!-2");
			System.exit(-1);
		}

		return null;
	}
	/**
	 * @author donghwan + Jiyoung
	 * @param solutionStr 솔루션 로그 파일에 적을 내용
	 * @param fileName  솔루션 로그 파일 이름 (fileName.txt으로 저장)
	 * @param st (variable0 value0)(variable1 value1)...
	 * @param tc (variable0 value0
	 */
	private static void solutionWriter(ArrayList<TestCase> testSuite, String fileName) {
		// * solution.txt 파일 출력
		String[] sortedInput = new String[xml_load.inputs.length];
		for(int i = 0; i<xml_load.inputs.length; i++){
			sortedInput[i] = xml_load.inputs[i];
		}
		Arrays.sort(sortedInput, String.CASE_INSENSITIVE_ORDER);
		String[][] tempArray = new String[3][xml_load.inputs.length]; 
		for(int i = 0; i<xml_load.inputs.length; i++){
			tempArray[1][i] =sortedInput[xml_load.inputs.length-i-1];
			for(int j = 0; j<xml_load.inputs.length; j++){
				if(xml_load.inputs[j].equals(tempArray[1][i])){
					tempArray[0][i]= ""+j;
					tempArray[2][i] = ParseXML.InputInterface.get(tempArray[1][i]).get(0);
					break;
				}
			}
		}
		try {
			/*constant writing start----------------------------------------------------------------------*/
			BufferedWriter solution = new BufferedWriter(new FileWriter(Path+fileName));
			solution.write("### constants\r\n");
			for (int i = 0 ; i< xml_load.constants.length; i++)
				solution.write(xml_load.constants[i]+"\t"+xml_load.cTypes[i]+"\r\n");
			solution.write("\r\n");
			//----------------------------------------------------------------------constant writing ends */
			
			/*input variable writing start---------------------------------------------------------------*/
			solution.write("### inputs\r\n");
			for (int i = 0; i< xml_load.inputs.length-1; i++)
				solution.write(tempArray[1][i]+", ");
			solution.write(tempArray[1][xml_load.inputs.length-1]+"\r\n\r\n");
			//-----------------------------------------------------------------input variable writing end*/
			
			/*output variable writing start----------------------------------------------------------------------*/
			solution.write("### outputs\r\n");
			ArrayList<String> tempOutput = new ArrayList<String>();
			for (int i =0; i< xml_load.outputs.length; i++){
				String str = xml_load.outputs[i].replace("_out", "");
				if (!tempOutput.contains(str))
					tempOutput.add(str);
			}
			for (int i = 0; i< tempOutput.size()-1; i++)
				solution.write(tempOutput.get(i)+", ");
			solution.write(tempOutput.get(tempOutput.size()-1)+"\r\n\r\n");
			//-------------------------------------------------------------------------output variable writing end*/

			/*test sequence writing start----------------------------------------------------*/
			solution.write("### number of test sequences\r\n"+testSuite.size()+"\r\n\r\n");
			solution.write("### test sequence\r\n");
			int tcNum = 0;
			for(TestCase testCase: testSuite){
				solution.write("TS"+tcNum+"\r\n");
				String[][] ts = new String[XML_load.setIter+2][xml_load.inputs.length];
				// initialize inputs (type boolean -> false, others -> 0)
				for(int i = 0; i< xml_load.inputs.length; i++){
					if(tempArray[2][i].equals("BOOL")) {
						for(int j = 0; j<XML_load.setIter+2; j++) {
							ts[j][i] = "false";
						}
					}
					else {
						for(int j = 0; j<XML_load.setIter+2; j++) {
							ts[j][i] = "0";
						}
					}
				}
				
				for(String key: testCase.valueMap.keySet()) {
					for(int i = 0; i < xml_load.inputs.length; i++) {
						String pattern = tempArray[1][i]+"_t[0-9]+";
						Pattern r = Pattern.compile(pattern);
						// Now create matcher object.
						Matcher m = r.matcher(key);
						if (m.matches( )) {
							int cycleNum = Integer.parseInt(key.substring(tempArray[1][i].length()+2));
							ts[cycleNum][i] = testCase.valueMap.get(key);
							break;
						}
						if(key.equals(tempArray[1][i])) {
							ts[0][i] = testCase.valueMap.get(key);
							break;
						}
					}
				}
				
				for (int i = 0; i<xml_load.inputs.length; i++){
					for (int j = 0; j<tempOutput.size(); j++){
						if(tempArray[1][i].equals(tempOutput.get(j))){
							for(int k = xml_load.setIter; k>=0; k--)
								ts[k][i]="-";
							break;
						}
					}
				}
				
				/*writing one test sequence start------------------------------------------------*/
				for(int i = XML_load.setIter+1; i >= 0 ; i--){
					solution.write("_t"+i+"\r\n");
					for(int j = 0; j < xml_load.inputs.length; j++){
						solution.write(ts[i][j]+"\t");
					}
					solution.write("\r\n");
				}
				tcNum++;
				solution.write("\r\n");
			}
			
			//---------------------------------------------------writing one test sequence end*/
			solution.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("solutionWriter(): FATAL ERR");
			System.exit(-1);
		}
	}
}
