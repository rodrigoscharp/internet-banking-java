package bitwise.internetBanking;

public class Main {
    public static void main(String[] args) {
        // Criando clientes
        Cliente cliente1 = new Cliente("Rodrigo Scharp", "123.456.789-00");
        Cliente cliente2 = new Cliente("Gatinha do Job", "987.654.321-00");

        // Criando contas
        Conta conta1 = new Conta(cliente1);
        Conta conta2 = new Conta(cliente2);

        // Criando banco
        Banco banco = new Banco("Banco Scharp");
        banco.adicionarConta(conta1);
        banco.adicionarConta(conta2);

        // Operações
        conta1.depositar(1000);
        conta1.sacar(200);
        conta1.transferir(conta2, 300);

        // Listar contas
        banco.listarContas();
    }
}
