package abhi.dblp.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author : abhishek
 * Created on 3/5/16.
 */
public class DblpAuthor {

    String userId;
    Integer id;
    Integer numArticles;
    Map<Field, Integer> fieldMap;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumArticles() {
        return numArticles;
    }

    public void setNumArticles(Integer numArticles) {
        this.numArticles = numArticles;
    }

    public Map<Field, Integer> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(Map<Field, Integer> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public void updateJournalMap( List<Field> fieldList) {
        if (fieldMap == null){
            fieldMap = new HashMap<>();
            for( Field field : fieldList){
                fieldMap.put(field, 1);
            }
        }else{
            for( Field field : fieldList){
                if (fieldMap.containsKey(field)){
                    fieldMap.put(field, fieldMap.get(field)+1);
                }else{
                    fieldMap.put(field, 1);
                }
            }
        }
    }
}
