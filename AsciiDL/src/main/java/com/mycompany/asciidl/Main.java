
package com.mycompany.asciidl;

import com.mycompany.asciidl.AsciiDLParser.ProgramaContext;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.PrintWriter;

public class Main {
    
    public static void main(String args[]) throws IOException {
        
        try{
            CharStream cs = CharStreams.fromFileName(args[0]);
            /* Gera o lexer (Analisador Léxico)*/
            AsciiDLLexer lexer = new AsciiDLLexer(cs);
            /* Gera um ErrorListener pra reconhecer e escrever os erros léxicos e sintáticos ao rodar o Parser */
            AsciiDLErrorListener ErrorL = new AsciiDLErrorListener();
            
            /* CommonTokenStream usa o lexer para gerar os tokens */
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            
            /* Gera o Parser (Analisador sintático) com as regras sintáticas para ser usado junto com o ErrorL identificando erros*/
            AsciiDLParser parser = new AsciiDLParser(tokens);
            
            /*Remove o error listener padrão e adiciona o error listener customizado*/
            parser.removeErrorListeners();
            parser.addErrorListener(ErrorL);
            
            /*Chama a primeira regra para montar a árvore sintática*/
            ProgramaContext arvore = parser.programa();
            
            //Instancia o analisador semantico
            AsciiDLSemantico as = new AsciiDLSemantico();
            
            //Percorre a árvore gerada pelo analisador sintático com o analisador semântico
            as.visitPrograma(arvore);
            
            //Imprime no terminal os erros semanticos encontrados, se existirem
            if (AsciiDLSemanticoUtils.errosSemanticos.size() > 0){
                AsciiDLSemanticoUtils.errosSemanticos.forEach((s) -> System.out.println(s)); 
                System.out.println("Fim da compilacao");
            }
            else{
            //instancia o gerador de codigo
            AsciiDLGerador gera = new AsciiDLGerador();
            gera.visitPrograma(arvore);
            try(PrintWriter output = new PrintWriter(args[1])){
                output.print(gera.saida.toString());      
            }
            }
        }catch(IOException ex)
        {
            System.out.println("Nao foi possivel abrir o arquivo");
        }
    }
}