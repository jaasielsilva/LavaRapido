package br.com.lavajato.service.financeiro;

import br.com.lavajato.dto.BalancoMensalDTO;
import br.com.lavajato.dto.MovimentacaoDTO;
import br.com.lavajato.model.agendamento.Agendamento;
import br.com.lavajato.model.agendamento.StatusAgendamento;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.financeiro.LancamentoFinanceiro;
import br.com.lavajato.model.financeiro.TipoLancamento;
import br.com.lavajato.model.servico.ServicoAvulso;
import br.com.lavajato.model.servico.StatusServico;
import br.com.lavajato.model.venda.ItemVenda;
import br.com.lavajato.model.venda.Venda;
import br.com.lavajato.repository.agendamento.AgendamentoRepository;
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

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    public Map<String, Object> calcularResumoFinanceiro(Empresa empresa, LocalDate inicio, LocalDate fim, String tipoFiltro) {
        LocalDateTime dataInicio = inicio.atStartOfDay();
        LocalDateTime dataFim = fim.atTime(LocalTime.MAX);

        boolean considerarEntradas = tipoFiltro == null || tipoFiltro.equals("TODOS") || tipoFiltro.equals("ENTRADA");
        boolean considerarSaidas = tipoFiltro == null || tipoFiltro.equals("TODOS") || tipoFiltro.equals("SAIDA");

        // Buscar dados brutos
        List<Venda> vendas = considerarEntradas ? vendaRepository.findByEmpresaAndDataVendaBetween(empresa, dataInicio, dataFim) : Collections.emptyList();
        List<ServicoAvulso> servicos = considerarEntradas ? servicoAvulsoRepository.findByEmpresaAndStatusAndDataConclusaoBetween(empresa, StatusServico.CONCLUIDO, dataInicio, dataFim) : Collections.emptyList();
        List<Agendamento> agendamentos = considerarEntradas ? agendamentoRepository.findByEmpresaAndStatusAndDataBetween(empresa, StatusAgendamento.CONCLUIDO, dataInicio, dataFim) : Collections.emptyList();
        List<LancamentoFinanceiro> lancamentos = lancamentoRepository.findByEmpresaAndDataBetween(empresa, dataInicio, dataFim);

        // 1. Receita Total
        BigDecimal receitaVendas = vendas.stream().map(v -> v.getValorTotal() != null ? v.getValorTotal() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal receitaServicos = servicos.stream().map(s -> s.getValor() != null ? s.getValor() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal receitaAgendamentos = agendamentos.stream().map(a -> a.getValor() != null ? a.getValor() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal receitasExtras = considerarEntradas ? lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.ENTRADA)
                .map(l -> l.getValor() != null ? l.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
        
        BigDecimal receitaTotal = receitaVendas.add(receitaServicos).add(receitaAgendamentos).add(receitasExtras);

        // 2. Custos e Despesas
        BigDecimal custoProdutos = considerarSaidas ? vendas.stream()
                .flatMap(v -> v.getItens().stream())
                .map(item -> {
                    BigDecimal custoUnitario = item.getProduto().getPrecoCusto() != null ? item.getProduto().getPrecoCusto() : BigDecimal.ZERO;
                    return custoUnitario.multiply(new BigDecimal(item.getQuantidade()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;

        BigDecimal despesasOperacionais = considerarSaidas ? lancamentos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.SAIDA)
                .map(l -> l.getValor() != null ? l.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;

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
        resumo.put("receitaServicos", receitaServicos.add(receitaAgendamentos)); // Unifica serviços avulsos + agendamentos
        resumo.put("receitaAgendamentos", receitaAgendamentos); // Mantém separado caso precise detalhar depois
        resumo.put("receitaProdutos", receitaVendas);
        resumo.put("lucroLiquido", lucroLiquido);
        resumo.put("margemLucro", margemLucro);
        resumo.put("qtdServicos", qtdServicos + agendamentos.size()); // Soma quantidades
        resumo.put("qtdProdutos", qtdProdutos);
        
        // Média Mensal (Simples: divide pelo número de meses no intervalo)
        long meses = java.time.temporal.ChronoUnit.MONTHS.between(YearMonth.from(inicio), YearMonth.from(fim)) + 1;
        resumo.put("mediaMensal", receitaTotal.divide(new BigDecimal(Math.max(1, meses)), 2, RoundingMode.HALF_UP));

        return resumo;
    }

    public List<BalancoMensalDTO> gerarBalancoMensal(Empresa empresa, LocalDate inicio, LocalDate fim, String tipoFiltro) {
        // Gera lista de meses
        List<BalancoMensalDTO> balanco = new ArrayList<>();
        YearMonth current = YearMonth.from(inicio);
        YearMonth end = YearMonth.from(fim);

        boolean considerarEntradas = tipoFiltro == null || tipoFiltro.equals("TODOS") || tipoFiltro.equals("ENTRADA");
        boolean considerarSaidas = tipoFiltro == null || tipoFiltro.equals("TODOS") || tipoFiltro.equals("SAIDA");

        while (!current.isAfter(end)) {
            LocalDateTime mesInicio = current.atDay(1).atStartOfDay();
            LocalDateTime mesFim = current.atEndOfMonth().atTime(LocalTime.MAX);

            // Fetch dados do mês
            List<Venda> vendasMes = considerarEntradas ? vendaRepository.findByEmpresaAndDataVendaBetween(empresa, mesInicio, mesFim) : Collections.emptyList();
            List<ServicoAvulso> servicosMes = considerarEntradas ? servicoAvulsoRepository.findByEmpresaAndStatusAndDataConclusaoBetween(empresa, StatusServico.CONCLUIDO, mesInicio, mesFim) : Collections.emptyList();
            List<Agendamento> agendamentosMes = considerarEntradas ? agendamentoRepository.findByEmpresaAndStatusAndDataBetween(empresa, StatusAgendamento.CONCLUIDO, mesInicio, mesFim) : Collections.emptyList();
            List<LancamentoFinanceiro> lancamentosMes = lancamentoRepository.findByEmpresaAndDataBetween(empresa, mesInicio, mesFim);

            // Cálculos
            BigDecimal recServicos = servicosMes.stream().map(s -> s.getValor() != null ? s.getValor() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal recAgendamentos = agendamentosMes.stream().map(a -> a.getValor() != null ? a.getValor() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal recProdutos = vendasMes.stream().map(v -> v.getValorTotal() != null ? v.getValorTotal() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal recExtras = considerarEntradas ? lancamentosMes.stream()
                    .filter(l -> l.getTipo() == TipoLancamento.ENTRADA)
                    .map(l -> l.getValor() != null ? l.getValor() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
            
            BigDecimal recTotal = recServicos.add(recAgendamentos).add(recProdutos).add(recExtras);

            BigDecimal custoProd = considerarSaidas ? vendasMes.stream()
                .flatMap(v -> v.getItens().stream())
                .map(item -> {
                    BigDecimal custoUnitario = item.getProduto().getPrecoCusto() != null ? item.getProduto().getPrecoCusto() : BigDecimal.ZERO;
                    return custoUnitario.multiply(new BigDecimal(item.getQuantidade()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;

            BigDecimal despOp = considerarSaidas ? lancamentosMes.stream()
                    .filter(l -> l.getTipo() == TipoLancamento.SAIDA)
                    .map(l -> l.getValor() != null ? l.getValor() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;

            BigDecimal lucro = recTotal.subtract(custoProd).subtract(despOp);
            BigDecimal margem = recTotal.compareTo(BigDecimal.ZERO) > 0 
                    ? lucro.divide(recTotal, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)) 
                    : BigDecimal.ZERO;

            balanco.add(BalancoMensalDTO.builder()
                    .mes(current)
                    .receitaServicos(recServicos.add(recAgendamentos)) // Unifica no balanço também
                    .receitaAgendamentos(recAgendamentos)
                    .receitaProdutos(recProdutos)
                    .receitaTotal(recTotal)
                    .custosProdutos(custoProd)
                    .despesasOperacionais(despOp)
                    .custosTotal(custoProd.add(despOp))
                    .lucroLiquido(lucro)
                    .margemLucro(margem)
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

            // 1.1 Buscar Agendamentos (Apenas se o filtro não for SAIDA)
            List<Agendamento> agendamentos = agendamentoRepository.findByEmpresaAndStatusAndDataBetween(empresa, StatusAgendamento.CONCLUIDO, dataInicio, dataFim);
            for (Agendamento ag : agendamentos) {
                movimentacoes.add(MovimentacaoDTO.builder()
                        .id("A-" + ag.getId())
                        .data(ag.getData())
                        .descricao("Agendamento #" + ag.getId() + " - " + ag.getVeiculo().getPlaca())
                        .valor(ag.getValor())
                        .tipo(TipoLancamento.ENTRADA)
                        .categoria("Agendamento")
                        .origem("AGENDAMENTO")
                        .build());
            }

            // 1.2 Buscar Serviços Avulsos (Apenas se o filtro não for SAIDA)
            List<ServicoAvulso> servicos = servicoAvulsoRepository.findByEmpresaAndStatusAndDataConclusaoBetween(empresa, StatusServico.CONCLUIDO, dataInicio, dataFim);
            for (ServicoAvulso sv : servicos) {
                movimentacoes.add(MovimentacaoDTO.builder()
                        .id("S-" + sv.getId())
                        .data(sv.getDataConclusao())
                        .descricao("Serviço Avulso #" + sv.getId() + " - " + (sv.getClienteAvulsoVeiculo() != null ? sv.getClienteAvulsoVeiculo() : "Veículo N/D"))
                        .valor(sv.getValor())
                        .tipo(TipoLancamento.ENTRADA)
                        .categoria("Serviço Avulso")
                        .origem("SERVICO")
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

    public void exportarExcel(List<MovimentacaoDTO> movimentacoes, List<BalancoMensalDTO> balancoMensal, HttpServletResponse response) throws IOException {
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
                // Se for SAIDA, torna o valor negativo para facilitar a soma no Excel
                double valor = mov.getValor().doubleValue();
                if (mov.getTipo() == TipoLancamento.SAIDA) {
                    valor = -valor;
                }
                cellValor.setCellValue(valor);
                cellValor.setCellStyle(currencyStyle);

                row.createCell(5).setCellValue(mov.getOrigem());
            }

            // Autosize columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Segunda aba: Balanço Mensal
            Sheet sheetBalanco = workbook.createSheet("Balanço Mensal");
            Row headerBalanco = sheetBalanco.createRow(0);
            String[] colsBalanco = {"Mês", "Serviços (+)", "Produtos (+)", "Receita Bruta", "Custos (-)", "Despesas (-)", "Lucro Líquido", "Margem"};
            for (int i = 0; i < colsBalanco.length; i++) {
                Cell cell = headerBalanco.createCell(i);
                cell.setCellValue(colsBalanco[i]);
                cell.setCellStyle(headerStyle);
            }

            java.time.format.DateTimeFormatter mesFmt = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", new java.util.Locale("pt", "BR"));
            int rowB = 1;
            for (BalancoMensalDTO b : balancoMensal) {
                Row row = sheetBalanco.createRow(rowB++);
                // Mês
                row.createCell(0).setCellValue(b.getMes().format(mesFmt));
                // Serviços (+): receitaServicos já inclui agendamentos conforme regra atual
                Cell cServ = row.createCell(1);
                cServ.setCellValue(b.getReceitaServicos() != null ? b.getReceitaServicos().doubleValue() : 0);
                cServ.setCellStyle(currencyStyle);
                // Produtos (+)
                Cell cProd = row.createCell(2);
                cProd.setCellValue(b.getReceitaProdutos() != null ? b.getReceitaProdutos().doubleValue() : 0);
                cProd.setCellStyle(currencyStyle);
                // Receita Bruta
                Cell cRec = row.createCell(3);
                cRec.setCellValue(b.getReceitaTotal() != null ? b.getReceitaTotal().doubleValue() : 0);
                cRec.setCellStyle(currencyStyle);
                // Custos (-) = custosProdutos
                Cell cCusto = row.createCell(4);
                cCusto.setCellValue(b.getCustosProdutos() != null ? b.getCustosProdutos().doubleValue() : 0);
                cCusto.setCellStyle(currencyStyle);
                // Despesas (-)
                Cell cDesp = row.createCell(5);
                cDesp.setCellValue(b.getDespesasOperacionais() != null ? b.getDespesasOperacionais().doubleValue() : 0);
                cDesp.setCellStyle(currencyStyle);
                // Lucro Líquido
                Cell cLucro = row.createCell(6);
                cLucro.setCellValue(b.getLucroLiquido() != null ? b.getLucroLiquido().doubleValue() : 0);
                cLucro.setCellStyle(currencyStyle);
                // Margem (percentual)
                Cell cMargem = row.createCell(7);
                double margemPercent = b.getMargemLucro() != null ? b.getMargemLucro().doubleValue() : 0;
                cMargem.setCellValue(margemPercent / 100.0); // Excel percent
                CellStyle percentStyle = workbook.createCellStyle();
                percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
                cMargem.setCellStyle(percentStyle);
            }
            for (int i = 0; i < colsBalanco.length; i++) {
                sheetBalanco.autoSizeColumn(i);
            }

            // Configurar Response
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "Relatorio_Financeiro_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            workbook.write(response.getOutputStream());
        }
    }
}
