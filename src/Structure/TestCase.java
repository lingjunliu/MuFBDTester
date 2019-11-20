package Structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;

/** 
 * 嚙趟葛嚙赭給嚙趣 TestCase��蕭 嚙踝蕭嚙趣嚙趟葛皝莎蕭 嚙趣�蕭賳� ���圾嚙�.
 * 嚙趣�蕭��� Yices��蕭 嚙趟嚙趟董 test case嚙趟�� 嚙趟�蕭��剁蕭 慦�蕭 嚙趣�蕭篧哨蕭�.
 * (= TRIP true)
 * (= PV_OUT 100)
 * ...
 * 
 * 慦蕭 頩蕭嚙趟�蕭� 嚙趣�篨抬蕭 inputs嚙趟�� 嚙踝蕭嚙趣嚙趟葛�剁蕭,
 * 頩蕭嚙趟��-慦蕭 嚙趟�蕭嚙� valueMap嚙趟�� 嚙踝蕭嚙趣嚙趟董嚙趟��.
 * 
 * @author donghwan
 *
 */
public class TestCase {
	
	public int mutantNo = 0;
	//public int counter = 0;
	public String originalTC;

	public ArrayList<String> inputs; // Test case inputs
	public HashMap<String, String> valueMap; // input-value pair

	// sample tc: (TRIP true)(PTRIP true)(RST_MCR_LATCH false)(PV_OUT 2)
	public TestCase(String tc) {
		originalTC = tc;
		inputs = new ArrayList<String>();
		valueMap = new HashMap<String, String>();

		String input = "", value = ""; // Temporal variables

		StringTokenizer token = new StringTokenizer(tc.trim(), "{}()= ");
		while (token.hasMoreTokens()) {
			input = token.nextToken().trim();
			value = token.nextToken().trim();

			// Test case data structure construction
			inputs.add(input);
			valueMap.put(input, value);
		}
	}
	
	public String getValue(String input) {
		return valueMap.get(input);
	}
	
	public String toString() {
		String ret = "";
		
		Collections.sort(inputs);
		for(String input: inputs)
			ret += "("+input + " " + valueMap.get(input) + ")";
		
		return ret.trim();
	}
}
