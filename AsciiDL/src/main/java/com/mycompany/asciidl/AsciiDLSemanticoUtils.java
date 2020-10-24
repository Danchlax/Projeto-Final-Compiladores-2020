
package com.mycompany.asciidl;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;

public class AsciiDLSemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();

    //metodo para compor uma lista de erros semanticos que serão impressos no main após o fim da análise semântica
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        int coluna = t.getCharPositionInLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }
    //metodo para verificar o tipo de uma expressao
    public static String verificarTipo(TabelaDeSimbolos tabela, AsciiDLParser.ExpressaoContext ctx){
        String ret = null;
        String aux;
        int i;
        for(i = 0; i < ctx.termo().size(); i++)
        {   //verifica o tipo de cada termo na expressao
            aux = verificarTipo(tabela, ctx.termo(i));
            if(ret == null)
            {
                ret = aux;
            }else if(!ret.equals(aux) && !aux.equals("invalido"))
            {//retorna invalido se os tipos dos termos sao diferentes
                ret = "invalido";
            }
        }
        return ret;
    }
    //metodo para verificar o tipo de um termo
    public static String verificarTipo(TabelaDeSimbolos tabela, AsciiDLParser.TermoContext ctx){
        String ret = null;
        String aux;
        int i;
        for(i = 0; i < ctx.fator().size(); i++)
        {   //verifica o tipo de cada fator no termo
            aux = verificarTipo(tabela, ctx.fator(i));
            if(ret == null)
            {
                ret = aux;
            }else if(!ret.equals(aux) && !aux.equals("invalido"))
            {   //retorna invalido se os tipos dos fatores sao diferentes
                ret = "invalido";
            }
        }
        return ret;
    }
    //metodo para verificar o tipo de um fator
    public static String verificarTipo(TabelaDeSimbolos tabela, AsciiDLParser.FatorContext ctx){
        String ret;
        
        if(ctx.Identificador() != null)
        {   //se for um identificador, olha se esta na tabela de simbolos e, se esta, extrai o seu tipo
            String NomeVar = ctx.Identificador().getText();
            if(tabela.existe(NomeVar))
            {
                ret = tabela.verificar(NomeVar);
            }else
            {   //retorna tipo invalido se a variavel nao foi declarada
                adicionarErroSemantico(ctx.Identificador().getSymbol(), "variavel " + NomeVar + " nao declarada");
                return "invalido";
            }
        }else if(ctx.Num_Inteiro() != null)
        {   //se for um numero inteiro retona tipo inteiro
            ret = "inteiro";
        }else{
            //se for uma expressao entre parenteses verifica o tipo dela
            ret = verificarTipo(tabela, ctx.expressao());
        }
        
        return ret;
    }
}
