
package com.mycompany.asciidl;

import java.util.HashMap;
import java.util.Map;

public class TabelaDeSimbolos {
    //definicao do tipo de entrada da tabela de simbolos
    class EntradaTabelaDeSimbolos {
        String nome;
        String tipo;
        int linha;

        private EntradaTabelaDeSimbolos(String nome, String tipo) {
            this.nome = nome;
            this.tipo = tipo;
        }
        private EntradaTabelaDeSimbolos(String nome, int linha){
            this.nome = nome;
            this.linha = linha;
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
    public void adicionar(String nome, int linha){
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome,linha));
    }
    //retorna se a variavel existe na tabela ou nao
    public boolean existe(String nome) {
        return tabela.containsKey(nome);
    }
    //retorna o tipo da entrada
    public String verificar(String nome) {
        return tabela.get(nome).tipo;
    }
    public int verificarLinha(String nome){
        return tabela.get(nome).linha;
    }
    public void modificar(String nome, String tipo){
        tabela.replace(nome, new EntradaTabelaDeSimbolos(nome,tipo));
    }
    public void modificar(String nome,int linha){
        tabela.replace(nome, new EntradaTabelaDeSimbolos(nome,linha));
    }
}