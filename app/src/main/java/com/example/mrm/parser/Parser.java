package com.example.mrm.parser;

class ShuntingYard{
    LL<Token> out;
    LL<Token> stack;
    boolean negPos, appPos,multPos;
    ShuntingYard(){
        out = new LL<Token>();
        stack = new LL<Token>();
        negPos = true;
        appPos = false;
        multPos = false;
    }
    void reset(){
        out.clear();
        stack.clear();
        negPos = true;
        appPos = false;
        multPos = false;
    }
    void readTokens(LL<Token> ll){
        for(Token t : ll){
            readToken(t);
        }
        finish();
        out = out.reverse();
    }

    void read(String input){
        readTokens(Lexer.tokens(input));
    }

    String results(){
        String str = "";
        for (Token t : out)
            str = str + " " + t.toString();
        return str;
    }

    void finish(){
        for(Token t : stack){
            out.push(t);
        }
    }
    void readToken(Token tok){
        Tag tag = tok.tag;
        switch(tag){
            case WSPACE: break;
            case NUM:
            case ID:
                if(multPos)
                    readToken(new Token(Tag.OP,"*"));
                out.push(tok);
                negPos = false;
                multPos = true;
                break;
            case OP:
                if(tok.content.equals("-") && negPos)
                    tok = new Token(Tag.OP, "neg");
                if(!stack.isEmpty()){
                    Token o = stack.peek();
                    if(o.tag == Tag.OP &&
                            ((tok.assoc == Assoc.LEFT) && (tok.prec() <= o.prec())) ||
                            (tok.assoc == Assoc.RIGHT) && (tok.prec() <  o.prec()))
                        out.push(stack.pop());
                }
                stack.push(tok);
                negPos = true;
                multPos = false;
                break;
            case KWD:
                if(multPos)
                    readToken(new Token(Tag.OP,"*"));
                appPos = true;
                multPos = false;
                out.push(tok);
                break;
            case LPAREN:
                if(appPos /* !out.isEmpty() && out.peek().tag == Tag.KWD */)
                    stack.push(new Token(Tag.OP, "app"));
                else if (multPos)
                    readToken(new Token(Tag.OP,"*"));

                stack.push(tok);

                negPos = true;
                appPos = false;
                multPos = false;
                break;
            case RPAREN:
                Token t = stack.pop();
                while(t.tag != Tag.LPAREN){
                    out.push(t);
                    t = stack.pop();
                }
                negPos = false;
                multPos = true;
                break;
        }
    }
}

public class Parser{
    LL<Expr> stack;
    ShuntingYard sy;
    public Parser(){
        stack = new LL<Expr>();
        sy = new ShuntingYard();
    }
    public void reset(){
        stack.clear();
        sy.reset();
    }
    void read(Token tok){
        Expr term = new Const(0f);
        Tag tag = tok.tag;
        switch(tag){
            case NUM:
                term = new Const(Float.parseFloat(tok.content));
                break;
            case ID:
                term = new Var(0);
                break;
            case KWD:
                switch(tok.content){
                    case "neg": term = PrimFun.NEG; break;
                    case "sin": term = PrimFun.SIN; break;
                    case "cos": term = PrimFun.COS; break;
                    case "tan": term = PrimFun.TAN; break;
                    case "abs": term = PrimFun.ABS; break;
                    case "sqrt": term = PrimFun.SQRT; break;

                    default: break;
                }
                break;
            case OP:
                if(tok.content.equals("neg")){
                    term = Ex.A(PrimFun.NEG, stack.pop());
                } else {
                    Expr r = stack.pop(), l = stack.pop();
                    switch(tok.content){
                        case "+": term = Ex.A(PrimFun.ADD, l, r); break;
                        case "-": term = Ex.A(PrimFun.SUB, l,r); break;
                        case "*": term = Ex.A(PrimFun.MULT, l,r); break;
                        case "/": term = Ex.A(PrimFun.DIV, l,r); break;
                        case "^": term = Ex.A(PrimFun.POW, l,r); break;
                        case "app": term = Ex.A((Fun)l, r); break;
                        default: break;
                    }
                }
                break;
            default: break;
        }
        stack.push(term);
    }

    public Expr parse(String input){
        Expr expr;
        if(input.length() == 0){
            expr = Ex.C(0f);
        } else {
            sy.read(input);
            for(Token t : sy.out){
                read(t);
            }

            expr = stack.pop();
        }
        reset();
        return expr;
    }

}