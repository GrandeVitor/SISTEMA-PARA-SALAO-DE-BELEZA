/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Telas;

import DAO.CaixaDAO;
import Objs.Caixa;
import Util.Converter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author User
 */
public class TelaCaixa extends javax.swing.JInternalFrame {
String salvar = "";

    /**
     * Creates new form Caixa
     */
    public TelaCaixa() {
        initComponents();
        personalizarTabela();
        desabilitarCampos();
        txtcai_data.setDateFormatString("dd/MM/yyyy");
        formatarCampoValorCaixa();
    }
    
public boolean validarCamposCaixa() {
    Color defaultBackground = Color.WHITE;
    Color errorBackground = new Color(255, 210, 220); // tom suave de vermelho

    JTextField editor = (JTextField) txtcai_data.getDateEditor().getUiComponent();
    editor.setBackground(defaultBackground);
    txtcai_descricao.setBackground(defaultBackground);
    txtcai_valor2.setBackground(defaultBackground);

    if (txtcai_data.getDate() == null) {
        JOptionPane.showMessageDialog(null, "Informe a data.");
        editor.setBackground(errorBackground);
        txtcai_data.requestFocus();
        return false;
        
    }

    if (txtcai_descricao.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Informe a descrição.");
        txtcai_descricao.setBackground(errorBackground);
        txtcai_descricao.requestFocus();
        return false;
    }

    if (txtcai_valor2.getValue() == null) {
        JOptionPane.showMessageDialog(null, "Informe o valor.");
        txtcai_valor2.setBackground(errorBackground);
        txtcai_valor2.requestFocus();
        return false;
    }

    double valor = ((Number) txtcai_valor2.getValue()).doubleValue();
    if (valor < 0) {
        JOptionPane.showMessageDialog(null, "O valor não pode ser negativo.");
        txtcai_valor2.setBackground(errorBackground);
        txtcai_valor2.requestFocus();
        return false;
    }

    return true;
}


   public void formatarCampoValorCaixa() {
    NumberFormat formatoDecimal = NumberFormat.getNumberInstance();
    formatoDecimal.setMinimumFractionDigits(2);
    formatoDecimal.setMaximumFractionDigits(2);
    formatoDecimal.setGroupingUsed(false); // Sem separador de milhar

    NumberFormatter formatter = new NumberFormatter(formatoDecimal);
    formatter.setValueClass(Double.class);
    formatter.setMinimum(0.0); // Não permite valores negativos
    formatter.setAllowsInvalid(false);
    formatter.setCommitsOnValidEdit(true);

    txtcai_valor2.setFormatterFactory(new DefaultFormatterFactory(formatter));
}
    
    private void carregarTabelaCaixa() {
        ArrayList<Caixa> lst = new ArrayList();
        
        DefaultTableModel tb = (DefaultTableModel) tbcaixa.getModel();
        
        tb.setNumRows(0);
        
       CaixaDAO caiDAO = new CaixaDAO();
            lst =caiDAO.buscaCaixa();
        
        for (Caixa c: lst) {
            tb.addRow(new Object[]{
                c.getCai_cod(),
                c.getCai_descricao(),
                c.getCai_valor(),
                c.getCai_data(),
                c.getCai_forma_pag(),
                c.getCai_tipo()
            });
        }
    }
    
    private void PesquisarTabelaCaixa() {
    String pesquisar = txtcaixa_busca.getText(); // texto digitado
    String campoSelecionado = comboBoxCampo.getSelectedItem().toString(); // campo escolhido
    String campoBanco = "";

    // Mapeia o nome visível no ComboBox para o nome real no banco
    switch (campoSelecionado) {
        case "Tipo":
            campoBanco = "cai_tipo";
            break;
        case "Descrição":
            campoBanco = "cai_descricao";
            break;
        case "Forma de Pagamento":
            campoBanco = "cai_forma_pag";
            break;
        case "Data":
            campoBanco = "cai_data";
            break;
        default:
            JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido!");
            return;
    }

    ArrayList<Caixa> lista = new ArrayList<>();
    DefaultTableModel tb = (DefaultTableModel) tbcaixa.getModel(); // nome da sua JTable
    tb.setNumRows(0);

    CaixaDAO caiDAO = new CaixaDAO();
    lista = caiDAO.pesquisarCaixa(campoBanco, pesquisar); // passa o campo e o texto de busca

    for (Caixa c : lista) {
        tb.addRow(new Object[]{
            c.getCai_cod(),
            c.getCai_descricao(),
            c.getCai_valor(),
            c.getCai_data(),
            c.getCai_forma_pag(),
            c.getCai_tipo()        });
    }
}

    
    public void limparCampos() {
        txtcai_cod.setText("");
        txtcai_descricao.setText("");
        txtcai_valor2.setValue(null);
        txtcai_data.setDate(null);
    }
    
    public void selecionarCampo() {

    // Índice da linha atualmente selecionada
    int linha = tbcaixa.getSelectedRow();

    // ✅ Verificação de seleção
    if (linha == -1) {                       // Nenhuma linha foi clicada
        JOptionPane.showMessageDialog(
            null,
            "Selecione uma linha da tabela para editar.",
            "Aviso",
            JOptionPane.WARNING_MESSAGE
        );
        return;                              // Sai do método
    }

    txtcai_cod.setText(tbcaixa.getValueAt(linha, 0).toString());
    txtcai_descricao.setText(tbcaixa.getValueAt(linha, 1).toString());
    txtcai_valor2.setText(tbcaixa.getValueAt(linha, 2).toString());

    /* --- Conversão e preenchimento do JDateChooser --- */
    Object dataObject = tbcaixa.getValueAt(linha, 3);

    if (dataObject instanceof java.util.Date) {
        txtcai_data.setDate((java.util.Date) dataObject);    // data já veio como Date
    } else if (dataObject instanceof String) {
        try {
            // Ajuste o padrão se seu banco armazenar em outro formato
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date dataConvertida = sdf.parse((String) dataObject);
            txtcai_data.setDate(dataConvertida);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao converter data: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
            txtcai_data.setDate(null);   // Limpa o campo para evitar confusão
        }
    } else {
        txtcai_data.setDate(null);       // Caso venha nulo ou tipo inesperado
    }

    /* --- Combos --- */
    combo_pag.setSelectedItem(tbcaixa.getValueAt(linha, 4));
    combo_tipo.setSelectedItem(tbcaixa.getValueAt(linha, 5));

    /* --- Habilitação/estado dos botões e campos --- */
    habilitarCampos();
    btn_alterar.setEnabled(true);
    btn_excluir.setEnabled(true);

    btn_novo.setEnabled(false);
    btn_salvar.setEnabled(false);
}

    
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        combo_pag = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtcai_cod = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtcai_descricao = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbcaixa = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        combo_tipo = new javax.swing.JComboBox<>();
        btn_novo = new javax.swing.JButton();
        btn_salvar = new javax.swing.JButton();
        btn_excluir = new javax.swing.JButton();
        btn_alterar = new javax.swing.JButton();
        txtcai_data = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txtcaixa_busca = new javax.swing.JTextField();
        comboBoxCampo = new javax.swing.JComboBox<>();
        btn_FecharCaixa = new javax.swing.JButton();
        txtcai_valor2 = new javax.swing.JFormattedTextField();
        btn_recarregar = new javax.swing.JButton();

        jLabel10.setText("jLabel10");

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Caixa");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jLabel2.setText("Valor");

        jLabel3.setText("Data");

        jLabel1.setText("Forma de Pagamento");

        combo_pag.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DINHEIRO", "PIX", "CARTÃO" }));

        jLabel4.setText("Codigo");

        jLabel5.setText("Descricao");

        tbcaixa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Descrição", "Valor", "Data", "Forma de Pagamento", "Tipo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbcaixa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbcaixaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbcaixa);

        jLabel6.setText("Tipo ");

        combo_tipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ENTRADA", "SAIDA" }));

        btn_novo.setText("Novo Pagamento");
        btn_novo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_novoActionPerformed(evt);
            }
        });

        btn_salvar.setText("Salvar");
        btn_salvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salvarActionPerformed(evt);
            }
        });

        btn_excluir.setText("Excluir");
        btn_excluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_excluirActionPerformed(evt);
            }
        });

        btn_alterar.setText("Alterar");
        btn_alterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_alterarActionPerformed(evt);
            }
        });

        jLabel7.setText("Busca por");

        txtcaixa_busca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtcaixa_buscaKeyReleased(evt);
            }
        });

        comboBoxCampo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Descrição", "Data", "Forma de Pagamento", "Tipo" }));

        btn_FecharCaixa.setText("Fechar Caixa");
        btn_FecharCaixa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_FecharCaixaActionPerformed(evt);
            }
        });

        btn_recarregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/recarregar.png"))); // NOI18N
        btn_recarregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recarregarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtcai_cod, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtcai_data, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtcaixa_busca, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btn_novo)
                            .addGap(18, 18, 18)
                            .addComponent(btn_salvar)
                            .addGap(18, 18, 18)
                            .addComponent(btn_alterar)
                            .addGap(18, 18, 18)
                            .addComponent(btn_excluir)
                            .addGap(18, 18, 18)
                            .addComponent(btn_FecharCaixa))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(combo_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(combo_pag, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(txtcai_descricao, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(29, 29, 29)
                                    .addComponent(jLabel2)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtcai_valor2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(131, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btn_recarregar))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtcai_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(txtcai_data, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtcai_descricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtcai_valor2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combo_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel1)
                    .addComponent(combo_pag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtcaixa_busca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btn_recarregar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_novo)
                            .addComponent(btn_salvar)
                            .addComponent(btn_alterar)
                            .addComponent(btn_excluir)
                            .addComponent(btn_FecharCaixa))
                        .addGap(41, 41, 41)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_novoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_novoActionPerformed
        salvar = "novo";
        limparCampos();
      habilitarCampos();
      btn_alterar.setEnabled(false);
      btn_excluir.setEnabled(false);
      txtcai_descricao.requestFocus();
    }//GEN-LAST:event_btn_novoActionPerformed

    private void btn_salvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvarActionPerformed
       
    java.util.Date dataUtil = txtcai_data.getDate(); 

// Verifica se foi selecionada uma data
        if (dataUtil == null) {  
        JOptionPane.showMessageDialog(null, "Selecione uma data válida!");
        return;
}
// Converte para java.sql.Date (compatível com banco de dados)
        java.sql.Date dataSQL = new java.sql.Date(dataUtil.getTime());
        
        if(salvar.equals("novo")) {
            
              if (!validarCamposCaixa()) {
        // Se não passou na validação, para aqui, não salva
        return;
              }
              
        Caixa cai = new Caixa();
        cai.getCai_cod();
        cai.setCai_descricao(txtcai_descricao.getText());
        cai.setCai_tipo((String) combo_tipo.getSelectedItem());
        cai.setCai_valor(Converter.stringParaFloat(txtcai_valor2.getText()));
        cai.setCai_data(dataSQL);
        cai.setCai_forma_pag((String) combo_pag.getSelectedItem());
        
        CaixaDAO caiDAO = new CaixaDAO();
        caiDAO.incluirCaixa(cai); 
       desabilitarCampos();
            limparCampos();
            btn_alterar.setEnabled(false);
            btn_excluir.setEnabled(false);
            btn_novo.setEnabled(true);
        carregarTabelaCaixa();
      
        }
       
        else {
            JOptionPane.showMessageDialog(null, "Selecione Novo Primeiro");
        }
    }//GEN-LAST:event_btn_salvarActionPerformed

    private void btn_excluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_excluirActionPerformed
        CaixaDAO caiDAO = new CaixaDAO();
        caiDAO.excluirCaixa(txtcai_cod.getText());
        desabilitarCampos();
        limparCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
        carregarTabelaCaixa();
        
    }//GEN-LAST:event_btn_excluirActionPerformed

    private void btn_alterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_alterarActionPerformed
    
      java.util.Date dataUtil = txtcai_data.getDate(); 

// Verifica se foi selecionada uma data
        if (dataUtil == null) {  
        JOptionPane.showMessageDialog(null, "Selecione uma data válida!");
        return;
}
// Converte para java.sql.Date (compatível com banco de dados)
        java.sql.Date dataSQL = new java.sql.Date(dataUtil.getTime());
        
        
        Caixa cai = new Caixa();
        cai.setCai_cod(Converter.stringParaInt(txtcai_cod.getText()));
        cai.setCai_descricao(txtcai_descricao.getText());
        cai.setCai_tipo((String) combo_tipo.getSelectedItem());
        cai.setCai_valor(Converter.stringParaFloat(txtcai_valor2.getText()));
        cai.setCai_data(dataSQL);
        cai.setCai_forma_pag((String) combo_pag.getSelectedItem());
        
        CaixaDAO caiDAO = new CaixaDAO();
        caiDAO.alterarCaixa(cai);
        desabilitarCampos();
            limparCampos();
            btn_alterar.setEnabled(false);
            btn_excluir.setEnabled(false);
            btn_novo.setEnabled(true);
        
        carregarTabelaCaixa();
      

    }//GEN-LAST:event_btn_alterarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        carregarTabelaCaixa();
    }//GEN-LAST:event_formInternalFrameOpened

    private void tbcaixaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbcaixaMouseClicked
        selecionarCampo();
    }//GEN-LAST:event_tbcaixaMouseClicked

    private void btn_FecharCaixaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_FecharCaixaActionPerformed
        CaixaDAO dao = new CaixaDAO();
    float entrada = dao.buscarTotalEntradasDiaAtual();
    float saida = dao.buscarTotalSaidasDiaAtual();
    float saldo = entrada - saida;

    String data = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    String mensagem = String.format("Resumo do Dia (%s):\n\nEntradas: R$ %.2f\nSaídas: R$ %.2f\nSaldo: R$ %.2f",data, entrada, saida, saldo);

    JOptionPane.showMessageDialog(null, mensagem, "Resumo Diário", JOptionPane.INFORMATION_MESSAGE);

    int opcao = JOptionPane.showConfirmDialog(
        null,
        "Deseja salvar o resumo do dia em arquivo?",
        "Salvar Resumo",
        JOptionPane.YES_NO_OPTION
    );

    if (opcao == JOptionPane.YES_OPTION) {
        try {
            String caminho = "E:\\SGJussaraCabelereira\\SGJussaraCabelereira\\REGISTRO_DIARIO_CAIXA\\resumo_diario.txt";
            java.io.FileWriter fw = new java.io.FileWriter(caminho, true);
            fw.write(mensagem + "\n\n");
            fw.close();
            JOptionPane.showMessageDialog(null, "Resumo salvo com sucesso!");

            // ✅ Pergunta se deseja gerar o relatório PDF
            int gerarPDF = JOptionPane.showConfirmDialog(
                null,
                "Deseja também gerar o relatório diário em PDF?",
                "Gerar Relatório PDF",
                JOptionPane.YES_NO_OPTION
            );

            if (gerarPDF == JOptionPane.YES_OPTION) {
                dao.gerarRelatorioDiarioPDF(); // ← Chamada correta ao método do DAO
            }

            entrada = 0.0f;
            saida = 0.0f;
            saldo = 0.0f;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar resumo: " + ex.getMessage());
        }
    }

    
    }//GEN-LAST:event_btn_FecharCaixaActionPerformed

    private void txtcaixa_buscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcaixa_buscaKeyReleased
        PesquisarTabelaCaixa();
    }//GEN-LAST:event_txtcaixa_buscaKeyReleased

    private void btn_recarregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recarregarActionPerformed
    tbcaixa.clearSelection();         // Remove seleção da tabela
    limparCampos();                     // Limpa os campos
    desabilitarCampos();                // Desabilita os campos
    btn_alterar.setEnabled(false);      // Desativa botão Alterar
    btn_excluir.setEnabled(false);      // Desativa botão Excluir
    btn_novo.setEnabled(true);       
    }//GEN-LAST:event_btn_recarregarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_FecharCaixa;
    private javax.swing.JButton btn_alterar;
    private javax.swing.JButton btn_excluir;
    private javax.swing.JButton btn_novo;
    private javax.swing.JButton btn_recarregar;
    private javax.swing.JButton btn_salvar;
    private javax.swing.JComboBox<String> comboBoxCampo;
    private javax.swing.JComboBox<String> combo_pag;
    private javax.swing.JComboBox<String> combo_tipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbcaixa;
    private javax.swing.JTextField txtcai_cod;
    private com.toedter.calendar.JDateChooser txtcai_data;
    private javax.swing.JTextField txtcai_descricao;
    private javax.swing.JFormattedTextField txtcai_valor2;
    private javax.swing.JTextField txtcaixa_busca;
    // End of variables declaration//GEN-END:variables

private void desabilitarCampos() {
    txtcai_cod.setEnabled(false);
    txtcai_descricao.setEnabled(false);
    txtcai_data.setEnabled(false);
    combo_tipo.setEnabled(false);
    combo_pag.setEnabled(false);
    txtcai_valor2.setEnabled(false);
    btn_salvar.setEnabled(false);
    btn_alterar.setEnabled(false);
    btn_excluir.setEnabled(false);
}

private void habilitarCampos() {
    txtcai_cod.setEnabled(true);
    txtcai_descricao.setEnabled(true);
    txtcai_data.setEnabled(true);
    combo_tipo.setEnabled(true);
    combo_pag.setEnabled(true);
    txtcai_valor2.setEnabled(true);
    btn_salvar.setEnabled(true);

}
    
private void configurarLayoutCaixa() {
    setTitle("Controle de Caixa");
    setSize(900, 600);
    setLayout(null);

    // ====== Labels e Campos ======
    jLabel1.setText("Código:");
    jLabel1.setBounds(30, 30, 80, 25);
    add(jLabel1);

    txtcai_cod.setBounds(100, 30, 100, 25);
    add(txtcai_cod);

    jLabel2.setText("Data:");
    jLabel2.setBounds(230, 30, 80, 25);
    add(jLabel2);

    txtcai_data.setBounds(280, 30, 150, 25);
    add(txtcai_data);

    jLabel3.setText("Descrição:");
    jLabel3.setBounds(30, 70, 80, 25);
    add(jLabel3);

    txtcai_descricao.setBounds(100, 70, 500, 25);
    add(txtcai_descricao);

    jLabel4.setText("Valor:");
    jLabel4.setBounds(620, 70, 50, 25);
    add(jLabel4);

    txtcai_valor2.setBounds(670, 70, 100, 25);
    add(txtcai_valor2);

    jLabel5.setText("Tipo:");
    jLabel5.setBounds(30, 110, 80, 25);
    add(jLabel5);

    combo_tipo.setBounds(100, 110, 200, 25);
    add(combo_tipo);

    jLabel6.setText("Pagamento:");
    jLabel6.setBounds(320, 110, 80, 25);
    add(jLabel6);

    combo_pag.setBounds(410, 110, 200, 25);
    add(combo_pag);

    // ====== Busca ======
    jLabel7.setText("Buscar por:");
    jLabel7.setBounds(30, 150, 80, 25);
    add(jLabel7);

    comboBoxCampo.setBounds(100, 150, 200, 25);
    add(comboBoxCampo);

    txtcaixa_busca.setBounds(310, 150, 250, 25);
    add(txtcaixa_busca);

    // ====== Botões ======
    btn_novo.setBounds(30, 200, 100, 30);
    add(btn_novo);

    btn_salvar.setBounds(140, 200, 100, 30);
    add(btn_salvar);

    btn_alterar.setBounds(250, 200, 100, 30);
    add(btn_alterar);

    btn_excluir.setBounds(360, 200, 100, 30);
    add(btn_excluir);

    btn_recarregar.setBounds(470, 200, 120, 30);
    add(btn_recarregar);

    btn_FecharCaixa.setBounds(600, 200, 140, 30);
    add(btn_FecharCaixa);

    // ====== Tabela ======
    jScrollPane1.setBounds(30, 250, 820, 270);
    add(jScrollPane1);
}

private void personalizarTabela() {
    // Aumenta a altura das linhas
    tbcaixa.setRowHeight(25);

    // Melhora a fonte do cabeçalho
    tbcaixa.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

    // Melhora a fonte dos dados
    tbcaixa.setFont(new Font("Arial", Font.PLAIN, 13));
}


}
