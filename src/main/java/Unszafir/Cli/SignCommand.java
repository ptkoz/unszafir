package Unszafir.Cli;

import Unszafir.Helpers.CertificateExceptionHandler;
import Unszafir.Signatures.*;
import com.google.inject.Inject;
import org.w3c.dom.Document;
import picocli.CommandLine;
import xades4j.production.*;
import xades4j.properties.DataObjectDesc;
import xades4j.providers.KeyingDataProvider;

import java.io.File;
import java.security.ProviderException;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "sign",
    description = "Create signature for given file."
)
public class SignCommand implements Callable<Integer> { ;
    @CommandLine.Option(names = {"-m", "--module"}, description = "Path to PKCS#11 library.", required = true)
    String pkcs11Module;

    @CommandLine.Option(names = {"-s", "--slot"}, description = "The index of the PKCS#11 slot to use, default is 0.", defaultValue = "0")
    int pkcs11SlotIndex;

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
        KeyingDataProvider keyingDataProvider = certificateProviderFactory.createKeyingDataProvider(pkcs11Module, pkcs11SlotIndex);
        Document outputDocument = outputDocumentService.createOutputDocument();
        XadesSigner signer = signerFactory.createXadesSigner(keyingDataProvider);
        DataObjectDesc dataToSign;

        try {
            dataToSign = inputDocumentFactory.createDataObjectForFile(inputFile, outputDocument);
        } catch(InvalidFileException e) {
            ui.displayError(e.getMessage());
            return 1;
        }

        try {
            if (!ui.promptSignatureCreation(keyingDataProvider.getSigningCertificateChain().getFirst(), inputFile)) {
                ui.confirmCancellation();
                return 0;
            }
            ui.confirmSigning();;
        } catch (ProviderException e) {
            String message = CertificateExceptionHandler.getMessageForProviderException(e, pkcs11SlotIndex);
            if (message != null) {
                ui.displayError(message);
                return 1;
            }

            throw e;
        }

        signer.sign(
            new SignedDataObjects(dataToSign),
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
