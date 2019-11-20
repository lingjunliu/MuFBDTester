package PLC_related;
import java.util.ArrayList;
import java.util.List;

public class DPath implements Comparable<DPath> {
	public int dpath_length;
	public int dpath_subindex;
	public int DPathID;
	public String datapath_str;
	public String dpc_str;
	public Element startElem;
	public Element endElem;
	public List<Connection> connections = new ArrayList<Connection>();
	public LogicStatement DPC;
	public boolean covered = false;
	public int dPathType = -1;
	public int identifier = 0;

	@Override
	public int compareTo(DPath dpath) {
		String my = endElem.outvar.getExpression();
		String other = dpath.endElem.outvar.getExpression();
		if (my.compareTo(other) != 0)
			return my.compareTo(other);
		if (dpath_length != dpath.dpath_length)
			return dpath_length - dpath.dpath_length;
		return dpath_subindex - dpath.dpath_subindex;
	}
}
