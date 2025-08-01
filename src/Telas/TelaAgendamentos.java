/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Telas;

import DAO.AgendamentoDAO;
import DAO.AgendamentoProdutoDAO;
import DAO.ClienteDAO;
import DAO.EnviarEmail;
import static DAO.EnviarEmail.enviarEmailConfirmacao;
import DAO.EstoqueDAO;
import DAO.FuncionarioDAO;
import Objs.Agendamento;
import Objs.Cliente;
import Objs.Estoque;
import Objs.Funcionario;
import Util.Converter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author User
 */
public class TelaAgendamentos extends javax.swing.JInternalFrame {

    String salvar = "";
    String produtos = "";

    /**
     * Creates new form Agendamentos
     */
    public TelaAgendamentos() {
        initComponents();
        configurarLayoutBonitoAgendamentos();
        desabilitarCampos();
        formatarCampoHora();
        txtage_data.setDateFormatString("dd/MM/yyyy");
        carregarClientes();
        carregarFuncionarios();
        configurarComboBoxServicos();

    }

    public boolean validarCamposAgendamento() {
        Color defaultBackground = Color.WHITE;
        Color errorBackground = new Color(255, 210, 220); // tom suave de vermelho

        // Resetar cores
        txtage_data.getDateEditor().getUiComponent().setBackground(defaultBackground);
        txtHora.setBackground(defaultBackground);
        txtage_preco2.setBackground(defaultBackground);

        if (txtage_data.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Selecione uma data.");
            txtage_data.getDateEditor().getUiComponent().setBackground(errorBackground);
            txtage_data.requestFocus();
            return false;
        }

        String hora = txtHora.getText().trim();

        // Verifica se o campo está "vazio" (apenas máscara)
        if (hora.equals("__:__")) {  // Considerando placeholder '_'
            JOptionPane.showMessageDialog(null, "Informe o horário.");
            txtHora.setBackground(errorBackground);
            txtHora.requestFocus();
            return false;
        }

        // Valida se a hora é válida (00:00 a 23:59)
        if (!validarHora()) {
            // A função validarHora() deve mostrar mensagem e ajustar foco/cor
            return false;
        }

        if (txtage_preco2.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Informe o preço.");
            txtage_preco2.setBackground(errorBackground);
            txtage_preco2.requestFocus();
            return false;
        }

        return true;
    }

    public void formatarCampoHora() {
        try {
            MaskFormatter mask = new MaskFormatter("##:##");
            mask.setPlaceholderCharacter('_');
            mask.install(txtHora);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao aplicar a máscara de hora", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarHora() {
        String hora = txtHora.getText();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime.parse(hora, formatter);
            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Horário inválido. Use o formato 00:00 a 23:59.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtHora.requestFocus();
            return false;
        }
    }

    private void carregarTabelaAgendamentos() {
        ArrayList<Agendamento> lst = new ArrayList();

        DefaultTableModel tb = (DefaultTableModel) tbage.getModel();

        tb.setNumRows(0);

        AgendamentoDAO ageDAO = new AgendamentoDAO();
        lst = ageDAO.buscaAgendamento();

        for (Agendamento a : lst) {
            tb.addRow(new Object[]{
                a.getAge_cod(),
                a.getCliente_nome(), // Exibe nome do cliente
                a.getFuncionario_nome(), // Exibe nome do funcionário
                a.getAge_servico(),
                a.getAge_preco(),
                a.getAge_data(),
                a.getAge_hora(),
                a.getAge_status()
            });
        }
        personalizarTabelaAgendamentos();
    }

    private void PesquisarTabelaAgendamentos() {
        String pesquisar = txt_agendamento_busca.getText();
        String campoSelecionado = comboBoxAgendamentoCampo.getSelectedItem().toString();
        String campoBanco = "";

        // Mapeia o nome visível do comboBox para o campo real do banco de dados
        switch (campoSelecionado) {
            case "Data":
                campoBanco = "age_data";
                break;
            case "Serviço":
                campoBanco = "age_servico";
                break;
            case "Hora":
                campoBanco = "age_hora";
                break;
            case "Status":
                campoBanco = "age_status";
                break;
            case "Cliente":
                campoBanco = "cli_nome";
                break;
            case "Funcionario":
                campoBanco = "fun_nome";
                break;
            default:
                JOptionPane.showMessageDialog(null, "Campo de pesquisa inválido!");
                return;
        }

        ArrayList<Agendamento> lista;
        DefaultTableModel tb = (DefaultTableModel) tbage.getModel();
        tb.setNumRows(0);

        AgendamentoDAO ageDAO = new AgendamentoDAO();
        lista = ageDAO.pesquisarAgendamento(campoBanco, pesquisar);

        for (Agendamento age : lista) {
            tb.addRow(new Object[]{
                age.getAge_cod(),
                age.getCliente_nome(),
                age.getFuncionario_nome(),
                age.getAge_servico(),
                age.getAge_preco(),
                age.getAge_data(),
                age.getAge_hora(),
                age.getAge_status()
            });
        }
    }

   private void configurarComboBoxServicos() {
    HashMap<String, Double> precosServicos = new HashMap<>();
    precosServicos.put("Corte de Cabelo", 25.00);
    precosServicos.put("Escova", 28.00);
    precosServicos.put("Hidratação Capilar", 30.00);
    precosServicos.put("Coloração", 35.00);
    precosServicos.put("Manicure", 20.00);
    precosServicos.put("Pedicure", 23.00);
    precosServicos.put("Design de Sobrancelha", 15.00);
    precosServicos.put("Maquiagem", 25.00);
    precosServicos.put("Chapinha", 22.00);
    precosServicos.put("Depilação", 18.00);

  
    combo_age_servico.addItem("Selecione um serviço...");
    
    for (String servico : precosServicos.keySet()) {
        combo_age_servico.addItem(servico);
    }
// Adicionando o evento de seleção no comboBox
combo_age_servico.addActionListener(e -> {
    String servicoSelecionado = (String) combo_age_servico.getSelectedItem();
    if (precosServicos.containsKey(servicoSelecionado)) {
        txtage_preco2.setText(String.format("%.2f", precosServicos.get(servicoSelecionado)));
    } else {
        txtage_preco2.setText("");
    }
});

}



   public void limparCampos() {
    txtage_cod.setText("");
    txtage_data.setDate(null);
    txtHora.setText("");
    txtage_preco2.setText("");

    combo_age_servico.setSelectedIndex(0);      // Seleciona "Selecione um serviço..."
    combo_cliente_cod.setSelectedIndex(0);      // Seleciona "Selecione um cliente..."
    combo_funcionario_cod.setSelectedIndex(0);  // Seleciona "Selecione um funcionário..."
}


   private void carregarClientes() {
    ClienteDAO cliDAO = new ClienteDAO();
    List<Cliente> clientes = cliDAO.listarClientes();

    combo_cliente_cod.removeAllItems(); // Evita itens duplicados

    // Cria o placeholder "Selecione um cliente..."
    Cliente clientePlaceholder = new Cliente();
    clientePlaceholder.setCli_cod(0); // código inválido para identificação
    clientePlaceholder.setCli_nome("Selecione um cliente...");

    combo_cliente_cod.addItem(clientePlaceholder);

    for (Cliente cliente : clientes) {
        combo_cliente_cod.addItem(cliente);
    }
}


   private void carregarFuncionarios() {
    FuncionarioDAO funDAO = new FuncionarioDAO();
    List<Funcionario> funcionarios = funDAO.listarFuncionarios();

    combo_funcionario_cod.removeAllItems();

    Funcionario funcionarioPlaceholder = new Funcionario();
    funcionarioPlaceholder.setFun_cod(0); // código inválido para placeholder
    funcionarioPlaceholder.setFun_nome("Selecione um funcionário...");

    combo_funcionario_cod.addItem(funcionarioPlaceholder);

    for (Funcionario funcionario : funcionarios) {
        combo_funcionario_cod.addItem(funcionario);
    }
}


    public void selecionarCampo() {

        /* -------- 1. Verifica se existe linha selecionada -------- */
        int linha = tbage.getSelectedRow();
        if (linha == -1) {                               // Nenhuma linha clicada
            JOptionPane.showMessageDialog(
                    null,
                    "Selecione uma linha da tabela para editar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;                                      // Sai do método
        }

        /* -------- 2. Preenche campos básicos -------- */
        txtage_cod.setText(String.valueOf(tbage.getValueAt(linha, 0)));

        String nomeCliente = String.valueOf(tbage.getValueAt(linha, 1));
        String nomeFuncionario = String.valueOf(tbage.getValueAt(linha, 2));

        combo_age_servico.setSelectedItem(String.valueOf(tbage.getValueAt(linha, 3)));
        txtage_preco2.setText(String.valueOf(tbage.getValueAt(linha, 4)));

        /* -------- 3. Data (JDateChooser) -------- */
        Object dataObject = tbage.getValueAt(linha, 5);
        if (dataObject instanceof java.util.Date) {
            txtage_data.setDate((java.util.Date) dataObject);
        } else if (dataObject instanceof String) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // ajuste se necessário
                java.util.Date dataConvertida = sdf.parse((String) dataObject);
                txtage_data.setDate(dataConvertida);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Erro ao converter data: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                txtage_data.setDate(null);
            }
        } else {
            txtage_data.setDate(null);
        }

        txtHora.setText(String.valueOf(tbage.getValueAt(linha, 6)));
        combo_age_status.setSelectedItem(String.valueOf(tbage.getValueAt(linha, 7)));

        /* -------- 4. Seleciona Cliente e Funcionário nos combos -------- */
        for (int i = 0; i < combo_cliente_cod.getItemCount(); i++) {
            Cliente cli = (Cliente) combo_cliente_cod.getItemAt(i);
            if (cli.getCli_nome().equals(nomeCliente)) {
                combo_cliente_cod.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < combo_funcionario_cod.getItemCount(); i++) {
            Funcionario fun = (Funcionario) combo_funcionario_cod.getItemAt(i);
            if (fun.getFun_nome().equals(nomeFuncionario)) {
                combo_funcionario_cod.setSelectedIndex(i);
                break;
            }
        }

        /* -------- 5. Habilita botões/campos -------- */
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

        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbage = new javax.swing.JTable();
        btn_excluir = new javax.swing.JButton();
        btn_alterar = new javax.swing.JButton();
        btn_salvar = new javax.swing.JButton();
        btn_novo = new javax.swing.JButton();
        btn_recarregar = new javax.swing.JButton();
        comboBoxAgendamentoCampo = new javax.swing.JComboBox<>();
        txt_agendamento_busca = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtHora = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtage_preco2 = new javax.swing.JTextField();
        combo_age_status = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        txtage_data = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        combo_cliente_cod = new javax.swing.JComboBox();
        combo_funcionario_cod = new javax.swing.JComboBox();
        combo_age_servico = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtage_cod = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("AGENDAMENTOS");
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        tbage.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Cliente", "Funcionario", "Serviço", "Preço", "Data", "Hora", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbageMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbage);

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

        btn_salvar.setText("Salvar");
        btn_salvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_salvarActionPerformed(evt);
            }
        });

        btn_novo.setText("Novo Agendamento");
        btn_novo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_novoActionPerformed(evt);
            }
        });

        btn_recarregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/recarregar.png"))); // NOI18N
        btn_recarregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recarregarActionPerformed(evt);
            }
        });

        comboBoxAgendamentoCampo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Data", "Serviço", "Hora", "Status", "Cliente", "Funcionario" }));

        txt_agendamento_busca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_agendamento_buscaKeyReleased(evt);
            }
        });

        jLabel6.setText("Busca");

        jLabel5.setText("Preço");

        txtage_preco2.setEditable(false);

        combo_age_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AGENDADO", "CONCLUIDO", "CANCELADO" }));

        jLabel7.setText("Cliente");

        jLabel8.setText("Funcionario");

        jLabel1.setText("Codigo");

        combo_cliente_cod.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cliente", "Item 2", "Item 3", "Item 4" }));

        combo_funcionario_cod.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Funcionario", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Data");

        jLabel4.setText("Hora");

        jLabel9.setText("Serviço");

        jLabel10.setText("Status");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtage_cod, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtage_data, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtage_preco2, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(37, 37, 37)
                                .addComponent(combo_cliente_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10))
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(combo_funcionario_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(combo_age_servico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(combo_age_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_novo)
                        .addGap(23, 23, 23)
                        .addComponent(btn_salvar)
                        .addGap(17, 17, 17)
                        .addComponent(btn_alterar)
                        .addGap(15, 15, 15)
                        .addComponent(btn_excluir)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(10, 10, 10)
                .addComponent(txt_agendamento_busca, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(comboBoxAgendamentoCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(104, 104, 104)
                .addComponent(btn_recarregar))
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtage_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo_cliente_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel7))))
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel3))
                            .addComponent(txtage_data, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel4))
                            .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtage_preco2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel8)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel9)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel10))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(combo_funcionario_cod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(combo_age_servico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(combo_age_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_novo)
                    .addComponent(btn_salvar)
                    .addComponent(btn_alterar)
                    .addComponent(btn_excluir))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel6))
                    .addComponent(txt_agendamento_busca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxAgendamentoCampo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_recarregar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel2))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_salvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_salvarActionPerformed

       // Validação do serviço
    String servicoSelecionado = (String) combo_age_servico.getSelectedItem();
    if (servicoSelecionado == null || servicoSelecionado.equals("Selecione um serviço...")) {
        JOptionPane.showMessageDialog(null, "Por favor, selecione um serviço válido.");
        return; // Interrompe o salvamento
    }

    // Validação do cliente (verifica se selecionou placeholder)
    Cliente clienteSelecionado = (Cliente) combo_cliente_cod.getSelectedItem();
    if (clienteSelecionado == null || clienteSelecionado.getCli_cod() == 0) {
        JOptionPane.showMessageDialog(null, "Por favor, selecione um cliente válido.");
        return;
    }

    // Validação do funcionário (verifica se selecionou placeholder)
    Funcionario funcionarioSelecionado = (Funcionario) combo_funcionario_cod.getSelectedItem();
    if (funcionarioSelecionado == null || funcionarioSelecionado.getFun_cod() == 0) {
        JOptionPane.showMessageDialog(null, "Por favor, selecione um funcionário válido.");
        return;
    }

    java.util.Date dataUtil = txtage_data.getDate();

    // Verifica se foi selecionada uma data
    if (dataUtil == null) {
        JOptionPane.showMessageDialog(null, "Selecione uma data válida!");
        return;
    }

    // Converte para java.sql.Date (compatível com banco de dados)
    java.sql.Date dataSQL = new java.sql.Date(dataUtil.getTime());

    // Formata a data para o formato desejado (dd/MM/yyyy)
    SimpleDateFormat formatoDesejado = new SimpleDateFormat("dd/MM/yyyy");
    String dataFormatada = formatoDesejado.format(dataSQL);

    if (salvar.equals("novo")) {

        if (!validarCamposAgendamento()) {
            // Se não passou na validação, para aqui, não salva
            return;
        }

        Agendamento age = new Agendamento();
        age.getAge_cod();
        age.setCliente_cod(clienteSelecionado.getCli_cod());
        age.setFuncionario_cod(funcionarioSelecionado.getFun_cod());
        age.setAge_servico(servicoSelecionado);
        age.setAge_data(dataSQL);  // Salvando no banco com o formato java.sql.Date
        age.setAge_hora(txtHora.getText());
        age.setAge_preco(Converter.stringParaFloat(txtage_preco2.getText()));
        age.setAge_status((String) combo_age_status.getSelectedItem());

        AgendamentoDAO ageDAO = new AgendamentoDAO();
        ageDAO.inserirAgenda(age);

        // Após salvar, buscar os agendamentos do cliente no mesmo dia e enviar e-mail
        try {

            int clienteId = clienteSelecionado.getCli_cod();
            String clienteNome = clienteSelecionado.getCli_nome();
            String clienteEmail = clienteSelecionado.getCli_email();

            List<Agendamento> agendamentosDoDia = ageDAO.buscarAgendamentosDoClienteNoDia(clienteId, dataSQL);

            EnviarEmail.enviarConfirmacaoPorEmail(clienteEmail, clienteNome, dataFormatada, agendamentosDoDia);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Agendamento salvo, mas falha ao enviar e-mail: " + ex.getMessage());
        }

        desabilitarCampos();
        limparCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
        btn_novo.setEnabled(true);
        carregarTabelaAgendamentos();
    } else {
        JOptionPane.showMessageDialog(null, "Selecione Novo Primeiro");
    }

    }//GEN-LAST:event_btn_salvarActionPerformed

    private void btn_novoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_novoActionPerformed
        salvar = "novo";
        limparCampos();
        habilitarCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
        txtage_data.requestFocus();
        txtage_data.getCalendarButton().doClick();

    }//GEN-LAST:event_btn_novoActionPerformed

    private void btn_excluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_excluirActionPerformed
        AgendamentoDAO ageDAO = new AgendamentoDAO();
        ageDAO.excluirAgendamento(txtage_cod.getText());
        desabilitarCampos();
        limparCampos();
        btn_novo.setEnabled(true);
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
        carregarTabelaAgendamentos();
    }//GEN-LAST:event_btn_excluirActionPerformed

    private void btn_alterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_alterarActionPerformed

        java.util.Date dataUtil = txtage_data.getDate();

        if (dataUtil == null) {
            JOptionPane.showMessageDialog(null, "Selecione uma data válida!");
            return;
        }

        if (!validarHora()) {
            return;
        }

        java.sql.Date dataSQL = new java.sql.Date(dataUtil.getTime());

        Agendamento age = new Agendamento();
        age.setAge_cod(Converter.stringParaInt(txtage_cod.getText()));
        age.setCliente_cod(((Cliente) combo_cliente_cod.getSelectedItem()).getCli_cod());
        age.setFuncionario_cod(((Funcionario) combo_funcionario_cod.getSelectedItem()).getFun_cod());
        age.setAge_servico((String) combo_age_servico.getSelectedItem());
        age.setAge_data(dataSQL);
        age.setAge_hora(txtHora.getText());
        age.setAge_preco(Converter.stringParaFloat(txtage_preco2.getText()));

        String status = (String) combo_age_status.getSelectedItem();
        age.setAge_status(status);

        AgendamentoDAO ageDAO = new AgendamentoDAO();
        ageDAO.alterarAgendamento(age);

        // Supondo que dataUtil já existe aqui no método
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = formato.format(dataUtil);

      if (status.equalsIgnoreCase("CONCLUIDO")) {
    AgendamentoProdutoDAO agProdDAO = new AgendamentoProdutoDAO();

    String mensagem = "Deseja registrar produtos utilizados no agendamento do dia " + dataFormatada + "?";

    int opcao = JOptionPane.showConfirmDialog(
            null,
            mensagem,
            "Produtos Utilizados",
            JOptionPane.YES_NO_OPTION
    );

    if (opcao == JOptionPane.YES_OPTION) {
        agProdDAO.registrarProdutosUtilizados(age.getAge_cod()); // ✅ Correto
    }
}
        desabilitarCampos();
        limparCampos();
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
        btn_novo.setEnabled(true);
        carregarTabelaAgendamentos();

    }//GEN-LAST:event_btn_alterarActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        carregarTabelaAgendamentos();
    }//GEN-LAST:event_formInternalFrameOpened

    private void tbageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbageMouseClicked
        selecionarCampo();
    }//GEN-LAST:event_tbageMouseClicked

    private void txt_agendamento_buscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_agendamento_buscaKeyReleased
        PesquisarTabelaAgendamentos();
    }//GEN-LAST:event_txt_agendamento_buscaKeyReleased

    private void btn_recarregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recarregarActionPerformed
        tbage.clearSelection();         // Remove seleção da tabela
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
    private javax.swing.JComboBox<String> comboBoxAgendamentoCampo;
    private javax.swing.JComboBox<String> combo_age_servico;
    private javax.swing.JComboBox<String> combo_age_status;
    private javax.swing.JComboBox combo_cliente_cod;
    private javax.swing.JComboBox combo_funcionario_cod;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbage;
    private javax.swing.JFormattedTextField txtHora;
    private javax.swing.JTextField txt_agendamento_busca;
    private javax.swing.JTextField txtage_cod;
    private com.toedter.calendar.JDateChooser txtage_data;
    private javax.swing.JTextField txtage_preco2;
    // End of variables declaration//GEN-END:variables

    private void desabilitarCampos() {
        txtage_cod.setEnabled(false);
        txtage_data.setEnabled(false);
        txtHora.setEnabled(false);
        txtage_preco2.setEnabled(false);
        combo_cliente_cod.setEnabled(false);
        combo_funcionario_cod.setEnabled(false);
        combo_age_servico.setEnabled(false);
        combo_age_status.setEnabled(false);
        btn_salvar.setEnabled(false);
        btn_alterar.setEnabled(false);
        btn_excluir.setEnabled(false);
    }

    private void habilitarCampos() {
        txtage_cod.setEnabled(true);
        txtage_data.setEnabled(true);
        txtHora.setEnabled(true);
        txtage_preco2.setEnabled(true);
        combo_cliente_cod.setEnabled(true);
        combo_funcionario_cod.setEnabled(true);
        combo_age_servico.setEnabled(true);
        combo_age_status.setEnabled(true);
        btn_salvar.setEnabled(true);
        btn_alterar.setEnabled(true);
        btn_excluir.setEnabled(true);
    }

    private void configurarLayoutBonitoAgendamentos() {
        setTitle("Tela de Agendamentos");

        // Painel principal com BorderLayout
        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // --- Painel de formulário de agendamento ---
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Dados do Agendamento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 1: Código
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        painelFormulario.add(txtage_cod, gbc);

        // Linha 2: Data
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Data:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        painelFormulario.add(txtage_data, gbc);

        // Linha 3: Hora
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Hora:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        painelFormulario.add(txtHora, gbc);

        // Linha 4: Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        painelFormulario.add(combo_age_status, gbc);

        // Linha 5: Serviço
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Serviço:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1;
        painelFormulario.add(combo_age_servico, gbc);

        // Linha 6: Preço
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Preço:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1;
        painelFormulario.add(txtage_preco2, gbc);

        // Linha 7: Funcionário
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Funcionário:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1;
        painelFormulario.add(combo_funcionario_cod, gbc);

        // Linha 8: Cliente
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0;
        painelFormulario.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1;
        painelFormulario.add(combo_cliente_cod, gbc);

        // --- Painel da tabela ---
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Agendamentos"));
        painelTabela.add(jScrollPane1, BorderLayout.CENTER);

        // --- Painel de botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        painelBotoes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Ações"));
        painelBotoes.add(btn_novo);
        painelBotoes.add(btn_salvar);
        painelBotoes.add(btn_alterar);
        painelBotoes.add(btn_excluir);
        painelBotoes.add(btn_recarregar);

        // --- Painel de busca ---
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painelBusca.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Busca"));
        painelBusca.add(new JLabel("Buscar por:"));
        painelBusca.add(comboBoxAgendamentoCampo);
        txt_agendamento_busca.setColumns(20); // <-- Ajuste de largura aqui
        painelBusca.add(txt_agendamento_busca);

        // Organizando os painéis na tela principal
        painelPrincipal.add(painelFormulario, BorderLayout.WEST);
        painelPrincipal.add(painelTabela, BorderLayout.CENTER);

        // Painel inferior que junta busca e botões
        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(painelBusca, BorderLayout.WEST);
        painelInferior.add(painelBotoes, BorderLayout.EAST);

        painelPrincipal.add(painelInferior, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
        pack();
    }

    private void personalizarTabelaAgendamentos() {
    // Aumenta a altura das linhas
    tbage.setRowHeight(25);

    // Fonte do cabeçalho
    tbage.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

    // Fonte dos dados da tabela
    tbage.setFont(new Font("Arial", Font.PLAIN, 13));
}

    
}
