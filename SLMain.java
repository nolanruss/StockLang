/**
 * Created by: Nolan Thompson and Alex Ing on 02/26/17
 *
 */
import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SLMain {
	public static void main(String[] args) throws Exception {
		
		for(int i = 0; i < args.length; i++){
			System.out.println(args[i]);
		}
		String inputFile = args[0];
		String className = args[1];
		
		IR.init( className );
		IR.setup();
		
		InputStream is = System.in;
		if (inputFile != null) {
			is = new FileInputStream(inputFile);
		}

		ANTLRInputStream input = new ANTLRInputStream(is);
		StockLangLexer lexer = new StockLangLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		StockLangParser parser = new StockLangParser(tokens);
		ParseTree tree = parser.prog();
		SLVisitor visitor = new SLVisitor();
		visitor.visit(tree);
		
		IR.tearDown();
		IR.dump();
		//System.out.println( tree.toStringTree(parser) );
	}
}
