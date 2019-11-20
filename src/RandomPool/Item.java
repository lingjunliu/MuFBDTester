package RandomPool;

public class Item {

	final int counterMax = 10;
	final int integerMax = 10000;

	private String name;
	private int type = -1; // 0: Boolean, 1: Counter, 2: Integer
	private int value = Integer.MIN_VALUE; // for Boolean, 0=false, 1=true.

	public Item(String name, int type) {
		this.name = name;
		this.type = type;

		if (type > 2) {
			System.out.println("Type error: " + type);
			System.exit(-1);
		}

		random();
	}

	public Item(String name, int value, int type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}
		
	public Item(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public void random() {
		switch (type) {
		case 0:
			value = (int) Math.round(Math.random());
			break;
		case 1:
			value = (int) (Math.random() * 2 * counterMax - counterMax);
			break;
		case 2:
			value = (int) (Math.random() * 2 * integerMax - integerMax);
			break;
		}
	}

}
