package guru.bonacci.blockchain;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import guru.bonacci.blockchain.Blockchain.Block;

@Path("/blockchain")
public class BlockResource {

	@Inject Blockchain blockchain;
	
    @GET @Path("rest")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }
    
    @GET @Path("mine_block")
    @Produces(MediaType.APPLICATION_JSON)
    public Block mineBlock() {
     	Block previousBlock = blockchain.getPreviousBlock();
    	int proof = blockchain.proofOfWork(previousBlock.getProof());
    	String previousHash = blockchain.hash(previousBlock);
    	Block block = blockchain.createBlock(proof, previousHash);
        return block;
    }
    
    @GET @Path("get_chain")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Block> getChain() {
    	return blockchain.getChain();
    }
    
    @GET @Path("is_valid")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean isValid() {
    	return blockchain.isChainValid(blockchain.getChain());
    }

}