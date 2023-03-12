import org.testcontainers.containers.FixedHostPortGenericContainer;

public class TradingContainer extends FixedHostPortGenericContainer<TradingContainer> {

    public TradingContainer(String dockerImageName) {
        super(dockerImageName);
    }
}