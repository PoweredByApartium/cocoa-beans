package net.apartium.cocoabeans.scoreboard.spigot.utils;

import net.apartium.cocoabeans.scoreboard.*;
import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static net.apartium.cocoabeans.structs.MinecraftVersion.*;

public class NMSUtils {

    private static final Map<Class<?>, Field[]> PACKETS = new HashMap<>(16);

    private static final String OBC_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
    private static final String NM_PACKAGE = "net.minecraft";
    private static final String NMS_PACKAGE = OBC_PACKAGE.replace("org.bukkit.craftbukkit", NM_PACKAGE + ".server");

    private static final boolean NMS_REPACKAGED = optionalForName(NM_PACKAGE + ".network.protocol.Packet").isPresent();
    private static final boolean MOJANG_MAPPING = optionalForName(NM_PACKAGE + ".network.chat.Component").isPresent();

    private static final MinecraftVersion VERSION = ServerUtils.getVersion();

    private static final MethodType VOID_METHOD_TYPE = MethodType.methodType(void.class);

    private static final MethodHandle PLAYER_GET_HANDLE;
    private static final MethodHandle PLAYER_CONNECTION;
    private static final MethodHandle PLAYER_SEND_PACKET;

    private static final PacketConstructor PACKET_SB_UPDATE_OBJ;
    private static final PacketConstructor PACKET_SB_DISPLAY_OBJ;
    private static final PacketConstructor PACKET_SB_TEAM;
    private static final PacketConstructor PACKET_SB_SERIALIZABLE_TEAM;

    private static final MethodHandle PACKET_SB_UPDATE_SCORE;
    private static final MethodHandle PACKET_SB_RESET_SCORE;
    private static final MethodHandle FIXED_NUMBER_FORMAT;
    private static final MethodHandle STYLE_NUMBER_FORMAT;
    private static final Object BLANK_NUMBER_FORMAT;

    private static final Object EMPTY_STYLE;
    private static final MethodHandle STYLE_METHOD_WITH_COLOR_RGB;
    private static final MethodHandle STYLE_METHOD_WITH_BOLD;
    private static final MethodHandle STYLE_METHOD_WITH_ITALIC;
    private static final MethodHandle STYLE_METHOD_WITH_UNDERLINE;
    private static final MethodHandle STYLE_METHOD_WITH_STRIKETHROUGH;
    private static final MethodHandle STYLE_METHOD_WITH_OBFUSCATED;

    private static final Class<?> CHAT_FORMAT_ENUM;
    private static final Object RESET_FORMATTING;

    private static volatile Object unsafeObject;

    public static final Class<?> DISPLAY_SLOT_TYPE;
    public static final Map<DisplaySlot, Object> DISPLAY_SLOT_OBJECT_MAP;
    public static final Class<?> CHAT_COMPONENT_CLASS;
    public static final Class<?> RENDER_TYPE;
    public static final Map<ObjectiveRenderType, Object> RENDER_TYPE_OBJECT_MAP;

    private static final Class<?> ENUM_SB_ACTION;
    private static final Object ENUM_SB_ACTION_CHANGE;
    private static final Object ENUM_SB_ACTION_REMOVE;

    private static final MethodHandle COMPONENT_METHOD;
    private static final Object EMPTY_COMPONENT;

    private static final boolean ADVENTURE_SUPPORT;
    private static final boolean SCORE_OPTIONAL_COMPONENTS;

    public static final String MODE_ALWAYS = "always";
    public static final String MODE_NEVER = "never";


    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            Class<?> entityPlayerClass = findNMSClass("server.level", "EntityPlayer", "ServerPlayer").orElseThrow();
            Class<?> playerConnectionClass = findNMSClass("server.network", "PlayerConnection", "ServerGamePacketListenerImpl").orElseThrow();

            PLAYER_CONNECTION = lookup.unreflectGetter(Arrays.stream(entityPlayerClass.getFields())
                    .filter(field -> field.getType().isAssignableFrom(playerConnectionClass))
                    .findFirst()
                    .orElseThrow()
            );

            Class<?> craftPlayerClass = findOBCClass("entity.CraftPlayer").orElseThrow();

            PLAYER_GET_HANDLE = lookup.findVirtual(craftPlayerClass, "getHandle", MethodType.methodType(entityPlayerClass));

            Class<?> packetClass = findNMSClass("network.protocol", "Packet").orElseThrow();

            PLAYER_SEND_PACKET = lookup.unreflect(Stream.concat(
                                    Arrays.stream(playerConnectionClass.getSuperclass().getMethods()),
                                    Arrays.stream(playerConnectionClass.getMethods())
                            ).filter(method -> method.getParameterCount() == 1 && method.getParameterTypes()[0] == packetClass)
                            .findFirst()
                            .orElseThrow()
            );

            String gameProtocolPackage = "network.protocol.game";

            Class<?> packetSbObjClass = findNMSClass(gameProtocolPackage, "PacketPlayOutScoreboardObjective", "ClientboundSetObjectivePacket").orElseThrow();

            PACKET_SB_UPDATE_OBJ = findPacketConstructor(packetSbObjClass, lookup);

            DISPLAY_SLOT_TYPE = findNMSClass("world.scores", "DisplaySlot").orElse(int.class);

            Map<DisplaySlot, Object> displaySlotObjectMap = new IdentityHashMap<>();
            for (DisplaySlot slot : DisplaySlot.values())
                displaySlotObjectMap.put(slot, DISPLAY_SLOT_TYPE == int.class
                        ? slot.getId()
                        : findEnumValueOf(DISPLAY_SLOT_TYPE, slot.name(), slot.getId())
                );

            DISPLAY_SLOT_OBJECT_MAP = Collections.unmodifiableMap(displaySlotObjectMap);

            RENDER_TYPE = findNMSClass("world.scores.criteria", "IScoreboardCriteria$EnumScoreboardHealthDisplay", "ObjectiveCriteria$RenderType").orElseThrow();

            Map<ObjectiveRenderType, Object> renderTypeObjectMap = new IdentityHashMap<>();
            for (ObjectiveRenderType type : ObjectiveRenderType.values())
                renderTypeObjectMap.put(type, findEnumValueOf(RENDER_TYPE, type.name(), type.getId()));

            RENDER_TYPE_OBJECT_MAP = Collections.unmodifiableMap(renderTypeObjectMap);

            Class<?> packetSbDisplayObjClass = findNMSClass(gameProtocolPackage, "PacketPlayOutScoreboardDisplayObjective", "ClientboundSetDisplayObjectivePacket").orElseThrow();

            PACKET_SB_DISPLAY_OBJ = findPacketConstructor(packetSbDisplayObjClass, lookup);

            CHAT_COMPONENT_CLASS = findNMSClass("network.chat", "IChatBaseComponent","Component").orElseThrow();

            ADVENTURE_SUPPORT = optionalForName("io.papermc.paper.adventure.PaperAdventure").isPresent();

            if (ADVENTURE_SUPPORT) {
                Class<?> paperAdventureClass = Class.forName("io.papermc.paper.adventure.PaperAdventure");
                COMPONENT_METHOD = lookup.unreflect(paperAdventureClass.getDeclaredMethod("asVanilla", Component.class));
                EMPTY_COMPONENT = COMPONENT_METHOD.invoke(Component.empty());
            } else {
                Class<?> craftChatMessageClass = findOBCClass("util.CraftChatMessage").orElseThrow();
                COMPONENT_METHOD = lookup.unreflect(craftChatMessageClass.getMethod("fromString", String.class));
                EMPTY_COMPONENT = Array.get(COMPONENT_METHOD.invoke(""), 0);
            }
            Class<?> packetSbTeamClass = findNMSClass(gameProtocolPackage, "PacketPlayOutScoreboardTeam", "ClientboundSetPlayerTeamPacket").orElseThrow();
            Class<?> sbTeamClass = VERSION.isHigherThanOrEqual(V1_17)
                    ? innerClass(packetSbTeamClass, clazz -> !clazz.isEnum()).orElseThrow()
                    : null;

            PACKET_SB_TEAM = findPacketConstructor(packetSbTeamClass, lookup);
            PACKET_SB_SERIALIZABLE_TEAM = sbTeamClass == null
                    ? null
                    : findPacketConstructor(sbTeamClass, lookup);

            Class<?> packetSbScoreClass = findNMSClass(gameProtocolPackage, "PacketPlayOutScoreboardScore", "ClientboundSetScorePacket").orElseThrow();

            CHAT_FORMAT_ENUM = findNMSClass(null, "EnumChatFormat", "ChatFormatting").orElseThrow();
            RESET_FORMATTING = findEnumValueOf(CHAT_FORMAT_ENUM, "RESET", 21);

            MethodHandle packetSbSetScore;
            MethodHandle packetSbResetScore = null;
            MethodHandle fixedFormatConstructor = null;
            MethodHandle styledFormatConstructor = null;
            Object blankNumberFormat = null;
            boolean scoreOptionalComponents = false;

            Object styleEmpty = null;
            MethodHandle styleWithColorRGB = null;
            MethodHandle styleWithBold = null;
            MethodHandle styleWithItalic = null;
            MethodHandle styleWithUnderline = null;
            MethodHandle styleWithStrikethrough = null;
            MethodHandle styleWithObfuscated = null;


            if (VERSION.isHigherThanOrEqual(V1_20_3)) {
                Class<?> numberFormat = findNMSClass("network.chat.numbers", "NumberFormat").orElseThrow();

                MethodType scoreType = MethodType.methodType(void.class, String.class, String.class, int.class, CHAT_COMPONENT_CLASS, numberFormat);
                MethodType scoreTypeOptional = MethodType.methodType(void.class, String.class, String.class, int.class, Optional.class, Optional.class);

                Class<?> resetScoreClass = findNMSClass(gameProtocolPackage, "ClientboundResetScorePacket").orElseThrow();
                MethodType removeScoreType = MethodType.methodType(void.class, String.class, String.class);

                Optional<MethodHandle> optionalScorePacket = findConstructor(packetSbScoreClass, lookup, scoreTypeOptional);

                packetSbSetScore = optionalScorePacket.isPresent()
                        ? optionalScorePacket.get()
                        : lookup.findConstructor(packetSbScoreClass, scoreType);

                scoreOptionalComponents = optionalScorePacket.isPresent();

                packetSbResetScore = lookup.findConstructor(resetScoreClass, removeScoreType);

                Class<?> fixedFormatClass = findNMSClass("network.chat.numbers", "FixedFormat").orElseThrow();
                MethodType fixedFormatType = MethodType.methodType(void.class, CHAT_COMPONENT_CLASS);

                fixedFormatConstructor = lookup.findConstructor(fixedFormatClass, fixedFormatType);

                Class<?> styledFormat = findNMSClass("network.chat.numbers", "StyledFormat").orElseThrow();

                Class<?> styleClass = findNMSClass("network.chat", "Style")
                        .orElse(findNMSClass("network.chat", "ChatModifier").orElseThrow());

                styleEmpty = lookup.findStaticVarHandle(styleClass, "a", styleClass).get();
                styleWithColorRGB = lookup.findVirtual(styleClass, "a", MethodType.methodType(styleClass, int.class));
                styleWithBold = lookup.findVirtual(styleClass, "a", MethodType.methodType(styleClass, Boolean.class));
                styleWithItalic = lookup.findVirtual(styleClass, "b", MethodType.methodType(styleClass, Boolean.class));
                styleWithUnderline = lookup.findVirtual(styleClass, "c", MethodType.methodType(styleClass, Boolean.class));
                styleWithStrikethrough = lookup.findVirtual(styleClass, "d", MethodType.methodType(styleClass, Boolean.class));
                styleWithObfuscated = lookup.findVirtual(styleClass, "e", MethodType.methodType(styleClass, Boolean.class));

                MethodType styledFormatType = MethodType.methodType(void.class, styleClass);

                styledFormatConstructor = lookup.findConstructor(styledFormat, styledFormatType);

                Class<?> blankFormatClass = findNMSClass("network.chat.numbers", "BlankFormat").orElseThrow();

                blankNumberFormat = Arrays.stream(blankFormatClass.getFields()).filter(field -> field.getType() == blankFormatClass).findAny()
                        .map(field -> {
                            try {
                                return field.get(null);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .orElse(null);

            } else if (VERSION.isHigherThanOrEqual(V1_17)) {
                Class<?> enumSbAction = findNMSClass("server", "ScoreboardServer$Action", "ServerScoreboard$Method").orElseThrow();
                MethodType scoreType = MethodType.methodType(void.class, enumSbAction, String.class, String.class, int.class);
                packetSbSetScore = lookup.findConstructor(packetSbScoreClass, scoreType);
            } else {
                packetSbSetScore = lookup.findConstructor(packetSbScoreClass, MethodType.methodType(void.class));
            }

            PACKET_SB_UPDATE_SCORE = packetSbSetScore;
            PACKET_SB_RESET_SCORE = packetSbResetScore;

            SCORE_OPTIONAL_COMPONENTS = scoreOptionalComponents;

            FIXED_NUMBER_FORMAT = fixedFormatConstructor;
            STYLE_NUMBER_FORMAT = styledFormatConstructor;
            BLANK_NUMBER_FORMAT = blankNumberFormat;

            EMPTY_STYLE = styleEmpty;
            STYLE_METHOD_WITH_COLOR_RGB = styleWithColorRGB;
            STYLE_METHOD_WITH_BOLD = styleWithBold;
            STYLE_METHOD_WITH_ITALIC = styleWithItalic;
            STYLE_METHOD_WITH_UNDERLINE = styleWithUnderline;
            STYLE_METHOD_WITH_STRIKETHROUGH = styleWithStrikethrough;
            STYLE_METHOD_WITH_OBFUSCATED = styleWithObfuscated;

            ENUM_SB_ACTION = findNMSClass(
                    "server",
                    VERSION.isHigherThanOrEqual(V1_13)
                            ? "ScoreboardServer$Action"
                            : "PacketPlayOutScoreboardScore$EnumScoreboardAction",
                    "ServerScoreboard$Method").orElseThrow();

            ENUM_SB_ACTION_CHANGE = findEnumValueOf(ENUM_SB_ACTION, "CHANGE", 0);
            ENUM_SB_ACTION_REMOVE = findEnumValueOf(ENUM_SB_ACTION, "REMOVE", 1);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static String getNMSClassName(String newPackage, String className) {
        if (NMS_REPACKAGED)
            return (newPackage == null ? NM_PACKAGE : NM_PACKAGE + "." + newPackage)  + "." + className;

        return NMS_PACKAGE + "." + className;
    }

    public static Optional<Class<?>> findNMSClass(String newPackage, String className) {
        return optionalForName(getNMSClassName(newPackage, className));
    }

    public static Optional<Class<?>> findNMSClass(String newPackage, String spigotClass, String mojangClass) {
        return findNMSClass(newPackage, MOJANG_MAPPING ? mojangClass : spigotClass);
    }

    public static String getOBCClassName(String className) {
        return OBC_PACKAGE + "." + className;
    }

    public static Optional<Class<?>> findOBCClass(String className) {
        return optionalForName(getOBCClassName(className));
    }

    public static PacketConstructor findPacketConstructor(Class<?> packetClass, MethodHandles.Lookup lookup) throws Exception {
        try {
            MethodHandle constructor = lookup.findConstructor(packetClass, VOID_METHOD_TYPE);
            return constructor::invoke;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            // ignored
        }

        if (unsafeObject == null) {
            synchronized (NMSUtils.class) {
                if (unsafeObject == null) {
                    Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                    Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
                    unsafeField.setAccessible(true);
                    unsafeObject = unsafeField.get(null);
                }
            }
        }

        MethodType allocateMethodType = MethodType.methodType(Object.class, Class.class);
        MethodHandle allocateMethod = lookup.findVirtual(unsafeObject.getClass(), "allocateInstance", allocateMethodType);
        return () -> allocateMethod.invoke(unsafeObject, packetClass);
    }

    public static Object findEnumValueOf(Class<?> enumClass, String enumName, int fallbackOrdinal) {
        try {
            return Enum.valueOf(enumClass.asSubclass(Enum.class), enumName);
        } catch (IllegalArgumentException e) {
            Object[] values = enumClass.getEnumConstants();
            if (values.length > fallbackOrdinal)
                return values[fallbackOrdinal];

            throw e;
        }
    }


    public static Optional<Class<?>> optionalForName(String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static Optional<MethodHandle> findConstructor(Class<?> clazz, MethodHandles.Lookup lookup, MethodType type) {
        try {
            return Optional.of(lookup.findConstructor(clazz, type));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            return Optional.empty();
        }
    }

    public static Object intoNetworkStyle(Style style) throws Throwable {
        Object newStyle = EMPTY_STYLE;

        TextColor color = style.color();
        if (color != null)
            newStyle = STYLE_METHOD_WITH_COLOR_RGB.invoke(newStyle, color.value());

        Map<TextDecoration, TextDecoration.State> decorations = style.decorations();

        newStyle = STYLE_METHOD_WITH_BOLD.invoke(newStyle, fromState(decorations.get(TextDecoration.BOLD)));
        newStyle = STYLE_METHOD_WITH_ITALIC.invoke(newStyle, fromState(decorations.get(TextDecoration.ITALIC)));
        newStyle = STYLE_METHOD_WITH_UNDERLINE.invoke(newStyle, fromState(decorations.get(TextDecoration.UNDERLINED)));
        newStyle = STYLE_METHOD_WITH_STRIKETHROUGH.invoke(newStyle, fromState(decorations.get(TextDecoration.STRIKETHROUGH)));
        newStyle = STYLE_METHOD_WITH_OBFUSCATED.invoke(newStyle, fromState(decorations.get(TextDecoration.OBFUSCATED)));


        return newStyle;
    }

    public static Boolean fromState(TextDecoration.State state) {
        if (state == null)
            return null;

        return switch (state) {
            case TRUE -> true;
            case FALSE -> false;
            default -> null;
        };
    }

    public static Object createObjectivePacket(String id, ObjectiveMode mode, ObjectiveRenderType renderType, Observable<Component> displayName) throws Throwable {
        Object packet = PACKET_SB_UPDATE_OBJ.createInstance();

        setField(packet, String.class, id);
        setField(packet, int.class, mode.getId());

        if (mode == ObjectiveMode.REMOVE)
            return packet;

        setComponentField(packet, displayName, 1);
        setField(packet, Optional.class, Optional.empty()); // Number format for 1.20.5+ is optional, old versions use null so no need to set it

        if (VERSION.isHigherThanOrEqual(V1_8))
            setField(packet, RENDER_TYPE, RENDER_TYPE_OBJECT_MAP.get(renderType));

        return packet;
    }

    public static Object createDisplayPacket(String id, DisplaySlot slot) throws Throwable {
        Object packet = PACKET_SB_DISPLAY_OBJ.createInstance();

        setField(packet, String.class, id);
        setField(packet, DISPLAY_SLOT_TYPE, DISPLAY_SLOT_OBJECT_MAP.get(slot)
        );

        return packet;
    }

    public static Object createScorePacket(String entityId, String objectiveId, Observable<Component> displayName, int scoreValue, ScoreboardAction action) throws Throwable {
        return createScorePacket(entityId, objectiveId, displayName, scoreValue, action, null);
    }


    public static Object createScorePacket(String entityId, String objectiveId, Observable<Component> displayName, int scoreValue, ScoreboardAction action, Style numberStyle) throws Throwable {
        if (VERSION.isHigherThanOrEqual(V1_17))
            return createScorePacketV1_17(entityId, objectiveId, displayName, scoreValue, action, numberStyle);

        Object packet = PACKET_SB_UPDATE_SCORE.invoke();

        setField(packet, String.class, entityId, 0); // Player name

        if (VERSION.isHigherThanOrEqual(V1_8))
            setField(
                    packet
                    , ENUM_SB_ACTION
                    , action == ScoreboardAction.REMOVE
                            ? ENUM_SB_ACTION_REMOVE
                            : ENUM_SB_ACTION_CHANGE
            );
        else
            setField(packet, int.class, action.ordinal(), 1); // Action

        if (action != ScoreboardAction.CREATE_OR_UPDATE)
            return packet;

        setField(packet, String.class, objectiveId, 1); // Objective name
        setField(packet, int.class, scoreValue); // Score

        return packet;
    }

    private static Object createScorePacketV1_17(String entityId, String objectiveId, Observable<Component> displayName, int scoreValue, ScoreboardAction action, Style numberStyle) throws Throwable {
        Object enumAction = action == ScoreboardAction.REMOVE
                ? ENUM_SB_ACTION_REMOVE
                : ENUM_SB_ACTION_CHANGE;

        if (PACKET_SB_RESET_SCORE == null) // Before 1.20.3
            return PACKET_SB_UPDATE_SCORE.invoke(enumAction, objectiveId, entityId, scoreValue);

        if (action == ScoreboardAction.REMOVE)
            return PACKET_SB_RESET_SCORE.invoke(entityId, objectiveId);

        Object format = numberStyle == null
                ? BLANK_NUMBER_FORMAT
                : STYLE_NUMBER_FORMAT.invoke(intoNetworkStyle(numberStyle));

        return SCORE_OPTIONAL_COMPONENTS
                ? PACKET_SB_UPDATE_SCORE.invoke(entityId, objectiveId, scoreValue, Optional.of(toMinecraftComponent(displayName)), Optional.of(format))
                : PACKET_SB_UPDATE_SCORE.invoke(entityId, objectiveId, scoreValue, toMinecraftComponent(displayName), format);
    }

    public static boolean isCustomScoreSupported() {
        return BLANK_NUMBER_FORMAT != null;
    }

    public static Optional<Class<?>> innerClass(Class<?> parentClass, Predicate<Class<?>> classPredicate) {
        for (Class<?> innerClass : parentClass.getDeclaredClasses()) {
            if (classPredicate.test(innerClass))
                return Optional.of(innerClass);
        }

        return Optional.empty();
    }

    public static Object toMinecraftComponent(Observable<Component> text) throws Throwable {
        if (text == null || text == Observable.<Component>empty() || text.get() == Component.empty())
            return EMPTY_COMPONENT;

        if (!ADVENTURE_SUPPORT || VERSION.isLowerThan(V1_13))
            return Array.get(COMPONENT_METHOD.invoke(LegacyComponentSerializer.legacySection().serialize(text.get())), 0);


        return COMPONENT_METHOD.invoke(text.get());

    }

    /* package-private */ static void setComponentField(Object packet, Observable<Component> component, int index) throws Throwable {
        if (VERSION.isLowerThan(V1_13)) {
            setField(packet, String.class, LegacyComponentSerializer.legacySection().serialize(component.get()), index);
            return;
        }

        int i = 0;
        for (Field field : getFieldsForPacket(packet.getClass())) {
            if (field.getType() == String.class || field.getType() == CHAT_COMPONENT_CLASS) {
                if (i == index) {
                    field.set(packet, toMinecraftComponent(component));
                    return;
                }

                i += 1;
            }
        }
    }

    /* package-private */ static void setField(Object packet, Class<?> fieldType, Object value) throws IllegalAccessException {
        setField(packet, fieldType, value, 0);
    }

    /* package-private */ static void setField(Object packet, Class<?> fieldType, Object value, int index) throws IllegalAccessException {
        int i = 0;
        for (Field field : getFieldsForPacket(packet.getClass())) {
            if (field.getType() == fieldType) {
                if (i == index) {
                    field.set(packet, value);
                    return;
                }

                i += 1;
            }
        }
    }

    private static Field[] getFieldsForPacket(Class<?> clazz) {
        return PACKETS.computeIfAbsent(clazz, key -> {
            Field[] fields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .toArray(Field[]::new);

            for (Field field : fields)
                field.setAccessible(true);

            return fields;
        });
    }

    public static void sendPacket(Player target, Object packet) throws Throwable {
        if (!target.isOnline())
            return;

        Object playerConnection = PLAYER_CONNECTION.invoke(PLAYER_GET_HANDLE.invoke(target));
        PLAYER_SEND_PACKET.invoke(playerConnection, packet);
    }

    public static Object createTeamPacket(String name, TeamMode mode) throws Throwable {
        return createTeamPacket(name, mode, null, null);
    }

    public static Object createTeamPacket(String name, TeamMode mode, Observable<Component> prefix, Observable<Component> suffix) throws Throwable {
        return createTeamPacket(name, mode, prefix, suffix, Collections.emptyList());
    }

    public static Object createTeamPacket(String name, TeamMode mode, Observable<Component> prefix, Observable<Component> suffix, Collection<String> entities) throws Throwable {
        if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS)
            throw new UnsupportedOperationException("TeamMode.ADD_PLAYERS and TeamMode.REMOVE_PLAYERS are not supported yet"); // TODO support those in the future...

        Object packet = PACKET_SB_TEAM.createInstance();

        setField(packet, String.class, name); // Team name
        setField(packet, int.class, mode.ordinal(), VERSION.isHigherThanOrEqual(V1_8) && VERSION.isLowerThanOrEqual(V1_8_9) ? 1 : 0); // Update mode

        if (mode == TeamMode.REMOVE)
            return packet;

        if (VERSION.isHigherThanOrEqual(V1_17)) {
            Object team = PACKET_SB_SERIALIZABLE_TEAM.createInstance();

            setComponentField(team, Observable.empty(), 0); // Display name
            setField(team, CHAT_FORMAT_ENUM, RESET_FORMATTING); // Color
            setComponentField(team, prefix, 1); // Prefix
            setComponentField(team, suffix, 2); // Suffix
            setField(team, String.class, MODE_ALWAYS, 0); // Visibility // TODO add option to change it
            setField(team, String.class, MODE_ALWAYS, 1); // Collision // TODO add option to change it

            setField(packet, Optional.class, Optional.of(team));
        } else {
            setComponentField(packet, prefix, 2); // Prefix
            setComponentField(packet, suffix, 3); // Suffix
            setField(packet, String.class, MODE_ALWAYS, 4); // Visibility for 1.8+ // TODO add option to change it
            setField(packet, String.class, MODE_ALWAYS, 5); // Collision for 1.9+ // TODO add option to change it
        }

        if (mode == TeamMode.CREATE)
            setField(packet, Collection.class, entities); // Players in the team

        return packet;
    }

    @FunctionalInterface
    public interface PacketConstructor {
        Object createInstance() throws Throwable;
    }


}
