package abhi.bitcoin;

/**
 * Author : abhishek
 * Created on 10/25/15.
 */
public class BitcoinUser {

    private String userId;
    private float totalUSDSent;
    private float totalUSDReceived;
    private int totalTxnsAsSender;
    private int totalTxnsAsReceiver;
    private float highestTxnUSD;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getTotalUSDSent() {
        return totalUSDSent;
    }

    public void setTotalUSDSent(float totalUSDSent) {
        this.totalUSDSent = totalUSDSent;
    }

    public float getTotalUSDReceived() {
        return totalUSDReceived;
    }

    public void setTotalUSDReceived(float totalUSDReceived) {
        this.totalUSDReceived = totalUSDReceived;
    }

    public int getTotalTxnsAsSender() {
        return totalTxnsAsSender;
    }

    public void setTotalTxnsAsSender(int totalTxnsAsSender) {
        this.totalTxnsAsSender = totalTxnsAsSender;
    }

    public int getTotalTxnsAsReceiver() {
        return totalTxnsAsReceiver;
    }

    public void setTotalTxnsAsReceiver(int totalTxnsAsReceiver) {
        this.totalTxnsAsReceiver = totalTxnsAsReceiver;
    }

    public float getHighestTxnUSD() {
        return highestTxnUSD;
    }

    public void setHighestTxnUSD(float highestTxnUSD) {
        this.highestTxnUSD = highestTxnUSD;
    }
}
