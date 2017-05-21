package net.mittnett.reke.Rekeverden.handlers;

public enum BlockAction {
    PLACED(1, "placed"),
    REMOVED(0, "removed"),
    PROTECTED(2, "protected"),
    UNPROTECTED(3, "unprotected"),
    ADMIN_STICKED(4, "admin-sticked");

    private int actionID;
    private String actionString;

    private BlockAction(int actionID, String actionString) {
        this.actionID = actionID;
        this.actionString = actionString;
    }

    public int getActionID() {
        return this.actionID;
    }

    public String getActionString() {
        return this.actionString;
    }


    public static BlockAction fromActionID(int actionID) {
        for (BlockAction ba : BlockAction.values()) {
            if (ba.getActionID() == actionID) {
                return ba;
            }
        }
        return null;
    }
}