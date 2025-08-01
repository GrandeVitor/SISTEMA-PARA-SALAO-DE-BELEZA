/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package Telas;

import DAO.ClienteDAO;
import DAO.FuncionarioDAO;
import DAO.RelatorioAgendamentoDAO;
import JDBC.ConnectionFactory;
import Objs.Agendamento;
import Objs.Cliente;
import Objs.Funcionario;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 *
 * @author User
 */
public class TelaRelatorioAge extends javax.swing.JInternalFrame {

    /**
     * Creates new form TelaRelatorioAge
     */
    public TelaRelatorioAge() {
        initComponents();
        configurarLayoutBonitoRelatorioAge();
        comboStatus.setEnabled(false);
        funcionarioComboBox.setEnabled(false);
        clienteComboBox.setEnabled(false);
        comboStatus.setEnabled(false);
        carregarFuncionarios();
        carregarClientes();
        dataInicioChooser.setDateFormatString("dd/MM/yyyy");
        dataFimChooser.setDateFormatString("dd/MM/yyyy");
    }

   private String escolherPastaParaSalvar() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Escolha a pasta onde salvar o relatório");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int resultado = fileChooser.showSaveDialog(null);
    if (resultado == JFileChooser.APPROVE_OPTION) {
        return fileChooser.getSelectedFile().getAbsolutePath();
    } else {
        JOptionPane.showMessageDialog(null, "Operação cancelada.");
        return null;
    }
}


    
   private void carregarFuncionarios() {
    FuncionarioDAO funDAO = new FuncionarioDAO();
    List<Funcionario> funcionarios = funDAO.listarFuncionarios();

    funcionarioComboBox.removeAllItems(); // Evita itens duplicados
    
    for (Funcionario funcionario : funcionarios) {
        funcionarioComboBox.addItem(funcionario);
    }
}   
    
     private void carregarClientes() {
    ClienteDAO cliDAO = new ClienteDAO();
    List<Cliente> clientes = cliDAO.listarClientes();

    clienteComboBox.removeAllItems(); // Evita itens duplicados
    
    for (Cliente cliente : clientes) {
        clienteComboBox.addItem(cliente);
    }
}
     
   private java.sql.Date obterDataInicio() {
    // Verifica se o campo de data de início foi preenchido
    if (dataInicioChooser.getDate() != null) {
        java.util.Date utilDate = dataInicioChooser.getDate();
        return new java.sql.Date(utilDate.getTime());  // Converte para java.sql.Date
    }
    return null; // Retorna null se não houver data selecionada, permitindo que seja opcional
}

private java.sql.Date obterDataFim() {
    // Verifica se o campo de data de fim foi preenchido
    if (dataFimChooser.getDate() != null) {
        java.util.Date utilDate = dataFimChooser.getDate();
        return new java.sql.Date(utilDate.getTime());  // Converte para java.sql.Date
    }
    return null; // Retorna null se não houver data selecionada, permitindo que seja opcional
}

  
    
private void onFuncionarioSelecionado() {
    int clienteCod = 0;
    Funcionario funcionarioSelecionado = (Funcionario) funcionarioComboBox.getSelectedItem();

    if (clienteCod == 0) {
        clienteCod = -1;  // Ignorar filtro de cliente
    }

    if (funcionarioSelecionado == null || funcionarioSelecionado.getFun_cod() == 0) {
        gerarRelatorioSemFiltro();
        return;
    }

    java.sql.Date dataInicio = obterDataInicio();
    java.sql.Date dataFim = obterDataFim();

    try {
        RelatorioAgendamentoDAO relatorioDAO = new RelatorioAgendamentoDAO();
        List<Agendamento> agendamentos = relatorioDAO.buscarAgendamentosPorFuncionario(
            funcionarioSelecionado.getFun_cod(),
            dataInicio,
            dataFim
        );

        if (agendamentos != null && !agendamentos.isEmpty()) {

            // Gerar nome do arquivo automaticamente
            String nomeArquivo = "relatorio_funcionario_" + 
                funcionarioSelecionado.getFun_nome().replaceAll("[^a-zA-Z0-9_]", "_") + ".pdf";

            // Escolher pasta
            String caminhoPasta = escolherPastaParaSalvar();
            if (caminhoPasta == null) {
                return;  // Cancelou
            }

            // Caminho completo
            String caminhoCompleto = caminhoPasta + File.separator + nomeArquivo;

            // Gerar o relatório
            relatorioDAO.gerarRelatorioFuncionario(
                "CONCLUIDO",
                dataInicio,
                dataFim,
                funcionarioSelecionado.getFun_cod(),
                funcionarioSelecionado.getFun_nome(),
                clienteCod,
                caminhoCompleto
            );

            JOptionPane.showMessageDialog(null, 
                "Relatório gerado com sucesso para " + funcionarioSelecionado.getFun_nome() + "!");

        } else {
            JOptionPane.showMessageDialog(null, 
                "Nenhum agendamento encontrado para o funcionário " + funcionarioSelecionado.getFun_nome() + ".", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, 
            "Ocorreu um erro ao gerar o relatório: " + e.getMessage(), 
            "Erro", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


private void onClienteSelecionado() {
    // Pega o cliente selecionado na ComboBox
    Cliente clienteSelecionado = (Cliente) clienteComboBox.getSelectedItem();

    // Verifica se o usuário selecionou um item válido
    if (clienteSelecionado == null || clienteSelecionado.getCli_cod() == 0) {
        gerarRelatorioSemFiltro();
        return;
    }

    // Obtém as datas de início e fim
    java.sql.Date dataInicio = obterDataInicio();
    java.sql.Date dataFim = obterDataFim();

    try {
        // Instancia o DAO
        RelatorioAgendamentoDAO relatorioDAO = new RelatorioAgendamentoDAO();

        // Busca os agendamentos por cliente
        List<Agendamento> agendamentos = relatorioDAO.buscarAgendamentosPorCliente(clienteSelecionado.getCli_cod());

        if (agendamentos != null && !agendamentos.isEmpty()) {

            // Nome do arquivo gerado automaticamente
            String nomeArquivo = "relatorio_cliente_" + 
                clienteSelecionado.getCli_nome().replaceAll("[^a-zA-Z0-9_]", "_") + ".pdf";

            // Usuário escolhe a pasta onde deseja salvar
            String caminhoPasta = escolherPastaParaSalvar();
            if (caminhoPasta == null) {
                return; // Cancelou
            }

            // Caminho completo do arquivo
            String caminhoCompleto = caminhoPasta + File.separator + nomeArquivo;

            // Gera o relatório
            relatorioDAO.gerarRelatorio(
                "CONCLUIDO",
                dataInicio,
                dataFim,
                -1, // Código do funcionário (-1 para não filtrar)
                clienteSelecionado.getCli_cod(),
                caminhoCompleto
            );

            JOptionPane.showMessageDialog(null, 
                "Relatório gerado com sucesso para " + clienteSelecionado.getCli_nome() + "!");

        } else {
            JOptionPane.showMessageDialog(null, 
                "Nenhum agendamento encontrado para o cliente " + clienteSelecionado.getCli_nome() + ".", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, 
            "Ocorreu um erro ao gerar o relatório: " + e.getMessage(), 
            "Erro", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}




private void gerarRelatorioSemFiltro() {
    RelatorioAgendamentoDAO relatorioDAO = new RelatorioAgendamentoDAO();
    
    // Gerar relatório completo sem filtro de cliente ou funcionário
    String arquivoSalvar = "relatorio_completo.pdf"; // Nome genérico para o relatório completo
    
    // Chama o método DAO passando os parâmetros necessários
    // Passando "null" para as datas e 0 para o código do funcionário e cliente
    Date dataInicio = null;  // Ou você pode passar uma data específica, se necessário
    Date dataFim = null;     // Ou você pode passar uma data específica, se necessário
    int funcionarioCod = 0;  // 0 porque estamos ignorando o filtro de funcionário
    int clienteCod = 0;      // 0 porque estamos ignorando o filtro de cliente
    
    // Chama o método de geração de relatório
    relatorioDAO.gerarRelatorio("CONCLUIDO", dataInicio, dataFim, funcionarioCod, clienteCod, arquivoSalvar);
    
    // Exibe uma mensagem informando que o relatório foi gerado com sucesso
    JOptionPane.showMessageDialog(null, "Relatório completo gerado.");
}


  
    
 private void onStatusSelecionado() {
    String statusSelecionado = (String) comboStatus.getSelectedItem();

    if (statusSelecionado == null || statusSelecionado.trim().isEmpty()) {
        statusSelecionado = "TODOS";
    }

    java.sql.Date dataInicio = obterDataInicio();
    java.sql.Date dataFim = obterDataFim();

    RelatorioAgendamentoDAO relatorioDAO = new RelatorioAgendamentoDAO();

    // ✅ Verifica antes se tem registros
    List<Agendamento> lista = relatorioDAO.buscarAgendamentosPorStatus(
        statusSelecionado, dataInicio, dataFim
    );

    if (lista == null || lista.isEmpty()) {
        JOptionPane.showMessageDialog(null, 
            "Nenhum agendamento encontrado para o status: " + statusSelecionado + ".", 
            "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // ✔️ Se tem, escolhe onde salvar
    String caminhoPasta = escolherPastaParaSalvar();
    if (caminhoPasta == null) {
        return;
    }

    String nomeArquivo = "relatorio_status_" + 
        statusSelecionado.replaceAll("[^a-zA-Z0-9_]", "_") + ".pdf";

    String caminhoCompleto = caminhoPasta + File.separator + nomeArquivo;

    boolean sucesso = relatorioDAO.gerarRelatorioStatus(
        statusSelecionado, dataInicio, dataFim, caminhoCompleto
    );

    if (sucesso) {
        JOptionPane.showMessageDialog(null, 
            "Relatório gerado com sucesso para o status: " + statusSelecionado + "!");
    }
}



 public void gerarRelatorioServicos() {
    java.util.Date dataInicio = dataInicioChooser.getDate();
    java.util.Date dataFim = dataFimChooser.getDate();

    String nomeArquivoSugerido;

    if (dataInicio == null && dataFim == null) {
        // Nome para relatório geral (sem datas)
        nomeArquivoSugerido = "Relatorio_Servicos_Geral.pdf";
    } else {
        if (dataInicio == null || dataFim == null) {
            JOptionPane.showMessageDialog(null, "Por favor, selecione as duas datas.");
            return;
        }
        if (dataFim.before(dataInicio)) {
            JOptionPane.showMessageDialog(null, "Data final deve ser igual ou maior que a data inicial.");
            return;
        }
        // Nome para relatório com intervalo de datas
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        nomeArquivoSugerido = "Relatorio_Servicos_" + sdf.format(dataInicio) + "_a_" + sdf.format(dataFim) + ".pdf";
    }

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Salvar Relatório");
    fileChooser.setSelectedFile(new File(nomeArquivoSugerido));

    int userSelection = fileChooser.showSaveDialog(null);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File arquivoSelecionado = fileChooser.getSelectedFile();
        String caminho = arquivoSelecionado.getAbsolutePath();

        if (!caminho.toLowerCase().endsWith(".pdf")) {
            caminho += ".pdf";
        }

        RelatorioAgendamentoDAO rel = new RelatorioAgendamentoDAO();

        if (dataInicio == null && dataFim == null) {
            rel.gerarRelatorioPorServico(caminho);
        } else {
            // converte para java.sql.Date antes de passar
            java.sql.Date sqlDataInicio = new java.sql.Date(dataInicio.getTime());
            java.sql.Date sqlDataFim = new java.sql.Date(dataFim.getTime());
            rel.gerarRelatorioPorServicoPeriodo(sqlDataInicio, sqlDataFim, caminho);
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

        dataInicioChooser = new com.toedter.calendar.JDateChooser();
        dataFimChooser = new com.toedter.calendar.JDateChooser();
        statusCheckbox = new javax.swing.JCheckBox();
        funcionarioCheckbox = new javax.swing.JCheckBox();
        clienteCheckbox = new javax.swing.JCheckBox();
        comboStatus = new javax.swing.JComboBox<>();
        funcionarioComboBox = new javax.swing.JComboBox();
        clienteComboBox = new javax.swing.JComboBox();
        btn_GerarRelatorio = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        servicoCheckbox = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle("RELATÓRIO DE AGENDAMENTOS");

        statusCheckbox.setText("Status");
        statusCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusCheckboxActionPerformed(evt);
            }
        });

        funcionarioCheckbox.setText("Funcionario");
        funcionarioCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                funcionarioCheckboxActionPerformed(evt);
            }
        });

        clienteCheckbox.setText("Cliente");
        clienteCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clienteCheckboxActionPerformed(evt);
            }
        });

        comboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "AGENDADO", "CONCLUIDO", "CANCELADO" }));

        funcionarioComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                funcionarioComboBoxActionPerformed(evt);
            }
        });

        clienteComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clienteComboBoxActionPerformed(evt);
            }
        });

        btn_GerarRelatorio.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_GerarRelatorio.setText("Gerar Relatório");
        btn_GerarRelatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_GerarRelatorioActionPerformed(evt);
            }
        });

        jLabel1.setText("Data Inicial");

        jLabel2.setText("Data Final");

        servicoCheckbox.setText("Serviço");
        servicoCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                servicoCheckboxActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("RELATÓRIO DE AGENDAMENTOS");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(47, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addComponent(jLabel1)
                                .addGap(131, 131, 131)
                                .addComponent(jLabel2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dataInicioChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(70, 70, 70)
                                .addComponent(dataFimChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(statusCheckbox)
                                .addGap(18, 18, 18)
                                .addComponent(funcionarioCheckbox)
                                .addGap(18, 18, 18)
                                .addComponent(clienteCheckbox)
                                .addGap(18, 18, 18)
                                .addComponent(servicoCheckbox)))
                        .addGap(39, 39, 39))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(btn_GerarRelatorio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(comboStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(funcionarioComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clienteComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(42, 42, 42))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel3)
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(comboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(funcionarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(clienteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dataInicioChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dataFimChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_GerarRelatorio)
                        .addGap(37, 37, 37)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusCheckbox)
                    .addComponent(funcionarioCheckbox)
                    .addComponent(clienteCheckbox)
                    .addComponent(servicoCheckbox))
                .addGap(32, 32, 32))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void statusCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusCheckboxActionPerformed
         comboStatus.setEnabled(statusCheckbox.isSelected());
    }//GEN-LAST:event_statusCheckboxActionPerformed

    private void funcionarioCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_funcionarioCheckboxActionPerformed
         funcionarioComboBox.setEnabled(funcionarioCheckbox.isSelected());
    }//GEN-LAST:event_funcionarioCheckboxActionPerformed

    private void clienteCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clienteCheckboxActionPerformed
        clienteComboBox.setEnabled(clienteCheckbox.isSelected());
    }//GEN-LAST:event_clienteCheckboxActionPerformed

    private void btn_GerarRelatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_GerarRelatorioActionPerformed
     boolean statusSelecionado = statusCheckbox.isSelected();
    boolean funcionarioSelecionado = funcionarioCheckbox.isSelected();
    boolean clienteSelecionado = clienteCheckbox.isSelected();
    boolean servicoSelecionado = servicoCheckbox.isSelected(); // <- NOVO

    // Apenas filtro de Status
    if (statusSelecionado && !funcionarioSelecionado && !clienteSelecionado && !servicoSelecionado) {
        onStatusSelecionado();
    }
    // Apenas filtro de Funcionário
    else if (funcionarioSelecionado && !statusSelecionado && !clienteSelecionado && !servicoSelecionado) {
        onFuncionarioSelecionado();
    }
    // Apenas filtro de Cliente
    else if (clienteSelecionado && !statusSelecionado && !funcionarioSelecionado && !servicoSelecionado) {
        onClienteSelecionado();
    }
    // Apenas filtro de Serviço
    else if (servicoSelecionado && !statusSelecionado && !funcionarioSelecionado && !clienteSelecionado) {
        gerarRelatorioServicos();
    }
    // Caso nenhum ou múltiplos filtros estejam marcados
    else {
        JOptionPane.showMessageDialog(null, "Por favor, selecione apenas um filtro por vez.", "Aviso", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_btn_GerarRelatorioActionPerformed

    private void funcionarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_funcionarioComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_funcionarioComboBoxActionPerformed

    private void clienteComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clienteComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clienteComboBoxActionPerformed

    private void servicoCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_servicoCheckboxActionPerformed

    }//GEN-LAST:event_servicoCheckboxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_GerarRelatorio;
    private javax.swing.JCheckBox clienteCheckbox;
    private javax.swing.JComboBox clienteComboBox;
    private javax.swing.JComboBox<String> comboStatus;
    private com.toedter.calendar.JDateChooser dataFimChooser;
    private com.toedter.calendar.JDateChooser dataInicioChooser;
    private javax.swing.JCheckBox funcionarioCheckbox;
    private javax.swing.JComboBox funcionarioComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JCheckBox servicoCheckbox;
    private javax.swing.JCheckBox statusCheckbox;
    // End of variables declaration//GEN-END:variables

private void configurarLayoutBonitoRelatorioAge() {
    setTitle("Relatório de Agendamentos");

    // Título
    javax.swing.JLabel tituloLabel = new javax.swing.JLabel("RELATÓRIO DE AGENDAMENTOS");
    tituloLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
    tituloLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    // Labels
    jLabel1.setText("Data Inicial:");
    jLabel2.setText("Data Final:");

    // Checkboxes
    statusCheckbox.setText("Status");
    funcionarioCheckbox.setText("Funcionário");
    clienteCheckbox.setText("Cliente");
    servicoCheckbox.setText("Serviço");

    // Botão
    btn_GerarRelatorio.setText("Gerar Relatório");
    btn_GerarRelatorio.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

    // Desativa combos no início (mas deixa visíveis)
    comboStatus.setEnabled(false);
    funcionarioComboBox.setEnabled(false);
    clienteComboBox.setEnabled(false);

    // Painel de datas
    javax.swing.JPanel painelDatas = new javax.swing.JPanel(new java.awt.GridLayout(2, 2, 10, 5));
    painelDatas.add(jLabel1);
    painelDatas.add(jLabel2);
    painelDatas.add(dataInicioChooser);
    painelDatas.add(dataFimChooser);

    // Painel de comboboxes
    javax.swing.JPanel painelCombos = new javax.swing.JPanel(new java.awt.GridLayout(3, 2, 10, 10));
    painelCombos.add(new javax.swing.JLabel("Status:"));
    painelCombos.add(comboStatus);
    painelCombos.add(new javax.swing.JLabel("Funcionário:"));
    painelCombos.add(funcionarioComboBox);
    painelCombos.add(new javax.swing.JLabel("Cliente:"));
    painelCombos.add(clienteComboBox);

    // Painel de checkboxes
    javax.swing.JPanel painelCheckboxes = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));
    painelCheckboxes.add(statusCheckbox);
    painelCheckboxes.add(funcionarioCheckbox);
    painelCheckboxes.add(clienteCheckbox);
    painelCheckboxes.add(servicoCheckbox);

    // Painel principal
    javax.swing.JPanel painelPrincipal = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
    painelPrincipal.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 30, 20, 30));
    painelPrincipal.add(tituloLabel, java.awt.BorderLayout.NORTH);

    javax.swing.JPanel centro = new javax.swing.JPanel(new java.awt.BorderLayout(10, 20));
    centro.add(painelDatas, java.awt.BorderLayout.NORTH);
    centro.add(painelCombos, java.awt.BorderLayout.CENTER);
    centro.add(painelCheckboxes, java.awt.BorderLayout.SOUTH);

    painelPrincipal.add(centro, java.awt.BorderLayout.CENTER);

    javax.swing.JPanel painelBotao = new javax.swing.JPanel();
    painelBotao.add(btn_GerarRelatorio);
    painelPrincipal.add(painelBotao, java.awt.BorderLayout.SOUTH);

    setContentPane(painelPrincipal);
    pack();
}



}

