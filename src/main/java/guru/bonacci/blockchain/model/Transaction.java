package guru.bonacci.blockchain.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

	@NotBlank(message="Sender may not be blank")
	private String sender;

	@NotBlank(message="Receiver may not be blank")
	private String receiver;
	
	@Min(message="Don't be stingy.. Give > 1", value=1)
	private float amount;
}
