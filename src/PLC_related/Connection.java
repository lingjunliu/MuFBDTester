package PLC_related;
public class Connection {
	public long start;
	public String startParam;
	public long end;
	public String endParam;
	public boolean negated = false;

	public Connection(long start, String startParam, long end, String endParam) {
		this.start = start;
		this.startParam = startParam;
		this.end = end;
		this.endParam = endParam;
	}
}