package guru.bonacci.blockchain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Block {

	private int index;
	private String timestamp;
	private int proof;
	private String previousHash;
	private List<Transaction> transactions;
}
