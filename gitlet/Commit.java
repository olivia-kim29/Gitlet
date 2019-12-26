package gitlet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Olivia Kim
 */


@SuppressWarnings("unchecked")

public class Commit implements Serializable {
    /** TimeStamp of current time. */
    private String timestamp;
    /** Commit Message.*/
    private String commitMsg;
    /** This is the previously committed. */
    private String parent;
    /** HashMap of filename as key and sha1 id as the value. */
    private HashMap<String, String> blobs;
    /** This is the committedId. */
    private String id;
    /** Filename. */
    private String fileName;
    /** fileId or sha1 of the whole file.*/
    private String fileId;
    /**List of parents in each Commit. */
    private ArrayList<Commit> parents;
    /** Parent Commit. */
    private Commit parr;
    /** Num of commits. */
    private int commitNum;

    /** Constructor.
     * @param msg message
     * @param time current time
     * @param fileID fileID
     * @param parentCID par
     * @param num number
     * */
    public Commit(String msg, String parentCID, String time,
                  String fileID, int num) {
        if (msg == null) {
            System.out.print("Please enter a commit message");
            System.exit(0);
        }
        this.commitMsg = msg;
        this.blobs = new HashMap<>();
        this.timestamp = time;
        this.fileId = fileID;
        this.parent = parentCID;
        this.commitNum = num;
    }

    /** Gets committed message.
     * @return committed message
     * */
    public String getCommitMsg() {
        return this.commitMsg;
    }

    /** Gets time of current time.
     * @return currentTime
     * */
    public String getTime() {
        return this.timestamp;
    }
    /** Gets text blobs.
     * @param fil file name
     * @param hash hash
     * */
    public void addBlobs(String fil, String hash) {
        blobs.put(fil, hash);
    }
    /** Gets commit id.
     * @returns id of the committed file.
     * */
    public String getId() {
        id = Utils.sha1(this.commitMsg, this.timestamp, this.fileId);
        return id;
    }
    /** Gets the file based on its fileID.
     * @return returns blobs*/
    public HashMap<String, String> getFile() {
        return blobs;
    }
    /** Gets the text blobs.
     * @param fil file
     * @return returns blobs*/
    public String getBlob(String fil) {
        return blobs.get(fil);
    }
    /** Returns true if Commit contains the file.
     * @param file file
     * @return returns true or false
     * */
    public boolean containsFile(String file) {
        return blobs.containsKey(file);
    }
    /** Gets parent id.
     * @return returns parent's commit id. */
    public String getParentId() {
        return parent;
    }
    /** Gets parents.
     * @return returns array of parents. */
    public ArrayList<Commit> getParents() {
        return parents;
    }

}
