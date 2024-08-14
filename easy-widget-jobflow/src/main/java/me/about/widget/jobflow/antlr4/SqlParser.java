package me.about.widget.jobflow.antlr4;

import me.about.widget.jobflow.entity.JobFlowDef;
import me.about.widget.jobflow.exception.MalformedSyntaxException;
import me.about.widget.jobflow.sql.antlr.JobFlowDSLLexer;
import me.about.widget.jobflow.sql.antlr.JobFlowDSLParser;
import org.antlr.v4.runtime.*;

import java.util.List;


public class SqlParser {
	private JobFlowDSLLexer createLexer(final CharStream stream) {
		JobFlowDSLLexer lexer = new JobFlowDSLLexer(stream);
		lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
		lexer.addErrorListener(new SqlErrorListener());
		return lexer;
	}

	private JobFlowDSLParser createParser(final CommonTokenStream tokens) {
		JobFlowDSLParser parser = new JobFlowDSLParser(tokens);
		parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
		parser.addErrorListener(new SqlErrorListener());
		return parser;
	}

	public List<JobFlowDef> parse(String input) throws MalformedSyntaxException {
		CharStream cs = CharStreams.fromString(input);
		JobFlowDSLLexer lexer = createLexer(cs);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		JobFlowDSLParser parser = createParser(tokens);
		SqlVisitor visitor = new SqlVisitor();
		visitor.visit(parser.jobFlow());
		return visitor.getJobFlowDefs();
	}

	public static SqlParser builder() {
		return new SqlParser();
	}
}

class SqlErrorListener extends BaseErrorListener {

	@Override
	public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
			final int charPositionInLine, final String msg, final RecognitionException e) {
		throw new MalformedSyntaxException(msg);
	}
}