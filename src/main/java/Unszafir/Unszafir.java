package Unszafir;

import Unszafir.Cli.ListCommand;
import Unszafir.Cli.MainCommand;
import Unszafir.Cli.SignCommand;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import picocli.CommandLine;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import java.io.Console;

public class Unszafir {
    public static void main(String[] args) {
        CliApplication cliApplication = DaggerCliApplication.create();
        System.exit(cliApplication.getCommandLine().execute(args));
    }
}

@Component(modules = {UnszafirModule.class})
@Singleton
interface CliApplication {
    CommandLine getCommandLine();
}

@Module
class UnszafirModule {
    @Provides
    @Singleton
    CommandLine provideCommandLine(MainCommand mainCommand, ListCommand listCommand, SignCommand signCommand, VersionProvider versionProvider) {
        CommandLine cli = new CommandLine(mainCommand)
            .addSubcommand(listCommand)
            .addSubcommand(signCommand);

        cli.getCommandSpec()
            .name(versionProvider.getName())
            .version(versionProvider.getVersion());

        return cli;
    }

    @Provides
    @Singleton
    @Nullable
    Console provideConsole() {
        return System.console();
    }

    @Provides
    @Singleton
    DocumentBuilder provideDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    Transformer provideTransformer() {
        try {
            return TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}

class VersionProvider {
    private Package pkg;

    @Inject
    public VersionProvider() {
        this.pkg = Unszafir.class.getPackage();
    }

    public String getName() {
        return (pkg != null && pkg.getImplementationVersion() != null)
            ? pkg.getImplementationTitle()
            : "[app]";
    }

    public String getVersion() {
        String version = (pkg != null && pkg.getImplementationVersion() != null)
            ? pkg.getImplementationVersion()
            : "dev";

        return getName() + " " + version;
    }
}
