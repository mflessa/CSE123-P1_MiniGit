import java.util.*;
import java.text.SimpleDateFormat;

public class Repository {

   /*Your Repository class should have the following fields as specified below and they should be declared private. You are not allowed to have any other fields.

    A reference to the head of the repository.

    A field to keep track of the repository's name. 

    (Optional) A size field to keep track of the size of the repository.
    */

    //fields
    private Commit repoHead; //the front of the "linkedlist" of commits
    //THINK OF EACH COMMIT AS A "ListNode" -> has a past + data
    private String name;

    //constructors
    public Repository(String name){
        if(name == null || name.equals("")){
            throw new IllegalArgumentException();
        }
        this.name = name;
        // this.repoHead = new Commit(null);
        this.repoHead = null;
    }

    //instance methods

    //Returns the ID of the current head of this repository
    //If the head is null, returns null
    public String getRepoHead(){
        if(this.repoHead == null){
            return null;
        }
        return this.repoHead.id;
    }

    //Returns the number of commits in the repository
    public int getRepoSize(){
        if(this.repoHead == null){
            return 0;
        }else{
            int acc = 0;
            Commit curr = repoHead;
            while(curr != null){
                acc++;
                curr = curr.past;
            }
            return acc;
        }
        
    }

    //Returns a String representation of this repository
    // <name> - Current head: <head>
    // <head> is the result of calling toString() on the head commit
    // If there are no commits: return <name> - No commits
    public String toString(){
        String result = this.name;
        result += " - Current head: ";
        if(this.repoHead == null){
            result+= "No commits";
        }else{
            result += this.repoHead.toString();
        }
        return result;  
    }


    //Return true if the commit with ID targetId is in repository
    //Return false if not
    //All elements are unique - loop should stop when element of interest is found
    public boolean contains(String targetId){
        Commit curr = this.repoHead;
        while(curr != null && !curr.id.equals(targetId)){ //move through list until you hit target id or null
            curr = curr.past;
        }
        if(curr.id.equals(targetId)){
            System.out.println("Target id found! Error is not in contains method");
            return true;
        }else{
            return false;
        }
    }

    //Return a String consisting of String representations of most recent n
    //commits in the repository - should be separated by a newline character
    public String getHistory(int n){
        if(n <= 0){
            throw new IllegalArgumentException("illegal number of commits");
        }
        String result = "";
        if(this.repoHead == null){
            return result;
        }else{
            result += this.repoHead.toString();
            result += "\n";
        }

        Commit curr = this.repoHead;
        curr = curr.past;
        int i = 1;
        while(curr!=null && i<n){
            result += curr.toString();
            result += "\n";
            curr = curr.past;
            i++;
        }
        return result;
    }

    //Remove the commit with ID targetID from repository
    //maintain the rest of the history
    //return true if commit was successfully dropped
    //return false if there is no commit that matches the given ID
    public boolean drop(String targetId){
        if(targetId == null){
            throw new IllegalArgumentException("Invalid id!");
        }
        //front case -> if targetId is repoHead
        if(this.getRepoHead().equals(targetId)){
            this.repoHead = this.repoHead.past;
            return true;
        }
        
        //middle case
        if(this.contains(targetId)){ //if it contains but its not the front
            Commit curr = this.repoHead;
            while(!curr.past.id.equals(targetId)){
                curr = curr.past;
            }
            curr.past = curr.past.past;
            return true;
        }else{
            return false;
        }
        
    }

    //Create a new commit with the given message
    //Then add it to the repository
    //New commit becomes new head of this repository
    //Preserve history behind it
    //Return id of the new commit
    public String commit(String message){

        //front case
        if(this.repoHead == null){
            this.repoHead = new Commit(message);
            return this.getRepoHead();
        }else{
            // //first, need past element of the commit - should be repoHead
            // this.repoHead = new Commit(message, this.repoHead);
            // //now, return the ID of the commit (which is now repoHead)
            // return this.getRepoHead();
            Commit temp = new Commit(message);
            temp.past = this.repoHead;
            this.repoHead = temp;
            return this.getRepoHead();
        }
        
        
    }

   
    //takes all the commits in the other repository,
    //moves them into this repository, preserving chronological order
    //

    //**SYNChRONIZE IS IN A DIFF ORDER THAN WEAVE!! FINAL SHOULD BE MOST RECENT TO LEAST */
    public void synchronize(Repository other){
        if(other == null){
            throw new IllegalArgumentException("other repository is null");
        }

        //if this repository is empty, just replace it with the other
        if(this.repoHead == null){
            this.repoHead = other.repoHead;
            other.repoHead = null;
        }else if(other.repoHead != null){ //repository isn't empty
            //front case
            //check if most recent commits of other are more recent than this repo's
            //only occurs if the most recent commit in other is more recent than most recent in this
            //THIS WORKS!!!
            
            if(other.repoHead.timeStamp > this.repoHead.timeStamp){
                Commit curr = this.repoHead;
                this.repoHead = other.repoHead;
                other.repoHead = other.repoHead.past;
                this.repoHead.past = curr;
            }
            
            Commit curr = this.repoHead;
            Commit temp = other.repoHead;
            //middle case - overall most recent time stamp is alr at beginning
            //temp is currently pointing to most recent object left in other list
            //curr is pointing to overall most recent object
            while(other.repoHead != null && curr.past !=null){
                if(other.repoHead.timeStamp >= curr.past.timeStamp){
                    other.repoHead = other.repoHead.past;
                    temp.past = curr.past;
                    curr.past = temp;
                    curr = temp;
                    temp = other.repoHead;
                }else{
                    curr = curr.past;
                }
                
            }

            //end case
            if(other.repoHead != null){
                curr.past = other.repoHead;
                other.repoHead = null;
            }
        }
    }









    /**
     * TODO: Implement your code here.
     */

    /**
     * DO NOT MODIFY
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this 
     * class openly mention the fields of the class. This is fine 
     * because the fields of the Commit class are public. In general, 
     * be careful about revealing implementation details!
     */
    public static class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
        * Resets the IDs of the commit nodes such that they reset to 0.
        * Primarily for testing purposes.
        */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}
