package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.REMARK;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.REMARKS_START;

import java.util.ArrayList;
import java.util.List;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.PrioritizedLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class RemarkStart extends PrioritizedLexemeVisitor {
    public RemarkStart(final Priority prio) {
        super(prio);
    }

    @Override
    public void visit(final Lexeme token, final ConversionHints hints) {
        if ("RMK".equalsIgnoreCase(token.getTACToken())) {
            token.identify(REMARKS_START);
        }
    }
    
    public static class Reconstructor extends FactoryBasedReconstructor {
    	@Override
        public <T extends AviationWeatherMessage> List<Lexeme> getAsLexemes(T msg, Class<T> clz, ConversionHints hints, Object... specifier)
                throws SerializingException {
            List<Lexeme> retval = null;
    		
    		if (msg.getRemarks() != null && !msg.getRemarks().isEmpty()) {
    			retval = new ArrayList<>();
    			retval.add(this.createLexeme("RMK", REMARKS_START));
    			for (String remark : msg.getRemarks()) {
    				retval.add(this.createLexeme(remark, REMARK));
    			}
    		}
    		
    		return retval;
    	}
    }
}
