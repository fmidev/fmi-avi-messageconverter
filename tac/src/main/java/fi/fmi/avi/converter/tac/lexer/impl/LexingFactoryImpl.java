package fi.fmi.avi.converter.tac.lexer.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.LexemeSequence;
import fi.fmi.avi.converter.tac.lexer.LexemeSequenceBuilder;
import fi.fmi.avi.converter.tac.lexer.LexemeVisitor;
import fi.fmi.avi.converter.tac.lexer.LexingFactory;

/**
 * Created by rinne on 10/02/17.
 */

public class LexingFactoryImpl implements LexingFactory {

    @Override
    public LexemeSequence createLexemeSequence(final String input, final ConversionHints hints) {
        LexemeSequenceImpl result = new LexemeSequenceImpl(input);
        appendArtifialStartTokenIfNecessary(input, result, hints);
        return result;
    }

    @Override
    public LexemeSequenceBuilder createLexemeSequenceBuilder() {
        return new LexemeSequenceBuilderImpl();
    }

    @Override
    public Lexeme createLexeme(final String token) {
        return new LexemeImpl(token);
    }

    @Override
    public Lexeme createLexeme(final String token, final Lexeme.Identity identity) {
        return new LexemeImpl(token, identity);
    }

    @Override
    public Lexeme createLexeme(final String token, final Lexeme.Identity identity, final Lexeme.Status status) {
        return new LexemeImpl(token, identity, status);
    }

    private static void appendArtifialStartTokenIfNecessary(final String input, final LexemeSequenceImpl result, final ConversionHints hints) {
        if (hints != null && hints.containsKey(ConversionHints.KEY_MESSAGE_TYPE)) {
            LexemeImpl artificialStartToken = null;
            if (hints.get(ConversionHints.KEY_MESSAGE_TYPE) == ConversionHints.VALUE_MESSAGE_TYPE_METAR && !input.startsWith("METAR ")) {
                artificialStartToken = new LexemeImpl("METAR", Lexeme.Identity.METAR_START);
            } else if (hints.get(ConversionHints.KEY_MESSAGE_TYPE) == ConversionHints.VALUE_MESSAGE_TYPE_TAF && !input.startsWith("TAF ")) {
                artificialStartToken = new LexemeImpl("TAF", Lexeme.Identity.TAF_START);
            }
            if (artificialStartToken != null) {
                artificialStartToken.setSynthetic(true);
                result.addAsFirst(artificialStartToken);
            }
        }
    }

    private static class LexemeSequenceImpl implements LexemeSequence {

        private String originalTac;
        private LinkedList<LexemeImpl> lexemes;

        public LexemeSequenceImpl(final String originalTac) {
            this();
            this.originalTac = originalTac;
            this.constructFromTAC();
        }

        public LexemeSequenceImpl() {
            this.lexemes = new LinkedList<LexemeImpl>();
        }

        @Override
        public String getTAC() {
            if (this.originalTac != null) {
                return this.originalTac;
            } else {
                return this.getAsTAC();
            }
        }

        @Override
        public Lexeme getFirstLexeme() {
            if (this.lexemes.size() > 0) {
                return this.lexemes.getFirst();
            } else {
                return null;
            }
        }

        @Override
        public Lexeme getLastLexeme() {
            if (this.lexemes.size() > 0) {
                return this.lexemes.getLast();
            } else {
                return null;
            }
        }

        @Override
        public List<Lexeme> getLexemes() {
            return Collections.unmodifiableList(this.lexemes);
        }

        @Override
        public List<LexemeSequence> splitBy(Lexeme.Identity...ids) {
            List<LexemeSequence> retval = new ArrayList<>();
            LexemeSequenceImpl seq = new LexemeSequenceImpl();
            for (LexemeImpl l:this.lexemes) {
                for (Lexeme.Identity toMatch:ids) {
                    //Do not produce empty sequences
                    if (toMatch == l.getIdentity()  && seq.lexemes.size() > 0) {
                        retval.add(seq);
                        seq = new LexemeSequenceImpl();
                        break;
                    }
                }
                seq.addAsLast(l);
            }
            if (seq.lexemes.size() > 0) {
                retval.add(seq);
            }
            return retval;
        }

        LexemeImpl replaceFirstWith(final LexemeImpl replacement) {
            if (replacement == null) {
                throw new NullPointerException();
            }
            if (this.lexemes.size() == 0) {
                throw new IllegalStateException("No first lexeme to replace");
            }
            LexemeImpl oldFirst = this.lexemes.removeFirst();
            this.addAsFirst(replacement);
            int indexAdjustment = 0;
            if (oldFirst.isSynthetic() && !replacement.isSynthetic()) {
                //add the full length
                this.adjustIndexes(1, replacement.getTACToken().length() + 1);
            } else if (!oldFirst.isSynthetic() && replacement.isSynthetic()) {
                //cut the full length
                this.adjustIndexes(1, -(oldFirst.getTACToken().length() + 1));
            } else if (!oldFirst.isSynthetic() && !replacement.isSynthetic()) {
                //adjust by the length difference
                this.adjustIndexes(1, replacement.getTACToken().length() - oldFirst.getTACToken().length());
            }
            return oldFirst;
        }

        LexemeImpl replaceLastWith(final LexemeImpl replacement) {
            if (replacement == null) {
                throw new NullPointerException();
            }
            if (this.lexemes.size() == 0) {
                throw new IllegalStateException("No last lexeme to replace");
            }
            LexemeImpl oldLast = this.lexemes.removeLast();
            this.addAsLast(replacement);
            return oldLast;
        }

        void addAsFirst(final LexemeImpl toAdd) {
            if (toAdd != null) {
                LexemeImpl oldFirst = this.lexemes.getFirst();
                if (oldFirst != null) {
                    oldFirst.setPrevious(toAdd);
                    toAdd.setNext(oldFirst);
                }
                toAdd.setPrevious(null);
                this.lexemes.addFirst(toAdd);
                this.updateLinksToFirst();
                if (!toAdd.isSynthetic()) {
                    //Assume a single white space token separator:
                    this.adjustIndexes(1, toAdd.getTACToken().length() + 1);
                }
            }
        }

        void addAsLast(final LexemeImpl toAdd) {
            if (toAdd != null) {
                if (this.lexemes.size() > 0) {
                    LexemeImpl oldLast = this.lexemes.getLast();
                    oldLast.setNext(toAdd);
                    toAdd.setPrevious(oldLast);
                    toAdd.setFirst(this.lexemes.getFirst());
                } else {
                    toAdd.setFirst(toAdd);
                    toAdd.setPrevious(null);
                }
                toAdd.setNext(null);
                this.lexemes.addLast(toAdd);
            }
        }

        LexemeImpl removeFirst() {
            LexemeImpl removed = this.lexemes.removeFirst();
            if (removed != null) {
                this.lexemes.getFirst().setPrevious(null);
                this.updateLinksToFirst();
                if (!removed.isSynthetic()) {
                    this.adjustIndexes(0, -(removed.getTACToken().length() + 1));
                }
            }

            return removed;
        }

        LexemeImpl removeLast() {
            LexemeImpl removed = this.lexemes.removeLast();
            if (removed != null) {
                this.lexemes.getLast().setNext(null);
            }
            return removed;
        }

        private void updateLinksToFirst() {
            LexemeImpl first = this.lexemes.getFirst();
            for (LexemeImpl l : this.lexemes) {
                l.setFirst(first);
            }
        }

        private void adjustIndexes(int fromIndex, int by) {
            ListIterator<LexemeImpl> it = this.lexemes.listIterator(fromIndex);
            LexemeImpl li;
            while (it.hasNext()) {
                li = it.next();
                li.setStartIndex(li.getStartIndex() + by);
                li.setEndIndex(li.getEndIndex() + by);
            }
        }


        private void constructFromTAC() {
            if (this.originalTac != null && this.originalTac.length() > 0) {
                Pattern horVisFractionNumberPart1Pattern = Pattern.compile("^[0-9]*$");
                Pattern horVisFractionNumberPart2Pattern = Pattern.compile("^[0-9]*/[0-9]*[A-Z]{2}$");
                // Windshear token for a particular runway has changed between 16th and 19th edition of Annex 3
                //  16th = "WS RWYnn[LRC]"
                //  19th = "WS Rnn[LRC]"
                Pattern windShearRunwayPattern = Pattern.compile("^R(?:WY)?([0-9]{2})?[LRC]?$");
                Splitter byWhiteSpace = Splitter.onPattern("\\s").trimResults().omitEmptyStrings();
                Iterable<String> tokens = byWhiteSpace.split(originalTac);
                String lastToken = null;
                String lastLastToken = null;
                int start = 0;
                LexemeImpl l;
                for (String s : tokens) {
                    start = originalTac.indexOf(s, start);
                    if (s.endsWith("=")) {
                        l = new LexemeImpl(s.substring(0, s.length() - 1));
                        l.setStartIndex(start);
                        l.setEndIndex(l.getStartIndex() + l.getTACToken().length() - 1);
                        this.addAsLast(l);
                        l = new LexemeImpl("=");
                        l.setStartIndex(start + s.length() - 1);
                        l.setEndIndex(l.getStartIndex() + l.getTACToken().length() - 1);
                        this.addAsLast(l);
                    } else if (lastToken != null && horVisFractionNumberPart2Pattern.matcher(s).matches() && horVisFractionNumberPart1Pattern.matcher(lastToken)
                            .matches()) {
                        // cases like "1 1/8SM", combine the two tokens:
                        l = new LexemeImpl(lastToken + " " + s);
                        l.setStartIndex(this.getLastLexeme().getStartIndex());
                        l.setEndIndex(l.getStartIndex() + l.getTACToken().length() - 1);
                        this.replaceLastWith(l);
                    } else if ("WS".equals(lastLastToken) && "ALL".equals(lastToken) && windShearRunwayPattern.matcher(s).matches()) {
                        // "WS ALL RWY" case: concat all three parts as the last token:
                        this.removeLast(); // ALL
                        l = new LexemeImpl("WS ALL RWY");
                        l.setStartIndex(this.getLastLexeme().getStartIndex());
                        l.setEndIndex(l.getStartIndex() + l.getTACToken().length() - 1);
                        this.replaceLastWith(l);
                    } else if ("WS".equals(lastToken) && windShearRunwayPattern.matcher(s).matches()) {
                        // "WS RWY22L" case, concat the two parts as the last token:
                        l = new LexemeImpl("WS " + s);
                        l.setStartIndex(this.getLastLexeme().getStartIndex());
                        l.setEndIndex(l.getStartIndex() + l.getTACToken().length() - 1);
                        this.replaceLastWith(l);
                    } else if (("PROB30".equals(lastToken) || "PROB40".equals(lastToken)) && ("TEMPO".equals(s))) {
                        l = new LexemeImpl(lastToken + " " + s);
                        l.setStartIndex(this.getLastLexeme().getStartIndex());
                        l.setEndIndex(l.getStartIndex() + l.getTACToken().length() - 1);
                        this.replaceLastWith(l);
                    } else {
                        l = new LexemeImpl(s);
                        l.setStartIndex(start);
                        l.setEndIndex(start + l.getTACToken().length() - 1);
                        this.addAsLast(l);
                    }
                    lastLastToken = lastToken;
                    lastToken = s;
                    start += s.length();
                }
            }
        }

        private String getAsTAC() {
            if (this.lexemes != null) {
                StringBuilder retval = new StringBuilder();
                retval.append(this.lexemes.stream()
                        .map(LexemeImpl::getTACToken)
                        .filter((str) -> !"=".equals(str))
                        .collect(Collectors.joining(" ")));
                if (Lexeme.Identity.END_TOKEN == this.lexemes.getLast().getIdentity()) {
                    retval.append('=');
                }
                return retval.toString();
            } else {
                return null;
            }
        }

    }

    private static class LexemeSequenceBuilderImpl implements LexemeSequenceBuilder {
        private LexemeSequenceImpl seq;

        LexemeSequenceBuilderImpl() {
            seq = new LexemeSequenceImpl();
        }

        @Override
        public LexemeSequenceBuilder append(final Lexeme lexeme) {
            this.seq.addAsLast(new LexemeImpl(lexeme));
            return this;
        }

        @Override
        public LexemeSequence build() {
            return seq;
        }

		@Override
		public LexemeSequenceBuilder appendAll(List<Lexeme> lexemes) {
			if (lexemes != null) {
				for (Lexeme l: lexemes) {
					this.seq.addAsLast(new LexemeImpl(l));
				}
			}
			return this;
		}
    }

    private static class LexemeImpl implements Lexeme {
        private Identity id;
        private String tacToken;
        private Status status;
        private String lexerMessage;
        private boolean isSynthetic;
        private Map<ParsedValueName, Object> parsedValues;
        private int startIndex = -1;
        private int endIndex = -1;
        private double certainty = 0.0d;

        //Lexing navigation:
        private Lexeme first;
        private Lexeme next;
        private Lexeme prev;

        LexemeImpl(final Lexeme lexeme) {
            this.tacToken = lexeme.getTACToken();
            this.id = lexeme.getIdentity();
            this.status = lexeme.getStatus();
            this.lexerMessage = lexeme.getLexerMessage();
            this.isSynthetic = lexeme.isSynthetic();
            this.parsedValues = new HashMap<ParsedValueName, Object>(lexeme.getParsedValues());
            this.startIndex = lexeme.getStartIndex();
            this.endIndex = lexeme.getEndIndex();
            this.certainty = lexeme.getIdentificationCertainty();
        }

        LexemeImpl(final String token) {
            this(token, null, Status.UNRECOGNIZED);
        }

        LexemeImpl(final String token, final Identity identity) {
            this(token, identity, Status.OK);
        }

        LexemeImpl(final String token, final Identity identity, final Status status) {
            this.tacToken = token;
            this.id = identity;
            this.status = status;
            this.isSynthetic = false;
            this.parsedValues = new HashMap<ParsedValueName, Object>();
        }

        @Override
        public Identity getIdentity() {
            return this.id;
        }

        @Override
        public Identity getIdentityIfAcceptable() throws IllegalStateException {
            if (Status.OK == this.status || Status.WARNING == this.status) {
                return this.id;
            } else {
                return null;
            }
        }

        @Override
        public Status getStatus() {
            return this.status;
        }

        @Override
        public String getLexerMessage() {
            return this.lexerMessage;
        }

        @Override
        public int getStartIndex() {
            return this.startIndex;
        }

        @Override
        public int getEndIndex() {
            return this.endIndex;
        }

        @SuppressWarnings("unchecked")
		public <T> T getParsedValue(ParsedValueName name, Class<T> clz) {
        	if (this.id == null) {
        		return null;
        	} else {
        		if (!this.id.canStore(name)) {
        			throw new IllegalArgumentException("Lexeme of identity " + this.id + " can never contain parsed value "+ name +", you should fix your code");
        		}
        	}
            Object val = this.parsedValues.get(name);
            if (val != null){
            	if (clz.isAssignableFrom(val.getClass())) {
            		return (T) val;
            	} else {
            		throw new ClassCastException("Cannot return value of type " + val.getClass() + " as " + clz);
            	}
            } else {
            	return null;
            }
        }

        @Override
        public Map<ParsedValueName, Object> getParsedValues() {
            return this.parsedValues;
        }

        @Override
        public String getTACToken() {
            return this.tacToken;
        }

        @Override
        public Lexeme getFirst() {
            return this.first;
        }

        @Override
        public Lexeme getPrevious() {
            return this.prev;
        }

        @Override
        public Lexeme getNext() {
            return this.next;
        }

        @Override
        public boolean hasPrevious() {
            return this.prev != null;
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public boolean isSynthetic() {
            return isSynthetic;
        }

        @Override
        public boolean isRecognized() {
            return !Status.UNRECOGNIZED.equals(this.status);
        }
        
        @Override
        public double getIdentificationCertainty() {
        	return this.certainty;
        }

        @Override
        public void identify(final Identity id, final double certainty) {
            identify(id, Status.OK, null, certainty);
        }

        @Override
        public void identify(final Identity id, final Status status, final double certainty) {
            identify(id, status, null, certainty);
        }

        @Override
        public void identify(final Identity id, final Status status, final String note, final double certainty) {
            this.id = id;
            this.status = status;
            this.lexerMessage = note;
            this.setIdentificationCertainty(certainty);
        }
        
        @Override
        public void identify(final Identity id) {
            identify(id, Status.OK, null, 1.0);
        }

        @Override
        public void identify(final Identity id, final Status status) {
            identify(id, status, null, 1.0);
        }

        @Override
        public void identify(final Identity id, final Status status, final String note) {
           identify(id, status, note, 1.0);
        }

        @Override
        public void setStatus(final Status status) {
            this.status = status;
        }

        @Override
        public void setSynthetic(final boolean synthetic) {
            isSynthetic = synthetic;
        }

        @Override
        public void setParsedValue(ParsedValueName name, Object value) {
        	if (this.id != null) {
        		if (!this.id.canStore(name)) {
                    throw new IllegalArgumentException(this.id + " can only store " + id.getPossibleNames());
                }
        		this.parsedValues.put(name, value);
        	} else {
        		throw new IllegalStateException("Cannot set parsed value before identifying Lexeme");
        	}
        }

        @Override
        public void setLexerMessage(final String msg) {
            this.lexerMessage = msg;
        }
        
        @Override
        public void setIdentificationCertainty(final double percentage) {
        	if (percentage < 0.0 || percentage > 1.0) {
        		throw new IllegalArgumentException("Certainty must be between 0.0 and 1.0");
        	}
        	this.certainty = percentage;
        }
        
        void setStartIndex(final int index) {
            this.startIndex = index;
        }

        void setEndIndex(final int index) {
            this.endIndex = index;
        }

        
        @Override
        public void accept(final LexemeVisitor visitor, final ConversionHints hints) {
            //Always acccept:
            if (visitor != null) {
                visitor.visit(this, hints);
            }
        }

        void setFirst(final Lexeme token) {
            this.first = token;
        }

        void setNext(final Lexeme token) {
            this.next = token;
        }

        void setPrevious(final Lexeme token) {
            this.prev = token;
        }
        
        

        public String toString() {
            return new StringBuilder().append(this.tacToken)
                    .append(' ')
                    .append('(')
                    .append(this.id)
                    .append(',')
                    .append(this.status)
                    .append(')')
                    .toString();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final LexemeImpl lexeme = (LexemeImpl) o;

            if (isSynthetic != lexeme.isSynthetic) {
                return false;
            }
            if (id != lexeme.id) {
                return false;
            }
            if (status != lexeme.status) {
                return false;
            }
            return lexerMessage != null ? lexerMessage.equals(lexeme.lexerMessage) : lexeme.lexerMessage == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (status != null ? status.hashCode() : 0);
            result = 31 * result + (lexerMessage != null ? lexerMessage.hashCode() : 0);
            result = 31 * result + (isSynthetic ? 1 : 0);
            return result;
        }
    }

}
