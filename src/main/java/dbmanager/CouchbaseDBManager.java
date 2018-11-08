package dbmanager;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

public class CouchbaseDBManager {

    private Bucket getBucket() {
        Cluster cluster = CouchbaseCluster.create("localhost");
        cluster.authenticate("admin", "admin1");
        return cluster.openBucket("user_exercise_files");
//        ClusterManager clusterManager = cluster.clusterManager("admin", "admin1");
//        return clusterManager.getBucket("user_exercise_files");
    }

    public JsonDocument walter() {
        Bucket bucket = getBucket();
        return bucket.get("walter");
    }

    public static void main(String[] args) {
//        Cluster cluster = CouchbaseCluster.create("localhost");
//        ClusterManager clusterManager = cluster.clusterManager("admin", "admin1");
//        BucketSettings bucketSettings = new DefaultBucketSettings.Builder().type(BucketType.COUCHBASE).name("user_exercise_files").build();
//
//        clusterManager.insertBucket(bucketSettings);
        CouchbaseDBManager db = new CouchbaseDBManager();
        Bucket bucket = db.getBucket();
        JsonObject user = JsonObject.empty()
                .put("firstname", "Walter")
                .put("lastname", "White")
                .put("job", "chemistry teacher")
                .put("age", 50);

        JsonDocument doc = JsonDocument.create("walter", user);
        JsonDocument response = bucket.upsert(doc);
        JsonDocument walter = bucket.get("walter");
    }
}
