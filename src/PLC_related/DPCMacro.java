package PLC_related;
public class DPCMacro implements Comparable<DPCMacro> {
	public String DPC;
	public String macroname;
	double value;

	@Override
	public int compareTo(DPCMacro arg0) {
		// TODO Auto-generated method stub
		return macroname.compareTo(arg0.macroname);
	}
}