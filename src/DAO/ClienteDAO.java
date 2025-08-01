/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import JDBC.ConnectionFactory;
import Objs.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Aluno
 */
public class ClienteDAO {
 private Connection conecta;
 
 public ClienteDAO() {
     this.conecta = new ConnectionFactory().conecta();
 }
 
 
  
     public void incluirCliente(Cliente cli) {
     
     try {
         String cmdsql = "insert into tb_cliente " 
                 + "(cli_cod, " 
                 + "cli_nome, " 
                 + "cli_telefone, " 
                 + "cli_email, " 
                 + "cli_cpf) "
                 + "values (default, ?,?,?,?)";
         PreparedStatement stmt = conecta.prepareStatement(cmdsql);
         stmt.setString(1, cli.getCli_nome());
         stmt.setString(2, cli.getCli_telefone());
         stmt.setString(3, cli.getCli_email());
         stmt.setString(4, cli.getCli_cpf());
          stmt.execute();
         JOptionPane.showMessageDialog(null, "Cliente Salvo com sucesso!!!");
          stmt.close();
     } catch (SQLException e) {
         JOptionPane.showMessageDialog(null, "Erro ao Salvar Cliente:" + e);
         
     }
     
 }
     
public void alterarCliente( Cliente cli) {
    try {
        String cmdsql = "update tb_cliente "
                + "set cli_nome = ?, "
                + "cli_telefone=?, "
                + "cli_email=?, "
                + "cli_cpf=? " 
                + "where cli_cod = ? ";
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        stmt.setString(1, cli.getCli_nome());
        stmt.setString(2, cli.getCli_telefone());
        stmt.setString(3, cli.getCli_email());
        stmt.setString(4, cli.getCli_cpf());
        stmt.setInt(5, cli.getCli_cod());
        stmt.execute();
        JOptionPane.showMessageDialog(null, "Cliente Alterado com sucesso!!!");
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao Alterar cliente "+e);
    }
}     
     
public void excluirCliente(String cli_cod) {
    try {
       
        // Verificar se o cliente tem agendamentos
        String sqlCheck = "SELECT COUNT(*) FROM tb_agendamento WHERE cliente_cod = ?";
        PreparedStatement stmtCheck = conecta.prepareStatement(sqlCheck);
        stmtCheck.setInt(1, Integer.parseInt(cli_cod));
        ResultSet rs = stmtCheck.executeQuery();
        
        if (rs.next() && rs.getInt(1) > 0) {
            // Cliente possui agendamentos, perguntar ao usuário
            int resposta = JOptionPane.showConfirmDialog(null, 
                    "Este cliente possui agendamentos registrados. Deseja excluí-lo mesmo assim?", 
                    "Confirmação", JOptionPane.YES_NO_OPTION);
            
            if (resposta != JOptionPane.YES_OPTION) {
                return; // Cancelar a exclusão
            }
        }
        
        
      String cmdsql = "delete from tb_cliente where cli_cod ="+cli_cod;
      PreparedStatement stmt = conecta.prepareStatement(cmdsql);
      int resp =JOptionPane.showConfirmDialog(null, "Deseja realmente excluir esse Cliente?");
      if (resp == JOptionPane.YES_OPTION) {
          stmt.execute();
          JOptionPane.showMessageDialog(null, "Cliente excluido com sucesso!!");
      }
      stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao excluir Cliente"+e);
    }
}   

public ArrayList<Cliente>buscaCliente() {
    ArrayList<Cliente> lista = new ArrayList();
    String cmdsql = "select * from tb_cliente order by cli_nome asc";
            try {
               PreparedStatement stmt = conecta.prepareStatement(cmdsql);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Cliente cli = new Cliente();
                    cli.setCli_cod(rs.getInt("cli_cod"));
                    cli.setCli_nome(rs.getString("cli_nome"));
                    cli.setCli_telefone(rs.getString("cli_telefone"));
                    cli.setCli_email(rs.getString("cli_email"));
                    cli.setCli_cpf(rs.getString("cli_cpf"));
                    lista.add(cli);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao buscar Cliente"+e);
            }
     return lista;
    
}

public ArrayList<Cliente> PesquisarCliente(String campo, String pesquisar) {
    ArrayList<Cliente> lista = new ArrayList<>();
    
    // Validação para evitar problemas: só aceita campos conhecidos
    if (!campo.equals("cli_nome") && !campo.equals("cli_telefone") && 
        !campo.equals("cli_email") && !campo.equals("cli_cpf")) {
        JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido.");
        return lista;
    }

    String cmdsql = "SELECT * FROM tb_cliente WHERE " + campo + " LIKE ? ORDER BY cli_nome ASC";

    try {
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        stmt.setString(1, "%" + pesquisar + "%"); // Usando parâmetro seguro

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Cliente cli = new Cliente();
            cli.setCli_cod(rs.getInt("cli_cod"));
            cli.setCli_nome(rs.getString("cli_nome"));
            cli.setCli_telefone(rs.getString("cli_telefone"));
            cli.setCli_email(rs.getString("cli_email"));
            cli.setCli_cpf(rs.getString("cli_cpf"));
            lista.add(cli);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar Cliente: " + e);
    }
    return lista;
}


public List<Cliente> listarClientes() {
    List<Cliente> clientes = new ArrayList<>();
    String sql = "SELECT cli_cod, cli_nome, cli_email FROM tb_cliente"; 

    try {
         PreparedStatement stmt = conecta.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery(); 

        while (rs.next()) {
            Cliente cliente = new Cliente();
            cliente.setCli_cod(rs.getInt("cli_cod"));
            cliente.setCli_nome(rs.getString("cli_nome"));
            cliente.setCli_email(rs.getString("cli_email")); // ← certifique-se de que está aqui!
            clientes.add(cliente);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar clientes: " + e.getMessage());
    }
    
    return clientes;
}

public Cliente buscarPorCodigo(int cod) {
    Cliente cliente = null;
    String sql = "SELECT cli_cod, cli_nome, cli_email FROM tb_cliente WHERE cli_cod = ?";

    try {
        PreparedStatement stmt = conecta.prepareStatement(sql);
        stmt.setInt(1, cod);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            cliente = new Cliente();
            cliente.setCli_cod(rs.getInt("cli_cod"));
            cliente.setCli_nome(rs.getString("cli_nome"));
            cliente.setCli_email(rs.getString("cli_email"));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar cliente por código: " + e.getMessage());
    }

    return cliente;
}


}


