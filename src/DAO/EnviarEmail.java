/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import Objs.Agendamento;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.JOptionPane;

public class EnviarEmail {

    public static void enviarEmailConfirmacao(String clienteEmail, String clienteNome, String dataAgendamento, String horaAgendamento, String servico, double preco) {
       
        if (clienteEmail == null || clienteEmail.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Este cliente não possui e-mail cadastrado. Não será enviado um e-mail de confirmação.");
            return;
        }

        if (!isEmailValido(clienteEmail)) {
            JOptionPane.showMessageDialog(null, "O e-mail fornecido não é válido. Não foi possível enviar a confirmação.");
            return;
        }

        if (!temConexaoComInternet()) {
            JOptionPane.showMessageDialog(null, "Sem conexão com a internet. O e-mail não foi enviado.");
            return;
        }

        String host = "smtp.gmail.com";
        String usuario = "dasilvarodriguesvitor264@gmail.com";
        String senha = "toug ylyy wswq rfqc";  // Senha de app do Gmail

        String assunto = "Confirmação de Agendamento - " + clienteNome;

        String corpoHtml = "<html><body style='font-family:Arial,sans-serif; background-color:#f9f9f9; padding:20px;'>"
    + "<div style='max-width:600px; margin:auto; background-color:#ffffff; border-radius:8px; padding:20px; box-shadow:0 0 10px rgba(0,0,0,0.1);'>"
    + "<h2 style='color:#4CAF50; text-align:center;'>Confirmação de Agendamento</h2>"
    + "<p style='font-size:16px;'>Olá, <strong>" + clienteNome + "</strong>!</p>"
    + "<p style='font-size:15px;'>Seu agendamento foi confirmado com sucesso. Veja os detalhes abaixo:</p>"

    + "<table style='width:100%; border-collapse:collapse; margin-top:10px;'>"
    + "<tr style='background-color:#f0f0f0;'>"
    + "<td style='padding:10px; border:1px solid #ddd;'><strong>Serviço</strong></td>"
    + "<td style='padding:10px; border:1px solid #ddd;'>" + servico + "</td>"
    + "</tr>"
    + "<tr>"
    + "<td style='padding:10px; border:1px solid #ddd;'><strong>Data</strong></td>"
    + "<td style='padding:10px; border:1px solid #ddd;'>" + dataAgendamento + "</td>"
    + "</tr>"
    + "<tr>"
    + "<td style='padding:10px; border:1px solid #ddd;'><strong>Hora</strong></td>"
    + "<td style='padding:10px; border:1px solid #ddd;'>" + horaAgendamento + "</td>"
    + "</tr>"
    + "<tr>"
    + "<td style='padding:10px; border:1px solid #ddd;'><strong>Preço</strong></td>"
    + "<td style='padding:10px; border:1px solid #ddd;'>R$ " + String.format("%.2f", preco) + "</td>"
    + "</tr>"
    + "</table>"

    + "<p style='margin-top:20px; font-size:14px;'>Caso tenha qualquer dúvida ou precise reagendar, estamos à disposição.</p>"
    + "<p style='color:#888; font-size:13px; text-align:center;'>Salão Jussara Cabeleireira</p>"
    + "</div></body></html>";


        Properties propriedades = new Properties();
        propriedades.put("mail.smtp.host", host);
        propriedades.put("mail.smtp.port", "587");
        propriedades.put("mail.smtp.auth", "true");
        propriedades.put("mail.smtp.starttls.enable", "true");

        Session sessao = Session.getInstance(propriedades, new Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(usuario, senha);
            }
        });

        try {
            Message message = new MimeMessage(sessao);
            message.setFrom(new InternetAddress(usuario));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(clienteEmail));
            message.setSubject(assunto);
            message.setContent(corpoHtml, "text/html; charset=utf-8");

            Transport.send(message);
            JOptionPane.showMessageDialog(null, "E-mail de confirmação enviado com sucesso!");
        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao enviar o e-mail. Tente novamente mais tarde.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean isEmailValido(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(regex).matcher(email).matches();
    }

    private static boolean temConexaoComInternet() {
        try (Socket socket = new Socket()) {
            SocketAddress endereco = new InetSocketAddress("smtp.gmail.com", 587);
            socket.connect(endereco, 3000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static void enviarEmailConfirmacaoMultiplosServicos(String clienteEmail, String clienteNome, String dataAgendamento, List<Agendamento> agendamentosDoDia) {
    if (clienteEmail == null || clienteEmail.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "Este cliente não possui e-mail cadastrado.");
        return;
    }

    if (!isEmailValido(clienteEmail)) {
        JOptionPane.showMessageDialog(null, "O e-mail fornecido não é válido.");
        return;
    }

    if (!temConexaoComInternet()) {
        JOptionPane.showMessageDialog(null, "Sem conexão com a internet. O e-mail não foi enviado.");
        return;
    }

    final String remetente = "dasilvarodriguesvitor264@gmail.com";
    final String senha = "toug ylyy wswq rfqc"; // senha de app

    double total = 0.0;
    StringBuilder corpoHtml = new StringBuilder();

    corpoHtml.append("<html><body style='font-family:Arial,sans-serif; background-color:#f9f9f9; padding:20px;'>")
        .append("<div style='max-width:600px; margin:auto; background-color:#ffffff; border-radius:8px; padding:20px; box-shadow:0 0 10px rgba(0,0,0,0.1);'>")
        .append("<h2 style='color:#4CAF50; text-align:center;'>Confirmação de Agendamento</h2>")
        .append("<p style='font-size:16px;'>Olá, <strong>").append(clienteNome).append("</strong>!</p>")
        .append("<p style='font-size:15px;'>Estes são os seus agendamentos para o dia <strong>").append(dataAgendamento).append("</strong>:</p>")

        .append("<table style='width:100%; border-collapse:collapse; margin-top:10px;'>")
        .append("<thead>")
        .append("<tr style='background-color:#f0f0f0;'>")
        .append("<th style='padding:10px; border:1px solid #ddd;'>Hora</th>")
        .append("<th style='padding:10px; border:1px solid #ddd;'>Serviço</th>")
        .append("<th style='padding:10px; border:1px solid #ddd;'>Preço</th>")
        .append("</tr>")
        .append("</thead><tbody>");

    for (Agendamento ag : agendamentosDoDia) {
        corpoHtml.append("<tr>")
            .append("<td style='padding:8px; border:1px solid #ddd; text-align:center;'>").append(ag.getAge_hora()).append("</td>")
            .append("<td style='padding:8px; border:1px solid #ddd;'>").append(ag.getAge_servico()).append("</td>")
            .append("<td style='padding:8px; border:1px solid #ddd;'>R$ ").append(String.format("%.2f", ag.getAge_preco())).append("</td>")
            .append("</tr>");
        total += ag.getAge_preco();
    }

    corpoHtml.append("</tbody></table>")
        .append("<p style='margin-top:20px; font-size:16px;'><strong>Total a pagar: R$ ").append(String.format("%.2f", total)).append("</strong></p>")
        .append("<p style='font-size:14px;'>Caso tenha qualquer dúvida ou precise reagendar, entre em contato conosco.</p>")
        .append("<p style='color:#888; font-size:13px; text-align:center;'>Salão Jussara Cabeleireira</p>")
        .append("</div></body></html>");

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");    
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(remetente, senha);
        }
    });

    try {
        Message mensagem = new MimeMessage(session);
        mensagem.setFrom(new InternetAddress(remetente));
        mensagem.setRecipients(Message.RecipientType.TO, InternetAddress.parse(clienteEmail));
        mensagem.setSubject("Confirmação de Agendamentos - " + clienteNome);
        mensagem.setContent(corpoHtml.toString(), "text/html; charset=utf-8");

        Transport.send(mensagem);
        JOptionPane.showMessageDialog(null, "E-mail com múltiplos agendamentos enviado com sucesso!");
    } catch (MessagingException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao enviar o e-mail de confirmação múltipla.");
    }
}


    
public static void enviarConfirmacaoPorEmail(String email, String nome, String data, List<Agendamento> agendamentosDoDia) {
    if (agendamentosDoDia == null || agendamentosDoDia.isEmpty()) {
        return; // Nenhum agendamento para confirmar
    }

    if (agendamentosDoDia.size() == 1) {
        Agendamento ag = agendamentosDoDia.get(0);
        enviarEmailConfirmacao(email, nome, data, ag.getAge_hora(), ag.getAge_servico(), ag.getAge_preco());
    } else {
        enviarEmailConfirmacaoMultiplosServicos(email, nome, data, agendamentosDoDia);
    }
}
    
    
 public void enviarEmailLembrete(String emailCliente, String nomeCliente, String dataAgendamento, String horaAgendamento, String servico) {
    // Configurações do servidor SMTP (exemplo com Gmail)
    final String remetente = "dasilvarodriguesvitor264@gmail.com";
    final String senha = "toug ylyy wswq rfqc"; // Use uma senha de app se for Gmail

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(remetente, senha);
        }
    });

    try {
        Message mensagem = new MimeMessage(session);
        mensagem.setFrom(new InternetAddress(remetente));
        mensagem.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailCliente));
        mensagem.setSubject("Lembrete de Agendamento - Salão de Beleza");

        String corpoEmail = "Olá, " + nomeCliente + "!\n\n" +
            "Estamos passando para lembrar que você tem um agendamento marcado para hoje às " + horaAgendamento + ".\n\n" +
            "📅 Data: " + dataAgendamento + "\n" +
            "🕒 Hora: " + horaAgendamento + "\n" +
            "💇‍♀️ Serviço: " + servico + "\n\n" +
            "Caso não possa comparecer, entre em contato conosco com antecedência.\n\n" +
            "Até logo!\n" +
            "Equipe do Salão";

        mensagem.setText(corpoEmail);

        Transport.send(mensagem);

        System.out.println("Lembrete enviado para: " + emailCliente);
    } catch (MessagingException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao enviar lembrete por e-mail.");
    }
}
   
    
}


