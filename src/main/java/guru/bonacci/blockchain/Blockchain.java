package guru.bonacci.blockchain;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApplicationScoped
public class Blockchain {

	private static final String MINING_DIFFICULTY = "000";
	
	@Getter private List<Block> chain = new ArrayList<>();
	private ObjectMapper objectMapper = new ObjectMapper();

	public Blockchain() {
		createBlock(1, "0");
	}

	Block createBlock(int proof, String previousHash) {
		Block block = new Block();
		block.index = chain.size() + 1;
		block.timestamp = LocalDateTime.now();
		block.proof = proof;
		block.previousHash = previousHash;
		this.chain.add(block);
		return block;
	}
	
	Block getPreviousBlock() {
		return Iterables.getLast(chain);
	}
	
	int proofOfWork(Integer previousProof) {
		int newProof = 1;
		boolean checkProof = false;
		while (!checkProof) {
			String hashOperation = Hashing.sha256()
					  .hashString(String.valueOf(newProof^2 - previousProof^2), StandardCharsets.UTF_8)
					  .toString();

			System.out.println(hashOperation);
			if (hashOperation.substring(0, MINING_DIFFICULTY.length()).equals(MINING_DIFFICULTY))
				checkProof = true;
			else 
				newProof++;
		}
		return newProof;
	}

	String hash(Block block) {
		try {
			return Hashing.sha256()
					  .hashString(objectMapper.writeValueAsString(block), StandardCharsets.UTF_8)
					  .toString();
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	boolean isChainValid(List<Block> chain) {
		for (int blockIndex = 1; blockIndex < chain.size(); blockIndex++) {
			Block previousBlock = chain.get(blockIndex - 1);
			Block block = chain.get(blockIndex);

			if (!block.previousHash.equals(hash(previousBlock))) {
				return false;
			}
			
			String hashOperation = Hashing.sha256()
					  .hashString(String.valueOf(block.proof^2 - previousBlock.proof^2), StandardCharsets.UTF_8)
					  .toString();

			if (!hashOperation.substring(0, MINING_DIFFICULTY.length()).equals(MINING_DIFFICULTY))
				return false;
		}
		return true;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class Block {
		private int index;
		private LocalDateTime timestamp;
		private int proof;
		private String previousHash;
	}
}
