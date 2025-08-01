/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import JDBC.ConnectionFactory;
import Objs.Caixa;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Aluno
 */
public class CaixaDAO {
  private Connection conecta;

public CaixaDAO() {
    this.conecta = new ConnectionFactory().conecta();
}  

public void incluirCaixa(Caixa cai) {
    try {
        String cmdsql = "insert into tb_caixa "
                + "(cai_cod,"
                + "cai_tipo,"
                + "cai_descricao," 
                + "cai_valor,"
                + "cai_data," 
                + "cai_forma_pag)"
        + "values (default, ?,?,?,?,?)";
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        stmt.setString(1, cai.getCai_tipo());
        stmt.setString(2, cai.getCai_descricao());
        stmt.setFloat(3, cai.getCai_valor() );
         java.sql.Date dataSql = new java.sql.Date(cai.getCai_data().getTime());
        stmt.setDate(4, dataSql);
        stmt.setString(5, cai.getCai_forma_pag());
        stmt.execute();
        JOptionPane.showMessageDialog(null, "Pagamento Salvo no Caixa com sucesso!!!");
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao Salvar Pagamento no Caixa "+  e);
    }
}

public void alterarCaixa(Caixa cai) {
    try {
        String cmdsql = "update tb_caixa " 
                + "set cai_tipo = ?, " 
                + "cai_descricao = ?, "
                + "cai_valor = ?, " 
                + "cai_data = ?, " 
                + "cai_forma_pag = ? "
                + "where cai_cod = ? ";
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        stmt.setString(1, cai.getCai_tipo());
        stmt.setString(2, cai.getCai_descricao());
        stmt.setFloat(3, cai.getCai_valor());
         java.sql.Date dataSql = new java.sql.Date(cai.getCai_data().getTime());
        stmt.setDate(4, dataSql);
        stmt.setString(5, cai.getCai_forma_pag());
        stmt.setInt(6, cai.getCai_cod());
        stmt.execute();
        JOptionPane.showMessageDialog(null, "Pagamento do Caixa Alterado com sucesso!!!");
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao Alterar Pagamento do Caixa "+e);
    }
}

public void excluirCaixa(String cai_cod) {
    try {
        String cmdsql = "delete from tb_caixa where cai_cod=" +cai_cod;
        PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        int resp = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir esse Pagamento do Caixa?");
        if (resp == JOptionPane.YES_OPTION) {
            stmt.execute();
            JOptionPane.showMessageDialog(null, "Pagamento excluido com sucesso!!");
        }        
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao excluir Pagamento do Caixa"+e);
    }
}

public ArrayList<Caixa> buscaCaixa() {
    ArrayList<Caixa> lista = new ArrayList();
    String cmdsql = "select * from tb_caixa order by cai_data asc";
    try {
       PreparedStatement stmt = conecta.prepareStatement(cmdsql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Caixa cai = new Caixa();
            cai.setCai_cod(rs.getInt("cai_cod"));
            cai.setCai_tipo(rs.getString("cai_tipo"));
            cai.setCai_descricao(rs.getString("cai_descricao"));
            cai.setCai_valor(rs.getFloat("cai_valor"));
            cai.setCai_data(rs.getDate("cai_data"));
            cai.setCai_forma_pag(rs.getString("cai_forma_pag"));
            lista.add(cai);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar Pagamento do caixa"+e);
    }
    return lista;
}

public ArrayList<Caixa> pesquisarCaixa(String campo, String pesquisar) {
    ArrayList<Caixa> lista = new ArrayList<>();
    PreparedStatement stmt = null;
    ResultSet rs = null;

    // Validação para evitar SQL injection ou erros de digitação
    if (!campo.equals("cai_tipo") && !campo.equals("cai_descricao") &&
        !campo.equals("cai_forma_pag") && !campo.equals("cai_data")) {
        JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido.");
        return lista;
    }

    String cmdsql = "";

    try {
        if (campo.equals("cai_data")) {
            cmdsql = "SELECT * FROM tb_caixa " +
                     "WHERE DATE_FORMAT(cai_data, '%d/%m/%Y') LIKE ? " +
                     "ORDER BY cai_data ASC";
            stmt = conecta.prepareStatement(cmdsql);
            stmt.setString(1, "%" + pesquisar + "%");
        } else {
            cmdsql = "SELECT * FROM tb_caixa " +
                     "WHERE " + campo + " LIKE ? " +
                     "ORDER BY cai_data ASC";
            stmt = conecta.prepareStatement(cmdsql);
            stmt.setString(1, "%" + pesquisar + "%");
        }

        rs = stmt.executeQuery();

        while (rs.next()) {
            Caixa cai = new Caixa();
            cai.setCai_cod(rs.getInt("cai_cod"));
            cai.setCai_tipo(rs.getString("cai_tipo"));
            cai.setCai_descricao(rs.getString("cai_descricao"));
            cai.setCai_valor(rs.getFloat("cai_valor"));
            cai.setCai_data(rs.getDate("cai_data"));
            cai.setCai_forma_pag(rs.getString("cai_forma_pag"));
            lista.add(cai);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar Caixa: " + e);
    }

    return lista;
}


// Retorna o total de ENTRADAS
public float buscarTotalEntradas() {
    float total = 0.0f;
   String sql = "SELECT SUM(cai_valor) AS total FROM tb_caixa WHERE cai_tipo = 'ENTRADA'";
    try {
        PreparedStatement stmt = conecta.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            total = rs.getFloat("total");
        }
        rs.close();
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao calcular total de Entradas: " + e);
    }
    return total;
}

// Retorna o total de SAÍDAS
public float buscarTotalSaidas() {
    float total = 0.0f;
    String sql = "SELECT SUM(cai_valor) AS total FROM tb_caixa WHERE cai_tipo = 'SAIDA'";
    try {
        PreparedStatement stmt = conecta.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            total = rs.getFloat("total");
        }
        rs.close();
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao calcular total de Saídas: " + e);
    }
    return total;
}

// Método para buscar o total de ENTRADAS do dia de hoje
public float buscarTotalEntradasDiaAtual() {
    float total = 0.0f;
    String sql = "SELECT SUM(cai_valor) AS total FROM tb_caixa WHERE cai_tipo = 'ENTRADA' AND cai_data = CURDATE()"; // Filtra pela data atual
    try {
        PreparedStatement stmt = conecta.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            total = rs.getFloat("total");
        }
        rs.close();
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao calcular total de Entradas do dia: " + e);
    }
    return total;
}

// Método para buscar o total de SAÍDAS do dia de hoje
public float buscarTotalSaidasDiaAtual() {
    float total = 0.0f;
    String sql = "SELECT SUM(cai_valor) AS total FROM tb_caixa WHERE cai_tipo = 'SAIDA' AND cai_data = CURDATE()"; // Filtra pela data atual
    try {
        PreparedStatement stmt = conecta.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            total = rs.getFloat("total");
        }
        rs.close();
        stmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao calcular total de Saídas do dia: " + e);
    }
    return total;
}


public ArrayList<Caixa> buscarPorPeriodo(java.sql.Date inicio, java.sql.Date fim, List<String> tiposTransacao) {
    ArrayList<Caixa> lista = new ArrayList<>();
    String sql = "SELECT * FROM tb_caixa WHERE cai_data BETWEEN ? AND ?";
    
      // Adicionar o filtro por múltiplos tipos de transação
    if (tiposTransacao != null && !tiposTransacao.isEmpty()) {
        sql += " AND cai_tipo IN (" + String.join(",", Collections.nCopies(tiposTransacao.size(), "?")) + ")";
    }
    try {
        PreparedStatement stmt = conecta.prepareStatement(sql);

        // Usa as datas diretamente, sem conversão
        stmt.setDate(1, inicio);
        stmt.setDate(2, fim);

        int index = 3;
        for (String tipo : tiposTransacao) {
            stmt.setString(index++, tipo);
        }
        
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Caixa c = new Caixa();
            c.setCai_cod(rs.getInt("cai_cod"));
            c.setCai_tipo(rs.getString("cai_tipo"));
            c.setCai_descricao(rs.getString("cai_descricao"));
            c.setCai_valor(rs.getFloat("cai_valor"));
            c.setCai_data(rs.getDate("cai_data")); // está correto se o atributo for java.util.Date ou java.sql.Date
            c.setCai_forma_pag(rs.getString("cai_forma_pag"));
            lista.add(c);
        }
    } catch (Exception e) {
        System.out.println("Erro ao buscar por período: " + e.getMessage());
        e.printStackTrace();
    }

    return lista;

    }


public void gerarRelatorioDiarioPDF() {
    LocalDate hoje = LocalDate.now();
    String dataHojeFormatada = hoje.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    String nomeArquivo = String.format("RelatorioDiario-%s.pdf", dataHojeFormatada);

    File pasta = new File("E:\\SGJussaraCabelereira\\SGJussaraCabelereira\\RelatórioDiarioCaixa");
    if (!pasta.exists()) pasta.mkdirs();

    File arquivo = new File(pasta, nomeArquivo);

    Document doc = new Document();

    try {
        PdfWriter.getInstance(doc, new FileOutputStream(arquivo));
        doc.open();

        // Título
        Paragraph titulo = new Paragraph("RELATÓRIO DIÁRIO DO CAIXA",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        doc.add(titulo);

        // Data
        Paragraph dataRelatorio = new Paragraph(
            "Data: " + hoje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK));
        dataRelatorio.setAlignment(Element.ALIGN_LEFT);
        doc.add(dataRelatorio);

        // Tabela
        PdfPTable tabela = new PdfPTable(5);
        tabela.setWidthPercentage(100);
        tabela.setSpacingBefore(15f);
        tabela.setSpacingAfter(15f);
        tabela.setWidths(new float[]{2f, 6f, 2f, 2f, 3f});

        // Cabeçalhos
        String[] cabecalhos = {"Data", "Descrição", "Entrada", "Saída", "Forma de Pagamento"};
        for (String cab : cabecalhos) {
            PdfPCell header = new PdfPCell(new Phrase(cab,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            header.setBackgroundColor(new BaseColor(0, 102, 204));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setPadding(8);
            tabela.addCell(header);
        }

        // Dados do dia
        CaixaDAO dao = new CaixaDAO();
        java.sql.Date sqlHoje = java.sql.Date.valueOf(hoje);
        ArrayList<Caixa> lista = dao.buscarPorPeriodo(sqlHoje, sqlHoje, Arrays.asList("ENTRADA", "SAIDA"));

        float totalEntradas = 0, totalSaidas = 0;
        DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Caixa c : lista) {
            LocalDate data = ((java.sql.Date) c.getCai_data()).toLocalDate();
            tabela.addCell(data.format(dataFormatter));

            PdfPCell cellDescricao = new PdfPCell(new Phrase(c.getCai_descricao()));
            cellDescricao.setPadding(5);
            tabela.addCell(cellDescricao);

            if ("ENTRADA".equalsIgnoreCase(c.getCai_tipo())) {
                tabela.addCell(new PdfPCell(new Phrase(String.format("R$ %.2f", c.getCai_valor()))));
                tabela.addCell(new PdfPCell(new Phrase("0,00")));
                totalEntradas += c.getCai_valor();
            } else {
                tabela.addCell(new PdfPCell(new Phrase("0,00")));
                tabela.addCell(new PdfPCell(new Phrase(String.format("R$ %.2f", c.getCai_valor()))));
                totalSaidas += c.getCai_valor();
            }

            tabela.addCell(new PdfPCell(new Phrase(c.getCai_forma_pag())));
        }

        doc.add(tabela);

        float saldoAtual = totalEntradas - totalSaidas;

        Paragraph total = new Paragraph();
        total.add("Total de Entradas: R$ " + String.format("%.2f", totalEntradas) + "\n");
        total.add("Total de Saídas: R$ " + String.format("%.2f", totalSaidas) + "\n");
        total.add("Saldo do Dia: R$ " + String.format("%.2f", saldoAtual) + "\n");
        doc.add(total);

        Paragraph rodape = new Paragraph("\nRelatório gerado automaticamente em: " + dataHojeFormatada,
            FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY));
        rodape.setAlignment(Element.ALIGN_RIGHT);
        doc.add(rodape);

        doc.close();

        JOptionPane.showMessageDialog(null, "Relatório diário gerado com sucesso!");
        Desktop.getDesktop().open(arquivo);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erro ao gerar relatório diário: " + e.getMessage());
    }
}


}

