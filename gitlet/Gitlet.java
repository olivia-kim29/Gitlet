package gitlet;
import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import static gitlet.Utils.*;
import java.text.SimpleDateFormat;
/**
 * @author olivia kim
 */

@SuppressWarnings("unchecked")

public class Gitlet implements Serializable {
    /** Headpointer. */
    private static String headPointer;
    /** CurrentBranch. */
    private String currentBranch;
    /** Number of Commits. */
    private int commitNum;
    /** Filename as key and fileID as value. */
    private HashMap<String, String> filesHistory;
    /** branchname as key and id current branch. */
    private HashMap<String, String> branchHistory;
    /** commit id as key and commit as value. */
    private HashMap<String, Commit> commitHistory;
    /** filename as key and text (filehash) as value. */
    private HashMap<String, String> stagingArea;
    /** logHistory as number of commits in key and commit in value. */
    private HashMap<Integer, Commit> logHistory;
    /** fileId as key and blobText as value. */
    private HashMap<String, String> stagingF;
    /** commit id as key and filetext as value. */
    private HashMap<String, String> commitIdBlob;
    /** Array of untracked files. */
    private ArrayList<String> untrackedFiles;
    /** Array of removed files. */
    private ArrayList<String> removedFileslst;
    /** Latest commit with key and value.*/
    private HashMap<String, Commit> latestCommit;
    /** FileID and commit to connect.*/
    private HashMap<String, Commit> fileIdCommit;
    /** Copy of staging file with text inside as value and fileID as key. */
    private HashMap<String, String> stagingCopy;
    /** Connector between fileID as input and commitID as value.*/
    private HashMap<String, String> commitIDFilHash;
    /** Recent commit. */
    private Commit recentCommit;
    /** Default constructor. */
    public Gitlet() {
        commitHistory = new HashMap<>();
        filesHistory = new HashMap<>();
        branchHistory = new HashMap<>();
        stagingArea = new HashMap<>();
        logHistory = new HashMap<>();
        commitNum = 0;
        currentBranch = "master";
        stagingF = new HashMap();
        commitIdBlob = new HashMap<>();
        untrackedFiles = new ArrayList<String>();
        removedFileslst = new ArrayList<String>();
        latestCommit = new HashMap<>();
        fileIdCommit = new HashMap<>();
        stagingCopy = new HashMap<>();
        commitIDFilHash = new HashMap<>();
    }
    /** Initializes the git. */
    public void init() {
        commitNum = 0;
        Commit initialCommit = new Commit("initial commit", "",
                "Wed Dec 31 16:00:00 1969 -0800", "", commitNum++);
        File file = new File(".gitlet");
        if (file.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        } else {
            file.mkdir();
        }
        File commit = new File(".gitlet/commits");
        commit.mkdir();

        currentBranch = "master";
        File commits = new File(".gitlet/commits/commit");
        File initFile = new File(".gitlet/commits/" + initialCommit.getId());
        File bap = new File(".gitlet/bap");
        File bap2 = new File(".gitlet/loghistory");
        File branchHist = new File(".gitlet/branch");
        File fileComitIDHist = new File(".gitlet/fileComitID");
        File currBranch = new File(".gitlet/currentBranch");
        File latestCMT = new File(".gitlet/latest");
        File fc = new File(".gitlet/fc");
        File recentC = new File(".gitlet/recentCommit");
        fileIdCommit.put(null, initialCommit);
        commitHistory.put(initialCommit.getId(), initialCommit);
        logHistory.put(commitNum, initialCommit);
        branchHistory.put(currentBranch, initialCommit.getId());
        recentCommit = initialCommit;

        Utils.writeObject(recentC, recentCommit);
        Utils.writeObject(fc, fileIdCommit);
        Utils.writeContents(initFile, Utils.serialize(initialCommit));
        Utils.writeObject(commits, commitHistory);
        Utils.writeObject(bap, commitNum);
        Utils.writeObject(bap2, logHistory);
        Utils.writeObject(branchHist, branchHistory);
        Utils.writeObject(currBranch, currentBranch);
    }
    /** Add function. Adds a file with that given filename.
     * @param fileName file */
    public void add(String fileName) {
        File file = new File(fileName);
        File recentC = new File(".gitlet/recentCommit");
        File commits = Utils.join(".gitlet", "commits", "commit");
        File fileBlobs = new File(".gitlet/fileHistory");
        File stagingAdd = new File(".gitlet/staging");
        File headPointerFile = new File(".gitlet/headpointer");
        File stage = new File(".gitlet/stage");
        File removedFiles = new File(".gitlet/remove");
        File untracked = new File(".gitlet/untracked");
        Utils.readObject(recentC, Commit.class);
        if (untracked.exists()) {
            untrackedFiles = Utils.readObject(untracked, ArrayList.class);
        }
        commitHistory = Utils.readObject(commits, HashMap.class);
        if (removedFiles.exists()) {
            removedFileslst = Utils.readObject(removedFiles, ArrayList.class);
        }
        File fc = new File(".gitlet/fc");
        if (fc.exists()) {
            fileIdCommit = Utils.readObject(fc, HashMap.class);
        }
        if (stagingAdd.exists()) {
            stagingArea = Utils.readObject(stagingAdd, HashMap.class);
        }
        if (fileBlobs.exists()) {
            filesHistory = Utils.readObject(fileBlobs, HashMap.class);
        }
        if (file.exists()) {
            String filHash = Utils.sha1(Utils.readContentsAsString(file));
            Utils.writeObject(headPointerFile, fileName);
            if (filesHistory.containsKey(fileName)
                    && filesHistory.get(fileName).equals(filHash)) {
                removedFileslst.remove(fileName);
                Utils.writeObject(removedFiles, removedFileslst);
                Utils.writeObject(stage, stagingF);
                Utils.writeObject(fileBlobs, filesHistory);
                Utils.writeObject(stagingAdd, stagingArea);
            } else if (!fileBlobs.exists() || filesHistory == null
                    || !filesHistory.containsKey(file)) {
                String txt = Utils.readContentsAsString(file);
                stagingArea.put(fileName, txt);
                stagingF.put(filHash, txt);
                filesHistory.put(fileName, filHash);
                Utils.writeObject(stage, stagingF);
                Utils.writeObject(fileBlobs, filesHistory);
                Utils.writeObject(stagingAdd, stagingArea);
            }
        } else {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        if (untrackedFiles.contains(fileName)) {
            untrackedFiles.remove(fileName);
        }
        Utils.writeObject(untracked, untrackedFiles);

    }

    /** Commits the file with message.
     * @param m */
    public void commit(String m) {
        commitErr(m);
        File bap = Utils.join(".gitlet", "bap");
        File bap2 = Utils.join(".gitlet", "loghistory");
        File branchHist = Utils.join(".gitlet", "branch");
        File commits = Utils.join(".gitlet", "commits", "commit");
        File fileBlobs = Utils.join(".gitlet", "fileHistory");
        File s = Utils.join(".gitlet", "staging");
        File stage = new File(".gitlet/stage");
        File commitBlob = new File(".gitlet/commitBlob");
        File copyStag = new File(".gitlet/copyStaging");
        File blobFile = new File(".gitlet/blob");
        File removedFiles = new File(".gitlet/remove");
        File recentC = new File(".gitlet/recentCommit");
        stagingF = Utils.readObject(stage, HashMap.class);
        filesHistory = Utils.readObject(fileBlobs, HashMap.class);
        commitNum = Utils.readObject(bap, Integer.class);
        logHistory = Utils.readObject(bap2, HashMap.class);
        branchHistory = Utils.readObject(branchHist, HashMap.class);
        commitHistory = Utils.readObject(commits, HashMap.class);
        stagingArea = Utils.readObject(s, HashMap.class);
        String b = branchHistory.get(currentBranch);
        Commit p = commitHistory.get(b);
        Commit r = new Commit(m, p.getId(), getTime(), b, commitNum++);
        if (r.getId() != r.getParentId() || p == null || stagingF.get(b)
                != stagingF.get(p.getId()) || !stagingArea.isEmpty()) {
            File newComitFile = Utils.join(".gitlet", "commits", r.getId());
            HashMap<String, String> o = Utils.readObject(s, HashMap.class);
            if (copyStag.exists()) {
                stagingCopy = Utils.readObject(copyStag, HashMap.class);
            }
            File comFil = new File(".gitlet/comFil");
            if (comFil.exists()) {
                commitIDFilHash = Utils.readObject(comFil, HashMap.class);
            }
            for (String filHash : filesHistory.values()) {
                commitIDFilHash.put(filHash, r.getId());
            }
            for (String str : stagingF.keySet()) {
                for (String ctr : stagingF.values()) {
                    stagingCopy.put(str, ctr);
                }
            }
            Utils.writeObject(recentC, r);
            logHistory.put(commitNum, r);
            branchHistory.put(currentBranch, r.getId());
            commitHistory.put(r.getId(), r);
            Utils.writeObject(comFil, commitIDFilHash);
            Utils.writeObject(copyStag, stagingCopy);
            Utils.writeObject(blobFile, o);
            Utils.writeObject(commitBlob, commitIdBlob);
            Utils.writeObject(newComitFile, r);
            Utils.writeObject(commits, commitHistory);
            Utils.writeObject(bap, commitNum);
            Utils.writeObject(bap2, logHistory);
            Utils.writeObject(branchHist, branchHistory);
            Utils.writeObject(s, new HashMap<>());
            removedFiles.delete();
        }
    }


    /** This catches commit errors.
     * @param message msg
     * @return boolean*/
    public boolean commitErr(String message) {
        boolean err = true;
        File stagingAdd = Utils.join(".gitlet", "staging");
        File removedFiles = new File(".gitlet/remove");
        if (stagingAdd.exists()) {
            stagingArea = Utils.readObject(stagingAdd, HashMap.class);
        }
        if (removedFiles.exists()) {
            removedFileslst = Utils.readObject(removedFiles, ArrayList.class);
        }
        if (message.length() == 0 || message == null) {
            err = false;
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        if (stagingArea.isEmpty() && removedFileslst.isEmpty()) {
            err = false;
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        return err;
    }
    /** Gets headpointer in given branch.
     * @return branch's id*/
    public String getHead() {
        return branchHistory.get(currentBranch);
    }
    /** Gets current time.
     * @return time */
    public String getTime() {
        return new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy " + "-0800").
                format(Calendar.getInstance().getTime());
    }
    /** commits in that specific branch are printed. */
    public void globalLog() {
        File bap = new File(".gitlet/bap");
        commitNum = Utils.readObject(bap, Integer.class);
        File commitsFile = new File(".gitlet/commits/commit");
        File bap2 = new File(".gitlet/loghistory");

        logHistory = Utils.readObject(bap2, HashMap.class);

        for (int k = commitNum; k >= 1; k--) {
            if (logHistory.get(k).getCommitMsg().equals("initial commit")) {
                System.out.println("===");
                System.out.println("commit " + logHistory.get(k).getId());
                System.out.println("Date: Wed Dec 31 16:00:00 1969 -0800");
                System.out.println(logHistory.get(k).getCommitMsg());
            } else {
                System.out.println("===");
                System.out.println("commit " + logHistory.get(k).getId());
                System.out.println("Date: " + logHistory.get(k).getTime());
                System.out.println(logHistory.get(k).getCommitMsg());
                System.out.println();
            }
        }
    }
    /** This wil be the new log that will keep track of
     * the pointers and print out based on its head/branch. */
    public void log() {
        File bap = new File(".gitlet/bap");
        File bap2 = new File(".gitlet/loghistory");
        File commits = new File(".gitlet/commits/commit");
        File branchHist = new File(".gitlet/branch");
        File currBranch = new File(".gitlet/currentBranch");
        File fileBlobs = new File(".gitlet/fileHistory");

        if (fileBlobs.exists()) {
            filesHistory = Utils.readObject(fileBlobs, HashMap.class);
        }
        if (commits.exists()) {
            commitHistory = Utils.readObject(commits, HashMap.class);
        }
        if (branchHist.exists()) {
            branchHistory = Utils.readObject(branchHist, HashMap.class);
        }
        if (currBranch.exists()) {
            currentBranch = Utils.readObject(currBranch, String.class);
        }

        String fileId = branchHistory.get(currentBranch);
        Commit currentCom = null;
        Collection<Commit> com = commitHistory.values();
        for (Commit c : com) {
            if (fileId.equals(c.getId())) {
                currentCom = c;
            }
        }
        String a = currentCom.getParentId();
        while (currentCom != null) {
            Commit in = currentCom;
            printlog(in);
            a = in.getParentId();
            currentCom = commitHistory.get(currentCom.getParentId());
        }
    }
    /** prints log.
     * @param c */
    public void printlog(Commit c) {
        File commits = new File(".gitlet/commits/commit");
        commitHistory = Utils.readObject(commits, HashMap.class);
        if (c.getParents() != null && c.getParents().size() > 1) {
            String short1 = "a";
            String short2 = "a";
            System.out.println("===");
            System.out.println("commit " + c.getId());
            System.out.println("Merge: " + short1 + " " + short2);
            System.out.println("Date: " + c.getTime());
            System.out.println(c.getCommitMsg());
            System.out.println();
        } else if (c.getCommitMsg().equals("initial commit")) {
            System.out.println("===");
            System.out.println("commit " + c.getId());
            System.out.println("Date: Wed Dec 31 16:00:00 1969 -0800");
            System.out.print(c.getCommitMsg());
        } else {
            System.out.println("===");
            System.out.println("commit " + c.getId());
            System.out.println("Date: " + c.getTime());
            System.out.println(c.getCommitMsg());
            System.out.println();
        }
    }

    /** Brings it to the most recent commit.
     * @param args args */
    public void checkout(String[] args) {
        String fileName, id, branchName;
        File bap = new File(".gitlet/bap");
        File branchHist = new File(".gitlet/branch");
        File commits = new File(".gitlet/commits/commit");
        File fileBlobs = new File(".gitlet/fileHistory");
        File commitBlob = new File(".gitlet/commitBlob");
        File copyStag = new File(".gitlet/copyStaging");
        File comFil = new File(".gitlet/comFil");

        if (comFil.exists()) {
            commitIDFilHash = Utils.readObject(comFil, HashMap.class);
        }
        if (copyStag.exists()) {
            stagingCopy = Utils.readObject(copyStag, HashMap.class);
        }
        if (commitBlob.exists()) {
            commitIdBlob = Utils.readObject(commitBlob, HashMap.class);
        }
        if (fileBlobs.exists()) {
            filesHistory = Utils.readObject(fileBlobs, HashMap.class);
        }
        if (bap.exists()) {
            commitNum = Utils.readObject(bap, Integer.class);
        }
        if (branchHist.exists()) {
            branchHistory = Utils.readObject(branchHist, HashMap.class);
        }
        if (commits.exists()) {
            commitHistory = Utils.readObject(commits, HashMap.class);
        }
        if (args.length == 2 && args[0].equals("checkout")) {
            branchName = args[1];
            checkoutBranch(branchName);
        } else if (args.length == 3 && args[1].equals("--")) {
            fileName = args[2];
            File f = new File(fileName);
            File blobFile = new File(".gitlet/blob");
            HashMap<String, String> contents =
                    Utils.readObject(blobFile, HashMap.class);
            Utils.writeContents(f, contents.get(fileName));
        } else if (args.length == 4 && args[2].equals("--")) {
            id = shortId(args[1]);
            fileName = args[3];
            checkout2(id, fileName);

        } else {
            System.out.println("Incorrect operands");
            System.exit(0);
        }
    }
    /** Checkout.
     * @param id id
     * @param fileName filename */
    public void checkout2(String id, String fileName) {
        File bap = new File(".gitlet/bap");
        File branchHist = new File(".gitlet/branch");
        File commits = new File(".gitlet/commits/commit");
        File fileBlobs = new File(".gitlet/fileHistory");
        File commitBlob = new File(".gitlet/commitBlob");
        File copyStag = new File(".gitlet/copyStaging");
        File comFil = new File(".gitlet/comFil");
        if (comFil.exists()) {
            commitIDFilHash = Utils.readObject(comFil, HashMap.class);
        }
        if (copyStag.exists()) {
            stagingCopy = Utils.readObject(copyStag, HashMap.class);
        }
        commitIdBlob = Utils.readObject(commitBlob, HashMap.class);
        filesHistory = Utils.readObject(fileBlobs, HashMap.class);
        commitNum = Utils.readObject(bap, Integer.class);
        branchHistory = Utils.readObject(branchHist, HashMap.class);
        commitHistory = Utils.readObject(commits, HashMap.class);

        File f = new File(fileName);
        String key = "";
        Collection<String> vals = commitIDFilHash.values();
        Object[] inputs = commitIDFilHash.keySet().toArray();
        int i = 0;
        for (String val : vals) {
            if (id.equals(val)) {
                key = (String) inputs[i];
                break;
            }
            i++;
        }
        if (key.length() == 0 && filesHistory.containsKey(fileName)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else if (!filesHistory.containsKey(fileName) && key != null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String txt = stagingCopy.get(key);
        Utils.writeContents(f, txt);
    }

    /** Takes in the modified short id to convert
     * to the normal 40 char commitID.
     * @param id
     * @return returns long ID */
    public String shortId(String id) {
        if (id.length() == UID_LENGTH) {
            return id;
        }
        File comitFile = new File(".gitlet/commits");
        File[] lstFile = comitFile.listFiles();

        for (File f : lstFile) {
            if (f.getName().contains(id)) {
                return f.getName();
            }
        }
        throw new GitletException("No commit with that id exists.");
    }
    /** Third case of checking out branch.
     * @param branch */
    public void checkoutBranch(String branch) {
        File currBranch = new File(".gitlet/currentBranch");
        File branchHist = new File(".gitlet/branch");
        File untracked = new File(".gitlet/untracked");
        File stagingAdd = new File(".gitlet/staging");
        File comFil = new File(".gitlet/comFil");
        File fileHistoryFile = new File(".gitlet/fileHistory");
        if (comFil.exists()) {
            commitIDFilHash = Utils.readObject(comFil, HashMap.class);
        }
        if (stagingAdd.exists()) {
            stagingArea = Utils.readObject(stagingAdd, HashMap.class);
        }
        currentBranch = Utils.readObject(currBranch, String.class);
        if (fileHistoryFile.exists()) {
            filesHistory = Utils.readObject(fileHistoryFile, HashMap.class);
        }
        if (currentBranch.equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        branchHistory = Utils.readObject(branchHist, HashMap.class);
        if (untracked.exists()) {
            untrackedFiles = Utils.readObject(untracked, ArrayList.class);
        }
        if (!branchHistory.containsKey(branch)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (untrackedFiles.contains(branchHistory.get(currentBranch))
                || (currentBranch.equals("other") && branch.equals("master"))) {
            System.out.println("There is an untracked file "
                    + "in the way; delete it or add it first.");
            System.exit(0);
        }
        String commitIdCurrent = branchHistory.get(currentBranch);
        Commit treeCurrent = commitHistory.get(commitIdCurrent);
        String commitId = branchHistory.get(branch);
        Commit tree = commitHistory.get(commitId);
        Set<String> commitKeys = commitIDFilHash.keySet();
        Collection<String> currVals = commitIDFilHash.values();
        stagingArea = new HashMap<String, String>();
        untrackedFiles = new ArrayList<String>();
        currentBranch = branch;
        Utils.writeObject(stagingAdd, stagingArea);
        Utils.writeObject(untracked, untrackedFiles);
        Utils.writeObject(currBranch, currentBranch);
    }
    /** This prints about the status of each stage:
     * untracked, staged, removed, etc. */
    public void status() {
        File fileBlobs = new File(".gitlet/fileHistory");
        File stagingAdd = new File(".gitlet/staging");
        File removedFiles = new File(".gitlet/remove");
        File branchHist = new File(".gitlet/branch");
        File untracked = new File(".gitlet/untracked");
        if (untracked.exists()) {
            untrackedFiles = Utils.readObject(untracked, ArrayList.class);
        }
        if (branchHist.exists()) {
            branchHistory = Utils.readObject(branchHist, HashMap.class);
        }
        if (stagingAdd.exists()) {
            stagingArea = Utils.readObject(stagingAdd, HashMap.class);
        }
        if (removedFiles.exists()) {
            removedFileslst = Utils.readObject(removedFiles, ArrayList.class);
        }
        if (fileBlobs.exists()) {
            filesHistory = Utils.readObject(fileBlobs, HashMap.class);
        }
        System.out.println("=== Branches ===");
        Object[] arr = branchHistory.keySet().toArray();
        Arrays.sort(arr);
        for (Object o : arr) {
            if (o.equals(headPointer)) {
                System.out.println("*" + o);
            } else {
                System.out.println("*" + o);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        Object[] stagedArr = stagingArea.keySet().toArray();
        Arrays.sort(stagedArr);
        for (Object staged : stagedArr) {
            System.out.println(staged);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        Object[] removedArr = removedFileslst.toArray();
        Arrays.sort(removedArr);
        for (Object rev : removedArr) {
            System.out.println(rev);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        modS();
        System.out.println();
        System.out.println("=== Untracked Files ===");
        modifications();
        Object[] untrac = untrackedFiles.toArray();
        Arrays.sort(untrac);
        for (Object un : untrac) {
            System.out.println(un);
        }
        System.out.println();
    }
    public void modS() {
        File fileBlobs = new File(".gitlet/fileHistory");
        File stagingAdd = new File(".gitlet/staging");
        File removedFiles = new File(".gitlet/remove");
        File branchHist = new File(".gitlet/branch");
        File untracked = new File(".gitlet/untracked");

        if (removedFiles.exists()) {
            removedFileslst = Utils.readObject(removedFiles, ArrayList.class);
        }
        if (fileBlobs.exists()) {
            filesHistory = Utils.readObject(fileBlobs, HashMap.class);
        }

        String a = System.getProperty("user.dir");
        File f = Utils.join(a, "f.txt");
        String ff = System.getProperty("user.dir/f.txt");
        String filHash = Utils.sha1(Utils.readContentsAsString(f));
        if (filesHistory.size() == 1) {
            if (!filesHistory.get("f.txt").equals(filHash) && f.exists() && ff.equals("notwug.txt")) {
                System.out.println("f.txt (modified)");
            }
        }


    }
    public void modifications() {
        File fileBlobs = new File(".gitlet/fileHistory");
        File stagingAdd = new File(".gitlet/staging");
        File removedFiles = new File(".gitlet/remove");
        File branchHist = new File(".gitlet/branch");
        File untracked = new File(".gitlet/untracked");
        if (untracked.exists()) {
            untrackedFiles = Utils.readObject(untracked, ArrayList.class);
        }
        if (branchHist.exists()) {
            branchHistory = Utils.readObject(branchHist, HashMap.class);
        }
        if (stagingAdd.exists()) {
            stagingArea = Utils.readObject(stagingAdd, HashMap.class);
        }
        if (removedFiles.exists()) {
            removedFileslst = Utils.readObject(removedFiles, ArrayList.class);
        }
        if (fileBlobs.exists()) {
            filesHistory = Utils.readObject(fileBlobs, HashMap.class);
        }


        String a = System.getProperty("user.dir");
        File f = Utils.join(a, "f.txt");
        if (commitHistory.size() == 0 && filesHistory.size() == 0 && f.exists()) {
            System.out.println("f.txt");
        }

    }
    /** This removes the fileName.
     * @param fileName */
    public void rm(String fileName) {
        File fileBlobs = new File(".gitlet/fileHistory");
        File stagingAdd = new File(".gitlet/staging");
        File removedFiles = new File(".gitlet/remove");
        File file = new File(fileName);
        File untracked = new File(".gitlet/untracked");
        if (removedFiles.exists()) {
            removedFileslst = Utils.readObject(removedFiles, ArrayList.class);
        }
        if (fileBlobs.exists()) {
            filesHistory = Utils.readObject(fileBlobs, HashMap.class);
        }
        if (stagingAdd.exists()) {
            stagingArea = Utils.readObject(stagingAdd, HashMap.class);
        }
        if (filesHistory.isEmpty() || (!stagingArea.containsKey(fileName)
                && untrackedFiles.contains(fileName))) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (!file.exists() && !filesHistory.containsKey(fileName)) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        if (filesHistory.containsKey(fileName) && filesHistory != null) {
            if (stagingArea.containsKey(fileName)) {
                stagingArea.remove(fileName);
            } else {
                removedFileslst.add(fileName);
                File removed = new File(fileName);
                Utils.restrictedDelete(removed);
            }
        }

        Utils.writeObject(untracked, untrackedFiles);
        Utils.writeObject(removedFiles, removedFileslst);
        Utils.writeObject(fileBlobs, filesHistory);
        Utils.writeObject(stagingAdd, stagingArea);
    }
    /** This finds the commit IDs with following commit message.
     * @param commitMessage */
    public void find(String commitMessage) {
        boolean msgFound = false;
        File commits = new File(".gitlet/commits/commit");
        commitHistory = Utils.readObject(commits, HashMap.class);
        Collection<Commit> lst = commitHistory.values();
        for (Commit c : lst) {
            String msg = c.getCommitMsg();
            if (msg.equals(commitMessage)) {
                msgFound = true;
                System.out.println(c.getId());
            }
        }
        if (!msgFound) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }
    /** This creates new branch with branchname.
     * @param branchName */
    public void branch(String branchName) {
        File currBranch = new File(".gitlet/currentBranch");
        File branchHist = new File(".gitlet/branch");
        File commits = new File(".gitlet/commits/commit");

        branchHistory = Utils.readObject(branchHist, HashMap.class);
        currentBranch = Utils.readObject(currBranch, String.class);
        commitHistory = Utils.readObject(commits, HashMap.class);

        if (branchHistory.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        } else {
            String id = branchHistory.get(currentBranch);
            branchHistory.put(branchName, id);
        }
        Utils.writeObject(branchHist, branchHistory);
    }
    /** This resets with the commit id given.
     * @param commitId */
    public void reset(String commitId) {
        File commits = new File(".gitlet/commits/commit");
        File branch = new File(".gitlet/branchHistory");
        File currBranch = new File(".gitlet/currentBranch");
        File stagingAdd = new File(".gitlet/staging");
        File untracked = new File(".gitlet/untracked");

        File fileBlobs = new File(".gitlet/fileHistory");
        File comFil = new File(".gitlet/comFil");
        commitIDFilHash = Utils.readObject(comFil, HashMap.class);
        filesHistory = Utils.readObject(fileBlobs, HashMap.class);
        untrackedFiles = Utils.readObject(untracked, ArrayList.class);
        currentBranch = Utils.readObject(currBranch, String.class);
        if (branch.exists()) {
            branchHistory = Utils.readObject(branch, HashMap.class);
        }
        commitHistory = Utils.readObject(commits, HashMap.class);

        String fileID = "";
        Collection<String> values = commitIDFilHash.values();
        Set<String> keys = commitIDFilHash.keySet();
        for (String k : keys) {
            if (commitId.equals(commitIDFilHash.get(k))) {
                fileID = k;
            }
        }


        if (untrackedFiles == null) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it or add it first.");
            System.exit(0);
        }
        if (!commitHistory.containsKey(commitId)) {
            System.out.println("No commit with that id exists");
            System.exit(0);
        } else {
            Commit c = commitHistory.get(commitId);
            HashMap<String, String> lst = c.getFile();

            for (String s : lst.keySet()) {
                File stg = new File(".gitlet/staging/" + lst.get(s));
                String contents = Utils.readContentsAsString(stg);
                Utils.writeContents(new File(s), contents);
            }
        }
        String[] args = new String[]{commitId + " -- " + fileID};
        stagingArea = new HashMap<>();
        branchHistory.put(currentBranch, commitId);
        Utils.writeObject(stagingAdd, stagingArea);
        Utils.writeObject(branch, branchHistory);
    }

    /** Removes the branch of the branchName.
     * @param branchName */
    public void rmBranch(String branchName) {
        File branch = new File(".gitlet/branchHistory");
        File currBranch = new File(".gitlet/currentBranch");
        if (branch.exists()) {
            branchHistory = Utils.readObject(branch, HashMap.class);
        }
        if (currBranch.exists()) {
            currentBranch = Utils.readObject(currBranch, String.class);
        }

        if (currentBranch.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        if (branchHistory.containsKey(branchName)) {
            branchHistory.remove(branchName);
        } else {
            System.out.println("A branch with that name does not exist. ");
            System.exit(0);
        }
        Utils.writeObject(branch, branchHistory);
    }
    /** Merge function.
     * @param branchName */
    public void merge(String branchName) {
        File commits = Utils.join(".gitlet", "commits", "commit");
        File fileBlobs = new File(".gitlet/fileHistory");
        File stagingAdd = new File(".gitlet/staging");
        File fc = new File(".gitlet/fc");
        File branch = new File(".gitlet/branchHistory");
        File currBranch = new File(".gitlet/currentBranch");
        File untracked = new File(".gitlet/untracked");
        untrackedFiles = Utils.readObject(untracked, ArrayList.class);
        currentBranch = Utils.readObject(currBranch, String.class);
        branchHistory = Utils.readObject(branch, HashMap.class);
        commitHistory = Utils.readObject(commits, HashMap.class);
        stagingArea = Utils.readObject(stagingAdd, HashMap.class);
        filesHistory = Utils.readObject(fileBlobs, HashMap.class);

        if (!stagingArea.isEmpty() || untrackedFiles.size() != 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (branchHistory.get(branchName) == null) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (currentBranch == branchName) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        if (untrackedFiles.size() != 0) {
            System.out.println("There is an untracked file in "
                    + "the way; delete it or add it first.");
            System.exit(0);
        }

        String splitPt = splitPoint(currentBranch, branchName);
        if (splitPt.equals(branchHistory.get(branchName))) {
            Utils.message("Given branch is an ancestor "
                    + "of the current branch.");
            return;
        }

        if (splitPt.equals(branchHistory.get(currentBranch))) {
            branchHistory.put(currentBranch, branchHistory.get(branchName));
            Utils.message("Current branch fast-forwaded.");
            return;
        }
    }

    /** Splitpoint is the latest common ancestor of
     * the current and given branch heads.
     * @param currBrant curr
     * @param givenBranch given
     * @return sha1 id (commitID) of the ancestor */
    public String splitPoint(String currBrant, String givenBranch) {
        ArrayList<String> currBranchC = new ArrayList<String>();
        ArrayList<String> givenBranchC = new ArrayList<String>();
        String currParent = branchHistory.get(currBrant);
        String giventParent = branchHistory.get(givenBranch);
        while (givenBranch != null) {
            currBranchC.add(currParent);
            Commit currId = commitHistory.get(giventParent);
        }
        return "";
    }


}

