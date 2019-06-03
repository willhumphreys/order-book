package codingtask.orderbook;

public class Instrument {

    private long instrumentId;

    private String name;

    public Instrument(long instrumentId, String name) {
        this.instrumentId = instrumentId;
        this.name = name;
    }

    public long getInstrumentId() {
        return instrumentId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "instrumentId=" + instrumentId +
                ", name='" + name + '\'' +
                '}';
    }
}
