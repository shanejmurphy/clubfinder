package security;


/**
 * @author Gamal Hilal & Ruairi O'Brien
 * @version 1
 * @since 1.2.0 - 22/03/2012
 */
public class BroadcastReceiverHandler {
    // ===========================================================
    // Constants
    // ===========================================================
    //private static final String TAG = "BroadcastReceiverHandler";

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================
    private BroadcastReceiverHandler() {

    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public static synchronized BroadcastReceiverHandler getInstance() {
        return SingletonHolder.instance;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    private static class SingletonHolder {
        public static final BroadcastReceiverHandler instance = new BroadcastReceiverHandler();
    }
}
