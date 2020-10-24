/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.asciidl;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;


public class AsciiDLErrorListener extends BaseErrorListener {
    
   
   public AsciiDLErrorListener() {
  }
   
   @Override
      public void syntaxError(Recognizer<?,?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
      Token errorToken = (Token) offendingSymbol;
      
      String errText = errorToken.getText();
      //primeiro checa se o erro foi lexico
      if (errorToken.getType() == 35) {
          //caso seja erro lexico de cadeia nao fechada
          System.out.println("Linha " + line + ": " + "cadeia nao fechada");
      } else if (errorToken.getType() == 36) {
          //caso seja erro lexico de simbolo nao identificado
          System.out.println("Linha " + line + ": " + " - simbolo nao identificado");
      }else{
        //se nao for erro lexico, eh erro sintatico
        System.out.println("Linha " + line + ": erro sintatico proximo a " + errText);    
      }     
      System.out.println("Fim da compilacao");
      //termina a compilacao se foi detectado erro lexico ou sintatico
      System.exit(0);
   }
}
