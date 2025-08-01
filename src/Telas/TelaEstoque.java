/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Telas;

import DAO.EstoqueDAO;
import DAO.FornecedorDAO;
import Objs.Estoque;
import Objs.Fornecedor;
import Objs.Sessao;
import Util.Converter;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author User
 */
public class TelaEstoque extends javax.swing.JInternalFrame {
String salvar = "";

    /**
     * Creates new form Estoque
     */
    public TelaEstoque() {
        initComponents();
        desabilitarCampos();
        txtest_validade_item.setDateFormatString("dd/MM/yyyy");
        carregarFornecedores();
        formatarCampoPrecoDecimal();
        
    }
    
public boolean validarCamposEstoque() {
    Color defaultBackground = Color.WHITE;
    Color errorBackground = new Color(255, 210, 220); // tom suave de vermelho

    // Resetar cores
    txtest_nome_item.setBackground(defaultBackground);
    txtest_quant_item.setBackground(defaultBackground);
    txtest_valor_item2.setBackground(defaultBackground);
    txtest_validade_item.getDateEditor().getUiComponent().setBackground(defaultBackground);

    if (txtest_nome_item.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Informe o nome do item.");
        txtest_nome_item.setBackground(errorBackground);
        txtest_nome_item.requestFocus();
        return false;
    }

    if (txtest_quant_item.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Informe a quantidade do item.");
        txtest_quant_item.setBackground(errorBackground);
        txtest_quant_item.requestFocus();
        return false;
    } else {
        try {
            int quantidade = Integer.parseInt(txtest_quant_item.getText().trim());
            if (quantidade < 0) {
                JOptionPane.showMessageDialog(null, "Quantidade não pode ser negativa.");
                txtest_quant_item.setBackground(errorBackground);
                txtest_quant_item.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Informe uma quantidade válida (número inteiro).");
            txtest_quant_item.setBackground(errorBackground);
            txtest_quant_item.requestFocus();
            return false;
        }
    }

    if (txtest_valor_item2.getValue() == null) {
        JOptionPane.showMessageDialog(null, "Informe o valor do item.");
        txtest_valor_item2.setBackground(errorBackground);
        txtest_valor_item2.requestFocus();
        return false;
    }

    double valor = ((Number) txtest_valor_item2.getValue()).doubleValue();
    if (valor < 0) {
        JOptionPane.showMessageDialog(null, "Valor não pode ser negativo.");
        txtest_valor_item2.setBackground(errorBackground);
        txtest_valor_item2.requestFocus();
        return false;
    }

    return true;
}

    public void formatarCampoPrecoDecimal() {
    NumberFormat formatoDecimal = NumberFormat.getNumberInstance();
    formatoDecimal.setMinimumFractionDigits(2);   // Mínimo 2 casas decimais
    formatoDecimal.setMaximumFractionDigits(2);   // Máximo 2 casas decimais
    formatoDecimal.setGroupingUsed(false);        // Sem separador de milhar

    NumberFormatter formatter = new NumberFormatter(formatoDecimal);
    formatter.setValueClass(Double.class);         // Tipo aceito
    formatter.setMinimum(0.0);                     // Valor mínimo permitido
    formatter.setAllowsInvalid(false);             // Impede caracteres inválidos
    formatter.setCommitsOnValidEdit(true);         // Valor válido ao digitar

    txtest_valor_item2.setFormatterFactory(new DefaultFormatterFactory(formatter));
}

    
    
   private void verificarEstoqueBaixo(List<Estoque> lista) {
    StringBuilder alerta = new StringBuilder();

    for (Estoque e : lista) {
        if (e.getEst_quant_item() <= 5) {
            alerta.append("- ").append(e.getEst_nome_item())
                  .append(" (Qtd: ").append(e.getEst_quant_item()).append(")\n");
        }
    }
    if (alerta.length() > 0) {
        JOptionPane.showMessageDialog(null, 
            "ATENÇÃO! Os seguintes itens estão com baixo estoque:\n\n" + alerta.toString(),
            "Alerta de Estoque",
            JOptionPane.WARNING_MESSAGE
        );
    }
}

    private void carregarTabelaEstoque() {
        ArrayList<Estoque> lst = new ArrayList();
        
        DefaultTableModel tb = (DefaultTableModel) tbest.getModel();
        
        tb.setNumRows(0);
        
        EstoqueDAO estDAO = new EstoqueDAO();
            lst = estDAO.buscaEstoque();
        
        for (Estoque e:lst) {
            tb.addRow(new Object[]{
               e.getEst_cod(),
               e.getEst_nome_item(),
               e.getEst_quant_item(),
               e.getEst_valor_item(),
               e.getEst_validade_item(),
               e.getFornecedor_nome()
            });
        } 
        verificarEstoqueBaixo(lst);
        personalizarTabelaEstoque();
    }
    
    private void PesquisarTabelaEstoque() {
    String pesquisar = txtestoque_busca.getText();
    String campoSelecionado = comboBoxCampo.getSelectedItem().toString();
    String campoBanco = "";

    switch (campoSelecionado) {
        case "Produto":
            campoBanco = "est_nome_item";
            break;
        case "Quantidade":
            campoBanco = "est_quant_item";
            break;
        case "Valor":
            campoBanco = "est_valor_item";
            break;
        case "Fornecedor":
            campoBanco = "for_nome";
            break;
        default:
            JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido!");
            return;
    }

    ArrayList<Estoque> lista = new ArrayList<>();
    DefaultTableModel tb = (DefaultTableModel) tbest.getModel();
    tb.setNumRows(0);

    EstoqueDAO estDAO = new EstoqueDAO();
    lista = estDAO.pesquisarEstoque(campoBanco, pesquisar);

    for (Estoque est : lista) {
        tb.addRow(new Object[]{
            est.getEst_cod(),
            est.getEst_nome_item(),
            est.getEst_quant_item(),
            est.getEst_valor_item(),
            est.getEst_validade_item(),
            est.getFornecedor_nome()
        });
    }
}

    
   private void carregarFornecedores() {
    FornecedorDAO fornDAO = new FornecedorDAO();
    List<Fornecedor> fornecedores = fornDAO.listarFornecedores();

    combo_fornecedor_cod.removeAllItems();

    // Cria o placeholder
    Fornecedor fornecedorPlaceholder = new Fornecedor();
    fornecedorPlaceholder.setForn_cod(0); // ou outro atributo identificador, ajuste conforme sua classe
    fornecedorPlaceholder.setForn_nome("Selecione um fornecedor...");

    combo_fornecedor_cod.addItem(fornecedorPlaceholder);

    for (Fornecedor fornecedor : fornecedores) {
        combo_fornecedor_cod.addItem(fornecedor);
    }
}

   public void limparCampos() {
    txtest_cod_item.setText("");
    txtest_nome_item.setText("");
    txtest_quant_item.setText("");
    txtest_valor_item2.setValue(null);
    txtest_validade_item.setDate(null);

    combo_fornecedor_cod.setSelectedIndex(0); // Seleciona o placeholder
}

    
    public void selecionarCampo() {

    /* -------- 1. Verifica se alguma linha foi selecionada -------- */
    int linha = tbest.getSelectedRow();
    if (linha == -1) {
        JOptionPane.showMessageDialog(
            null,
            "Selecione uma linha da tabela para editar.",
            "Aviso",
            JOptionPane.WARNING_MESSAGE
        );
        return;   // Sai do método se nada estiver selecionado
    }

    /* -------- 2. Preenche campos de texto -------- */
    txtest_cod_item.setText(String.valueOf(tbest.getValueAt(linha, 0)));
    txtest_nome_item.setText(String.valueOf(tbest.getValueAt(linha, 1)));
    txtest_quant_item.setText(String.valueOf(tbest.getValueAt(linha, 2)));
    txtest_valor_item2.setText(String.valueOf(tbest.getValueAt(linha, 3)));

    /* -------- 3. JDateChooser (validade) -------- */
    Object dataObject = tbest.getValueAt(linha, 4);
    if (dataObject instanceof java.util.Date) {
        txtest_validade_item.setDate((java.util.Date) dataObject);
    } else if (dataObject instanceof String) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // altere se a máscara do BD for diferente
            java.util.Date dataConvertida = sdf.parse((String) dataObject);
            txtest_validade_item.setDate(dataConvertida);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao converter data: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
            txtest_validade_item.setDate(null);    // limpa em caso de erro
        }
    } else {
        txtest_validade_item.setDate(null);        // valor inesperado
    }

    /* -------- 4. Combo de Fornecedor (busca pelo nome) -------- */
    String nomeFornecedor = String.valueOf(tbest.getValueAt(linha, 5));
    for (int i = 0; i < combo_fornecedor_cod.getItemCount(); i++) {
        Fornecedor forn = (Fornecedor) combo_fornecedor_cod.getItemAt(i);
        if (forn.getForn_nome().equals(nomeFornecedor)) {
            combo_fornecedor_cod.setSelectedIndex(i);
            break;
        }
    }

    /* -------- 5. Habilita/desabilita campos dependendo do usuário -------- */
    if (!"FUNC".equals(Sessao.tipoUsuario)) {
        habilitarCampos();
        btn_alterar.setEnabled(true);
        btn_excluir.setEnabled(true);
        btn_novo.setEnabled(false);
        btn_salvar.setEnabled(false);
    } else {
        // Para funcionários os campos permanecem bloqueados
        desabilitarCampos(); // se você tiver um método equivalente
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
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

        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtest_cod_item = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtest_nome_item = new javax.swing.JTextField();
        txtest_quant_item = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbest = new javax.swing.JTable();
        btn_novo = new javax.swing.JButton();
        btn_salvar = new javax.swing.JButton();
        btn_excluir = new javax.swing.JButton();
        btn_alterar = new javax.swing.JButton();
        combo_fornecedor_cod = new javax.swing.JComboBox();
        txtest_validade_item = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txtestoque_busca = new javax.swing.JTextField();
        comboBoxCampo = new javax.swing.JComboBox<>();
        txtest_valor_item2 = new javax.swing.JFormattedTextField();
        btn_recarregar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("ESTOQUE");
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

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel6.setText("PRODUTOS");

        jLabel1.setText("Codigo");

        jLabel4.setText("R$");

        jLabel5.setText("Validade");

        jLabel2.setText("Produto");

        jLabel3.setText("Quantidade");

        tbest.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Produto", "Quantidade", "Valor", "Validade", "Fornecedor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbest.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbestMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbest);

        btn_novo.setText("Cadastrar Produto");
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

        jLabel7.setText("Busca");

        txtestoque_busca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtestoque_buscaActionPerformed(evt);
            }
        });
        txtestoque_busca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtestoque_buscaKeyReleased(evt);
            }
        });

        comboBoxCampo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Produto", "Quantidade", "Valor", "Fornecedor" }));

        btn_recarregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/recarregar.png"))); // NOI18N
        btn_recarregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recarregarActionPerformed(evt);
            }
        });

        jLabel8.setText("Fornecedor");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel1)
                        .addGap(21, 21, 21)
                        .addComponent(txtest_cod_item, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(combo_fornecedor_cod, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel2))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel5)))
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtest_validade_item, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtest_nome_item))))
                        .addGap(15, 15, 15))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addGap(237, 237, 237)))
                .addGap(19, 19, 19))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtest_quant_item, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtest_valor_item2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(134, 134, 134)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_novo)
                                .addGap(10, 10, 10)
                                .addComponent(btn_salvar)
                                .addGap(14, 14, 14)
                                .addComponent(btn_alterar)
                                .addGap(14, 14, 14)
                                .addComponent(btn_excluir))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(19, 19, 19)
                                .addComponent(txtestoque_busca, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(60, 60, 60)
                                .addComponent(btn_recarregar)))))
                .addGap(94, 94, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel6)
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1))
                    .addComponent(txtest_cod_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(combo_fornecedor_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtest_quant_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel3))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtest_nome_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtest_valor_item2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(txtest_validade_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_novo)
                    .addComponent(btn_salvar)
                    .addComponent(btn_alterar)
                    .addComponent(btn_excluir))
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel7))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(txtestoque_busca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btn_recarregar))
                .addGap(13, 13, 13)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_salvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvarActionPerformed
          
java.util.Date dataUtil = txtest_validade_item.getDate(); 

java.sql.Date dataSQL = null;

if (dataUtil != null) {
    dataSQL = new java.sql.Date(dataUtil.getTime());
}

if(salvar.equals("novo")) {
    
    if (!validarCamposEstoque()) {
        // Se não passou na validação, para aqui, não salva
        return;
    }
    
    Fornecedor fornecedorSelecionado = (Fornecedor) combo_fornecedor_cod.getSelectedItem();
    if (fornecedorSelecionado == null || fornecedorSelecionado.getForn_cod() == 0) {
        JOptionPane.showMessageDialog(null, "Por favor, selecione um fornecedor válido.");
        return; // Para o salvamento aqui
    }
    
    Estoque est = new Estoque();
    est.getEst_cod();
    est.setEst_nome_item(txtest_nome_item.getText());
    est.setEst_quant_item(Converter.stringParaInt(txtest_quant_item.getText()));
    est.setEst_valor_item(Converter.stringParaFloat(txtest_valor_item2.getText()));
    est.setEst_validade_item(dataSQL);
    est.setFornecedor_cod(fornecedorSelecionado.getForn_cod()); // código válido aqui

    EstoqueDAO estDAO = new EstoqueDAO();
    estDAO.incluirProduto(est);  
    desabilitarCampos();
    limparCampos();
    btn_alterar.setEnabled(false);
    btn_excluir.setEnabled(false);
    btn_novo.setEnabled(true);
    carregarTabelaEstoque();
} else {
    JOptionPane.showMessageDialog(null, "Selecione Novo Primeiro");
}

    }//GEN-LAST:event_btn_salvarActionPerformed

    private void btn_novoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_novoActionPerformed
       if (!"FUNC".equals(Sessao.tipoUsuario)) 
        salvar = "novo";
        limparCampos();
        habilitarCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
        txtest_nome_item.requestFocus();
    }//GEN-LAST:event_btn_novoActionPerformed

    private void btn_excluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_excluirActionPerformed
        EstoqueDAO estDAO = new EstoqueDAO();
        estDAO.excluirEstoque(txtest_cod_item.getText());
        desabilitarCampos();
        limparCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
        carregarTabelaEstoque();
    }//GEN-LAST:event_btn_excluirActionPerformed

    private void btn_alterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_alterarActionPerformed
       
       java.util.Date dataUtil = txtest_validade_item.getDate(); 

       java.sql.Date dataSQL = null;

if (dataUtil != null) {
    dataSQL = new java.sql.Date(dataUtil.getTime());
}  
        
        Estoque est = new Estoque();
        est.setEst_cod(Converter.stringParaInt(txtest_cod_item.getText()));
        est.setEst_nome_item(txtest_nome_item.getText());
        est.setEst_quant_item(Converter.stringParaInt(txtest_quant_item.getText()));
        est.setEst_valor_item(Converter.stringParaFloat(txtest_valor_item2.getText()));
        est.setEst_validade_item(dataSQL);
      Fornecedor fornecedorSelecionado = (Fornecedor) combo_fornecedor_cod.getSelectedItem();
    if (fornecedorSelecionado != null) {
        est.setFornecedor_cod(fornecedorSelecionado.getForn_cod()); // <- aqui é int
    } else {
        JOptionPane.showMessageDialog(null, "Fornecedor inválido!");
        return;
    }

        EstoqueDAO estDAO = new EstoqueDAO();
        estDAO.alterarEstoque(est);
       desabilitarCampos();
            limparCampos();
            btn_alterar.setEnabled(false);
            btn_excluir.setEnabled(false);
            btn_novo.setEnabled(true);
        carregarTabelaEstoque();

    }//GEN-LAST:event_btn_alterarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        carregarTabelaEstoque();
    }//GEN-LAST:event_formInternalFrameOpened

    private void tbestMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbestMouseClicked
            selecionarCampo();
    }//GEN-LAST:event_tbestMouseClicked

    private void txtestoque_buscaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtestoque_buscaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtestoque_buscaActionPerformed

    private void txtestoque_buscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtestoque_buscaKeyReleased
        PesquisarTabelaEstoque();
    }//GEN-LAST:event_txtestoque_buscaKeyReleased

    private void btn_recarregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recarregarActionPerformed
    tbest.clearSelection();          // Remove seleção da tabela
    limparCampos();                  // Limpa os campos
    desabilitarCampos();             // Desabilita os campos
    btn_alterar.setEnabled(false);   // Desativa botão Alterar
    btn_excluir.setEnabled(false);   // Desativa botão Excluir

    if (!"FUNC".equals(Sessao.tipoUsuario)) {
        btn_novo.setEnabled(true);   // Ativa botão Novo apenas se for ADM
    } else {
        btn_novo.setEnabled(false);  // Garante que continue desativado para FUNC
    }
    }//GEN-LAST:event_btn_recarregarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_alterar;
    private javax.swing.JButton btn_excluir;
    private javax.swing.JButton btn_novo;
    private javax.swing.JButton btn_recarregar;
    private javax.swing.JButton btn_salvar;
    private javax.swing.JComboBox<String> comboBoxCampo;
    private javax.swing.JComboBox combo_fornecedor_cod;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbest;
    private javax.swing.JTextField txtest_cod_item;
    private javax.swing.JTextField txtest_nome_item;
    private javax.swing.JTextField txtest_quant_item;
    private com.toedter.calendar.JDateChooser txtest_validade_item;
    private javax.swing.JFormattedTextField txtest_valor_item2;
    private javax.swing.JTextField txtestoque_busca;
    // End of variables declaration//GEN-END:variables

 public void definirPermissaoUsuario(String tipoUsuario) {
        if ("FUNC".equals(tipoUsuario)) {
            habilitarBotoesEstoque(false);
        } else {
            habilitarBotoesEstoque(true);
            desabilitarCampos();
        }
    }   
    
public void habilitarBotoesEstoque(boolean ativo) {
    btn_novo.setEnabled(ativo);
    btn_alterar.setEnabled(ativo);
    btn_excluir.setEnabled(ativo);
    btn_salvar.setEnabled(ativo);
}

private void desabilitarCampos() {
    txtest_cod_item.setEnabled(false);
    txtest_nome_item.setEnabled(false);
    txtest_quant_item.setEnabled(false);
    combo_fornecedor_cod.setEnabled(false);
    txtest_valor_item2.setEnabled(false);
    txtest_validade_item.setEnabled(false);
    btn_salvar.setEnabled(false);
    btn_alterar.setEnabled(false);
    btn_excluir.setEnabled(false);
}

private void habilitarCampos() {
    txtest_cod_item.setEnabled(true);
    txtest_nome_item.setEnabled(true);
    txtest_quant_item.setEnabled(true);
    combo_fornecedor_cod.setEnabled(true);
    txtest_valor_item2.setEnabled(true);
    txtest_validade_item.setEnabled(true);
    btn_salvar.setEnabled(true);

}

private void personalizarTabelaEstoque() {
    // Aumenta a altura das linhas
    tbest.setRowHeight(25);

    // Fonte do cabeçalho
    tbest.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

    // Fonte dos dados da tabela
    tbest.setFont(new Font("Arial", Font.PLAIN, 13));
}


}
