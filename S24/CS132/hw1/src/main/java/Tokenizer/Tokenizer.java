package Tokenizer;

import Enums.Enums;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    // tokenizes the entire file.
    public LinkedList<Enums.Token> scan() throws Error {
        String file = "";
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            file += " " + scanner.nextLine();
        }
        scanner.close();

        // split by whitespace
        String [] words = file.split("\\s+");

        // (E), {L}, !E -> [(, E, )], [{, L, }], [!, E]
        Pattern pattern = Pattern.compile("!\\B|[^\\s!(){}]+|[\\s(){}]");

        // split by pattern
        ArrayList<String> tokenStrs = new ArrayList<String>();
        for (String word : words) {
            Matcher matcher = pattern.matcher(word);
            while (matcher.find()) {
                tokenStrs.add(matcher.group());
            }
        }

        // tokenize input
        LinkedList<Enums.Token> tokens = new LinkedList<Enums.Token>();
        for (String token : tokenStrs) {
            tokens.add(tokenize(token));
        }

        tokens.add(Enums.Token.EOF);
        return tokens;
    }

    public Enums.Token tokenize(String token) throws Error {
        switch (token) {
            case "{":
                return Enums.Token.LBRAC;
            case "}":
                return Enums.Token.RBRAC;
            case "(":
                return Enums.Token.LPAREN;
            case ")":
                return Enums.Token.RPAREN;
            case ";":
                return Enums.Token.SEMICOLON;
            case "System.out.println":
                return Enums.Token.SYSOUT;
            case "if":
                return Enums.Token.IF;
            case "else":
                return Enums.Token.ELSE;
            case "while":
                return Enums.Token.WHILE;
            case "!":
                return Enums.Token.NOT;
            case "true":
                return Enums.Token.TRUE;
            case "false":
                return Enums.Token.FALSE;
        }

        throw new Error("Parse error");
    }
}

