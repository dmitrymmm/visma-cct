import org.p2p.solanaj.core.Account;
import solana.SolanaService;

// This is just initial example

public class App {
    public static void main(String[] args) {

        SolanaService solanaService = new SolanaService();

        Account sender = new Account();
        Account receiver = new Account("ReceiverPublicKeyHere".getBytes()); // Replace with actual receiver public key

        long lamports = 1000000000L;

        long balanceBefore = solanaService.getBalance(sender.getPublicKey().toBase58());
        System.out.println("Balance before: " + balanceBefore);

        solanaService.sendTransaction(sender, receiver, lamports);

        long balanceAfter = solanaService.getBalance(sender.getPublicKey().toBase58());
        System.out.println("Balance after: " + balanceAfter);
    }
}
