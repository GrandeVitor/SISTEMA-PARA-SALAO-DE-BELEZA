/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objs;

import java.util.Date;

/**
 *
 * @author Aluno
 */
public class Caixa {
    private int cai_cod;
    private String cai_tipo;
    private String cai_descricao;
    private float cai_valor;
    private Date cai_data;
    private String cai_forma_pag;

    public int getCai_cod() {
        return cai_cod;
    }

    public void setCai_cod(int cai_cod) {
        this.cai_cod = cai_cod;
    }

    public String getCai_tipo() {
        return cai_tipo;
    }

    public void setCai_tipo(String cai_tipo) {
        this.cai_tipo = cai_tipo;
    }

    public String getCai_descricao() {
        return cai_descricao;
    }

    public void setCai_descricao(String cai_descricao) {
        this.cai_descricao = cai_descricao;
    }

    public float getCai_valor() {
        return cai_valor;
    }

    public void setCai_valor(float cai_valor) {
        this.cai_valor = cai_valor;
    }
    
    
    public Date getCai_data() {
        return cai_data;
    }

    public void setCai_data(Date cai_data) {
        this.cai_data = cai_data;
    }

    public String getCai_forma_pag() {
        return cai_forma_pag;
    }

    public void setCai_forma_pag(String cai_forma_pag) {
        this.cai_forma_pag = cai_forma_pag;
    }
    
    
}
