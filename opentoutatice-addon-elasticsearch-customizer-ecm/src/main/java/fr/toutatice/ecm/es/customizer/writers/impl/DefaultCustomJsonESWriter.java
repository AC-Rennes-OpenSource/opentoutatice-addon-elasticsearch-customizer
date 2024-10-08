/**
 *
 */
package fr.toutatice.ecm.es.customizer.writers.impl;

import fr.toutatice.ecm.es.customizer.writers.api.AbstractCustomJsonESWriter;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.JsonGenerator;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.runtime.api.Framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Default JSON ES writer enabling writing of desired denormalized data.
 *
 * @author David Chevrier
 * @author Cédric Krommenhoek
 * @see AbstractCustomJsonESWriter
 */
public class DefaultCustomJsonESWriter extends AbstractCustomJsonESWriter {

    /**
     * Space path.
     */
    private final String SPACE_PATH = Framework.getProperty("ottc.es.space.indexation", "/default-domain/workspaces");


    @Override
    public boolean accept(DocumentModel document) {
        return true;
    }


    @Override
    public void writeData(JsonGenerator jsonGenerator, DocumentModel document, String[] schemas, Map<String, String> contextParameters) throws IOException {
        // Parent documents
        List<DocumentModel> parentDocuments = this.session.getParentDocuments(document.getRef());

        // Lock information
        this.writeLockInfo(jsonGenerator, document);

        // Space information
        if (document.getPathAsString() != null && document.getPathAsString().startsWith(SPACE_PATH)) {
            this.writeSpaceInfo(jsonGenerator, document, parentDocuments);
        }

        // Search information
        this.writeSearchInfo(jsonGenerator, document, parentDocuments);
    }


    /**
     * Write lock information.
     *
     * @param jsonGenerator JSON generator
     * @param document      document
     */
    // FIXME: create a lock index?
    protected void writeLockInfo(JsonGenerator jsonGenerator, DocumentModel document) throws IOException {
        Lock lock = document.getLockInfo();
        if (lock != null) {
            jsonGenerator.writeStringField("ttc:lockOwner", lock.getOwner());
            jsonGenerator.writeStringField("ttc:lockCreated", ISODateTimeFormat.dateTime().print(new DateTime(lock.getCreated())));
        }
    }


    /**
     * Write space information.
     *
     * @param jsonGenerator   JSON generator
     * @param currentDocument current document
     * @param parentDocuments parent documents
     */
    protected void writeSpaceInfo(JsonGenerator jsonGenerator, DocumentModel currentDocument, List<DocumentModel> parentDocuments) throws IOException {
        List<DocumentModel> documents = new ArrayList<>(parentDocuments);
        documents.add(currentDocument);

        DocumentModel rootSpace = documents.stream()
                .filter(document -> !"Domain".equals(document.getType()))
                .filter(document -> document.hasFacet("Space"))
                .findFirst()
                .orElse(null);

        if (rootSpace != null) {
            jsonGenerator.writeStringField("ttc:spaceUuid", rootSpace.getId());
            jsonGenerator.writeStringField("ttc:spaceTitle", rootSpace.getTitle());
            jsonGenerator.writeStringField("ttc:spaceType", rootSpace.getType());
            if (rootSpace.hasSchema("webcontainer")) {
                jsonGenerator.writeStringField("ttc:spaceLdapId", rootSpace.getPropertyValue("webc:url").toString());
            }
        }
    }


    /**
     * Write search information.
     *
     * @param jsonGenerator   JSON generator
     * @param currentDocument current document
     * @param parentDocuments parent documents
     */
    protected void writeSearchInfo(JsonGenerator jsonGenerator, DocumentModel currentDocument, List<DocumentModel> parentDocuments) throws IOException {
        // Filtered parents
        List<DocumentModel> filteredParents;
        if (CollectionUtils.isEmpty(parentDocuments)) {
            filteredParents = null;
        } else {
            filteredParents = parentDocuments.stream()
                    .filter(document -> !currentDocument.equals(document))
                    .filter(document -> !"Domain".equals(document.getType()))
                    .filter(document -> document.hasFacet("Space"))
                    .collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(filteredParents)) {
            jsonGenerator.writeArrayFieldStart("search:parents");

            for (DocumentModel parent : filteredParents) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("title", parent.getTitle());
                jsonGenerator.writeStringField("type", parent.getType());
                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndArray();
        }
    }

}
