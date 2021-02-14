package guru.bonacci.blockchain;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import guru.bonacci.blockchain.model.Block;
import guru.bonacci.blockchain.model.Nodes;
import guru.bonacci.blockchain.model.Transaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/blockchain")
public class BlockResource {

	private static final String NODE_ADDRESS = UUID.randomUUID().toString().replace("-", "");
	@Inject Blockchain blockchain;
	
    @GET @Path("aloha")
    @Produces(MediaType.TEXT_PLAIN)
    public String aloha() {
    	log.info("aloha");
        return "Aloha";
    }
    
    @GET @Path("mine_block")
    @Produces(MediaType.APPLICATION_JSON)
    public Block mineBlock() {
    	log.info("mine block");
     	Block previousBlock = blockchain.getPreviousBlock();
    	int proof = blockchain.proofOfWork(previousBlock.getProof());
    	String previousHash = blockchain.hash(previousBlock);
    	blockchain.addTransaction(new Transaction(NODE_ADDRESS, "Me", 10));
    	return blockchain.createBlock(proof, previousHash);
    }

    @GET @Path("get_chain")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Block> getChain() {
    	log.info("get chain");
    	return blockchain.getChain();
    }
    
    @GET @Path("is_valid")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean isValid() {
    	log.info("is valid");
    	return blockchain.isChainValid(blockchain.getChain());
    }

    @POST @Path("add_transaction")
    @Consumes(MediaType.APPLICATION_JSON)
    public String addTransaction(@Valid Transaction tx) {
    	log.info("add transaction");
        return String.format("This transaction will be added to block %d", blockchain.addTransaction(tx));
    }

    @POST @Path("connect_node")
    @Consumes(MediaType.APPLICATION_JSON)
    public Set<URI> connectNode(Nodes nodes) {
    	log.info("connect node");
    	nodes.getNodes().forEach(blockchain::addNode);
    	return blockchain.getNodes();
    }
    
    @GET @Path("replace_chain")
    @Produces(MediaType.TEXT_PLAIN)
    public String replaceChain() {
    	log.info("replace chain");
        return String.format("Chain replaced? %s", blockchain.replaceChain());
    }
}