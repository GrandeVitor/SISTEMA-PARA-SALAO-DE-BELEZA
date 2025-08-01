/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objs;

/**
 *
 * @author Aluno
 */
public class Funcionario {
    private int fun_cod;
    private String fun_nome;
    private String fun_telefone;
    private String fun_email;
    private String fun_cpf;
    private String fun_usuario;
    private String fun_senha;
   
    public int getFun_cod() {
        return fun_cod;
    }

    public void setFun_cod(int fun_cod) {
        this.fun_cod = fun_cod;
    }

    public String getFun_nome() {
        return fun_nome;
    }

    public void setFun_nome(String fun_nome) {
        this.fun_nome = fun_nome;
    }

    public String getFun_telefone() {
        return fun_telefone;
    }

    public void setFun_telefone(String fun_telefone) {
        this.fun_telefone = fun_telefone;
    }

    public String getFun_email() {
        return fun_email;
    }

    public void setFun_email(String fun_email) {
        this.fun_email = fun_email;
    }

    public String getFun_cpf() {
        return fun_cpf;
    }

    public void setFun_cpf(String fun_cpf) {
        this.fun_cpf = fun_cpf;
    }

    @Override
    public String toString() {
        return fun_nome;
    }

    public String getFun_usuario() {
        return fun_usuario;
    }

    public void setFun_usuario(String fun_usuario) {
        this.fun_usuario = fun_usuario;
    }
    
    

    public String getFun_senha() {
        return fun_senha;
    }

    public void setFun_senha(String fun_senha) {
        this.fun_senha = fun_senha;
    }
    
    
    
    
}
