/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import JDBC.ConnectionFactory;
import Objs.Estoque;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

public class AgendamentoProdutoDAO {

  private Connection conecta;

  public AgendamentoProdutoDAO() {
        this.conecta = new ConnectionFactory().conecta();
    }

    public void inserirProdutoUtilizado(int age_cod, int est_cod, java.sql.Date data) {

        try {
          String sql = "INSERT INTO tb_agendamento_produto (age_cod, est_cod, agprod_data) VALUES (?, ?, ?)";

    PreparedStatement stmt = conecta.prepareStatement(sql);
            stmt.setInt(1, age_cod);
            stmt.setInt(2, est_cod);
            stmt.setDate(3, data);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao registrar produto utilizado: " + e.getMessage());
        }
    }
    
  public void registrarProdutosUtilizados(int age_cod) {
    EstoqueDAO estoqueDAO = new EstoqueDAO();
    boolean continuar = true;

    while (continuar) {
        Estoque produto = estoqueDAO.selecionarProduto();

        if (produto == null) {
            JOptionPane.showMessageDialog(null, "Nenhum produto selecionado.");
            break;
        }

        String input = JOptionPane.showInputDialog(
            "Informe a quantidade utilizada de " + produto.getEst_nome_item() + 
            " (ou deixe vazio para registrar sem atualizar estoque):"
        );

        if (input == null) {
            // Usuário clicou em Cancelar
            // Registrar produto sem atualizar estoque
            this.inserirProdutoUtilizado(age_cod, produto.getEst_cod(), new java.sql.Date(System.currentTimeMillis()));
            JOptionPane.showMessageDialog(null, "Produto '" + produto.getEst_nome_item() + "' registrado sem atualizar estoque.");
            
            // Pergunta se deseja continuar adicionando produtos
            int opcao = JOptionPane.showConfirmDialog(null, "Deseja adicionar mais um produto?", "Adicionar Produto", JOptionPane.YES_NO_OPTION);
            if (opcao != JOptionPane.YES_OPTION) {
                continuar = false;
            }
            continue; // volta para o início do while
        }

        if (input.trim().isEmpty()) {
            // Só registra o uso, não altera estoque
            this.inserirProdutoUtilizado(age_cod, produto.getEst_cod(), new java.sql.Date(System.currentTimeMillis()));
            JOptionPane.showMessageDialog(null, "Produto '" + produto.getEst_nome_item() + "' registrado sem atualizar estoque.");
        } else {
            try {
                int quantidadeUtilizada = Integer.parseInt(input);

                if (quantidadeUtilizada <= 0) {
                    JOptionPane.showMessageDialog(null, "Quantidade inválida. Informe um número maior que zero.");
                    continue;
                }

                int novaQuantidade = produto.getEst_quant_item() - quantidadeUtilizada;

                if (novaQuantidade >= 0) {
                    estoqueDAO.atualizarQuantidade(produto.getEst_cod(), novaQuantidade);
                    this.inserirProdutoUtilizado(age_cod, produto.getEst_cod(), new java.sql.Date(System.currentTimeMillis()));
                    JOptionPane.showMessageDialog(null, "Produto '" + produto.getEst_nome_item() + "' atualizado. Estoque atual: " + novaQuantidade);
                } else {
                    JOptionPane.showMessageDialog(null, "Estoque insuficiente para " + produto.getEst_nome_item());
                    continue;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Digite um número válido.");
                continue;
            }
        }

        int opcao = JOptionPane.showConfirmDialog(null, "Deseja adicionar mais um produto?", "Adicionar Produto", JOptionPane.YES_NO_OPTION);
        if (opcao != JOptionPane.YES_OPTION) {
            continuar = false;
        }
    }
}

  
  public void excluirProdutoUtilizado(int age_cod, int est_cod) {
    String sql = "DELETE FROM tb_agendamento_produto WHERE age_cod = ? AND est_cod = ?";

    try (PreparedStatement stmt = conecta.prepareStatement(sql)) {
        stmt.setInt(1, age_cod);
        stmt.setInt(2, est_cod);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao excluir produto utilizado: " + e.getMessage());
    }
}

    
   public List<Object[]> listarProdutosUtilizados() {
    List<Object[]> lista = new ArrayList<>();

    String sql = "SELECT ap.age_cod, a.age_data, c.cli_nome, a.age_servico, e.est_nome_item, e.est_cod " +
                 "FROM tb_agendamento_produto ap " +
                 "JOIN tb_agendamento a ON ap.age_cod = a.age_cod " +
                 "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
                 "JOIN tb_estoque e ON ap.est_cod = e.est_cod " +
                 "ORDER BY a.age_data DESC";

    try (PreparedStatement stmt = conecta.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Object[] linha = new Object[6];
            linha[0] = rs.getInt("age_cod");
            linha[1] = rs.getDate("age_data");
            linha[2] = rs.getString("cli_nome");
            linha[3] = rs.getString("age_servico");
            linha[4] = rs.getString("est_nome_item");
            linha[5] = rs.getInt("est_cod");  // código do produto (para exclusão)
            lista.add(linha);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao buscar produtos utilizados: " + e.getMessage());
    }

    return lista;
}

   
   public List<Object[]> pesquisarProdutosUtilizados(String campo, String pesquisar) {
    List<Object[]> lista = new ArrayList<>();

    List<String> camposValidos = Arrays.asList(
        "a.age_data", "a.age_servico", "c.cli_nome", "e.est_nome_item"
    );

    if (!camposValidos.contains(campo)) {
        JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido.");
        return lista;
    }

    String sql;

    if (campo.equals("a.age_data")) {
        sql = "SELECT ap.age_cod, a.age_data, c.cli_nome, a.age_servico, e.est_nome_item " +
              "FROM tb_agendamento_produto ap " +
              "JOIN tb_agendamento a ON ap.age_cod = a.age_cod " +
              "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
              "JOIN tb_estoque e ON ap.est_cod = e.est_cod " +
              "WHERE DATE_FORMAT(a.age_data, '%d/%m/%Y') LIKE ? " +
              "ORDER BY a.age_data DESC";
    } else {
        sql = "SELECT ap.age_cod, a.age_data, c.cli_nome, a.age_servico, e.est_nome_item " +
              "FROM tb_agendamento_produto ap " +
              "JOIN tb_agendamento a ON ap.age_cod = a.age_cod " +
              "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
              "JOIN tb_estoque e ON ap.est_cod = e.est_cod " +
              "WHERE " + campo + " LIKE ? " +
              "ORDER BY a.age_data DESC";
    }

    try {
        PreparedStatement stmt = conecta.prepareStatement(sql);
        stmt.setString(1, "%" + pesquisar + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Object[] linha = {
                rs.getInt("age_cod"),
                rs.getDate("age_data"),
                rs.getString("cli_nome"),
                rs.getString("age_servico"),
                rs.getString("est_nome_item")
            };
            lista.add(linha);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro na busca: " + e.getMessage());
    }

    return lista;
}


   
}
