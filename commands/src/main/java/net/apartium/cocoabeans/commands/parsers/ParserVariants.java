package net.apartium.cocoabeans.commands.parsers;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParserVariants {

    ParserVariant[] value();

}
