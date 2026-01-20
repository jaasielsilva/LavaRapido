package br.com.lavajato.service.produto;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.produto.CategoriaProduto;
import br.com.lavajato.model.produto.Produto;
import br.com.lavajato.model.produto.UnidadeMedida;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.produto.ProdutoRepository;
import br.com.lavajato.service.usuario.UsuarioService;
import br.com.lavajato.service.financeiro.FinanceiroService;
import br.com.lavajato.model.financeiro.LancamentoFinanceiro;
import br.com.lavajato.model.financeiro.TipoLancamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FinanceiroService financeiroService;

    public List<Produto> listarAtivos() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return repository.findAllByEmpresaAndAtivoTrue(usuario.getEmpresa());
    }

    public Produto salvar(Produto produto) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (produto.getId() == null) {
            produto.setEmpresa(usuario.getEmpresa());
            Produto salvo = repository.save(produto);
            
            // Lançar Despesa de Estoque Inicial no Financeiro
            if (salvo.getEstoque() != null && salvo.getEstoque() > 0 && 
                salvo.getPrecoCusto() != null && salvo.getPrecoCusto().compareTo(BigDecimal.ZERO) > 0) {
                
                try {
                    BigDecimal custoTotal = salvo.getPrecoCusto().multiply(new BigDecimal(salvo.getEstoque()));
                    
                    LancamentoFinanceiro lancamento = new LancamentoFinanceiro();
                    lancamento.setTipo(TipoLancamento.SAIDA);
                    lancamento.setCategoria("Compra de Estoque");
                    lancamento.setDescricao("Estoque Inicial: " + salvo.getNome());
                    lancamento.setValor(custoTotal);
                    lancamento.setData(java.time.LocalDateTime.now());
                    
                    financeiroService.salvarLancamento(lancamento, usuario.getEmpresa());
                } catch (Exception e) {
                    System.err.println("Erro ao lançar despesa de estoque: " + e.getMessage());
                    // Não impede o cadastro do produto
                }
            }
            return salvo;
        } else {
            Produto existente = repository.findById(produto.getId())
                    .orElseThrow(() -> new IllegalStateException("Produto não encontrado"));
            if (!existente.getEmpresa().equals(usuario.getEmpresa())) {
                throw new IllegalStateException("Usuário não pode editar produto de outra empresa");
            }
            existente.setNome(produto.getNome());
            existente.setDescricao(produto.getDescricao());
            existente.setPrecoVenda(produto.getPrecoVenda());
            existente.setPrecoCusto(produto.getPrecoCusto());
            existente.setEstoque(produto.getEstoque());
            existente.setEstoqueMinimo(produto.getEstoqueMinimo());
            existente.setCategoria(produto.getCategoria());
            existente.setUnidade(produto.getUnidade());
            return repository.save(existente);
        }
    }

    public Optional<Produto> buscarPorId(Long id) {
        return repository.findById(id);
    }
    
    // Métricas para Dashboard
    public long contarProdutos() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return repository.countByEmpresaAndAtivoTrue(usuario.getEmpresa());
    }
    
    public long contarEstoqueBaixo() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return repository.countEstoqueBaixo(usuario.getEmpresa());
    }
    
    public BigDecimal calcularValorEstoque() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return repository.sumValorEstoque(usuario.getEmpresa());
    }

    public void inicializarProdutosPadrao(Empresa empresa) {
        if (repository.countByEmpresaAndAtivoTrue(empresa) == 0) {
            criarProduto(empresa, "Cera Cristalizadora 500ml", "Cera premium para brilho intenso", new BigDecimal("89.90"), 15, CategoriaProduto.CERA);
            criarProduto(empresa, "Shampoo Automotivo 1L", "Shampoo neutro concentrado", new BigDecimal("35.90"), 25, CategoriaProduto.SHAMPOO);
            criarProduto(empresa, "Silicone Gel 400g", "Silicone para painel e pneus", new BigDecimal("28.90"), 30, CategoriaProduto.SILICONE);
            criarProduto(empresa, "Aromatizante Carro Novo", "Fragrância duradoura", new BigDecimal("19.90"), 40, CategoriaProduto.AROMATIZANTE);
            criarProduto(empresa, "Flanela Microfibra", "Flanela alta absorção 40x60cm", new BigDecimal("15.90"), 50, CategoriaProduto.ACESSORIO);
            criarProduto(empresa, "Pretinho para Pneus 500ml", "Revitalizador de pneus", new BigDecimal("24.90"), 20, CategoriaProduto.OUTROS);
            criarProduto(empresa, "Limpa Vidros 500ml", "Limpa e protege vidros", new BigDecimal("18.90"), 35, CategoriaProduto.OUTROS);
            criarProduto(empresa, "Cera Spray Rápida", "Cera em spray para retoque", new BigDecimal("45.90"), 12, CategoriaProduto.CERA);
        }
    }

    private void criarProduto(Empresa empresa, String nome, String descricao, BigDecimal preco, Integer estoque, CategoriaProduto categoria) {
        Produto p = new Produto();
        p.setEmpresa(empresa);
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setPrecoVenda(preco);
        p.setEstoque(estoque);
        p.setCategoria(categoria);
        p.setUnidade(UnidadeMedida.UNIDADE); // Padrão
        p.setEstoqueMinimo(5);
        p.setPrecoCusto(preco.multiply(new BigDecimal("0.6"))); // Simula 60% do valor de venda
        p.setAtivo(true);
        repository.save(p);
    }
}
