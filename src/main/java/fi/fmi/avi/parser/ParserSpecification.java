package fi.fmi.avi.parser;

import fi.fmi.avi.data.AviationWeatherMessage;
import fi.fmi.avi.data.metar.Metar;
import fi.fmi.avi.data.taf.TAF;

public class ParserSpecification<S, T extends AviationWeatherMessage> {
	public static final ParserSpecification<String, Metar> TAC_TO_METAR = new ParserSpecification<>(String.class,  Metar.class);
	public static final ParserSpecification<String, TAF> TAC_TO_TAF = new ParserSpecification<>(String.class, TAF.class);


	private Class<S> inputClass;
    private Class<T> outputClass;
    private Object specifier;

    public ParserSpecification(final Class<S> input, final Class<T> output){
    	this(input, output, null);
    }
    
    public ParserSpecification(final Class<S> input, final Class<T> output, final Object specifier) {
        this.inputClass = input;
        this.outputClass = output;
        this.specifier = specifier;
    }

   

    public Class<S> getInputClass() {
        return inputClass;
    }

    public Class<T> getOutputClass() {
        return outputClass;
    }
    
    @Override
   	public int hashCode() {
   		final int prime = 31;
   		int result = 1;
   		result = prime * result + ((inputClass == null) ? 0 : inputClass.hashCode());
   		result = prime * result + ((outputClass == null) ? 0 : outputClass.hashCode());
   		result = prime * result + ((specifier == null) ? 0 : specifier.hashCode());
   		return result;
   	}

   	@Override
   	public boolean equals(Object obj) {
   		if (this == obj)
   			return true;
   		if (obj == null)
   			return false;
   		if (getClass() != obj.getClass())
   			return false;
   		ParserSpecification other = (ParserSpecification) obj;
   		if (inputClass == null) {
   			if (other.inputClass != null)
   				return false;
   		} else if (!inputClass.equals(other.inputClass))
   			return false;
   		if (outputClass == null) {
   			if (other.outputClass != null)
   				return false;
   		} else if (!outputClass.equals(other.outputClass))
   			return false;
   		if (specifier == null) {
   			if (other.specifier != null)
   				return false;
   		} else if (!specifier.equals(other.specifier))
   			return false;
   		return true;
   	}
    
    

}