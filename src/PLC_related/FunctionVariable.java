package PLC_related;
public class FunctionVariable {
	public static int PRE = 0;
	public static int IN = 1;
	static int TIMER = 2; // timer variable.
	public int type;
	public long block_id;
	public String name;
	public LogicStatement logic;

	public FunctionVariable(int type, String name, long block_id) {
		this.name = name;
		this.type = type;
		this.block_id = block_id;
		this.logic = new LogicStatement(LogicStatement.VALUE, 0.0);
	}

	public FunctionVariable(int type, String name, long block_id, LogicStatement logic) {
		this.name = name;
		this.type = type;
		this.block_id = block_id;
		this.logic = logic;
	}
}