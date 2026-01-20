package br.com.lavajato.service.financeiro;

import br.com.lavajato.dto.BalancoMensalDTO;
import br.com.lavajato.dto.MovimentacaoDTO;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.financeiro.LancamentoFinanceiro;
import br.com.lavajato.model.financeiro.TipoLancamento;
import br.com.lavajato.model.servico.ServicoAvulso;
import br.com.lavajato.model.servico.StatusServico;
import br.com.lavajato.model.venda.ItemVenda;
import br.com.lavajato.model.venda.Venda;
import br.com.lavajato.repository.financeiro.LancamentoFinanceiroRepository;
import br.com.lavajato.repository.servico.ServicoAvulsoRepository;
import br.com.lavajato.repository.venda.VendaRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinanceiroService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private LancamentoFinanceiroRepository lancamentoRepository;

    @Autowired
    private ServicoAvulsoRepository servicoAvulsoRepository;

    public Map<String, Object> calcularResumoFinanceiro(Empresa empresa, LocalDate inicio, LocalDate fim) {
        LocalDateTime dataInicio = inicio.atStartOfDay();
        LocalDateTime dataFim = fim.atTime(LocalTime.MAX);

        // Buscar dados brutos
        List<Venda> vendas = vendaRepository.findByEmpresaAndDataVendaBetween(empresa, dataInicio, dataFim);
        List<ServicoAvulso> servicos = servicoAvulsoRepository.findByEmpresaAndStatusAndDataConclusaoBetween(empresa, StatusServico.CONCLUIDO, dataInicio, dataFim);
        List<LancamentoFinanceiro> lancamentos = lancamentoRepository.findByEmpresaAndDataBetween(empresa, dataInicio, dataFim);

        // 1. Receita Total
        BigDecimal receitaVendas = vendas.stream().map(Venda::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal receitaServicos = servicos.stream().map(ServicoAvulso::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal receitasExtras = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.ENTRADA)
                .map(LancamentoFinanceiro::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal receitaTotal = receitaVendas.add(receitaServicos).add(receitasExtras);

        // 2. Custos e Despesas
        BigDecimal custoProdutos = vendas.stream()
                .flatMap(v -> v.getItens().stream())
                .map(item -> {
                    BigDecimal custoUnitario = item.getProduto().getPrecoCusto() != null ? item.getProduto().getPrecoCusto() : BigDecimal.ZERO;
                    return custoUnitario.multiply(new BigDecimal(item.getQuantidade()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal despesasOperacionais = lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.SAIDA)
                .map(LancamentoFinanceiro::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Lucro Líquido
        BigDecimal lucroLiquido = receitaTotal.subtract(custoProdutos).subtract(despesasOperacionais);
        BigDecimal margemLucro = receitaTotal.compareTo(BigDecimal.ZERO) > 0 
                ? lucroLiquido.divide(receitaTotal, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)) 
                : BigDecimal.ZERO;

        // 4. Counts
        long qtdServicos = servicos.size();
        long qtdProdutos = vendas.stream().mapToLong(v -> v.getItens().stream().mapToInt(ItemVenda::getQuantidade).sum()).sum();

        Map<String, Object> resumo = new HashMap<>();
        resumo.put("receitaTotal", receitaTotal);
        resumo.put("receitaServicos", receitaServicos); // Apenas serviços para o card azul
        resumo.put("receitaProdutos", receitaVendas); // Apenas produtos para o card roxo
        resumo.put("lucroLiquido", lucroLiquido);
        resumo.put("margemLucro", margemLucro);
        resumo.put("qtdServicos", qtdServicos);
        resumo.put("qtdProdutos", qtdProdutos);
        
        // Média Mensal (Simples: divide pelo número de meses no intervalo)
        long meses = java.time.temporal.ChronoUnit.MONTHS.between(YearMonth.from(inicio), YearMonth.from(fim)) + 1;
        resumo.put("mediaMensal", receitaTotal.divide(new BigDecimal(Math.max(1, meses)), 2, RoundingMode.HALF_UP));

        return resumo;
    }

    public List<BalancoMensalDTO> gerarBalancoMensal(Empresa empresa, LocalDate inicio, LocalDate fim) {
        // Gera lista de meses
        List<BalancoMensalDTO> balanco = new ArrayList<>();
        YearMonth current = YearMonth.from(inicio);
        YearMonth end = YearMonth.from(fim);

        while (!current.isAfter(end)) {
            LocalDateTime mesInicio = current.atDay(1).atStartOfDay();
            LocalDateTime mesFim = current.atEndOfMonth().atTime(LocalTime.MAX);

            // Fetch dados do mês
            List<Venda> vendasMes = vendaRepository.findByEmpresaAndDataVendaBetween(empresa, mesInicio, mesFim);
            List<ServicoAvulso> servicosMes = servicoAvulsoRepository.findByEmpresaAndStatusAndDataConclusaoBetween(empresa, StatusServico.CONCLUIDO, mesInicio, mesFim);
            List<LancamentoFinanceiro> lancamentosMes = lancamentoRepository.findByEmpresaAndDataBetween(empresa, mesInicio, mesFim);

            // Cálculos
            BigDecimal recServicos = servicosMes.stream().map(ServicoAvulso::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal recProdutos = vendasMes.stream().map(Venda::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal recExtras = lancamentosMes.stream()
                    .filter(l -> l.getTipo() == TipoLancamento.ENTRADA)
                    .map(LancamentoFinanceiro::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal recTotal = recServicos.add(recProdutos).add(recExtras);

            BigDecimal custoProd = vendasMes.stream()
                .flatMap(v -> v.getItens().stream())
                .map(item -> {
                    BigDecimal custoUnitario = item.getProduto().getPrecoCusto() != null ? item.getProduto().getPrecoCusto() : BigDecimal.ZERO;
                    return custoUnitario.multiply(new BigDecimal(item.getQuantidade()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal despOp = lancamentosMes.stream()
                    .filter(l -> l.getTipo() == TipoLancamento.SAIDA)
                    .map(LancamentoFinanceiro::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal lucro = recTotal.subtract(custoProd).subtract(despOp);

            balanco.add(BalancoMensalDTO.builder()
                    .mes(current)
                    .receitaServicos(recServicos)
                    .receitaProdutos(recProdutos)
                    .receitaTotal(recTotal)
                    .custosProdutos(custoProd)
                    .despesasOperacionais(despOp)
                    .custosTotal(custoProd.add(despOp))
                    .lucroLiquido(lucro)
                    .build());

            current = current.plusMonths(1);
        }

        // Ordenar descrescente (mais recente primeiro)
        balanco.sort(Comparator.comparing(BalancoMensalDTO::getMes).reversed());
        return balanco;
    }


    public List<MovimentacaoDTO> buscarMovimentacoes(Empresa empresa, LocalDate inicio, LocalDate fim, String tipoFiltro) {
        LocalDateTime dataInicio = inicio.atStartOfDay();
        LocalDateTime dataFim = fim.atTime(LocalTime.MAX);

        List<MovimentacaoDTO> movimentacoes = new ArrayList<>();

        // 1. Buscar Vendas (Apenas se o filtro não for SAIDA)
        if (tipoFiltro == null || tipoFiltro.equals("TODOS") || tipoFiltro.equals("ENTRADA")) {
            List<Venda> vendas = vendaRepository.findByEmpresaAndDataVendaBetween(empresa, dataInicio, dataFim);
            for (Venda venda : vendas) {
                movimentacoes.add(MovimentacaoDTO.builder()
                        .id("V-" + venda.getId())
                        .data(venda.getDataVenda())
                        .descricao("Venda #" + venda.getId() + (venda.getCliente() != null ? " - " + venda.getCliente().getNome() : ""))
                        .valor(venda.getValorTotal())
                        .tipo(TipoLancamento.ENTRADA)
                        .categoria("Venda")
                        .origem("VENDA")
                        .build());
            }
        }

        // 2. Buscar Lançamentos Manuais
        // Obs: O repositório busca por empresa
        List<LancamentoFinanceiro> lancamentos = lancamentoRepository.findByEmpresaAndDataBetween(empresa, dataInicio, dataFim);
        
        for (LancamentoFinanceiro lanc : lancamentos) {
            // Filtrar pelo tipo se necessário
            if (tipoFiltro != null && !tipoFiltro.equals("TODOS")) {
                if (!lanc.getTipo().name().equals(tipoFiltro)) {
                    continue;
                }
            }

            movimentacoes.add(MovimentacaoDTO.builder()
                    .id("L-" + lanc.getId())
                    .data(lanc.getData())
                    .descricao(lanc.getDescricao())
                    .valor(lanc.getValor())
                    .tipo(lanc.getTipo())
                    .categoria(lanc.getCategoria())
                    .origem("MANUAL")
                    .build());
        }

        // 3. Ordenar por Data Decrescente
        return movimentacoes.stream()
                .sorted(Comparator.comparing(MovimentacaoDTO::getData).reversed())
                .collect(Collectors.toList());
    }

    public void salvarLancamento(LancamentoFinanceiro lancamento, Empresa empresa) {
        lancamento.setEmpresa(empresa);
        if (lancamento.getData() == null) {
            lancamento.setData(LocalDateTime.now());
        }
        lancamentoRepository.save(lancamento);
    }

    public void exportarExcel(List<MovimentacaoDTO> movimentacoes, HttpServletResponse response) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatório Financeiro");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));

            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("R$ #,##0.00"));

            // Cabeçalho
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Data", "Descrição", "Categoria", "Tipo", "Valor", "Origem"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Dados
            int rowNum = 1;
            for (MovimentacaoDTO mov : movimentacoes) {
                Row row = sheet.createRow(rowNum++);

                Cell cellData = row.createCell(0);
                cellData.setCellValue(mov.getData());
                cellData.setCellStyle(dateStyle);

                row.createCell(1).setCellValue(mov.getDescricao());
                row.createCell(2).setCellValue(mov.getCategoria());
                row.createCell(3).setCellValue(mov.getTipo().toString());

                Cell cellValor = row.createCell(4);
                cellValor.setCellValue(mov.getValor().doubleValue());
                cellValor.setCellStyle(currencyStyle);

                row.createCell(5).setCellValue(mov.getOrigem());
            }

            // Autosize columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Configurar Response
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "Relatorio_Financeiro_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            workbook.write(response.getOutputStream());
        }
    }
}
