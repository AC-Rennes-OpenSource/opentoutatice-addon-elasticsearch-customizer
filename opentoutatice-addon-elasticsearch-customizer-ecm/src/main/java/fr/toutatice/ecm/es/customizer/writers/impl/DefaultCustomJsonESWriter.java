/**
 * 
 */
package fr.toutatice.ecm.es.customizer.writers.impl;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Lock;

import fr.toutatice.ecm.es.customizer.writers.api.AbstractCustomJsonESWriter;


/**
 * Default Json ES writer enabling writing of desired denormalized data.
 * 
 * @author david
 *
 */
public class DefaultCustomJsonESWriter extends AbstractCustomJsonESWriter {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(DocumentModel doc) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeData(JsonGenerator jg, DocumentModel doc, String[] schemas, Map<String, String> contextParameters) throws IOException {
        // Lock
        writeLockInfos(jg, doc);
    }

    /**
     * Write lock informations of document.
     * 
     * @param jg
     * @param doc
     * @throws JsonGenerationException
     * @throws IOException
     */
    // FIXME: create a lock index?
    protected void writeLockInfos(JsonGenerator jg, DocumentModel doc) throws JsonGenerationException, IOException {
        Lock lock = doc.getLockInfo();
        if (lock != null) {
            jg.writeStringField("ottc:lockOwner", lock.getOwner());
            jg.writeStringField("ottc:lockCreated", ISODateTimeFormat.dateTime().print(new DateTime(lock.getCreated())));
        }
    }

}
