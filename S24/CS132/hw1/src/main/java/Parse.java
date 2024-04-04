import java.util.Scanner;
import java.util.Stack;
import java.util.LinkedList;

public class Parse {
    enum Token {
        SYSOUT,
        IF,
        ELSE,
        WHILE,
        TRUE,
        FALSE,
        NOT,
        LPAREN,
        RPAREN,
        LBRAC,
        RBRAC,
        SEMICOLON,
        EOF
    }

    enum State {
        STMT,
        LIST,
        EXPR
    }

    public static void main(String[] args) {
        LinkedList<Token> tokens = new LinkedList<Token>();
        try {
            tokens = scan();
        } catch (Error e) {
            System.out.println(e.getMessage());
            return;
        }

        try {
            parseStmt(tokens);
            if (consume(tokens) != Token.EOF) {
                System.out.println("Parse error");
                return;
            }
        } catch (Error e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Program parsed successfully");
        return;
    }


    public static void parseStmt(LinkedList<Token> tokens) {
        switch (consume(tokens)) {
            // [{] L } | System.out.println(E); | if (E) S else S | while (E) S
            case LBRAC:
                parseLBrac(tokens);
                return;

            // { L } | [System.out.println](E); | if (E) S else S | while (E) S
            case SYSOUT:
                parseSysout(tokens);
                return;

            // { L } | System.out.println(E); | [if] (E) S else S | while (E) S
            case IF:
                parseIf(tokens);
                return;

            // { L } | System.out.println(E); | if (E) S else S | [while] (E) S
            case WHILE:
                parseWhile(tokens);
                return;

            default:
                throw new Error("Parse error");
        }
    }

    public static void parseList(LinkedList<Token> tokens) {
        // S L | [\epsilon]
        if (getState(tokens.peek()) == State.LIST) {
            return;
        }

        if (getState(tokens.peek()) == State.EXPR) {
            throw new Error("Parse error");
        }

        // [S] L | \epsilon
        parseStmt(tokens);
        // S [L] | \epsilon
        parseList(tokens);
    }

    public static void parseExpr(LinkedList<Token> tokens) {
        switch(consume(tokens)) {
            // [true | false] | ! E
            case TRUE:
            case FALSE:
                return;

            // true | false | [!] E
            case NOT:
                parseExpr(tokens);
                return;

            default:
                throw new Error("Parse error");
        }
    }


    public static void parseLBrac(LinkedList<Token> tokens) {
        // { [L] } | \epsilon
        parseList(tokens);

        if (consume(tokens) != Token.RBRAC) {
            throw new Error("Parse error");
        }
    }

    public static void parseIf(LinkedList<Token> tokens) {
        // if [(]E) S else S
        if (consume(tokens) != Token.LPAREN) {
            throw new Error("Parse error");
        }

        // if ([E]) S else S
        parseExpr(tokens);

        // if (E[)] S else S
        if (consume(tokens) != Token.RPAREN) {
            throw new Error("Parse error");
        }

        // if (E) [S] else S
        parseStmt(tokens);

        // if (E) S [else] S
        if (consume(tokens) != Token.ELSE) {
            throw new Error("Parse error");
        }

        // if (E) S else [S]
        parseStmt(tokens);
    }

    public static void parseWhile(LinkedList<Token> tokens) {
        // while [(]E) S
        if (consume(tokens) != Token.LPAREN) {
            throw new Error("Parse error");
        }

        // while ([E]) S
        parseExpr(tokens);

        // while (E[)] S
        if (consume(tokens) != Token.RPAREN) {
            throw new Error("Parse error");
        }

        // while (E) [S]
        parseStmt(tokens);
    }

    public static void parseSysout(LinkedList<Token> tokens) {
        // System.out.println[(]E);
        if (consume(tokens) != Token.LPAREN) {
            throw new Error("Parse error");
        }

        // System.out.println([E]);
        parseExpr(tokens);

        // System.out.println(E[)];
        if (consume(tokens) != Token.RPAREN) {
            throw new Error("Parse error");
        }

        // System.out.println(E)[;]
        if (consume(tokens) != Token.SEMICOLON) {
            throw new Error("Parse error");
        }
    }

    // wrapper for .poll() so the code looks nice.
    public static Token consume(LinkedList<Token> tokens) {
        return tokens.poll();
    }

    // tokenizes the entire file.
    public static LinkedList<Token> scan() {
        String file = "";
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            file += " " + scanner.nextLine();
        }
        scanner.close();

        file = file.replaceAll("\\s+", "");

        LinkedList<Token> tokens = new LinkedList<Token>();

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

        tokens.add(Token.EOF);
        return tokens;
    }

    // string -> token.
    public static Token tokenize(String token) {
        switch (token) {
            case "{":
                return Token.LBRAC;
            case "}":
                return Token.RBRAC;
            case "(":
                return Token.LPAREN;
            case ")":
                return Token.RPAREN;
            case ";":
                return Token.SEMICOLON;
            case "System.out.println":
                return Token.SYSOUT;
            case "if":
                return Token.IF;
            case "else":
                return Token.ELSE;
            case "while":
                return Token.WHILE;
            case "!":
                return Token.NOT;
            case "true":
                return Token.TRUE;
            case "false":
                return Token.FALSE;
        }

        throw new Error("Parse error");
    }

    public static String parseToken(String tokens, int position) {
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

    public static State getState(Token token) {
        switch (token) {
            case IF:
            case ELSE:
            case WHILE:
            case LBRAC:
            case LPAREN:
            case RPAREN:
            case SYSOUT:
            case SEMICOLON:
                return State.STMT;
            case RBRAC:
                return State.LIST;
            case NOT:
            case TRUE:
            case FALSE:
                return State.EXPR;
        }

        throw new Error("Parse error");
    }
}
