package Unszafir.Signatures;

import javax.inject.Inject;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class OutputDocumentService {
    private final DocumentBuilder documentBuilder;
    private final Transformer transformer;

    @Inject
    public OutputDocumentService(DocumentBuilder documentBuilder, Transformer transformer) {
        this.documentBuilder = documentBuilder;
        this.transformer = transformer;
    }

    public Document createOutputDocument() {
        return documentBuilder.newDocument();
    }

    public void saveOutputDocument(Document outputDocument, File outputFile) throws UnableToSaveException {
        try {
            transformer.transform(new DOMSource(outputDocument), new StreamResult(outputFile));
        } catch (TransformerException e) {
            throw new UnableToSaveException("Unable to save output document: " + e.getMessage());
        }
    }
}
