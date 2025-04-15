package Unszafir;

import Unszafir.Cli.ListCommand;
import Unszafir.Cli.SignCommand;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Providers;
import picocli.CommandLine;

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
        Injector injector = Guice.createInjector(new UnszafirModule());

        System.exit(
            new CommandLine(injector.getInstance(Unszafir.class))
                .addSubcommand(injector.getInstance(ListCommand.class))
                .addSubcommand(injector.getInstance(SignCommand.class))
                .execute(args)
        );
    }
}

class UnszafirModule extends AbstractModule {
    private final DocumentBuilder documentBuilder;
    private final Transformer transformer;

    UnszafirModule() {
        try {
            this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.transformer = TransformerFactory.newInstance().newTransformer();
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void configure() {
        bind(Console.class).toProvider(Providers.of(System.console()));
        bind(DocumentBuilder.class).toInstance(documentBuilder);
        bind(Transformer.class).toInstance(transformer);
    }
}
