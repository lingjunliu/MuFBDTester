package PLC_related;
import plcopen.inf.type.group.fbd.IBlock;
import plcopen.inf.type.group.fbd.IInVariable;
import plcopen.inf.type.group.fbd.IOutVariable;

public class Element {
	public static final int INVAR = 1;
	public static final int BLOCK = 2;
	public static final int OUTVAR = 3;

	public static final int BOOLEAN = 1;
	public static final int INTEGER = 2;
	public static final int REAL = 3;

	public String FormalParam;

	public int type;
	public long LocalID;

	public IInVariable invar;
	public IBlock block;
	public IOutVariable outvar;

	public Element prevElement;
	public Element nextElement;

	public String value;
	public int valueType;

	public boolean constant = false;

	public Element(int type, long LocalID) {
		this.type = type;
		this.LocalID = LocalID;
	}
}