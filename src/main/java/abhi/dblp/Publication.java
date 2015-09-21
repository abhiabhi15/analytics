package abhi.dblp;

import java.util.List;

/**
 * Author : abhishek
 * Created on 9/11/15.
 */
public class Publication {

    String journal;
    String title;
    List<String> co_authors;

    public List<String> getCo_authors() {
        return co_authors;
    }

    public void setCo_authors(List<String> co_authors) {
        this.co_authors = co_authors;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
