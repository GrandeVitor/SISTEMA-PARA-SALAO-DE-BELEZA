/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import JDBC.ConnectionFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.toedter.calendar.JDateChooser;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author User
 */
public class RelatorioProdutosUtilizados {
   
   // ... (importações permanecem iguais)

public void gerarRelatorioPorPeriodo() {
    int resposta = JOptionPane.showConfirmDialog(
            null,
            "Deseja filtrar o relatório por período específico?",
            "Filtro de Período",
            JOptionPane.YES_NO_OPTION
    );

    Date dataInicial = null;
    Date dataFinal = null;

    if (resposta == JOptionPane.YES_OPTION) {
        JDateChooser dataInicialChooser = new JDateChooser();
        dataInicialChooser.setDateFormatString("dd/MM/yyyy");
        JDateChooser dataFinalChooser = new JDateChooser();
        dataFinalChooser.setDateFormatString("dd/MM/yyyy");

        JPanel painel = new JPanel(new GridLayout(2, 2, 10, 10));
        painel.add(new JLabel("Data Inicial (opcional):"));
        painel.add(dataInicialChooser);
        painel.add(new JLabel("Data Final (opcional):"));
        painel.add(dataFinalChooser);

        int option = JOptionPane.showConfirmDialog(null, painel, "Selecione o Período para o Relatório", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Operação cancelada.");
            return;
        }

        dataInicial = dataInicialChooser.getDate();
        dataFinal = dataFinalChooser.getDate();

        if (dataInicial != null && dataFinal != null && dataFinal.before(dataInicial)) {
            JOptionPane.showMessageDialog(null, "Data final não pode ser anterior à data inicial!");
            return;
        }
    } else if (resposta != JOptionPane.NO_OPTION) {
        return;
    }

      // 🔥 Gera nome do arquivo com data e hora
    SimpleDateFormat sdfArquivo = new SimpleDateFormat("dd-MM-yyyy_HH-mm");
    String nomeArquivo = "RelatorioProdutosUtilizados_" + sdfArquivo.format(new Date()) + ".pdf";

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setSelectedFile(new File(nomeArquivo));
    int escolha = fileChooser.showSaveDialog(null);

    if (escolha != JFileChooser.APPROVE_OPTION) {
        JOptionPane.showMessageDialog(null, "Operação cancelada.");
        return;
    }

    File arquivo = fileChooser.getSelectedFile();

    try (Connection conecta = new ConnectionFactory().conecta()) {

        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(arquivo));
        doc.open();

        // -------- Cores e Formatação
        BaseColor corCabecalho = new BaseColor(60, 90, 153); // Azul elegante
        BaseColor corTextoCabecalho = BaseColor.WHITE;
        BaseColor corBorda = BaseColor.LIGHT_GRAY;

        // -------- Título
        Paragraph titulo = new Paragraph("RELATÓRIO DE PRODUTOS UTILIZADOS",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingBefore(10);
        titulo.setSpacingAfter(15);
        doc.add(titulo);

        // -------- Período
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String periodoTexto;
        if (dataInicial == null && dataFinal == null) {
            periodoTexto = "Período: Todos os registros";
        } else if (dataInicial != null && dataFinal == null) {
            periodoTexto = "Período: De " + sdf.format(dataInicial) + " até o último registro";
        } else if (dataInicial == null && dataFinal != null) {
            periodoTexto = "Período: Até " + sdf.format(dataFinal);
        } else {
            periodoTexto = "Período: De " + sdf.format(dataInicial) + " até " + sdf.format(dataFinal);
        }

        Paragraph periodo = new Paragraph(periodoTexto + "\n\n",
                FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY));
        periodo.setAlignment(Element.ALIGN_LEFT);
        doc.add(periodo);

        // -------- Separador
        LineSeparator linha = new LineSeparator();
        linha.setLineColor(BaseColor.LIGHT_GRAY);
        doc.add(new Chunk(linha));

        // -------- Tabela Detalhada
        PdfPTable tabela = new PdfPTable(5);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{2.5f, 2.5f, 2.5f, 2.5f, 2.5f});
        tabela.setSpacingBefore(10);

        String[] cabecalhos = {"Agendamento", "Cliente", "Serviço", "Produto", "Data"};
        for (String cab : cabecalhos) {
            PdfPCell header = new PdfPCell(new Phrase(cab,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, corTextoCabecalho)));
            header.setBackgroundColor(corCabecalho);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setPadding(8);
            header.setBorderColor(corBorda);
            tabela.addCell(header);
        }

        String sql = "SELECT ap.age_cod, c.cli_nome, a.age_servico, e.est_nome_item, a.age_data " +
                "FROM tb_agendamento_produto ap " +
                "JOIN tb_agendamento a ON ap.age_cod = a.age_cod " +
                "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
                "JOIN tb_estoque e ON ap.est_cod = e.est_cod ";

        boolean temDataInicial = dataInicial != null;
        boolean temDataFinal = dataFinal != null;

        if (temDataInicial && temDataFinal) {
            sql += "WHERE a.age_data BETWEEN ? AND ? ";
        } else if (temDataInicial) {
            sql += "WHERE a.age_data >= ? ";
        } else if (temDataFinal) {
            sql += "WHERE a.age_data <= ? ";
        }

        sql += "ORDER BY a.age_data ASC";

        PreparedStatement stmt = conecta.prepareStatement(sql);

        if (temDataInicial && temDataFinal) {
            stmt.setDate(1, new java.sql.Date(dataInicial.getTime()));
            stmt.setDate(2, new java.sql.Date(dataFinal.getTime()));
        } else if (temDataInicial) {
            stmt.setDate(1, new java.sql.Date(dataInicial.getTime()));
        } else if (temDataFinal) {
            stmt.setDate(1, new java.sql.Date(dataFinal.getTime()));
        }

        ResultSet rs = stmt.executeQuery();

        int contador = 0;
        while (rs.next()) {
            contador++;
            tabela.addCell(criarCelula(String.valueOf(rs.getInt("age_cod"))));
            tabela.addCell(criarCelula(rs.getString("cli_nome")));
            tabela.addCell(criarCelula(rs.getString("age_servico")));
            tabela.addCell(criarCelula(rs.getString("est_nome_item")));
            tabela.addCell(criarCelula(sdf.format(rs.getDate("age_data"))));
        }

        if (contador == 0) {
            JOptionPane.showMessageDialog(null, "Nenhum registro encontrado para o período informado.");
            doc.close();
            return;
        }

        doc.add(tabela);

        // -------- Resumo
        Paragraph resumoTitulo = new Paragraph("\nResumo de Produtos Utilizados\n\n",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY));
        resumoTitulo.setAlignment(Element.ALIGN_LEFT);
        doc.add(resumoTitulo);

        PdfPTable tabelaResumo = new PdfPTable(2);
        tabelaResumo.setWidthPercentage(50);
        tabelaResumo.setWidths(new float[]{4f, 2f});
        tabelaResumo.setHorizontalAlignment(Element.ALIGN_LEFT);

        String[] cabecalhosResumo = {"Produto", "Quantidade"};
        for (String cab : cabecalhosResumo) {
            PdfPCell header = new PdfPCell(new Phrase(cab,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, corTextoCabecalho)));
            header.setBackgroundColor(corCabecalho);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setPadding(8);
            header.setBorderColor(corBorda);
            tabelaResumo.addCell(header);
        }

        String sqlResumo = "SELECT e.est_nome_item, COUNT(*) AS qtd_usada " +
                "FROM tb_agendamento_produto ap " +
                "JOIN tb_estoque e ON ap.est_cod = e.est_cod " +
                "JOIN tb_agendamento a ON ap.age_cod = a.age_cod " +
                "WHERE (a.age_data >= ? OR ? IS NULL) AND (a.age_data <= ? OR ? IS NULL) " +
                "GROUP BY e.est_nome_item " +
                "ORDER BY qtd_usada DESC";

        PreparedStatement stmtResumo = conecta.prepareStatement(sqlResumo);

        if (temDataInicial && temDataFinal) {
            stmtResumo.setDate(1, new java.sql.Date(dataInicial.getTime()));
            stmtResumo.setDate(2, new java.sql.Date(dataInicial.getTime()));
            stmtResumo.setDate(3, new java.sql.Date(dataFinal.getTime()));
            stmtResumo.setDate(4, new java.sql.Date(dataFinal.getTime()));
        } else if (temDataInicial) {
            stmtResumo.setDate(1, new java.sql.Date(dataInicial.getTime()));
            stmtResumo.setDate(2, new java.sql.Date(dataInicial.getTime()));
            stmtResumo.setNull(3, Types.DATE);
            stmtResumo.setNull(4, Types.DATE);
        } else if (temDataFinal) {
            stmtResumo.setNull(1, Types.DATE);
            stmtResumo.setNull(2, Types.DATE);
            stmtResumo.setDate(3, new java.sql.Date(dataFinal.getTime()));
            stmtResumo.setDate(4, new java.sql.Date(dataFinal.getTime()));
        } else {
            stmtResumo.setNull(1, Types.DATE);
            stmtResumo.setNull(2, Types.DATE);
            stmtResumo.setNull(3, Types.DATE);
            stmtResumo.setNull(4, Types.DATE);
        }

        ResultSet rsResumo = stmtResumo.executeQuery();

        while (rsResumo.next()) {
            tabelaResumo.addCell(criarCelula(rsResumo.getString("est_nome_item")));
            tabelaResumo.addCell(criarCelula(String.valueOf(rsResumo.getInt("qtd_usada"))));
        }

        doc.add(tabelaResumo);

        // -------- Rodapé
        Paragraph rodape = new Paragraph("\n\nRelatório gerado em: " + sdf.format(new Date()),
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY));
        rodape.setAlignment(Element.ALIGN_RIGHT);
        doc.add(rodape);

        doc.close();

        JOptionPane.showMessageDialog(null, "Relatório gerado com sucesso!");

        if (arquivo.exists()) {
            Desktop.getDesktop().open(arquivo);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erro ao gerar relatório: " + e.getMessage());
        e.printStackTrace();
    }
}

// 🔸 Método Auxiliar para células
private PdfPCell criarCelula(String texto) {
    PdfPCell cell = new PdfPCell(new Phrase(texto,
            FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK)));
    cell.setPadding(6);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setBorderColor(BaseColor.LIGHT_GRAY);
    return cell;
}

}
