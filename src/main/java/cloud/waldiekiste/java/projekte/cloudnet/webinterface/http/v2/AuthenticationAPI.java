package cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.RequestUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.ResponseUtil;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.utils.UserUtil;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.ArrayList;

public final class AuthenticationAPI extends MethodWebHandlerAdapter {

  public AuthenticationAPI() {
    super("/cloudnet/api/v2/auth");
    CloudNet.getInstance().getWebServer().getWebServerProvider().registerHandler(this);
  }

  @SuppressWarnings("deprecation")
  @Override
  public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
        httpRequest.getProtocolVersion(),
        HttpResponseStatus.OK);
    ResponseUtil.setHeader(fullHttpResponse, "Content-Type", "application/json");
    if (!RequestUtil.hasHeader(httpRequest, "-xcloudnet-user", "-xcloudnet-password")) {
      return ResponseUtil.xCloudFieldsNotFound(fullHttpResponse);
    }
    String username = RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-user");
    String userpassword = RequestUtil.getHeaderValue(httpRequest, "-xcloudnet-password");
    if (!CloudNet.getInstance().authorizationPassword(username, userpassword)) {
      return UserUtil.failedAuthorization(fullHttpResponse);
    }
    User user = CloudNet.getInstance().getUser(username);
    Document userinfos = new Document();
    userinfos.append("UUID", user.getUniqueId().toString());
    userinfos.append("token", user.getApiToken());
    userinfos.append("name", user.getName());
    userinfos.append("password", user.getHashedPassword());
    userinfos.append("permissions", new ArrayList<>(user.getPermissions()));
    Document document = new Document();
    document.append("response", userinfos);
    return ResponseUtil.success(fullHttpResponse, true, document);
  }

  @Override
  public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
      QueryDecoder queryDecoder,
      PathProvider pathProvider, HttpRequest httpRequest) {
    return ResponseUtil.cross(httpRequest);
  }
}
