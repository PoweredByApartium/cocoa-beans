package net.apartium.cocoabeans.state.animation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

/* package-private */ class AnimationHelpers {

    private AnimationHelpers() {

    }

    @NotNull
    /* package-private */ static Component fading(String text, int index, Style fade, Style in, Style out) {
        if (index == 0)
            return Component.text(text.charAt(0))
                    .style(fade)
                    .append(Component.text(text.substring(1))
                            .style(in)
                    );

        if (index == text.length() - 1)
            return Component.text(text.substring(0, text.length() - 1))
                    .style(out)
                    .append(Component.text(text.substring(text.length() - 1))
                            .style(fade)
                    );

        return Component.text(text.substring(0, index))
                .style(out)
                .append(Component.text(text.charAt(index))
                        .style(fade)
                        .append(Component.text(text.substring(index + 1))
                                .style(in)
                        )
                );
    }

}
