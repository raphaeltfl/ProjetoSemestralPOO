package servicos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import entidades.Cliente;
import entidades.ItemPedido;
import entidades.Pedido;
import entidades.Produto;
import entidadesEnumerada.StatusPedido;

public class ServicoPedido {
	private ArrayList<Pedido> pedidos = new ArrayList<>();
	private int proximoNumero = 1001;

	public Pedido incluir(Cliente cliente, String formaPagamento) {
		Pedido pedido = new Pedido(proximoNumero++, cliente, formaPagamento);
		pedidos.add(pedido);
		return pedido;
	}

	// Itens só mudam enquanto o pedido está PENDENTE; o estoque do produto diminui
	public boolean adicionarItem(Pedido pedido, Produto produto, int quantidade) {
		if (pedido.getStatus() != StatusPedido.PENDENTE) {
			return false;
		}
		return pedido.adicionarItem(produto, quantidade);
	}

	// O estoque do produto volta ao remover o item
	public boolean removerItem(Pedido pedido, Integer codigoProduto) {
		if (pedido.getStatus() != StatusPedido.PENDENTE) {
			return false;
		}
		return pedido.removerItem(codigoProduto);
	}

	public boolean alterarFormaPagamento(Integer numero, String formaPagamento) {
		Pedido p = buscarPorNumero(numero);
		if (p == null) {
			return false;
		}
		p.setFormaPagamento(formaPagamento);
		return true;
	}

	// Cancelar um pedido devolve ao estoque todos os itens
	public boolean alterarStatus(Integer numero, StatusPedido novoStatus) {
		Pedido p = buscarPorNumero(numero);
		if (p == null || p.getStatus() == novoStatus || !p.alterarStatus(novoStatus)) {
			return false;
		}
		if (novoStatus == StatusPedido.CANCELADO) {
			devolverEstoque(p);
		}
		return true;
	}

	public boolean excluir(Integer numero) {
		Pedido p = buscarPorNumero(numero);
		if (p == null) {
			return false;
		}
		if (p.getStatus() != StatusPedido.CANCELADO) {
			devolverEstoque(p);
		}
		return pedidos.remove(p);
	}

	private void devolverEstoque(Pedido p) {
		for (ItemPedido item : p.getItens()) {
			item.getProduto().entradaEstoque(item.getQuantidade());
		}
	}

	public Pedido buscarPorNumero(Integer numero) {
		return pedidos.stream().filter(p -> p.getNumero().equals(numero)).findFirst().orElse(null);
	}

	public List<Pedido> buscarPorCliente(String trecho) {
		String t = trecho.toLowerCase();
		return pedidos.stream().filter(p -> p.getCliente().getNome().toLowerCase().contains(t)).toList();
	}

	public boolean produtoEmPedido(Integer codigoProduto) {
		return pedidos.stream().anyMatch(
				p -> p.getItens().stream().anyMatch(i -> i.getProduto().getCodigo().equals(codigoProduto)));
	}

	public boolean clienteTemPedidos(String cpf) {
		return pedidos.stream().anyMatch(p -> p.getCliente().getCpf().equals(cpf));
	}

	public List<Pedido> listar() {
		return new ArrayList<>(pedidos);
	}

	public List<Pedido> listarOrdenadoPorNumero() {
		return pedidos.stream().sorted(Comparator.comparing(Pedido::getNumero)).toList();
	}

	public List<Pedido> listarOrdenadoPorTotal() {
		return pedidos.stream().sorted(Comparator.comparing(Pedido::calcularTotal)).toList();
	}

	public void imprimirTabela(List<Pedido> lista) {
		if (lista.isEmpty()) {
			System.out.println("Nenhum pedido encontrado.");
			return;
		}
		String formato = "%-8s %-26s %-6s %-11s %-18s %14s%n";
		System.out.printf(formato, "NÚMERO", "CLIENTE", "ITENS", "STATUS", "PAGAMENTO", "TOTAL");
		System.out.println("-".repeat(90));
		for (Pedido p : lista) {
			System.out.printf(formato, p.getNumero(), p.getCliente().getNome(), p.getItens().size(), p.getStatus(),
					p.getFormaPagamento(), String.format("R$ %.2f", p.calcularTotal()));
		}
	}

	public void imprimirDetalhe(Pedido p) {
		System.out.printf("%nPedido #%d | Cliente: %s (CPF %s)%n", p.getNumero(), p.getCliente().getNome(),
				p.getCliente().getCpf());
		System.out.printf("Status: %s | Pagamento: %s%n", p.getStatus(), p.getFormaPagamento());
		if (p.getItens().isEmpty()) {
			System.out.println("(pedido sem itens)");
		} else {
			String formato = "%-7s %-28s %5s %14s %14s%n";
			System.out.printf(formato, "CÓDIGO", "PRODUTO", "QTD", "PREÇO UNIT.", "SUBTOTAL");
			System.out.println("-".repeat(72));
			for (ItemPedido i : p.getItens()) {
				System.out.printf(formato, i.getProduto().getCodigo(), i.getProduto().getNome(), i.getQuantidade(),
						String.format("R$ %.2f", i.getPrecoUnitario()), String.format("R$ %.2f", i.getSubtotal()));
			}
		}
		System.out.printf("TOTAL: R$ %.2f%n", p.calcularTotal());
	}
}
