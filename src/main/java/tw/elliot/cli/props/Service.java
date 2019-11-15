package tw.elliot.cli.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "service")
@Data
public class Service {
	private List<String> restfuls;
}
