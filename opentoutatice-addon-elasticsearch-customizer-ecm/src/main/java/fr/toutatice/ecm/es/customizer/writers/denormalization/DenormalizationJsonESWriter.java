/**
 * 
 */
package fr.toutatice.ecm.es.customizer.writers.denormalization;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.automation.jaxrs.io.documents.JsonESDocumentWriter;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.lifecycle.LifeCycle;

import fr.toutatice.ecm.es.customizer.nx.writer.JsonESDocumentWriterCustomizer;
import fr.toutatice.ecm.es.customizer.writers.ICustomJsonESWriter;

/**
 * @author david
 *
 */
public abstract class DenormalizationJsonESWriter implements ICustomJsonESWriter {
	
	/**
	 * Native Nx JsonWriter.
	 */
	protected JsonESDocumentWriterCustomizer jsonESWriter;
	
	/** System session. */
	protected CoreSession session;
	
	/** Denormalization infos. */
	protected Map<String, Object> denormalizationInfos;
	
	/**
	 * Default constructor.
	 */
	public DenormalizationJsonESWriter(){};
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setJsonESWriter(JsonESDocumentWriterCustomizer jsonESWriter) {
		this.jsonESWriter = jsonESWriter;
	}
	
	/**
	 * gets the current (thread?) system session (we are in a queue).
	 * 
	 * @param doc
	 */
	protected void setCurrentSystemSession(DocumentModel doc){
		    this.session = doc.getCoreSession();
	}
	
	/**
	 * @return documents to denormalize (null safe).
	 */
	public List<String> getDocsToDenormalize(){
		Set<String> docs = this.denormalizationInfos.keySet();
		docs = docs == null ? new LinkedHashSet<String>(0) : docs;
		return new LinkedList<String>(docs);
	}
	
	/**
	 * @param doc current doc in indexing process.
	 * @return true if doc must be denormalize.
	 */
	protected boolean hasToDenormalize(DocumentModel doc){
		if(doc == null){
			return false;
		}
		boolean isNotDeleted = !doc.getLifeCyclePolicy().equalsIgnoreCase(LifeCycleConstants.DELETED_STATE);
		boolean isNotVersion = !doc.isVersion();
		return isNotDeleted && isNotVersion && getDocsToDenormalize().contains(doc.getType());
	}
	
	@Override
	public void writeData(JsonGenerator jg, DocumentModel doc, String[] schemas,
            Map<String, String> contextParameters) throws IOException {
		if(hasToDenormalize(doc)){
		    setCurrentSystemSession(doc);
			denormalizeDoc(jg, doc, schemas, contextParameters);
		}
	}
	
	/**
	 * Denormalize given doc. 
	 * @param doc doc to denormalize.
	 */
	protected abstract void denormalizeDoc(JsonGenerator jg, DocumentModel doc, String[] schemas,
            Map<String, String> contextParameters) throws IOException;
	
}
