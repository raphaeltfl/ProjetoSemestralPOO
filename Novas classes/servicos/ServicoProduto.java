package servicos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import entidades.Produto;

public class ServicoProduto {
	private ArrayList<Produto> produtos = new ArrayList<>();

	public boolean incluir(Produto produto) {
		if (produto == null || buscarPorCodigo(produto.getCodigo()) != null) {
			return false;
		}
		produtos.add(produto);
		return true;
	}

	// Parâmetro nulo = manter o valor atual
	public boolean alterar(Integer codigo, String nome, Double precoBase, Integer quantidadeEstoque) {
		Produto p = buscarPorCodigo(codigo);
		if (p == null) {
			return false;
		}
		if (nome != null) {
			p.setNome(nome);
		}
		if (precoBase != null) {
			p.setPrecoBase(precoBase);
		}
		if (quantidadeEstoque != null) {
			p.setQuantidadeEstoque(quantidadeEstoque);
		}
		return true;
	}

	public boolean excluir(Integer codigo) {
		Produto p = buscarPorCodigo(codigo);
		return p != null && produtos.remove(p);
	}

	public Produto buscarPorCodigo(Integer codigo) {
		return produtos.stream().filter(p -> p.getCodigo().equals(codigo)).findFirst().orElse(null);
	}

	public List<Produto> buscarPorNome(String trecho) {
		String t = trecho.toLowerCase();
		return produtos.stream().filter(p -> p.getNome().toLowerCase().contains(t)).toList();
	}

	public List<Produto> listar() {
		return new ArrayList<>(produtos);
	}

	public List<Produto> listarOrdenadoPorNome() {
		return produtos.stream().sorted(Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER))
				.toList();
	}

	public List<Produto> listarOrdenadoPorPreco() {
		return produtos.stream().sorted(Comparator.comparing(Produto::calcularPrecoFinal)).toList();
	}

	public void imprimirTabela(List<Produto> lista) {
		if (lista.isEmpty()) {
			System.out.println("Nenhum produto encontrado.");
			return;
		}
		String formato = "%-12s %-7s %-28s %13s %13s %8s  %s%n";
		System.out.printf(formato, "TIPO", "CÓDIGO", "NOME", "PREÇO BASE", "PREÇO FINAL", "ESTOQUE", "DETALHES");
		System.out.println("-".repeat(110));
		for (Produto p : lista) {
			System.out.printf(formato, p.getClass().getSimpleName(), p.getCodigo(), cortar(p.getNome(), 28),
					String.format("R$ %.2f", p.getPrecoBase()), String.format("R$ %.2f", p.calcularPrecoFinal()),
					p.getQuantidadeEstoque(), p.getDetalhes());
		}
	}

	private String cortar(String texto, int max) {
		return texto.length() <= max ? texto : texto.substring(0, max - 1) + ".";
	}
}
