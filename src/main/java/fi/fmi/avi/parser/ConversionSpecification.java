package fi.fmi.avi.parser;

import fi.fmi.avi.data.metar.Metar;
import fi.fmi.avi.data.taf.TAF;

public class ConversionSpecification<S, T> {
	public static final ConversionSpecification<String, Metar> TAC_TO_METAR = new ConversionSpecification<>(String.class, Metar.class);
	public static final ConversionSpecification<Metar, String> METAR_TO_TAC = new ConversionSpecification<>(Metar.class, String.class);

	public static final ConversionSpecification<String, TAF> TAC_TO_TAF = new ConversionSpecification<>(String.class, TAF.class);
	public static final ConversionSpecification<TAF, String> TAF_TO_TAC = new ConversionSpecification<>(TAF.class, String.class);

	private Class<S> inputClass;
    private Class<T> outputClass;
    private Object specifier;

	public ConversionSpecification(final Class<S> input, final Class<T> output) {
		this(input, output, null);
    }

	public ConversionSpecification(final Class<S> input, final Class<T> output, final Object specifier) {
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
		ConversionSpecification other = (ConversionSpecification) obj;
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