/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import JDBC.ConnectionFactory;
import Objs.Funcionario;
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
public class FuncionarioDAO {
   private Connection conecta;
   
   public FuncionarioDAO() {
       this.conecta = new ConnectionFactory().conecta();
   }
   
   public void incluirFuncionario(Funcionario fun) {
        try {
            String cmdsql = "INSERT INTO tb_funcionario "
                    + "(fun_cod, fun_nome, fun_telefone, fun_email, fun_cpf, fun_usuario, fun_senha) "
                    + "VALUES (default, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conecta.prepareStatement(cmdsql);
            stmt.setString(1, fun.getFun_nome());
            stmt.setString(2, fun.getFun_telefone());
            stmt.setString(3, fun.getFun_email());
            stmt.setString(4, fun.getFun_cpf());
            stmt.setString(5, fun.getFun_usuario());
            stmt.setString(6, fun.getFun_senha());

            stmt.execute();
            JOptionPane.showMessageDialog(null, "Funcionário salvo com sucesso!");
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar funcionário: " + e);
        }
    }

    public void alterarFuncionario(Funcionario fun) {
        try {
            String cmdsql = "UPDATE tb_funcionario SET "
                    + "fun_nome = ?, fun_telefone = ?, fun_email = ?, fun_cpf = ?, "
                    + "fun_usuario = ?, fun_senha = ? "
                    + "WHERE fun_cod = ?";
            PreparedStatement stmt = conecta.prepareStatement(cmdsql);
            stmt.setString(1, fun.getFun_nome());
            stmt.setString(2, fun.getFun_telefone());
            stmt.setString(3, fun.getFun_email());
            stmt.setString(4, fun.getFun_cpf());
            stmt.setString(5, fun.getFun_usuario());
            stmt.setString(6, fun.getFun_senha());
            stmt.setInt(7, fun.getFun_cod());

            stmt.execute();
            JOptionPane.showMessageDialog(null, "Funcionário alterado com sucesso!");
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao alterar funcionário: " + e);
        }
    }

    public void excluirFuncionario(String fun_cod) {
        try {
            String sqlCheck = "SELECT COUNT(*) FROM tb_agendamento WHERE funcionario_cod = ?";
            PreparedStatement stmtCheck = conecta.prepareStatement(sqlCheck);
            stmtCheck.setInt(1, Integer.parseInt(fun_cod));
            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                int resposta = JOptionPane.showConfirmDialog(null,
                        "Este funcionário possui agendamentos registrados. Deseja excluí-lo mesmo assim?",
                        "Confirmação", JOptionPane.YES_NO_OPTION);

                if (resposta != JOptionPane.YES_OPTION) return;
            }

            String cmdsql = "DELETE FROM tb_funcionario WHERE fun_cod = ?";
            PreparedStatement stmt = conecta.prepareStatement(cmdsql);
            stmt.setInt(1, Integer.parseInt(fun_cod));
            int resp = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir esse Funcionário?");
            if (resp == JOptionPane.YES_OPTION) {
                stmt.execute();
                JOptionPane.showMessageDialog(null, "Funcionário excluído com sucesso!");
            }
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir funcionário: " + e);
        }
    }

    public ArrayList<Funcionario> buscaFuncionario() {
        ArrayList<Funcionario> lista = new ArrayList<>();
        String cmdsql = "SELECT * FROM tb_funcionario ORDER BY fun_nome ASC";
        try {
            PreparedStatement stmt = conecta.prepareStatement(cmdsql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Funcionario fun = new Funcionario();
                fun.setFun_cod(rs.getInt("fun_cod"));
                fun.setFun_nome(rs.getString("fun_nome"));
                fun.setFun_telefone(rs.getString("fun_telefone"));
                fun.setFun_email(rs.getString("fun_email"));
                fun.setFun_cpf(rs.getString("fun_cpf"));
                fun.setFun_usuario(rs.getString("fun_usuario"));
                fun.setFun_senha(rs.getString("fun_senha"));
                lista.add(fun);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar funcionário: " + e);
        }
        return lista;
    }

    public List<Funcionario> listarFuncionarios() {
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT fun_cod, fun_nome FROM tb_funcionario";
        try {
            PreparedStatement stmt = conecta.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Funcionario funcionario = new Funcionario();
                funcionario.setFun_cod(rs.getInt("fun_cod"));
                funcionario.setFun_nome(rs.getString("fun_nome"));
                funcionarios.add(funcionario);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar funcionários: " + e);
        }
        return funcionarios;
    }

    public ArrayList<Funcionario> pesquisarFuncionario(String campo, String pesquisar) {
        ArrayList<Funcionario> lista = new ArrayList<>();

        if (!campo.equals("fun_nome") && !campo.equals("fun_telefone") &&
            !campo.equals("fun_email") && !campo.equals("fun_cpf")) {
            JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido.");
            return lista;
        }

        String cmdsql = "SELECT * FROM tb_funcionario WHERE " + campo + " LIKE ? ORDER BY fun_nome ASC";
        try {
            PreparedStatement stmt = conecta.prepareStatement(cmdsql);
            stmt.setString(1, "%" + pesquisar + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Funcionario fun = new Funcionario();
                fun.setFun_cod(rs.getInt("fun_cod"));
                fun.setFun_nome(rs.getString("fun_nome"));
                fun.setFun_telefone(rs.getString("fun_telefone"));
                fun.setFun_email(rs.getString("fun_email"));
                fun.setFun_cpf(rs.getString("fun_cpf"));
                fun.setFun_usuario(rs.getString("fun_usuario"));
                fun.setFun_senha(rs.getString("fun_senha"));
                lista.add(fun);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao pesquisar funcionário: " + e);
        }

        return lista;
    }
 
    public boolean atualizarSenhaPorEmail(String email, String novaSenha) {
    try {
        String sql = "UPDATE tb_funcionario SET fun_senha = ? WHERE fun_email = ?";
        PreparedStatement stmt = conecta.prepareStatement(sql);
        stmt.setString(1, novaSenha);
        stmt.setString(2, email);

        int rowsUpdated = stmt.executeUpdate();
        stmt.close();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public boolean emailExiste(String email) {
    String sql = "SELECT 1 FROM tb_funcionario WHERE fun_email = ?";
    try (PreparedStatement stmt = conecta.prepareStatement(sql)) {
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao verificar e-mail: " + e.getMessage());
        return false;
    }
}

}
