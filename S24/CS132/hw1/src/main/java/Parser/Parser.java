package Parser;

import Enums.Enums;
import java.util.LinkedList;

public class Parser {
    private LinkedList<Enums.Token> tokens;

    public Parser(LinkedList<Enums.Token> tokens) {
        this.tokens = tokens;
    }

    public void parse() {
        parseStmt(this.tokens.peek());
    }

    public void parseStmt(Enums.Token token) throws Error {
        switch (consume(token)) {
            // [{]L} | System.out.println(E); | if (E) S else S | while (E) S
            case LBRAC:
                parseList(next_token());            // {[L]}
                consume(Enums.Token.RBRAC);         // {L[}]
                return;

            // {L} | [System.out.println](E); | if (E) S else S | while (E) S
            case SYSOUT:
                consume(Enums.Token.LPAREN);        // System.out.println[(]E);
                parseExpr(next_token());            // System.out.println([E]);
                consume(Enums.Token.RPAREN);        // System.out.println(E[)];
                consume(Enums.Token.SEMICOLON);     // System.out.println(E)[;]
                return;

            // {L} | System.out.println(E); | [if] (E) S else S | while (E) S
            case IF:
                consume(Enums.Token.LPAREN);        // if [(]E) S else S
                parseExpr(next_token());            // if ([E]) S else S
                consume(Enums.Token.RPAREN);        // if (E[)] S else S
                parseStmt(next_token());            // if (E) [S] else S
                consume(Enums.Token.ELSE);          // if (E) S [else] S
                parseStmt(next_token());            // if (E) S else [S]
                return;

            // {L} | System.out.println(E); | if (E) S else S | [while] (E) S
            case WHILE:
                consume(Enums.Token.LPAREN);        // while [(]E) S
                parseExpr(next_token());            // while ([E]) S
                consume(Enums.Token.RPAREN);        // while (E[)] S
                parseStmt(next_token());            // while (E) [S]
                return;

            default:
                throw new Error("Parse error");
        }
    }

    public void parseList(Enums.Token token) throws Error {
        // S L | [\epsilon]
        if (getState(token) == Enums.State.LIST) {
            return;
        }

        if (getState(token) == Enums.State.EXPR) {
            throw new Error("Parse error");
        }

        parseStmt(next_token());    // [S] L | \epsilon
        parseList(next_token());    // S [L] | \epsilon
    }

    public void parseExpr(Enums.Token token) throws Error {
        switch(consume(token)) {
            // [true | false] | !E
            case TRUE:
            case FALSE:
                return;

            // true | false | [!]E
            case NOT:
                parseExpr(next_token());
                return;

            default:
                throw new Error("Parse error");
        }
    }

    // wrapper for .poll() so the code looks nice.
    public Enums.Token consume(Enums.Token token) throws Error {
        if (this.tokens.peek() != token) {
            throw new Error("Parse error");
        }

        return this.tokens.poll();
    }

    // wrapper for .peek() so the code looks nice.
    public Enums.Token next_token() throws Error {
        if (this.tokens.isEmpty()) {
            throw new Error("Parse error");
        }

        return this.tokens.peek();
    }

    public Enums.State getState(Enums.Token token) throws Error {
        switch (token) {
            case IF:
            case ELSE:
            case WHILE:
            case LBRAC:
            case LPAREN:
            case RPAREN:
            case SYSOUT:
            case SEMICOLON:
                return Enums.State.STMT;

            case RBRAC:
                return Enums.State.LIST;

            case NOT:
            case TRUE:
            case FALSE:
                return Enums.State.EXPR;

            default:
                throw new Error("Parse error");
        }
    }
}
