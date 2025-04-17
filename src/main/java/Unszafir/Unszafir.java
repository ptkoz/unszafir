package Unszafir;

import Unszafir.Cli.ListCommand;
import Unszafir.Cli.SignCommand;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import picocli.CommandLine;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import java.io.Console;

@CommandLine.Command(
    name = "unszafir",
    mixinStandardHelpOptions = true,
    description = "Unszafir ",
    subcommands = {CommandLine.HelpCommand.class}
)


public class Unszafir {
    public static void main(String[] args) {
        CliApplication cliApplication = DaggerCliApplication.create();
        System.exit(cliApplication.getCommandLine().execute(args));
    }
}

@Component(modules = {UnszafirModule.class})
interface CliApplication {
    CommandLine getCommandLine();
}

@Module
class UnszafirModule {
    @Provides
    CommandLine provideCommandLine(ListCommand listCommand, SignCommand signCommand) {
        //noinspection InstantiationOfUtilityClass
        return new CommandLine(new Unszafir())
            .addSubcommand(listCommand)
            .addSubcommand(signCommand);
    }

    @Provides
    @Nullable Console provideConsole() {
        return System.console();
    }

    @Provides
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
