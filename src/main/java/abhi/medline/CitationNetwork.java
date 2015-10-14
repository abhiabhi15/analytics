package abhi.medline;

/**
 * Author : abhishek
 * Created on 10/12/15.
 */
public class CitationNetwork {

    private String srcAuthorID;
    private String destAuthorID;
    private String pubID;
    private String pubDate;

    public String getSrcAuthorID() {
        return srcAuthorID;
    }

    public void setSrcAuthorID(String srcAuthorID) {
        this.srcAuthorID = srcAuthorID;
    }

    public String getDestAuthorID() {
        return destAuthorID;
    }

    public void setDestAuthorID(String destAuthorID) {
        this.destAuthorID = destAuthorID;
    }

    public String getPubID() {
        return pubID;
    }

    public void setPubID(String pubID) {
        this.pubID = pubID;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }
}
