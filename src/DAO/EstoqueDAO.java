/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import JDBC.ConnectionFactory;
import Objs.Estoque;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Aluno
 */
public class EstoqueDAO {

    private Connection conecta;

    public EstoqueDAO() {
        this.conecta = new ConnectionFactory().conecta();
    }

    public void incluirProduto(Estoque est) {
        try {
            String cmdsql = "insert into tb_estoque "
                    + "(est_cod,"
                    + "est_nome_item,"
                    + "est_quant_item,"
                    + "est_valor_item,"
                    + "est_validade_item,"
                    + "fornecedor_cod)"
                    + "values (default, ?,?,?,?,?)";
            PreparedStatement stmt = conecta.prepareStatement(cmdsql);
            stmt.setString(1, est.getEst_nome_item());
            stmt.setInt(2, est.getEst_quant_item());
            stmt.setFloat(3, est.getEst_valor_item());
            if (est.getEst_validade_item() != null) {
                stmt.setDate(4, new java.sql.Date(est.getEst_validade_item().getTime()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            stmt.setInt(5, est.getFornecedor_cod());
            stmt.execute();
            JOptionPane.showMessageDialog(null, "Produto Salvo no Estoque com sucesso!!!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar Produto no Estoque " + e);
        }
    }

    public void alterarEstoque(Estoque est) {
        try {
            String cmdsql = "update tb_estoque "
                    + "set est_nome_item = ?, "
                    + "est_quant_item = ?, "
                    + "est_valor_item = ?, "
                    + "est_validade_item = ? ,"
                    + "fornecedor_cod = ? "
                    + "where est_cod = ? ";
            PreparedStatement stmt = conecta.prepareStatement(cmdsql);
            stmt.setString(1, est.getEst_nome_item());
            stmt.setInt(2, est.getEst_quant_item());
            stmt.setFloat(3, est.getEst_valor_item());
            if (est.getEst_validade_item() != null) {
                stmt.setDate(4, new java.sql.Date(est.getEst_validade_item().getTime()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            stmt.setInt(5, est.getFornecedor_cod());
            stmt.setInt(6, est.getEst_cod());
            stmt.execute();
            JOptionPane.showMessageDialog(null, "Produto Alterado com sucesso!!!");
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao Alterar Produto " + e);
        }

    }

    public void excluirEstoque(String est_cod) {
        try {
            String cmdsql = "delete from tb_estoque where est_cod= " + est_cod;
            PreparedStatement stmt = conecta.prepareStatement(cmdsql);
            int resp = JOptionPane.showConfirmDialog(null, "Deseja realmente Remover esse Produto do Estoque?");
            if (resp == JOptionPane.YES_OPTION) {
                stmt.execute();
                JOptionPane.showMessageDialog(null, "Produto removido do Estoque com sucesso!!");
            }
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao Remover produto do Estoque");
        }
    }

    public ArrayList<Estoque> buscaEstoque() {
        ArrayList<Estoque> lista = new ArrayList<>();

        String cmdsql = "SELECT e.est_cod, e.est_nome_item, e.est_quant_item, e.est_valor_item, "
                + "e.est_validade_item, e.fornecedor_cod, f.for_nome "
                + "FROM tb_estoque e "
                + "JOIN tb_fornecedor f ON e.fornecedor_cod = f.for_cod "
                + "ORDER BY e.est_nome_item ASC";
        try {
            PreparedStatement stmt = conecta.prepareStatement(cmdsql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Estoque est = new Estoque();
                est.setEst_cod(rs.getInt("est_cod"));
                est.setEst_nome_item(rs.getString("est_nome_item"));
                est.setEst_quant_item(rs.getInt("est_quant_item"));
                est.setEst_valor_item(rs.getFloat("est_valor_item"));
                est.setEst_validade_item(rs.getDate("est_validade_item"));
                est.setFornecedor_cod(rs.getInt("fornecedor_cod"));
                est.setFornecedor_nome(rs.getString("for_nome")); // Agora funciona!
                lista.add(est);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar Produto do Estoque: " + e.getMessage());
        }

        return lista;
    }

    public ArrayList<Estoque> pesquisarEstoque(String campo, String pesquisar) {
        ArrayList<Estoque> lista = new ArrayList<>();

        // Lista de campos válidos
        List<String> camposValidos = Arrays.asList(
                "est_nome_item", "est_quant_item", "est_valor_item", "for_nome"
        );

        // Verificação de campo válido
        if (!camposValidos.contains(campo)) {
            JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido.");
            return lista;
        }

        // Definir prefixo da tabela (caso seja campo de fornecedor)
        String prefixo = "e."; // Padrão: tabela de estoque
        if (campo.equals("for_nome")) {
            prefixo = "f."; // Tabela fornecedor
        }

        // Consulta SQL dinâmica
        String cmdsql;
        if (campo.equals("for_nome")) {
            cmdsql = "SELECT e.*, f.for_nome "
                    + "FROM tb_estoque e "
                    + "JOIN tb_fornecedor f ON e.fornecedor_cod = f.for_cod "
                    + "WHERE f.for_nome LIKE ? "
                    + "ORDER BY e.est_nome_item ASC";
        } else {
            cmdsql = "SELECT e.*, f.for_nome "
                    + "FROM tb_estoque e "
                    + "JOIN tb_fornecedor f ON e.fornecedor_cod = f.for_cod "
                    + "WHERE " + prefixo + campo + " LIKE ? "
                    + "ORDER BY e.est_nome_item ASC";
        }

        try {
            PreparedStatement stmt = conecta.prepareStatement(cmdsql);
            stmt.setString(1, "%" + pesquisar + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Estoque est = new Estoque();
                est.setEst_cod(rs.getInt("est_cod"));
                est.setEst_nome_item(rs.getString("est_nome_item"));
                est.setEst_quant_item(rs.getInt("est_quant_item"));
                est.setEst_valor_item(rs.getFloat("est_valor_item"));
                est.setEst_validade_item(rs.getDate("est_validade_item"));
                est.setFornecedor_cod(rs.getInt("fornecedor_cod"));
                est.setFornecedor_nome(rs.getString("for_nome")); // Nome do fornecedor vindo do JOIN

                lista.add(est);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar Produto do Estoque: " + e);
        }

        return lista;
    }

    public List<Estoque> buscarPorValidade(Date dataInicio, Date dataFim) {
        List<Estoque> lista = new ArrayList<>();
        String sql = "SELECT e.*, f.for_nome FROM tb_estoque e "
                + "JOIN tb_fornecedor f ON e.fornecedor_cod = f.for_cod "
                + "WHERE e.est_validade_item BETWEEN ? AND ?";
        try {
            PreparedStatement stmt = conecta.prepareStatement(sql);

            // Converte java.util.Date para java.sql.Date
            stmt.setDate(1, new java.sql.Date(dataInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(dataFim.getTime()));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Estoque e = new Estoque();
                e.setEst_cod(rs.getInt("est_cod"));
                e.setEst_nome_item(rs.getString("est_nome_item"));
                e.setEst_quant_item(rs.getInt("est_quant_item"));
                e.setEst_valor_item(rs.getFloat("est_valor_item"));
                e.setEst_validade_item(rs.getDate("est_validade_item"));
                e.setFornecedor_nome(rs.getString("for_nome"));
                lista.add(e);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar por validade: " + e.getMessage());
        }
        return lista;
    }

    public List<Estoque> buscarPorValor(double valorMin, double valorMax) {
        List<Estoque> lista = new ArrayList<>();

        String sql = "SELECT e.*, f.for_nome "
                + "FROM tb_estoque e "
                + "JOIN tb_fornecedor f ON e.fornecedor_cod = f.for_cod "
                + "WHERE CAST(e.est_valor_item AS DECIMAL(10,2)) BETWEEN ? AND ?";

        try {
            PreparedStatement stmt = conecta.prepareStatement(sql);

            stmt.setFloat(1, (float) valorMin);
            stmt.setFloat(2, (float) valorMax);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Estoque est = new Estoque();
                est.setEst_cod(rs.getInt("est_cod"));
                est.setEst_nome_item(rs.getString("est_nome_item"));
                est.setEst_quant_item(rs.getInt("est_quant_item"));
                est.setEst_valor_item(rs.getFloat("est_valor_item"));
                est.setEst_validade_item(rs.getDate("est_validade_item"));
                est.setFornecedor_nome(rs.getString("for_nome"));

                lista.add(est);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar por valor: " + e.getMessage());
        }

        return lista;
    }

    public Estoque selecionarProduto() {
        EstoqueDAO estoqueDAO = new EstoqueDAO();
        ArrayList<Estoque> produtos = estoqueDAO.buscaEstoque();

        if (produtos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum produto disponível no estoque.");
            return null;
        } else {
            String[] nomesProdutos = new String[produtos.size()];
            for (int i = 0; i < produtos.size(); i++) {
                nomesProdutos[i] = produtos.get(i).getEst_nome_item();
            }

            String produtoEscolhido = (String) JOptionPane.showInputDialog(
                    null,
                    "Selecione o produto para o agendamento:",
                    "Selecionar Produto",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    nomesProdutos,
                    nomesProdutos[0]
            );

            if (produtoEscolhido != null) {
                for (Estoque est : produtos) {
                    if (est.getEst_nome_item().equals(produtoEscolhido)) {
                        return est; // ✅ Retorna o objeto Estoque selecionado
                    }
                }
            }
        }
        return null; // Nenhum produto foi selecionado
    }

    public void atualizarQuantidade(int idProduto, int novaQuantidade) {
        String sql = "UPDATE tb_estoque SET est_quant_item = ? WHERE est_cod = ?";

        try (PreparedStatement stmt = conecta.prepareStatement(sql)) {
            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, idProduto);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao atualizar o estoque.");
        }
    }

}
