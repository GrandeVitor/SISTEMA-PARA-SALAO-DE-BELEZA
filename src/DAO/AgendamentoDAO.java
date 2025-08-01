/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import JDBC.ConnectionFactory;
import Objs.Agendamento;
import Objs.Cliente;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

/**
 *
 * @author Aluno
 */
public class AgendamentoDAO {
 private Connection conecta;
 
 public AgendamentoDAO() {
     this.conecta = new ConnectionFactory().conecta();
     
 }
 
 public void inserirAgenda(Agendamento age) {
     try {
         String cmd = "insert into tb_agendamento "
                 + "(age_cod," 
                 + "cliente_cod," 
                 + "funcionario_cod," 
                 + "age_servico," 
                 + "age_data," 
                 + "age_hora,"
                 + "age_preco," 
                 + "age_status)" 
                 + "values (default,?,?,?,?,?,?,?)";
         PreparedStatement stmt = conecta.prepareStatement(cmd);
         stmt.setInt(1, age.getCliente_cod());
         stmt.setInt(2, age.getFuncionario_cod());
         stmt.setString(3, age.getAge_servico());
          
        // Convertendo a data corretamente
        java.sql.Date dataSql = new java.sql.Date(age.getAge_data().getTime());
        stmt.setDate(4, dataSql);
         stmt.setString(5, age.getAge_hora());
         stmt.setFloat(6, age.getAge_preco());
         stmt.setString(7, age.getAge_status());
         stmt.execute();
         JOptionPane.showMessageDialog(null, "Agendamento Salvo com sucesso!!!");
         stmt.close();
     } catch (SQLException e) {
         JOptionPane.showMessageDialog(null, "Erro ao Salvar Agendamento "+e);
     }
 }
 
 public void alterarAgendamento(Agendamento age) {
     try {
         String cmdsql = "UPDATE tb_agendamento " // <-- Adicione espaço após o nome da tabela
        + "SET cliente_cod = ?, "
        + "funcionario_cod = ?, "
        + "age_servico = ?, " 
        + "age_data = ?, "
        + "age_hora = ?, " 
        + "age_preco = ?, "
        + "age_status = ? "
        + "WHERE age_cod = ? "; // <-- Adicione espaço antes do WHERE

         PreparedStatement stmt = conecta.prepareStatement(cmdsql);
         stmt.setInt(1, age.getCliente_cod());
         stmt.setInt(2, age.getFuncionario_cod());
         stmt.setString(3, age.getAge_servico());
          
        // Convertendo a data corretamente
        java.sql.Date dataSql = new java.sql.Date(age.getAge_data().getTime());
        stmt.setDate(4, dataSql);
         stmt.setString(5, age.getAge_hora());
         stmt.setFloat(6, age.getAge_preco());
         stmt.setString(7, age.getAge_status());
         stmt.setInt(8, age.getAge_cod());
         stmt.executeUpdate();
         JOptionPane.showMessageDialog(null, "Agendamento Alterado com sucesso!!!");
         stmt.close();
     } catch (SQLException e) {
         JOptionPane.showMessageDialog(null, "Erro ao Alterar Agendamento "+e);
     }
 }
 
 public void excluirAgendamento(String age_cod) {
     try {
         String cmdsql = "delete from tb_agendamento where age_cod = "+age_cod;
         PreparedStatement stmt = conecta.prepareStatement(cmdsql);
         int resp = JOptionPane.showConfirmDialog(null, "Deseja realmente Excluir esse Agendamento?");
         if (resp == JOptionPane.YES_OPTION) {
             stmt.execute();
             JOptionPane.showMessageDialog(null, "Agendamento excluido com sucesso!!");
         }        
         stmt.close();
     } catch (SQLException e) {
         JOptionPane.showMessageDialog(null, "Erro ao Excluir Agendamento"+e);
     }
 }
 
 public ArrayList<Agendamento> buscaAgendamento() {
      ArrayList<Agendamento> lista = new ArrayList<>();
      String cmdsql = "SELECT a.*, c.cli_nome, f.fun_nome " +
                    "FROM tb_agendamento a " +
                    "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
                    "JOIN tb_funcionario f ON a.funcionario_cod = f.fun_cod " +
                    "ORDER BY a.age_data ASC";
     try {
         PreparedStatement stmt = conecta.prepareStatement(cmdsql);
         ResultSet rs = stmt.executeQuery();
         while (rs.next()) {
             Agendamento age = new Agendamento();
             age.setAge_cod(rs.getInt("age_cod"));
             age.setCliente_cod(rs.getInt("cliente_cod"));
             age.setFuncionario_cod(rs.getInt("funcionario_cod"));
             age.setAge_servico(rs.getString("age_servico"));
             age.setAge_data(rs.getDate("age_data"));
             age.setAge_hora(rs.getString("age_hora"));
             age.setAge_preco(rs.getFloat("age_preco"));
             age.setAge_status(rs.getString("age_status"));
             
             age.setCliente_nome(rs.getString("cli_nome"));
             age.setFuncionario_nome(rs.getString("fun_nome"));
            
             lista.add(age);
         }        
     } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar Agendamento"+e);
     }
     return lista;
 }
 
 public ArrayList<Agendamento> pesquisarAgendamento(String campo, String pesquisar) {
    ArrayList<Agendamento> lista = new ArrayList<>();

    // Campos permitidos (tanto da tabela agendamento quanto das tabelas relacionadas)
    List<String> camposValidos = Arrays.asList(
        "age_data", "age_servico", "age_hora", "age_status",
        "cli_nome", "fun_nome"
    );

    if (!camposValidos.contains(campo)) {
        JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido.");
        return lista;
    }

    // Ajusta o prefixo da tabela, caso seja campo de cliente ou funcionário
    String prefixo = "a.";
    if (campo.equals("cli_nome")) {
        prefixo = "c.";
    } else if (campo.equals("fun_nome")) {
        prefixo = "f.";
    }

    String cmdsql;
    
    if (campo.equals("age_data")) {
    cmdsql = "SELECT a.*, c.cli_nome, f.fun_nome " +
             "FROM tb_agendamento a " +
             "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
             "JOIN tb_funcionario f ON a.funcionario_cod = f.fun_cod " +
             "WHERE DATE_FORMAT(a.age_data, '%d/%m/%Y') LIKE ? " +
             "ORDER BY a.age_data ASC";
} else {
    
    cmdsql = "SELECT a.*, c.cli_nome, f.fun_nome " +
             "FROM tb_agendamento a " +
             "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
             "JOIN tb_funcionario f ON a.funcionario_cod = f.fun_cod " +
             "WHERE " + prefixo + campo + " LIKE ? " +
             "ORDER BY a.age_data ASC";
    }
    try {
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        stmt.setString(1, "%" + pesquisar + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Agendamento age = new Agendamento();
            age.setAge_cod(rs.getInt("age_cod"));
            age.setCliente_cod(rs.getInt("cliente_cod"));
            age.setFuncionario_cod(rs.getInt("funcionario_cod"));
            age.setAge_servico(rs.getString("age_servico"));
            age.setAge_data(rs.getDate("age_data"));
            age.setAge_hora(rs.getString("age_hora"));
            age.setAge_preco(rs.getFloat("age_preco"));
            age.setAge_status(rs.getString("age_status"));
            age.setCliente_nome(rs.getString("cli_nome"));
            age.setFuncionario_nome(rs.getString("fun_nome"));

            lista.add(age);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar Agendamento: " + e);
    }
    return lista;
}


 public List<Agendamento> buscarAgendamentosDoClienteNoDia(int clienteId, Date data) {
    List<Agendamento> agendamentos = new ArrayList<>();

    String sql = "SELECT * FROM tb_agendamento WHERE cliente_cod = ? AND age_data = ?";

    try {
        PreparedStatement stmt = conecta.prepareStatement(sql);

        stmt.setInt(1, clienteId);
        stmt.setDate(2, new java.sql.Date(data.getTime()));

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Agendamento ag = new Agendamento();
            ag.setAge_cod(rs.getInt("age_cod"));
            ag.setCliente_cod(rs.getInt("cliente_cod"));  // Ajustado aqui
            ag.setFuncionario_cod(rs.getInt("funcionario_cod"));  // Ajustado aqui
            ag.setAge_servico(rs.getString("age_servico"));
            ag.setAge_data(rs.getDate("age_data"));
            ag.setAge_hora(rs.getString("age_hora"));
            ag.setAge_preco(Float.parseFloat(rs.getString("age_preco"))); // pois age_preco é VARCHAR
            ag.setAge_status(rs.getString("age_status"));

            agendamentos.add(ag);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar agendamentos do cliente: " + e.getMessage());
    }

    return agendamentos;
}

 
}
