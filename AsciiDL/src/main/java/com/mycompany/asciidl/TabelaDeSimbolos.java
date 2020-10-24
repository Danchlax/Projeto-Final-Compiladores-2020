
package com.mycompany.asciidl;

import java.util.HashMap;
import java.util.Map;

public class TabelaDeSimbolos {
    //definicao do tipo de entrada da tabela de simbolos
    class EntradaTabelaDeSimbolos {
        String nome;
        String tipo;

        private EntradaTabelaDeSimbolos(String nome, String tipo) {
            this.nome = nome;
            this.tipo = tipo;
        }
    }
    
    private final Map<String, EntradaTabelaDeSimbolos> tabela;
    
    public TabelaDeSimbolos() {
        //a tabela de simbolos nao eh nada mais que um hashmap de entradas
        this.tabela = new HashMap<>();
    }
    //metodo para adicionar variáveis na tabela
    public void adicionar(String nome, String tipo) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipo));
    }
    //retorna se a variavel existe na tabela ou nao
    public boolean existe(String nome) {
        return tabela.containsKey(nome);
    }
    //retorna o tipo da entrada
    public String verificar(String nome) {
        return tabela.get(nome).tipo;
    }
}