/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Telas;

import DAO.ClienteDAO;
import Objs.Cliente;
import Util.Converter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import static java.awt.image.ImageObserver.HEIGHT;
import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Aluno
 */
public class TelaCliente extends javax.swing.JInternalFrame {
String salvar = "";

    /**
     * Creates new form Cliente
     */
    public TelaCliente() {
        initComponents();
        personalizarTabelaClientes();
        desabilitarCampos();
        formatarCampo();
        formatarCampo2();
       
    }
 
   private static final Set<String> DDD_VALIDOS = new HashSet<>(Arrays.asList(
    "11","12","13","14","15","16","17","18","19",
    "21","22","23","24","27","28",
    "31","32","33","34","35","37","38",
    "41","42","43","44","45","46",
    "47","48","49",
    "51","53","54","55",
    "61","62","64","63","65","66","67","68","69",
    "71","73","74","75","77","79",
    "81","82","83","84","85","86","87","88","89",
    "91","92","93","94","95","96","97","98","99"
));

    public boolean validarCPF(String cpf) {
    cpf = cpf.replaceAll("\\D", ""); // Remove caracteres não numéricos
    
    if (cpf.length() != 11) return false;

    // Verifica se todos os dígitos são iguais (ex: 11111111111)
    if (cpf.matches("(\\d)\\1{10}")) return false;

    try {
        int soma = 0, peso = 10;
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * peso--;
        }
        int resto = 11 - (soma % 11);
        int digito1 = (resto == 10 || resto == 11) ? 0 : resto;

        soma = 0;
        peso = 11;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * peso--;
        }
        resto = 11 - (soma % 11);
        int digito2 = (resto == 10 || resto == 11) ? 0 : resto;

        return digito1 == (cpf.charAt(9) - '0') && digito2 == (cpf.charAt(10) - '0');
    } catch (Exception e) {
        return false;
    }
}

    
   public boolean validarCamposCliente() {
    String cpf = txtcli_cpf.getText();
    
    Color defaultBackground = Color.WHITE;
    Color errorBackground = new Color(255, 210, 220); // tom suave de vermelho

    // Resetar cores
    txtcli_cpf.setBackground(defaultBackground);
    txtcli_nome.setBackground(defaultBackground);
    txtcli_tel.setBackground(defaultBackground);

   

if (isCampoVazio(cpf)) {
    JOptionPane.showMessageDialog(null, "CPF do cliente é obrigatório.");
    txtcli_cpf.setBackground(errorBackground);
    txtcli_cpf.requestFocus();
    return false;
}

if (!validarCPF(cpf)) {
    JOptionPane.showMessageDialog(null, "CPF inválido. Digite um CPF válido.");
    txtcli_cpf.setBackground(errorBackground);
    txtcli_cpf.requestFocus();
    return false;
}


    if (txtcli_nome.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Nome do cliente é obrigatório.");
        txtcli_nome.setBackground(errorBackground);
        txtcli_nome.requestFocus();
        return false;
    }

   if (isCampoVazio(txtcli_tel.getText())) {
    JOptionPane.showMessageDialog(null, "Telefone do cliente é obrigatório.");
    txtcli_tel.setBackground(errorBackground);
    txtcli_tel.requestFocus();
    return false;
} else {
    // Remove tudo que não é número
    String telefoneNumeros = txtcli_tel.getText().replaceAll("\\D", "");
    if (telefoneNumeros.length() != 11) {
        JOptionPane.showMessageDialog(null, "Telefone inválido. Deve conter 11 dígitos (DDD + número).");
        txtcli_tel.setBackground(errorBackground);
        txtcli_tel.requestFocus();
        return false;
    }

    // Extrair o DDD
    String ddd = telefoneNumeros.substring(0, 2);
    if (!DDD_VALIDOS.contains(ddd)) {
        JOptionPane.showMessageDialog(null, "DDD inválido.");
        txtcli_tel.setBackground(errorBackground);
        txtcli_tel.requestFocus();
        return false;
    }
    // Campo de e-mail pode ficar vazio, sem validação
    return true;
} }
   
    private boolean isCampoVazio(String texto) {
    // Remove tudo que não for dígito
    String somenteNumeros = texto.replaceAll("\\D", "");
    return somenteNumeros.isEmpty();
}
    
    public void formatarCampo() {{
        try {
      MaskFormatter mask = new MaskFormatter("(##) #####-####");

       mask.install(txtcli_tel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Digite o numero de Telefone", "Mensagem de ERRO", HEIGHT);
        }
 
    } 
    
    }
    
    public void formatarCampo2() {{
        try {
     MaskFormatter mask = new MaskFormatter("###.###.###-##");

     mask.install(txtcli_cpf);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Digite o numero do CPF", "Mensagem de ERRO", HEIGHT);
        }
 
    } 
    
    }
    
    private void carregarTabelaClientes() {
        ArrayList<Cliente> lst = new ArrayList();
        
        DefaultTableModel tb = (DefaultTableModel) tbcliente.getModel();
        
        tb.setNumRows(0);
        
        ClienteDAO cliDAO = new ClienteDAO();
            lst = cliDAO.buscaCliente();
            
            for (Cliente c:lst) {
             tb.addRow(new Object[]{
              c.getCli_cod(),
              c.getCli_nome(),
              c.getCli_telefone(),
              c.getCli_email(),
              c.getCli_cpf()
                     
                     
             });
            }    
    }
    
   private void PesquisarTabelaClientes() {
    String pesquisar = txtcli_busca.getText(); // Texto que o usuário digitou
    String campoSelecionado = comboBoxCampo.getSelectedItem().toString(); // Nome do campo escolhido
    String campoBanco = "";

    // Mapeando o campo selecionado para o nome da coluna no banco
    switch (campoSelecionado) {
        case "Nome":
            campoBanco = "cli_nome";
            break;
        case "Telefone":
            campoBanco = "cli_telefone";
            break;
        case "Email":
            campoBanco = "cli_email";
            break;
        case "CPF":
            campoBanco = "cli_cpf";
            break;
        default:
            JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido!");
            return;
    }

    ArrayList<Cliente> lst = new ArrayList<>();
    DefaultTableModel tb = (DefaultTableModel) tbcliente.getModel();
    tb.setNumRows(0);

    ClienteDAO cliDAO = new ClienteDAO();
    lst = cliDAO.PesquisarCliente(campoBanco, pesquisar); // Agora passa o campo E o valor de pesquisa

    for (Cliente c : lst) {
        tb.addRow(new Object[]{
            c.getCli_cod(),
            c.getCli_nome(),
            c.getCli_telefone(),
            c.getCli_email(),
            c.getCli_cpf()
        });
    }
}

    
    public void limparCampos() {
        txtcli_cod.setText("");
        txtcli_nome.setText("");
        txtcli_tel.setText("");
        txtcli_email.setText("");
        txtcli_cpf.setText("");
        
    }
    
   public void selecionarCampo() {
    int linha = tbcliente.getSelectedRow();

    if (linha != -1) {
        txtcli_cod.setText(tbcliente.getValueAt(linha, 0).toString());
        txtcli_nome.setText(tbcliente.getValueAt(linha, 1).toString());
        txtcli_tel.setText(tbcliente.getValueAt(linha, 2).toString());
        txtcli_email.setText(tbcliente.getValueAt(linha, 3).toString());
        txtcli_cpf.setText(tbcliente.getValueAt(linha, 4).toString());

        habilitarCampos();
        btn_alterar.setEnabled(true);
        btn_excluir.setEnabled(true);

        btn_novo.setEnabled(false);
        btn_salvar.setEnabled(false);
    } else {
        JOptionPane.showMessageDialog(null, "Por favor, selecione uma linha da tabela para editar.");
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

        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtcli_cod = new javax.swing.JTextField();
        txtcli_nome = new javax.swing.JTextField();
        txtcli_email = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbcliente = new javax.swing.JTable();
        btn_novo = new javax.swing.JButton();
        btn_salvar = new javax.swing.JButton();
        btn_alterar = new javax.swing.JButton();
        btn_excluir = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtcli_busca = new javax.swing.JTextField();
        txtcli_tel = new javax.swing.JFormattedTextField();
        txtcli_cpf = new javax.swing.JFormattedTextField();
        comboBoxCampo = new javax.swing.JComboBox<>();
        btn_Recarregar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cadastro de Cliente");
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

        jLabel3.setText("Nome");

        jLabel4.setText("Telefone");

        jLabel5.setText("Email");

        jLabel6.setText("CPF");

        jLabel1.setText("Codigo");

        tbcliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Nome", "Telefone", "Email", "CPF"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbcliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbclienteMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbcliente);

        btn_novo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/Add_User-80_icon-icons.com_57380.png"))); // NOI18N
        btn_novo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_novoActionPerformed(evt);
            }
        });

        btn_salvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/Save_37110.png"))); // NOI18N
        btn_salvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salvarActionPerformed(evt);
            }
        });

        btn_alterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/Edit_User-80_icon-icons.com_57329.png"))); // NOI18N
        btn_alterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_alterarActionPerformed(evt);
            }
        });

        btn_excluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/Remove_User-80_icon-icons.com_57283.png"))); // NOI18N
        btn_excluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_excluirActionPerformed(evt);
            }
        });

        jLabel2.setText("Busca por:");

        txtcli_busca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcli_buscaActionPerformed(evt);
            }
        });
        txtcli_busca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtcli_buscaKeyReleased(evt);
            }
        });

        comboBoxCampo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nome", "Telefone", "Email", "CPF" }));

        btn_Recarregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/recarregar.png"))); // NOI18N
        btn_Recarregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RecarregarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel5))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel6)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtcli_cod)
                    .addComponent(txtcli_nome)
                    .addComponent(txtcli_tel)
                    .addComponent(txtcli_cpf, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtcli_email, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addComponent(btn_novo)
                .addGap(39, 39, 39)
                .addComponent(btn_salvar)
                .addGap(18, 18, 18)
                .addComponent(btn_alterar)
                .addGap(28, 28, 28)
                .addComponent(btn_excluir)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(txtcli_busca, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Recarregar))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel1))
                    .addComponent(txtcli_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel3))
                    .addComponent(txtcli_nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel4))
                    .addComponent(txtcli_tel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel5))
                    .addComponent(txtcli_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel6))
                    .addComponent(txtcli_cpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_novo)
                    .addComponent(btn_salvar)
                    .addComponent(btn_alterar)
                    .addComponent(btn_excluir))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel2))
                    .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtcli_busca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Recarregar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtcli_buscaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcli_buscaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcli_buscaActionPerformed

    private void btn_novoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_novoActionPerformed
       salvar = "novo";
       habilitarCampos();
       btn_alterar.setEnabled(false);
       btn_excluir.setEnabled(false);
       txtcli_nome.requestFocus();
    }//GEN-LAST:event_btn_novoActionPerformed

    private void btn_salvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvarActionPerformed
        if(salvar.equals("novo")) {
            
            if (!validarCamposCliente()) {
        // Se não passou na validação, para aqui, não salva
        return;
              }
            
            Cliente cli = new Cliente();
            cli.getCli_cod();
            cli.setCli_nome(txtcli_nome.getText());
            cli.setCli_telefone(txtcli_tel.getText());
            cli.setCli_email(txtcli_email.getText());
            cli.setCli_cpf(txtcli_cpf.getText());
            
            ClienteDAO cliDAO = new ClienteDAO();
            cliDAO.incluirCliente(cli);
            desabilitarCampos();
            limparCampos();
            btn_alterar.setEnabled(false);
            btn_excluir.setEnabled(false);
            btn_novo.setEnabled(true);
        }
        
        else {
            JOptionPane.showMessageDialog(null, "Selecione Novo Primeiro");
        }
         carregarTabelaClientes();
        
        
    }//GEN-LAST:event_btn_salvarActionPerformed

    private void btn_excluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_excluirActionPerformed
        ClienteDAO cliDAO = new ClienteDAO();
        cliDAO.excluirCliente(txtcli_cod.getText());
        desabilitarCampos();
        limparCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);

        carregarTabelaClientes();
    }//GEN-LAST:event_btn_excluirActionPerformed

    private void btn_alterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_alterarActionPerformed
            Cliente cli = new Cliente();
            cli.setCli_cod(Converter.stringParaInt(txtcli_cod.getText()));
            cli.setCli_nome(txtcli_nome.getText());
            cli.setCli_telefone(txtcli_tel.getText());
            cli.setCli_email(txtcli_email.getText());
            cli.setCli_cpf(txtcli_cpf.getText());
            
            ClienteDAO cliDAO = new ClienteDAO();
            cliDAO.alterarCliente(cli);
            desabilitarCampos();
            limparCampos();
            btn_alterar.setEnabled(false);
            btn_excluir.setEnabled(false);
            btn_novo.setEnabled(true);

            carregarTabelaClientes();
    }//GEN-LAST:event_btn_alterarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
       carregarTabelaClientes();
       
    }//GEN-LAST:event_formInternalFrameOpened

    private void tbclienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbclienteMouseClicked
        selecionarCampo();
    }//GEN-LAST:event_tbclienteMouseClicked

    private void txtcli_buscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcli_buscaKeyReleased
        PesquisarTabelaClientes();
    }//GEN-LAST:event_txtcli_buscaKeyReleased

    private void btn_RecarregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RecarregarActionPerformed
    tbcliente.clearSelection();         // Remove seleção da tabela
    limparCampos();                     // Limpa os campos
    desabilitarCampos();                // Desabilita os campos
    btn_alterar.setEnabled(false);      // Desativa botão Alterar
    btn_excluir.setEnabled(false);      // Desativa botão Excluir
    btn_novo.setEnabled(true);          // Ativa botão Novo
    }//GEN-LAST:event_btn_RecarregarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Recarregar;
    private javax.swing.JButton btn_alterar;
    private javax.swing.JButton btn_excluir;
    private javax.swing.JButton btn_novo;
    private javax.swing.JButton btn_salvar;
    private javax.swing.JComboBox<String> comboBoxCampo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tbcliente;
    private javax.swing.JTextField txtcli_busca;
    private javax.swing.JTextField txtcli_cod;
    private javax.swing.JFormattedTextField txtcli_cpf;
    private javax.swing.JTextField txtcli_email;
    private javax.swing.JTextField txtcli_nome;
    private javax.swing.JFormattedTextField txtcli_tel;
    // End of variables declaration//GEN-END:variables

private void desabilitarCampos() {
    txtcli_cod.setEnabled(false);
    txtcli_cpf.setEnabled(false);
    txtcli_email.setEnabled(false);
    txtcli_nome.setEnabled(false);
    txtcli_tel.setEnabled(false);
    btn_salvar.setEnabled(false);
    btn_alterar.setEnabled(false);
    btn_excluir.setEnabled(false);
}

private void habilitarCampos() {
    txtcli_cod.setEnabled(true);
    txtcli_cpf.setEnabled(true);
    txtcli_email.setEnabled(true);
    txtcli_nome.setEnabled(true);
    txtcli_tel.setEnabled(true);
    btn_salvar.setEnabled(true);

}

private void personalizarTabelaClientes() {
    // Aumenta a altura das linhas
    tbcliente.setRowHeight(25);

    // Melhora a fonte do cabeçalho
    tbcliente.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

    // Melhora a fonte dos dados
    tbcliente.setFont(new Font("Arial", Font.PLAIN, 13));
}


}
