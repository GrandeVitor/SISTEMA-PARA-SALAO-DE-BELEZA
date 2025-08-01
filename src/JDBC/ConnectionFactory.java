/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Aluno
 */
public class ConnectionFactory {
    private Connection conecta;
    
    public Connection conecta() {
        try {
            this.conecta = (Connection)
          DriverManager.getConnection("jdbc:mysql://localhost/bd_jussaracabeleireira","root","sql1");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao acessar ao Banco de Dados "+e.getMessage());
        }
        return conecta;
    }
}
