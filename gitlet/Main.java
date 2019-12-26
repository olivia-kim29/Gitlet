package gitlet;
import java.io.Serializable;
import java.io.File;


/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Olivia kim
 */
@SuppressWarnings("unchecked")
public class Main implements Serializable {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        Gitlet gitlet = new Gitlet();
        File git = new File(".gitlet");
        if (args[0].equals("init")) {
            gitlet.init();
            return;
        }
        if (git.exists()) {
            switch (args[0]) {
            case "add":
                gitlet.add(args[1]);
                return;
            case "commit":
                gitlet.commit(args[1]);
                return;
            case "log":
                gitlet.log();
                return;
            case "checkout":
                gitlet.checkout(args);
                return;
            case "global-log":
                gitlet.globalLog();
                return;
            case "merge":
                gitlet.merge(args[1]);
                return;
            case "rm":
                gitlet.rm(args[1]);
                return;
            case "status":
                gitlet.status();
                return;
            case "find":
                gitlet.find(args[1]);
                return;
            case "branch":
                gitlet.branch(args[1]);
                return;
            case "rm-branch":
                gitlet.rmBranch(args[1]);
                return;
            case "reset":
                gitlet.reset(args[1]);
                return;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
                break;
            }
        } else {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

}
