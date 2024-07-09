package SCM;
public class FreightRate {
    private String carrier;
    private String origPort;
    private String destPort;
    private double minWeight;
    private double maxWeight;
    private ServiceLevel serviceLevel;
    private double minRate;
    private double rate;
    private String modeOfTransport;
    public FreightRate(String carrier, String origPort, String destPort, double minWeight, double maxWeight, ServiceLevel serviceLevel, double minRate, double rate, String modeOfTransport) {
        this.carrier = carrier;
        this.origPort = origPort;
        this.destPort = destPort;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.serviceLevel = serviceLevel;
        this.minRate = minRate;
        this.rate = rate;
        this.modeOfTransport = modeOfTransport;
    }

    @Override
    public String toString() {
        return "FreightRate{" +
                "carrier='" + carrier + '\'' +
                ", origPort='" + origPort + '\'' +
                ", destPort='" + destPort + '\'' +
                ", minWeight=" + minWeight +
                ", maxWeight=" + maxWeight +
                ", serviceLevel=" + serviceLevel +
                ", minRate=" + minRate +
                ", rate=" + rate +
                ", modeOfTransport='" + modeOfTransport + '\'' +
                '}';
    }
}
