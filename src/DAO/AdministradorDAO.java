/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import JDBC.ConnectionFactory;
import Objs.Administrador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Aluno
 */
public class AdministradorDAO {
  private Connection conecta;
  
  public AdministradorDAO() {
      this.conecta = new ConnectionFactory().conecta();
  }
  
  public void IncluirAdministrador(Administrador adm) {
    try {
        String cmdsql = "INSERT INTO tb_adm "
                + "(adm_cod, adm_nome, adm_telefone, adm_email, adm_usuario, senha) "
                + "VALUES (default, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        stmt.setString(1, adm.getAdm_nome());
        stmt.setString(2, adm.getAdm_telefone());
        stmt.setString(3, adm.getAdm_email());
        stmt.setString(4, adm.getAdm_usuario());
        stmt.setString(5, adm.getAdm_senha());

        stmt.execute();
        JOptionPane.showMessageDialog(null, "Administrador salvo com sucesso!!!");
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao salvar Administrador: " + e);
    }
}

public void alterarAdministrador(Administrador adm) {
    try {
        String cmdsql = "UPDATE tb_adm SET "
                + "adm_nome = ?, adm_telefone = ?, adm_email = ?, adm_usuario = ?, senha = ? "
                + "WHERE adm_cod = ?";
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        stmt.setString(1, adm.getAdm_nome());
        stmt.setString(2, adm.getAdm_telefone());
        stmt.setString(3, adm.getAdm_email());
        stmt.setString(4, adm.getAdm_usuario());
        stmt.setString(5, adm.getAdm_senha());
        stmt.setInt(6, adm.getAdm_cod());

        stmt.execute();
        JOptionPane.showMessageDialog(null, "Administrador alterado com sucesso!!!");
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao alterar Administrador: " + e);
    }
}

public void excluirAdministrador(String adm_cod) {
    try {
        String cmdsql = "DELETE FROM tb_adm WHERE adm_cod = ?";
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        stmt.setInt(1, Integer.parseInt(adm_cod));
        int resp = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir esse Administrador?");
        if (resp == JOptionPane.YES_OPTION) {
            stmt.execute();
            JOptionPane.showMessageDialog(null, "Administrador excluído com sucesso!!");
        }
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao excluir Administrador: " + e);
    }
}

public ArrayList<Administrador> buscaAdministrador() {
    ArrayList<Administrador> lista = new ArrayList<>();
    String cmdsql = "SELECT * FROM tb_adm ORDER BY adm_nome ASC";
    try {
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Administrador adm = new Administrador();
            adm.setAdm_cod(rs.getInt("adm_cod"));
            adm.setAdm_nome(rs.getString("adm_nome"));
            adm.setAdm_telefone(rs.getString("adm_telefone"));
            adm.setAdm_email(rs.getString("adm_email"));
            adm.setAdm_usuario(rs.getString("adm_usuario")); // ✅ novo campo
            adm.setAdm_senha(rs.getString("senha"));
            lista.add(adm);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar Administrador: " + e);
    }
    return lista;
}

public ArrayList<Administrador> pesquisarAdministrador(String campo, String pesquisar) {
    ArrayList<Administrador> lista = new ArrayList<>();

    if (!campo.equals("adm_nome") && !campo.equals("adm_telefone") && !campo.equals("adm_email")) {
        JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido.");
        return lista;
    }

    String cmdsql = "SELECT * FROM tb_adm WHERE " + campo + " LIKE ? ORDER BY adm_nome ASC";
    try {
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        stmt.setString(1, "%" + pesquisar + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Administrador adm = new Administrador();
            adm.setAdm_cod(rs.getInt("adm_cod"));
            adm.setAdm_nome(rs.getString("adm_nome"));
            adm.setAdm_telefone(rs.getString("adm_telefone"));
            adm.setAdm_email(rs.getString("adm_email"));
            adm.setAdm_usuario(rs.getString("adm_usuario")); // ✅ novo campo
            adm.setAdm_senha(rs.getString("senha"));
            lista.add(adm);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar Administrador: " + e);
    }

    return lista;
}

public boolean atualizarSenhaPorEmail(String email, String novaSenha) {
    try {
        String sql = "UPDATE tb_adm SET senha = ? WHERE adm_email = ?";
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
    String sql = "SELECT 1 FROM tb_adm WHERE adm_email = ?";
    try (PreparedStatement stmt = conecta.prepareStatement(sql)) {
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        return rs.next(); // retorna true se encontrou
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao verificar e-mail: " + e.getMessage());
        return false;
    }
}


}
