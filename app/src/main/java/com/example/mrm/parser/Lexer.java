package com.example.mrm.parser;
import java.util.regex.Matcher;
import java.util.LinkedList;
import java.util.regex.Pattern;

class Lexer{
    private static Tag[] tags = {
            Tag.KWD, Tag.ID, Tag.NUM, Tag.OP, Tag.PUNCT,
            Tag.LPAREN, Tag.RPAREN, Tag.WSPACE};
    private static int t_len = tags.length;
    static LL<Token> tokens(String input){
        LL<Token> ll = new LL<Token>();
        int i_len = input.length();
        boolean nextExists = true;
        int pos = 0;
        Matcher[] matchers = new Matcher[t_len];
        for(int i = 0; i < t_len; i++)
            matchers[i] = tags[i].pattern.matcher(input);
        while(nextExists){
            boolean hasMatch = false;
            for(int i = 0; i < t_len && !hasMatch; i++){
                Matcher m = matchers[i];
                m.region(pos, i_len);
                if(m.find()){
                    int new_pos = m.end();
                    hasMatch = true;
                    ll.push(new Token(tags[i], input.substring(pos, new_pos)));
                    pos = new_pos;
                }
            }
            nextExists = hasMatch;
        }
        return ll.reverse();
    }
}
class LL<T> extends LinkedList<T>{
    LL<T> reverse(){
        LL<T> temp = new LL<T>();
        for(T t : this){
            temp.push(t);
        }
        return temp;
    }
}

enum Tag{
    KWD("(sin|cos|tan|sqrt|abs)"),
    ID ("[a-zA-Z]+"),
    NUM ("[0-9]+(\\.[0-9]+)?"),
    OP ("(\\+|\\-|\\*|\\/|\\^)"),
    PUNCT(","),
    LPAREN ("\\("),
    RPAREN ("\\)"),
    WSPACE("( )+");
    Pattern pattern;
    Tag(String regex){
        pattern = Pattern.compile("^"+regex);
    }
    public String toString(){
        switch(this){
            case KWD: return "KWD";
            case ID: return "ID";
            case NUM: return "NUM";
            case OP: return "OP";
            case PUNCT: return "PUNCT";
            case LPAREN: return "LPAREN";
            case RPAREN: return "RPAREN";
            case WSPACE: return "WSPACE";
            default: return "unknown";
        }
    }
}

enum Assoc {RIGHT,LEFT,NONE}
class Token{
    Tag tag;
    String content;
    private int prec;
    Assoc assoc;
    int prec(){return prec;}
    Assoc assoc(){return assoc;}
    Token(Tag tag, String content){
        this.tag = tag; this.content = content;
        switch(content){
            case "+":
                prec = 2; assoc = Assoc.LEFT;
                break;
            case "-":
                prec = 2; assoc = Assoc.LEFT;
                break;
            case "neg":
                prec = 2; assoc = Assoc.RIGHT;
                break;
            case "*":
                prec = 3; assoc = Assoc.LEFT;
                break;
            case "/":
                prec = 3; assoc = Assoc.LEFT;
                break;
            case "^":
                prec = 4; assoc = Assoc.RIGHT;
                break;
            default:
                prec = 0; assoc = Assoc.NONE;
                break;
        }
    }
    @Override
    public String toString() {
        return String.format("<%s: %s>", tag.toString(), content);
    }
}
