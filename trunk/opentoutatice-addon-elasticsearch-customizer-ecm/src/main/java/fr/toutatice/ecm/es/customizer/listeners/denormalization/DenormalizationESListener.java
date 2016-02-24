/**
 * 
 */
package fr.toutatice.ecm.es.customizer.listeners.denormalization;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.toutatice.ecm.es.customizer.listeners.ICustomESListener;
import fr.toutatice.ecm.es.customizer.nx.listener.ESInlineListenerCustomizer;


/**
 * @author david
 *
 */
public abstract class DenormalizationESListener implements ICustomESListener {
    
    public ESInlineListenerCustomizer esListener;
    
    /**
     * Default constructor.
     */
    public DenormalizationESListener(){};

    /**
     * {@inheritDoc}
     */
    @Override
    public void setESInlineListener(ESInlineListenerCustomizer esListener) {
        this.esListener = esListener;
    }
    
    /**
     * @param sourceDocument
     * @return true if sourceDocument is linked with a document to re-index.
     */
    protected abstract boolean needToReIndex(DocumentModel sourceDocument);

    /**
     * {@inheritDoc}
     */
    @Override
    public void customStackCommands(DocumentEventContext docCtx, String eventId) {
        DocumentModel sourceDocument = docCtx.getSourceDocument();
        if (needToReIndex(sourceDocument)) {
            CoreSession session = sourceDocument.getCoreSession();
            stackCommands(session, sourceDocument, eventId);
        }
    }
    
    /**
     * @param linkedDocument
     * @return true if re-indexation of document linked to linkedDocument
     *         must be synchronous.
     */
    protected abstract boolean isSyncReIndexation(DocumentModel linkedDocument);
    
    /**
     * Stacks commands of reindexation of linked docs.
     * 
     * @param session
     * @param sourceDocument
     * @param eventId
     */
    protected abstract void stackCommands(CoreSession session, DocumentModel sourceDocument, String eventId);


}
