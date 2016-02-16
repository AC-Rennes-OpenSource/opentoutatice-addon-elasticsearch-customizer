/**
 * 
 */
package fr.toutatice.ecm.es.customizer.writers;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.toutatice.ecm.es.customizer.nx.writer.JsonESDocumentWriterCustomizer;

/**
 * @author david
 *
 */
public interface ICustomJsonESWriter {
	
	/**
	 * Setter of native Nx Json ES Writer.
	 * @param nxJsonESWriter
	 */
	public void setJsonESWriter(JsonESDocumentWriterCustomizer jsonESWriter);
	
	/**
	 * Method to add custom data in ES Json flux.
	 * 
	 * @param jg
	 * @param doc
	 * @param schemas
	 * @param contextParameters
	 */
	public void writeData(JsonGenerator jg, DocumentModel doc, String[] schemas,
            Map<String, String> contextParameters) throws IOException;

}
