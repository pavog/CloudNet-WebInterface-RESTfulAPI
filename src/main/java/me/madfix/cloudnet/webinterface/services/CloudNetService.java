package me.madfix.cloudnet.webinterface.services;

import me.madfix.cloudnet.webinterface.WebInterface;

public class CloudNetService {

    private final MobService mobService;
    private final SignService signService;
    private final ProxyGroupService proxyGroupService;
    private final ServerGroupService serverGroupService;
    private final WrapperService wrapperService;

    public CloudNetService(WebInterface webInterface) {
        this.mobService = new MobService(webInterface);
        this.signService = new SignService(webInterface);
        this.proxyGroupService = new ProxyGroupService(webInterface);
        this.serverGroupService = new ServerGroupService(webInterface);
        this.wrapperService = new WrapperService(webInterface);
    }

    public MobService getMobService() {
        return mobService;
    }

    public SignService getSignService() {
        return signService;
    }

    public ProxyGroupService getProxyGroupService() {
        return proxyGroupService;
    }

    public ServerGroupService getServerGroupService() {
        return serverGroupService;
    }

    public WrapperService getWrapperService() {
        return wrapperService;
    }
}
