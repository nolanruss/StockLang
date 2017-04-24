import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Error;

public class SLVisitor extends StockLangBaseVisitor<Object> {
	/** ID '=' expr NEWLINE */
	/** "memory" for the program; variable/value pairs go here */
	Map<String, Object> memory = new HashMap<String, Object>();
	Map<String, String> typeMap = new HashMap<String, String>();
	
	@Override
	public Object visitExpr_aop_exprAsn(StockLangParser.Expr_aop_exprAsnContext ctx) {
		System.out.println("visitExpr_aop_exprAsn");
		String id = ctx.expr().getText(); // id is left hand side of '='
		Object value = null;
		String stockChecker = id.substring(0, 1);
		String dotChecker = ctx.expr_or_assign().getText();
		if(stockChecker.equals("$")){
			/*code injection*/
	//		IR.addCode("/* ");
//			value = visit(ctx.expr_or_assign()); // value is right hand side of '='
			String[] stockID;
			stockID = id.split("\\.");
			String[] dotID;
			if(!memory.containsKey(stockID[0])){
				System.out.println("undeclared variable " + id);
				System.exit(-1);
			}
			
			String[] tempArray = (String[]) memory.get(stockID[0]);
			if(stockID[1].equals("symbol")){
				/*code injection*/
				IR.addCode("IRstock.setSymbol( "+stockID[0]+", ");
				value = visit(ctx.expr_or_assign()); // value is right hand side of '='
				/*code injection*/
				IR.addCode(");");
				memory.put(stockID[0], new String[] {value.toString(), tempArray[1], tempArray[2], tempArray[3]});
				
			} else if(stockID[1].equals("companyName")){
				/*code injection*/
				IR.addCode("IRstock.setCompanyName( "+stockID[0]+", ");
				value = visit(ctx.expr_or_assign()); // value is right hand side of '='
				/*code injection*/
				IR.addCode(");");
				memory.put(stockID[0], new String[] {tempArray[0], value.toString(),  tempArray[2], tempArray[3]});
				
			} else if(stockID[1].equals("currentPrice")){/*code injection*/
				IR.addCode("IRstock.setCurrentPrice( "+stockID[0]+", ");
				value = visit(ctx.expr_or_assign()); // value is right hand side of '='
				/*code injection*/
				IR.addCode(");");
				if(!value.toString().matches("[-+]?\\d*\\.?\\d+")){
					System.out.println(id +" must be numeric");
					System.exit(-1);
				}
				memory.put(stockID[0], new String[] {tempArray[0], tempArray[1], value.toString(),   tempArray[3]});
				
			} else if(stockID[1].equals("yearHigh")){
				/*code injection*/
				IR.addCode("IRstock.setYearHigh( "+stockID[0]+", ");
				value = visit(ctx.expr_or_assign()); // value is right hand side of '='
				/*code injection*/
				IR.addCode(");");
				if(!value.toString().matches("[-+]?\\d*\\.?\\d+")){
					System.out.println(id +" must be numeric");
					System.exit(-1);
				}
				memory.put(stockID[0], new String[] {tempArray[0], tempArray[1], tempArray[2], value.toString()});
				
			} 
			
		} else { //not a stock
			if (!memory.containsKey(id)) {
				System.out.println("undeclared variable " + id);
				System.exit(-1);
			}
			
			/* Code Injection */
			IR.addCode( id + " = ");
			value = visit(ctx.expr_or_assign()); // value is right hand side of '='
			memory.put( id, value); // store it in our memory
			
			/* Code Injection */
			//IR.addCode( id + " = ");
			//value = visit(ctx.expr_or_assign()); //print out values when visit each node
			IR.addCode(";" );
		}
		return value;
	}
	
	@Override
	public Object visitExpr_only(StockLangParser.Expr_onlyContext ctx){
		System.out.println("visitExpr_only");
		Object value = visit(ctx.expr());
		
		return value;
	}
	
	@Override
	public Object visitDecl_id(StockLangParser.Decl_idContext ctx){
		System.out.println("visitDecl_id");
		String type = ctx.TYPE().getText();
		String id = ctx.ID().getText();
		Object obj;
		
		if(type.equals("String")){
			obj = new String("");
		} else if(type.equals("double")){
			obj = new Double(0.0);
		} else if(type.equals("int")){
			obj = new Integer(0);
		} else {
			obj = new Error("error");
			System.out.println("Unrecognized type: " +type);
			System.exit(-1);
			
		}
		typeMap.put(id, type);
		memory.put(id, obj);
		
		/*code injection*/
		IR.addCode(type +" " +id +";");
		return obj;
	}
	
	@Override
	public Object visitDecl_stock(StockLangParser.Decl_stockContext ctx){
		System.out.println("visitDecl_stock");
		String stock = ctx.STOCK().getText();
		Object obj;
		String subLetter;
		subLetter = stock.substring(0, 1);
		if(!subLetter.equals("$")){
				System.out.println("Variables of type Stock must start with '$'");
				System.exit(-1);
		}
		obj = new String[4]; //placeholder
		typeMap.put(stock, "Stock");
		memory.put(stock, obj);
		/*code injection*/
		IR.addCode("String "+stock+"= "+"\"" +stock +"\"" +";"+" IRstock.declStock( " +stock +" );");
		return obj;
	}
	
	@Override 
	public Double visitExponent(StockLangParser.ExponentContext ctx){ 
		System.out.println("visitExponent");
		/*code injection*/
		IR.addCode("Math.pow( ");
		Object leftObj = visit(ctx.expr(0));
		/*code injection*/
		IR.addCode(" , ");
		Object rightObj = visit(ctx.expr(1));
		/*code injection*/
		IR.addCode(" )");
		if(leftObj.getClass() != Double.class || rightObj.getClass() != Double.class){
			System.out.println("Exponent Must be numeric");
			System.exit(-1);
		}
		Double leftValue = Double.valueOf(leftObj.toString()); // value is left hand side
		Double rightValue = Double.valueOf(rightObj.toString()); // value is right hand side
		return (double)Math.pow(leftValue, rightValue); //return leftValue ^ rightValue
	}
	
	@Override 
	public Double visitPos_neg(StockLangParser.Pos_negContext ctx){
		System.out.println("visitPos_neg");
		if(visit(ctx.expr()).getClass() != Double.class){
			System.out.println("Pos_neg Must have Type Double");
			System.exit(-1);
		}
		Double value = Double.valueOf(ctx.expr().getText());
		if(ctx.op.getType() == StockLangParser.ADD){
			/*code injection*/
			IR.addCode("Math.abs(" +ctx.expr().getText()+")" );
			return Math.abs(value);
		} else {
			/*code injection*/
			IR.addCode(ctx.expr().getText() +" *= -1");
			return value *= -1;
		}
	}
	
	@Override
	public Double visitMul_div(StockLangParser.Mul_divContext ctx){
		System.out.println("visitMul_div");
		Object leftObj = visit(ctx.expr(0));
		if(ctx.op.getType() == StockLangParser.MUL){
			/*code injection*/
			IR.addCode(" * ");
		} else {
			/*code injection*/
			IR.addCode(" / ");
		}
		Object rightObj = visit(ctx.expr(1));
		if(leftObj.getClass() != Double.class || rightObj.getClass() != Double.class){
			System.out.println("Mul_div Must have Type Double");
			System.exit(-1);
		}
		Double leftValue = Double.valueOf(leftObj.toString()); // value is left hand side
		Double rightValue = Double.valueOf(rightObj.toString()); // value is right hand side
		
		if(ctx.op.getType() == StockLangParser.MUL){
			return leftValue * rightValue;
		} else {
			return leftValue / rightValue;
		}
	}
	
	@Override
	public Double visitAdd_sub(StockLangParser.Add_subContext ctx){
		System.out.println("visitAdd_sub");
		Object leftObj = visit(ctx.expr(0));
		if(ctx.op.getType() == StockLangParser.ADD){
			/*code injection*/
			IR.addCode(" + ");
		} else {
			/*code injection*/
			IR.addCode(" - ");
		}
		Object rightObj = visit(ctx.expr(1));
		if(leftObj.getClass() != Double.class || rightObj.getClass() != Double.class){
			System.out.println("Add_sub Must have Type Double");
			System.exit(-1);
		}
		Double leftValue = Double.valueOf(leftObj.toString()); // value is left hand side
		Double rightValue = Double.valueOf(rightObj.toString()); // value is right hand side
		if(ctx.op.getType() == StockLangParser.ADD){
			return leftValue + rightValue;
		} else {
			return leftValue - rightValue;
		}
	}
	
	@Override
	public Boolean visitBool_op(StockLangParser.Bool_opContext ctx){
		System.out.println("visitBool_op");
		Object leftValue = visit(ctx.expr(0));
		/*code injection*/
		IR.addCode("/*");
		Object rightValue = visit(ctx.expr(1));
		/*code injection*/
		IR.addCode("*/");
		if(leftValue.getClass() != rightValue.getClass()){
			System.out.println("Cannot compare objects of diffrent type");
			System.exit(-1);
		}
		
		if(leftValue.getClass() == String.class){
			if(!(ctx.op.getText().toString().equals("==") || ctx.op.getText().toString().equals("!="))){
				System.out.println("Compairison not compatable with String");
				System.exit(-1);
			}
			if(ctx.op.getText().toString().equals("==")){
				/*code injection*/
				IR.addCode(".equals(");
				if(leftValue.toString().equals(rightValue.toString())){
					System.out.println("true");
					visit(ctx.expr(1));
					/*code injection*/
					IR.addCode(")");
					return true;
				} else {
					System.out.println("false");
					visit(ctx.expr(1));
					/*code injection*/
					IR.addCode(")");
					return false;
				}
			} else {
				/*code injection*/
				IR.addCode(".equals(");
				if(!leftValue.toString().equals(rightValue.toString())){
					System.out.println("true");
					visit(ctx.expr(1));
					/*code injection*/
					IR.addCode(")");
					return true;
				} else {
					System.out.println("false");
					visit(ctx.expr(1));
					/*code injection*/
					IR.addCode(")");
					return false;
				}
			}
		}
		
		Double leftValueDouble = Double.valueOf(leftValue.toString());
		Double rightValueDouble = Double.valueOf(rightValue.toString());
		if(ctx.op.getText().toString().equals(">")){
			/*code injection*/
			IR.addCode(" > ");
			if(leftValueDouble > rightValueDouble){
				System.out.println("true");
				visit(ctx.expr(1));
				return true;
			} else {
				System.out.println("false");
				visit(ctx.expr(1));
				return false;
			}
		} else if(ctx.op.getText().toString().equals(">=")){
			/*code injection*/
			IR.addCode(" >= ");
			if(leftValueDouble >= rightValueDouble){
				System.out.println("true");
				visit(ctx.expr(1));
				return true;
			} else {
				System.out.println("false");
				visit(ctx.expr(1));
				return false;
			}
		} else if(ctx.op.getText().toString().equals("<")){
			/*code injection*/
			IR.addCode(" < ");
			if(leftValueDouble < rightValueDouble){
				System.out.println("true");
				visit(ctx.expr(1));
				return true;
			} else {
				System.out.println("false");
				visit(ctx.expr(1));
				return false;
			}
		} else if(ctx.op.getText().toString().equals("<=")){
			/*code injection*/
			IR.addCode(" <= ");
			if(leftValueDouble <= rightValueDouble){
				System.out.println("true");
				visit(ctx.expr(1));
				return true;
			} else {
				System.out.println("false");
				visit(ctx.expr(1));
				return false;
			}
		} else if(ctx.op.getText().toString().equals("==")){
			/*code injection*/
			IR.addCode(" == ");
			if(leftValueDouble.equals(rightValueDouble)){
				System.out.println("true");
				visit(ctx.expr(1));
				return true;
			} else {
				System.out.println("false");
				visit(ctx.expr(1));
				return false;
			}
		} else if(ctx.op.getText().toString().equals("!=")){
			/*code injection*/
			IR.addCode(" != ");
			if(!leftValueDouble.equals(rightValueDouble)){
				System.out.println("true");
				visit(ctx.expr(1));
				return true;
			} else {
				System.out.println("false");
				visit(ctx.expr(1));
				return false;
			}
		}
		visit(ctx.expr(1));
		return false;
	}
	
	@Override
	public Double visitNot(StockLangParser.NotContext ctx){
		System.out.println("visitNot");
		return (double)0.0;
	}
	
	
	@Override
	public Object visitAnd(StockLangParser.AndContext ctx){
		System.out.println("visitAnd");
		Object leftValue = visit(ctx.expr(0));
		/*code injection*/
		IR.addCode(" && ");
		Object rightValue = visit(ctx.expr(1));
		if(leftValue.getClass() != Boolean.class || rightValue.getClass() != Boolean.class){
			System.out.println("Expressions using & must evaluate to Boolean");
			System.exit(-1);
		}
		if((Boolean) leftValue && (Boolean) rightValue){
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public Object visitOr(StockLangParser.OrContext ctx){
		System.out.println("visitOr");
		Object leftValue = visit(ctx.expr(0));
		/*code injection*/
		IR.addCode(" || ");
		Object rightValue = visit(ctx.expr(1));
		if(leftValue.getClass() != Boolean.class || rightValue.getClass() != Boolean.class){
			System.out.println("Expressions using || must evaluate to Boolean");
			System.exit(-1);
		}
		if((Boolean) leftValue || (Boolean) rightValue){
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public Boolean visitBoolean(StockLangParser.BooleanContext ctx){
		System.out.println("visitBoolean");
		Boolean value;
		if(ctx.BOOLEAN().getText().toString().equals("TRUE")){
			value = true;
		} else {
			value = false;
		}
		return value;
	}
	
	@Override
	public Object visitIf(StockLangParser.IfContext ctx){
		System.out.println("visitIf");
		/*code injection*/
		IR.addCode("if( ");
		Object value = visit(ctx.expr());
		if(value.getClass() == Boolean.class){
			Boolean valueConvert = (Boolean) value;
			/*code injection*/
			IR.addCode(" ){" );
			Object trueValue = visit(ctx.expr_or_assign()); 
			/*code injection*/
			IR.addCode( "}" );
			if(valueConvert){
				return trueValue;
			}
		} else {
			System.out.println("Condition in if statement must be Boolean expression");
			System.exit(-1);
		}
		return (double)0.0;
	}
	
	@Override
	public Object visitIf_else(StockLangParser.If_elseContext ctx){
		System.out.println("visitIf_else");
		/*code injection*/
		IR.addCode("if( ");
		Object value = visit(ctx.expr());
		if(value.getClass() == Boolean.class){
			Boolean valueConvert = (Boolean) value;
			/*code injection*/
			IR.addCode("){" );
			Object trueValue = visit(ctx.expr_or_assign(0)); 
			/*code injection*/
			IR.addCode(" } else { " );
			Object falseValue = visit(ctx.expr_or_assign(1));
			/*code injection*/
			IR.addCode(" }" );
			if(valueConvert){
				return trueValue;
			} else {
				return falseValue;
			}
		} else {
			System.out.println("Condition in if statement must be Boolean expression");
			System.exit(-1);
		}
		return (double)0.0;
	}
	
	@Override
	public Object visitWhile(StockLangParser.WhileContext ctx){
        System.out.println("visitWhile");
        Object visitReturn;
        /*code injection*/
		IR.addCode("while( ");
        visitReturn = visit(ctx.expr());
        if(visitReturn.getClass() == Boolean.class ) {
        	/*code injection*/
    		IR.addCode("){" );
			visitReturn = visit(ctx.expr_or_assign()); 
			/*code injection*/
			IR.addCode("}" );
        } else {
            System.out.println("While loop requires a boolean condition");
            System.exit(-1);
        }
        return visitReturn;
	}
	
	@Override
	public Object visitParens(StockLangParser.ParensContext ctx){
		System.out.println("visitParens");
		return visit(ctx.expr());
	}

	@Override
	public Double visitDouble(StockLangParser.DoubleContext ctx){
		System.out.println("visitDouble");
		
		/* Code Injection */
		IR.addCode( ctx.DOUBLE().getText());
		return Double.valueOf(ctx.DOUBLE().getText());
	}
	
	@Override
	public Double visitInt(StockLangParser.IntContext ctx){
		System.out.println("visitInt");
		
		/* Code Injection */
		IR.addCode( ctx.INT().getText());
		return Double.valueOf(ctx.INT().getText());
	}
	
	@Override
	public String visitString(StockLangParser.StringContext ctx){
		System.out.println("visitString");
		
		/* Code Injection */
		IR.addCode( ctx.STRING().getText());
		return ctx.STRING().getText().toString();
	}
	
	@Override
	public Object visitStock(StockLangParser.StockContext ctx){
		System.out.println("visitStock");
		if(!ctx.STOCK().getText().toString().contains(".")){
			System.out.println(ctx.STOCK().getText() +" must be accompanied with '.symbol' , '.companyName' , '.currentPrice' , or '.yearHigh'");
			System.exit(-1);
		}
		String[] split = ctx.STOCK().getText().toString().split("\\.");
		if(!memory.containsKey(split[0])){
			System.out.println("Stock " +split[0]+" has not been declared yet");
			System.exit(-1);
		}
		String[] stockPrinter = (String[]) memory.get(split[0]);
		if(split[1].equals("symbol")){
			if(stockPrinter[0] == null){
				System.out.println(ctx.STOCK().getText().toString() +" has not been declared yet");
				System.exit(-1);
			}
			/*code injection*/
			IR.addCode("IRstock.getSymbol( "+split[0] +" )");
			return stockPrinter[0].toString();
		} else if(split[1].equals("companyName")){
			if(stockPrinter[1] == null){
				System.out.println(ctx.STOCK().getText().toString() +" has not been declared yet");
				System.exit(-1);
			}
			/*code injection*/
			IR.addCode("IRstock.getCompanyName( "+split[0] +" )");
			return stockPrinter[1].toString();
		} else if(split[1].equals("currentPrice")){
			if(stockPrinter[2] == null){
				System.out.println(ctx.STOCK().getText().toString() +" has not been declared yet");
				System.exit(-1);
			}
			/*code injection*/
			IR.addCode("IRstock.getCurrentPrice( "+split[0] +" )");
			return Double.valueOf(stockPrinter[2]);
		} else if(split[1].equals("yearHigh")){
			if(stockPrinter[3] == null){
				System.out.println(ctx.STOCK().getText().toString() +" has not been declared yet");
				System.exit(-1);
			}
			/*code injection*/
			IR.addCode("IRstock.getYearHigh( "+split[0] +" )");
			return Double.valueOf(stockPrinter[3]);
		}
		return ctx.STOCK().getText().toString();
	}
	
	@Override
	public Object visitId(StockLangParser.IdContext ctx){
		System.out.println("visitId");
		String ID = ctx.ID().getText().toString();
		if(!memory.containsKey(ID)){
			System.out.println("Undeclared variable: " +ID);
			System.exit(-1);
		}
		
		/* Code Injection */
		IR.addCode( ID);
		
		Object value = memory.get(ID);
		String typeValue = typeMap.get(ctx.ID().getText().toString());
		
		//create one for every 'type'
		if(value.getClass() == Double.class){
			return Double.valueOf(memory.get(ctx.ID().getText()).toString());
		} else if(value.getClass() == String.class){
			return memory.get(ctx.ID().getText()).toString();
		} else if(typeValue.equals("Stock")){
			String[] split = ID.split("\\.");
			String[] stockPrinter = (String[]) memory.get(split[0]);
			if(split[1].equals("symbol")){
				return stockPrinter[0];
			} else if(split[1].equals("companyName")){
				return stockPrinter[1];
			} else if(split[1].equals("currentPrice")){
				return Double.valueOf(stockPrinter[2]);
			} else if(split[1].equals("yearHigh")){
				return Double.valueOf(stockPrinter[3]);
			}
		}//continue else if for more id types
		
		return 0;
	}
		
	@Override
	public Object visitMult_expr_assn(StockLangParser.Mult_expr_assnContext ctx){
		System.out.println("visitMult_expr_assn");
		Object value;
		List valueList = ctx.expr_or_assign();
		for(int i = 0; i < valueList.size(); i++){
			value = visit(ctx.expr_or_assign(i));
		}
		return (double)0.0;
	}
	
	/*Predefined functions here */
	@Override
	public Object visitPrint_fxn(StockLangParser.Print_fxnContext ctx){
		System.out.println("visitPrint_fxn");
		/*code injection*/
		IR.addCode("System.out.println( ");
		String value = visit(ctx.expr()).toString();
		System.out.println(value);
		/*code injection*/
		IR.addCode(");");
		return (double)0.0;
	}
	@Override
	public Object visitPrint_stock_fxn(StockLangParser.Print_stock_fxnContext ctx){
		System.out.println("visitPrint_stock_fxn");
		if(memory.containsKey(ctx.STOCK().toString())){
			Object obj = memory.get(ctx.STOCK().toString());
			String[] value = (String[])obj;
			Boolean errorFlag = false;
			if(value[0] == null){
				System.out.println(ctx.STOCK().getText().toString() +".symbol has not been declared yet");
				errorFlag = true;
			}	
			if(value[1] == null){
				System.out.println(ctx.STOCK().getText().toString() +".companyName has not been declared yet");
				errorFlag = true;
			}
			if(value[2] == null){
				System.out.println(ctx.STOCK().getText().toString() +".currentPrice has not been declared yet");
				errorFlag = true;
			}	
			if(value[3] == null){
				System.out.println(ctx.STOCK().getText().toString() +".yearHigh has not been declared yet");
				errorFlag = true;
			}
			if(errorFlag){
				System.exit(-1);
			}
			System.out.println("Symbol: "+value[0]);
			/*code injection*/
			IR.addCode("System.out.println( IRstock.getSymbol("+ctx.STOCK().getText().toString()+"));");
			System.out.println("CompanyName: "+value[1]);
			/*code injection*/
			IR.addCode("System.out.println( IRstock.getCompanyName("+ctx.STOCK().getText().toString()+"));");
			System.out.println("CurrentPrice: "+value[2]);
			/*code injection*/
			IR.addCode("System.out.println( IRstock.getCurrentPrice("+ctx.STOCK().getText().toString()+"));");
			System.out.println("yearHigh: "+value[3]);
			/*code injection*/
			IR.addCode("System.out.println( IRstock.getYearHigh("+ctx.STOCK().getText().toString()+"));");
		} else {
			System.out.println("Stock: "+ctx.STOCK().getText()+" has not been declared");
			System.exit(-1);
		}
		
		return (double)0.0;
	}
}
