package com.mycompany.asciidl;

public class AsciiDLSemantico extends AsciiDLBaseVisitor<Void> {

    TabelaDeSimbolos tabela;

    @Override
    //Inicializa a tabela de simbolos do programa
    public Void visitPrograma(AsciiDLParser.ProgramaContext ctx) {
        tabela = new TabelaDeSimbolos();
        return super.visitPrograma(ctx);
    }

    @Override
    //Trata as declaracoes de variaveis do tipo output
    public Void visitDecl_output(AsciiDLParser.Decl_outputContext ctx) {
        String NomeVar;
        String TipoVar = "output";
        int i;
        //checa se nao ha declaracoes repetidas e insere as variaveis novas na tabela
        for (i = 0; i < ctx.Identificador().size(); i++) {
            NomeVar = ctx.Identificador(i).getText();
            if (tabela.existe(NomeVar)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.start, "output " + NomeVar + " ja declarado");
            } else {
                tabela.adicionar(NomeVar, TipoVar);
            }
        }
        return super.visitDecl_output(ctx);
    }

    @Override
    //Trata as declaracoes de variaveis do tipo macro e inteiro
    public Void visitDecl_var(AsciiDLParser.Decl_varContext ctx) {
        String NomeVar;
        String TipoVar = ctx.tipo_var().getText();
        int i;
        //checa se nao ha declaracoes repetidas e insere as variaveis novas na tabela
        for (i = 0; i < ctx.Identificador().size(); i++) {
            NomeVar = ctx.Identificador(i).getText();
            if (tabela.existe(NomeVar)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.start, TipoVar + " " + NomeVar + " ja declarado");
            } else {
                tabela.adicionar(NomeVar, TipoVar);
            }
        }
        return super.visitDecl_var(ctx);
    }

    @Override
    //verifica se os argumentos da estrutura de dimensao sao ambos do tipo inteiro
    public Void visitDimensao(AsciiDLParser.DimensaoContext ctx) {
        int i;
        for (i = 0; i < ctx.expressao().size(); i++) {
            if (!"inteiro".equals(AsciiDLSemanticoUtils.verificarTipo(tabela, ctx.expressao(i)))) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.start, "Estrutura de dimensao deve conter somente expressoes do tipo inteiro");
            }
        }
        return super.visitDimensao(ctx);
    }

    @Override
    //verifica se a variavel iteradora do comando For existe e se ela eh do tipo inteiro
    public Void visitCmdFor(AsciiDLParser.CmdForContext ctx) {
        String NomeVar = ctx.Identificador().getText();

        if (!tabela.existe(NomeVar)) {
            AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador().getSymbol(), "variavel " + NomeVar + " nao declarada");
            return super.visitCmdFor(ctx);
        }
        String TipoVar = tabela.verificar(NomeVar);
        if (!"inteiro".equals(TipoVar)) {
            AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador().getSymbol(), "variavel iteradora da estrutura For deve ser do tipo inteiro");
        }

        return super.visitCmdFor(ctx);
    }

    @Override
    //verifica os tipos dos argumentos do comando print
    public Void visitCmdPrint(AsciiDLParser.CmdPrintContext ctx) {
        String TipoVar;
        String NomeVar = ctx.Identificador(0).getText();
        //o primeiro argumento pode ser tanto uma cadeia quanto um identificador do tipo macro, portanto este comando pode ser um ou dois identificadores
        if (ctx.Identificador().size() == 1) {
            //caso o primeiro argumento seja uma cadeia, so checa a existencia e tipo do segundo argumento, que deve ser sempre um output
            if (!tabela.existe(NomeVar)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(0).getSymbol(), "variavel " + NomeVar + " nao declarada");
            } else {
                TipoVar = tabela.verificar(NomeVar);
                if (!"output".equals(TipoVar)) {
                    AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(0).getSymbol(), "identificador alvo do comando print nao eh um output");
                }
            }
        } else {
            //se o primeiro argumento eh um identificador, checa a existencia e os tipos dos dois
            if (!tabela.existe(NomeVar)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(0).getSymbol(), "variavel " + NomeVar + " nao declarada");
            } else {
                TipoVar = tabela.verificar(NomeVar);
                if (!"macro".equals(TipoVar)) {
                    AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(0).getSymbol(), "identificador que sera impresso nao eh um macro");
                }
            }
            NomeVar = ctx.Identificador(1).getText();
            if (!tabela.existe(NomeVar)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(1).getSymbol(), "variavel " + NomeVar + " nao declarada");
            } else {
                TipoVar = tabela.verificar(NomeVar);
                if (!"output".equals(TipoVar)) {
                    AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(1).getSymbol(), "identificador alvo do comando print nao eh um output");
                }
            }
        }
        if (ctx.expressao() != null) {
            //caso o comando print contenha uma estrutura times, verifica o tipo da expressao
            TipoVar = AsciiDLSemanticoUtils.verificarTipo(tabela, ctx.expressao());
            if (!"inteiro".equals(TipoVar)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.expressao().start, "expressao definindo o numero de vezes nao eh do tipo inteiro");
            }
        }

        return super.visitCmdPrint(ctx);
    }

    @Override
    //checa os tipos dos argumentos do comando add
    public Void visitCmdAdd(AsciiDLParser.CmdAddContext ctx) {
        String TipoVar;
        String NomeVar = ctx.Identificador(0).getText();
        //o primeiro argumento pode ser tanto uma cadeia quanto um identificador do tipo macro, portanto este comando pode ser um ou dois identificadores
        if (ctx.Identificador().size() == 1) {
            //caso o primeiro argumento seja uma cadeia, so checa a existencia e tipo do segundo argumento, que deve ser sempre um macro
            if (!tabela.existe(NomeVar)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(0).getSymbol(), "variavel " + NomeVar + " nao declarada");
            } else {
                TipoVar = tabela.verificar(NomeVar);
                if (!"macro".equals(TipoVar)) {
                    AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(0).getSymbol(), "identificador a qual deseja adicionar nao eh um macro");
                }
            }
        } else {
            //se o primeiro argumento eh um identificador, checa a existencia e os tipos dos dois. ambos devem ser macros
            if (!tabela.existe(NomeVar)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(0).getSymbol(), "variavel " + NomeVar + " nao declarada");
            } else {
                TipoVar = tabela.verificar(NomeVar);
                if (!"macro".equals(TipoVar)) {
                    AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(0).getSymbol(), "identificador que sera adicionado nao eh um macro");
                }
            }
            NomeVar = ctx.Identificador(1).getText();
            if (!tabela.existe(NomeVar)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(1).getSymbol(), "variavel " + NomeVar + " nao declarada");
            } else {
                TipoVar = tabela.verificar(NomeVar);
                if (!"macro".equals(TipoVar)) {
                    AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador(1).getSymbol(), "identificador a qual deseja adicionar nao eh um macro");
                }
            }
        }
        return super.visitCmdAdd(ctx);
    }

    @Override
    //checa a existencia e o tipo do identificador da estrutura. o identificador deve ser do tipo output
    public Void visitCmdSetbg(AsciiDLParser.CmdSetbgContext ctx) {
        String TipoVar;
        String NomeVar = ctx.Identificador().getText();
        if (!tabela.existe(NomeVar)) {
            AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador().getSymbol(), "variavel " + NomeVar + " nao declarada");
        } else {
            TipoVar = tabela.verificar(NomeVar);
            if (!"output".equals(TipoVar)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador().getSymbol(), "identificador deve ser um output");
            }
        }
        return super.visitCmdSetbg(ctx);
    }

    @Override
    //checa a existencia e os tipos dos identificadores do comando de atribuicao
    public Void visitCmdAtribuicao(AsciiDLParser.CmdAtribuicaoContext ctx) {
        String NomeVar;
        String TipoVar, TipoOutro;
        NomeVar = ctx.Identificador().getText();
        //primeiro, checa se o identificador que recebera a atribuicao existe. Se nao, aponta o erro e encerra o visit
        if (!tabela.existe(NomeVar)) {
            AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador().getSymbol(), "variavel " + NomeVar + " nao declarada");
            return super.visitCmdAtribuicao(ctx);
        }
        TipoVar = tabela.verificar(NomeVar);
        //checa se o identificador que recebera a atribuicao eh do tipo output, que nao eh permitido nesta operacao
        if ("output".equals(TipoVar)) {
            AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador().getSymbol(), "tipo output nao permitido nesta operacao");
        }
        //verificacao de compatibilidade de tipo dos dois argumentos
        if (ctx.cadeia() != null) {
            if (!"macro".equals(TipoVar)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador().getSymbol(), "tipos incompativeis na atribuicao");
            }
        } else {
            TipoOutro = AsciiDLSemanticoUtils.verificarTipo(tabela, ctx.expressao());
            if ("output".equals(TipoOutro)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.expressao().start, "tipo output nao permitido nesta operacao");
            } else if (!TipoOutro.equals(TipoVar) && !"invalido".equals(TipoOutro)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador().getSymbol(), "tipos incompativeis na atribuicao");
            }
        }
        return super.visitCmdAtribuicao(ctx);
    }

    @Override
    //verificacao de tipos da estrutura de cadeia (Inteiro, Cadeia)
    public Void visitCadeia(AsciiDLParser.CadeiaContext ctx) {
        if (ctx.expressao() != null) {
            //verifica se a expressao do primeiro argumento eh do tipo inteiro
            String TipoEx = AsciiDLSemanticoUtils.verificarTipo(tabela, ctx.expressao());
            if (!"inteiro".equals(TipoEx)) {
                AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.expressao().start, "expressao deve ser do tipo inteiro");
            }
            //verifica a existencia e o tipo do segundo argumento
            if (ctx.Identificador() != null) {
                String NomeVar = ctx.Identificador().getText();
                if (!tabela.existe(NomeVar)) {
                    AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador().getSymbol(), "variavel " + NomeVar + " nao declarada");
                } else {
                    String TipoVar = tabela.verificar(NomeVar);
                    if (!"macro".equals(TipoVar)) {
                        AsciiDLSemanticoUtils.adicionarErroSemantico(ctx.Identificador().getSymbol(), "identificador deve ser do tipo macro");
                    }
                }
            }
        }
        return super.visitCadeia(ctx);
    }

}
