package Tokenizer;

import Enums.Enums;
import java.util.Scanner;
import java.util.LinkedList;

public class Tokenizer {
    // tokenizes the entire file.
    public LinkedList<Enums.Token> scan() {
        String file = "";
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            file += " " + scanner.nextLine();
        }
        scanner.close();

        file = file.replaceAll("\\s+", "");

        LinkedList<Enums.Token> tokens = new LinkedList<Enums.Token>();
        String token = "";
        int position = 0;

        while (position < file.length()) {
            try {
                token = parseToken(file, position);
                position += token.length();
                tokens.add(tokenize(token));
            } catch (Error e) {
                throw e;
            }
        }

        tokens.add(Enums.Token.EOF);
        return tokens;
    }

    public Enums.Token tokenize(String token) {
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

    public String parseToken(String tokens, int position) {
        String token = "";
        while (position < tokens.length()) {
            token += tokens.charAt(position++);
            switch (token) {
                case "{":
                case "}":
                case "(":
                case ")":
                case ";":
                case "System.out.println":
                case "if":
                case "else":
                case "while":
                case "!":
                case "true":
                case "false":
                    return token;
            }
        }

        throw new Error("Parse error");
    }
}

