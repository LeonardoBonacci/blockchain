package guru.bonacci.blockchain;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;

import guru.bonacci.blockchain.model.Block;
import guru.bonacci.blockchain.model.Transaction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class Blockchain {

	private static final String MINING_DIFFICULTY = "000";
	
	@Getter private List<Block> chain = Lists.newArrayList();
	private List<Transaction> transactions = Lists.newArrayList();
	private ObjectMapper objectMapper = new ObjectMapper();
	@Getter private Set<URI> nodes = Sets.newHashSet();

	public Blockchain() {
		log.info("new Blockchain");
		createBlock(1, "0");
	}

	Block createBlock(int proof, String previousHash) {
		log.info("createBlock()");
		Block block = new Block(	
				chain.size() + 1,
				LocalDateTime.now().toString(),
				proof,
				previousHash,
				transactions); //clone?
		this.transactions = Lists.newArrayList();
		this.chain.add(block);
		return block;
	}
	
	Block getPreviousBlock() {
		log.info("getPreviousBlock()");
		return Iterables.getLast(chain);
	}
	
	int proofOfWork(int previousProof) {
		log.info("proofOfWork()");
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
		log.info("hash()");
		try {
			return Hashing.sha256()
					  .hashString(objectMapper.writeValueAsString(block), StandardCharsets.UTF_8)
					  .toString();
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	boolean isChainValid(List<Block> chain) {
		log.info("isChainValid()");
		for (int blockIndex = 1; blockIndex < chain.size(); blockIndex++) {
			Block previousBlock = chain.get(blockIndex - 1);
			Block block = chain.get(blockIndex);

			if (!block.getPreviousHash().equals(hash(previousBlock))) {
				return false;
			}
			
			String hashOperation = Hashing.sha256()
					  .hashString(String.valueOf(block.getProof()^2 - previousBlock.getProof()^2), StandardCharsets.UTF_8)
					  .toString();

			if (!hashOperation.substring(0, MINING_DIFFICULTY.length()).equals(MINING_DIFFICULTY))
				return false;
		}
		return true;
	}
	
	int addTransaction(Transaction tx) {
		log.info("addTransaction()");
		this.transactions.add(tx);
		return getPreviousBlock().getIndex() + 1; //last block is fixed/mined, take the next/new one.
	}
	
	void addNode(String address) {
		log.info("addNode()");
		try {
			nodes.add(new URI(address));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	boolean replaceChain() {
		log.info("replaceChain()");
		Set<URI> network = nodes;
		List<Block> longestChain = null;
		int maxLength = chain.size();

		for (URI node : network) {
			List<Block> remoteChain = getRemoteChain(node);
			if (remoteChain.size() > maxLength && isChainValid(remoteChain)) {
				maxLength = remoteChain.size();
				longestChain = remoteChain;
			}
		}

		if (longestChain != null) {
			this.chain = longestChain;
			return true;
		} else {
			return false;
		}
	}

   public List<Block> getRemoteChain(URI uri) {
	   log.info("getRemoteChain()");
	   NodeClient nodeClient = RestClientBuilder.newBuilder()
            .baseUri(uri)
            .build(NodeClient.class);
	   try {
		   return nodeClient.getChain();
	   } catch (RuntimeException e) {
		   e.printStackTrace();
		   return ImmutableList.of();
	   }   
   }
}
