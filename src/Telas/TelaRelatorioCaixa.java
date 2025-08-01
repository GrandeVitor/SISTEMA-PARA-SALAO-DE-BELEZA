/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Telas;

import DAO.CaixaDAO;
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class TelaRelatorioCaixa extends javax.swing.JInternalFrame {

    /**
     * Creates new form RelatórioCaixa
     */
    public TelaRelatorioCaixa() {
        initComponents();
        inicializarComponentesRelatorioCaixa();
        dataInicialChooser.setDateFormatString("dd/MM/yyyy");
        dataFinalChooser.setDateFormatString("dd/MM/yyyy");
   }
    
    public void gerarPDF(LocalDate dataInicial, LocalDate dataFinal, List<String> tiposTransacao, List<String> formasPagamento) {
    Document doc = new Document();

    String dataHojeFormatada = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    String nomePadrao = String.format("RelatorioFinanceiro-%s.pdf", dataHojeFormatada);

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setSelectedFile(new File(nomePadrao));
    int userSelection = fileChooser.showSaveDialog(null);

    if (userSelection != JFileChooser.APPROVE_OPTION) {
        JOptionPane.showMessageDialog(null, "Geração de relatório cancelada.");
        return;
    }

    File arquivoSelecionado = fileChooser.getSelectedFile();

    try {
        PdfWriter.getInstance(doc, new FileOutputStream(arquivoSelecionado));
        doc.open();

        // Título
        Paragraph titulo = new Paragraph("RELATÓRIO FINANCEIRO",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        doc.add(titulo);

        // Período
        Paragraph periodo = new Paragraph(
            "Período: " + dataInicial.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
            " até " + dataFinal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n",
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK)
        );
        periodo.setAlignment(Element.ALIGN_LEFT);
        doc.add(periodo);

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

        // Dados
        CaixaDAO dao = new CaixaDAO();
        java.sql.Date sqlInicio = java.sql.Date.valueOf(dataInicial);
        java.sql.Date sqlFim = java.sql.Date.valueOf(dataFinal);
        ArrayList<Caixa> lista = dao.buscarPorPeriodo(sqlInicio, sqlFim, tiposTransacao);

        // Aplica filtro de forma de pagamento se necessário
        if (!formasPagamento.isEmpty()) {
            lista.removeIf(c -> !formasPagamento.contains(c.getCai_forma_pag()));
        }

        float totalEntradas = 0, totalSaidas = 0;
        
        DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Formato para a data

        for (Caixa c : lista) {
           // Converte java.sql.Date para LocalDate
    java.sql.Date sqlDate = (java.sql.Date) c.getCai_data();
    LocalDate localDate = sqlDate.toLocalDate();

    // Formata a data
    String dataFormatada = localDate.format(dataFormatter);

    // Adiciona a célula com a data formatada à tabela
    tabela.addCell(new PdfPCell(new Phrase(dataFormatada)));

            PdfPCell cellDescricao = new PdfPCell(new Phrase(c.getCai_descricao()));
            cellDescricao.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellDescricao.setPadding(5);
            cellDescricao.setNoWrap(false);
            tabela.addCell(cellDescricao);

            if (c.getCai_tipo().equalsIgnoreCase("ENTRADA")) {
                tabela.addCell(new PdfPCell(new Phrase(String.format("R$ %.2f", c.getCai_valor()))));
                tabela.addCell(new PdfPCell(new Phrase("0,00")));
                totalEntradas += c.getCai_valor();
            } else if (c.getCai_tipo().equalsIgnoreCase("SAIDA")) {
                tabela.addCell(new PdfPCell(new Phrase("0,00")));
                tabela.addCell(new PdfPCell(new Phrase(String.format("R$ %.2f", c.getCai_valor()))));
                totalSaidas += c.getCai_valor();
            } else {
                tabela.addCell(new PdfPCell(new Phrase("")));
                tabela.addCell(new PdfPCell(new Phrase("")));
            }

            tabela.addCell(new PdfPCell(new Phrase(c.getCai_forma_pag())));
        }

        doc.add(tabela);

        float saldoAtual = totalEntradas - totalSaidas;

        Paragraph total = new Paragraph();
        total.add("Total de Entradas: R$ " + String.format("%.2f", totalEntradas) + "\n");
        total.add("Total de Saídas: R$ " + String.format("%.2f", totalSaidas) + "\n");
        total.add("Saldo Atual: R$ " + String.format("%.2f", saldoAtual) + "\n");
        doc.add(total);

        Paragraph rodape = new Paragraph("\n\nRelatório gerado em: " + dataHojeFormatada,
            FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY));
        rodape.setAlignment(Element.ALIGN_RIGHT);
        doc.add(rodape);

        doc.close();

       

        JOptionPane.showMessageDialog(null, "Relatório gerado com sucesso");
        if (arquivoSelecionado.exists()) {
            Desktop.getDesktop().open(arquivoSelecionado);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erro ao gerar relatório: " + e.getMessage());
    }
}

    

   private void gerarRelatorio() {
    java.util.Date dataInicio = dataInicialChooser.getDate();
    java.util.Date dataFim = dataFinalChooser.getDate();

    if (dataInicio == null || dataFim == null) {
        JOptionPane.showMessageDialog(this, "Selecione ambas as datas.");
        return;
    }

    if (dataFim.before(dataInicio)) {
        JOptionPane.showMessageDialog(this, "Data final não pode ser antes da data inicial.");
        return;
    }

    // Verifica se pelo menos uma das checkboxes de tipo foi marcada
    List<String> tiposTransacao = new ArrayList<>();
    if (entradaCheckBox.isSelected()) tiposTransacao.add("ENTRADA");
    if (saidaCheckBox.isSelected()) tiposTransacao.add("SAIDA");

    if (tiposTransacao.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Selecione pelo menos 'Entrada' ou 'Saída'.");
        return;
    }

    // Lista de formas de pagamento (só se a checkbox estiver marcada)
    List<String> formasPagamento = new ArrayList<>();
    if (formaPagamentoCheckBox.isSelected()) {
        Object[] opcoes = {"PIX", "DINHEIRO", "CARTÃO"};
        int escolha = JOptionPane.showOptionDialog(
            this,
            "Escolha a forma de pagamento para o relatório:",
            "Forma de Pagamento",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcoes,
            opcoes[0]
        );

        if (escolha == JOptionPane.CLOSED_OPTION) {
            JOptionPane.showMessageDialog(this, "Relatório cancelado.");
            return;
        }

        formasPagamento.add(opcoes[escolha].toString());
    }

    // Convertendo as datas para LocalDate
    LocalDate dataInicialLD = dataInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate dataFinalLD = dataFim.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    try {
        gerarPDF(dataInicialLD, dataFinalLD, tiposTransacao, formasPagamento);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage());
        e.printStackTrace();
    }
}


public void salvarSaldoAnterior(float saldo, LocalDate dataFinal) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("saldo_anterior.txt"))) {
        // Salva o saldo no arquivo com a data do relatório
        writer.write("saldo=" + saldo);
        writer.newLine(); // Nova linha para a próxima vez
        writer.write("data=" + dataFinal.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    } catch (IOException e) {
        System.out.println("Erro ao salvar o saldo anterior: " + e.getMessage());
    }
}

public float lerSaldoAnterior() {
    float saldo = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader("saldo_anterior.txt"))) {
        String linha;
        // Lê cada linha do arquivo
        while ((linha = reader.readLine()) != null) {
            // Verifica se a linha começa com "saldo=" e extrai o valor
            if (linha.startsWith("saldo=")) {
                saldo = Float.parseFloat(linha.substring(6)); // Pega o valor após "saldo="
            }
        }
    } catch (IOException e) {
        // Caso o arquivo não exista ou haja erro ao ler, o saldo é 0
        System.out.println("Arquivo de saldo anterior não encontrado. Considerando saldo 0.");
    }
    return saldo;
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dataInicialChooser = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        dataFinalChooser = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        btn_Relatorio = new javax.swing.JButton();
        entradaCheckBox = new javax.swing.JCheckBox();
        saidaCheckBox = new javax.swing.JCheckBox();
        formaPagamentoCheckBox = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle("RELATÓRIO CAIXA");

        jLabel1.setText("Data Inicial");

        jLabel2.setText("Data Final");

        btn_Relatorio.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_Relatorio.setText("Gerar Relatório");
        btn_Relatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RelatorioActionPerformed(evt);
            }
        });

        entradaCheckBox.setText("ENTRADA");

        saidaCheckBox.setText("SAIDA");

        formaPagamentoCheckBox.setText("FORMA DE PAGAMENTO");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setText("Relatório de Caixa");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(entradaCheckBox)
                                .addGap(57, 57, 57)
                                .addComponent(saidaCheckBox)
                                .addGap(41, 41, 41)
                                .addComponent(formaPagamentoCheckBox))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(29, 29, 29)
                                        .addComponent(jLabel1))
                                    .addComponent(dataInicialChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(93, 93, 93)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(32, 32, 32)
                                        .addComponent(jLabel2))
                                    .addComponent(dataFinalChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(234, 234, 234)
                        .addComponent(btn_Relatorio)))
                .addGap(0, 105, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(193, 193, 193))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dataInicialChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dataFinalChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(entradaCheckBox)
                    .addComponent(saidaCheckBox)
                    .addComponent(formaPagamentoCheckBox))
                .addGap(32, 32, 32)
                .addComponent(btn_Relatorio)
                .addGap(27, 27, 27))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_RelatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RelatorioActionPerformed
        gerarRelatorio();
    }//GEN-LAST:event_btn_RelatorioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Relatorio;
    private com.toedter.calendar.JDateChooser dataFinalChooser;
    private com.toedter.calendar.JDateChooser dataInicialChooser;
    private javax.swing.JCheckBox entradaCheckBox;
    private javax.swing.JCheckBox formaPagamentoCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JCheckBox saidaCheckBox;
    // End of variables declaration//GEN-END:variables

// <editor-fold defaultstate="collapsed" desc="Generated Code">                          
private void inicializarComponentesRelatorioCaixa() {

    dataInicialChooser = new com.toedter.calendar.JDateChooser();
    jLabel1 = new javax.swing.JLabel();
    dataFinalChooser = new com.toedter.calendar.JDateChooser();
    jLabel2 = new javax.swing.JLabel();
    btn_Relatorio = new javax.swing.JButton();
    entradaCheckBox = new javax.swing.JCheckBox();
    saidaCheckBox = new javax.swing.JCheckBox();
    formaPagamentoCheckBox = new javax.swing.JCheckBox();
    jLabel3 = new javax.swing.JLabel();

    setClosable(true);
    setIconifiable(true);
    setMaximizable(true);
    setTitle("RELATÓRIO DE CAIXA");

    jLabel3.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel3.setText("RELATÓRIO DE CAIXA");

    jLabel1.setText("Data Inicial:");
    jLabel2.setText("Data Final:");

    btn_Relatorio.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
    btn_Relatorio.setText("Gerar Relatório");
    btn_Relatorio.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btn_RelatorioActionPerformed(evt);
        }
    });

    entradaCheckBox.setText("Entrada");
    saidaCheckBox.setText("Saída");
    formaPagamentoCheckBox.setText("Forma de Pagamento");

    javax.swing.JPanel painelDatas = new javax.swing.JPanel(new java.awt.GridLayout(2, 2, 10, 5));
    painelDatas.add(jLabel1);
    painelDatas.add(jLabel2);
    painelDatas.add(dataInicialChooser);
    painelDatas.add(dataFinalChooser);

    javax.swing.JPanel painelFiltros = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 10));
    painelFiltros.add(entradaCheckBox);
    painelFiltros.add(saidaCheckBox);
    painelFiltros.add(formaPagamentoCheckBox);

    javax.swing.JPanel painelBotao = new javax.swing.JPanel();
    painelBotao.add(btn_Relatorio);

    javax.swing.JPanel painelPrincipal = new javax.swing.JPanel(new java.awt.BorderLayout(10, 20));
    painelPrincipal.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 30, 20, 30));

    painelPrincipal.add(jLabel3, java.awt.BorderLayout.NORTH);
    painelPrincipal.add(painelDatas, java.awt.BorderLayout.CENTER);
    painelPrincipal.add(painelFiltros, java.awt.BorderLayout.SOUTH);

    getContentPane().setLayout(new java.awt.BorderLayout());
    getContentPane().add(painelPrincipal, java.awt.BorderLayout.CENTER);
    getContentPane().add(painelBotao, java.awt.BorderLayout.SOUTH);

    pack();
}
// </editor-fold>                        


}
