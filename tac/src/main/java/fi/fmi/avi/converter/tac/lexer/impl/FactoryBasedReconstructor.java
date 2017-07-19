package fi.fmi.avi.converter.tac.lexer.impl;

import java.util.ArrayList;
import java.util.List;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.LexingFactory;

/**
 * Created by rinne on 01/03/17.
 */
public abstract class FactoryBasedReconstructor implements TACTokenReconstructor {
	
	protected static <T> T getAs(Object[] specifiers, Class<T> clz) {
		T ret = null;
		for (int i = 0; i < specifiers.length; i++) {
			ret = getAs(specifiers, i, clz);
			if (ret != null) {
				break;
			}
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	protected static <T> T getAs(Object[] specifiers, final int index, Class<T> clz) {
		T retval = null;
		if (specifiers != null && specifiers.length > index && specifiers[index] != null && clz.isAssignableFrom(specifiers[index].getClass())) {
			retval = (T) specifiers[index];
		}
		return retval;
	}
	
	private LexingFactory factory;

    public void setLexingFactory(final LexingFactory factory) {
        this.factory = factory;
    }

    public LexingFactory getLexingFactory() {
        return this.factory;
    }

	protected Lexeme createLexeme(final String token) {
		return this.createLexeme(token, null, Lexeme.Status.UNRECOGNIZED);
	}

	protected Lexeme createLexeme(final String token, final Lexeme.Identity identity) {
		return this.createLexeme(token, identity, Lexeme.Status.OK);
	}

	protected Lexeme createLexeme(final String token, final Lexeme.Identity identity, final Lexeme.Status status) {
		if (this.factory != null) {
			return this.factory.createLexeme(token, identity, status);
		} else {
			throw new IllegalStateException("No LexingFactory injected");
		}
	}

    @Override
	public <T extends AviationWeatherMessage> List<Lexeme> getAsLexemes(T msg, Class<T> clz, ConversionHints hints, Object... specifier)
			throws SerializingException {
		List<Lexeme> retval = new ArrayList<>();
		Lexeme lexeme = getAsLexeme(msg, clz, hints, specifier);
		if (lexeme != null) {
    		retval.add(lexeme);
    	}
    	return retval;
    }
    
    /**
     * Override this unless the class overrides getAsLexemes()
     *
	 * @param msg the source message
	 * @param clz the class of the source message
	 * @param hints conversion hints to guide the reconstructor implementation
	 * @param specifier additional specifiers for selecting the Lexeme to produce
	 * @param <T> the type of the source message
	 * @return the reconstructed Lexeme
	 * @throws SerializingException when the Lexeme cannot be constructed
	 */
	public <T extends AviationWeatherMessage> Lexeme getAsLexeme(T msg, Class<T> clz, ConversionHints hints, Object... specifier) throws SerializingException {
		return null;
	}
}
