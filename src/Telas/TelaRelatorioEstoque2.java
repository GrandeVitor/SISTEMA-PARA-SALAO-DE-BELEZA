/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Telas;

import DAO.EstoqueDAO;
import DAO.FornecedorDAO;
import Objs.Estoque;
import Objs.Fornecedor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author User
 */
public class TelaRelatorioEstoque2 extends javax.swing.JInternalFrame {

    /**
     * Creates new form TelaRelatorioEstoque2
     */
    public TelaRelatorioEstoque2() {
        initComponents();
        configurarLayoutRelatorioEstoque();
        combo_fornecedor_cod.setEnabled(false);
        carregarFornecedores();
        txtDataInicial.setDateFormatString("dd/MM/yyyy");
        txtDataFinal.setDateFormatString("dd/MM/yyyy");
    }

    
    
    private void carregarFornecedores() {
    FornecedorDAO fornDAO = new FornecedorDAO();
    List<Fornecedor> fornecedores = fornDAO.listarFornecedores();

    combo_fornecedor_cod.removeAllItems(); // Evita itens duplicados

    for (Fornecedor fornecedor: fornecedores) {
        combo_fornecedor_cod.addItem(fornecedor);
    }
      }
    
    public File escolherArquivoSalvar(String nomeSugerido) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Salvar Relatório");
    fileChooser.setSelectedFile(new File(nomeSugerido + ".pdf"));

    int resultado = fileChooser.showSaveDialog(null);
    if (resultado == JFileChooser.APPROVE_OPTION) {
        File arquivoSelecionado = fileChooser.getSelectedFile();
        if (!arquivoSelecionado.getName().toLowerCase().endsWith(".pdf")) {
            arquivoSelecionado = new File(arquivoSelecionado.getAbsolutePath() + ".pdf");
        }
        return arquivoSelecionado;
    } else {
        JOptionPane.showMessageDialog(null, "Operação cancelada.");
        return null;
    }
}
 
    private void gerarRelatorioPorFornecedorSelecionado() {
    // Obtendo o fornecedor selecionado no ComboBox
    Fornecedor fornecedorSelecionado = (Fornecedor) combo_fornecedor_cod.getSelectedItem();

    if (fornecedorSelecionado != null) {
        // Chamando o método para gerar o relatório passando o nome do fornecedor
        gerarRelatorioPorFornecedor(fornecedorSelecionado.getForn_nome());
    } else {
        JOptionPane.showMessageDialog(null, "Por favor, selecione um fornecedor.");
    }
}

  
    
  public void gerarRelatorioPDFTabela() {
    Document document = new Document();
    try {
        // FileChooser para selecionar onde salvar
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório de Estoque");
        fileChooser.setSelectedFile(new File("relatorio_estoque.pdf")); // nome sugerido

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null, "Operação cancelada.");
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
        }

        PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
        document.open();

        // Estilos de fonte
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font destaqueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);

        // Título centralizado
        Paragraph titulo = new Paragraph("Relatório de Estoque", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        // Data
        Paragraph data = new Paragraph("Data: " + java.time.LocalDate.now(), cellFont);
        data.setAlignment(Element.ALIGN_RIGHT);
        document.add(data);
        document.add(new Paragraph(" "));

        // Tabela
        PdfPTable tabela = new PdfPTable(6);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{1.2f, 2.5f, 1.2f, 1.5f, 2f, 2f});

        // Cabeçalhos
        String[] colunas = {"Código", "Nome", "Quantidade", "Valor", "Validade", "Fornecedor"};
        BaseColor headerColor = new BaseColor(230, 230, 250);
        for (String coluna : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(coluna, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            cell.setBackgroundColor(headerColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            tabela.addCell(cell);
        }

        // Dados
        EstoqueDAO estDAO = new EstoqueDAO();
        ArrayList<Estoque> listaEstoque = estDAO.buscaEstoque();

        int totalItens = 0;
        double valorTotal = 0.0;

        Estoque maiorQtd = null, menorQtd = null, maiorValor = null, menorValor = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Estoque e : listaEstoque) {
            tabela.addCell(createCell(String.valueOf(e.getEst_cod()), cellFont));
            tabela.addCell(createCell(e.getEst_nome_item(), cellFont));
            tabela.addCell(createCell(String.valueOf(e.getEst_quant_item()), cellFont));
            tabela.addCell(createCell("R$ " + String.format("%.2f", e.getEst_valor_item()), cellFont));
            tabela.addCell(createCell(sdf.format(e.getEst_validade_item()), cellFont));
            tabela.addCell(createCell(e.getFornecedor_nome(), cellFont));

            totalItens += e.getEst_quant_item();
            valorTotal += e.getEst_quant_item() * e.getEst_valor_item();

            if (maiorQtd == null || e.getEst_quant_item() > maiorQtd.getEst_quant_item()) maiorQtd = e;
            if (menorQtd == null || e.getEst_quant_item() < menorQtd.getEst_quant_item()) menorQtd = e;

            if (maiorValor == null || e.getEst_valor_item() > maiorValor.getEst_valor_item()) maiorValor = e;
            if (menorValor == null || e.getEst_valor_item() < menorValor.getEst_valor_item()) menorValor = e;
        }

        document.add(tabela);
        document.add(new Paragraph(" "));

        // Separador
        document.add(new LineSeparator());

        // Totais
        document.add(new Paragraph("Total de itens em estoque: " + totalItens, destaqueFont));
        document.add(new Paragraph("Valor total do estoque: R$ " + String.format("%.2f", valorTotal), destaqueFont));
        document.add(new Paragraph(" "));

        // Destaques
        if (maiorQtd != null && menorQtd != null) {
            document.add(new Paragraph("Item com MAIOR quantidade: " + maiorQtd.getEst_nome_item() +
                    " (Quantidade: " + maiorQtd.getEst_quant_item() + ")", cellFont));
            document.add(new Paragraph("Item com MENOR quantidade: " + menorQtd.getEst_nome_item() +
                    " (Quantidade: " + menorQtd.getEst_quant_item() + ")", cellFont));
        }

        if (maiorValor != null && menorValor != null) {
            document.add(new Paragraph("Item com MAIOR valor unitário: " + maiorValor.getEst_nome_item() +
                    " (Valor: R$ " + String.format("%.2f", maiorValor.getEst_valor_item()) + ")", cellFont));
            document.add(new Paragraph("Item com MENOR valor unitário: " + menorValor.getEst_nome_item() +
                    " (Valor: R$ " + String.format("%.2f", menorValor.getEst_valor_item()) + ")", cellFont));
        }

        // Rodapé
        Paragraph rodape = new Paragraph("Relatório gerado automaticamente pelo sistema.", 
        FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY));
        rodape.setAlignment(Element.ALIGN_CENTER);
        rodape.setSpacingBefore(20);
        document.add(rodape);

        JOptionPane.showMessageDialog(null, "Relatório PDF com tabela gerado com sucesso!");

    } catch (DocumentException | IOException e) {
        JOptionPane.showMessageDialog(null, "Erro ao gerar relatório PDF: " + e.getMessage());
    } finally {
        document.close();
    }
}

// Método auxiliar para criar células
private PdfPCell createCell(String texto, Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(texto, font));
    cell.setPadding(5);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    return cell;
}

public void gerarRelatorioEstoqueBaixo(int limiteQuantidade) {
    Document document = new Document();

    try {
        // FileChooser para salvar o arquivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório de Estoque Baixo");
        fileChooser.setSelectedFile(new File("relatorio_estoque_baixo.pdf"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null, "Operação cancelada.");
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
        }

        PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
        document.open();

        // Fontes
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font destaqueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);

        // Título centralizado
        Paragraph titulo = new Paragraph("Relatório de Estoque Baixo", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        // Data
        Paragraph data = new Paragraph("Data: " + java.time.LocalDate.now(), cellFont);
        data.setAlignment(Element.ALIGN_RIGHT);
        document.add(data);
        document.add(new Paragraph(" "));

        // Tabela
        PdfPTable tabela = new PdfPTable(6);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{1.2f, 2.5f, 1.2f, 1.5f, 2f, 2f});

        // Cabeçalhos com cor
        String[] colunas = {"Código", "Nome", "Quantidade", "Valor", "Validade", "Fornecedor"};
        BaseColor headerColor = new BaseColor(220, 220, 240);
        for (String coluna : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
            cell.setBackgroundColor(headerColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            tabela.addCell(cell);
        }

        // Buscar dados
        EstoqueDAO estDAO = new EstoqueDAO();
        ArrayList<Estoque> listaEstoque = estDAO.buscaEstoque();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Preencher tabela apenas com itens abaixo do limite
        for (Estoque e : listaEstoque) {
            if (e.getEst_quant_item() <= limiteQuantidade) {
                tabela.addCell(createCell(String.valueOf(e.getEst_cod()), cellFont));
                tabela.addCell(createCell(e.getEst_nome_item(), cellFont));
                tabela.addCell(createCell(String.valueOf(e.getEst_quant_item()), cellFont));
                tabela.addCell(createCell("R$ " + String.format("%.2f", e.getEst_valor_item()), cellFont));
                tabela.addCell(createCell(sdf.format(e.getEst_validade_item()), cellFont));
                tabela.addCell(createCell(e.getFornecedor_nome(), cellFont));
            }
        }

        document.add(tabela);
        document.add(new Paragraph(" "));

        // Rodapé
        Paragraph rodape = new Paragraph("Relatório gerado automaticamente pelo sistema.",
                                          FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY));
        rodape.setAlignment(Element.ALIGN_CENTER);
        rodape.setSpacingBefore(20);
        document.add(rodape);

        JOptionPane.showMessageDialog(null, "Relatório PDF gerado com sucesso!");

    } catch (DocumentException | IOException e) {
        JOptionPane.showMessageDialog(null, "Erro ao gerar relatório PDF: " + e.getMessage());
    } finally {
        document.close();
    }
}



   
public void gerarRelatorioPorFornecedor(String nomeFornecedor) {
    Document document = new Document();

    try {
        // Escolher onde salvar
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório de Estoque por Fornecedor");
        fileChooser.setSelectedFile(new File("relatorio_fornecedor_" + nomeFornecedor + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null, "Operação cancelada.");
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
        }

        PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
        document.open();

        // Fontes
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font destaqueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);

        // Título centralizado
        Paragraph titulo = new Paragraph("Relatório de Itens por Fornecedor: " + nomeFornecedor, tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        // Data
        Paragraph data = new Paragraph("Data: " + java.time.LocalDate.now(), cellFont);
        data.setAlignment(Element.ALIGN_RIGHT);
        document.add(data);
        document.add(new Paragraph(" "));

        // Tabela
        PdfPTable tabela = new PdfPTable(6);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{1.2f, 2.5f, 1.2f, 1.5f, 2f, 2f});

        // Cabeçalhos com cor
        String[] colunas = {"Código", "Nome", "Quantidade", "Valor", "Validade", "Fornecedor"};
        BaseColor headerColor = new BaseColor(220, 220, 240);
        for (String coluna : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
            cell.setBackgroundColor(headerColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            tabela.addCell(cell);
        }

        // Dados do fornecedor
        EstoqueDAO estDAO = new EstoqueDAO();
        ArrayList<Estoque> listaEstoque = estDAO.buscaEstoque();

       

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Estoque e : listaEstoque) {
            if (e.getFornecedor_nome().equalsIgnoreCase(nomeFornecedor)) {
                tabela.addCell(createCell(String.valueOf(e.getEst_cod()), cellFont));
                tabela.addCell(createCell(e.getEst_nome_item(), cellFont));
                tabela.addCell(createCell(String.valueOf(e.getEst_quant_item()), cellFont));
                tabela.addCell(createCell("R$ " + String.format("%.2f", e.getEst_valor_item()), cellFont));
                tabela.addCell(createCell(sdf.format(e.getEst_validade_item()), cellFont));
                tabela.addCell(createCell(e.getFornecedor_nome(), cellFont));

               
            }
        }

        document.add(tabela);
        document.add(new Paragraph(" "));

        

        // Rodapé
        Paragraph rodape = new Paragraph("Relatório gerado automaticamente pelo sistema.", 
                                          FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY));
        rodape.setAlignment(Element.ALIGN_CENTER);
        rodape.setSpacingBefore(20);
        document.add(rodape);

        JOptionPane.showMessageDialog(null, "Relatório PDF por fornecedor gerado com sucesso!");

    } catch (DocumentException | IOException e) {
        JOptionPane.showMessageDialog(null, "Erro ao gerar relatório PDF: " + e.getMessage());
    } finally {
        document.close();
    }
}

public void gerarRelatorioPorValidade() {
    java.util.Date dataInicio = (java.util.Date) txtDataInicial.getDate();
    java.util.Date dataFim = (java.util.Date) txtDataFinal.getDate();

    if (dataInicio == null || dataFim == null) {
        JOptionPane.showMessageDialog(null, "Por favor, selecione ambas as datas.");
        return;
    }

    java.sql.Date dataInicioSql = new java.sql.Date(dataInicio.getTime());
    java.sql.Date dataFimSql = new java.sql.Date(dataFim.getTime());

    EstoqueDAO dao = new EstoqueDAO();
    List<Estoque> lista = dao.buscarPorValidade(dataInicioSql, dataFimSql);

    if (lista.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Nenhum item encontrado no intervalo informado.");
        return;
    }

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Salvar Relatório de Estoque por Validade");
    fileChooser.setSelectedFile(new File("relatorio_estoque_por_validade.pdf"));

    int userSelection = fileChooser.showSaveDialog(null);
    if (userSelection != JFileChooser.APPROVE_OPTION) {
        JOptionPane.showMessageDialog(null, "Operação cancelada.");
        return;
    }

    File fileToSave = fileChooser.getSelectedFile();
    if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
        fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
    }

    Document doc = new Document();

    try {
        PdfWriter.getInstance(doc, new FileOutputStream(fileToSave));
        doc.open();

       // Fontes (estilo igual ao gerarRelatorioPorFornecedor)
Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
Font rodapeFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY);

// Título
Paragraph titulo = new Paragraph("Relatório de Itens por Validade", tituloFont);
titulo.setAlignment(Element.ALIGN_CENTER);
titulo.setSpacingAfter(10);
doc.add(titulo);

// Data
Paragraph data = new Paragraph("Data: " + java.time.LocalDate.now(), cellFont);
data.setAlignment(Element.ALIGN_RIGHT);
doc.add(data);
doc.add(new Paragraph(" "));

// Tabela
PdfPTable tabela = new PdfPTable(5);
tabela.setWidthPercentage(100);
tabela.setSpacingBefore(10f);
tabela.setSpacingAfter(10f);
tabela.setWidths(new float[]{2f, 1.5f, 1.5f, 2f, 2f});

// Cabeçalhos com cor
String[] colunas = {"Item", "Quantidade", "Valor", "Validade", "Fornecedor"};
BaseColor headerColor = new BaseColor(220, 220, 240);
for (String coluna : colunas) {
    PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
    cell.setBackgroundColor(headerColor);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setPadding(5);
    tabela.addCell(cell);
}

// Dados
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

for (Estoque e : lista) {
    tabela.addCell(createCell(e.getEst_nome_item(), cellFont));
    tabela.addCell(createCell(String.valueOf(e.getEst_quant_item()), cellFont));
    tabela.addCell(createCell("R$ " + String.format("%.2f", e.getEst_valor_item()), cellFont));
    tabela.addCell(createCell(sdf.format(e.getEst_validade_item()), cellFont));
    tabela.addCell(createCell(e.getFornecedor_nome(), cellFont));
}

doc.add(tabela);

// Rodapé
Paragraph rodape = new Paragraph("Relatório gerado automaticamente pelo sistema.", rodapeFont);
rodape.setAlignment(Element.ALIGN_CENTER);
rodape.setSpacingBefore(20);
doc.add(rodape);

        JOptionPane.showMessageDialog(null, "Relatório de validade gerado com sucesso!");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erro ao gerar relatório: " + e.getMessage());
    } finally {
        doc.close();
    }
}

// Método para criar células personalizadas com alinhamento e bordas
private PdfPCell createCell(String text, Font font, int alignment) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setHorizontalAlignment(alignment);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setPadding(5);
    cell.setBorderWidth(1);
    cell.setBorderColor(BaseColor.GRAY);
    return cell;
}



private void gerarRelatorioPorValor() {
    // Criação dos campos de texto para capturar os valores mínimo e máximo
    JTextField txtMin = new JTextField();
    JTextField txtMax = new JTextField();

    JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
    panel.add(new JLabel("Valor Mínimo (R$):"));
    panel.add(txtMin);
    panel.add(new JLabel("Valor Máximo (R$):"));
    panel.add(txtMax);

    // Exibe o JOptionPane com os campos de entrada
    int result = JOptionPane.showConfirmDialog(null, panel, 
        "Filtrar por Intervalo de Valores", JOptionPane.OK_CANCEL_OPTION);

    // Verifica se o usuário clicou em OK
    if (result == JOptionPane.OK_OPTION) {
        try {
            // Converte os valores digitados para double
            double min = Float.parseFloat(txtMin.getText().replace(",", "."));
            double max = Float.parseFloat(txtMax.getText().replace(",", "."));

            // FileChooser para selecionar onde salvar
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar Relatório de Estoque");
            fileChooser.setSelectedFile(new File("relatorio_estoque.pdf")); // nome sugerido

            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "Operação cancelada.");
                return; // usuário cancelou
            }

            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }
            
            // Cria o DAO para buscar os dados filtrados
            EstoqueDAO dao = new EstoqueDAO();
            List<Estoque> lista = dao.buscarPorValor(min, max);  // Chama o método no DAO com o filtro de preço

            // Gera o relatório em PDF com os dados filtrados
            gerarRelatorioPDF(lista, fileToSave);  // Passa a lista e o caminho para o PDF

        } catch (NumberFormatException e) {
            // Em caso de erro de conversão (valor não numérico)
            JOptionPane.showMessageDialog(null, "Digite valores numéricos válidos.");
        }
    }
}

private void gerarRelatorioPDF(List<Estoque> lista, File file) {
    Document document = new Document();
    try {
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Fontes
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        // Título
        Paragraph titulo = new Paragraph("Relatório de Estoque Filtrado por Valor", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        // Data
        Paragraph data = new Paragraph("Data: " + java.time.LocalDate.now(), cellFont);
        data.setAlignment(Element.ALIGN_RIGHT);
        document.add(data);
        document.add(new Paragraph(" ")); // Espaço

        // Tabela com 6 colunas
        PdfPTable tabela = new PdfPTable(6);
        tabela.setWidthPercentage(100);
        tabela.setSpacingBefore(10f);
        tabela.setSpacingAfter(10f);
        tabela.setWidths(new float[]{1.2f, 2.5f, 1.2f, 1.5f, 2f, 2f});

        // Cabeçalhos
        String[] colunas = {"Código", "Nome", "Quantidade", "Valor", "Validade", "Fornecedor"};
        BaseColor headerColor = new BaseColor(220, 220, 240);
        for (String coluna : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
            cell.setBackgroundColor(headerColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            tabela.addCell(cell);
        }

        // Dados da tabela
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Estoque e : lista) {
            tabela.addCell(createCell(String.valueOf(e.getEst_cod()), cellFont));
            tabela.addCell(createCell(e.getEst_nome_item(), cellFont));
            tabela.addCell(createCell(String.valueOf(e.getEst_quant_item()), cellFont));
            tabela.addCell(createCell("R$ " + String.format("%.2f", e.getEst_valor_item()), cellFont));
            tabela.addCell(createCell(sdf.format(e.getEst_validade_item()), cellFont));
            tabela.addCell(createCell(e.getFornecedor_nome(), cellFont));
        }

        document.add(tabela);
        document.add(new Paragraph(" "));

        // Rodapé
        Paragraph rodape = new Paragraph("Relatório gerado automaticamente pelo sistema.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY));
        rodape.setAlignment(Element.ALIGN_CENTER);
        rodape.setSpacingBefore(20);
        document.add(rodape);

        JOptionPane.showMessageDialog(null, "Relatório PDF gerado com sucesso!");
    } catch (DocumentException | IOException e) {
        JOptionPane.showMessageDialog(null, "Erro ao gerar relatório PDF: " + e.getMessage());
    } finally {
        document.close();
    }
}

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_GerarRelatório = new javax.swing.JButton();
        txtDataInicial = new com.toedter.calendar.JDateChooser();
        txtDataFinal = new com.toedter.calendar.JDateChooser();
        combo_fornecedor_cod = new javax.swing.JComboBox();
        chkFornecedor = new javax.swing.JCheckBox();
        chkValidade = new javax.swing.JCheckBox();
        chkPreco = new javax.swing.JCheckBox();
        chkEstoqueBaixo = new javax.swing.JCheckBox();

        setClosable(true);
        setIconifiable(true);

        btn_GerarRelatório.setText("Gerar Relatório");
        btn_GerarRelatório.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GerarRelatórioActionPerformed(evt);
            }
        });

        chkFornecedor.setText("FORNECEDOR");
        chkFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFornecedorActionPerformed(evt);
            }
        });

        chkValidade.setText("VALIDADE");

        chkPreco.setText("PREÇO");

        chkEstoqueBaixo.setText("QUANTIDADE");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(185, 185, 185)
                        .addComponent(btn_GerarRelatório))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkFornecedor)
                                .addGap(18, 18, 18)
                                .addComponent(chkValidade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkPreco)
                                .addGap(18, 18, 18)
                                .addComponent(chkEstoqueBaixo))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(106, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(combo_fornecedor_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addComponent(combo_fornecedor_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(btn_GerarRelatório)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFornecedor)
                    .addComponent(chkValidade)
                    .addComponent(chkPreco)
                    .addComponent(chkEstoqueBaixo))
                .addGap(99, 99, 99))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_GerarRelatórioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GerarRelatórioActionPerformed
         // Verificamos o estado de cada checkbox
boolean validadeSelecionada = chkValidade.isSelected();
boolean fornecedorSelecionado = chkFornecedor.isSelected();
boolean precoSelecionado = chkPreco.isSelected();
boolean estoqueBaixoSelecionado = chkEstoqueBaixo.isSelected();

// Apenas validade
if (validadeSelecionada && !fornecedorSelecionado && !precoSelecionado && !estoqueBaixoSelecionado) {
    gerarRelatorioPorValidade();
}
// Apenas fornecedor
else if (fornecedorSelecionado && !validadeSelecionada && !precoSelecionado && !estoqueBaixoSelecionado) {
    gerarRelatorioPorFornecedorSelecionado();
}
// Apenas preço
else if (precoSelecionado && !validadeSelecionada && !fornecedorSelecionado && !estoqueBaixoSelecionado) {
    gerarRelatorioPorValor();
}
// Apenas estoque baixo
else if (estoqueBaixoSelecionado && !validadeSelecionada && !fornecedorSelecionado && !precoSelecionado) {
    String input = JOptionPane.showInputDialog(
        null,
        "Digite o limite de quantidade para considerar o estoque como baixo:",
        "Limite de Estoque Baixo",
        JOptionPane.QUESTION_MESSAGE
    );

    if (input != null && !input.trim().isEmpty()) {
        try {
            int limite = Integer.parseInt(input.trim());
            if (limite < 0) {
                JOptionPane.showMessageDialog(null, "Digite um valor maior ou igual a 0.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            gerarRelatorioEstoqueBaixo(limite); // método que já está implementado
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor, insira um número inteiro válido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(null, "Operação cancelada ou valor inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
// Nenhum filtro selecionado, gera o relatório completo
else if (!validadeSelecionada && !fornecedorSelecionado && !precoSelecionado && !estoqueBaixoSelecionado) {
    gerarRelatorioPDFTabela(); // relatório geral
}
// Múltiplos filtros selecionados
else {
    JOptionPane.showMessageDialog(null, "Por favor, selecione apenas um filtro por vez.", "Aviso", JOptionPane.WARNING_MESSAGE);
}

    }//GEN-LAST:event_btn_GerarRelatórioActionPerformed

    private void chkFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFornecedorActionPerformed
          if (chkFornecedor.isSelected()) {
        combo_fornecedor_cod.setEnabled(true);
    } else {
        combo_fornecedor_cod.setEnabled(false);
    }
    }//GEN-LAST:event_chkFornecedorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_GerarRelatório;
    private javax.swing.JCheckBox chkEstoqueBaixo;
    private javax.swing.JCheckBox chkFornecedor;
    private javax.swing.JCheckBox chkPreco;
    private javax.swing.JCheckBox chkValidade;
    private javax.swing.JComboBox combo_fornecedor_cod;
    private com.toedter.calendar.JDateChooser txtDataFinal;
    private com.toedter.calendar.JDateChooser txtDataInicial;
    // End of variables declaration//GEN-END:variables

private void configurarLayoutRelatorioEstoque() {
    setTitle("RELATÓRIO DE ESTOQUE");

    javax.swing.JLabel tituloLabel = new javax.swing.JLabel("RELATÓRIO DE ESTOQUE", javax.swing.SwingConstants.CENTER);
    tituloLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));

    javax.swing.JLabel dataInicialLabel = new javax.swing.JLabel("Data Inicial:");
    javax.swing.JLabel dataFinalLabel = new javax.swing.JLabel("Data Final:");
    javax.swing.JLabel fornecedorLabel = new javax.swing.JLabel("Fornecedor:");

    // Painel de datas
    javax.swing.JPanel painelDatas = new javax.swing.JPanel(new java.awt.GridLayout(2, 2, 10, 5));
    painelDatas.add(dataInicialLabel);
    painelDatas.add(dataFinalLabel);
    painelDatas.add(txtDataInicial);
    painelDatas.add(txtDataFinal);

    // Painel fornecedor
    javax.swing.JPanel painelFornecedor = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
    painelFornecedor.add(fornecedorLabel);
    painelFornecedor.add(combo_fornecedor_cod);

    // Painel filtros
    javax.swing.JPanel painelFiltros = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));
    painelFiltros.add(chkFornecedor);
    painelFiltros.add(chkValidade);
    painelFiltros.add(chkPreco);
    painelFiltros.add(chkEstoqueBaixo);

    // Painel botão
    javax.swing.JPanel painelBotao = new javax.swing.JPanel();
    painelBotao.add(btn_GerarRelatório);

    // Painel principal
    javax.swing.JPanel painelPrincipal = new javax.swing.JPanel(new java.awt.BorderLayout(10, 20));
    painelPrincipal.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 30, 20, 30));

    painelPrincipal.add(tituloLabel, java.awt.BorderLayout.NORTH);

    // Agrupar datas e fornecedor no centro
    javax.swing.JPanel centro = new javax.swing.JPanel(new java.awt.GridLayout(2, 1, 10, 10));
    centro.add(painelDatas);
    centro.add(painelFornecedor);

    painelPrincipal.add(centro, java.awt.BorderLayout.CENTER);
    painelPrincipal.add(painelFiltros, java.awt.BorderLayout.SOUTH);

    // Aplicar ao frame
    getContentPane().setLayout(new java.awt.BorderLayout());
    getContentPane().add(painelPrincipal, java.awt.BorderLayout.CENTER);
    getContentPane().add(painelBotao, java.awt.BorderLayout.SOUTH);

    pack();
}


}
