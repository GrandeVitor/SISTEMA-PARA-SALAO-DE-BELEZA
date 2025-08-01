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
public class Agendamento {
    private int age_cod;
    private int cliente_cod;
    private int funcionario_cod;
    private String age_servico;
    private Date age_data;
    private String age_hora;
    private Float age_preco;
    private String age_status;
    private String cliente_nome;
    private String funcionario_nome;
    private Cliente cliente;


    public int getAge_cod() {
        return age_cod;
    }

    public void setAge_cod(int age_cod) {
        this.age_cod = age_cod;
    }

    public int getCliente_cod() {
        return cliente_cod;
    }

    public void setCliente_cod(int cliente_cod) {
        this.cliente_cod = cliente_cod;
    }

    public int getFuncionario_cod() {
        return funcionario_cod;
    }

    public void setFuncionario_cod(int funcionario_cod) {
        this.funcionario_cod = funcionario_cod;
    }

    
    public String getAge_servico() {
        return age_servico;
    }

    public void setAge_servico(String age_servico) {
        this.age_servico = age_servico;
    }

    public Date getAge_data() {
        return age_data;
    }

    public void setAge_data(Date age_data) {
        this.age_data = age_data;
    }

    public String getAge_hora() {
        return age_hora;
    }

    public void setAge_hora(String age_hora) {
        this.age_hora = age_hora;
    }

    public Float getAge_preco() {
        return age_preco;
    }

    public void setAge_preco(Float age_preco) {
        this.age_preco = age_preco;
    }

    
    public String getAge_status() {
        return age_status;
    }

    public void setAge_status(String age_status) {
        this.age_status = age_status;
    }

    public String getCliente_nome() {
        return cliente_nome;
    }

    public void setCliente_nome(String cliente_nome) {
        this.cliente_nome = cliente_nome;
    }

    public String getFuncionario_nome() {
        return funcionario_nome;
    }

    public void setFuncionario_nome(String funcionario_nome) {
        this.funcionario_nome = funcionario_nome;
    }

    public Cliente getCliente() {
    return cliente;
}

    public void setCliente(Cliente cliente) {
    this.cliente = cliente;
}

    
    

}
