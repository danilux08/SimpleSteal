package me.danilux.simplesteal.utils.lifesteal;

public enum LSUnbanResult {

    ALREADY("already"),
    NOT_LS_RELATED("not-ls-related"),
    SUCCESS("success");

    public final String messageKey;

    LSUnbanResult(String messageKey) {
        this.messageKey = "unban." + messageKey;
    }
}
