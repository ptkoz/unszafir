package Unszafir.Cli;

import Unszafir.Signatures.*;
import javax.inject.Inject;
import org.w3c.dom.Document;
import picocli.CommandLine;
import xades4j.production.*;
import xades4j.properties.AllDataObjsCommitmentTypeProperty;
import xades4j.properties.DataObjectDesc;
import xades4j.providers.KeyingDataProvider;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "sign",
    description = "Create signature for given file."
)
public class SignCommand implements Callable<Integer> {
    @CommandLine.ParentCommand
    private MainCommand mainCommand;

    @CommandLine.Parameters(index = "0", description = "The file to be signed")
    String inputFile;

    private final SignerFactory signerFactory;
    private final InputDocumentFactory inputDocumentFactory;
    private final OutputDocumentService outputDocumentService;
    private final CertificateProviderFactory certificateProviderFactory;
    private final Ui ui;

    @Inject
    public SignCommand(
        SignerFactory signerFactory,
        InputDocumentFactory inputDocumentFactory,
        OutputDocumentService outputDocumentService,
        CertificateProviderFactory certificateProviderFactory,
        Ui ui
    ) {
        this.signerFactory = signerFactory;
        this.inputDocumentFactory = inputDocumentFactory;
        this.outputDocumentService = outputDocumentService;
        this.certificateProviderFactory = certificateProviderFactory;
        this.ui = ui;
    }

    @Override
    public Integer call() throws Exception {
        Document outputDocument = outputDocumentService.createOutputDocument();
        KeyingDataProvider keyingDataProvider;
        DataObjectDesc dataToSign;

        try {
            keyingDataProvider = certificateProviderFactory.createKeyingDataProvider(mainCommand.pkcs11Module, mainCommand.pkcs11SlotIndex);
            dataToSign = inputDocumentFactory.createDataObjectForFile(inputFile, outputDocument);
        } catch(InvalidFileException | InvalidProviderException e) {
            ui.displayError(e.getMessage());
            return 1;
        }

        try {
            if (!ui.promptSignatureCreation(certificateProviderFactory.extractCertificate(keyingDataProvider), inputFile)) {
                ui.confirmCancellation();
                return 0;
            }
            ui.confirmSigning();
        } catch (InvalidProviderException e) {
            ui.displayError(e.getMessage());
            return 1;
        }

        XadesSigner signer = signerFactory.createXadesSigner(keyingDataProvider);
        signer.sign(
            new SignedDataObjects(dataToSign).withCommitmentType(AllDataObjsCommitmentTypeProperty.proofOfApproval()),
            outputDocument
        );

        File outputFile;
        do {
            outputFile = new File(ui.requestOutputFilePath(inputFile + ".xades"));
        } while(outputFile.getAbsoluteFile().getParentFile() == null || !outputFile.getAbsoluteFile().getParentFile().exists() || !outputFile.getAbsoluteFile().getParentFile().canWrite());

        try {
            outputDocumentService.saveOutputDocument(outputDocument, outputFile);
        } catch (UnableToSaveException e) {
            ui.displayError(e.getMessage());
            return 1;
        }

        ui.confirmSignatureCreation(outputFile.getAbsolutePath());

        return 0;
    }
}
