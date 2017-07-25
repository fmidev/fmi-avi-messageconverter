package fi.fmi.avi.converter.iwxxm;


import javax.xml.bind.JAXBException;

import org.w3c.dom.Document;

import fi.fmi.avi.converter.ConversionHints;
import icao.iwxxm21.TAFType;

/**
 * Created by rinne on 19/07/17.
 */
public class TAFIWXXMDOMSerializer extends AbstractTAFIWXXMSerializer<Document> {

  public TAFIWXXMDOMSerializer() {
  }

  @Override
  protected Document render(final TAFType taf, final ConversionHints hints) throws JAXBException {
   return this.renderXMLDocument(taf, hints);
  }

}
