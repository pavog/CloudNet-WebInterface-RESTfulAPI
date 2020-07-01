package me.madfix.cloudnet.webinterface.http.v2;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.player.CorePlayerExecutor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpAuthHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpUserHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.RequestHelper;
import me.madfix.cloudnet.webinterface.http.v2.utils.HttpResponseHelper;

import java.util.Locale;
import java.util.UUID;

public final class PlayerApi extends MethodWebHandlerAdapter {

    private final WebInterface webInterface;

    /**
     * Process the requests for player backend.
     *
     * @param cloudNet     The main class of cloudnet
     * @param webInterface The main class of the project
     */
    public PlayerApi(CloudNet cloudNet, WebInterface webInterface) {
        super("/cloudnet/api/v2/player");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this);
        this.webInterface = webInterface;
    }

    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
                                 QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) {
        FullHttpResponse fullHttpResponse = HttpAuthHelper.simpleCheck(httpRequest);
        User user = CloudNet.getInstance()
                .getUser(RequestHelper.headerValue(httpRequest, "-xcloudnet-user"));
        Document document = new Document();
        if ("send".equals(RequestHelper.headerValue(httpRequest, "-Xmessage")
                .toLowerCase(Locale.ENGLISH)) && RequestHelper.hasHeader(httpRequest, "-Xvalue", "-Xcount")) {
            String player = RequestHelper.headerValue(httpRequest, "-Xvalue");
            String server = RequestHelper.headerValue(httpRequest, "-Xcount");
            if (!HttpUserHelper.hasPermission(user, "cloudnet.web.player.send", "*",
                    "cloudnet.web.player.*", "cloudnet.web.player.send." + player)) {
                return HttpResponseHelper.permissionDenied(fullHttpResponse);
            }
            if (player.matches(
                    "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
                CloudPlayer cloudPlayer = this.webInterface.getCloud().getNetworkManager()
                        .getOnlinePlayer(UUID.fromString(player));
                CorePlayerExecutor.INSTANCE.sendPlayer(cloudPlayer, server);
            } else {
                if (player.equalsIgnoreCase("*")) {
                    this.webInterface.getCloud().getNetworkManager().getOnlinePlayers().values()
                            .forEach(t -> CorePlayerExecutor.INSTANCE.sendPlayer(t, server));
                    return HttpResponseHelper.success(fullHttpResponse, document);
                } else {
                    UUID uuid = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase()
                            .get(player);
                    if (uuid == null) {
                        document.append("code", 404);
                        return HttpResponseHelper.badRequest(fullHttpResponse, document);
                    }
                    CloudPlayer cloudPlayer = CloudNet.getInstance().getNetworkManager()
                            .getOnlinePlayer(uuid);
                    CorePlayerExecutor.INSTANCE.sendPlayer(cloudPlayer, server);
                    return HttpResponseHelper.success(fullHttpResponse, document);
                }
            }
            return HttpResponseHelper.success(fullHttpResponse, document);
        }
        return HttpResponseHelper.messageFieldNotFound(fullHttpResponse);
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
                                    QueryDecoder queryDecoder,
                                    PathProvider pathProvider, HttpRequest httpRequest) {
        return HttpResponseHelper.cross(httpRequest);
    }
}