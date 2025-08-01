/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Random;
/**
 *
 * @author Aluno
 */
public class Converter {
    public static Date strToDate(String data){
        String dia = data.substring(0, 2);
        String mes = data.substring(3, 5);
        String ano = data.substring(6, 10);
        String dataSql = ano+"-"+mes+"-"+dia;
        return Date.valueOf(dataSql);
    }

    
     // Método para converter data no formato dd/MM/yyyy para java.sql.Date
    public static Date strToDate2(String dateStr) {
        // Verificar se a data está vazia
        if (dateStr == null || dateStr.isEmpty()) {
            throw new IllegalArgumentException("Data inválida: " + dateStr);
        }

        // Tentando converter a data do formato dd/MM/yyyy para o formato yyyy-MM-dd
        try {
            // Definir o formato da data de entrada (dd/MM/yyyy)
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date utilDate = sdf.parse(dateStr); // Convertendo para java.util.Date

            // Agora, converter para java.sql.Date
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            // Caso o formato não seja válido, lançar a exceção
            throw new IllegalArgumentException("Formato de data inválido: " + dateStr);
        }
    }
    
    public static int stringParaInt(String valor) {
        return Integer.parseInt(valor);
    }
  
    public static float stringParaFloat(String valor) {
    if (valor == null || valor.trim().isEmpty()) {
        throw new IllegalArgumentException("Valor não pode ser nulo ou vazio: " + valor);
    }

    try {
        return Float.parseFloat(valor.replace(",", ".")); // Suporta vírgula como separador decimal
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Valor inválido para float: " + valor);
    }
}
 
     public static String gerarCodigo(int tamanho) {
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < tamanho; i++) {
            codigo.append(random.nextInt(10));
        }
        return codigo.toString();
    }
    
}
    
     
   

