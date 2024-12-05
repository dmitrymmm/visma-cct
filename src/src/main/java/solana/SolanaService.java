package solana;

import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;

public class SolanaService {

    private RpcClient client;

    public SolanaService() {
        // Initialize the client (MAINNET, TESTNET, DEVNET)
        client = new RpcClient(Cluster.MAINNET);
    }

    // Method to get account balance using the public key
    public long getBalance(String publicKey) {
        try {
            PublicKey pubKey = new PublicKey(publicKey);
            //long balance = client.getBalance(pubKey);
            // TODO
            return 0;
        } catch (Exception e) {
            System.err.println("Error getting balance: " + e.getMessage());
            return 0;
        }
    }

    // Method to send a transaction
    public void sendTransaction(Account sender, Account receiver, long lamports) {
        try {
            // 1. Create the transaction
            Transaction transaction = new Transaction();

            // 2. Create the transfer instruction (from sender to receiver)
            TransactionInstruction transferInstruction = SystemProgram.transfer(
                    sender.getPublicKey(),   // Sender's public key
                    receiver.getPublicKey(), // Receiver's public key
                    lamports                // Amount in lamports (1 SOL = 1e9 lamports)
            );

            // 3. Add the instruction to the transaction
            transaction.addInstruction(transferInstruction);

            // 4. Sign the transaction with the sender's private key
            transaction.sign(sender); // Sign the transaction using the sender account's private key

            // 5. Send the signed transaction to the Solana network
            String signature = client.getApi().sendTransaction(transaction, sender);
            System.out.println("Transaction successful, signature: " + signature);
        } catch (Exception e) {
            System.err.println("Error sending transaction: " + e.getMessage());
        }
    }

}
