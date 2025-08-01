/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Telas;

import DAO.FuncionarioDAO;
import Objs.Funcionario;
import Objs.Sessao;
import Util.Converter;
import java.awt.Color;
import java.awt.Font;
import static java.awt.image.ImageObserver.HEIGHT;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Aluno
 */
public class TelaFuncionario extends javax.swing.JInternalFrame {
String salvar = "";
    Funcionario usuarioLogado = null;

    /**
     * Creates new form Funcionario
     */
    public TelaFuncionario() {
        initComponents();
        personalizarTabelaFuncionarios();
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
    
   public boolean validarCamposFuncionario() {
    String cpf = txtfun_cpf.getText();
    
    Color defaultBackground = Color.WHITE;
    Color errorBackground = new Color(255, 210, 220); // tom suave de vermelho

    // Resetar cores
    txtfun_cpf.setBackground(defaultBackground);
    txtfun_nome.setBackground(defaultBackground);
    txtfun_tel.setBackground(defaultBackground);
    txtfun_email.setBackground(defaultBackground);
    txtfun_senha.setBackground(defaultBackground);

    if (isCampoVazio(cpf)) {
    JOptionPane.showMessageDialog(null, "CPF do cliente é obrigatório.");
    txtfun_cpf.setBackground(errorBackground);
    txtfun_cpf.requestFocus();
    return false;
}

if (!validarCPF(cpf)) {
    JOptionPane.showMessageDialog(null, "CPF inválido. Digite um CPF válido.");
    txtfun_cpf.setBackground(errorBackground);
    txtfun_cpf.requestFocus();
    return false;
}

    if (txtfun_nome.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Nome do funcionário é obrigatório.");
        txtfun_nome.setBackground(errorBackground);
        txtfun_nome.requestFocus();
        return false;
    }

    if (isCampoVazio(txtfun_tel.getText())) {
    JOptionPane.showMessageDialog(null, "Telefone do cliente é obrigatório.");
    txtfun_tel.setBackground(errorBackground);
    txtfun_tel.requestFocus();
    return false;
} else {
    // Remove tudo que não é número
    String telefoneNumeros = txtfun_tel.getText().replaceAll("\\D", "");
    if (telefoneNumeros.length() != 11) {
        JOptionPane.showMessageDialog(null, "Telefone inválido. Deve conter 11 dígitos (DDD + número).");
        txtfun_tel.setBackground(errorBackground);
        txtfun_tel.requestFocus();
        return false;
    }

    // Extrair o DDD
    String ddd = telefoneNumeros.substring(0, 2);
    if (!DDD_VALIDOS.contains(ddd)) {
        JOptionPane.showMessageDialog(null, "DDD inválido.");
        txtfun_tel.setBackground(errorBackground);
        txtfun_tel.requestFocus();
        return false;
    }
   
}

    if (txtfun_email.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "E-mail do funcionário é obrigatório.");
        txtfun_email.setBackground(errorBackground);
        txtfun_email.requestFocus();
        return false;
    }

    return true;
}

private boolean isCampoVazio(String texto) {
    // Remove todos os caracteres que não são números
    String somenteNumeros = texto.replaceAll("\\D", "");
    return somenteNumeros.isEmpty();
}


    
     public void formatarCampo() {{
        try {
      MaskFormatter mask = new MaskFormatter("(##) #####-####");

       mask.install(txtfun_tel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Digite o numero de Telefone", "Mensagem de ERRO", HEIGHT);
        }
 
    } 
    
    }
     
    public void formatarCampo2() {{
        try {
     MaskFormatter mask = new MaskFormatter("###.###.###-##");

     mask.install(txtfun_cpf);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Digite o numero do CPF", "Mensagem de ERRO", HEIGHT);
        }
 
    } 
    
    } 
    
    private void carregarTabelaFuncionarios(Funcionario usuarioLogado) {
    ArrayList<Funcionario> lst = new ArrayList();
    DefaultTableModel tb = (DefaultTableModel) tbfun.getModel();
    tb.setNumRows(0);

    FuncionarioDAO funDAO = new FuncionarioDAO();
    lst = funDAO.buscaFuncionario();

    boolean isFuncionario = Sessao.tipoUsuario.equals("FUNC");

    for (Funcionario f : lst) {
        // Máscara da senha
        String senhaExibir;
        if (isFuncionario) {
            senhaExibir = f.getFun_senha() != null ? f.getFun_senha().replaceAll(".", "*") : "";
        } else {
            senhaExibir = f.getFun_senha() != null ? f.getFun_senha() : "";
        }

        // Máscara do nome de usuário
        String usuarioExibir;
        if (isFuncionario) {
            String usuario = f.getFun_usuario();
            if (usuario != null && usuario.length() >= 3) {
                usuarioExibir = usuario.substring(0, 2) + "***";
            } else {
                usuarioExibir = "***";
            }
        } else {
            usuarioExibir = f.getFun_usuario() != null ? f.getFun_usuario() : "";
        }

        // Adiciona os dados na tabela
        tb.addRow(new Object[] {
            f.getFun_cod(),
            f.getFun_nome(),
            f.getFun_telefone(),
            f.getFun_email(),
            f.getFun_cpf(),
            usuarioExibir,
            senhaExibir
        });
    }
}


     
  private void PesquisarTabelaFuncionarios() {
    // Pegando o texto da pesquisa
    String pesquisar = txtfun_busca.getText();


    // Pegando o campo selecionado na ComboBox
    String campoSelecionado = comboBoxCampo.getSelectedItem().toString();
    String campoBanco = "";

    // Mapeando o campo selecionado para o nome da coluna no banco
    switch (campoSelecionado) {
        case "Nome":
            campoBanco = "fun_nome";
            break;
        case "Telefone":
            campoBanco = "fun_telefone";
            break;
        case "Email":
            campoBanco = "fun_email";
            break;
        case "CPF":
            campoBanco = "fun_cpf";
            break;
        default:
            JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido!");
            return;
    }

    // Criando a lista de funcionários
    ArrayList<Funcionario> lst = new ArrayList<>();
    
    // Obtendo o modelo da tabela
    DefaultTableModel tb = (DefaultTableModel) tbfun.getModel();
    
    // Limpando a tabela antes de adicionar novos resultados
    tb.setNumRows(0);
    
    // Criando uma instância do DAO
    FuncionarioDAO funDAO = new FuncionarioDAO();
    
    // Realizando a pesquisa com o campo e valor
    lst = funDAO.pesquisarFuncionario(campoBanco, pesquisar);
    
    // Adicionando os resultados à tabela
    for (Funcionario f : lst) {
        tb.addRow(new Object[]{
            f.getFun_cod(),
            f.getFun_nome(),
            f.getFun_telefone(),
            f.getFun_email(),
            f.getFun_cpf(),
            f.getFun_usuario(),
            f.getFun_senha()
        });
    }
}

    
    public void limparCampos() {
        txtfun_cod.setText("");
        txtfun_nome.setText("");
        txtfun_tel.setText("");
        txtfun_email.setText("");
        txtfun_cpf.setText("");
        txtfun_usuario.setText("");
        txtfun_senha.setText("");
    }
    public void selecionarCampo() {
    int linha = tbfun.getSelectedRow();

    if (linha >= 0) { // checa se alguma linha está selecionada
        Object cod = tbfun.getValueAt(linha, 0);
        Object nome = tbfun.getValueAt(linha, 1);
        Object tel = tbfun.getValueAt(linha, 2);
        Object email = tbfun.getValueAt(linha, 3);
        Object cpf = tbfun.getValueAt(linha, 4);
        Object usuario = tbfun.getValueAt(linha, 5);
        Object senha = tbfun.getValueAt(linha, 6);

        txtfun_cod.setText(cod == null ? "" : cod.toString());
        txtfun_nome.setText(nome == null ? "" : nome.toString());
        txtfun_tel.setText(tel == null ? "" : tel.toString());
        txtfun_email.setText(email == null ? "" : email.toString());
        txtfun_cpf.setText(cpf == null ? "" : cpf.toString());
        txtfun_usuario.setText(usuario == null ? "" : usuario.toString());
        txtfun_senha.setText(senha == null ? "" : senha.toString());
        
      // Só habilita campos e botões se o usuário não for FUNCIONÁRIO
        if (!"FUNC".equals(Sessao.tipoUsuario)) {
            habilitarCampos();
            btn_alterar.setEnabled(true);
            btn_excluir.setEnabled(true);
            btn_novo.setEnabled(false);
            btn_salvar.setEnabled(false);
    }
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtfun_cod = new javax.swing.JTextField();
        txtfun_nome = new javax.swing.JTextField();
        txtfun_email = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbfun = new javax.swing.JTable();
        btn_novo = new javax.swing.JButton();
        btn_excluir = new javax.swing.JButton();
        btn_alterar = new javax.swing.JButton();
        btn_salvar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtfun_busca = new javax.swing.JTextField();
        txtfun_tel = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtfun_cpf = new javax.swing.JFormattedTextField();
        comboBoxCampo = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        txtfun_senha = new javax.swing.JPasswordField();
        btn_recarregar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtfun_usuario = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cadastro de Funcionario");
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

        tbfun.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Nome", "Telefone", "Email", "CPF", "Usuario", "Senha"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbfun.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbfunMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbfun);

        btn_novo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/funcionario_3.png"))); // NOI18N
        btn_novo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_novoActionPerformed(evt);
            }
        });

        btn_excluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/FUNCIONARIO_2.png"))); // NOI18N
        btn_excluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_excluirActionPerformed(evt);
            }
        });

        btn_alterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/Editar_2.png"))); // NOI18N
        btn_alterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_alterarActionPerformed(evt);
            }
        });

        btn_salvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/Salvar_novo.png"))); // NOI18N
        btn_salvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salvarActionPerformed(evt);
            }
        });

        jLabel5.setText("Busca por:");

        txtfun_busca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtfun_buscaKeyReleased(evt);
            }
        });

        jLabel6.setText("CPF");

        comboBoxCampo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nome", "Telefone", "Email", "CPF" }));

        jLabel7.setText("Senha");

        btn_recarregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/recarregar.png"))); // NOI18N
        btn_recarregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recarregarActionPerformed(evt);
            }
        });

        jLabel8.setText("Usuario");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(btn_novo, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(btn_salvar)
                        .addGap(38, 38, 38)
                        .addComponent(btn_alterar)
                        .addGap(38, 38, 38)
                        .addComponent(btn_excluir))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(txtfun_busca, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(105, 105, 105)
                        .addComponent(btn_recarregar))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)
                        .addGap(36, 36, 36)
                        .addComponent(txtfun_cpf, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(12, 12, 12)
                                .addComponent(txtfun_tel, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7)
                                .addGap(25, 25, 25)
                                .addComponent(txtfun_senha, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtfun_cod, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(24, 24, 24)
                                        .addComponent(txtfun_nome, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(32, 32, 32)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtfun_usuario))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(28, 28, 28)
                                        .addComponent(txtfun_email, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtfun_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtfun_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtfun_nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jLabel2))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(txtfun_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtfun_tel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtfun_senha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel7))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtfun_cpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_novo, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_salvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_alterar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_excluir))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_recarregar)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))
                            .addComponent(txtfun_busca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_novoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_novoActionPerformed
      if (!"FUNC".equals(Sessao.tipoUsuario)) 
        salvar = "novo";
        limparCampos();
        habilitarCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
        txtfun_nome.requestFocus();
        
    }//GEN-LAST:event_btn_novoActionPerformed

    private void btn_salvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvarActionPerformed
        if(salvar.equals("novo")) {
            
            if (!validarCamposFuncionario()) {
        // Se não passou na validação, para aqui, não salva
        return;
              }
            
        Funcionario fun = new Funcionario();
        fun.getFun_cod();
        fun.setFun_nome(txtfun_nome.getText());
        fun.setFun_telefone(txtfun_tel.getText());
        fun.setFun_email(txtfun_email.getText());
        fun.setFun_cpf(txtfun_cpf.getText());
        fun.setFun_usuario(txtfun_usuario.getText());
        fun.setFun_senha(txtfun_senha.getText());
      
        FuncionarioDAO funDAO = new FuncionarioDAO();
        funDAO.incluirFuncionario(fun);  
        desabilitarCampos();
            limparCampos();
            btn_alterar.setEnabled(false);
            btn_excluir.setEnabled(false);
            btn_novo.setEnabled(true);
        }
      
        else {
            JOptionPane.showMessageDialog(null, "Selecione Novo Primeiro");
        }
        carregarTabelaFuncionarios(usuarioLogado);
    }//GEN-LAST:event_btn_salvarActionPerformed

    private void btn_excluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_excluirActionPerformed
        FuncionarioDAO funDAO = new FuncionarioDAO();
        funDAO.excluirFuncionario(txtfun_cod.getText());
        carregarTabelaFuncionarios(usuarioLogado); 
        desabilitarCampos();
        limparCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
    }//GEN-LAST:event_btn_excluirActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        carregarTabelaFuncionarios(usuarioLogado);
    }//GEN-LAST:event_formInternalFrameOpened

    private void btn_alterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_alterarActionPerformed
        Funcionario fun = new Funcionario();
        fun.setFun_cod(Converter.stringParaInt(txtfun_cod.getText()));
        fun.setFun_nome(txtfun_nome.getText());
        fun.setFun_telefone(txtfun_tel.getText());
        fun.setFun_email(txtfun_email.getText());
        fun.setFun_cpf(txtfun_cpf.getText());
        fun.setFun_usuario(txtfun_usuario.getText());        
        fun.setFun_senha(txtfun_senha.getText());
        
        FuncionarioDAO funDAO = new FuncionarioDAO();
        funDAO.alterarFuncionario(fun);
        desabilitarCampos();
            limparCampos();
            btn_alterar.setEnabled(false);
            btn_excluir.setEnabled(false);
            btn_novo.setEnabled(true);
        carregarTabelaFuncionarios(usuarioLogado);
    }//GEN-LAST:event_btn_alterarActionPerformed

    private void tbfunMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbfunMouseClicked
        selecionarCampo();
    }//GEN-LAST:event_tbfunMouseClicked

    private void txtfun_buscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtfun_buscaKeyReleased
        PesquisarTabelaFuncionarios();
    }//GEN-LAST:event_txtfun_buscaKeyReleased

    private void btn_recarregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recarregarActionPerformed
    tbfun.clearSelection();          // Remove seleção da tabela
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbfun;
    private javax.swing.JTextField txtfun_busca;
    private javax.swing.JTextField txtfun_cod;
    private javax.swing.JFormattedTextField txtfun_cpf;
    private javax.swing.JTextField txtfun_email;
    private javax.swing.JTextField txtfun_nome;
    private javax.swing.JPasswordField txtfun_senha;
    private javax.swing.JFormattedTextField txtfun_tel;
    private javax.swing.JTextField txtfun_usuario;
    // End of variables declaration//GEN-END:variables

public void definirPermissaoUsuario(String tipoUsuario) {
        if ("FUNC".equals(tipoUsuario)) {
            habilitarBotoesFuncionario(false);
        } else {
            habilitarBotoesFuncionario(true);
            desabilitarCampos(); // mantém os campos desabilitados até clicar em "Novo"
        }
    }       
    
public void habilitarBotoesFuncionario(boolean ativo) {
    btn_novo.setEnabled(ativo);
    btn_alterar.setEnabled(ativo);
    btn_excluir.setEnabled(ativo);
    btn_salvar.setEnabled(ativo);
}

private void desabilitarCampos() {
    txtfun_cod.setEnabled(false);
    txtfun_nome.setEnabled(false);
    txtfun_cpf.setEnabled(false);
    txtfun_tel.setEnabled(false);
    txtfun_email.setEnabled(false);
    txtfun_usuario.setEnabled(false);
    txtfun_senha.setEnabled(false);
    btn_salvar.setEnabled(false);
    btn_alterar.setEnabled(false);
    btn_excluir.setEnabled(false);
}

private void habilitarCampos() {
    txtfun_cod.setEnabled(true);
    txtfun_nome.setEnabled(true);
    txtfun_cpf.setEnabled(true);
    txtfun_email.setEnabled(true);
    txtfun_tel.setEnabled(true);
    txtfun_usuario.setEnabled(true);
    txtfun_senha.setEnabled(true);
    btn_salvar.setEnabled(true);

}

private void personalizarTabelaFuncionarios() {
    // Altura das linhas
    tbfun.setRowHeight(25);

    // Estilo do cabeçalho
    tbfun.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

    // Fonte dos dados
    tbfun.setFont(new Font("Arial", Font.PLAIN, 13));
}


}
