package ethereum;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.Arrays;

public class EthereumService {

    private Web3j web3;

    public EthereumService(String rpcEndpoint) {
        this.web3 = Web3j.build(new HttpService(rpcEndpoint));  // Connect to the Ethereum RPC endpoint
    }

    // Method to receive bridged tokens from Solana via Wormhole and handle the reception
    public void receiveBridgedTokens(String recipient, String tokenAddress, String wormholePayload) throws Exception {
        System.out.println("Processing Wormhole payload for recipient: " + recipient);

        BigInteger amount = decodeWormholePayload(wormholePayload);  // Decode the payload and get amount

        String senderAddress = "0xSenderAddress";  // Replace with the actual sender address
        String txHash = claimBridgedTokens(senderAddress, tokenAddress, amount);

        // Wait for transaction confirmation
        waitForTransactionConfirmation(txHash);

        // Log the successful reception
        System.out.println("Successfully received bridged tokens for " + recipient);
    }

    // Decode the Wormhole payload (this example assumes a simple BigInteger amount in payload)
    private BigInteger decodeWormholePayload(String payload) {
        return new BigInteger(payload);  // Replace with actual decoding logic
    }

    // Claim the bridged tokens and interact with the Ethereum contract
    public String claimBridgedTokens(String privateKey, String contractAddress, BigInteger amount) throws Exception {
        String senderAddress = getAddressFromPrivateKey(privateKey);

        // Encode the function to call on the contract to claim tokens
        String encodedFunction = encodeClaimFunction(amount);

        // Send the transaction to the Ethereum network
        return sendTransaction(senderAddress, contractAddress, encodedFunction);
    }

    // Encode the claim function (e.g., claimTokens(uint256 amount))
    private String encodeClaimFunction(BigInteger amount) {
        // Assuming the claim function is `claimTokens(uint256)`
        String methodSignature = "claimTokens(uint256)";
        Function function = new Function(
                methodSignature,
                Arrays.asList(new Uint256(amount)),
                Arrays.asList()  // No return values expected
        );

        return FunctionEncoder.encode(function);
    }

    // Send a transaction to the Ethereum network
    private String sendTransaction(String senderAddress, String contractAddress, String encodedFunction) throws Exception {
        // Load credentials from private key
        Credentials credentials = Credentials.create("your_private_key_here");  // Replace with actual private key

        // Estimate gas limit (optional, could use a default gas provider)
        DefaultGasProvider gasProvider = new DefaultGasProvider();

        // Prepare the transaction
        Transaction transaction = Transaction.createFunctionCallTransaction(
                senderAddress,
                null,
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit(),
                contractAddress,
                BigInteger.ZERO,
                encodedFunction
        );

        // Send the transaction
        EthSendTransaction ethSendTransaction = web3.ethSendTransaction(transaction).send();

        if (ethSendTransaction.hasError()) {
            throw new Exception("Error sending transaction: " + ethSendTransaction.getError().getMessage());
        }

        // Return the transaction hash
        return ethSendTransaction.getTransactionHash();
    }

    // Helper method to get the address from private key
    private String getAddressFromPrivateKey(String privateKey) {
        Credentials credentials = Credentials.create(privateKey);
        return credentials.getAddress();
    }

    // Wait for the transaction to be confirmed
    private void waitForTransactionConfirmation(String txHash) throws Exception {
        EthGetTransactionReceipt receipt = web3.ethGetTransactionReceipt(txHash).send();

        if (receipt.getTransactionReceipt().isPresent()) {
            TransactionReceipt transactionReceipt = receipt.getTransactionReceipt().get();
            if ("0x1".equals(transactionReceipt.getStatus())) {
                System.out.println("Transaction confirmed: " + txHash);
            } else {
                System.out.println("Transaction failed: " + txHash);
            }
        } else {
            System.out.println("Transaction not mined yet, retrying...");
            Thread.sleep(10000);  // Wait and retry
            waitForTransactionConfirmation(txHash);
        }
    }
}
