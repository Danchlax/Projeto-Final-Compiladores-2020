/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.asciidl;

/**
 *
 * @author Roger
 */
public class AsciiDLGerador extends AsciiDLBaseVisitor <Void>{
    StringBuilder saida;
    TabelaDeSimbolos tabela;
    int contador;
    int contaarquivos;
    
    public AsciiDLGerador(){
        saida = new StringBuilder();
        this.tabela = new TabelaDeSimbolos();
        this.contador = 0;
        this.contaarquivos=0;
    }
    @Override
    public Void visitPrograma(AsciiDLParser.ProgramaContext ctx )  {
        /*declara bibliotecas, main e chama declações globais e locais*/
        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n");
        saida.append("#include <string.h>\n");
        saida.append("\n");
        saida.append("int main() {\n");
        saida.append("char *quebra = \"\\n\";\n" );
        ctx.corpo().comando().forEach(dec -> visitComando(dec));
        saida.append("\n");
        int i;
        for(i=1;i<=contaarquivos;i++){
        saida.append("fclose("+"A" + i + "pointer" + ");\n");    
        }
        saida.append("return 0;\n }");
        return null;
    }
    //Declara as variaveis, macros são lidados como um ponteiro para char
    //Tabelas são lidadas como uma matriz de char, porém talvez seja mais prático implementar de outra forma
   
    @Override
    public Void visitCmdDeclaracao(AsciiDLParser.CmdDeclaracaoContext ctx){
            if (ctx.decl_output()!=null){
                int i;
                for (i=0;i<ctx.decl_output().Identificador().size();i++){
                    contaarquivos++;
                    saida.append("FILE *A" + contaarquivos + "pointer;\n");
                    saida.append("A" + contaarquivos + "pointer" + " =fopen(" + "\"A" + contaarquivos + "file.txt\"" + ",\"a+\"" +  ");\n");
                    saida.append("int loopauxiliar" + contador + ";\n");
                    tabela.adicionar(ctx.decl_output().Identificador(i).getText() + "bg","\" \"");
                    saida.append("char " + ctx.decl_output().Identificador(i).getText() + "[" + ctx.decl_output().dimensao().expressao(0).getText() + "+1]" + "[" + ctx.decl_output().dimensao().expressao(1).getText() + "]" + ";\n");
                    tabela.adicionar(ctx.decl_output().Identificador(i).getText(),0);
                    tabela.adicionar(ctx.decl_output().Identificador(i).getText()+ "coluna",0);
                    tabela.adicionar(ctx.decl_output().Identificador(i).getText() + "dimensao1",ctx.decl_output().dimensao().expressao(0).getText());
                    tabela.adicionar(ctx.decl_output().Identificador(i).getText() + "dimensao2",ctx.decl_output().dimensao().expressao(1).getText());
                    saida.append("for(loopauxiliar" +  contador + "=0;loopauxiliar" + contador + " <" + ctx.decl_output().dimensao().expressao(1).getText() + ";loopauxiliar" + contador + "++){\n");
                    saida.append("memset(" + ctx.decl_output().Identificador(i).getText()+ "[loopauxiliar" + contador +"]" + ", ' '," + ctx.decl_output().dimensao().expressao(0).getText() + ");\n" );
                    saida.append(ctx.decl_output().Identificador(i).getText() + "[" + ctx.decl_output().dimensao().expressao(0).getText() + "+1][loopauxiliar" + contador+ "]=\'\\0\';\n}\n" );
                    contador++;
                    
            } 
            } else
            if (ctx.decl_var()!=null){
                int i;
                if(ctx.decl_var().tipo_var().getText().equals("inteiro")){
                    for (i=0;i<ctx.decl_var().Identificador().size();i++){
                        saida.append("int "  + ctx.decl_var().Identificador(i).getText() + ";\n");
                    }
                }
                if(ctx.decl_var().tipo_var().getText().equals("macro")){
                    for (i=0;i<ctx.decl_var().Identificador().size();i++){
                        tabela.adicionar(ctx.decl_var().Identificador(i).getText(), 0);
                        saida.append("char *"  + ctx.decl_var().Identificador(i).getText() + "= \".\";\n");
                    } 
                }
            }
        
        return null;
    }
    //Atribui um macro no outro ou uma string em um macro ou uma expressão de loop entre parenteses em um macro e altera
    // o tamanho do macro na tabela.
    @Override 
    public Void visitCmdAtribuicao(AsciiDLParser.CmdAtribuicaoContext ctx)
    {
        if (ctx.cadeia()!=null){
            if (ctx.cadeia().expressao()!= null){
                saida.append("int loopauxiliar" + contador + ";\n");
                saida.append("int " + ctx.Identificador() + "expressao" + contador + " = " + ctx.cadeia().expressao().getText() +  ";\n");
                if (ctx.cadeia().String()!=null){
                tabela.modificar(ctx.Identificador().getText(), ctx.cadeia().String().getText().length() + Integer.parseInt(ctx.cadeia().expressao().getText()));    
                saida.append("char " + ctx.Identificador() + "buffer" + contador+"[" + ctx.Identificador() + "expressao" + contador + " * strlen(" + ctx.cadeia().String().getText() + ")]"  + ";\n");
                saida.append("strcpy(" + ctx.Identificador() + "buffer" + contador + "," + ctx.cadeia().String().getText() + ");\n");
                saida.append("for(loopauxiliar" + contador + "=1;loopauxiliar"+contador+"<" + ctx.Identificador() + "expressao" + contador + ";loopauxiliar" + contador + "++){\n");
                saida.append("strcat("+ ctx.Identificador() + "buffer" + contador + "," + ctx.cadeia().String().getText() + ");\n}\n" );
                }
                else if (ctx.cadeia().Identificador()!=null){
                tabela.modificar(ctx.Identificador().getText(),tabela.verificarLinha(ctx.cadeia().Identificador().getText()));
                saida.append("char " + ctx.Identificador() + "buffer" + contador+ "[" + ctx.Identificador() + "expressao" + contador + " * strlen(" + ctx.cadeia().Identificador().getText() + ")]"  + ");\n");
                saida.append("strcpy(" + ctx.Identificador() + "buffer" + contador + "," + ctx.Identificador() + ");\n");
                saida.append("for(loopauxiliar" + contador + "=1;loopauxilir"+ contador + "<" + ctx.Identificador() + "expressao" + contador + ";loopauxiliar" + contador + "++){\n");
                saida.append("strcat("+ ctx.Identificador() + "buffer" + contador + "," + ctx.Identificador().getText() + ");\n}\n" );    
                }
                saida.append(ctx.Identificador().getText() + " = " + ctx.Identificador() + "buffer" + contador + ";\n");
                contador++;    
                }
            else if(ctx.cadeia().String()!=null){
                tabela.modificar(ctx.Identificador().getText(), ctx.cadeia().String().getText().length());
                saida.append(ctx.Identificador().getText() + " = " + ctx.cadeia().String() + ";\n");
            }
            }
               
            
        
        return null;
    }
    //Adiciona na tabela de simbolos o background para poder ser usado no resto do programa e também cria um preset da tabela preenchida.
    @Override
    public Void visitCmdSetbg(AsciiDLParser.CmdSetbgContext ctx){
        saida.append("int loopauxiliar" + contador +";\n");
        tabela.modificar(ctx.Identificador().getText() + "bg", ctx.Caractere().toString());
        String dimensao1 = tabela.verificar(ctx.Identificador().getText() + "dimensao1");
        String dimensao2 = tabela.verificar(ctx.Identificador().getText() + "dimensao2");
        saida.append("for(loopauxiliar" +  contador + "=0;loopauxiliar" + contador + " <" + dimensao2 + ";loopauxiliar" + contador + "++){\n");
        saida.append("memset(" + ctx.Identificador().getText()+ "[loopauxiliar" + contador +"]" + ",\'" + ctx.Caractere().getText().charAt(1) + "\'," + dimensao1 + ");\n" );
        saida.append(ctx.Identificador().getText() + "[" + dimensao1 + "+1][loopauxiliar" + contador+ "]=\'\\0\';\n}\n" );
        contador++;
        return null;
    }
    //Concatena dois macros, ou 1 macro e 1 string ou 1 macro e 1 expressão de loop, mudando o tamanho na tabela
    @Override
    public Void visitCmdAdd(AsciiDLParser.CmdAddContext ctx){
        if (ctx.cadeia()!=null){
        tabela.modificar(ctx.Identificador(0).getText(), ctx.cadeia().getText().length() - 1 + tabela.verificarLinha(ctx.Identificador(0).getText()));
        saida.append("char AddBuffer" + contador + "[strlen(" + ctx.cadeia().getText()+ ") + strlen(" + ctx.Identificador(0) + ")];\n");
        saida.append("strcpy(AddBuffer" + contador + "," + ctx.Identificador(0)+ ");\n");
        saida.append("strcat(AddBuffer" + contador + "," + ctx.cadeia().getText()+ ");\n");
        saida.append(ctx.Identificador(0) + " = " + "AddBuffer" + contador + ";\n");
        contador++;

        } 
        else{
        tabela.modificar(ctx.Identificador(1).getText(), tabela.verificarLinha(ctx.Identificador(1).getText()) -1 + tabela.verificarLinha(ctx.Identificador(0).getText()));
        saida.append("char AddBuffer" + contador + "[strlen(" + ctx.Identificador(1)+ ") + strlen(" + ctx.Identificador(0) + ")];\n");
        saida.append("strcpy(AddBuffer" + contador + "," + ctx.Identificador(1)+ ");\n");
        saida.append("strcat(AddBuffer" + contador + "," + ctx.Identificador(0)+ ");\n");
        saida.append(ctx.Identificador(1) + " = " + "AddBuffer" + contador + ";\n");
        contador++;
        }
        return null;
    }

    //Aqui começa os prints
    @Override
    public Void visitCmdPrint(AsciiDLParser.CmdPrintContext ctx){
        switch(ctx.impressor().getText()){
            //println está funcionando, mas tem uns bugs com background
            case "println":
                if (ctx.expressao()!= null){
                    if (ctx.Identificador(1)!=null){
                        int linha = tabela.verificarLinha(ctx.Identificador(1).getText());
                        int coluna = tabela.verificarLinha(ctx.Identificador(1).getText()+"coluna");
                        String bg = tabela.verificar(ctx.Identificador(1).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        String expressao = "int expressao" + contador + "="+ ctx.expressao().getText() +";\n";
                        String expressao1 = "expressao" + contador;
                        saida.append(expressao);
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(1)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        saida.append("strcpy(bufferPrint" + contador + "," + ctx.Identificador(0).getText() + ");\n");
                        saida.append("strcat(bufferPrint" + contador + ",\"\\n\" );\n");
                        saida.append("for(" + aux1 + "=0;" + aux1 + "<" +  expressao1 + "-1;" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + ctx.Identificador(0).getText() + ");\n");
                        saida.append("strcat(bufferPrint" + contador + ",\"\\n\" );\n");
                        saida.append("}\n");
                        saida.append("strcpy(" + ctx.Identificador(1).getText()+ "[" + linha + "]" + ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(1).getText()+ "[" + linha + "]" + "," + ctx.Identificador(1).getText() + "pointer);\n");
                        int colunaNova= Integer.parseInt(ctx.expressao().getText())*tabela.verificarLinha(ctx.Identificador(0).getText());
                        tabela.modificar(ctx.Identificador(1).getText()+"coluna",coluna + colunaNova);
                        if (coluna + colunaNova >= tabela.verificarLinha(ctx.Identificador(1).getText()+"dimensao1")){
                        
                        tabela.modificar(ctx.Identificador(1).getText()+"coluna",0);
                        tabela.modificar(ctx.Identificador(1).getText(), linha+1);
                        }
                        contador++;
                    }
                    else if(ctx.cadeia().expressao()!=null){
                        int linha = tabela.verificarLinha(ctx.Identificador(0).getText());
                        int coluna = tabela.verificarLinha(ctx.Identificador(0).getText()+"coluna");
                        String bg = tabela.verificar(ctx.Identificador(0).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        String expressao = "int expressao" + contador + "="+ ctx.expressao().getText()+";\n";
                        String expressao1 = "expressao" + contador;
                        saida.append(expressao);
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(0)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        saida.append("strcpy(bufferPrint" + contador + "," + ctx.cadeia().String().getText() + ");\n");
                        saida.append("for(" + aux1 + "=0;" + aux1 + "<(" +  expressao1 + "-1)*"+ ctx.cadeia().expressao().getText() + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + ctx.cadeia().String().getText() + ");\n");
                        saida.append("}\n");
                        saida.append("for(" + aux1 + "=0;"+ aux1 + "<" + dimensaolinha + "-" + expressao1 + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + bg + ");\n");
                        saida.append("}\n");
                        saida.append("strcat(bufferPrint" + contador + ",\"\\n\" );\n");
                        saida.append("strcpy(" + ctx.Identificador(0).getText()+ "[" + linha + "]" + ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(0).getText()+ "[" + linha + "]" + "," + ctx.Identificador(0).getText() + "pointer);\n");
                        int colunaNova= Integer.parseInt(ctx.expressao().getText()) * Integer.parseInt(ctx.cadeia().expressao().getText()) * ctx.cadeia().String().getText().length();
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",coluna + colunaNova );
                        if (coluna + colunaNova >= tabela.verificarLinha(ctx.Identificador(0).getText()+"dimensao1")){
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",0);
                        tabela.modificar(ctx.Identificador(0).getText(), linha+1);
                        }
                        contador++;
                        
                    }
                    else {
                        int linha = tabela.verificarLinha(ctx.Identificador(0).getText());
                        int coluna = tabela.verificarLinha(ctx.Identificador(0).getText()+"coluna");
                        String bg = tabela.verificar(ctx.Identificador(0).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        String expressao = "int expressao" + contador + "="+ ctx.expressao().getText() +";\n";
                        String expressao1 = "expressao" + contador;
                        saida.append(expressao);
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(0)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        saida.append("strcpy(bufferPrint" + contador + "," + ctx.cadeia().getText() + ");\n");
                        saida.append("for(" + aux1 + "=0;" + aux1 + "<" +  expressao1 + "-1;" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + ctx.cadeia().getText() + ");\n");
                        saida.append("}\n");
                        saida.append("for(" + aux1 + "=0;"+ aux1 + "<" + dimensaolinha + "-" + expressao1 + "-1;" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + bg + ");\n");
                        saida.append("}\n");
                        saida.append("strcat(bufferPrint" + contador + ",\"\\n\" );\n");
                        saida.append("strcpy(" + ctx.Identificador(0).getText()+ "[" + linha + "]" + ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(0).getText()+ "[" + linha + "]" + "," + ctx.Identificador(0).getText() + "pointer);\n");
                        int colunaNova;
                        colunaNova= Integer.parseInt(ctx.expressao().getText())* ctx.cadeia().getText().length();
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",coluna + colunaNova );
                        if (coluna + colunaNova >= tabela.verificarLinha(ctx.Identificador(0).getText()+"dimensao1")){
                        
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",0);
                        tabela.modificar(ctx.Identificador(0).getText(), linha+1);
                        }
                        contador++;
                        
                    }     
                    }
                else{
                    if (ctx.Identificador(1)!=null){
                        int linha = tabela.verificarLinha(ctx.Identificador(1).getText());
                        int coluna = tabela.verificarLinha(ctx.Identificador(1).getText()+"coluna");
                        String bg = tabela.verificar(ctx.Identificador(1).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(1)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        
                        saida.append("strcpy(bufferPrint" + contador + "," + ctx.Identificador(0).getText() + ");\n");
                        saida.append("for(" + aux1 + "=0;"+ aux1 + "<" + dimensaolinha + "-" + tabela.verificarLinha(ctx.Identificador(0).getText()) + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + bg + ");\n");
                        saida.append("}\n");
                        saida.append("strcat(bufferPrint" + contador + ",\"\\n\" );\n");
                        saida.append("strcpy(" + ctx.Identificador(1).getText()+ "[" + linha + "]" + ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(1).getText()+ "[" + linha + "]" + "," + ctx.Identificador(1).getText() + "pointer);\n");
                        int colunaNova= tabela.verificarLinha(ctx.Identificador(0).getText());
                        tabela.modificar(ctx.Identificador(1).getText()+"coluna",coluna + colunaNova );
                        if (coluna + colunaNova >= tabela.verificarLinha(ctx.Identificador(1).getText()+"dimensao1")){
                        
                        tabela.modificar(ctx.Identificador(1).getText(), linha+1);
                        tabela.modificar(ctx.Identificador(1).getText()+"coluna",0);
                        }
                        contador++;
                    }
                    else if(ctx.cadeia().expressao()!=null){
                        int linha = tabela.verificarLinha(ctx.Identificador(0).getText());
                        int coluna = tabela.verificarLinha(ctx.Identificador(0).getText()+"coluna");
                        String bg = tabela.verificar(ctx.Identificador(0).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(0)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        
                        saida.append("strcpy(bufferPrint" + contador + "," + ctx.cadeia().String().getText() + ");\n");
                        saida.append("for(" + aux1 + "=0;"+ aux1 + "<" + dimensaolinha + "-" + "(" + ctx.cadeia().String().getText().length() + "-1)" + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + bg + ");\n");
                        saida.append("strcat(bufferPrint" + contador + ",\"\n\" );\n");
                        saida.append("}\n");
                        saida.append("strcat(bufferPrint" + contador + ",\"\\n\" );\n");
                        saida.append("strcpy(" + ctx.Identificador(0).getText()+ "[" + linha + "]" + ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(0).getText()+ "[" + linha + "]" + "," + ctx.Identificador(0).getText() + "pointer);\n");
                        int colunaNova= Integer.parseInt(ctx.cadeia().expressao().getText()) * ctx.cadeia().String().getText().length();
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",coluna + colunaNova );
                        if (coluna + colunaNova >= tabela.verificarLinha(ctx.Identificador(0).getText()+"dimensao1")){
                        
                        tabela.modificar(ctx.Identificador(0).getText(), linha+1);
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",0);
                        }
                        contador++;
                    }
                    else {
                        int linha = tabela.verificarLinha(ctx.Identificador(0).getText());
                        int coluna = tabela.verificarLinha(ctx.Identificador(0).getText()+"coluna");
                        String bg = tabela.verificar(ctx.Identificador(0).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(0)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        saida.append("strcpy(bufferPrint" + contador + "," + ctx.cadeia().getText() + ");\n");
                        saida.append("for(" + aux1 + "=0;"+ aux1 + "<" + dimensaolinha + "-" + "(" + ctx.cadeia().getText().length() +"-1)" + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + bg + ");\n");
                        saida.append("}\n");
                        saida.append("strcat(bufferPrint" + contador + ",\"\\n\" );\n");
                        saida.append("strcpy(" + ctx.Identificador(0).getText()+ "[" + linha + "]" + ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(0).getText()+ "[" + linha + "]" + "," + ctx.Identificador(0).getText() + "pointer);\n");
                        int colunaNova;
                        colunaNova= ctx.cadeia().getText().length();
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",coluna + colunaNova );

                        if (coluna + colunaNova >= tabela.verificarLinha(ctx.Identificador(0).getText()+"dimensao1")){
                        
                        tabela.modificar(ctx.Identificador(0).getText(), linha+1);
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",0);
                        }
                        contador++;
                    }
                }
                    break;
                
        //aqui está o print, mas devido a alguns problemas com buffer eu não consegui implementar completamente e não está
        //funcionando
            case "print":
                if (ctx.expressao()!= null){
                    if (ctx.Identificador(1)!=null){
                        int linha = tabela.verificarLinha(ctx.Identificador(1).getText());
                        String bg = tabela.verificar(ctx.Identificador(1).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        String expressao = "int expressao" + contador + "="+ Integer.parseInt(ctx.expressao().getText()) * tabela.verificarLinha(ctx.Identificador(0).getText())+";\n";
                        String expressao1 = "expressao" + contador;
                        saida.append(expressao);
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(1)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        saida.append("strcpy(bufferPrint"+ contador  + ","+ ctx.Identificador(1).getText() + "[" + linha + "]" + ");\n");
                        saida.append("for(" + aux1 + "=0;" + aux1 + "<" +  expressao1 + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + ctx.Identificador(0).getText() + ");\n");
                        saida.append("}\n");
                        saida.append("for(" + aux1 + "=0;"+ aux1 + "<" + dimensaolinha + "-" + expressao1 + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + bg + ");\n");
                        saida.append("}\n");
                        saida.append("strcpy(" + ctx.Identificador(1).getText()+ "[" + linha + "]" + ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(1).getText()+ "[" + linha + "]" + "," + ctx.Identificador(1).getText() + "pointer);\n");
                        
                        tabela.modificar(ctx.Identificador(1).getText()+"coluna",0);
                        tabela.modificar(ctx.Identificador(1).getText(), linha+1);
                        contador++;
                        
                    }
                    else if(ctx.cadeia().expressao()!=null){
                        int linha = tabela.verificarLinha(ctx.Identificador(0).getText());
                        String bg = tabela.verificar(ctx.Identificador(0).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        String expressao = "int expressao" + contador + "="+ ctx.expressao().getText()+ "*" + ctx.cadeia().expressao().getText() +";\n";
                        String expressao1 = "expressao" + contador;
                        saida.append(expressao);
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(0)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        saida.append("strcpy(bufferPrint"+ contador  + ","+ ctx.Identificador(0) + "[" + linha + "]" + ");\n");
                        saida.append("for(" + aux1 + "=0;" + aux1 + "<" +  expressao1 + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + ctx.cadeia().String().getText() + ");\n");
                        saida.append("}\n");
                        saida.append("for(" + aux1 + "=0;"+ aux1 + "<" + dimensaolinha + "-" + expressao1 + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + bg + ");\n");
                        saida.append("}\n");
                        saida.append("strcpy(" + ctx.Identificador(0).getText()+ "[" + linha + "]" + ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(0).getText()+ "[" + linha + "]" + "," + ctx.Identificador(0).getText() + "pointer);\n");
                        
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",0);
                        tabela.modificar(ctx.Identificador(0).getText(), linha+1);
                        contador++;
                        
                    }
                    else {
                        int linha = tabela.verificarLinha(ctx.Identificador(0).getText());
                        int coluna = tabela.verificarLinha(ctx.Identificador(0).getText()+"coluna");
                        String bg = tabela.verificar(ctx.Identificador(0).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        String expressao = "int expressao" + contador + "="+ ctx.expressao().getText()+";\n";
                        String expressao1 = "expressao" + contador;
                        saida.append(expressao);
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(0)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        saida.append("strcpy(bufferPrint"+ contador  + ","+ ctx.cadeia().getText() + ");\n");
                        saida.append("for(" + aux1 + "=0;" + aux1 + "<" +  expressao1 + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + ctx.cadeia().getText() + ");\n");
                        saida.append("}\n");
                        saida.append("for(" + aux1 + "=0;"+ aux1 + "<" + dimensaolinha + "-" + expressao1 + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + bg + ");\n");
                        saida.append("}\n");
                        saida.append("strcpy(" + ctx.Identificador(0).getText()+ "[" + linha + "]" + ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(0).getText()+ "[" + linha + "]" + "," + ctx.Identificador(0).getText() + "pointer);\n");
                        
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",0);
                        tabela.modificar(ctx.Identificador(0).getText(), linha+1);
                        contador++;
                    }     
                    }
                    else{
                    if (ctx.Identificador(1)!=null){
                        int linha = tabela.verificarLinha(ctx.Identificador(1).getText());
                        String bg = tabela.verificar(ctx.Identificador(1).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(1)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        saida.append("strcpy(bufferPrint"+ contador  + ","+ ctx.Identificador(1).getText() + "[" + linha + "]" + ");\n");
                        saida.append("strcat(bufferPrint" + contador + "," + ctx.Identificador(0).getText() + ");\n");
                        saida.append("for(" + aux1 + "=0;"+ aux1 + "<" + dimensaolinha + "-" + tabela.verificarLinha(ctx.Identificador(0).getText()) + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + bg + ");\n");
                        saida.append("}\n");
                        saida.append("strcpy(" + ctx.Identificador(1).getText()+ "[" + linha + "]" + ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(1).getText()+ "[" + linha + "]" + "," + ctx.Identificador(1).getText() + "pointer);\n");
                        saida.append("fputs(quebra" + "," + ctx.Identificador(1).getText() + "pointer);\n");
                        tabela.modificar(ctx.Identificador(1).getText()+"coluna",0);
                        tabela.modificar(ctx.Identificador(1).getText(), linha+1);
                        contador++;
                        
                    }
                    else if(ctx.cadeia().expressao()!=null){
                        int linha = tabela.verificarLinha(ctx.Identificador(0).getText());
                        String bg = tabela.verificar(ctx.Identificador(0).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(0)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        saida.append("strcpy(bufferPrint"+ contador  + ","+ ctx.Identificador(0) + "[" + linha + "]" + ");\n");
                        saida.append("strcat(bufferPrint" + contador + "," + ctx.cadeia().String().getText() + ");\n");
                        saida.append("for(" + aux1 + "=0;"+ aux1 + "<" + dimensaolinha + "-" + ctx.cadeia().String().getText().length() + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + bg + ");\n");
                        saida.append("}\n");
                        saida.append("strcpy(" + ctx.Identificador(0).getText()+ "[" + linha + "]" + ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(0).getText()+ "[" + linha + "]" + "," + ctx.Identificador(0).getText() + "pointer);\n");
                        saida.append("fputs(quebra" + "," + ctx.Identificador(0).getText() + "pointer);\n");
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",0);
                        tabela.modificar(ctx.Identificador(0).getText(), linha+1);
                        contador++;
                        
                    }
                    else {
                        int linha = tabela.verificarLinha(ctx.Identificador(0).getText());
                        String bg = tabela.verificar(ctx.Identificador(0).getText() + "bg");
                        String aux = "int loopauxiliar" + contador + ";\n";
                        String aux1 = "loopauxiliar" + contador;
                        saida.append(aux);
                        String dimensaolinha = tabela.verificar(ctx.Identificador(0)+ "dimensao1");
                        saida.append("char bufferPrint" + contador + "[" + dimensaolinha +"];\n");
                        saida.append("strcpy(bufferPrint"+ contador  + ","+ ctx.Identificador(0).getText() + "[" + linha + "]" + ");\n");
                        saida.append("strcat(bufferPrint" + contador + "," + ctx.cadeia().getText() + ");\n");
                        saida.append("for(" + aux1 + "=0;"+ aux1 + "<" + dimensaolinha + "-" + ctx.cadeia().getText().length() + ";" + aux1 + "++){\n");
                        saida.append("strcat(bufferPrint" + contador + "," + bg + ");\n");
                        saida.append("}\n");
                        saida.append("strcpy(" + ctx.cadeia().getText()+  ","+ "bufferPrint" + contador + ");\n");
                        saida.append("fputs("+ ctx.Identificador(0).getText()+ "[" + linha + "]" + "," + ctx.Identificador(0).getText() + "pointer);\n");
                        saida.append("fputs(quebra" + "," + ctx.Identificador(0).getText() + "pointer);\n");
                        tabela.modificar(ctx.Identificador(0).getText()+"coluna",0);
                        tabela.modificar(ctx.Identificador(0).getText(), linha+1);
                        contador++;
                        
                    }
                }
                    break;
            //eu tentei, mas esse semestre foi horrível
            case "printCenter":
            
            case "printendln":       
        }
        
        return null;
    }
    //falta também implementar o for que estava planejado para ser incluido para automatizar, mas tive mais problemas com
    //buffer. Parabéns para quem implementou qualquer linguagem que lida com strings fácil e compila para C, são verdadeiros
    //heróis.
}
