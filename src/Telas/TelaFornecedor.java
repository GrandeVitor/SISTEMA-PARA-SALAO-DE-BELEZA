/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Telas;

import DAO.FornecedorDAO;
import Objs.Fornecedor;
import Objs.Sessao;
import Util.Converter;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Aluno
 */
public class TelaFornecedor extends javax.swing.JInternalFrame {
String salvar = "";

    /**
     * Creates new form Fornecedor
     */
    public TelaFornecedor() {
        initComponents();
        personalizarTabelaFornecedores();
        //estilizarTelaFornecedor();
        desabilitarCampos();
        formatarCampo();
        formatarCampo2();
        formatarCampo3();
        
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
    
public boolean validarCNPJ(String cnpj) {
    cnpj = cnpj.replaceAll("\\D", "");
    
    if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) {
        return false;
    }

    try {
        int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += (cnpj.charAt(i) - '0') * peso1[i];
        }

        int dig1 = soma % 11;
        dig1 = (dig1 < 2) ? 0 : 11 - dig1;

        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += (cnpj.charAt(i) - '0') * peso2[i];
        }

        int dig2 = soma % 11;
        dig2 = (dig2 < 2) ? 0 : 11 - dig2;

        return cnpj.charAt(12) - '0' == dig1 && cnpj.charAt(13) - '0' == dig2;

    } catch (Exception e) {
        return false;
    }
}
    
    
   public boolean validarCamposFornecedor() {
       
    Color defaultBackground = Color.WHITE;
    Color errorBackground = new Color(255, 210, 220); // tom suave de vermelho

    // Resetar cores
    txtfor_nome.setBackground(defaultBackground);
    txtfor_item.setBackground(defaultBackground);
    txtfor_tel.setBackground(defaultBackground);
    txtfor_cpf.setBackground(defaultBackground);
    txtfor_cnpj.setBackground(defaultBackground);

    if (txtfor_nome.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Nome do fornecedor é obrigatório.");
        txtfor_nome.setBackground(errorBackground);
        txtfor_nome.requestFocus();
        return false;
    }

    if (txtfor_item.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Item fornecido é obrigatório.");
        txtfor_item.setBackground(errorBackground);
        txtfor_item.requestFocus();
        return false;
    }

    if (isCampoVazio(txtfor_tel.getText())) {
    JOptionPane.showMessageDialog(null, "Telefone do cliente é obrigatório.");
    txtfor_tel.setBackground(errorBackground);
    txtfor_tel.requestFocus();
    return false;
} else {
    // Remove tudo que não é número
    String telefoneNumeros = txtfor_tel.getText().replaceAll("\\D", "");
    if (telefoneNumeros.length() != 11) {
        JOptionPane.showMessageDialog(null, "Telefone inválido. Deve conter 11 dígitos (DDD + número).");
        txtfor_tel.setBackground(errorBackground);
        txtfor_tel.requestFocus();
        return false;
    }

    // Extrair o DDD
    String ddd = telefoneNumeros.substring(0, 2);
    if (!DDD_VALIDOS.contains(ddd)) {
        JOptionPane.showMessageDialog(null, "DDD inválido.");
        txtfor_tel.setBackground(errorBackground);
        txtfor_tel.requestFocus();
        return false;
    }
   
}

    String cpf = txtfor_cpf.getText().trim();
    String cnpj = txtfor_cnpj.getText().trim();

    if (isCampoVazio(cpf) && isCampoVazio(cnpj)) {
        JOptionPane.showMessageDialog(null, "Informe o CPF ou o CNPJ do fornecedor.");
        txtfor_cpf.setBackground(errorBackground);
        txtfor_cnpj.setBackground(errorBackground);
        txtfor_cpf.requestFocus(); // pode ser cnpj se preferir
        return false;
    }

    if (!isCampoVazio(cpf) && !validarCPF(cpf)) {
        JOptionPane.showMessageDialog(null, "CPF inválido.");
        txtfor_cpf.setBackground(errorBackground);
        txtfor_cpf.requestFocus();
        return false;
    }

    if (!isCampoVazio(cnpj) && !validarCNPJ(cnpj)) {
        JOptionPane.showMessageDialog(null, "CNPJ inválido.");
        txtfor_cnpj.setBackground(errorBackground);
        txtfor_cnpj.requestFocus();
        return false;
    }


    return true;
}


private boolean isCampoVazio(String texto) {
    // Remove tudo que não for número (dígito)
    String apenasNumeros = texto.replaceAll("\\D", "");
    return apenasNumeros.isEmpty();
}


    
    public void formatarCampo() {{
        try {
      MaskFormatter mask = new MaskFormatter("(##) #####-####");

       mask.install(txtfor_tel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Digite o numero de Telefone", "Mensagem de ERRO", HEIGHT);
        }
 
    } 
    }
    
     public void formatarCampo2() {{
        try {
     MaskFormatter mask = new MaskFormatter("##.###.###/####-##");

    mask.install(txtfor_cnpj);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Digite o numero do CNPJ", "Mensagem de ERRO", HEIGHT);
        }
 
    } 
    
    }
    
    public void formatarCampo3() {{
        try {
     MaskFormatter mask = new MaskFormatter("###.###.###-##");

     mask.install(txtfor_cpf);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Digite o numero do CPF", "Mensagem de ERRO", HEIGHT);
        }
 
    } 
    
    }     
    
     private void carregarTabelaFornecedores() {
        ArrayList<Fornecedor> lst = new ArrayList();
        
        DefaultTableModel tb = (DefaultTableModel) tbfor.getModel();
        
        tb.setNumRows(0);
        
        FornecedorDAO fornDAO = new FornecedorDAO();
            lst = fornDAO.buscaFornecedor();
            
        for (Fornecedor fo: lst) {
            tb.addRow(new Object[]{
                fo.getForn_cod(),
                fo.getForn_nome(),
                fo.getForn_telefone(),
                fo.getForn_cpf(),
                fo.getForn_cnpj(),
                fo.getForn_endereco(),
                fo.getForn_email(),
                fo.getForn_item()
            });
        }
        
    }
     
   private void PesquisarTabelaFornecedores() {
    String pesquisar = txtfor_busca.getText();  // Pega o texto digitado na caixa de pesquisa
    String campoSelecionado = comboBoxCampo.getSelectedItem().toString();  // Pega o campo selecionado na ComboBox
    String campoBanco = "";

    // Mapeando o campo selecionado para o nome da coluna no banco de dados
    switch (campoSelecionado) {
        case "Nome":
            campoBanco = "for_nome";
            break;
        case "Telefone":
            campoBanco = "for_telefone";
            break;
        case "CPF":
            campoBanco = "for_cpf";
            break;
        case "CNPJ":
            campoBanco = "for_cnpj";
            break;
        case "Endereço":
            campoBanco = "for_endereco";
            break;
        case "Email":
            campoBanco = "for_email";
            break;
        case "Produto":
            campoBanco = "for_item";
            break;
        default:
            JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido!");
            return;
    }

    // Cria uma lista de fornecedores e o modelo da tabela
    ArrayList<Fornecedor> lst = new ArrayList<>();
    DefaultTableModel tb = (DefaultTableModel) tbfor.getModel();
    
    // Limpa os dados da tabela antes de adicionar novos resultados
    tb.setNumRows(0);
    
    // Cria o DAO e chama o método de pesquisa
    FornecedorDAO fornDAO = new FornecedorDAO();
    lst = fornDAO.pesquisarFornecedor(campoBanco, pesquisar);
    
    // Preenche a tabela com os resultados da pesquisa
    for (Fornecedor fo : lst) {
        tb.addRow(new Object[]{
            fo.getForn_cod(),
            fo.getForn_nome(),
            fo.getForn_telefone(),
            fo.getForn_cpf(),
            fo.getForn_cnpj(),
            fo.getForn_endereco(),
            fo.getForn_email(),
            fo.getForn_item()
        });
    }
}

    
    public void limparCampos() {
        txtfor_cod.setText("");
        txtfor_nome.setText("");
        txtfor_tel.setText("");
        txtfor_cpf.setText("");
        txtfor_cnpj.setText("");
        txtfor_endereco.setText("");
        txtfor_email.setText("");
        txtfor_item.setText("");
    }
    
   public void selecionarCampo() {
    int linha = tbfor.getSelectedRow();

    if (linha != -1) {
        txtfor_cod.setText(getStringValue(tbfor.getValueAt(linha, 0)));
        txtfor_nome.setText(getStringValue(tbfor.getValueAt(linha, 1)));
        txtfor_tel.setText(getStringValue(tbfor.getValueAt(linha, 2)));

        // CPF
        Object cpfValue = tbfor.getValueAt(linha, 3);
        String cpfStr = normalizeCpfCnpj(cpfValue);
        txtfor_cpf.setText(cpfStr);
        if (cpfStr.isEmpty()) {
            tbfor.setValueAt(null, linha, 3);  // limpa a célula na tabela para evitar mascara fantasma
        }

        // CNPJ
        Object cnpjValue = tbfor.getValueAt(linha, 4);
        String cnpjStr = normalizeCpfCnpj(cnpjValue);
        txtfor_cnpj.setText(cnpjStr);
        if (cnpjStr.isEmpty()) {
            tbfor.setValueAt(null, linha, 4);
        }

        txtfor_endereco.setText(getStringValue(tbfor.getValueAt(linha, 5)));
        txtfor_email.setText(getStringValue(tbfor.getValueAt(linha, 6)));
        txtfor_item.setText(getStringValue(tbfor.getValueAt(linha, 7)));
        
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

// Método que trata CPF/CNPJ, removendo máscara vazia ou somente símbolos
private String normalizeCpfCnpj(Object value) {
    if (value == null) return "";
    String str = value.toString().trim();

    // Remove caracteres que podem ser máscara mas não dados reais, exemplo: '.', '-', '/'
    String onlyDigits = str.replaceAll("[^0-9]", "");

    // Se não tem dígitos, considera vazio
    if (onlyDigits.isEmpty()) {
        return "";
    }
    return str; // retorna o valor original, que pode conter máscara válida
}

private String getStringValue(Object value) {
    return value == null ? "" : value.toString();
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtfor_cod = new javax.swing.JTextField();
        txtfor_nome = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtfor_endereco = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtfor_email = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtfor_item = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbfor = new javax.swing.JTable();
        btn_novo = new javax.swing.JButton();
        btn_alterar = new javax.swing.JButton();
        btn_excluir = new javax.swing.JButton();
        btn_salvar = new javax.swing.JButton();
        txtfor_tel = new javax.swing.JFormattedTextField();
        txtfor_cnpj = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtfor_cpf = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtfor_busca = new javax.swing.JTextField();
        comboBoxCampo = new javax.swing.JComboBox<>();
        btn_recarregar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cadastro de Fornecedor");
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

        jLabel5.setText("Endereço");

        jLabel6.setText("Email");

        jLabel7.setText("Item");

        jLabel1.setText("Codigo");

        jLabel2.setText("Nome");

        jLabel3.setText("Telefone");

        jLabel4.setText("CNPJ");

        tbfor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Nome", "Telefone", "CPF", "CNPJ", "Endereço", "Email", "Produto"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbfor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbforMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbfor);

        btn_novo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/icons8-adicionar-usuário-masculino-50.png"))); // NOI18N
        btn_novo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_novoActionPerformed(evt);
            }
        });

        btn_alterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/icons8-editar-arquivo-de-texto-50.png"))); // NOI18N
        btn_alterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_alterarActionPerformed(evt);
            }
        });

        btn_excluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/icons8-excluir-usuário-macho-50.png"))); // NOI18N
        btn_excluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_excluirActionPerformed(evt);
            }
        });

        btn_salvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/icons8-salvar-50.png"))); // NOI18N
        btn_salvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salvarActionPerformed(evt);
            }
        });

        jLabel8.setText("CPF");

        jLabel9.setText("Busca");

        txtfor_busca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtfor_buscaKeyReleased(evt);
            }
        });

        comboBoxCampo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nome", "Telefone", "CPF", "CNPJ", "Endereço", "Email", "Produto" }));

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
            .addGroup(layout.createSequentialGroup()
                .addGap(133, 133, 133)
                .addComponent(btn_novo)
                .addGap(34, 34, 34)
                .addComponent(btn_salvar)
                .addContainerGap(353, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(27, 27, 27))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(18, 18, 18))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addGap(42, 42, 42))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(18, 18, 18)))))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtfor_cpf, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                            .addComponent(txtfor_nome)
                            .addComponent(txtfor_tel)
                            .addComponent(txtfor_cod))
                        .addGap(45, 45, 45)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtfor_endereco)
                                    .addComponent(txtfor_cnpj)
                                    .addComponent(txtfor_email)
                                    .addComponent(txtfor_item, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_alterar)
                                .addGap(39, 39, 39)
                                .addComponent(btn_excluir)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(txtfor_busca, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(98, 98, 98))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btn_recarregar)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtfor_cnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtfor_endereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtfor_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtfor_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtfor_nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtfor_tel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtfor_cpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(txtfor_item, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_novo)
                    .addComponent(btn_salvar)
                    .addComponent(btn_excluir)
                    .addComponent(btn_alterar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtfor_busca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(btn_recarregar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_alterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_alterarActionPerformed
        Fornecedor forn = new Fornecedor();
        forn.setForn_cod(Converter.stringParaInt(txtfor_cod.getText()));
        forn.setForn_nome(txtfor_nome.getText());
        forn.setForn_telefone(txtfor_tel.getText());
        forn.setForn_cpf(txtfor_cpf.getText());
        forn.setForn_cnpj(txtfor_cnpj.getText());
        forn.setForn_endereco(txtfor_endereco.getText());
        forn.setForn_email(txtfor_email.getText());
        forn.setForn_item(txtfor_item.getText());
        
        FornecedorDAO fornDAO = new FornecedorDAO();
        fornDAO.alterarFornecedor(forn);
        desabilitarCampos();
            limparCampos();
            btn_alterar.setEnabled(false);
            btn_excluir.setEnabled(false);
            btn_novo.setEnabled(true);
        
        carregarTabelaFornecedores();
        
    }//GEN-LAST:event_btn_alterarActionPerformed

    private void btn_novoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_novoActionPerformed
       if (!"FUNC".equals(Sessao.tipoUsuario)) 
        salvar = "novo";
        limparCampos();
        habilitarCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
        txtfor_nome.requestFocus();
    }//GEN-LAST:event_btn_novoActionPerformed

    private void btn_excluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_excluirActionPerformed
        FornecedorDAO fornDAO = new FornecedorDAO();
        fornDAO.excluirFornecedor(txtfor_cod.getText());
        carregarTabelaFornecedores();
        desabilitarCampos();
        limparCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
    }//GEN-LAST:event_btn_excluirActionPerformed

    private void btn_salvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvarActionPerformed
    if (salvar.equals("novo")) {
        
        if (!validarCamposFornecedor()) {
        // Se não passou na validação, para aqui, não salva
        return;
              }
        
         Fornecedor forn = new Fornecedor();
        forn.getForn_cod();
        forn.setForn_nome(txtfor_nome.getText());
        forn.setForn_telefone(txtfor_tel.getText());
        forn.setForn_cpf(txtfor_cpf.getText());
        forn.setForn_cnpj(txtfor_cnpj.getText());
        forn.setForn_endereco(txtfor_endereco.getText());
        forn.setForn_email(txtfor_email.getText());
        forn.setForn_item(txtfor_item.getText());
        
        FornecedorDAO fornDAO = new FornecedorDAO();
        fornDAO.incluirFornecedor(forn);
        desabilitarCampos();
            limparCampos();
            btn_alterar.setEnabled(false);
            btn_excluir.setEnabled(false);
            btn_novo.setEnabled(true);
        carregarTabelaFornecedores();
        }
   
        else {
            JOptionPane.showMessageDialog(null, "Selecione Novo Primeiro");
        }
           

    }//GEN-LAST:event_btn_salvarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        carregarTabelaFornecedores();
    }//GEN-LAST:event_formInternalFrameOpened

    private void tbforMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbforMouseClicked
        selecionarCampo();
    }//GEN-LAST:event_tbforMouseClicked

    private void txtfor_buscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtfor_buscaKeyReleased
        PesquisarTabelaFornecedores();
    }//GEN-LAST:event_txtfor_buscaKeyReleased

    private void btn_recarregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recarregarActionPerformed
    tbfor.clearSelection();          // Remove seleção da tabela
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbfor;
    private javax.swing.JTextField txtfor_busca;
    private javax.swing.JFormattedTextField txtfor_cnpj;
    private javax.swing.JTextField txtfor_cod;
    private javax.swing.JFormattedTextField txtfor_cpf;
    private javax.swing.JTextField txtfor_email;
    private javax.swing.JTextField txtfor_endereco;
    private javax.swing.JTextField txtfor_item;
    private javax.swing.JTextField txtfor_nome;
    private javax.swing.JFormattedTextField txtfor_tel;
    // End of variables declaration//GEN-END:variables

public void definirPermissaoUsuario(String tipoUsuario) {
        if ("FUNC".equals(tipoUsuario)) {
            habilitarBotoesFornecedor(false);
        } else {
            habilitarBotoesFornecedor(true);
            desabilitarCampos();
        }
    }       
    
public void habilitarBotoesFornecedor(boolean ativo) {
    btn_novo.setEnabled(ativo);
    btn_alterar.setEnabled(ativo);
    btn_excluir.setEnabled(ativo);
    btn_salvar.setEnabled(ativo);
}

private void desabilitarCampos() {
    txtfor_cod.setEnabled(false);
    txtfor_nome.setEnabled(false);
    txtfor_cpf.setEnabled(false);
    txtfor_email.setEnabled(false);
    txtfor_tel.setEnabled(false);
    txtfor_endereco.setEnabled(false);
    txtfor_cnpj.setEnabled(false);
    txtfor_item.setEnabled(false);

    btn_salvar.setEnabled(false);
    btn_alterar.setEnabled(false);
    btn_excluir.setEnabled(false);
}

private void habilitarCampos() {
    txtfor_cod.setEnabled(true);
    txtfor_nome.setEnabled(true);
    txtfor_cpf.setEnabled(true);
    txtfor_email.setEnabled(true);
    txtfor_tel.setEnabled(true);
    txtfor_endereco.setEnabled(true);
    txtfor_cnpj.setEnabled(true);
    txtfor_item.setEnabled(true);
    btn_salvar.setEnabled(true);

}

private void estilizarTelaFornecedor() {
    // Estilo geral da fonte
    Font fonteGeral = new Font("Segoe UI", Font.PLAIN, 14);
    Font fonteNegrito = new Font("Segoe UI", Font.BOLD, 14);

    // Labels
    JLabel[] labels = {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7};
    for (JLabel label : labels) {
        label.setFont(fonteNegrito);
    }

    // Campos de texto
    JTextField[] camposTexto = {
        txtfor_cod, txtfor_nome, txtfor_endereco,
        txtfor_email, txtfor_item
    };
    for (JTextField campo : camposTexto) {
        campo.setFont(fonteGeral);
        campo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        campo.setBackground(new Color(250, 250, 250));
    }

    // Botões
    JButton[] botoes = {btn_novo, btn_alterar, btn_excluir, btn_salvar, btn_recarregar};
    for (JButton botao : botoes) {
        botao.setFont(fonteNegrito);
        botao.setBackground(new Color(70, 130, 180)); // azul suave
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ComboBox
    comboBoxCampo.setFont(fonteGeral);
    comboBoxCampo.setBackground(Color.WHITE);
    comboBoxCampo.setFocusable(false);

    // ScrollPane da tabela
    jScrollPane1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    // Estilo da tabela

    // Plano de fundo do internal frame (opcional)
    this.getContentPane().setBackground(new Color(245, 245, 255));
}

private void personalizarTabelaFornecedores() {
    // Altura das linhas
    tbfor.setRowHeight(25);

    // Fonte do cabeçalho
    tbfor.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

    // Fonte dos dados
    tbfor.setFont(new Font("Arial", Font.PLAIN, 13));
}


}
