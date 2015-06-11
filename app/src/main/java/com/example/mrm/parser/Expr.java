package com.example.mrm.parser;

interface Fun{
    float apply(float... xs);
    int arity();
}


enum PrimFun implements Fun, Expr {
    NEG (1), ABS (1), SIN (1), COS (1), TAN (1), SQRT (1),
    ADD (2), SUB (2), MULT (2), DIV (2), POW (2);
    private int arity;
    public float eval(float... xs){
        return 0f;
    }
    PrimFun(int arity){
        this.arity = arity;
    }
    public float apply(float... xs){
        switch(this){
            case NEG: return - xs[0];
            case ABS: return Math.abs(xs[0]);
            case SIN: return (float) Math.sin(xs[0]);
            case COS: return (float) Math.cos(xs[0]);
            case TAN: return (float) Math.tan(xs[0]);
            case SQRT: return (float) Math.sqrt(xs[0]);
            case ADD: return xs[0] + xs[1];
            case SUB: return xs[0] - xs[1];
            case MULT: return xs[0] * xs[1];
            case DIV: return xs[0] / xs[1];
            case POW: return (float) Math.pow(xs[0], xs[1]);
        }
        return Float.NaN;
    }
    public int arity(){
        return arity;
    }
}

public interface Expr{
    float eval(float... env);
}


class Const implements Expr{
    float value;
    Const(float value){
        this.value = value;
    }
    public float eval(float... sto){
        return value;
    }
}

class Var implements Expr{
    int index;
    Var(int index){
        this.index = index;
    }
    public float eval(float... sto){
        return sto[index];
    }
}

class App implements Expr{
    Fun f;
    private int arity;
    Expr[] subterms;
    App(Fun f, Expr... xs){
        this.arity = f.arity();
        this.f = f; subterms = xs;
    }
    public float eval(float... sto){
        switch(arity) {
            case 1:
                return f.apply(subterms[0].eval(sto));
            case 2:
                return f.apply(subterms[0].eval(sto), subterms[1].eval(sto));
            default:
                float[] ret = new float[arity];
                for (int i = 0; i < arity; i++)
                    ret[i] = subterms[i].eval(sto);
                return f.apply(ret);
        }
    }
}

class Ex{
    static Const C(float x) {
        return new Const(x);
    }
    static Var V(int index){
        return new Var(index);
    }
    static App A(Fun f, Expr... xs){
        return new App(f, xs);
    }
}