package io.github.adainish.wynautrankup.util;

import io.github.adainish.wynautrankup.WynautRankUp;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtil
{
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]){6}");
    private static final Pattern LEGACY_PATTERN = Pattern.compile("[&§]([0-9a-fA-fk-oK-OrR])");

    public static FabricServerAudiences adventure = FabricServerAudiences.of(WynautRankUp.instance.server);
    public static Component coloredText(String text, String hexColor) {
        TextColor color = TextColor.fromHexString(hexColor);
        return Component.text(text).color(color);
    }

    public static Component appendColoredText(Component baseComponent, String text, String hexColor) {
        TextColor color = TextColor.fromHexString(hexColor);
        return baseComponent.append(Component.text(text).color(color));
    }

    public static Component boldColoredText(String text, String hexColor) {
        TextColor color = TextColor.fromHexString(hexColor);
        return Component.text(text).color(color).decorate(TextDecoration.BOLD);
    }

    public static Component italicColoredText(String text, String hexColor) {
        TextColor color = TextColor.fromHexString(hexColor);
        return Component.text(text).color(color).decorate(TextDecoration.ITALIC);
    }

    public static Component underlinedColoredText(String text, String hexColor) {
        TextColor color = TextColor.fromHexString(hexColor);
        return Component.text(text).color(color).decorate(TextDecoration.UNDERLINED);
    }

    public static Component decoratedColoredText(String text, String hexColor, TextDecoration... decorations) {
        TextColor color = TextColor.fromHexString(hexColor);
        Component component = Component.text(text).color(color);
        for (TextDecoration decoration : decorations) {
            component = component.decorate(decoration);
        }
        return component;
    }

    public static Component clickableText(String text, String hexColor, String url) {
        TextColor color = TextColor.fromHexString(hexColor);
        return Component.text(text)
                .color(color)
                .clickEvent(ClickEvent.openUrl(url));
    }

    public static Component appendClickableText(Component baseComponent, String text, String hexColor, String url) {
        TextColor color = TextColor.fromHexString(hexColor);
        return baseComponent.append(Component.text(text)
                .color(color)
                .clickEvent(ClickEvent.openUrl(url)));
    }

    public static Component gradientText(String text, String startHexColor, String endHexColor) {
        if (startHexColor == null || endHexColor == null) {
            throw new IllegalArgumentException("Both start and end colors must be provided");
        }

        if (!startHexColor.startsWith("#")) {
            startHexColor = "#" + startHexColor;
        }
        if (!endHexColor.startsWith("#")) {
            endHexColor = "#" + endHexColor;
        }

        TextColor startColor = TextColor.fromHexString(startHexColor);
        TextColor endColor = TextColor.fromHexString(endHexColor);

        if (startColor == null || endColor == null) {
            throw new IllegalArgumentException("Invalid hex color format");
        }

        int length = text.length();
        Component component = Component.empty();
        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int red = (int) (startColor.red() * (1 - ratio) + endColor.red() * ratio);
            int green = (int) (startColor.green() * (1 - ratio) + endColor.green() * ratio);
            int blue = (int) (startColor.blue() * (1 - ratio) + endColor.blue() * ratio);
            TextColor color = TextColor.color(red, green, blue);
            component = component.append(Component.text(String.valueOf(text.charAt(i))).color(color));
        }
        return component;
    }

    public static Component gradientBoldText(String text, String startHexColor, String endHexColor) {
        return gradientText(text, startHexColor, endHexColor).decorate(TextDecoration.BOLD);
    }

    public static Component gradientItalicText(String text, String startHexColor, String endHexColor) {
        return gradientText(text, startHexColor, endHexColor).decorate(TextDecoration.ITALIC);
    }

    public static Component gradientUnderlinedText(String text, String startHexColor, String endHexColor) {
        return gradientText(text, startHexColor, endHexColor).decorate(TextDecoration.UNDERLINED);
    }

    public static Component gradientDecoratedText(String text, String startHexColor, String endHexColor, TextDecoration... decorations) {
        Component component = gradientText(text, startHexColor, endHexColor);
        for (TextDecoration decoration : decorations) {
            component = component.decorate(decoration);
        }
        return component;
    }

    public static Component gradientClickableText(String text, String startHexColor, String endHexColor, String url) {
        return gradientText(text, startHexColor, endHexColor).clickEvent(ClickEvent.openUrl(url));
    }

    public static Component appendGradientText(Component baseComponent, String text, String startHexColor, String endHexColor) {
        return baseComponent.append(gradientText(text, startHexColor, endHexColor));
    }

    public static Component appendGradientBoldText(Component baseComponent, String text, String startHexColor, String endHexColor) {
        return baseComponent.append(gradientBoldText(text, startHexColor, endHexColor));
    }

    public static Component appendGradientItalicText(Component baseComponent, String text, String startHexColor, String endHexColor) {
        return baseComponent.append(gradientItalicText(text, startHexColor, endHexColor));
    }

    public static Component appendGradientUnderlinedText(Component baseComponent, String text, String startHexColor, String endHexColor) {
        return baseComponent.append(gradientUnderlinedText(text, startHexColor, endHexColor));
    }

    public static Component appendGradientDecoratedText(Component baseComponent, String text, String startHexColor, String endHexColor, TextDecoration... decorations) {
        return baseComponent.append(gradientDecoratedText(text, startHexColor, endHexColor, decorations));
    }

    public static Component appendGradientClickableText(Component baseComponent, String text, String startHexColor, String endHexColor, String url) {
        return baseComponent.append(gradientClickableText(text, startHexColor, endHexColor, url));
    }


    public static List<Component> gradientTextList(List<String> strings, String startHexColor, String endHexColor) {
        return strings.stream().map(s -> gradientText(s, startHexColor, endHexColor)).collect(Collectors.toList());
    }

    public static net.minecraft.network.chat.Component nativeGradientText(String text, String startHexColor, String endHexColor) {
        return adventure.toNative(gradientText(text, startHexColor, endHexColor));
    }

    public static List<net.minecraft.network.chat.Component> nativeGradientTextList(List<String> strings, String startHexColor, String endHexColor) {
        List<Component> components = gradientTextList(strings, startHexColor, endHexColor);
        return components.stream().map(component -> adventure.toNative(component)).collect(Collectors.toList());
    }

    public static net.minecraft.network.chat.Component parseNativeText(String text) {
        return adventure.toNative(parseText(text));
    }

    public static Component parseText(String text) {
        return Component.text(replaceCodes(text));
    }

    private static String replaceCodes(String input) {
        Matcher matcher = HEX_PATTERN.matcher(input);
        while (matcher.find()) {
            input = input.replace(matcher.group(), "<reset><c:" + matcher.group().substring(1) + ">");
            matcher = HEX_PATTERN.matcher(input);
        }
        return replaceLegacyCodes(input);
    }

    private static String replaceLegacyCodes(String input) {
        Matcher matcher = LEGACY_PATTERN.matcher(input);
        while (matcher.find()) {
            input = input.replace(matcher.group(), getLegacyReplacement(matcher.group().substring(1)));
            matcher = LEGACY_PATTERN.matcher(input);
        }
        return input;
    }

    private static String getLegacyReplacement(String input) {
        return switch (input.toUpperCase(Locale.ENGLISH)) {
            case "0" -> "<reset><c:#000000>";
            case "1" -> "<reset><c:#0000AA>";
            case "2" -> "<reset><c:#00AA00>";
            case "3" -> "<reset><c:#00AAAA>";
            case "4" -> "<reset><c:#AA0000>";
            case "5" -> "<reset><c:#AA00AA>";
            case "6" -> "<reset><c:#FFAA00>";
            case "7" -> "<reset><c:#AAAAAA>";
            case "8" -> "<reset><c:#555555>";
            case "9" -> "<reset><c:#5555FF>";
            case "A" -> "<reset><c:#55FF55>";
            case "B" -> "<reset><c:#55FFFF>";
            case "C" -> "<reset><c:#FF5555>";
            case "D" -> "<reset><c:#FF55FF>";
            case "E" -> "<reset><c:#FFFF55>";
            case "F" -> "<reset><c:#FFFFFF>";
            case "K" -> "<obf>";
            case "L" -> "<b>";
            case "M" -> "<st>";
            case "N" -> "<u>";
            case "O" -> "<i>";
            case "R" -> "<reset>";
            default -> input;
        };
    }


    public static List<Component> parseTextList(List<String> strings) {
        return strings.stream().map(TextUtil::parseText).collect(Collectors.toList());
    }


    public static List<net.minecraft.network.chat.Component> parseNativeTextList(List<String> strings) {
        List<Component> components = parseTextList(strings);
        return components.stream().map(adventure::toNative).collect(Collectors.toList());
    }
}
