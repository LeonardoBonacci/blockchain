package guru.bonacci.blockchain;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import guru.bonacci.blockchain.model.Block;

@Path("/blockchain")
public interface NodeClient {

	@GET @Path("get_chain")
	public List<Block> getChain();
}