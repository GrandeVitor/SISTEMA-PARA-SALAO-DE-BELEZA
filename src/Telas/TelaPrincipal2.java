/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Telas;

import JDBC.ConnectionFactory;
import Objs.Sessao;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Aluno
 */
public class TelaPrincipal2 extends javax.swing.JFrame {
private BufferedImage imagemOriginalFundo;

    Connection conecta = null;

    /**
     * Creates new form TelaPrincipal2
     */
    public TelaPrincipal2() {
        initComponents();
        setLocationRelativeTo(null); // Centraliza a janela na tela        
        esconderMenu();
        verificarAdministrador();
    }

    private void verificarAdministrador() {
        try {
            conecta = new ConnectionFactory().conecta();
            String sql = "SELECT COUNT(*) AS total FROM tb_adm";
            PreparedStatement stmt = conecta.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt("total") == 0) {
                // Abre a tela de cadastro com o sinal de que é o primeiro ADM
                TelaAdministrador cadastroAdm = new TelaAdministrador(true);
                jDesktopPane1.add(cadastroAdm);
                cadastroAdm.setVisible(true);
                int x = (jDesktopPane1.getWidth() - cadastroAdm.getWidth()) / 2;
                int y = (jDesktopPane1.getHeight() - cadastroAdm.getHeight()) / 2;
                cadastroAdm.setLocation(x, y);
            } else {
                // Já existe ADM, então abre a tela de login
                TelaLogin login = new TelaLogin(this); // this = TelaPrincipal
                jDesktopPane1.add(login);
                login.setVisible(true);
                // Centraliza no meio do JDesktopPane
                int x = (jDesktopPane1.getWidth() - login.getWidth()) / 2;
                int y = (jDesktopPane1.getHeight() - login.getHeight()) / 2;
                login.setLocation(x, y);
            }

            conecta.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

// Método para esconder ou desabilitar os menus
    private void esconderMenu() {
        jMenu2.setVisible(false);  // Desabilitar os menus inicialmente
        txt_agenda.setVisible(false);
        menuitemcaixa.setVisible(false);
        txtrelatorio.setVisible(false);
        txt_opções.setVisible(false);
        txtEstoque.setVisible(false);
        txt_produtousados.setVisible(false);
    }

    // Método para mostrar ou habilitar os menus após login
    private void mostrarMenu() {
        jMenu2.setVisible(true);
        txt_agenda.setVisible(true);
        menuitemcaixa.setVisible(true);
        txtrelatorio.setVisible(true);
        txt_opções.setVisible(true);
        txtEstoque.setVisible(true);
        txt_produtousados.setVisible(true);

    }

    // 👉 Para login de administrador
    public void habilitarMenusAdministrador() {
        jMenu2.setVisible(true);
        txt_agenda.setVisible(true);
        txt_agenda.setVisible(true); // Menu de administração
        txtad.setVisible(true);
        txtfun2.setVisible(true);
        menuitemcaixa.setVisible(true);
        txtrelatorio.setVisible(true);
        txt_opções.setVisible(true);
        txtEstoque.setVisible(true);
        txt_produtousados.setVisible(true);

    }

    // 👉 Para login de funcionário
    public void habilitarMenusFuncionario() {
        jMenu2.setVisible(true);
        txt_agenda.setVisible(true);
        txtad.setVisible(false);
        txtfun2.setVisible(true);
        txt_agenda.setVisible(true); // Oculta menus de administração
        menuitemcaixa.setVisible(true);
        txtrelatorio.setVisible(false); // Oculta relatórios
        txt_opções.setVisible(true);
        txtEstoque.setVisible(true);
        txt_produtousados.setVisible(true);

    }

    // Método para abrir uma janela interna apenas uma vez
    private void abrirJanelaUnica(JInternalFrame novaJanela) {
        for (JInternalFrame janela : jDesktopPane1.getAllFrames()) {
            if (janela.getClass().equals(novaJanela.getClass())) {
                try {
                    janela.setSelected(true);
                    janela.moveToFront();
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        jDesktopPane1.add(novaJanela);

        // Centraliza a janela no centro do DesktopPane
        int x = (jDesktopPane1.getWidth() - novaJanela.getWidth()) / 2;
        int y = (jDesktopPane1.getHeight() - novaJanela.getHeight()) / 2;
        novaJanela.setLocation(x, y);

        novaJanela.setVisible(true);
    }

    // Método chamado quando o login for bem-sucedido
    public void loginBemSucedido() {
        mostrarMenu();  // Quando o login for bem-sucedido, mostra os menus
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        txt_agenda = new javax.swing.JMenu();
        txtagen = new javax.swing.JMenuItem();
        txt_produtousados = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        txtcli = new javax.swing.JMenuItem();
        txtfun2 = new javax.swing.JMenuItem();
        txtfor = new javax.swing.JMenuItem();
        txtad = new javax.swing.JMenuItem();
        menuitemcaixa = new javax.swing.JMenu();
        txtcaixa = new javax.swing.JMenuItem();
        txtEstoque = new javax.swing.JMenu();
        txtest = new javax.swing.JMenuItem();
        txtrelatorio = new javax.swing.JMenu();
        txtrelatorioF = new javax.swing.JMenuItem();
        txtRelatorioA = new javax.swing.JMenuItem();
        txtrelatorioest = new javax.swing.JMenuItem();
        txt_opções = new javax.swing.JMenu();
        txt_login = new javax.swing.JMenuItem();
        txt_fechar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jDesktopPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jDesktopPane1ComponentResized(evt);
            }
        });

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1023, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 658, Short.MAX_VALUE)
        );

        txt_agenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/agenda(1).png"))); // NOI18N
        txt_agenda.setText("Agendamentos");

        txtagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/planning_9948561.png"))); // NOI18N
        txtagen.setText("Cadastro de Agendamentos");
        txtagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtagenActionPerformed(evt);
            }
        });
        txt_agenda.add(txtagen);

        txt_produtousados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/recebendo.png"))); // NOI18N
        txt_produtousados.setText("Produtos Utilizados");
        txt_produtousados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_produtousadosActionPerformed(evt);
            }
        });
        txt_agenda.add(txt_produtousados);

        jMenuBar1.add(txt_agenda);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/local-na-rede-internet.png"))); // NOI18N
        jMenu2.setText("Cadastros");

        txtcli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/cliente (1).png"))); // NOI18N
        txtcli.setText("Clientes");
        txtcli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcliActionPerformed(evt);
            }
        });
        jMenu2.add(txtcli);

        txtfun2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/funciona.png"))); // NOI18N
        txtfun2.setText("Funcionarios");
        txtfun2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtfun2ActionPerformed(evt);
            }
        });
        jMenu2.add(txtfun2);

        txtfor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/forn.png"))); // NOI18N
        txtfor.setText("Fornecedores");
        txtfor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtforActionPerformed(evt);
            }
        });
        jMenu2.add(txtfor);

        txtad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/ADM(1).png"))); // NOI18N
        txtad.setText("Administrador");
        txtad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtadActionPerformed(evt);
            }
        });
        jMenu2.add(txtad);

        jMenuBar1.add(jMenu2);

        menuitemcaixa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/caixa-registradora.png"))); // NOI18N
        menuitemcaixa.setText("Caixa");

        txtcaixa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/cash-flow_18353585.png"))); // NOI18N
        txtcaixa.setText("Movimento Caixa");
        txtcaixa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcaixaActionPerformed(evt);
            }
        });
        menuitemcaixa.add(txtcaixa);

        jMenuBar1.add(menuitemcaixa);

        txtEstoque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/estoque-pronto.png"))); // NOI18N
        txtEstoque.setText("Estoque");

        txtest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/products_1312307.png"))); // NOI18N
        txtest.setText("Itens do Estoque");
        txtest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtestActionPerformed(evt);
            }
        });
        txtEstoque.add(txtest);

        jMenuBar1.add(txtEstoque);

        txtrelatorio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/relatorio-medico(1).png"))); // NOI18N
        txtrelatorio.setText("Relatórios");

        txtrelatorioF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/dinheiro.png"))); // NOI18N
        txtrelatorioF.setText("Relatório Financeiro");
        txtrelatorioF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtrelatorioFActionPerformed(evt);
            }
        });
        txtrelatorio.add(txtrelatorioF);

        txtRelatorioA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/calendario.png"))); // NOI18N
        txtRelatorioA.setText("Relatório de Agendamentos");
        txtRelatorioA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRelatorioAActionPerformed(evt);
            }
        });
        txtrelatorio.add(txtRelatorioA);

        txtrelatorioest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/caracteristicas.png"))); // NOI18N
        txtrelatorioest.setText("Relatório de Estoque");
        txtrelatorioest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtrelatorioestActionPerformed(evt);
            }
        });
        txtrelatorio.add(txtrelatorioest);

        jMenuBar1.add(txtrelatorio);

        txt_opções.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/opcoes.png"))); // NOI18N
        txt_opções.setText("Opções");

        txt_login.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/login-de-usuario.png"))); // NOI18N
        txt_login.setText("Voltar ao Login");
        txt_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_loginActionPerformed(evt);
            }
        });
        txt_opções.add(txt_login);

        txt_fechar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagens/conecte-se.png"))); // NOI18N
        txt_fechar.setText("Sair");
        txt_fechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_fecharActionPerformed(evt);
            }
        });
        txt_opções.add(txt_fechar);

        jMenuBar1.add(txt_opções);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtforActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtforActionPerformed
        TelaFornecedor telforn = new TelaFornecedor();
        telforn.definirPermissaoUsuario(Sessao.tipoUsuario);
        abrirJanelaUnica(telforn);
    }//GEN-LAST:event_txtforActionPerformed

    private void txtagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtagenActionPerformed
        abrirJanelaUnica(new TelaAgendamentos());
    }//GEN-LAST:event_txtagenActionPerformed

    private void txtcliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcliActionPerformed
        abrirJanelaUnica(new TelaCliente());
    }//GEN-LAST:event_txtcliActionPerformed

    private void txtadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtadActionPerformed
        abrirJanelaUnica(new TelaAdministrador(false));
    }//GEN-LAST:event_txtadActionPerformed

    private void txtcaixaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcaixaActionPerformed
        abrirJanelaUnica(new TelaCaixa());
    }//GEN-LAST:event_txtcaixaActionPerformed

    private void txtestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtestActionPerformed
        TelaEstoque telesto = new TelaEstoque();
        telesto.definirPermissaoUsuario(Sessao.tipoUsuario);
        abrirJanelaUnica(telesto);
    }//GEN-LAST:event_txtestActionPerformed

    private void txtrelatorioFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtrelatorioFActionPerformed
        abrirJanelaUnica(new TelaRelatorioCaixa());
    }//GEN-LAST:event_txtrelatorioFActionPerformed

    private void txtRelatorioAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRelatorioAActionPerformed
        abrirJanelaUnica(new TelaRelatorioAge());
    }//GEN-LAST:event_txtRelatorioAActionPerformed

    private void txtrelatorioestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtrelatorioestActionPerformed
        abrirJanelaUnica(new TelaRelatorioEstoque2());
    }//GEN-LAST:event_txtrelatorioestActionPerformed

    private void txt_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_loginActionPerformed
        // Verifica se a tela de login já está aberta e visível
        for (JInternalFrame janela : jDesktopPane1.getAllFrames()) {
            if (janela instanceof TelaLogin && janela.isVisible()) {
                try {
                    janela.setSelected(true);
                    janela.moveToFront();
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
                return; // já está aberta, então só foca nela
            }
        }

        // Se não está aberta, fecha as outras janelas
        for (JInternalFrame janela : jDesktopPane1.getAllFrames()) {
            janela.dispose();
        }

        // Abre nova tela de login
        TelaLogin login = new TelaLogin(this);
        jDesktopPane1.add(login);

        // Centraliza
        int x = (jDesktopPane1.getWidth() - login.getWidth()) / 2;
        int y = (jDesktopPane1.getHeight() - login.getHeight()) / 2;
        login.setLocation(x, y);

        login.setVisible(true);

        // Oculta menus
        esconderMenu();


    }//GEN-LAST:event_txt_loginActionPerformed

    private void txt_fecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_fecharActionPerformed
        int resposta = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja sair?",
                "Sair do sistema",
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            try {
                if (conecta != null && !conecta.isClosed()) {
                    conecta.close(); // Fecha a conexão com o banco
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao fechar conexão: " + e.getMessage());
            }
            System.exit(0); // Encerra o programa
        }
    }//GEN-LAST:event_txt_fecharActionPerformed

    private void txt_produtousadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_produtousadosActionPerformed
        abrirJanelaUnica(new TelaProdutosUtilizados());
    }//GEN-LAST:event_txt_produtousadosActionPerformed

    private void jDesktopPane1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jDesktopPane1ComponentResized
    }//GEN-LAST:event_jDesktopPane1ComponentResized

    private void txtfun2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtfun2ActionPerformed
        TelaFuncionario telfun = new TelaFuncionario();
        telfun.definirPermissaoUsuario(Sessao.tipoUsuario);
        abrirJanelaUnica(telfun);
    }//GEN-LAST:event_txtfun2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static final javax.swing.JDesktopPane jDesktopPane1 = new Telas.DesktopPaneComImagem("/Imagens/fundo_Salao.jpg");
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu menuitemcaixa;
    private javax.swing.JMenu txtEstoque;
    private javax.swing.JMenuItem txtRelatorioA;
    private javax.swing.JMenu txt_agenda;
    private javax.swing.JMenuItem txt_fechar;
    private javax.swing.JMenuItem txt_login;
    private javax.swing.JMenu txt_opções;
    private javax.swing.JMenuItem txt_produtousados;
    private javax.swing.JMenuItem txtad;
    private javax.swing.JMenuItem txtagen;
    private javax.swing.JMenuItem txtcaixa;
    private javax.swing.JMenuItem txtcli;
    private javax.swing.JMenuItem txtest;
    private javax.swing.JMenuItem txtfor;
    private javax.swing.JMenuItem txtfun2;
    private javax.swing.JMenu txtrelatorio;
    private javax.swing.JMenuItem txtrelatorioF;
    private javax.swing.JMenuItem txtrelatorioest;
    // End of variables declaration//GEN-END:variables


}
