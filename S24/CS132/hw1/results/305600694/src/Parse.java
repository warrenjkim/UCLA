import Enums.Enums;
import Tokenizer.Tokenizer;
import Parser.Parser;
import java.util.LinkedList;

public class Parse {
    public static void main(String[] args) {
        Tokenizer scanner = new Tokenizer();

        // scanning
        LinkedList<Enums.Token> tokens = new LinkedList<Enums.Token>();
        try {
            tokens = scanner.scan();
        } catch (Error e) {
            System.out.println(e.getMessage());
            return;
        }

        Parser parser = new Parser(tokens);
        // parsing
        try {
            parser.parse();
            parser.consume(Enums.Token.EOF);
        } catch (Error e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Program parsed successfully");
        return;
    }


}
