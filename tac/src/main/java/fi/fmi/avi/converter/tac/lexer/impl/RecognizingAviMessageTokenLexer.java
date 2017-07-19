package fi.fmi.avi.converter.tac.lexer.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.LexemeVisitor;

/**
 * Created by rinne on 01/02/17.
 */
public class RecognizingAviMessageTokenLexer implements LexemeVisitor {

    public enum RelationalOperator {
        LESS_THAN("M"), MORE_THAN("P");

        private final String code;

        RelationalOperator(final String code) {
            this.code = code;
        }

        public static RelationalOperator forCode(final String code) {
            for (RelationalOperator w : values()) {
                if (w.code.equals(code)) {
                    return w;
                }
            }
            return null;
        }
    }

    public enum TendencyOperator {
        UPWARD("U"), DOWNWARD("D"), NO_CHANGE("N");

        private final String code;

        TendencyOperator(final String code) {
            this.code = code;
        }

        public static TendencyOperator forCode(final String code) {
            for (TendencyOperator w : values()) {
                if (w.code.equals(code)) {
                    return w;
                }
            }
            return null;
        }
    }

    private List<PrioritizedLexemeVisitor> visitors = new ArrayList<PrioritizedLexemeVisitor>();

    public void teach(PrioritizedLexemeVisitor lexer) {
        this.visitors.add(lexer);
        Collections.sort(this.visitors);
    }

    @Override
    public void visit(final Lexeme token, final ConversionHints hints) {
        if (visitors != null) {
            for (LexemeVisitor v : visitors) {
            	if (token.getIdentificationCertainty() < 1.0) {
            		token.accept(v, hints);
            	} else {
            		break;
            	}
            }
        }
    }

}
