import java.util.Scanner;

public class Parse {
    enum State {
        S,
        L,
        E
    }

    public static void main(String[] args) {
        String tokens = "";
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            tokens += " " + scanner.nextLine();
        }
        scanner.close();

        tokens = tokens.replaceAll("\\s+", "");
        tokens = tokens.trim();

        String token = "";
        try {
            token = parseToken(tokens, 0);
        } catch (Error e) {
            System.out.println(e.getMessage());
            return;
        }
        int position = token.length();
        State state = getState(token);

        while (position < tokens.length()) {
            switch (state) {
                case S:
                    try {
                        position = parseS(token, tokens, position);
                    } catch (Error e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                    break;

                case L:
                case E:
                    System.out.println("Parse error");
                    return;
            }
        }

        System.out.println("Program parsed successfully");
    }

    public static int parseS(String token, String tokens, int position) {
        // beginning of L statement
        if (token.equals("{")) {
            position = parseL(tokens, position);
            if (tokens.charAt(position) != '}') {
                throw new Error("Parse error");
            }

            return position + 1;
        }

        // all S grammars that do not take the form { L } take the form
        // token [(E)] tokens
        if (tokens.charAt(position) != '(') {
            throw new Error("Parse error");
        }

        // token [(E)]
        position = parseE(tokens, position + 1);

        // Not of the form '(E)' => error
        if (tokens.charAt(position) != ')') {
            throw new Error("Parse error");
        }
        position += 1;

        switch (token) {
            // System.out.println(E);
            case "System.out.println":
                // System.out.println(E)[;]
                if (tokens.charAt(position) != ';') {
                    throw new Error("Parse error");
                }

                return position + 1;

            // if (E) S else S
            case "if":
                // if (E) [S]
                token = parseToken(tokens, position);

                // Not of the form 'if (E) S' => error
                if (getState(token) != State.S) {
                    throw new Error("Parse error");
                }

                // consume token
                position = parseS(token, tokens, position + token.length());

                // if (E) S [else]
                token = parseToken(tokens, position);

                // Not of the form 'if (E) S else' => error
                if (!token.equals("else")) {
                    throw new Error("Parse error");
                }

                // consume token
                position += token.length();

                // if (E) S else [S]
                token = parseToken(tokens, position);

                // Not of the form 'if (E) S else S' => error
                if (getState(token) != State.S) {
                    throw new Error("Parse error");
                }

                // return the final position after the if-else statement.
                return parseS(token, tokens, position + token.length());

            // while (E) S
            case "while":
                // while (E) [S]
                token = parseToken(tokens, position);

                if (getState(token) != State.S) {
                    throw new Error("Parse error");
                }

                return parseS(token, tokens, position + token.length());
        }

        throw new Error("Parse error");
    }

    public static int parseL(String tokens, int position) {
        String token = "";
        token = parseToken(tokens, position);

        if (getState(token) == State.E) {
            throw new Error("Parse error");
        }

        if (getState(token) == State.S) {
            position = parseS(token, tokens, position + token.length());
            position = parseL(tokens, position);
        }

        return position;
    }

    public static int parseE(String tokens, int position) {
        String token = "";
        token = parseToken(tokens, position);
        position += token.length();

        if (getState(token) != State.E) {
            throw new Error("Parse error");
        }

        if (token.equals("!")) {
            position = parseE(tokens, position);
        }

        return position;
    }

    public static String parseToken(String tokens, int position) {
        String token = "";
        for (; position < tokens.length(); position++) {
            token += tokens.charAt(position);
            if (isToken(token)) {
                return token;
            }
        }

        throw new Error("Parse error");
    }

    public static boolean isToken(String token) {
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
                return true;
        }

        return false;
    }

    public static State getState(String token) {
        switch (token) {
            case "{":
            case "(":
            case ")":
            case ";":
            case "System.out.println":
            case "if":
            case "else":
            case "while":
                return State.S;

            case "}":
                return State.L;

            case "!":
            case "true":
            case "false":
                return State.E;

            default:
                throw new Error("Parse error");
        }
    }
}
