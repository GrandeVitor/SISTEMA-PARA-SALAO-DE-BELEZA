/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public class CodigoCache {
    
    private static Map<String, String> cache = new HashMap<>();

    public static void salvarCodigo(String email, String codigo) {
        cache.put(email, codigo);
    }

    public static boolean validarCodigo(String email, String codigo) {
        return codigo.equals(cache.get(email));
    }

    public static void limparCodigo(String email) {
        cache.remove(email);
    }

}
