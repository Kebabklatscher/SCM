package SCM;
public class FreightRate {
    private final String carrier;
    private final String origPort;
    private final String destPort;
    private final double minWeight;
    private final double maxWeight;
    private final ServiceLevel serviceLevel;
    private final double minRate;
    private final double rate;
    private final ModeOfTransport modeOfTransport;
    private final int transportTime;

    public String getCarrier() {
        return carrier;
    }
    public String getOrigPort() {
        return origPort;
    }
    public String getDestPort() {
        return destPort;
    }
    public double getMinWeight() {
        return minWeight;
    }
    public double getMaxWeight() {
        return maxWeight;
    }
    public ServiceLevel getServiceLevel() {
        return serviceLevel;
    }
    public double getMinRate() {
        return minRate;
    }
    public double getRate() {
        return rate;
    }
    public ModeOfTransport getModeOfTransport() {
        return modeOfTransport;
    }
    public double getCost(double weight){
        //TODO: calculate GROUND transportation cost
        //AIR cost:
        //returns minRate, if weight isn't over minimum
        return Math.max(weight * rate, minRate);
    }
    public Boolean isInWeightRange(double weight){
        return weight >= minWeight && weight <= maxWeight;
    }
    public FreightRate(String carrier, String origPort, String destPort, double minWeight, double maxWeight, ServiceLevel serviceLevel, double minRate, double rate, ModeOfTransport modeOfTransport, int transportTime) {
        this.carrier = carrier;
        this.origPort = origPort;
        this.destPort = destPort;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.serviceLevel = serviceLevel;
        this.minRate = minRate;
        this.rate = rate;
        this.modeOfTransport = modeOfTransport;
        this.transportTime = transportTime;
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
                ", ModeOfTransport='" + modeOfTransport + '\'' +
                '}';
    }
}
