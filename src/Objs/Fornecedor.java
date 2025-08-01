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
public class Fornecedor {
    private int forn_cod;
    private String forn_nome;
    private String forn_telefone;
    private String forn_cpf;
    private String forn_cnpj;
    private String forn_endereco;
    private String forn_email;
    private String forn_item;

    public int getForn_cod() {
        return forn_cod;
    }

    public void setForn_cod(int forn_cod) {
        this.forn_cod = forn_cod;
    }

    public String getForn_nome() {
        return forn_nome;
    }

    public void setForn_nome(String forn_nome) {
        this.forn_nome = forn_nome;
    }

    public String getForn_telefone() {
        return forn_telefone;
    }

    public void setForn_telefone(String forn_telefone) {
        this.forn_telefone = forn_telefone;
    }

    public String getForn_cpf() {
        return forn_cpf;
    }

    public void setForn_cpf(String forn_cpf) {
        this.forn_cpf = forn_cpf;
    }

    public String getForn_cnpj() {
        return forn_cnpj;
    }

    public void setForn_cnpj(String forn_cnpj) {
        this.forn_cnpj = forn_cnpj;
    }

    public String getForn_endereco() {
        return forn_endereco;
    }

    public void setForn_endereco(String forn_endereco) {
        this.forn_endereco = forn_endereco;
    }

    public String getForn_email() {
        return forn_email;
    }

    public void setForn_email(String forn_email) {
        this.forn_email = forn_email;
    }

    public String getForn_item() {
        return forn_item;
    }

    public void setForn_item(String forn_item) {
        this.forn_item = forn_item;
    }

    @Override
    public String toString() {
        return forn_nome;
    }

    
    
}
