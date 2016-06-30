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

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleConstants;

import fr.toutatice.ecm.es.customizer.writers.api.AbstractCustomJsonESWriter;

/**
 * @author david
 *
 */
public abstract class AbstractDenormalizationJsonESWriter extends AbstractCustomJsonESWriter {
	
	/** Denormalization infos. */
	protected Map<String, Object> denormalizationInfos;
	
	/**
	 * Default constructor.
	 */
	public AbstractDenormalizationJsonESWriter(){
	    super();
	    initializeDenormalizationInfos();
	};
	
	/**
	 * To set the denormalizationInfos attribute (Map<String, Object>)
	 * used in default constructor.
	 */
	public abstract void initializeDenormalizationInfos();
	
	/**
	 * Gets linked documents to document to denormalize.
	 * 
	 * @param docToDenormalize
	 * @return linked documents.
	 */
	public Object getLinkedInfosDocs(String docToDenormalize){
	    Object linkedInfosDocs = this.denormalizationInfos.get(docToDenormalize);
	    return linkedInfosDocs != null ? linkedInfosDocs : new Object();
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
	 * {@inheritDoc}
	 */
	public boolean accept(DocumentModel doc){
	    return getDocsToDenormalize().contains(doc.getType());
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
		return isNotDeleted && isNotVersion && accept(doc);
	}
	
	@Override
	public void writeData(JsonGenerator jg, DocumentModel doc, String[] schemas,
            Map<String, String> contextParameters) throws IOException {
		if(hasToDenormalize(doc)){
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
