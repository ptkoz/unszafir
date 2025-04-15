package Unszafir.Signatures;

import com.google.inject.Inject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import xades4j.algorithms.CanonicalXMLWithComments;
import xades4j.algorithms.CanonicalXMLWithoutComments;
import xades4j.production.EnvelopedXmlObject;
import xades4j.properties.DataObjectDesc;
import xades4j.properties.DataObjectFormatProperty;

import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.IOException;

public class InputDocumentFactory {
    private final DocumentBuilder documentBuilder;

    @Inject
    public InputDocumentFactory(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    public DataObjectDesc createDataObjectForFile(String inputFilePath, Document outputDocument) throws InvalidFileException {
        Document documentToSign = loadXml(getFileForPath(inputFilePath));

        return new EnvelopedXmlObject(
            outputDocument.importNode(documentToSign.getDocumentElement(), true),
            "text/xml",
            null
        ).withDataObjectFormat(new DataObjectFormatProperty("text/xml")).withTransform(new CanonicalXMLWithoutComments());

    }

    private File getFileForPath(String filePath) throws InvalidFileException {
        File inputFile = new File(filePath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            throw new InvalidFileException("Input file `" + filePath + "` does not exist or is not a file");
        }

        if (!inputFile.canRead()) {
            throw new InvalidFileException("Input file `" + filePath + "` is not readable");
        }

        return inputFile;
    }

    private Document loadXml(File inputFile) throws InvalidFileException {
        try {
            return documentBuilder.parse(inputFile);
        } catch (IOException | SAXException e) {
            throw new InvalidFileException("Could not parse input file `" + inputFile.getAbsolutePath() + "`");
        }
    };
}
