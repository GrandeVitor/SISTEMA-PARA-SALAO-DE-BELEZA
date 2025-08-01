/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Telas;

import DAO.AdministradorDAO;
import Objs.Administrador;
import Util.Converter;
import java.awt.Color;
import java.awt.Font;
import static java.awt.image.ImageObserver.HEIGHT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Aluno
 */
public class TelaAdministrador extends javax.swing.JInternalFrame {
String salvar = "";
private boolean primeiroCadastro;

    /**
     * Creates new form Administrador
     */
 // Construtor padrão sem argumentos
    public TelaAdministrador() {
        this(false); // chama o outro construtor, assumindo que não é o primeiro cadastro
    }

    public TelaAdministrador(boolean primeiroCadastro) {
        initComponents();
        desabilitarCampos();
        this.primeiroCadastro = primeiroCadastro;
        formatarCampo();
        
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

    
public boolean validarCamposAdministrador() {
    Color defaultBackground = Color.WHITE;
    Color errorBackground = new Color(255, 210, 220); // tom suave de vermelho

    // Resetar cores
    txtadm_nome.setBackground(defaultBackground);
    txtadm_tel.setBackground(defaultBackground);
    txtadm_email.setBackground(defaultBackground);
    txtadm_usuario.setBackground(defaultBackground); // ✅ novo campo
    txtsenha1.setBackground(defaultBackground);

    if (txtadm_nome.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Nome do administrador é obrigatório.");
        txtadm_nome.setBackground(errorBackground);
        txtadm_nome.requestFocus();
        return false;
    }

    if (isCampoVazio(txtadm_tel.getText())) {
        JOptionPane.showMessageDialog(null, "Telefone do administrador é obrigatório.");
        txtadm_tel.setBackground(errorBackground);
        txtadm_tel.requestFocus();
        return false;
    } else {
        String telefoneNumeros = txtadm_tel.getText().replaceAll("\\D", "");
        if (telefoneNumeros.length() != 11) {
            JOptionPane.showMessageDialog(null, "Telefone inválido. Deve conter 11 dígitos (DDD + número).");
            txtadm_tel.setBackground(errorBackground);
            txtadm_tel.requestFocus();
            return false;
        }

        String ddd = telefoneNumeros.substring(0, 2);
        if (!DDD_VALIDOS.contains(ddd)) {
            JOptionPane.showMessageDialog(null, "DDD inválido.");
            txtadm_tel.setBackground(errorBackground);
            txtadm_tel.requestFocus();
            return false;
        }
    }

    if (txtadm_email.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "E-mail do administrador é obrigatório.");
        txtadm_email.setBackground(errorBackground);
        txtadm_email.requestFocus();
        return false;
    }

    if (txtadm_usuario.getText().trim().isEmpty()) { // ✅ novo campo
        JOptionPane.showMessageDialog(null, "Usuário do administrador é obrigatório.");
        txtadm_usuario.setBackground(errorBackground);
        txtadm_usuario.requestFocus();
        return false;
    }

    if (String.valueOf(txtsenha1.getPassword()).trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Senha do administrador é obrigatória.");
        txtsenha1.setBackground(errorBackground);
        txtsenha1.requestFocus();
        return false;
    }

    return true;
}

private boolean isCampoVazio(String texto) {
    // Remove tudo que não for número
    String apenasNumeros = texto.replaceAll("\\D", "");
    return apenasNumeros.isEmpty();
}


    
     public void formatarCampo() {{
        try {
      MaskFormatter mask = new MaskFormatter("(##) #####-####");

       mask.install(txtadm_tel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Digite o numero de Telefone", "Mensagem de ERRO", HEIGHT);
        }
 
    } 
    }
     
     private void carregarTabelaAdministrador() {
        ArrayList<Administrador> lst = new ArrayList();
        
        DefaultTableModel tb = (DefaultTableModel) tbadm.getModel();
        
        tb.setNumRows(0);
        
        AdministradorDAO admDAO = new AdministradorDAO();
            lst = admDAO.buscaAdministrador();
        
        for (Administrador ad: lst) {
            tb.addRow(new Object[]{
                ad.getAdm_cod(),
                ad.getAdm_nome(),
                ad.getAdm_telefone(),
                ad.getAdm_email(),
                ad.getAdm_usuario(),
                ad.getAdm_senha()
            });
        }
        personalizarTabelaAdministrador();
    }
     
    private void PesquisarTabelaAdministrador() {
    String pesquisar = txtadm_busca.getText();  // Pega o texto digitado na caixa de pesquisa
    String campoSelecionado = comboBoxCampo.getSelectedItem().toString();  // Pega o campo selecionado na ComboBox
    String campoBanco = "";

    // Mapeando o campo selecionado para o nome da coluna no banco de dados
    switch (campoSelecionado) {
        case "Nome":
            campoBanco = "adm_nome";
            break;
        case "Telefone":
            campoBanco = "adm_telefone";
            break;
        case "Email":
            campoBanco = "adm_email";
            break;
        default:
            JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido!");
            return;
    }

    // Cria uma lista de administradores e o modelo da tabela
    ArrayList<Administrador> lst = new ArrayList<>();
    DefaultTableModel tb = (DefaultTableModel) tbadm.getModel();
    
    // Limpa os dados da tabela antes de adicionar novos resultados
    tb.setNumRows(0);
    
    // Cria o DAO e chama o método de pesquisa
    AdministradorDAO admDAO = new AdministradorDAO();
    lst = admDAO.pesquisarAdministrador(campoBanco, pesquisar);
    
    // Preenche a tabela com os resultados da pesquisa
    for (Administrador ad : lst) {
        tb.addRow(new Object[]{
            ad.getAdm_cod(),
            ad.getAdm_nome(),
            ad.getAdm_telefone(),
            ad.getAdm_email(),
            ad.getAdm_usuario(),
            ad.getAdm_senha()
        });
    }
}

     
     public void limparCampos() {
         txtadm_cod.setText("");
         txtadm_nome.setText("");
         txtadm_tel.setText("");
         txtadm_email.setText("");
         txtadm_usuario.setText("");
         txtsenha1.setText("");
         
     }
     
     public void selecionarCampo() {
    int linha = tbadm.getSelectedRow();

    // Verifica se alguma linha foi selecionada
    if (linha == -1) {
        JOptionPane.showMessageDialog(
            null,
            "Por favor, selecione uma linha da tabela para editar.",
            "Aviso",
            JOptionPane.WARNING_MESSAGE
        );
        return; // Interrompe o método se nada estiver selecionado
    }

    // Preenche os campos com os dados da linha selecionada
    txtadm_cod.setText(String.valueOf(tbadm.getValueAt(linha, 0)));
    txtadm_nome.setText(String.valueOf(tbadm.getValueAt(linha, 1)));
    txtadm_tel.setText(String.valueOf(tbadm.getValueAt(linha, 2)));
    txtadm_email.setText(String.valueOf(tbadm.getValueAt(linha, 3)));
    txtadm_usuario.setText(String.valueOf(tbadm.getValueAt(linha, 4)));
    txtsenha1.setText(String.valueOf(tbadm.getValueAt(linha, 5)));

    // Habilita campos e botões
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

        txtadm_cod = new javax.swing.JTextField();
        txtadm_nome = new javax.swing.JTextField();
        txtadm_email = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbadm = new javax.swing.JTable();
        btn_novo = new javax.swing.JButton();
        btn_excluir = new javax.swing.JButton();
        btn_alterar = new javax.swing.JButton();
        btn_salvar = new javax.swing.JButton();
        txtadm_tel = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtadm_busca = new javax.swing.JTextField();
        comboBoxCampo = new javax.swing.JComboBox<>();
        txtsenha = new javax.swing.JLabel();
        txtsenha1 = new javax.swing.JPasswordField();
        btn_recarregar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtadm_usuario = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cadastro de Administrador");
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

        jLabel1.setText("Codigo");

        jLabel2.setText("Nome");

        jLabel3.setText("Telefone");

        jLabel4.setText("Email");

        tbadm.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Nome", "Telefone", "Email", "Usuario", "Senha"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbadm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbadmMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbadm);

        btn_novo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/adicionar usuario.png"))); // NOI18N
        btn_novo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_novoActionPerformed(evt);
            }
        });

        btn_excluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/deletar usuario.png"))); // NOI18N
        btn_excluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_excluirActionPerformed(evt);
            }
        });

        btn_alterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/editar usuario.png"))); // NOI18N
        btn_alterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_alterarActionPerformed(evt);
            }
        });

        btn_salvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/salvar usuario.png"))); // NOI18N
        btn_salvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salvarActionPerformed(evt);
            }
        });

        jLabel5.setText("Busca");

        txtadm_busca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtadm_buscaKeyReleased(evt);
            }
        });

        comboBoxCampo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nome", "Telefone", "Email" }));

        txtsenha.setText("Senha");

        btn_recarregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/recarregar.png"))); // NOI18N
        btn_recarregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recarregarActionPerformed(evt);
            }
        });

        jLabel6.setText("Usuario");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_recarregar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtadm_cod, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                                        .addComponent(txtadm_tel))
                                    .addComponent(txtadm_nome, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(133, 133, 133)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(49, 49, 49)
                                        .addComponent(jLabel5)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtadm_busca, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btn_novo)
                                        .addGap(38, 38, 38)
                                        .addComponent(btn_salvar)
                                        .addGap(32, 32, 32)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(5, 5, 5)
                                                .addComponent(jLabel4)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtadm_email, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtsenha)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtsenha1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtadm_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(btn_alterar)
                                                .addGap(39, 39, 39)
                                                .addComponent(btn_excluir)))))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtadm_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtadm_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)))
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtadm_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtadm_nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtadm_tel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtsenha1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtsenha))
                .addGap(61, 61, 61)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_novo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_salvar, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_alterar, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_excluir, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtadm_busca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(btn_recarregar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_salvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvarActionPerformed
        if(salvar.equals("novo")) {
            
            if (!validarCamposAdministrador()) {
        // Se não passou na validação, para aqui, não salva
        return;
              }
            
        Administrador adm = new Administrador();
        adm.getAdm_cod();
        adm.setAdm_nome(txtadm_nome.getText());
        adm.setAdm_telefone(txtadm_tel.getText());
        adm.setAdm_email(txtadm_email.getText());
        adm.setAdm_usuario(txtadm_usuario.getText());
        adm.setAdm_senha(txtsenha1.getText());
        
        AdministradorDAO admDAO = new AdministradorDAO();
        admDAO.IncluirAdministrador(adm); 
        desabilitarCampos();
            limparCampos();
            btn_alterar.setEnabled(false);
            btn_excluir.setEnabled(false);
            btn_novo.setEnabled(true);
        }
        
        if (primeiroCadastro) {
            // Redireciona para login se for o primeiro ADM
            TelaLogin login = new TelaLogin((TelaPrincipal2) this.getDesktopPane().getTopLevelAncestor());
            this.getDesktopPane().add(login);
            login.setVisible(true);
        }
       
        else {
            JOptionPane.showMessageDialog(null, "Selecione Novo Primeiro");
        }
        
        carregarTabelaAdministrador();
    }//GEN-LAST:event_btn_salvarActionPerformed

    private void btn_novoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_novoActionPerformed
        salvar = "novo";
        limparCampos();
        habilitarCampos();
      btn_alterar.setEnabled(false);
      btn_excluir.setEnabled(false);
    }//GEN-LAST:event_btn_novoActionPerformed

    private void btn_excluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_excluirActionPerformed
        AdministradorDAO admDAO = new AdministradorDAO();
        admDAO.excluirAdministrador(txtadm_cod.getText());
        carregarTabelaAdministrador();
        desabilitarCampos();
        limparCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
    }//GEN-LAST:event_btn_excluirActionPerformed

    private void btn_alterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_alterarActionPerformed
        Administrador adm = new Administrador();
        adm.setAdm_cod(Converter.stringParaInt(txtadm_cod.getText()));
        adm.setAdm_nome(txtadm_nome.getText());
        adm.setAdm_telefone(txtadm_tel.getText());
        adm.setAdm_email(txtadm_email.getText());
        adm.setAdm_usuario(txtadm_usuario.getText());
        adm.setAdm_senha(txtsenha1.getText());
        
        AdministradorDAO admDAO = new AdministradorDAO();
        admDAO.alterarAdministrador(adm);
        desabilitarCampos();
            limparCampos();
            btn_alterar.setEnabled(false);
            btn_excluir.setEnabled(false);
            btn_novo.setEnabled(true);
        
        carregarTabelaAdministrador();
    }//GEN-LAST:event_btn_alterarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        carregarTabelaAdministrador();
    }//GEN-LAST:event_formInternalFrameOpened

    private void tbadmMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbadmMouseClicked
        selecionarCampo();
    }//GEN-LAST:event_tbadmMouseClicked

    private void txtadm_buscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtadm_buscaKeyReleased
        PesquisarTabelaAdministrador();
    }//GEN-LAST:event_txtadm_buscaKeyReleased

    private void btn_recarregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recarregarActionPerformed
    tbadm.clearSelection();         // Remove seleção da tabela
    limparCampos();                     // Limpa os campos
    desabilitarCampos();                // Desabilita os campos
    btn_alterar.setEnabled(false);      // Desativa botão Alterar
    btn_excluir.setEnabled(false);      // Desativa botão Excluir
    btn_novo.setEnabled(true);       
    }//GEN-LAST:event_btn_recarregarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_alterar;
    private javax.swing.JButton btn_excluir;
    private javax.swing.JButton btn_novo;
    private javax.swing.JButton btn_recarregar;
    private javax.swing.JButton btn_salvar;
    private javax.swing.JComboBox<String> comboBoxCampo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tbadm;
    private javax.swing.JTextField txtadm_busca;
    private javax.swing.JTextField txtadm_cod;
    private javax.swing.JTextField txtadm_email;
    private javax.swing.JTextField txtadm_nome;
    private javax.swing.JFormattedTextField txtadm_tel;
    private javax.swing.JTextField txtadm_usuario;
    private javax.swing.JLabel txtsenha;
    private javax.swing.JPasswordField txtsenha1;
    // End of variables declaration//GEN-END:variables

private void desabilitarCampos() {
    txtadm_cod.setEnabled(false);
    txtadm_nome.setEnabled(false);
    txtadm_email.setEnabled(false);
    txtadm_tel.setEnabled(false);
    txtadm_usuario.setEnabled(false);
    txtsenha1.setEnabled(false);

    btn_salvar.setEnabled(false);
    btn_alterar.setEnabled(false);
    btn_excluir.setEnabled(false);
}

private void habilitarCampos() {
    txtadm_cod.setEnabled(true);
    txtadm_nome.setEnabled(true);
    txtadm_email.setEnabled(true);
    txtadm_tel.setEnabled(true);
    txtadm_usuario.setEnabled(true);
    txtsenha1.setEnabled(true);
    btn_salvar.setEnabled(true);

}

private void estilizarTela() {
    // Cor de fundo do JInternalFrame
    getContentPane().setBackground(new java.awt.Color(230, 240, 255)); // Azul claro

    // Estilo dos JLabels
    java.awt.Font fonteLabels = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14);
    java.awt.Color corTexto = new java.awt.Color(50, 50, 50);

    JLabel[] labels = { jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, txtsenha };
    for (JLabel label : labels) {
        label.setFont(fonteLabels);
        label.setForeground(corTexto);
    }

    // Estilo da tabela
    tbadm.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
    tbadm.setRowHeight(25);
    tbadm.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
    tbadm.getTableHeader().setBackground(new java.awt.Color(100, 149, 237)); // Azul médio
    tbadm.getTableHeader().setForeground(java.awt.Color.WHITE);
    tbadm.setGridColor(new java.awt.Color(200, 200, 200));
    tbadm.setShowGrid(true);
    tbadm.setSelectionBackground(new java.awt.Color(173, 216, 230));

    // Estilo dos botões
    java.awt.Font fonteBotoes = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13);
    java.awt.Color corBotao = new java.awt.Color(70, 130, 180); // Steel Blue
    java.awt.Color corTextoBotao = java.awt.Color.WHITE;

    JButton[] botoes = { btn_novo, btn_alterar, btn_excluir, btn_salvar, btn_recarregar };
    for (JButton botao : botoes) {
        botao.setFont(fonteBotoes);
        botao.setBackground(corBotao);
        botao.setForeground(corTextoBotao);
        botao.setFocusPainted(false);
    }

    // Estilo dos campos de texto
    JTextField[] camposTexto = {
        txtadm_cod, txtadm_nome, txtadm_email, txtadm_usuario, txtsenha1, txtadm_busca
    };
    for (JTextField campo : camposTexto) {
        campo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        campo.setBackground(new java.awt.Color(255, 255, 255));
        campo.setForeground(new java.awt.Color(33, 33, 33));
    }

    JFormattedTextField[] camposFormatados = { txtadm_tel };
    for (JFormattedTextField campo : camposFormatados) {
        campo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
    }

    txtsenha1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
}

private void personalizarTabelaAdministrador() {
    // Aumenta a altura das linhas
    tbadm.setRowHeight(25);

    // Melhora a fonte do cabeçalho
    tbadm.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

    // Melhora a fonte dos dados
    tbadm.setFont(new Font("Arial", Font.PLAIN, 13));
}


}
