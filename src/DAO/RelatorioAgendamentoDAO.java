/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import JDBC.ConnectionFactory;
import Objs.Agendamento;
import java.sql.Connection;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import javax.swing.*;
import java.text.*;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class RelatorioAgendamentoDAO {
 private Connection conecta;
 
 public RelatorioAgendamentoDAO() {
     this.conecta = new ConnectionFactory().conecta();
     
 }   
  
public void gerarRelatorio(String status, Date dataInicio, Date dataFim, int funcionarioCod, int clienteCod, String arquivoSalvar) {
    Connection conecta = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Document document = new Document(PageSize.A4, 50, 50, 50, 50); // Margens

    try {
        // Abrir conexão
        ConnectionFactory cf = new ConnectionFactory();
        conecta = cf.conecta();

        if (conecta == null) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados.");
            return;
        }


        PdfWriter.getInstance(document, new FileOutputStream(arquivoSalvar));
        document.open();

        Font fontePadrao = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

        // Título
        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph titulo = new Paragraph("Relatório de Agendamentos", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        document.add(titulo);

        // Data de geração (somente data)
        String dataGeracao = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        Paragraph data = new Paragraph("Data do relatório: " + dataGeracao, new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC));
        data.setAlignment(Element.ALIGN_RIGHT);
        data.setSpacingAfter(10f);
        document.add(data);

        // Linha separadora
        LineSeparator separator = new LineSeparator();
        separator.setOffset(-5);
        document.add(new Chunk(separator));

        // Tabela com 8 colunas
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        table.setWidths(new float[]{2f, 3f, 3f, 2f, 2f, 3f, 2f, 2f});

        // Cabeçalhos com fundo azul
        Font cabecalhoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        BaseColor cabecalhoBackground = new BaseColor(60, 120, 180);
        String[] colunas = {"Código", "Cliente", "Funcionário", "Data", "Hora", "Serviço", "Preço", "Status"};
        for (String coluna : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(coluna, cabecalhoFont));
            cell.setBackgroundColor(cabecalhoBackground);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5f);
            table.addCell(cell);
        }

        // SQL com filtros
        String sql = "SELECT a.age_cod, c.cli_nome, f.fun_nome, a.age_servico, a.age_preco, a.age_data, a.age_hora, a.age_status " +
                     "FROM tb_agendamento a " +
                     "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
                     "JOIN tb_funcionario f ON a.funcionario_cod = f.fun_cod WHERE 1=1";

        List<Object> params = new ArrayList<>();

        if (funcionarioCod > 0) {
            sql += " AND a.funcionario_cod = ?";
            params.add(funcionarioCod);
        }

        if (clienteCod != -1) {
            sql += " AND a.cliente_cod = ?";
            params.add(clienteCod);
        }

        if (status != null && !status.equalsIgnoreCase("TODOS")) {
            sql += " AND UPPER(a.age_status) = ?";
            params.add(status.toUpperCase());
        }

        if (dataInicio != null) {
            sql += " AND a.age_data >= ?";
            params.add(new java.sql.Date(dataInicio.getTime()));
        }

        if (dataFim != null) {
            sql += " AND a.age_data <= ?";
            params.add(new java.sql.Date(dataFim.getTime()));
        }

        pstmt = conecta.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            pstmt.setObject(i + 1, params.get(i));
        }

        rs = pstmt.executeQuery();

        int totalAgendamentos = 0;
        boolean linhaClara = true;
        BaseColor corClara = BaseColor.WHITE;
        BaseColor corEscura = new BaseColor(240, 240, 240);

        while (rs.next()) {
            BaseColor bgColor = linhaClara ? corClara : corEscura;
            linhaClara = !linhaClara;

            addCellToTable(table, rs.getString("age_cod"), bgColor);
            addCellToTable(table, rs.getString("cli_nome"), bgColor);
            addCellToTable(table, rs.getString("fun_nome"), bgColor);
            String dataFormatada = new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("age_data"));
            addCellToTable(table, dataFormatada, bgColor);
            addCellToTable(table, rs.getString("age_hora"), bgColor);
            addCellToTable(table, rs.getString("age_servico"), bgColor);
            addCellToTable(table, String.format("R$ %.2f", rs.getDouble("age_preco")), bgColor);
            addCellToTable(table, rs.getString("age_status"), bgColor);

            totalAgendamentos++;
        }

        document.add(table);

        Paragraph rodape = new Paragraph(
            "Total de agendamentos listados: " + totalAgendamentos,
            new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)
        );
        rodape.setAlignment(Element.ALIGN_RIGHT);
        rodape.setSpacingBefore(10f);
        document.add(rodape);

        document.close();

        File file = new File(arquivoSalvar);
        if (file.exists() && Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erro ao gerar o relatório: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conecta != null) conecta.close();
            if (document != null && document.isOpen()) document.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao fechar recursos: " + e.getMessage());
        }
    }
}

// Método auxiliar
private void addCellToTable(PdfPTable table, String content, BaseColor backgroundColor) {
    Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    PdfPCell cell = new PdfPCell(new Phrase(content != null ? content : "", font));
    cell.setBackgroundColor(backgroundColor);
    cell.setPadding(4f);
    table.addCell(cell);
}

public void gerarRelatorioFuncionario(String status, Date dataInicio, Date dataFim, int funcionarioCod, String funcionarioNome, int clienteCod, String nomeArquivo) {
    try {
       
        if (conecta == null) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados.");
            return;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // Configuração do PDF
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
        document.open();

        // Fonte padrão
        Font fontePadrao = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

        // Título
        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph titulo = new Paragraph("Relatório de Agendamentos\nFuncionário: " + funcionarioNome, tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        document.add(titulo);

        // Data de geração
        String dataGeracao = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        Paragraph data = new Paragraph("Relatório gerado em: " + dataGeracao, new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC));
        data.setAlignment(Element.ALIGN_RIGHT);
        data.setSpacingAfter(10f);
        document.add(data);

        // Linha separadora
        LineSeparator separator = new LineSeparator();
        separator.setOffset(-5);
        document.add(new Chunk(separator));

        // Tabela
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        table.setWidths(new float[]{2f, 3f, 3f, 2f, 2f, 3f, 2f, 2f});

        // Cabeçalhos
        Font cabecalhoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        BaseColor cabecalhoBackground = new BaseColor(60, 120, 180); // Azul

        String[] colunas = {"Código", "Cliente", "Funcionário", "Data", "Hora", "Serviço", "Preço", "Status"};
        for (String coluna : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(coluna, cabecalhoFont));
            cell.setBackgroundColor(cabecalhoBackground);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5f);
            table.addCell(cell);
        }

        // Consulta SQL
        String sql = "SELECT a.age_cod, c.cli_nome, f.fun_nome, a.age_servico, a.age_preco, a.age_data, a.age_hora, a.age_status " +
                     "FROM tb_agendamento a " +
                     "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
                     "JOIN tb_funcionario f ON a.funcionario_cod = f.fun_cod " +
                     "WHERE a.funcionario_cod = ?";

        if (dataInicio != null) sql += " AND a.age_data >= ?";
        if (dataFim != null) sql += " AND a.age_data <= ?";

        pstmt = conecta.prepareStatement(sql);
        pstmt.setInt(1, funcionarioCod);

        int paramIndex = 2;
        if (dataInicio != null) pstmt.setDate(paramIndex++, new java.sql.Date(dataInicio.getTime()));
        if (dataFim != null) pstmt.setDate(paramIndex++, new java.sql.Date(dataFim.getTime()));

        rs = pstmt.executeQuery();

        int totalAgendamentos = 0;
        double totalFaturado = 0;
        boolean linhaClara = true;
        BaseColor corClara = BaseColor.WHITE;
        BaseColor corEscura = new BaseColor(240, 240, 240);

        while (rs.next()) {
            BaseColor bgColor = linhaClara ? corClara : corEscura;
            linhaClara = !linhaClara;

            addCellToTable(table, rs.getString("age_cod"), bgColor);
            addCellToTable(table, rs.getString("cli_nome"), bgColor);
            addCellToTable(table, rs.getString("fun_nome"), bgColor);
            String dataFormatada = new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("age_data"));
            addCellToTable(table, dataFormatada, bgColor);
            addCellToTable(table, rs.getString("age_hora"), bgColor);
            addCellToTable(table, rs.getString("age_servico"), bgColor);
            addCellToTable(table, String.format("R$ %.2f", rs.getDouble("age_preco")), bgColor);
            addCellToTable(table, rs.getString("age_status"), bgColor);

            totalFaturado += rs.getDouble("age_preco");
            totalAgendamentos++;
        }

        document.add(table);

        // Rodapé
        Paragraph rodape = new Paragraph(
            String.format("Total de agendamentos: %d   |   Total faturado: R$ %.2f", totalAgendamentos, totalFaturado),
            new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)
        );
        rodape.setAlignment(Element.ALIGN_RIGHT);
        rodape.setSpacingBefore(10f);
        document.add(rodape);

        document.close();

        // Abre o arquivo
        File file = new File(nomeArquivo);
        if (file.exists() && Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }


    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erro ao gerar o relatório: " + e.getMessage());
    }
}





// Método que retorna os agendamentos filtrados por funcionário
 // Método não estático
public List<Agendamento> buscarAgendamentosPorFuncionario(int funcionarioCod, Date dataInicio, Date dataFim) {
    List<Agendamento> agendamentos = new ArrayList<>();

    // Consulta SQL com as colunas cliente_cod e funcionario_cod incluídas
    String sql = "SELECT a.age_cod, a.cliente_cod, a.funcionario_cod, c.cli_nome, f.fun_nome, " +
                 "a.age_servico, a.age_preco, a.age_data, a.age_hora, a.age_status " +
                 "FROM tb_agendamento a " +
                 "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
                 "JOIN tb_funcionario f ON a.funcionario_cod = f.fun_cod " +
                 "WHERE a.funcionario_cod = ? ";

    // Adiciona filtros de data se as datas forem fornecidas
    if (dataInicio != null) {
        sql += "AND a.age_data >= ? ";
    }
    if (dataFim != null) {
        sql += "AND a.age_data <= ? ";
    }

    try (Connection conecta = new ConnectionFactory().conecta();
         PreparedStatement pstmt = conecta.prepareStatement(sql)) {

        pstmt.setInt(1, funcionarioCod);

        // Define os parâmetros de data, se fornecidos
        int index = 2;
        if (dataInicio != null) {
            pstmt.setDate(index++, new java.sql.Date(dataInicio.getTime()));
        }
        if (dataFim != null) {
            pstmt.setDate(index, new java.sql.Date(dataFim.getTime()));
        }

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Agendamento agendamento = new Agendamento();
                agendamento.setAge_cod(rs.getInt("age_cod"));
                agendamento.setCliente_cod(rs.getInt("cliente_cod"));
                agendamento.setFuncionario_cod(rs.getInt("funcionario_cod"));
                agendamento.setAge_servico(rs.getString("age_servico"));
                java.sql.Date sqlDate = rs.getDate("age_data");
                if (sqlDate != null) {
                    agendamento.setAge_data(new java.util.Date(sqlDate.getTime()));
                }
                agendamento.setAge_hora(rs.getString("age_hora"));
                agendamento.setAge_preco(rs.getFloat("age_preco"));
                agendamento.setAge_status(rs.getString("age_status"));

                agendamentos.add(agendamento);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return agendamentos;
}



public List<Agendamento> buscarAgendamentosPorCliente(int clienteCod) {
    List<Agendamento> agendamentos = new ArrayList<>();
    String sql = "SELECT a.age_cod, a.cliente_cod, a.funcionario_cod, c.cli_nome, f.fun_nome, a.age_servico, a.age_preco, a.age_data, a.age_hora, a.age_status " +
                 "FROM tb_agendamento a " +
                 "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
                 "JOIN tb_funcionario f ON a.funcionario_cod = f.fun_cod " +
                 "WHERE c.cli_cod = ?"; // Filtra pelos agendamentos do cliente

    try (PreparedStatement pstmt = conecta.prepareStatement(sql)) {
        pstmt.setInt(1, clienteCod); // Passa o código do cliente como parâmetro
        ResultSet rs = pstmt.executeQuery();

        // Preenche a lista de agendamentos com os dados retornados
        while (rs.next()) {
            Agendamento agendamento = new Agendamento();
            agendamento.setAge_cod(rs.getInt("age_cod"));
            agendamento.setCliente_cod(rs.getInt("cliente_cod")); // Ajustado para garantir que o cliente_cod existe
            agendamento.setFuncionario_cod(rs.getInt("funcionario_cod")); // Ajustado para garantir que o funcionario_cod existe
            agendamento.setAge_servico(rs.getString("age_servico"));
            agendamento.setAge_data(rs.getDate("age_data"));
            agendamento.setAge_hora(rs.getString("age_hora"));
            agendamento.setAge_preco(rs.getFloat("age_preco"));
            agendamento.setAge_status(rs.getString("age_status"));
            agendamentos.add(agendamento);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar agendamentos por cliente: " + e.getMessage());
    }

    return agendamentos; // Retorna a lista de agendamentos
}

public List<Agendamento> buscarAgendamentosPorStatus(String status, Date dataInicio, Date dataFim) {
    List<Agendamento> lista = new ArrayList<>();
    Connection conecta = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        conecta = new ConnectionFactory().conecta();

        if (conecta == null) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco.");
            return lista;
        }

        StringBuilder sql = new StringBuilder(
            "SELECT a.age_cod, c.cli_nome, f.fun_nome, a.age_servico, a.age_preco, a.age_data, a.age_hora, a.age_status " +
            "FROM tb_agendamento a " +
            "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
            "JOIN tb_funcionario f ON a.funcionario_cod = f.fun_cod "
        );

        List<Object> parametros = new ArrayList<>();
        boolean temWhere = false;

        if (!status.trim().equalsIgnoreCase("TODOS")) {
            sql.append("WHERE TRIM(UPPER(a.age_status)) = ? ");
            parametros.add(status.trim().toUpperCase());
            temWhere = true;
        }

        if (dataInicio != null) {
            sql.append(temWhere ? "AND " : "WHERE ");
            sql.append("a.age_data >= ? ");
            parametros.add(new java.sql.Date(dataInicio.getTime()));
            temWhere = true;
        }

        if (dataFim != null) {
            sql.append(temWhere ? "AND " : "WHERE ");
            sql.append("a.age_data <= ? ");
            parametros.add(new java.sql.Date(dataFim.getTime()));
        }

        pstmt = conecta.prepareStatement(sql.toString());

        for (int i = 0; i < parametros.size(); i++) {
            Object param = parametros.get(i);
            if (param instanceof String) {
                pstmt.setString(i + 1, (String) param);
            } else if (param instanceof java.sql.Date) {
                pstmt.setDate(i + 1, (java.sql.Date) param);
            }
        }

        rs = pstmt.executeQuery();

        while (rs.next()) {
            Agendamento age = new Agendamento();
            age.setAge_cod(rs.getInt("age_cod"));
            age.setAge_data(rs.getDate("age_data"));
            age.setAge_hora(rs.getString("age_hora"));
            age.setAge_servico(rs.getString("age_servico"));
            age.setAge_preco(rs.getFloat("age_preco"));
            age.setAge_status(rs.getString("age_status"));
            // Pode adicionar cliente e funcionário se desejar
            lista.add(age);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar agendamentos: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conecta != null) conecta.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao fechar conexão: " + e.getMessage());
        }
    }

    return lista;
}


public boolean gerarRelatorioStatus(String status, Date dataInicio, Date dataFim, String arquivoSalvar) {
   Connection conecta = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Document document = new Document(PageSize.A4, 50, 50, 50, 50);
    boolean gerado = false;

    try {
        // 🔗 Abrir conexão com o banco
        ConnectionFactory cf = new ConnectionFactory();
        conecta = cf.conecta();

        // Verificar se conseguiu conectar
        if (conecta == null) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados.");
            return false;
        }
        
        PdfWriter.getInstance(document, new FileOutputStream(arquivoSalvar));
        document.open();

        Font fontePadrao = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        // Título
        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph titulo = new Paragraph("Relatório de Agendamentos - Status: " + status, tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        document.add(titulo);

        // Data de geração
        String dataGeracao = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        Paragraph data = new Paragraph("Data do relatório: " + dataGeracao, new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC));
        data.setAlignment(Element.ALIGN_RIGHT);
        data.setSpacingAfter(10f);
        document.add(data);

        // Linha separadora
        LineSeparator separator = new LineSeparator();
        separator.setOffset(-5);
        document.add(new Chunk(separator));

        // Tabela
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{2f, 3f, 3f, 2f, 2f, 3f, 2f, 2f});

        // Cabeçalhos
        Font cabecalhoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        BaseColor corCabecalho = new BaseColor(60, 120, 180);
        String[] colunas = {"Código", "Cliente", "Funcionário", "Data", "Hora", "Serviço", "Preço", "Status"};

        for (String coluna : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(coluna, cabecalhoFont));
            cell.setBackgroundColor(corCabecalho);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5f);
            table.addCell(cell);
        }

        // SQL
        StringBuilder sql = new StringBuilder(
            "SELECT a.age_cod, c.cli_nome, f.fun_nome, a.age_servico, a.age_preco, a.age_data, a.age_hora, a.age_status " +
            "FROM tb_agendamento a " +
            "JOIN tb_cliente c ON a.cliente_cod = c.cli_cod " +
            "JOIN tb_funcionario f ON a.funcionario_cod = f.fun_cod "
        );

        List<Object> parametros = new ArrayList<>();
        boolean temWhere = false;

        if (!status.trim().equalsIgnoreCase("TODOS")) {
            sql.append("WHERE TRIM(UPPER(a.age_status)) = ? ");
            parametros.add(status.trim().toUpperCase());
            temWhere = true;
        }

        if (dataInicio != null) {
            sql.append(temWhere ? "AND " : "WHERE ");
            sql.append("a.age_data >= ? ");
            parametros.add(new java.sql.Date(dataInicio.getTime()));
            temWhere = true;
        }

        if (dataFim != null) {
            sql.append(temWhere ? "AND " : "WHERE ");
            sql.append("a.age_data <= ? ");
            parametros.add(new java.sql.Date(dataFim.getTime()));
        }

        pstmt = conecta.prepareStatement(sql.toString());

        for (int i = 0; i < parametros.size(); i++) {
            Object param = parametros.get(i);
            if (param instanceof String) {
                pstmt.setString(i + 1, (String) param);
            } else if (param instanceof java.sql.Date) {
                pstmt.setDate(i + 1, (java.sql.Date) param);
            }
        }

        rs = pstmt.executeQuery();
        int totalAgendamentos = 0;

        boolean linhaClara = true;
        BaseColor corClara = BaseColor.WHITE;
        BaseColor corEscura = new BaseColor(240, 240, 240);

        while (rs.next()) {
            BaseColor bg = linhaClara ? corClara : corEscura;
            linhaClara = !linhaClara;

            addCellToTable(table, rs.getString("age_cod"), bg);
            addCellToTable(table, rs.getString("cli_nome"), bg);
            addCellToTable(table, rs.getString("fun_nome"), bg);
            String dataFormatada = new SimpleDateFormat("dd/MM/yyyy").format(rs.getDate("age_data"));
            addCellToTable(table, dataFormatada, bg);
            addCellToTable(table, rs.getString("age_hora"), bg);
            addCellToTable(table, rs.getString("age_servico"), bg);
            addCellToTable(table, String.format("R$ %.2f", rs.getDouble("age_preco")), bg);
            addCellToTable(table, rs.getString("age_status"), bg);

            totalAgendamentos++;
        }

        if (totalAgendamentos == 0) {
            document.close();
            return false;
        }

        document.add(table);

        Paragraph rodape = new Paragraph(
            "Total de agendamentos com status \"" + status + "\": " + totalAgendamentos,
            new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)
        );
        rodape.setAlignment(Element.ALIGN_RIGHT);
        rodape.setSpacingBefore(10f);
        document.add(rodape);

        gerado = true;
        document.close();

        File file = new File(arquivoSalvar);
        if (file.exists() && Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erro ao gerar o relatório por status: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conecta != null) conecta.close();
            if (document != null && document.isOpen()) document.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao fechar recursos: " + e.getMessage());
        }
    }

    return gerado;
}

public void gerarRelatorioPorServico(String caminho) {
    Document documento = new Document(PageSize.A4, 50, 50, 50, 50);

    try {
        PdfWriter.getInstance(documento, new FileOutputStream(caminho));
        documento.open();

        // Título
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("Relatório de Serviços Realizados", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);
        documento.add(new Paragraph(" ")); // Espaço

        // Fonte e cor para cabeçalho da tabela
        Font cabecalhoFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
        BaseColor corCabecalho = new BaseColor(33, 150, 243);

        // Fonte para o conteúdo das células
        Font celulaFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);

        // Tabela com 3 colunas
        PdfPTable tabela = new PdfPTable(3);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{5f, 2f, 3f}); // Ajuste proporcional das colunas

        // Cabeçalho da tabela com estilo
        String[] colunas = {"Serviço", "Quantidade", "Total Arrecadado (R$)"};
        for (String coluna : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(coluna, cabecalhoFont));
            cell.setBackgroundColor(corCabecalho);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6f);
            tabela.addCell(cell);
        }

       

        String sql = "SELECT age_servico, COUNT(*) AS quantidade, SUM(age_preco) AS total " +
                     "FROM tb_agendamento " +
                     "WHERE age_status = 'Concluído' " +
                     "GROUP BY age_servico";

        PreparedStatement stmt = conecta.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        // Preenchendo linhas com estilo e alinhamento
        while (rs.next()) {
            PdfPCell servicoCell = new PdfPCell(new Phrase(rs.getString("age_servico"), celulaFont));
            servicoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            servicoCell.setPadding(5f);
            tabela.addCell(servicoCell);

            PdfPCell quantidadeCell = new PdfPCell(new Phrase(String.valueOf(rs.getInt("quantidade")), celulaFont));
            quantidadeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            quantidadeCell.setPadding(5f);
            tabela.addCell(quantidadeCell);

            PdfPCell totalCell = new PdfPCell(new Phrase(String.format("R$ %.2f", rs.getDouble("total")), celulaFont));
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalCell.setPadding(5f);
            tabela.addCell(totalCell);
        }

        documento.add(tabela);
        documento.close();
        conecta.close();

        JOptionPane.showMessageDialog(null, "Relatório de Serviços gerado com sucesso!");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void gerarRelatorioPorServicoPeriodo(Date dataInicio, Date dataFim, String caminho) {
    Document documento = new Document(PageSize.A4, 50, 50, 50, 50);

    try {
        PdfWriter.getInstance(documento, new FileOutputStream(caminho));
        documento.open();

        // Título
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("Relatório de Serviços Realizados\nPeríodo: " +
            new SimpleDateFormat("dd/MM/yyyy").format(dataInicio) + " até " +
            new SimpleDateFormat("dd/MM/yyyy").format(dataFim),
            tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);
        documento.add(new Paragraph(" ")); // Espaço

        // Cabeçalho da tabela - mesmo estilo
        Font cabecalhoFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
        BaseColor corCabecalho = new BaseColor(33, 150, 243);

        // Fonte para células
        Font celulaFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);

        PdfPTable tabela = new PdfPTable(3);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{5f, 2f, 3f});

        String[] colunas = {"Serviço", "Quantidade", "Total Arrecadado (R$)"};
        for (String coluna : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(coluna, cabecalhoFont));
            cell.setBackgroundColor(corCabecalho);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6f);
            tabela.addCell(cell);
        }

       

        // Query com filtro de período
        String sql = "SELECT age_servico, COUNT(*) AS quantidade, SUM(age_preco) AS total " +
                     "FROM tb_agendamento " +
                     "WHERE age_status = 'Concluído' " +
                     "AND age_data >= ? AND age_data <= ? " +
                     "GROUP BY age_servico";

       PreparedStatement stmt = conecta.prepareStatement(sql);
       stmt.setDate(1, new java.sql.Date(dataInicio.getTime()));
       stmt.setDate(2, new java.sql.Date(dataFim.getTime()));

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            PdfPCell servicoCell = new PdfPCell(new Phrase(rs.getString("age_servico"), celulaFont));
            servicoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            servicoCell.setPadding(5f);
            tabela.addCell(servicoCell);

            PdfPCell quantidadeCell = new PdfPCell(new Phrase(String.valueOf(rs.getInt("quantidade")), celulaFont));
            quantidadeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            quantidadeCell.setPadding(5f);
            tabela.addCell(quantidadeCell);

            PdfPCell totalCell = new PdfPCell(new Phrase(String.format("R$ %.2f", rs.getDouble("total")), celulaFont));
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalCell.setPadding(5f);
            tabela.addCell(totalCell);
        }

        documento.add(tabela);
        documento.close();
        conecta.close();

        JOptionPane.showMessageDialog(null, "Relatório de Serviços (período) gerado com sucesso!");

    } catch (Exception e) {
        e.printStackTrace();
    }
}


}

 
    

