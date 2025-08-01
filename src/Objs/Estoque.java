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
public class Estoque {
    private int est_cod;
    private String est_nome_item;
    private int est_quant_item;
    private Float est_valor_item;
    private Date est_validade_item;
    private int fornecedor_cod;
    private String fornecedor_nome;

    public int getEst_cod() {
        return est_cod;
    }

    public void setEst_cod(int est_cod) {
        this.est_cod = est_cod;
    }

    public String getEst_nome_item() {
        return est_nome_item;
    }

    public void setEst_nome_item(String est_nome_item) {
        this.est_nome_item = est_nome_item;
    }

    public int getEst_quant_item() {
        return est_quant_item;
    }

    public void setEst_quant_item(int est_quant_item) {
        this.est_quant_item = est_quant_item;
    }

    public Float getEst_valor_item() {
        return est_valor_item;
    }

    public void setEst_valor_item(Float est_valor_item) {
        this.est_valor_item = est_valor_item;
    }


    public Date getEst_validade_item() {
        return est_validade_item;
    }

    public void setEst_validade_item(Date est_validade_item) {
        this.est_validade_item = est_validade_item;
    }

    public int getFornecedor_cod() {
        return fornecedor_cod;
    }

    public void setFornecedor_cod(int fornecedor_cod) {
        this.fornecedor_cod = fornecedor_cod;
    }

    public String getFornecedor_nome() {
        return fornecedor_nome;
    }

    public void setFornecedor_nome(String fornecedor_nome) {
        this.fornecedor_nome = fornecedor_nome;
    }

    
   
}
