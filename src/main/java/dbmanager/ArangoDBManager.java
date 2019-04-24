package dbmanager;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ArangoDBManager {
    private final ArangoDB arangoDB;
    static final String DB = "DP";
    static final String COLLECTION = "user_exercise_files";

    public ArangoDBManager() {
        arangoDB = new ArangoDB.Builder().password("admin").build();
    }

    public void insertDocument(BaseDocument document) {
        arangoDB.db(DB).collection(COLLECTION).insertDocument(document);
    }

    public void deleteDocument(String key) {
        arangoDB.db(DB).collection(COLLECTION).deleteDocument(key);
    }


    public void deleteExerciseDocuments(String exerciseID) {
        try {
            String query = "FOR t IN user_exercise_files FILTER t._key like @key OR t._key == @ex RETURN t";
            Map<String, Object> bindVars = new MapBuilder().put("key", "%-" + exerciseID + "-%").put("ex", exerciseID).get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(DB).query(query, bindVars, null,
                    BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                deleteDocument(aDocument.getKey());
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
        }
    }

    public BaseDocument getDocument(String key) {
        return arangoDB.db(DB).collection(COLLECTION).getDocument(key,
                BaseDocument.class);
    }

    public BaseDocument getDocument(int key) {
        return arangoDB.db(DB).collection(COLLECTION).getDocument(Integer.toString(key),
                BaseDocument.class);
    }

    public void updateAttribute(String key, String attribute, String value) {
        BaseDocument bd = getDocument(key);
        bd.updateAttribute(attribute, value);
        updateDocument(key, bd);
    }


    public void updateDocument(String key, BaseDocument doc) {
        arangoDB.db(DB).collection(COLLECTION).updateDocument(key, doc);
    }

    public String getKey(int userID, int exerciseID, int version) {
        return Integer.toString(userID) + "-" + Integer.toString(exerciseID) + "-" + Integer.toString(version);
    }


    public void createNewVersion(String oldKey, String newKey) {
        BaseDocument doc = getDocument(oldKey);
        doc.setKey(newKey);
        insertDocument(doc);
    }

    public int getUserLastVersion(int exerciseID, int userId) {
        ArrayList<Integer> keys = new ArrayList<>();
        try {
            String query = "FOR t IN user_exercise_files FILTER t._key like @key RETURN t";
            Map<String, Object> bindVars = new MapBuilder().put("key",  userId + "-" + exerciseID + "-%").get();
            ArangoCursor<BaseDocument> cursor = arangoDB.db(DB).query(query, bindVars, null, BaseDocument.class);
            cursor.forEachRemaining(doc -> {
                keys.add(Integer.parseInt(doc.getKey().split("-")[2]));
            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
        }
        return keys.size() == 0 ? 0 : Collections.max(keys);
    }



}
