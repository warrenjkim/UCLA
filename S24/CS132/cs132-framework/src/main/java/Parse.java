import java.util.Scanner;

public class Parse {
    enum State {
        S,
        L,
        E
    }

    public static void main(String [] args) {
        String tokens = "";
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            tokens += " " + scanner.nextLine();
        }
        scanner.close();

        tokens = tokens.replaceAll("\\s+", "");
        tokens = tokens.trim();
        // System.out.println(tokens);

        State state = State.S;
        String token = "";
        for (int i = 0; i < tokens.length(); i++) {
            try {
                state = getState(token);
            } catch (Exception e) {
                // System.out.println(e.getMessage());
                token += tokens.charAt(i);
                continue;
            } catch (Error e) {
                // System.out.println(e.getMessage());
                return;
            }

            switch (state) {
                case S:
                    try {
                    i = parseS(token, tokens, i);
                    } catch (Error e) {
                        // System.out.println(e.getMessage());
                        return;
                    }
                    token = "";
                    break;
                case L:
                    break;
                case E:
                    break;
            }
        }

        System.out.println("Program parsed successfully");
        return;
    }

    public static int parseS(String token, String tokens, int position) {
        if (token.equals("{")) {
            position = parseL(tokens, position);
            if (tokens.charAt(position) != '}') {
                throw new Error("Parse error");
            }

            return position;
        }

        if (tokens.charAt(position) != '(') {
            // System.out.println(tokens.charAt(position));
            throw new Error("Parse error");
        }

        position = parseE(tokens, position + 1);

        switch (token) {
            case "System.out.println":
                if (!tokens.substring(position, position + 2).equals(");")) {
                    System.out.println(tokens.substring(position, position + 2));
                    throw new Error("Parse error");
                }
                return position + 2;

            case "if":
                if (!parseToken(tokens, position).equals(")")) {
                    throw new Error("Parse error");
                }

                token = parseToken(tokens, position + 1);
                // System.out.println(token);
                position = parseS(token, tokens, position + token.length() + 1);
                if (!tokens.substring(position, position + 4).equals("else")) {
                    throw new Error("Parse error (if)");
                }

                return position;

            case "else":
                // System.out.println(token);
                if (!parseToken(tokens, position).equals(")")) {
                    throw new Error("Parse error");
                }

                token = parseToken(tokens, position + 1);
                position = parseS(token, tokens, position + token.length());
                return position;

            case "while":
                token = parseToken(tokens, position + 1);
                position = parseS(token, tokens, position + token.length() + 1);
                return position;
        }


        return position;
    }

    public static int parseL(String tokens, int position) {
        while (true) {
            String token = parseToken(tokens, position);
            State state = State.L;
            try {
                state = getState(token);
            } catch (Exception e) {

            } catch (Error e) {
                throw e;
            }

            switch (state) {
                case S:
                    position = parseS(token, tokens, position + token.length());
                    break;
                case L:
                    return position;
                case E:
                    throw new Error("Parse error");
            }
        }
    }

    public static int parseE(String tokens, int position) {
        String token = parseToken(tokens, position);
        try {
            if (getState(token) == State.E) {
                return position + token.length();
            }
        } catch (Exception e) {

        }

        throw new Error("Parse error");
    }

    public static State getState(String token) throws Exception {
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
            case "!true":
            case "!false":
                return State.E;
            default:
                throw new Exception("Invalid token: " + token);
        }
    }


    public static String parseToken(String tokens, int position) {
        String token = "";
        for (; position < tokens.length(); position++) {
            try {
                getState(token);
                if (token.equals("!")) {
                    token += tokens.charAt(position);
                    continue;
                }
            } catch (Exception e) {
                // System.out.println(e.getMessage());
                token += tokens.charAt(position);
                continue;
            } catch (Error e) {
                // System.out.println(e.getMessage());
                return "";
            }

            break;
        }

        return token;
    }
}
