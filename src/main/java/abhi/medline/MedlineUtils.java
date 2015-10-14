package abhi.medline;

/**
 * Author : abhishek
 * Created on 10/11/15.
 */
public class MedlineUtils {

    public static String getAuthorID(MedlineCitation.Author author) {

        String name = author.getLastName().replaceAll("[^a-zA-Z]+", "");
        if(author.getForeName() != null){
            name = name + "_" + author.getForeName().replaceAll("[^a-zA-Z]+", "");
        }
        return name.toLowerCase();
    }

    public static String getAuthorName(MedlineCitation.Author author) {
        return author.getForeName() + " " + author.getLastName();
    }

    public static String formatMesh(String heading) {
        return heading.replaceAll("[^a-zA-Z]+", "").toLowerCase();
    }
}
