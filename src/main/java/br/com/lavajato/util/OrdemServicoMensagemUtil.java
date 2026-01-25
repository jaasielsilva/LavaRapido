package br.com.lavajato.util;

public class OrdemServicoMensagemUtil {
    public static String gerarMensagemOrdemServico(
            String numero,
            String data,
            String cliente,
            String veiculo,
            String placa,
            String servico,
            String valor,
            String status
    ) {
        return "ORDEM DE SERVIÇO Nº " + numero + "\n\n" +
                "Data: " + data + "\n\n" +
                "Cliente: " + cliente + "\n\n" +
                "Veículo: " + veiculo + "\n\n" +
                "Placa: " + placa + "\n\n" +
                "Serviço:\n\n- " + servico + "\n\n" +
                "Valor Total: R$ " + valor + "\n\n" +
                "Status: " + status + "\n\n" +
                "Agradecemos pela preferência.\n\n" +
                "RAlavarapido";
    }
}
