/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import JDBC.ConnectionFactory;
import Objs.Fornecedor;
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
public class FornecedorDAO {
   private Connection conecta;
   
   public FornecedorDAO() {
     this.conecta = new ConnectionFactory().conecta();  
}

 public void incluirFornecedor(Fornecedor forn) {
     try {
         String cmdsql = "insert into tb_fornecedor " 
                 + "(for_cod," 
                 + "for_nome,"
                 + "for_telefone,"
                 + "for_cpf,"
                 + "for_cnpj,"
                 + "for_endereco,"
                 + "for_email,"
                 + "for_item)" + "values (default,?,?,?,?,?,?,?)";  
     PreparedStatement stmt = conecta.prepareStatement(cmdsql);
     stmt.setString(1, forn.getForn_nome());
     stmt.setString(2, forn.getForn_telefone());
     stmt.setString(3, forn.getForn_cpf());
     stmt.setString(4, forn.getForn_cnpj());
     stmt.setString(5, forn.getForn_endereco());
     stmt.setString(6, forn.getForn_email());
     stmt.setString(7, forn.getForn_item());
     stmt.execute();
            JOptionPane.showMessageDialog(null, "Fornecedor Salvo com sucesso!!!");
     stmt.close();
     } catch (SQLException e) {
         JOptionPane.showMessageDialog(null, "Erro ao Salvar Fornecedor "+ e);
     }
 }  
 
  public void alterarFornecedor(Fornecedor forn) {
      try {
          String cmdsql = "update tb_fornecedor "
                  + "set for_nome = ?, " 
                  + "for_telefone = ?, "
                  + "for_cpf = ?, " 
                  + "for_cnpj = ?, " 
                  + "for_endereco = ?, "
                  + "for_email = ?, "
                  + "for_item = ? "
                  + "where for_cod = ? ";
          PreparedStatement stmt = conecta.prepareStatement(cmdsql);
          stmt.setString(1, forn.getForn_nome());
          stmt.setString(2, forn.getForn_telefone());
          stmt.setString(3, forn.getForn_cpf());
          stmt.setString(4, forn.getForn_cnpj());
          stmt.setString(5, forn.getForn_endereco());
          stmt.setString(6, forn.getForn_email());
          stmt.setString(7, forn.getForn_item());
          stmt.setInt(8, forn.getForn_cod());
          stmt.execute();
          JOptionPane.showMessageDialog(null, "Fornecedor Alterado com sucesso!!!");
          stmt.close();
      } catch (SQLException e) {
          JOptionPane.showMessageDialog(null, "Erro ao Alterar Fornecedor "+e);
      }
  } 
 
  public void excluirFornecedor(String forn_cod) {
      try {
          
            // Verificar se o fornecedor produtos cadastrados no Estoque
        String sqlCheck = "SELECT COUNT(*) FROM tb_estoque WHERE fornecedor_cod = ?";
        PreparedStatement stmtCheck = conecta.prepareStatement(sqlCheck);
        stmtCheck.setInt(1, Integer.parseInt(forn_cod));
        ResultSet rs = stmtCheck.executeQuery();
        
        if (rs.next() && rs.getInt(1) > 0) {
            // Fornecedor possui produtos cadastrados, perguntar ao usuário
            int resposta = JOptionPane.showConfirmDialog(null, 
                    "Este Fornecedor possui Produtos registrados no Estoque. Deseja excluí-lo mesmo assim?", 
                    "Confirmação", JOptionPane.YES_NO_OPTION);
            
            if (resposta != JOptionPane.YES_OPTION) {
                return; // Cancelar a exclusão
            }
        }
          
          String cmdsql = "delete from tb_fornecedor where for_cod="+forn_cod;
          PreparedStatement stmt = conecta.prepareStatement(cmdsql);
          int resp =JOptionPane.showConfirmDialog(null, "Deseja realmente excluir esse fornecedor?");
          if (resp == JOptionPane.YES_OPTION) {
              stmt.execute();
              JOptionPane.showMessageDialog(null, "Fornecedor excluido com sucesso!!");
          }
          stmt.close();
      } catch (SQLException e) {
          JOptionPane.showMessageDialog(null, "Erro ao excluir Fornecedor"+e.getMessage());
      }
  }
  
  public ArrayList<Fornecedor> buscaFornecedor() {
      ArrayList<Fornecedor> lista = new ArrayList();
      String cmdsql = "select * from tb_fornecedor order by for_nome asc";
      try {
          PreparedStatement stmt = conecta.prepareStatement(cmdsql);
          ResultSet rs = stmt.executeQuery();
          while (rs.next()) {
              Fornecedor forn = new Fornecedor();
              forn.setForn_cod(rs.getInt("for_cod"));
              forn.setForn_nome(rs.getString("for_nome"));
              forn.setForn_telefone(rs.getString("for_telefone"));
              forn.setForn_cpf(rs.getString("for_cpf"));
              forn.setForn_cnpj(rs.getString("for_cnpj"));
              forn.setForn_endereco(rs.getString("for_endereco"));
              forn.setForn_email(rs.getString("for_email"));
              forn.setForn_item(rs.getString("for_item"));
              lista.add(forn);
          }
      } catch (SQLException e) {
          JOptionPane.showMessageDialog(null, "Erro ao buscar Fornecedor"+e);
      }
       return lista;
  }
  
  public List<Fornecedor> listarFornecedores() {
    List<Fornecedor> fornecedores = new ArrayList<>();
    String sql = "SELECT for_cod, for_nome FROM tb_fornecedor"; 

    try {
         PreparedStatement stmt = conecta.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery(); 

        while (rs.next()) {
            Fornecedor fornecedor = new Fornecedor();
            fornecedor.setForn_cod(rs.getInt("for_cod"));
            fornecedor.setForn_nome(rs.getString("for_nome"));
            fornecedores.add(fornecedor);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar fornecedores: " + e.getMessage());
    }
    
    return fornecedores;
}
   
 
  public ArrayList<Fornecedor> pesquisarFornecedor(String campo, String pesquisar) {
    ArrayList<Fornecedor> lista = new ArrayList<>();
    
    // Validação para garantir que o campo seja válido
    if (!campo.equals("for_nome") && !campo.equals("for_telefone") && 
        !campo.equals("for_cpf") && !campo.equals("for_cnpj") &&
        !campo.equals("for_endereco") && !campo.equals("for_email") &&
        !campo.equals("for_item")) {
        JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido.");
        return lista;
    }

    // Consulta SQL utilizando o campo de pesquisa escolhido
    String cmdsql = "SELECT * FROM tb_fornecedor WHERE " + campo + " LIKE ? ORDER BY for_nome ASC";
    
    try {
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        stmt.setString(1, "%" + pesquisar + "%");  // Parâmetro seguro para pesquisa
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Fornecedor forn = new Fornecedor();
            forn.setForn_cod(rs.getInt("for_cod"));
            forn.setForn_nome(rs.getString("for_nome"));
            forn.setForn_telefone(rs.getString("for_telefone"));
            forn.setForn_cpf(rs.getString("for_cpf"));
            forn.setForn_cnpj(rs.getString("for_cnpj"));
            forn.setForn_endereco(rs.getString("for_endereco"));
            forn.setForn_email(rs.getString("for_email"));
            forn.setForn_item(rs.getString("for_item"));
            lista.add(forn);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar Fornecedor: " + e);
    }
    
    return lista;
}

}
