/**
 * 
 */
package fr.toutatice.ecm.es.customizer.listeners.denormalization;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.toutatice.ecm.es.customizer.listeners.api.ICustomESListener;
import fr.toutatice.ecm.es.customizer.nx.listener.ESInlineListenerCustomizer;


/**
 * @author david
 *
 */
public abstract class AbstractDenormalizationESListener implements ICustomESListener {
    
    public ESInlineListenerCustomizer esListener;
    
    /**
     * Default constructor.
     */
    public AbstractDenormalizationESListener(){};

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
    protected abstract boolean needToReIndex(DocumentModel sourceDocument, String eventId);

    /**
     * {@inheritDoc}
     */
    @Override
    public void customStackCommands(DocumentEventContext docCtx, String eventId) {
        DocumentModel sourceDocument = docCtx.getSourceDocument();
        if (needToReIndex(sourceDocument, eventId)) {
            CoreSession session = sourceDocument.getCoreSession();
            stackCommands(session, sourceDocument, eventId);
        }
    }
    
    // FIXME: should be used in stackCommands.
//    /**
//     * @param linkedDocument
//     * @return true if re-indexation of document linked to linkedDocument
//     *         must be synchronous.
//     */
//    protected abstract boolean isSyncReIndexation(DocumentModel linkedDocument);
    
    /**
     * Stacks commands of reindexation of linked docs.
     * 
     * @param session
     * @param sourceDocument
     * @param eventId
     */
    protected abstract void stackCommands(CoreSession session, DocumentModel sourceDocument, String eventId);


}
