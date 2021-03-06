package me.madfix.cloudnet.webinterface.updates;

import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.api.update.UpdateTask;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class Update_1_9 extends UpdateTask {

    private static final String UPDATE_1_9_TABLE_PROCEDURE = "CREATE PROCEDURE IF NOT EXISTS `update_1_9_create_tables`()\n" +
            "LANGUAGE SQL\n" +
            "NOT DETERMINISTIC\n" +
            "CONTAINS SQL\n" +
            "SQL SECURITY DEFINER\n" +
            "COMMENT ''\n" +
            "BEGIN\n" +
            "\tCREATE TABLE IF NOT EXISTS `groups` (id INT(32) PRIMARY KEY AUTO_INCREMENT, groupname VARCHAR(32));\n" +
            "\tCREATE TABLE IF NOT EXISTS `group_permission` (id INT(32) PRIMARY KEY AUTO_INCREMENT, permission VARCHAR(255), gId INT(32), FOREIGN KEY (gId) REFERENCES groups(id));\n" +
            "\tCREATE TABLE IF NOT EXISTS `user_groups` (id INT(32) PRIMARY KEY AUTO_INCREMENT, potency INT(32), gId INT(32), uId INT(32), FOREIGN KEY (gId) REFERENCES groups(id), FOREIGN KEY (uId) REFERENCES users(id));\n" +
            "END";
    private static final String CALL_UPDATE_1_9_PROCEDURE = "CALL `update_1_9_create_tables`";

    public Update_1_9() {
        super("1.9.0");
    }

    @Override
    public CompletableFuture<Boolean> preUpdateStep(WebInterface webInterface) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        webInterface.getDatabaseService().getConnection().ifPresent(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(Update_1_9.UPDATE_1_9_TABLE_PROCEDURE)) {
                statement.execute();
                completableFuture.complete(true);
            } catch (SQLException e) {
                webInterface.getLogger().log(Level.SEVERE, "Update 1.9.0 table procedures could not be created", e);
                completableFuture.cancel(true);
            }
        });

        return completableFuture;
    }

    @Override
    public CompletableFuture<Boolean> updateStep(WebInterface webInterface) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        webInterface.getDatabaseService().getConnection().ifPresent(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(Update_1_9.CALL_UPDATE_1_9_PROCEDURE)) {
                completableFuture.complete(true);
                statement.execute();
            } catch (SQLException e) {
                webInterface.getLogger().log(Level.SEVERE, "Update 1.9.0 table procedures could not be called!", e);
                completableFuture.cancel(true);
            }
        });
        return completableFuture;
    }

    @Override
    public CompletableFuture<Boolean> postUpdateStep(WebInterface webInterface) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        completableFuture.complete(true);
        return completableFuture;
    }
}
