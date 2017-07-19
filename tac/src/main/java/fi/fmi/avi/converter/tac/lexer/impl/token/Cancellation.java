package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.CANCELLATION;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.VALID_TIME;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.PrioritizedLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class Cancellation extends PrioritizedLexemeVisitor {

    public Cancellation(final Priority prio) {
        super(prio);
    }

    @Override
    public void visit(final Lexeme token, final ConversionHints hints) {
        if (token.getPrevious() != null && token.getPrevious().getIdentity() == VALID_TIME && "CNL".equalsIgnoreCase(token.getTACToken())) {
            token.identify(CANCELLATION);
        }
    }
    
    public static class Reconstructor extends FactoryBasedReconstructor {
    	@Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(T msg, Class<T> clz, ConversionHints hints, Object... specifier)
                throws SerializingException {
            Lexeme retval = null;
            if (TAF.class.isAssignableFrom(clz)) {
            	if (AviationCodeListUser.TAFStatus.CANCELLATION == ((TAF) msg).getStatus()) {
                    retval = this.createLexeme("CNL", CANCELLATION);
                }
            }
            
            return retval;
    	}
    }
}
