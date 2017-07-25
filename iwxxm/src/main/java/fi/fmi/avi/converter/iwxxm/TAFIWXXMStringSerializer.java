package fi.fmi.avi.converter.iwxxm;

import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import fi.fmi.avi.converter.ConversionHints;
import icao.iwxxm21.TAFType;

public class TAFIWXXMStringSerializer extends AbstractTAFIWXXMSerializer<String> {

  public TAFIWXXMStringSerializer() {
  }

  @Override
  protected String render(TAFType taf, ConversionHints hints) throws JAXBException {
    return renderXMLString(taf, hints);
  }
  
  private String renderXMLString(final TAFType tafElem, final ConversionHints hints) throws JAXBException {
    Document result = renderXMLDocument(tafElem, hints);
    String retval = null;
    if (result != null) {
      try {
        StringWriter sw = new StringWriter();
        Result output = new StreamResult(sw);
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        
        //TODO: switch these on based on the ConversionHints:
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        DOMSource dsource = new DOMSource(result);
        transformer.transform(dsource, output);
        retval = sw.toString();
      } catch (TransformerConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (TransformerException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return retval;
  }




}
