/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.permission;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import de.dytanic.cloudnet.lib.utility.Return;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ConfigPermissions {
    private final Path path;
    private Configuration cache;
    @SuppressWarnings("unchecked")
    public ConfigPermissions() throws Exception {
        this.path = Paths.get("local/perms.yml");
        if (!Files.exists(this.path)) {
            Files.createFile(this.path, (FileAttribute<?>[])new FileAttribute[0]);
            final Configuration configuration = new Configuration();
            configuration.set("enabled", true);
            configuration.set("groups", new Configuration());
            if (!Files.exists(Paths.get("local/permissions.yml"))) {
                final PermissionGroup member = new PermissionGroup("default", "","§eMember §7\u258e ", "§f", "§e", 9999, 0, true, new HashMap<>(), MapWrapper.valueableHashMap(new Return<>("Lobby", Collections.singletonList("test.permission.for.group.Lobby"))), new HashMap<>(), new ArrayList<>());
                this.write(member, configuration);
                final PermissionGroup admin = new PermissionGroup("Admin", "","§cAdmin §7\u258e ", "§f", "§c", 0, 100, false, (HashMap<String, Boolean>)MapWrapper.valueableHashMap(new Return[] { new Return<>("*", true) }), MapWrapper.valueableHashMap(new Return<>("Lobby", Collections.singletonList("test.permission.for.group.Lobby"))), new HashMap<>(), new ArrayList<>());
                this.write(admin, configuration);
            }
            else {
                final Document document = Document.loadDocument(Paths.get("local/permissions.yml"));
                final Collection<PermissionGroup> groups = document.getObject("groups", new TypeToken<Collection<PermissionGroup>>() {}.getType());
                final Map<String,PermissionGroup> maps = MapWrapper.collectionCatcherHashMap(groups, PermissionGroup::getName);
                configuration.set("enabled", document.getBoolean("enabled"));
                for (final PermissionGroup value : maps.values()) {
                    this.write(value, configuration);
                }
                Files.deleteIfExists(Paths.get("local/permissions.yml"));
            }
            try (final OutputStream outputStream = Files.newOutputStream(this.path);
                 final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
            }
        }
        this.loadCache();
    }
    public void updatePermissionGroup(final PermissionGroup permissionGroup) {
        if (this.cache == null) {
            this.loadCache();
        }
        this.write(permissionGroup, this.cache);
        try (final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(this.path), StandardCharsets.UTF_8)) {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.cache, outputStreamWriter);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, PermissionGroup> loadAll0() {
        this.loadCache();
        return this.read(this.cache);
    }

    public Map<String, PermissionGroup> loadAll() {
        if (this.cache == null) {
            this.loadCache();
        }
        return this.read(this.cache);
    }

    private void loadCache() {
        try (final InputStream inputStream = Files.newInputStream(this.path);
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            this.cache = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(final PermissionGroup permissionGroup, final Configuration configuration) {
        final Configuration section = configuration.getSection("groups");
        final Configuration group = new Configuration();
        group.set("prefix", permissionGroup.getPrefix());
        group.set("suffix", permissionGroup.getSuffix());
        group.set("display",permissionGroup.getDisplay());
        group.set("tagId", permissionGroup.getTagId());
        group.set("joinPower",permissionGroup.getJoinPower());
        group.set("defaultGroup",permissionGroup.isDefaultGroup());
        final Collection<String> perms = new CopyOnWriteArrayList<>();
        for (final Map.Entry<String, Boolean> entry : permissionGroup.getPermissions().entrySet()) {
            perms.add((entry.getValue() ? "" : "-") + entry.getKey());
        }
        group.set("permissions", perms);
        final Configuration permsCfg = new Configuration();
        for (final Map.Entry<String, List<String>> keys : permissionGroup.getServerGroupPermissions().entrySet()) {
            permsCfg.set(keys.getKey(), keys.getValue());
        }
        group.set("serverGroupPermissions", permsCfg);
        if (permissionGroup.getOptions().size() == 0) {
            permissionGroup.getOptions().put("test_option", true);
        }
        group.set("options", permissionGroup.getOptions());
        group.set("implements", permissionGroup.getImplementGroups());
        section.set(permissionGroup.getName(), null);
        section.set(permissionGroup.getName(), group);
    }

    private Map<String, PermissionGroup> read(final Configuration configuration) {
        final Map<String, PermissionGroup> maps = new LinkedHashMap<>();
        final Configuration section = configuration.getSection("groups");
        for (final String key : section.getKeys()) {
            final Configuration group = section.getSection(key);
            final HashMap<String, Boolean> permissions = new HashMap<>();
            final List<String> permissionSection = group.getStringList("permissions");
            for (final String entry : permissionSection) {
                permissions.put(entry.replaceFirst("-", ""), !entry.startsWith("-"));
            }
            final HashMap<String, List<String>> permissionsGroups = new HashMap<>();
            final Configuration permissionSectionGroups = group.getSection("serverGroupPermissions");
            for (final String entry2 : permissionSectionGroups.getKeys()) {
                permissionsGroups.put(entry2, permissionSectionGroups.getStringList(entry2));
            }
            final PermissionGroup permissionGroup = new PermissionGroup(key, group.getString("prefix"),group.getString("color"), group.getString("suffix"), group.getString("display"), group.getInt("tagId"), group.getInt("joinPower"), group.getBoolean("defaultGroup"), permissions, permissionsGroups, group.getSection("options").self, group.getStringList("implements"));
            maps.put(permissionGroup.getName(), permissionGroup);
        }
        return maps;
    }

    public boolean isEnabled() {
        this.loadCache();
        return this.cache.getBoolean("enabled");
    }

    public boolean isEnabled0() {
        if (this.cache == null) {
            this.loadCache();
        }
        return this.cache.getBoolean("enabled");
    }
}
