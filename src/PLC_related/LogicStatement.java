package PLC_related;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.StringTokenizer;

import MuFBDTesting.XML_load;

//import MuFBDTesting.XML_load;

public class LogicStatement {
	public final static int VARIABLE = 0;
	public final static int VALUE = -1;
	public final static int SUBTRACT = 1;
	public final static int PLUS = 2;
	public final static int MULTIPLY = 3;
	public final static int DIVIDE = 4;
	public final static int AND = 5;
	public final static int OR = 6;
	public final static int NOT = 7;
	public final static int TERNARY = 8;
	public final static int GREATEREQUAL = 9;
	public final static int LESSEQUAL = 10;
	public final static int GREATER = 11;
	public final static int LESS = 12;
	public final static int NOTEQUAL = 13;
	public final static int EQUAL = 14;
	public final static int MOD = 15;
	public final static int OPERATOR = 16;
	public final static int FUNCTION = 17;
	public final static int EMBRACE = 18;
	public final static int MINUS = 19;

	public final static int BOOLEAN = 0;
	public final static int INTEGER = 1;
	public final static int REAL = 2;
	
	public DPCLibrary dpcl;
	public long blockId;
	public double blockOrder = 0;
	
	public int brace;

	public int type;
	public int valueType;

	public boolean boolValue;
	public int intValue;
	public double realValue;
	public String variable;

	public LogicStatement L1 = null;
	public LogicStatement L2 = null;
	public LogicStatement L3 = null;
	

	public LogicStatement(int type, boolean b) {
		this.type = type;
		this.boolValue = b;
		this.valueType = BOOLEAN;
	}

	public LogicStatement(int type, double b) {
		this.type = type;
		this.realValue = b;
		this.valueType = REAL;
	}

	public LogicStatement(int type, int b) {
		this.type = type;
		this.intValue = b;
		this.valueType = INTEGER;
	}

	public LogicStatement(int type, LogicStatement L) {
		this.type = type;
		this.L1 = L;
	}

	public LogicStatement(int type, LogicStatement L1, LogicStatement L2) {
		this.type = type;
		this.L1 = L1;
		this.L2 = L2;
	}

	public LogicStatement(int type, LogicStatement L1, LogicStatement L2, LogicStatement L3) {
		this.type = type;
		this.L1 = L1;
		this.L2 = L2;
		this.L3 = L3;
	}

	public LogicStatement(int type, String str) {
		// Variable��蕭?慦儔嚗�?�����.
		this.type = type;
		this.variable = str;
	}

	public LogicStatement(String str) {
		int type = -1, newtype;
		LinkedList<LogicStatement> tokens = new LinkedList<LogicStatement>();
		int bracecnt = 0;
		String specialchar = "~-+*/|&?:>=<=!=%";
		String parenthesis = "()";
		char[] s = str.toCharArray();
		char[] temp = new char[256];
		int tempcnt = 0;
		int bracecnt_to_register = 0;

		for (int i = 0; i < s.length; i++) {
			if (s[i] == '(')
				bracecnt++;
			else if (s[i] == ')') {
				bracecnt--;
			}
			if (parenthesis.indexOf(s[i]) != -1)
				continue;

			// specialchar ��蕭?0, value��蕭?1
			newtype = (specialchar.indexOf(s[i]) != -1) ? 0 : 1;

			if (type == -1)
				type = newtype;
			String tempstr = new String(temp);
			tempstr = tempstr.substring(0, tempcnt);
			if (type != newtype || (specialchar.contains(tempstr) && tempcnt > 0)) {
				temp[tempcnt] = 0;
				int logictype = (type == 0) ? LogicStatement.OPERATOR : LogicStatement.VARIABLE;
				LogicStatement newLogic = new LogicStatement(logictype, tempstr);
				newLogic.brace = bracecnt_to_register;
				tokens.add(newLogic);
				tempcnt = 0;
			}
			temp[tempcnt++] = s[i];
			bracecnt_to_register = bracecnt;
			type = newtype;
		}
		temp[tempcnt] = 0;
		String tempstr = new String(temp);
		tempstr = tempstr.substring(0, tempcnt);
		LogicStatement newLogic = new LogicStatement(LogicStatement.VARIABLE, tempstr);
		newLogic.brace = bracecnt_to_register;
		tokens.add(newLogic);
		for (int i = 0; i < tokens.size() - 1; i++) {
			LogicStatement L1 = tokens.get(i);
			LogicStatement L2 = tokens.get(i + 1);
			if (L1.variable.equals("<") && L2.variable.equals("=")) {
				L1.variable = "<=";
				tokens.remove(i + 1);
				i--;
			}
			if (L1.variable.equals("!") && L2.variable.equals("=")) {
				L1.variable = "!=";
				tokens.remove(i + 1);
				i--;
			}
			if (L1.variable.equals(">") && L2.variable.equals("=")) {
				L1.variable = ">=";
				tokens.remove(i + 1);
				i--;
			}
		}
		for (int i = 0; i < tokens.size(); i++) {
			if (specialchar.contains(tokens.get(i).variable))
				tokens.get(i).type = LogicStatement.OPERATOR;
			else
				tokens.get(i).type = LogicStatement.VARIABLE;
		}

		for (int i = 0; i < tokens.size(); i++) {
			LogicStatement var = tokens.get(i);
			if (var.type != LogicStatement.VARIABLE)
				continue;
			String varstr = var.variable;

			if (varstr.toLowerCase().equals("true") || varstr.toLowerCase().equals("false")) {
				var.type = LogicStatement.VALUE;
				var.valueType = LogicStatement.BOOLEAN;
				var.boolValue = Boolean.parseBoolean(varstr);
				continue;
			}

			char[] varstr_array = varstr.toCharArray();
			boolean isalphabet = false;
			boolean isreal = false;
			for (int j = 0; j < varstr.length(); j++) {
				if (Character.isLetter(varstr_array[j])) {
					isalphabet = true;
					break;
				}
				if (varstr_array[j] == '.') {
					isreal = true;
				}
				if (varstr_array[j] != '.' && !Character.isLetterOrDigit(varstr_array[j])) {
					isalphabet = true;
				}
			}
			if (!isalphabet) {
				if (isreal) {
					var.type = LogicStatement.VALUE;
					var.valueType = LogicStatement.REAL;
					var.realValue = Double.parseDouble(varstr);
				} else {
					var.type = LogicStatement.VALUE;
					var.valueType = LogicStatement.INTEGER;
					var.intValue = Integer.parseInt(varstr);
				}
			}
		}

		int TokenSize = tokens.size();

		if (TokenSize == 1) {
			this.type = tokens.get(0).type;
			this.variable = tokens.get(0).variable;
			this.boolValue = tokens.get(0).boolValue;
			this.realValue = tokens.get(0).realValue;
			this.intValue = tokens.get(0).intValue;
		}

		for (int I = 0; I < TokenSize - 1; I++) {
			if (tokens.size() == 1)
				break;
			int maxBrace = -1;
			int startPoint = 0;
			for (int i = 0; i < tokens.size(); i++) {
				if (maxBrace < tokens.get(i).brace)
					maxBrace = tokens.get(i).brace;
			}
			
			for (int i = 0; i < tokens.size(); i++) {
				if (tokens.get(i).brace != maxBrace)
					continue;
				startPoint = i;
				break;
			}

			if (tokens.get(startPoint + 1).brace != maxBrace) {
				tokens.get(startPoint).brace = maxBrace - 1;
				continue;
			}
			if (tokens.get(startPoint).type == LogicStatement.OPERATOR && tokens.get(startPoint).variable.equals("~")) {
				// ~ A
				tokens.get(startPoint).type = LogicStatement.NOT;
				tokens.get(startPoint).L1 = tokens.get(startPoint + 1);
				tokens.get(startPoint).brace = maxBrace - 1;
				tokens.remove(startPoint + 1);
				continue;
			}
			if (tokens.get(startPoint).type == LogicStatement.OPERATOR) {
				if(tokens.get(startPoint-1).variable.equals("~")) {
					startPoint--;
				} else {
					System.err.println("FATAL ERROR IN PROCESSING LOGIC STATEMENT!-1");
					System.err.println(tokens.toString());
					System.err.println("####################");
					System.exit(-1);
				}
			}
			if (tokens.get(startPoint + 1).type == LogicStatement.OPERATOR
					&& tokens.get(startPoint + 1).variable.equals("?")) {
				if (tokens.get(startPoint).type == LogicStatement.OPERATOR
						|| tokens.get(startPoint + 2).type == LogicStatement.OPERATOR
						|| tokens.get(startPoint + 3).type != LogicStatement.OPERATOR
						|| tokens.get(startPoint + 4).type == LogicStatement.OPERATOR) {
					//XML_load.console_println("FATAL ERROR IN PROCESSING LOGIC STATEMENT!-2");
					//XML_load.console_println(tokens.toString());
					System.err.println("####################");
					System.exit(-1);
					break;
				}
				tokens.get(startPoint + 1).type = LogicStatement.TERNARY;
				tokens.get(startPoint + 1).L1 = tokens.get(startPoint);
				tokens.get(startPoint + 1).L2 = tokens.get(startPoint + 2);
				tokens.get(startPoint + 1).L3 = tokens.get(startPoint + 4);
				tokens.get(startPoint + 1).brace = maxBrace - 1;
				tokens.remove(startPoint);
				tokens.remove(startPoint + 1);
				tokens.remove(startPoint + 1);
				tokens.remove(startPoint + 1);
				continue;
			}
			if (tokens.get(startPoint).type == LogicStatement.OPERATOR
					|| tokens.get(startPoint + 1).type != LogicStatement.OPERATOR
					|| tokens.get(startPoint + 2).type == LogicStatement.OPERATOR) {
				System.out.println("error statement: "+str);
				//XML_load.console_println("FATAL ERROR IN PROCESSING LOGIC STATEMENT!-3");
				//XML_load.console_println(tokens.toString());
				System.err.println("####################");
				System.exit(-1);
				break;
			}
			String operator = tokens.get(startPoint + 1).variable;
			int LogicType = 0;
			if (operator.equals("+"))
				LogicType = LogicStatement.PLUS;
			else if (operator.equals("*"))
				LogicType = LogicStatement.MULTIPLY;
			else if (operator.equals("/"))
				LogicType = LogicStatement.DIVIDE;
			else if (operator.equals("&"))
				LogicType = LogicStatement.AND;
			else if (operator.equals("|"))
				LogicType = LogicStatement.OR;
			else if (operator.equals(">="))
				LogicType = LogicStatement.GREATEREQUAL;
			else if (operator.equals("<="))
				LogicType = LogicStatement.LESSEQUAL;
			else if (operator.equals(">"))
				LogicType = LogicStatement.GREATER;
			else if (operator.equals("<"))
				LogicType = LogicStatement.LESS;
			else if (operator.equals("!="))
				LogicType = LogicStatement.NOTEQUAL;
			else if (operator.equals("="))
				LogicType = LogicStatement.EQUAL;
			else if (operator.equals("%"))
				LogicType = LogicStatement.MOD;
			else if (operator.equals("-"))
				LogicType = LogicStatement.MINUS;
			tokens.get(startPoint + 1).type = LogicType;
			tokens.get(startPoint + 1).L1 = tokens.get(startPoint);
			tokens.get(startPoint + 1).L2 = tokens.get(startPoint + 2);
			tokens.get(startPoint + 1).brace = maxBrace - 1;
			tokens.remove(startPoint);
			tokens.remove(startPoint + 1);
		}
		// System.out.println(tokens);
		LogicStatement Tok = tokens.get(0);
		this.type = Tok.type;
		this.L1 = Tok.L1;
		this.L2 = Tok.L2;
		this.L3 = Tok.L3;
		this.variable = Tok.variable;
		tempcnt = 0;
	}

	public double boolToDouble(boolean x) {
		if (x == false)
			return 0.0;
		return 1.0;
	}

	public double calculate() {
		if (this.type == LogicStatement.VALUE) {
			if (this.valueType == LogicStatement.INTEGER)
				return this.intValue;
			else if (this.valueType == LogicStatement.REAL)
				return this.realValue;
			else if (this.valueType == LogicStatement.BOOLEAN)
				return boolToDouble(this.boolValue);
		} else if (this.type == LogicStatement.VARIABLE) {
			double retvalue = 0;
			String var_str = this.variable.toString();
			StringTokenizer tok = new StringTokenizer(var_str, "[]");
			String prefix = tok.nextToken();
			if (prefix.equals("in") || prefix.equals("pre")) {
				String blockid = tok.nextToken();
				String varname = tok.nextToken();
				if (prefix.equals("in")) {
					return XML_load.GetFunctionBlockLocalVariable(varname, Long.parseLong(blockid)).logic.calculate();
				} else if (prefix.equals("pre")) {
					return XML_load.GetFunctionBlockPreVariable(varname, Long.parseLong(blockid)).logic.calculate();
				}
			}
			for (Element e : XML_load.invars) {
				if (e.invar.getExpression().equals(this.variable)) {
					String s = e.value;
					if (s == null) {
						//XML_load.console_println("Warning : variable " + this.variable
							//	+ " not found in test file. calculated as 0.");
						s = "0.0";
					}
					if (s.toLowerCase().equals("true"))
						retvalue = 1.0;
					else if (s.toLowerCase().equals("false"))
						retvalue = 0.0;
					else
						retvalue = Double.parseDouble(s);
					return retvalue;
				}
			}
		} else if (this.type == LogicStatement.EMBRACE) {
			return this.L1.calculate();
		} else if (this.type == LogicStatement.NOT) {
			double retvalue = this.L1.calculate();
			return (retvalue == 1.0) ? 0.0 : 1.0;
		} else if (this.type == LogicStatement.SUBTRACT) {
			return this.L1.calculate() - this.L2.calculate();
		} else if (this.type == LogicStatement.PLUS) {
			return this.L1.calculate() + this.L2.calculate();
		} else if (this.type == LogicStatement.MULTIPLY) {
			return this.L1.calculate() * this.L2.calculate();
		} else if (this.type == LogicStatement.DIVIDE) {
			if (this.L2.calculate() == 0.0)
				return 0.0;
			return this.L1.calculate() / this.L2.calculate();
		} else if (this.type == LogicStatement.OR) {
			return boolToDouble(doubleToBool(this.L1.calculate()) || doubleToBool(this.L2.calculate()));
		} else if (this.type == LogicStatement.AND) {
			return boolToDouble(doubleToBool(this.L1.calculate()) && doubleToBool(this.L2.calculate()));
		} else if (this.type == LogicStatement.GREATEREQUAL) {
			return boolToDouble(this.L1.calculate() >= this.L2.calculate());
		} else if (this.type == LogicStatement.LESSEQUAL) {
			return boolToDouble(this.L1.calculate() <= this.L2.calculate());
		} else if (this.type == LogicStatement.GREATER) {
			return boolToDouble(this.L1.calculate() > this.L2.calculate());
		} else if (this.type == LogicStatement.LESS) {
			return boolToDouble(this.L1.calculate() < this.L2.calculate());
		} else if (this.type == LogicStatement.NOTEQUAL) {
			return boolToDouble(this.L1.calculate() != this.L2.calculate());
		} else if (this.type == LogicStatement.EQUAL) {
			return boolToDouble(this.L1.calculate() == this.L2.calculate());
		} else if (this.type == LogicStatement.MOD) {
			return this.L1.calculate() % this.L2.calculate();
		} else if (this.type == LogicStatement.TERNARY) {
			return doubleToBool(this.L1.calculate()) ? this.L2.calculate() : this.L3.calculate();
		} else if (this.type == LogicStatement.MINUS) {
			return this.L1.calculate() - this.L2.calculate();
		} else {
			System.err.println("Non-supported function : LogicStatement.calculate()");
		}
		return 1.0;
	}

	public boolean doubleToBool(double x) {
		if (x == 0.0)
			return false;
		return true;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();

		if (this.type == LogicStatement.OPERATOR || this.type == LogicStatement.VARIABLE) {
			sb.append("(");
			sb.append(this.variable);
			sb.append(")");
			return sb.toString();
		} else if (this.type == LogicStatement.TERNARY) {
			sb.append("(");
			sb.append(this.L1);
			sb.append("?");
			sb.append(this.L2);
			sb.append(":");
			sb.append(this.L3);
			sb.append(")");
			return sb.toString();
		} else if (this.type == LogicStatement.NOT) {
			sb.append("(~");
			sb.append(this.L1);
			sb.append(")");
			return sb.toString();
		} else if (this.type == LogicStatement.EMBRACE) {
			sb.append("(");
			sb.append(this.L1);
			sb.append(")");
			return sb.toString();
		} else if (this.type == LogicStatement.VALUE) {
			String s = "";
			if (this.valueType == LogicStatement.BOOLEAN)
				s += this.boolValue;
			else if (this.valueType == LogicStatement.REAL)
				s += this.realValue;
			else
				s += this.intValue;
			sb.append("(");
			sb.append(s);
			sb.append(")");
			return sb.toString();
		} else {
			String op = " ";
			switch (this.type) {
			case LogicStatement.SUBTRACT:
				op += "- ";
				break;
			case LogicStatement.PLUS:
				op += "+ ";
				break;
			case LogicStatement.MULTIPLY:
				op += "* ";
				break;
			case LogicStatement.DIVIDE:
				op += "/ ";
				break;
			case LogicStatement.OR:
				op += "| ";
				break;
			case LogicStatement.AND:
				op += "& ";
				break;
			case LogicStatement.GREATEREQUAL:
				op += ">= ";
				break;
			case LogicStatement.LESSEQUAL:
				op += "<= ";
				break;
			case LogicStatement.GREATER:
				op += "> ";
				break;
			case LogicStatement.LESS:
				op += "< ";
				break;
			case LogicStatement.NOTEQUAL:
				op += "!= ";
				break;
			case LogicStatement.EQUAL:
				op += "= ";
				break;
			case LogicStatement.MOD:
				op += "% ";
				break;
			case LogicStatement.MINUS:
				op += "- ";
				break;
			}
			sb.append("(");
			sb.append(this.L1);
			sb.append(op);
			sb.append(this.L2);
			sb.append(")");
			return sb.toString();
		}
	}

	public String YicesString() {

		StringBuffer sb = new StringBuffer();
		// StringBuffer.append()
		// 2011.11.03. Donghwan

		if (this.type == LogicStatement.OPERATOR || this.type == LogicStatement.VARIABLE) {
			return this.variable;
		} else if (this.type == LogicStatement.TERNARY) {
			// Ternary (A ? B : C) => (if A B C)
			sb.append("(if ");
			sb.append(this.L1.YicesString());
			sb.append(" ");
			sb.append(this.L2.YicesString());
			sb.append(" ");
			sb.append(this.L3.YicesString());
			sb.append(")");
			return sb.toString();
		} else if (this.type == LogicStatement.NOT) {
			// (~A) => (not A)
			sb.append("(not ");
			sb.append(this.L1.YicesString());
			sb.append(")");
			return sb.toString();
		} else if (this.type == LogicStatement.EMBRACE) {
			return this.L1.YicesString();
		} else if (this.type == LogicStatement.VALUE) {
			if (this.valueType == LogicStatement.BOOLEAN)
				sb.append(this.boolValue);
			else if (this.valueType == LogicStatement.REAL)
				sb.append(this.realValue);
			else
				sb.append(this.intValue);
			return sb.toString();
		} else {
			String op = "";
			switch (this.type) {
			case LogicStatement.SUBTRACT:
				op = "-";
				break;
			case LogicStatement.PLUS:
				op = "+";
				break;
			case LogicStatement.MULTIPLY:
				op = "*";
				break;
			case LogicStatement.DIVIDE:
				op = "/";
				break;
			case LogicStatement.OR:
				op = "or";
				break;
			case LogicStatement.AND:
				op = "and";
				break;
			case LogicStatement.GREATEREQUAL:
				op = ">=";
				break;
			case LogicStatement.LESSEQUAL:
				op = "<=";
				break;
			case LogicStatement.GREATER:
				op = ">";
				break;
			case LogicStatement.LESS:
				op = "<";
				break;
			case LogicStatement.NOTEQUAL:
				op = "/=";
				break;
			case LogicStatement.EQUAL:
				op = "=";
				break;
			case LogicStatement.MOD:
				op = "%";
				break;
			case LogicStatement.MINUS:
				op = "-";
				break;
			}
			// ([operator] [L1] [L2])
			sb.append("(");
			sb.append(op);
			sb.append(" ");
			sb.append(this.L1.YicesString());
			sb.append(" ");
			sb.append(this.L2.YicesString());
			sb.append(")");
			return sb.toString();
		}
	}
	
	public String YicesString(int iter, boolean isblock) {

		StringBuffer sb = new StringBuffer();
		// StringBuffer.append()
		// 2011.11.03. Donghwan
		//System.out.println(XML_load.setIter);
		if (this.type == LogicStatement.OPERATOR || this.type == LogicStatement.VARIABLE) {
			if(XML_load.constants != null){
				for(int i = 0 ; i < XML_load.constants.length ; i++){
					if(this.variable.equals(XML_load.constants[i]) || this.variable.equals("SCAN_TIME"))
						return(this.variable);
				}
			}
			if(this.variable.startsWith("in_T")){ 
				/*
				if(iter > XML_load.setIter)
					return "0";
					*/
				if(isblock == true) {
					//System.out.println("--------"+XML_load.in_Ts.get(Long.parseLong(this.variable.substring(4, this.variable.length()))));
					String str = XML_load.in_Ts.get(Long.parseLong(this.variable.substring(4, this.variable.length())));
					if(XML_load.cycleCheck&&XML_load.mutant==true) {
						if(Arrays.asList(XML_load.inouts).contains(str)&&iter==XML_load.setIter+1) {
							return(str+"_t"+(iter+1));
						}
						if(Arrays.asList(XML_load.inputs).contains(str)&&!Arrays.asList(XML_load.inouts).contains(str)) {
							return (str+"_t"+(iter+1));
						}
						else{
							return ("M_"+str+"_t"+(iter+1));
						}
					}
					return(XML_load.in_Ts.get(Long.parseLong(this.variable.substring(4, this.variable.length())))+"_t"+(iter+1));
				}
				else
					if (iter == 0) {
						String str = XML_load.in_Ts.get(Long.parseLong(this.variable.substring(4, this.variable.length())));
						if(XML_load.cycleCheck&&XML_load.mutant==true) {
							if(Arrays.asList(XML_load.inouts).contains(str)&&iter==XML_load.setIter+1) {
								return(str);
							}
							if(Arrays.asList(XML_load.inputs).contains(str)&&!Arrays.asList(XML_load.inouts).contains(str)) {
								return (str);
							}
							else{
								return ("M_"+str);
							}
						}
						//System.out.println("--------"+XML_load.in_Ts.get(Long.parseLong(this.variable.substring(4, this.variable.length()))));
						return(XML_load.in_Ts.get(Long.parseLong(this.variable.substring(4, this.variable.length()))));
					}
					else {
						String str = XML_load.in_Ts.get(Long.parseLong(this.variable.substring(4, this.variable.length())));
						if(XML_load.cycleCheck&&XML_load.mutant==true) {
							if(Arrays.asList(XML_load.inouts).contains(str)&&iter==XML_load.setIter+1) {
								return(str+"_t"+(iter+1));
							}
							if(Arrays.asList(XML_load.inputs).contains(str)&&!Arrays.asList(XML_load.inouts).contains(str)) {
								return (str+"_t"+(iter+1));
							}
							else{
								return ("M_"+str+"_t"+(iter+1));
							}
						}
						//System.out.println("--------"+XML_load.in_Ts.get(Long.parseLong(this.variable.substring(4, this.variable.length()))));
						return(XML_load.in_Ts.get(Long.parseLong(this.variable.substring(4, this.variable.length())))+"_t"+(iter+1));
					}
			}
//			if(this.variable.startsWith("CV")){ 
//				if(isblock == true)
//					return(XML_load.CVs.get(Long.parseLong(this.variable.substring(2, this.variable.length())))+"_t"+(iter+1));
//				else
//					if (iter == 0)
//						return(XML_load.CVs.get(Long.parseLong(this.variable.substring(2, this.variable.length()))));
//					else
//						return(XML_load.CVs.get(Long.parseLong(this.variable.substring(2, this.variable.length())))+"_t"+(iter));
//			}
			if(this.variable.endsWith("_pre")){
				/*
				if(iter > XML_load.setIter)
					return "false";
				*/
				String str = this.variable.substring(0, this.variable.length()-4);
				if(XML_load.cycleCheck&&XML_load.mutant==true) {
					if(Arrays.asList(XML_load.inouts).contains(str)&&iter==XML_load.setIter+1) {
						return(str+"_t"+(iter+1));
					}
					if(Arrays.asList(XML_load.inputs).contains(str)&&!Arrays.asList(XML_load.inouts).contains(str)) {
						return (str+"_t"+(iter+1));
					}
					else{
						return ("M_"+str+"_t"+(iter+1));
					}
				}
				//System.out.println("--------"+this.variable.substring(0, this.variable.length()-4));
				return this.variable.substring(0, this.variable.length()-4)+"_t"+(iter+1);
			}
			/*
			if(this.variable.startsWith("pre_CV") && (iter > XML_load.setIter))
				return "0";
			if(this.variable.startsWith("pre_") && (iter > XML_load.setIter))
				return "false";
				*/
			if(this.variable.startsWith("pre_CV")) {
				/*
				if(iter > XML_load.setIter)
					return "0";
					*/
				String str = XML_load.pre_CVs.get(Long.parseLong(this.variable.substring(6, this.variable.length())));
				if(XML_load.cycleCheck&&XML_load.mutant==true) {
					if(Arrays.asList(XML_load.inouts).contains(str)&&iter==XML_load.setIter+1) {
						return(str+"_t"+(iter+1));
					}
					if(Arrays.asList(XML_load.inputs).contains(str)&&!Arrays.asList(XML_load.inouts).contains(str)) {
						return (str+"_t"+(iter+1));
					}
					else{
						return ("M_"+str+"_t"+(iter+1));
					}
				}
				
				return(XML_load.pre_CVs.get(Long.parseLong(this.variable.substring(6, this.variable.length())))+"_t"+(iter+1));
			}
			if(this.variable.startsWith("pre_CLK")) {
				/*
				if(iter > XML_load.setIter)
					return "false";
					*/
				String str = XML_load.pre_CLKs.get(Long.parseLong(this.variable.substring(7, this.variable.length())));
				if(XML_load.cycleCheck&&XML_load.mutant==true) {
					if(Arrays.asList(XML_load.inouts).contains(str)&&iter==XML_load.setIter+1) {
						return(str+"_t"+(iter+1));
					}
					if(Arrays.asList(XML_load.inputs).contains(str)&&!Arrays.asList(XML_load.inouts).contains(str)) {
						return (str+"_t"+(iter+1));
					}
					else{
						return ("M_"+str+"_t"+(iter+1));
					}
				}
				
				return(XML_load.pre_CLKs.get(Long.parseLong(this.variable.substring(7, this.variable.length())))+"_t"+(iter+1));
			}
			if(this.variable.startsWith("pre_Q1")) {
				/*
				if(iter > XML_load.setIter)
					return "false";
					*/
				String str = XML_load.pre_Q1s.get(Long.parseLong(this.variable.substring(6, this.variable.length())));
				if(XML_load.cycleCheck&&XML_load.mutant==true) {
					if(Arrays.asList(XML_load.inouts).contains(str)&&iter==XML_load.setIter+1) {
						return(str+"_t"+(iter+1));
					}
					if(Arrays.asList(XML_load.inputs).contains(str)&&!Arrays.asList(XML_load.inouts).contains(str)) {
						return (str+"_t"+(iter+1));
					}
					else{
						return ("M_"+str+"_t"+(iter+1));
					}
				}
				
				return(XML_load.pre_Q1s.get(Long.parseLong(this.variable.substring(6, this.variable.length())))+"_t"+(iter+1));
			}
			if(this.variable.startsWith("pre_IN")) {
				/*
				if(iter > XML_load.setIter)
					return "false";
					*/
				String str = XML_load.pre_INs.get(Long.parseLong(this.variable.substring(6, this.variable.length())));
				if(XML_load.cycleCheck&&XML_load.mutant==true) {
					if(Arrays.asList(XML_load.inouts).contains(str)&&iter==XML_load.setIter+1) {
						return(str+"_t"+(iter+1));
					}
					if(Arrays.asList(XML_load.inputs).contains(str)&&!Arrays.asList(XML_load.inouts).contains(str)) {
						return (str+"_t"+(iter+1));
					}
					else{
						return ("M_"+str+"_t"+(iter+1));
					}
				}
				
				return(XML_load.pre_INs.get(Long.parseLong(this.variable.substring(6, this.variable.length())))+"_t"+(iter+1));
			}
			if (iter != 0) {
				
				String str = this.variable;
				if(XML_load.cycleCheck&&XML_load.mutant==true) {
					if(Arrays.asList(XML_load.inouts).contains(str)&&iter==XML_load.setIter+1) {
						return(str+"_t"+iter);
					}
					if(Arrays.asList(XML_load.inputs).contains(str)&&!Arrays.asList(XML_load.inouts).contains(str)) {
						return (str+"_t"+iter);
					}
					else{
						return ("M_"+str+"_t"+iter);
					}
				}
				//System.out.println("--------"+this.variable);
				return (this.variable+"_t"+iter);
			}
			else {
				String str = this.variable;
				if(XML_load.cycleCheck&&XML_load.mutant==true) {
					if(Arrays.asList(XML_load.inouts).contains(str)&&iter==XML_load.setIter+1) {
						return(str);
					}
					if(Arrays.asList(XML_load.inputs).contains(str)&&!Arrays.asList(XML_load.inouts).contains(str)) {
						return (str);
					}
					else{
						return ("M_"+str);
					}
				}
				return (this.variable);
			}
			
		} else if (this.type == LogicStatement.TERNARY) {
			// Ternary (A ? B : C) => (if A B C)
			sb.append("(if ");
			sb.append(this.L1.YicesString(iter,isblock));
			sb.append(" ");
			sb.append(this.L2.YicesString(iter,isblock));
			sb.append(" ");
			sb.append(this.L3.YicesString(iter,isblock));
			sb.append(")");
			return sb.toString();
		} else if (this.type == LogicStatement.NOT) {
			// (~A) => (not A)
			sb.append("(not ");
			sb.append(this.L1.YicesString(iter,isblock));
			sb.append(")");
			return sb.toString();
		} else if (this.type == LogicStatement.EMBRACE) {
			return this.L1.YicesString(iter,isblock);
		} else if (this.type == LogicStatement.VALUE) {
			if (this.valueType == LogicStatement.BOOLEAN)
				sb.append(this.boolValue);
			else if (this.valueType == LogicStatement.REAL)
				sb.append(this.realValue);
			else
				sb.append(this.intValue);
			return sb.toString();
		} else {
			String op = "";
			switch (this.type) {
			case LogicStatement.SUBTRACT:
				op = "-";
				break;
			case LogicStatement.PLUS:
				op = "+";
				break;
			case LogicStatement.MULTIPLY:
				op = "*";
				break;
			case LogicStatement.DIVIDE:
				op = "/";
				break;
			case LogicStatement.OR:
				op = "or";
				break;
			case LogicStatement.AND:
				op = "and";
				break;
			case LogicStatement.GREATEREQUAL:
				op = ">=";
				break;
			case LogicStatement.LESSEQUAL:
				op = "<=";
				break;
			case LogicStatement.GREATER:
				op = ">";
				break;
			case LogicStatement.LESS:
				op = "<";
				break;
			case LogicStatement.NOTEQUAL:
				op = "/=";
				break;
			case LogicStatement.EQUAL:
				op = "=";
				break;
			case LogicStatement.MOD:
				op = "mod";
				break;
			case LogicStatement.MINUS:
				op = "-";
				break;
			}
			// ([operator] [L1] [L2])
			sb.append("(");
			sb.append(op);
			sb.append(" ");
			sb.append(this.L1.YicesString(iter,isblock));
			sb.append(" ");
			sb.append(this.L2.YicesString(iter,isblock));
			sb.append(")");
			return sb.toString();
		}
	}
}
